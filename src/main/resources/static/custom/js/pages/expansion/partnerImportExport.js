
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
            var file = document.getElementById("importPartnerInput").files[0];
            if(file) {
                var formData = new FormData();
                formData.append('file', file);
                function onSuccess(response) {
                    if(response && response.status) {
                        $.alert("取引先のインポートに成功しました");
                    } else {
                        $.alert("取引先のインポートに失敗しました");
                    }
                }

                function onError(response) {
                    $.alert("取引先のインポートに失敗しました");
                }
                importPartners(formData, includeHeader, onSuccess, onError)
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
            getPartnerExport(includeHeader);
        }
    }
    
    function doGroupActions() {
        var actionType = $("#groupActionType").val();
        var includeHeader = $('#groupIncludeHeader').is(":checked");
        if(actionType == "import") {
            var file = document.getElementById("importGroupInput").files[0];
            if(file) {
                var formData = new FormData();
                formData.append('file', file);
                function onSuccess(response) {
                    if(response && response.status) {
                        $.alert("取引先グループのインポートに成功しました");
                    } else {
                        $.alert("取引先グループのインポートに失敗しました");
                    }
                }

                function onError(response) {
                    $.alert("取引先グループのインポートに失敗しました");
                }
                importPartnerGroups(formData, includeHeader, onSuccess, onError)
            } else {
                $.alert("インポートするファイルを選択");
            }
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
