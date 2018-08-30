
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
                    hideloading();
                    if(response && response.status) {
                        $.alert("取引先のインポートに成功しました");
                    } else {
                        $.alert("取引先のインポートに失敗しました");
                    }
                }

                function onError(response) {
                    hideloading();
                    $.alert("取引先のインポートに失敗しました");
                }
                showImportPartnerLoading();
                importPartners(formData, includeHeader, onSuccess, onError);
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
            getPartnerExport(includeHeader);
        }
    }
    
    function showImportPartnerLoading() {
        showLoading("取引先のインポート中");
    }

    function showImportGroupLoading() {
        showLoading("取引先グループのインポート中");
    }

    function showLoading(message) {
        hideloading();
        $('body').loadingModal({
            position: 'auto',
            text: message,
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
    }
    
    function hideloading() {
        $('body').loadingModal('hide');
        $('body').loadingModal('destroy');
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
                    hideloading();
                    if(response && response.status) {
                        $.alert("取引先グループのインポートに成功しました");
                    } else {
                        $.alert("取引先グループのインポートに失敗しました");
                    }
                }

                function onError(response) {
                    hideloading();
                    $.alert("取引先グループのインポートに失敗しました");
                }
                showImportGroupLoading();
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
