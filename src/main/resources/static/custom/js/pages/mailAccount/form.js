
(function () {

    "use strict";
    var IMAP = 0;
    var POP3 = 1;
    var formChange = false;

    $(function () {
        setFormsChangeListener();
        setMailProtocolChangeListener();
        setCCChangeListener("senderCC");
        setGoBackListener('backBtn');
        setResetListener('resetFormBtn');
        validateSenderCC();
    });

    function setCCChangeListener() {
        $('#senderCC').on('input', function() {
            validateSenderCC();
        });
    }

    function validateSenderCC() {
        var rawCC = $('#senderCC').val();
        rawCC = rawCC || "";
        var ccText = rawCC.replace(/\s*,\s*/g, ",");
        var cc = ccText.split(",");
        var senderValid = true;
        if(cc.length === 1 && cc[0] == "") {
            senderValid = true;
        } else {
            for(var i = 0; i < cc.length; i++) {
                var email = cc[i];
                var valid = validateEmail(email);
                if(!valid) {
                    senderValid = false;
                    break;
                }
            }
        }
        senderValid ? $('#cc-container').removeClass('has-error') : $('#cc-container').addClass('has-error');
        disableSubmitBtn(!senderValid);
    }
    
    function disableSubmitBtn(disabled) {
        $('#submitBtn').prop("disabled", disabled);
    }

    function validateEmail(email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    function setFormsChangeListener() {
        $('#fullAccountForm').change(function() {
            formChange = true;
        });
        tinymce.init({
            force_br_newlines : true,
            force_p_newlines : false,
            forced_root_block : '',
            selector: '#signatureSetting',
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 250,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor colorpicker'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            fontsize_formats: '6pt 8pt 10pt 11pt 12pt 13pt 14pt 16pt 18pt 20pt 24pt 28pt 32pt 36pt 40pt 45pt 50pt',
            init_instance_callback: function (editor) {
                editor.on('Change', function (e) {
                    formChange = true;
                });
            }
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
    
    function setResetListener(name) {
        $("button[name='"+name+"']").click(function () {
            var isUpdate = $(this).attr("data");
            if(isUpdate === "false") {
                if(formChange){
                    var isClear = confirm("本当にリセットフォームが必要ですか。");
                    if(isClear){
                        clearForm();
                    }
                }
            } else {
                clearForm();
            }
        })
    }
    
    function clearForm() {
        $('#fullAccountForm').trigger("reset");
        formChange = false;
        var editor = tinymce.get("signatureSetting");
        editor.undoManager.clear();
        editor.undoManager.add();
    }

    function setGoBackListener(name){
        $("button[name='"+name+"']").click(function () {
            var isUpdate = $(this).attr("data");
            if(isUpdate === "true" && formChange) {
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
