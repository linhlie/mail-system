
(function () {

    $(function () {
        setButtonClickListenter("#importGroupBtn", onImportGroupClick);
        setButtonClickListenter("#importPartnerBtn", onImportPartnerClick);
        setImportFileChangeListener()
    });
    
    function onImportPartnerClick() {
        $("#importPartnerInput").click();
    }
    
    function onImportGroupClick() {
        $("#importGroupInput").click();
    }
    
    function setImportFileChangeListener() {
        $("#importPartnerInput").change(function (){
            var fileName = $(this).val();
            fileName = fileName.replace(/C:\\fakepath\\/i, '');
            $("#importPartner").val(fileName);
        });
        $("#importGroupInput").change(function (){
            var fileName = $(this).val();
            fileName = fileName.replace(/C:\\fakepath\\/i, '');
            $("#importGroup").val(fileName);
        });
    }


})(jQuery);
