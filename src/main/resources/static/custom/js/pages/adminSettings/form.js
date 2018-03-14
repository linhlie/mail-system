
(function () {

    "use strict";

    $(function () {
        setGoBackListener('backBtn');
    });

    function setGoBackListener(name){
        $("button[name='"+name+"']").click(function () {
            goBack();
        })
    }

    function goBack() {
        window.history.back();
    }

})(jQuery);
