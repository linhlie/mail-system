
(function () {

    $(function () {
        $("#engineerActionType").change(function () {
           setDisplayType(this.value);
        });
        setButtonClickListenter("#importEngineerBtn", onImportEngineerClick);
        setButtonClickListenter("#engineerActions", doEngineerActions);
        setImportFileChangeListener()
    });

    function onImportEngineerClick() {
        $("#importEngineerInput").click();
    }

    function doEngineerActions() {
        var actionType = $("#engineerActionType").val();
        var includeHeader = $('#engineerIncludeHeader').is(":checked");
        var deleteOld = $('#engineerDeleteOld').is(":checked");
        if(actionType == "import") {
            var file = document.getElementById("importEngineerInput").files[0];
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
                showImportEngineerLoading();
                importEngineers(formData, includeHeader, deleteOld, onSuccess, onError)
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
            clearImportLogs();
            getEngineerExport(includeHeader);
        }
    }

    function setImportFileChangeListener() {
        $("#importEngineerInput").change(function (){
            var fileName = $(this).val();
            fileName = fileName.replace(/C:\\fakepath\\/i, '');
            $("#importEngineer").val(fileName);
        });
    }

    function showImportEngineerLoading() {
        showLoading("技術者のインポート中");
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

    function setDisplayType(type) {
        if(type=="export"){
            $("#engineerIncludeHeaderText").html("&nbsp;先頭行はタイトルとする");
            $("#importEngineerInputDiv").css("display","none");
            $("#engineerDeleteOldDiv").css("visibility","hidden");
        }else{
            $("#engineerIncludeHeaderText").html("&nbsp;先頭行はタイトルとみなす");
            $("#importEngineerInputDiv").css("display","block");
            $("#engineerDeleteOldDiv").css("visibility","visible");
        }
    }


})(jQuery);
