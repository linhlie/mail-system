
(function () {

    "use strict";
    var formChange = false;

    $(function () {
        setFormChangeListener();
        setGoBackListener('backBtn');
    });

    function setFormChangeListener() {
        $('#enviromentSettingsForm').change(function() {
            formChange = true;
        });
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
