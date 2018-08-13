
(function () {
    var partnerComboBoxId = "partnerComboBox";
    var partnerAddBtnId = "#partnerAdd";
    var partnerUpdateBtnId = "#partnerUpdate";
    var partnerClearBtnId = "#partnerClear";
    var formId = "#partnerForm";

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
        setButtonClickListenter(partnerAddBtnId, addPartnerOnClick);
        setButtonClickListenter(partnerUpdateBtnId, updatePartnerOnClick);
        setButtonClickListenter(partnerClearBtnId, clearPartnerOnClick);
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
    
    function addPartnerOnClick() {
        var validated = partnerFormValidate();
        if(!validated) return;
    }
    
    function updatePartnerOnClick() {
        var validated = partnerFormValidate();
        if(!validated) return;
    }
    
    function partnerFormValidate() {
        var validate1 = partnerNameValidate();
        var validate2 = partnerKanaNameValidate();
        var validate3 = partnerIdentifierValidate();
        var validate4 = partnerDomainValidate();
        return validate1 && validate2 && validate3 && validate4;
    }
    
    function partnerNameValidate() {
        var input = $("input[name='partnerName']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }
    
    function showError(error) {
        var container = $(this).closest("div.form-group.row");
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }
    
    function partnerKanaNameValidate() {
        var input = $("input[name='partnerKanaName']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }
    
    function partnerIdentifierValidate() {
        var input = $("input[name='identificationId']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }
    
    function partnerDomainValidate() {
        var domain1 = $("#domain1");
        var domain2 = $("#domain2");
        var domain3 = $("#domain3");
        if(!domain1.val() && !domain2.val() && !domain3.val()) {
            showError.apply(domain1, ["必要"]);
            return false;
        }
        return true;
    }
    
    function clearPartnerOnClick() {
        resetForm()
        clearFormValidate();
    }

    function resetForm() {
        $(formId).trigger("reset");
    }
    
    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
    }

})(jQuery);
