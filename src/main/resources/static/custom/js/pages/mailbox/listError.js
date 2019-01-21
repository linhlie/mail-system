
(function () {

    "use strict";
    var selectAllCheckBoxId = "#selectall";
    var deleteMailsButtonId = "#deleteMails";


    $(function () {
        retryListener('retry');
        setButtonClickListenter(deleteMailsButtonId, doDeleteMails);
        setupSelectBoxes();
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

    function retryListener(name, type) {
        $("button[name='"+name+"']").click(function () {
            $('body').loadingModal({
                position: 'auto',
                text: '再試行中...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });
            var messageId = $(this).attr("data");
            messageId = messageId.replace(/\+/g, '%2B');
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: "/admin/retry?messageId=" + messageId,
                cache: false,
                timeout: 600000,
                success: function (data) {
                    $('body').loadingModal('hide');
                    window.location.reload();
                },
                error: function (e) {
                    console.error("[ERROR] submit error: ", e);
                    $('body').loadingModal('hide');
                    window.location.reload();
                }
            });
        })
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
                            deleteFromErrorBox(msgIds, onSuccess, onError);
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
