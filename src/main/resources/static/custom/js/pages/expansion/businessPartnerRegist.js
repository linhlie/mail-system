
(function () {
    var partnerComboBoxId = "partnerComboBox";
    var partnerAddBtnId = "#partnerAdd";
    var partnerUpdateBtnId = "#partnerUpdate";
    var partnerClearBtnId = "#partnerClear";
    var formId = "#partnerForm";
    var partnerTableId = "partner";
    var partnerGroupTableId = "partnerGroup";
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

    var GroupPartnerRowTypes = {
        ORIGINAL: "original",
        NEW: "add",
    }

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

    var groupReplaceRow = '<tr name="group-partner" data-type="original" role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="name"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="partnerCode"><span></span></td>' +
        '<td name="deleteGroupPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        initStickyHeader();
        partnerComboBoxListener();
        setButtonClickListenter(partnerAddBtnId, addPartnerOnClick);
        setButtonClickListenter(partnerUpdateBtnId, updatePartnerOnClick);
        setButtonClickListenter(partnerClearBtnId, clearPartnerOnClick);
        companyTypeChangeListener();
        loadBusinessPartners();
        draggingSetup();
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
    
    function addPartnerComboBox() {
        var tr = '<tr role="row">' +
            '<td rowspan="1" colspan="1">' +
            '<select id="partnerComboBox" style="width: 100%; border: none; padding: 2px;"></select>' +
            '</td>' +
            '<td rowspan="1" colspan="1"></td>' +
            '<td class="fit" rowspan="1" colspan="1">' +
            '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
            '</td>' +
            '</tr>';
        $("#" + partnerGroupTableId).append(tr);
    }
    
    function updatePartnerComboBox(options) {
        options = options ? options.slice(0) : [];
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
                value: item.partnerCode,
                text : item.name,
            }).attr('data-id',item.id));
        });
    }
    
    function partnerComboBoxListener() {
        $('#' + partnerComboBoxId).off('change');
        $('#' + partnerComboBoxId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var code = this.value;
            addPartnerToGroup.apply(this, [id, name, code]);
            $('#' + partnerComboBoxId).prop('selectedIndex',0);
        });
    }
    
    function addPartnerToGroup(id, name, code) {
        var tr = '<tr name="group-partner" data-id="' + id + '" data-type="add" role="row">' +
            '<td rowspan="1" colspan="1" data="name"><span>' + name + '</span></td>' +
            '<td rowspan="1" colspan="1" data="partnerCode"><span>' + code + '</span></td>' +
            '<td name="deleteGroupPartner" class="fit action" rowspan="1" colspan="1" data="id">' +
            '<button type="button">削除</button>' +
            '</td> </tr>';
        $(this).closest('table').find('tr:last').before(tr);
        setDeleteGroupPartnerListener();
    }
    
    function addPartnerOnClick() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
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
        addPartner({
            builder: data,
            groupAddIds: addRemoveGroupPartnerIds.add,
            groupRemoveIds: addRemoveGroupPartnerIds.remove,
        }, onSuccess, onError)
    }
    
    function getAddRemoveGroupPartnerIds() {
        var data = {
            add: [],
            remove: [],
        };
        $('#'+ partnerGroupTableId + ' > tbody  > tr').each(function(i, row) {
            var $row = $(row)
            var type = $row.attr("data-type");
            var id = $row.attr("data-id");
            id = parseInt(id);
            if(type == GroupPartnerRowTypes.NEW) {
                if(!isNaN(id) && data.add.indexOf(id) < 0) {
                    data.add.push(id);
                }
            } else if (type == GroupPartnerRowTypes.ORIGINAL) {
                if($row.hasClass("hidden")) {
                    if(!isNaN(id) && data.remove.indexOf(id) < 0) {
                        data.remove.push(id);
                    }
                }
            }
        });
        return data;
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
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
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

        updatePartner(
            updatingPartnerId,
            {
                builder: data,
                groupAddIds: addRemoveGroupPartnerIds.add,
                groupRemoveIds: addRemoveGroupPartnerIds.remove,
            },
            onSuccess,
            onError
        );
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
        loadBusinessPartnerGroupData(partnerGroupTableId, []);
        resetPartnerTable();
    }
    
    function resetPartnerTable() {
        $("#" + partnerTableId).find('tr.highlight-selected').removeClass('highlight-selected');
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
                updatePartnerComboBox(response.list);
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
        row.setAttribute("data-id", data.id);
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
            console.log("doDeletePartner success: ", id);
            loadBusinessPartners();
            clearPartnerOnClick();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        $.confirm({
            title: '<b>【取引先の削除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deletePartner(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }
    
    function doEditPartner(data) {
        clearFormValidate();
        updatingPartnerId = data.id;
        loadBusinessPartnerGroup(data.id);
        disableUpdatePartner(false);
        setFormData(data);
        updateCompanySpecificType(data.companyType);
    }
    
    function loadBusinessPartnerGroup(partnerId) {
        function onSuccess(response) {
            if(response && response.status) {
                loadBusinessPartnerGroupData(partnerGroupTableId, response.list);
            }
        }
        function onError() {}

        getBusinessPartnerGroup(partnerId, onSuccess, onError);
    }
    
    function loadBusinessPartnerGroupData(tableId, data) {
        removeAllRow(tableId, groupReplaceRow);
        if (data.length > 0) {
            var html = groupReplaceRow;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i].withPartner, i);
            }
            $("#" + tableId + "> tbody").html(html);
            setDeleteGroupPartnerListener();
        }
        addPartnerComboBox();
        updatePartnerComboBox(partners);
        partnerComboBoxListener();
    }
    
    function setDeleteGroupPartnerListener() {
        setRowClickListener("deleteGroupPartner", function () {
            //TODO:
            var tr = $(this).closest('tr');
            var type = tr.attr("data-type");
            if(type == GroupPartnerRowTypes.NEW) {
                tr.remove();
            } else if (type == GroupPartnerRowTypes.ORIGINAL) {
                tr.addClass("hidden");
            }
        });
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

    function draggingSetup() {
        var dragging = false;
        $('#dragbar2').mousedown(function(e){
            e.preventDefault();

            dragging = true;
            var dragbar = $('#dragbar2');
            var ghostbar = $('<div>',
                {id:'ghostbar2',
                    css: {
                        width: dragbar.outerWidth(),
                        top: dragbar.offset().top,
                        left: dragbar.offset().left
                    }
                }).appendTo('body');

            $(document).mousemove(function(e){
                ghostbar.css("top",e.pageY);
            });

        });

        $(document).mouseup(function(e){
            if (dragging)
            {
                var container = $('#partnerBox');
                var topHeight = (e.pageY - container.offset().top);
                console.log("topHeight: ", topHeight);
                var tableHeight = Math.floor(topHeight - 10);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 400 ? tableHeight : 400;
                $('#partnerBox').css("height", tableHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

})(jQuery);