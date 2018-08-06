
(function () {
    "use strict";

    $(function(){
        // add multiple select / deselect functionality
        $("#selectall").click(function () {
            $('.case').prop('checked', this.checked);
        });

        // if all checkbox are selected, check the selectall checkbox
        // and viceversa
        $(".case").click(function(){

            if($(".case").length == $(".case:checked").length) {
                $("#selectall").prop("checked", true);
            } else {
                $("#selectall").prop("checked", false);
            }

        });
    });



})(jQuery);
