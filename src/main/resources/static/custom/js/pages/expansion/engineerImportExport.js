
(function () {

    $(function () {
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
        if(actionType == "import") {
            var file = document.getElementById("importEngineerInput").files[0];
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
                importEngineers(formData, includeHeader, onSuccess, onError)
            } else {
                $.alert("インポートするファイルを選択");
            }
        } else if(actionType == "export") {
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


})(jQuery);
