
(function () {

    $(function () {
        $("#peopleInChargeActionType").change(function () {
           setDisplayType(this.value);
        });
        setButtonClickListenter("#importPeopleInChargeBtn", importPeopleInChargeOnclick);
        setButtonClickListenter("#peopleInChargeActions", doPeopleinChargeActions);
        setImportFileChangeListener()
    });

    function importPeopleInChargeOnclick() {
        $("#importPeopleInChargeInput").click();
    }

    function doPeopleinChargeActions() {
        var actionType = $("#peopleInChargeActionType").val();
        var includeHeader = $('#peopleInChargeIncludeHeader').is(":checked");
        var deleteOld = $('#peopleInChargeDeleteOld').is(":checked");
        if(actionType == "import") {
            var file = document.getElementById("importPeopleInChargeInput").files[0];
            if(file) {
                var formData = new FormData();
                formData.append('file', file);
                function onSuccess(response) {
                    hideloading();
                    if(response && response.status) {
                        updateImportLogs(response.list);
                        $.alert("担当者のインポートに成功しました");
                    } else {
                        $.alert("担当者のインポートに失敗しました");
                    }
                }

                function onError(response) {
                    hideloading();
                    $.alert("担当者のインポートに失敗しました");
                }
                clearImportLogs();
                showImportPeopleInChargeLoading();
                importPeopleInChargePartners(formData, includeHeader, deleteOld, onSuccess, onError)
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
            clearImportLogs();
            getPeopleInChargePartnerExport(includeHeader);
        }
    }

    function setImportFileChangeListener() {
        $("#importPeopleInChargeInput").change(function (){
            var fileName = $(this).val();
            fileName = fileName.replace(/C:\\fakepath\\/i, '');
            $("#importPeopleInCharge").val(fileName);
        });
    }

    function showImportPeopleInChargeLoading() {
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
            $("#peopleInChargeIncludeHeaderText").html("&nbsp;先頭行はタイトルとする");
            $("#importpeopleInChargeInputDiv").css("display","none");
            $("#peopleInChargeDeleteOldDiv").css("visibility","hidden");
        }else{
            $("#peopleInChargeIncludeHeaderText").html("&nbsp;先頭行はタイトルとみなす");
            $("#importpeopleInChargeInputDiv").css("display","block");
            $("#peopleInChargeDeleteOldDiv").css("visibility","visible");
        }
    }


})(jQuery);
