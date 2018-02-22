
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
        ];
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            form[field.name] = $("" + field.type + "[name='" + field.name + "']").val();
        }

        form["replaceNumberList"] = [];
        form["replaceUnitList"] = [];

        var rawReplaceNumberData = buildReplaceNumberOrUnitDataTable('replaceNumber');
        for (var i = 0; i < rawReplaceNumberData.length; i++) {
            var rowData = rawReplaceNumberData[i];
            if(typeof rowData["character"] === 'string' && rowData["character"].length > 0
                && typeof rowData["replaceValueStr"] === 'string' && rowData["replaceValueStr"].length > 0) {
                form["replaceNumberList"].push(rowData);
            }
        }

        var rawReplaceUnitData = buildReplaceNumberOrUnitDataTable('replaceUnit');
        for (var j = 0; j < rawReplaceUnitData.length; j++) {
            var rowData = rawReplaceUnitData[j];
            if(typeof rowData["unit"] === 'string' && rowData["unit"].length > 0
                && typeof rowData["replaceUnit"] === 'string' && rowData["replaceUnit"].length > 0) {
                form["replaceUnitList"].push(rowData);
            }
        }

        $(submitButtonId).prop("disabled", true);

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/numberTreatment",
            data: JSON.stringify(form),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                $(submitButtonId).prop("disabled", false);
                if(data && data.status){
                    console.log("SUCCESS : ", data);
                } else {
                    console.log("FAILED : ", data);
                }

            },
            error: function (e) {
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

    $(function () {
        setAddRowListener('addReplaceNumber', 'replaceNumber', ["character", "replaceValueStr"]);
        setAddRowListener('addReplaceUnit', 'replaceUnit', ["unit", "replaceUnit"]);
        setRemoveRowListener('removeReplaceNumber');
        setRemoveRowListener('removeReplaceUnit');
        setAddReplaceLetterRowListener('addReplaceLetter', 'replaceLetter');
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

    function setAddReplaceLetterRowListener(name, tableId) {
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
                '<option value="1" >数値の前の</option>' +
                '<option value="2" >数値の後の</option>' +
                '</select>'
            cell2.innerHTML = '<input class="text-center" type="text" placeholder="文字"/>';
            cell3.innerHTML = '<select class="form-control select2 select2-hidden-accessible" aria-hidden="true">' +
                '<option value="" selected="selected" disabled="disabled">選択してください</option>'
                '<option value="1" >以上として認識する</option>' +
                '<option value="2" >以下として認識する</option>' +
                '<option value="3" >未満として認識する</option>' +
                '<option value="4" >超として認識する</option>' +
                '</select>'
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

})(jQuery);
