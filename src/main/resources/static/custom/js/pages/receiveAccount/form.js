
(function () {

    "use strict";
    var IMAP = 0;
    var POP3 = 1;
    var formChange = false;

    $(function () {
        setFormChangeListener();
        setReceiveMailProtocolChangeListener();
        setGoBackListener('backBtn');
    });

    function setFormChangeListener() {
        $('#receiveMailForm').change(function() {
            formChange = true;
        });
    }

    function setReceiveMailProtocolChangeListener() {
        $('#mailProtocol').change(function(){
            var protocol = $(this).find("option:selected").attr('value');
            var port = 993;
            if(protocol == IMAP) {
                port = 993;
            } else if ( protocol == POP3) {
                port = 995;
            }
            $('#mailPort').val(port);
        });
    }

    function setGoBackListener(name){
        $("button[name='"+name+"']").click(function () {
            var isUpdate = $(this).attr("data");
            if(isUpdate === "true" && formChange) {
                var isBack = confirm(isUpdate + "離れたいですか。");
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
