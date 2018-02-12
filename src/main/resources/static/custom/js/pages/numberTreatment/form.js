
(function () {

    "use strict";

    console.log("Load numberTreatment form js");
    $(function () {
        setAddRowListener('addReplaceNumber', 'replaceNumber');
        setAddRowListener('addReplaceUnit', 'replaceUnit');
        setRemoveRowListener('removeReplaceNumber', 'replaceNumber');
        setRemoveRowListener('removeReplaceUnit', 'replaceUnit');
        setAddReplaceLetterRowListener('addReplaceLetter', 'replaceLetter');
        setRemoveRowListener('removeReplaceLetter', 'replaceLetter');
        setGoBackListener('backBtn');
    });

    function setAddRowListener(name, tableId) {
        $("span[name='"+name+"']").click(function () {
            var table = document.getElementById(tableId);
            var row = table.insertRow(-1);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            cell1.innerHTML = '<input type="text" placeholder="文字"/>';
            cell2.innerHTML = '<input type="text" placeholder="置き換え"/>';
        })
    }
    
    function setRemoveRowListener(name, tableId) {
        $("span[name='"+name+"']").click(function () {
            var table = document.getElementById(tableId);
            var index = $(this)[0].parentNode.parentNode.rowIndex;
            table.deleteRow(index);
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
                '<option value="0" >数値の後の</option>' +
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
