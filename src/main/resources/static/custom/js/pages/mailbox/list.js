
(function () {
    "use strict";
    var selectAllCheckBoxId = "#selectall";
    var deleteMailsButtonId = "#deleteMails";

    $(function(){
        setupSelectBoxes();
        previewDraggingSetup();
        setButtonClickListenter(deleteMailsButtonId, doDeleteMails);
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

})(jQuery);
