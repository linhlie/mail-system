
(function () {
    "use strict";
    var formId = '#numberTreatmentForm';
    var submitButtonId = '#btn-submit-number-treatment';
    var deleteButtonId = '#deleteBtn';

    var formFields = [
        {type: "input", name: "name"},
        {type: "input", name: "upperLimitName"},
        {type: "select", name: "upperLimitSign"},
        {type: "input", name: "upperLimitRate"},
        {type: "input", name: "lowerLimitName"},
        {type: "select", name: "lowerLimitSign"},
        {type: "input", name: "lowerLimitRate"},
        {type: "input", name: "leftBoundaryValue"},
        {type: "select", name: "leftBoundaryOperator"},
        {type: "select", name: "combineOperator"},
        {type: "input", name: "rightBoundaryValue"},
        {type: "select", name: "rightBoundaryOperator"},
        {type: "checkbox", name: "enableReplaceLetter"},
        {type: "checkbox", name: "enablePrettyNumber"},
        {type: "input", name: "prettyNumberStep"},
    ];

    $(formId).submit(function(e){
        e.preventDefault();
        clearFormValidate();
        var validated = numberTreatmentFormValidate();
        if(!validated) return;

        var form = {};
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.type == "checkbox"){
                form[field.name] = $("input" + "[name='" + field.name + "']").is(':checked');
            } else {
                form[field.name] = $("" + field.type + "[name='" + field.name + "']").val();
            }
        }

        form["replaceNumberList"] = [];
        form["replaceUnitList"] = [];
        form["replaceLetterList"] = [];

        var rawReplaceNumberData = buildReplaceNumberOrUnitDataTable('replaceNumber');
        for (var i = 0; i < rawReplaceNumberData.length; i++) {
            var rowData = rawReplaceNumberData[i];
            if(typeof rowData["character"] === 'string' && rowData["character"].length > 0
                && typeof rowData["replaceValueStr"] === 'string' && rowData["replaceValueStr"].length > 0) {
                form["replaceNumberList"].push(rowData);
            }
        }

        var rawReplaceUnitData = buildReplaceNumberOrUnitDataTable('replaceUnit');
        for (var i = 0; i < rawReplaceUnitData.length; i++) {
            var rowData = rawReplaceUnitData[i];
            if(typeof rowData["unit"] === 'string' && rowData["unit"].length > 0
                && typeof rowData["replaceUnit"] === 'string' && rowData["replaceUnit"].length > 0) {
                form["replaceUnitList"].push(rowData);
            }
        }

        var rawReplaceLetterData = buildReplaceLetterDataTable('replaceLetter');
        for (var i = 0; i < rawReplaceLetterData.length; i++) {
            var rowData = rawReplaceLetterData[i];
            if(typeof rowData["letter"] === 'string' && rowData["letter"].length > 0
                && typeof rowData["position"] === 'string' && rowData["position"].length > 0
                && typeof rowData["replace"] === 'string' && rowData["replace"].length > 0) {
                form["replaceLetterList"].push(rowData);
            }
        }

        hideAllError();
        $(submitButtonId).prop("disabled", true);

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/user/numberTreatment",
            data: JSON.stringify(form),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                $(submitButtonId).prop("disabled", false);
                if(data && data.status){
                    window.location.reload();
                } else {
                    handleError(data);
                }

            },
            error: function (e) {
                //TODO: show error
                alert("Something went wrong. Try it later!");
                console.log("ERROR : ", e);
                $(submitButtonId).prop("disabled", false);

            }
        });
    });
    
    function getNormalizedValue(value) {
        return typeof value === 'string' ? value.trim() : value;
        // return typeof value === 'string' ? value.replace(/\s/g,'') : value;
    }
    
    function buildReplaceNumberOrUnitDataTable(tableId) {
        var data = [];
        var table = document.getElementById(tableId);
        for (var i = 1; i < table.rows.length; i++) {
            var objRow = table.rows.item(i);
            var objCells = objRow.cells;
            var rowData = {};
            for (var j = 0; j < objCells.length; j++) {
                var cellKey = objCells.item(j).getAttribute("data");
                if(!cellKey) continue;
                var cellNode = objCells.item(j).childNodes[0];
                if(cellNode.nodeType == Node.TEXT_NODE){
                    rowData[cellKey] = getNormalizedValue(cellNode.nodeValue);
                } else if(cellNode.nodeName == "INPUT"){
                    rowData[cellKey] = getNormalizedValue(cellNode.value);
                }
            }
            rowData["remove"] = objRow.className.indexOf('hidden') >= 0 ? 1 : 0;
            data.push(rowData);
        }
        return data;
    }

    function buildReplaceLetterDataTable(tableId) {
        var data = [];
        var table = document.getElementById(tableId);
        for (var i = 1; i < table.rows.length; i++) {
            var objRow = table.rows.item(i);
            var objCells = objRow.cells;
            var rowData = {};
            for (var j = 0; j < objCells.length; j++) {
                var cellKey = objCells.item(j).getAttribute("data");
                if(!cellKey) continue;
                var cellNode = objCells.item(j).childNodes.length >= 2 ?
                    objCells.item(j).childNodes[1] : objCells.item(j).childNodes[0];
                if(cellNode.nodeType == Node.TEXT_NODE){
                    rowData[cellKey] = getNormalizedValue(cellNode.nodeValue);
                } else if(cellNode.nodeName == "INPUT"){
                    rowData[cellKey] = getNormalizedValue(cellNode.value);
                }  else if(cellNode.nodeName == "SELECT"){
                    rowData[cellKey] = getNormalizedValue(cellNode.value);
                }
            }
            rowData["remove"] = objRow.className.indexOf('hidden') >= 0 ? 1 : 0;
            data.push(rowData);
        }
        return data;
    }

    $(function () {
        setAddRowListener('addReplaceNumber', 'replaceNumber', ["character", "replaceValueStr"]);
        setAddRowListener('addReplaceUnit', 'replaceUnit', ["unit", "replaceUnit"]);
        setRemoveRowListener('removeReplaceNumber');
        setRemoveRowListener('removeReplaceUnit');
        setAddReplaceLetterRowListener('addReplaceLetter', 'replaceLetter', ["position", "letter", "replace"]);
        setRemoveRowListener('removeReplaceLetter');
        setGoBackListener('backBtn');
        followSettingName();
        followUpperLimitName();
        followLowerLimitName();
        setButtonClickListenter(deleteButtonId, callDeleteNumbertreatment);
    });

    function setAddRowListener(name, tableId, data) {
        $("span[name='"+name+"']").click(function () {
            var table = document.getElementById(tableId);
            var row = table.insertRow(-1);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            cell1.innerHTML = '<input type="text" placeholder="文字"/>';
            cell2.innerHTML = '<input type="text" placeholder="置き換え"/>';
            cell1.setAttribute('data', data[0]);
            cell2.setAttribute('data', data[1]);
        })
    }
    
    function setRemoveRowListener(name) {
        $("span[name='"+name+"']").click(function () {
            $(this)[0].parentNode.parentNode.className+=' hidden';
        })
    }

    function setAddReplaceLetterRowListener(name, tableId, data) {
        $("span[name='"+name+"']").click(function () {
            var table = document.getElementById(tableId);
            var row = table.insertRow(-1);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var cell4 = row.insertCell(3);
            cell1.className = 'select-container';
            cell3.className = 'select-container';
            cell1.innerHTML = '<select class="form-control select2 select2-hidden-accessible" aria-hidden="true">' +
                '<option value="" selected="selected" disabled="disabled">選択してください</option>' +
                '<option value="0" >数値の前の</option>' +
                '<option value="1" >数値の後の</option>' +
                '</select>'
            cell2.innerHTML = '<input class="text-center" type="text" placeholder="文字"/>';
            cell3.innerHTML = '<select class="form-control select2 select2-hidden-accessible" aria-hidden="true">' +
                '<option value="" selected="selected" disabled="disabled">選択してください</option>' +
                '<option value="0" >「以上」として認識する</option>' +
                '<option value="1" >「以下」として認識する</option>' +
                '<option value="2" >「未満」として認識する</option>' +
                '<option value="3" >「超」として認識する</option>' +
                '</select>'
            cell1.setAttribute('data', data[0]);
            cell2.setAttribute('data', data[1]);
            cell3.setAttribute('data', data[2]);
        })
    }

    function setGoBackListener(name){
        $("button[name='"+name+"']").click(function () {
            //TODO: show confirm incase change
            goBack();
        })
    }

    function goBack() {
        window.history.back();
    }
    
    function handleError(data) {
        if(data) {
            var errorCode = data.errorCode;
            var errorMessage = data.msg;
            switch (errorCode){
                case 600:
                    errorMessage = "データには 「.」「,」「，」を含めることはできません。";
                    showReplaceNumberError(errorMessage);
                    break;
                case 601:
                    errorMessage = "データには 「.」「,」「，」を含めることはできません。";
                    showReplaceUnitError(errorMessage);
                    break;
                case 602:
                    errorMessage = "データが正しくフォーマットされていません。";
                    showReplaceNumberError(errorMessage);
                    break;
                default:
                    showGeneralError(errorMessage);
                    
            }
        }
    }
    
    function showGeneralError(message) {
        $( "#errorMessage" ).html( message );
        $("#errorContainer").show();
    }
    
    function showReplaceNumberError(message) {
        $("#replaceNumberErrorMsg").text(message);
        $("#replaceNumber").parent().addClass( 'error' );
        $("#replaceNumberError").show();
    }

    function showReplaceUnitError(message) {
        $("#replaceUnitErrorMsg").text(message);
        $("#replaceUnit").parent().addClass( 'error' );
        $("#replaceUnitError").show();
    }
    
    function hideAllError() {
        $("#errorContainer").hide();
        $("#replaceNumberError").hide();
        $("#replaceNumber").parent().removeClass( 'error' );
        $("#replaceUnit").parent().removeClass( 'error' );
        $("#replaceUnitError").hide();
    }

    function followSettingName() {
        var name = $("input[name='name']").val();
        if(!name || name == null){
            disableForm(true);
        }else{
            disableForm(false)
        }

        $("input[name='name']").change(function(){
            var value = $(this).val();
            if(!value || value == null){
                disableForm(true);
            }else{
                disableForm(false)

                var upperLimitName = $("input[name='upperLimitName']").val();
                if(!upperLimitName || upperLimitName == null){
                    disableFormUpperLimit(true);
                }else{
                    disableFormUpperLimit(false)
                }

                var lowerLimitName = $("input[name='lowerLimitName']").val();
                if(!lowerLimitName || lowerLimitName == null){
                    disableFormLowerLimit(true);
                }else{
                    disableFormLowerLimit(false)
                }
            }
        })
    }

    function disableForm(disable) {
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            $("" + field.type + "[name='" + field.name + "']").prop('disabled', disable);
        }

        if(disable){
            $("div .treatment-container").css("background-color", "#eee");
            $("table").css("background-color", "#eee");
            $("span").css("pointer-events", "none");

        }else{
            $("div .treatment-container").css("background-color", "#fff");
            $("table").css("background-color", "white");
            $("span").css("pointer-events", "auto");
        }

        $("input[name='enablePrettyNumber']").prop('disabled', disable);
        $("input[name='enableReplaceLetter']").prop('disabled', disable);
        $("table").find("input,button,span,select,td").prop('disabled', disable);
        $("input[name='name']").prop('disabled', false);
    }

    function followUpperLimitName() {
        var upperLimitName = $("input[name='upperLimitName']").val();
        if(!upperLimitName || upperLimitName == null){
            disableFormUpperLimit(true);
        }else{
            disableFormUpperLimit(false)
        }

        $("input[name='upperLimitName']").change(function(){
            var value = $(this).val();
            if(!value || value == null){
                disableFormUpperLimit(true);
            }else{
                disableFormUpperLimit(false)
            }
        })
    }

    function disableFormUpperLimit(disable) {
            $("select[name='upperLimitSign']").prop('disabled', disable);
            $("input[name='upperLimitRate']").prop('disabled', disable);
    }

    function followLowerLimitName() {
        var lowerLimitName = $("input[name='lowerLimitName']").val();
        if(!lowerLimitName || lowerLimitName == null){
            disableFormLowerLimit(true);
        }else{
            disableFormLowerLimit(false)
        }

        $("input[name='lowerLimitName']").change(function(){
            var value = $(this).val();
            if(!value || value == null){
                disableFormLowerLimit(true);
            }else{
                disableFormLowerLimit(false)
            }
        })
    }

    function disableFormLowerLimit(disable) {
        $("select[name='lowerLimitSign']").prop('disabled', disable);
        $("input[name='lowerLimitRate']").prop('disabled', disable);
    }

    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
        $("span.form-error").text("");
    }

    function numberTreatmentFormValidate() {
        var validate1 = settingNameValidate();
        return validate1;
    }

    function settingNameValidate() {
        var input = $("input[name='name']");
        if(!input.val()) {
            showError.apply(input, ["必須"]);
            return false;
        }
        return true;
    }

    function showError(error, selector) {
        selector = selector || "div.number-treatment-form-group";
        var container = $(this).closest(selector);
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }
    
    function callDeleteNumbertreatment() {
        function onSuccess() {
            location.reload();
        }
        function onError() {
            $.alert("数値扱い設定クリアが失敗しました。");
        }
        $.confirm({
            title: '<b>【数値扱い設定クリア】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">数値扱い設定を本当にクリアしたいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deletenumberTreatment(onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

})(jQuery);
