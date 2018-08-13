
(function () {
    var partnerComboBoxId = "partnerComboBox";

    $(function () {
        initStickyHeader();
        updatePartnerComboBox([
            {
                name: "DEF",
                code: "def",
            },
            {
                name: "ABC",
                code: "abc",
            },
            {
                name: "BCD",
                code: "bcd",
            },
        ]);
        partnerComboBoxListener();
    });

    function initStickyHeader() {
        $(".table-container-wrapper").scroll(function () {
            $(this).find("thead.sticky-header")
                .css({
                    "user-select": "none",
                    "position": "relative",
                    "z-index": "10",
                    "transform": "translate(0px, " + $(this).scrollTop() + "px)"
                });
        });
    }
    
    function updatePartnerComboBox(options) {
        options.sort(comparePartner);
        console.log("updatePartnerComboBox: ", options);
        $('#' + partnerComboBoxId).empty();
        $('#' + partnerComboBoxId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "選んでください",
        }));
        $.each(options, function (i, item) {
            $('#' + partnerComboBoxId).append($('<option>', {
                value: item.code,
                text : item.name,
            }));
        });
    }
    
    function partnerComboBoxListener() {
        $('#' + partnerComboBoxId).off('change');
        $('#' + partnerComboBoxId).change(function() {
            console.log(this.value, );
            var name = $(this).find("option:selected").text();
            var code = this.value;
            addPartnerToGroup.apply(this, [name, code]);
            $('#' + partnerComboBoxId).prop('selectedIndex',0);
        });
    }
    
    function addPartnerToGroup(name, code) {
        var tr = '<tr role="row">' +
            '<td rowspan="1" colspan="1" data="title"><span>' + name + '</span></td>' +
            '<td rowspan="1" colspan="1" data="type"><span>' + code + '</span></td>' +
            '<td class="fit action" rowspan="1" colspan="1" data="id">' +
            '<button name="removeGreeting" type="button">削除</button>' +
            '</td> </tr>';
        $(this).closest('table').find('tr:last').prev().after(tr);
    }

})(jQuery);
