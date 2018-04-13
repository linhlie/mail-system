
(function () {

    "use strict";

    $(function () {
        deleteAccountListener('trash');
    });

    function deleteAccountListener(name) {
        $("span[name='"+name+"']").click(function () {
            var deleteId = $(this).attr("data");
            $('#confirm-delete').modal();
            $("#deleteMailConfirm").off('click');
            $("#deleteMailConfirm").click(function () {
                var keepMail = $("#keepMailCheckbox").is(':checked');
                $('#confirm-delete').modal('hide');
                deleteAccount(deleteId, keepMail);
                console.log("confirm delete account: ", deleteId, keepMail);
            })
        })
    }
    
    function deleteAccount(deleteId, keepMail) {
        $('body').loadingModal({
            position: 'auto',
            text: 'プロセッシング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/admin/deleteAccount?id=" + deleteId + "&deleteMail=" + !keepMail,
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if(data && data.status){
                    window.location.reload();
                } else {
                    console.error("[ERROR] deleteAccount failed: ");
                }
            },
            error: function (e) {
                console.error("[ERROR] deleteAccount error: ", e);
                $('body').loadingModal('hide');
            }
        });
    }



})(jQuery);
