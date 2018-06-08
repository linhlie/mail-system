
(function () {

    var formId = '#numberTreatmentForm';
    var submitButtonId = '#btn-submit-number-treatment';

    "use strict";

    $(formId).submit(function(e){
        e.preventDefault();

        var form = {};
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
        for (i = 1; i < table.rows.length; i++) {
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

})(jQuery);
