
(function () {
    "use strict";
    var selectAllCheckBoxId = "#selectall";
    var emptyTrashBoxId = "#emptyTrashBox";
    var trashBoxTableId = "#trashBoxTable";
    var actionTrashBoxId = "#actionTrashBox";

    $(function(){
        setupSelectBoxes();
        setButtonClickListenter(emptyTrashBoxId, doEmptyTrashBox);
        setButtonClickListenter(actionTrashBoxId, doActionTrashBox);
        initEmptyTrashBoxButton()
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
    
    function doEmptyTrashBox () {
        $(emptyTrashBoxId).click(function () {
            function onSuccess() {
                locationReload();
            }
            function onError() {
                $.alert("Empty trash box failed");
            }
            $.confirm({
                title: '<b>【TrashBoxを空にする】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            emptyTrashBox(onSuccess, onError)
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        })
    }

    function initEmptyTrashBoxButton() {
        var isEmpty = $(trashBoxTableId + ' >tbody >tr').length == 0;
        $(emptyTrashBoxId).prop('disabled', isEmpty);
    }
    
    function doActionTrashBox() {
        var msgIds = [];
        $(".case:checked").each(function () {
            var msgId = $(this).attr("value");
            if(msgId) msgIds.push(msgId);
        });
        function onSuccess() {
            locationReload();
        }

        function onError(e) {
            console.log("error: ", e);
            $.alert("delete from trash box failed");
        }
        var actionType = $("#actionTypeSelect").val();
        if(msgIds.length > 0) {
            if(actionType === "inbox") {
                moveToInbox(msgIds, onSuccess, onError);
            } else if (actionType === "delete") {
                deleteFromTrashBox(msgIds, onSuccess, onError);
            }
        }
    }

})(jQuery);
