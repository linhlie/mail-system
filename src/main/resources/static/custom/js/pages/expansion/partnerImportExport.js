
(function () {

    $(function () {
        setButtonClickListenter("#importGroupBtn", onImportGroupClick);
        setButtonClickListenter("#importPartnerBtn", onImportPartnerClick);
        setButtonClickListenter("#partnerActions", doPartnerActions);
        setButtonClickListenter("#groupActions", doGroupActions);
        setImportFileChangeListener()
    });
    
    function onImportPartnerClick() {
        $("#importPartnerInput").click();
    }
    
    function onImportGroupClick() {
        $("#importGroupInput").click();
    }

    function doPartnerActions() {
        var actionType = $("#partnerActionType").val();
        var includeHeader = $('#partnerIncludeHeader').is(":checked");
        if(actionType == "import") {
            //TODO: import
        } else if(actionType == "export") {
            getPartnerExport(includeHeader);
        }
    }
    
    function doGroupActions() {
        var actionType = $("#groupActionType").val();
        var includeHeader = $('#groupIncludeHeader').is(":checked");
        if(actionType == "import") {
            //TODO: import
        } else if(actionType == "export") {
            getPartnerGroupExport(includeHeader);
        }
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
