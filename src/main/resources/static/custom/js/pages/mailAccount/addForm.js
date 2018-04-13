
(function () {

    "use strict";
    var formChange = false;

    $(function () {
        setFormChangeListener();
        setGoBackListener('backBtn');
        setResetListener('resetBtn');
    });

    function setFormChangeListener() {
        $('#mailAccountForm').change(function() {
            formChange = true;
        });
    }
    
    function setResetListener(name) {
        $("button[name='"+name+"']").click(function () {
            var isUpdate = $(this).attr("data");
            if(isUpdate === "false") {
                if(formChange){
                    var isClear = confirm("本当にリセットフォームが必要ですか。");
                    if(isClear){
                        $('#mailAccountForm').trigger("reset");
                        formChange = false;
                    }
                }
            } else {
                $('#mailAccountForm').trigger("reset");
                formChange = false;
            }
        })
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
