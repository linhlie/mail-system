
(function () {
    var partnerComboBoxId = "partnerComboBox";
    var partnerAddBtnId = "#partnerAdd";
    var partnerUpdateBtnId = "#partnerUpdate";
    var partnerClearBtnId = "#partnerClear";
    var formId = "#partnerForm";
    var partnerTableId = "partner";
    var partners = null;
    var updatingPartnerId = null;

    var formFields = [
        {type: "input", name: "name"},
        {type: "input", name: "kanaName"},
        {type: "input", name: "partnerCode"},
        {type: "input", name: "domain1"},
        {type: "input", name: "domain2"},
        {type: "input", name: "domain3"},
        {type: "checkbox", name: "ourCompany"},
        {type: "radio", name: "companyType"},
        {type: "input", name: "companySpecificType"},
        {type: "radio", name: "stockShare"},
    ];

    var CompanyTypes = {
        LTD: 1,
        LIMITED: 2,
        GROUP: 3,
        JOINT_STOCK: 4,
        FOUNDATION: 5,
        CORPORATION: 6,
        OTHER: 7,
    };

    var partnerReplaceRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="name"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="partnerCode"><span></span></td>' +
        '<td name="editPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">編集</button>' +
        '</td>' +
        '<td name="deletePartner" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

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
        companyTypeChangeListener();
        loadBusinessPartners();
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
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("保存に成功しました");
                loadBusinessPartners();
                clearPartnerOnClick();
            } else {
                $.alert("保存に失敗しました");
            }
        }
        
        function onError(response) {
            $.alert("保存に失敗しました");
        }
        addPartner(data, onSuccess, onError)
    }

    function getFormData() {
        var form = {};
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.type == "checkbox"){
                form[field.name] = $("input" + "[name='" + field.name + "']").is(':checked');
            } else if (field.type == "radio") {
                form[field.name] = $('input[name=' + field.name + ']:checked', formId).val()
            } else {
                form[field.name] = $("" + field.type + "[name='" + field.name + "']").val();
            }
        }
        return form;
    }
    
    function setFormData(form) {
        for (var i = 0; i < formFields.length; i++) {
            var field = formFields[i];
            if(field.type == "checkbox"){
                $("input" + "[name='" + field.name + "']").prop('checked', form[field.name]);
            } else if (field.type == "radio") {
                $("input[name=" + field.name + "][value=" + form[field.name] + "]").prop('checked', true);
            } else {
                $("" + field.type + "[name='" + field.name + "']").val(form[field.name]);
            }
        }
    }
    
    function updatePartnerOnClick() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("保存に成功しました");
                loadBusinessPartners();
                clearPartnerOnClick();
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }
        updatePartner(updatingPartnerId, data, onSuccess, onError)
    }
    
    function partnerFormValidate() {
        var validate1 = partnerNameValidate();
        var validate2 = partnerKanaNameValidate();
        var validate3 = partnerPartnerCodeValidate();
        var validate4 = partnerDomainValidate();
        return validate1 && validate2 && validate3 && validate4;
    }
    
    function partnerNameValidate() {
        var input = $("input[name='name']");
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
        var input = $("input[name='kanaName']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }
    
    function partnerPartnerCodeValidate() {
        var input = $("input[name='partnerCode']");
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
            showError.apply(domain1, ["せめて一つのドメインを入力してください"]);
            return false;
        }
        return true;
    }
    
    function clearPartnerOnClick() {
        resetForm();
        clearFormValidate();
        disableUpdatePartner(true);
        updateCompanySpecificType();
        clearUpdatingParterId();
    }
    
    function clearUpdatingParterId() {
        updatingPartnerId = null;
    }

    function resetForm() {
        $(formId).trigger("reset");
    }
    
    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
    }
    
    function loadBusinessPartners() {
        function onSuccess(response) {
            if(response && response.status){
                loadBusinessPartnersData(partnerTableId, response.list);
            }
        }
        
        function onError(error) {
            
        }
        getBusinessPartners(onSuccess, onError);
    }
    
    function loadBusinessPartnersData(tableId, data) {
        partners = data;
        removeAllRow(tableId, partnerReplaceRow);
        if (partners.length > 0) {
            var html = partnerReplaceRow;
            for (var i = 0; i < partners.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deletePartner", function () {

                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = partners[index];
                if (rowData && rowData.id) {
                    doDeletePartner(rowData.id);
                }
            });
            setRowClickListener("editPartner", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = partners[index];
                if (rowData && rowData.id) {
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                    doEditPartner(rowData);
                }
            });
        }
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells.item(i);
            var cellKeysData = cell.getAttribute("data");
            if (!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if (cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (Array.isArray(cellData)) {
                        cellNode.textContent = cellData.length;
                    } else {
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        return row.outerHTML;
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function doDeletePartner(id) {
        function onSuccess() {
            console.log("doDeletePartner success");
            loadBusinessPartners();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        deletePartner(id, onSuccess, onError);
    }
    
    function doEditPartner(data) {
        updatingPartnerId = data.id;
        disableUpdatePartner(false);
        setFormData(data);
        updateCompanySpecificType(data.companyType)
    }
    
    function disableUpdatePartner(disable) {
        $(partnerUpdateBtnId).prop('disabled', disable);
    }
    
    function companyTypeChangeListener() {
        $("input[name='companyType']").click(function() {
            updateCompanySpecificType(this.value);
        });
    }

    function updateCompanySpecificType(companyType) {
        var input = $("#companySpecificType");
        if(parseInt(companyType) == CompanyTypes.OTHER) {
            input.css('visibility', 'visible');
        } else {
            input.css('visibility', 'hidden');
        }
    }

})(jQuery);
