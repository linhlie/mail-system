
(function () {
    "use strict";

    $(function () {
        setAddReplaceLetterRowListener('addMotoRow', 'motoMail', ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addSakiRow', 'sakiMail', ["group", "combine", "item", "condition", "value"]);
        setAddReplaceLetterRowListener('addMatchRow', 'matching', ["group", "combine", "item", "condition", "value"]);
        setRemoveRowListener("removeConditionRow");
    });

    function setRemoveRowListener(name) {
        $("span[name='"+name+"']").off('click');
        $("span[name='"+name+"']").click(function () {
            $(this)[0].parentNode.parentNode.className+=' hidden';
        })
    }

    function setAddReplaceLetterRowListener(name, tableId, data) {
        $("span[name='"+name+"']").click(function () {
            var table = document.getElementById(tableId);
            var body = table.tBodies[0];
            var rowToClone = table.rows[1];
            var clone = rowToClone.cloneNode(true);
            clone.className = undefined;
            body.appendChild(clone);
            setRemoveRowListener("removeConditionRow");
        })
    }

})(jQuery);
