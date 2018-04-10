
(function () {

    "use strict";
    var IMAP = 0;
    var POP3 = 1;
    var formChange = false;

    $(function () {
        setFormsChangeListener();
        setMailProtocolChangeListener();
        setGoBackListener('backBtn');
        setResetListener('resetFormBtn', function () {
            $('#fullAccountForm').trigger("reset");
            formChange = false;
        });
    });

    function setFormsChangeListener() {
        $('#fullAccountForm').change(function() {
            formChange = true;
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
            if(formChange) {
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
