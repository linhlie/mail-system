
(function () {

    "use strict";

    console.log("Load numberTreatment form js");
    $(function () {
        setAddRowListener('addReplaceNumber', 'replaceNumber');
        setAddRowListener('addReplaceUnit', 'replaceUnit');
        setRemoveRowListener('removeReplaceNumber', 'replaceNumber');
        setRemoveRowListener('removeReplaceUnit', 'replaceUnit');
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
