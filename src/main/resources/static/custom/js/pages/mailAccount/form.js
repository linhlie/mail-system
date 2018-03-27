
(function () {

    "use strict";
    var IMAP = 0;
    var POP3 = 1;
    var accountFormChange = false;
    var receiveFormChange = false;
    var sendFormChange = false;

    $(function () {
        setFormsChangeListener();
        setMailProtocolChangeListener();
        setGoBackListener('backBtn');
        setResetListener('resetMailFormBtn', function () {
            $('#mailAccountForm').trigger("reset");
            accountFormChange = false;
        });
        setResetListener('resetReceiveFormBtn', function () {
            $('#receiveMailForm').trigger("reset");
            receiveFormChange = false;
        });
        setResetListener('resetSendFormBtn', function () {
            $('#sendMailForm').trigger("reset");
            sendFormChange = false;
        });
    });

    function setFormsChangeListener() {
        $('#mailAccountForm').change(function() {
            accountFormChange = true;
        });
        $('#receiveMailForm').change(function() {
            receiveFormChange = true;
        });
        $('#sendMailForm').change(function() {
            sendFormChange = true;
        });
    }

    function setMailProtocolChangeListener() {
        $('#receiveMailProtocol').change(function(){
            var protocol = $(this).find("option:selected").attr('value');
            var port = 993;
            if(protocol == IMAP) {
                port = 993;
            } else if ( protocol == POP3) {
                port = 995;
            }
            $('#receiveMailPort').val(port);
        });
    }
    
    function setResetListener(name, callback) {
        $("button[name='"+name+"']").click(function () {
            if(typeof callback === "function"){
                callback();
            }
        })
    }

    function setGoBackListener(name){
        $("button[name='"+name+"']").click(function () {
            if(accountFormChange || receiveFormChange || sendFormChange) {
                var isBack = confirm("離れたいですか。");
                if(isBack){
                    goBack();
                }
            } else {
                goBack();
            }
        })
    }

    function goBack() {
        window.history.back();
    }

})(jQuery);
