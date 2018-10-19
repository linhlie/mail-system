
(function () {

    $(function () {
        $("#partnerActionType").change(function () {
            setDisplayTypePartner(this.value);
        });
        $("#groupActionType").change(function () {
            setDisplayTypePartnerGroup(this.value);
        });
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
        var deleteOld = $('#partnerDeleteOld').is(":checked");
        if(actionType == "import") {
            var file = document.getElementById("importPartnerInput").files[0];
            if(file) {
                var formData = new FormData();
                formData.append('file', file);
                function onSuccess(response) {
                    hideloading();
                    if(response && response.status) {
                        updateImportLogs(response.list);
                        $.alert("取引先のインポートに成功しました");
                    } else {
                        $.alert("取引先のインポートに失敗しました");
                    }
                }

                function onError(response) {
                    hideloading();
                    $.alert("取引先のインポートに失敗しました");
                }
                clearImportLogs();
                showImportPartnerLoading();
                importPartners(formData, includeHeader, deleteOld, onSuccess, onError);
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
            clearImportLogs();
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
        var deleteOld = $('#groupDeleteOld').is(":checked");
        if(actionType == "import") {
            var file = document.getElementById("importGroupInput").files[0];
            if(file) {
                var formData = new FormData();
                formData.append('file', file);
                function onSuccess(response) {
                    hideloading();
                    if(response && response.status) {
                        updateImportLogs(response.list);
                        $.alert("取引先グループのインポートに成功しました");
                    } else {
                        $.alert("取引先グループのインポートに失敗しました");
                    }
                }

                function onError(response) {
                    hideloading();
                    $.alert("取引先グループのインポートに失敗しました");
                }
                clearImportLogs();
                showImportGroupLoading();
                importPartnerGroups(formData, includeHeader, deleteOld, onSuccess, onError)
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
            clearImportLogs();
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
    
    function updateImportLogs(logs) {
        logs = logs || [];
        var innerHTML = "";
        for(var i = 0; i < logs.length; i++) {
            innerHTML = innerHTML + buildLogLine(logs[i]);
        }
        $("#importLogs").html(innerHTML);
    }
    
    function buildLogLine(log) {
        var line = "<p><b>" + log.type + " " + log.line +"行目:</b>" + log.info + " ・・・・ <span>" + log.detail + "</span></p>"
        return line;
    }
    
    function clearImportLogs() {
        $("#importLogs").html("");
    }

    function setDisplayTypePartner(type) {
        if(type=="export"){
            $("#partnerIncludeHeaderText").html("&nbsp;先頭行はタイトルとする");
            $("#importPartnerInputDiv").css("visibility","hidden");
            $("#partnerDeleteOldDiv").css("visibility","hidden");
        }else{
            $("#partnerIncludeHeaderText").html("&nbsp;先頭行はタイトルとみなす");
            $("#importPartnerInputDiv").css("visibility","visible");
            $("#partnerDeleteOldDiv").css("visibility","visible");
        }
    }

    function setDisplayTypePartnerGroup(type) {
        if(type=="export"){
            $("#groupIncludeHeaderText").html("&nbsp;先頭行はタイトルとする");
            $("#importGroupInputDiv").css("visibility","hidden");
            $("#groupDeleteOldDiv").css("visibility","hidden");
        }else{
            $("#groupIncludeHeaderText").html("&nbsp;先頭行はタイトルとみなす");
            $("#importGroupInputDiv").css("visibility","visible");
            $("#groupDeleteOldDiv").css("visibility","visible");
        }
    }

})(jQuery);
