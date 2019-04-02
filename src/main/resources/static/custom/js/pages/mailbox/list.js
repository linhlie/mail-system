
(function () {
    "use strict";
    var selectAllCheckBoxId = "#selectall";
    var deleteMailsButtonId = "#deleteMails";
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var markSearchOptions = {
        "element": "mark",
        "className": "mark-search",
        "separateWordSearch": false,
    };

    $(function(){
        setupSelectBoxes();
        previewDraggingSetup();
        setButtonClickListenter(deleteMailsButtonId, doDeleteMails);
        setRowClickListener("sourceRow", function () {
            selectRow($(this).closest('tr'))
        });
        initSearch();
    });

    function setupSelectBoxes() {
        // add multiple select / deselect functionality
        $(selectAllCheckBoxId).click(function () {
            $('.case').prop('checked', this.checked);
        });

        // if all checkbox are selected, check the selectall checkbox
        // and viceversa
        $(".case").click(function(){

            if($(".case").length == $(".case:checked").length) {
                $(selectAllCheckBoxId).prop("checked", true);
            } else {
                $(selectAllCheckBoxId).prop("checked", false);
            }
        });
    }

    function previewDraggingSetup() {
        var dragging = false;
        $('#dragbar2').mousedown(function(e){
            e.preventDefault();

            dragging = true;
            var dragbar = $('#dragbar2');
            var ghostbar = $('<div>',
                {id:'ghostbar2',
                    css: {
                        width: dragbar.outerWidth(),
                        top: dragbar.offset().top,
                        left: dragbar.offset().left
                    }
                }).appendTo('body');

            $(document).mousemove(function(e){
                ghostbar.css("top",e.pageY);
            });

        });

        $(document).mouseup(function(e){
            if (dragging)
            {
                var container = $('#mailBox');
                var topHeight = (e.pageY - container.offset().top);
                console.log("topHeight: ", topHeight);
                var tableHeight = Math.floor(topHeight - 64);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 800 ? tableHeight : 800;
                var previewHeightChange = 450 - tableHeight;
                var previewHeight = 444 + previewHeightChange;
                $('#mailBox').css("height", tableHeight + "px");
                $('.matching-result .mail-body').css("height", previewHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

    function doDeleteMails() {
        var msgIds = [];
        $(".case:checked").each(function () {
            var msgId = $(this).attr("value");
            if(msgId) msgIds.push(msgId);
        });
        function onSuccess() {
            locationReload();
        }

        function onError(e) {
            $.alert("delete mails failed");
        }
        if(msgIds.length > 0) {
            $.confirm({
                title: '<b>【リストから「削除」】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            deleteFromInBox(msgIds, onSuccess, onError);
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        }
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function selectRow(row) {
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var messageId = row.find('td[name="messageId"]').attr("value");
        showPreviewMail(messageId)
        console.log("selectRow: ", messageId);
    }
    
    function showPreviewMail(messageId) {
        showMail(messageId, function (result) {
            showMailContent(result);
        });
    }

    function showMail(messageId, callback) {
        messageId = messageId.replace(/\+/g, '%2B');
        var url = "/admin/mailbox/email?messageId=" + messageId;
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                var result;
                if (data.status) {
                    if (data.list && data.list.length > 0) {
                        result = data.list[0];
                    }
                }
                if (typeof callback === "function") {
                    callback(result);
                }
            },
            error: function (e) {
                console.error("showMail ERROR : ", e);
                if (typeof callback === "function") {
                    callback();
                }
            }
        });
    }

    function showMailContent(data) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({originalBody: ""});
        mailAttachmentDiv.innerHTML = "";
        if (data) {
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
                '</div>';
            showMailBodyContent(data);
            var files = data.files ? data.files : [];
            showAttachFile(mailAttachmentDiv, files);
        }
    }

    function showMailBodyContent(data) {
        data.originalBody = wrapText(data.originalBody);
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.scrollTop = 0;
        mailBodyDiv.innerHTML = data.originalBody;
    }

    function initSearch() {
        // the input field
        var $input = $("input[type='search']"),
            // clear button
            $clearBtn = $("button[data-search='clear']"),
            // prev button
            $prevBtn = $("button[data-search='prev']"),
            // next button
            $nextBtn = $("button[data-search='next']"),
            // the context where to search
            $content = $("#" + mailBodyDivId),
            // jQuery object to save <mark> elements
            $results,
            // the class that will be appended to the current
            // focused element
            currentClass = "current",
            // the current index of the focused element
            currentIndex = 0;

        $input.keyup(function(event) {
            if (event.keyCode === 10 || event.keyCode === 13)
                event.preventDefault();
        });

        function jumpTo() {
            if ($results.length) {
                var position,
                    $current = $results.eq(currentIndex);
                $results.removeClass(currentClass);
                if ($current.length) {
                    $current.addClass(currentClass);
                    $content.scrollTop($content.scrollTop() + $current.position().top
                        - $content.height()/2 + $current.height()/2);
                }
            }
        }

        $input.on("input", function() {
            var searchVal = this.value;
            $content.unmark(
                Object.assign(
                    {},
                    markSearchOptions,
                    {
                        done: function() {
                            $content.mark(searchVal, Object.assign({},
                                markSearchOptions,
                                {
                                    done: function() {
                                        $results = $content.find("mark.mark-search");
                                        currentIndex = 0;
                                        jumpTo();
                                    }
                                }
                            ));
                        }
                    }
                )
            );
        });

        /**
         * Clears the search
         */
        $clearBtn.on("click", function() {
            $content.unmark(markSearchOptions);
            $input.val("").focus();
        });

        /**
         * Next and previous search jump to
         */
        $nextBtn.add($prevBtn).on("click", function() {
            if ($results.length) {
                currentIndex += $(this).is($prevBtn) ? -1 : 1;
                if (currentIndex < 0) {
                    currentIndex = $results.length - 1;
                }
                if (currentIndex > $results.length - 1) {
                    currentIndex = 0;
                }
                jumpTo();
            }
        });
    }

})(jQuery);
