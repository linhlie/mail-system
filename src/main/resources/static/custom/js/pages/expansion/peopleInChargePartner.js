(function () {
    var partnerComboBoxId = "partnerComboBox";
    var partnerIdComboBoxId = "partnerIdComboBox";

    var peopleAddBtnId = "#peopleAdd";
    var peopleUpdateBtnId = "#peopleUpdate";
    var checkboxNextSelectId = "#checkboxNext"
    var peopleClearBtnId = "#peopleClear";

    var peopleTableId = "peopleTable";
    var formId = "#peopleForm";

    var selectPartnerId = "selectPartner";
    var labelDomainId = "labelDomain";

    var partnerGroupTableId = "partnerGroup";
    var partners = null;
    var domains = null;
    var updatingPartnerId = null;
    var selectedSourceTableRow=-1;
    var updatingDomainId = null;

    var formFields = [
        {type: "input", name: "lastName"},
        {type: "input", name: "firstName"},
        {type: "input", name: "department"},
        {type: "input", name: "position"},
        {type: "input", name: "emailAddress"},
        {type: "checkbox", name: "emailAddressIncharge"},
        {type: "input", name: "numberPhone1"},
        {type: "input", name: "numberPhone2"},
        {type: "textarea", name: "specialProblem"},
        {type: "checkbox", name: "pause"}
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

    var peopleReplaceHead = '<tr> ' +
        '<th class="dark">担当者氏名</th>' +
        '<th class="dark">所属部署</th>' +
        '<th class="dark">役職</th>' +
        '<th class="dark">メールアドレス</th>' +
        '<th class="fit dark" style="text-align: center">休止</th>' +
        '<th class="fit dark" style="text-align: center"></th>' +
        '<th class="fit dark" style="text-align: center"></th>' +
        '</tr>';

    var peopleReplaceRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="name"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="department"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="position"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="emailAddress"><span></span></td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="pause">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td name="deletePeople" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="emailAddressInCharge">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '</tr>';


    $(function () {
        initStickyHeader();
        partnerComboBoxListener();
        // partnerIdComboBoxListener();
        setButtonClickListenter(peopleAddBtnId, addPeopleOnClick);
        setButtonClickListenter(peopleUpdateBtnId, updatePeopleOnClick);
        setButtonClickListenter(peopleClearBtnId, clearPeopleOnClick);
        loadBusinessPartners();

        // companyTypeChangeListener();
        // styleShowTableChangeListener();
        // loadBusinessPartners();
        // draggingSetup();
        // setVisibleCountDomain("hidden")
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

    function partnerComboBoxListener() {
        $('#' + selectPartnerId).off('change');
        $('#' + selectPartnerId).change(function() {
            var selected = $(this).find("option:selected");
            var name = selected.text();
            var id = selected.attr("data-id");
            var domain = this.value;
            console.log(id+"   "+name+"  "+domain);
            $('#' + labelDomainId).text(domain);
        });
    }

    function addPeopleOnClick() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    content: "保存に成功しました",
                    onClose: function () {
                        loadBusinessPartners();
                        clearPartnerOnClick();
                    }
                });
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

    function setFormDomainUpdate(form) {
        $('#domain1').val(form.domain);
    }

    function updatePeopleOnClick() {
        clearFormValidate();
        var validated = partnerFormValidate();
        if(!validated) return;
        var data = getFormData();
        var addRemoveGroupPartnerIds = getAddRemoveGroupPartnerIds();
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        loadBusinessPartners(selectNextRow);
                    }
                });
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

    function pFormValidate() {
        var validate1 = lastNameValidate();
        var validate2 = firstNameValidate();
        var validate3 = departmentValidate();
        var validate4 = positionValidate();
        var validate5 = emailAddressValidate();
        var validate6 = numberphone1Validate();
        var validate7 = numberphone2Validate();
        return validate1 && validate2 && validate3 && validate4 && validate5 && validate6 && validate7;
    }


    function showError(error) {
        var container = $(this).closest("div.form-group.row");
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }

    function lastNameValidate() {
        var input = $("input[name='lastName']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }

    function firstNameValidate() {
        var input = $("input[name='firstName']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }

    function departmentValidate() {
        var input = $("input[name='department']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }

    function positionValidate() {
        var input = $("input[name='position']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }

    function emailAddressValidate() {
        var input = $("input[name='position']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }else{
            var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(String(input).toLowerCase());
        }
    }

    function numberphone1Validate() {
        var input = $("input[name='position']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }

    function numberphone2Validate() {
        var input = $("input[name='position']");
        if(!input.val()) {
            showError.apply(input, ["必要"]);
            return false;
        }
        return true;
    }


    function clearPeopleOnClick() {
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
                console.log(response.list);
                setDataComboboxPartner(response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }
        getBusinessPartnersForPeopleInCharge(onSuccess, onError);
    }

    function setDataComboboxPartner(options) {
        options = options ? options.slice(0) : [];
        options.sort(comparePartner);
        $('#' + selectPartnerId).empty();
        $('#' + selectPartnerId).empty();
        $('#' + selectPartnerId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "所属企業",
        }));

        $.each(options, function (i, item) {
            $('#' + selectPartnerId).append($('<option>', {
                value: item.domain,
                text : item.name,
            }).attr('data-id',item.id));
        });
    }

    function loadBusinessPartnersData(tableId, data) {
        partners = data;
        removeAllRow(tableId, partnerReplaceRow);
        if (partners.length > 0) {
            var html = partnerReplaceRow;
            for (var i = 0; i < partners.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> thead").html(partnerReplaceHead);
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
                selectedSourceTableRow = parseInt(index) + 1;
                var rowData = partners[index];
                if (rowData && rowData.id) {
                    selectedRow($('#' + partnerTableId).find(' tbody tr:eq('+selectedSourceTableRow+')'));
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

    function doEditDomain(data) {
        clearFormValidate();
        updatingDomainId = data.id;
        disableUpdatePartner(false);
        setFormDomainUpdate(data);
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
        partnerIdComboBoxListener();
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

    function disableAddPartner(disable) {
        $(partnerAddBtnId).prop('disabled', disable);
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

    function selectNextRow(data){
        if ($(checkboxNextSelectId).is(":checked")){
            var type = $(styleShowTableId + ' option:selected').text();
            if(type == '取引先一覧'){
                selectedSourceTableRow = selectedSourceTableRow+1;
            }
            selectNext(selectedSourceTableRow, data);
        }else{
            clearPartnerOnClick();
        }
    }

    function selectNext(index, data) {
        if(index>data.length) {
            $.alert("最終行まで更新しました");
            clearPartnerOnClick();
        } else {
            var row = $('#' + partnerTableId).find(' tbody tr:eq('+index+')');
            selectedRow(row);
            var rowData = data[index-1];

            var type = $(styleShowTableId + ' option:selected').text();
            if(type == '取引先一覧'){
                doEditPartner(rowData);
            }

            if(type == '未登録取引先一覧'){
                doEditDomain(rowData);
            }
        }
    }

    function selectedRow(row) {
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

    function styleShowTableChangeListener(){
        $(styleShowTableId).change(function(){
            $("#" + partnerTableId).parent().scrollTop(0);
            var type = $(styleShowTableId + ' option:selected').text();
            //Show list partner table
            if(type == '取引先一覧'){
                disableAddPartner(false);
                loadBusinessPartners();
                clearPartnerOnClick();
                setVisibleCountDomain("hidden")
            }

            //Show list domainUnregister table
            if(type == '未登録取引先一覧'){
                disableAddPartner(true);
                loadDomainUnregisters();
                clearPartnerOnClick();
            }

        });
    }

    function setVisibleCountDomain(visibility){
        $(countDomain).css('visibility', visibility);
    }

    function loadDomainUnregisters(callback) {
        function onSuccess(response) {
            if(response && response.status){
                loadDomainUnregisterData(partnerTableId, response.list);
            }
            if(typeof callback == 'function'){
                callback(response.list);
            }
        }

        function onError(error) {

        }
        getDomainUnregisters(onSuccess, onError);


        function onSuccessPartner(response) {
            if(response && response.status){
                updatePartnerComboBox(response.list);
            }
        }

        function onErrorPartner(error) {

        }
        getBusinessPartners(onSuccessPartner, onErrorPartner);
    }

    function loadDomainUnregisterData(tableId, data) {
        domains = data;
        $(countDomain).html("<u>未登録の取引先が"+data.length+"件あります</u>");
        setVisibleCountDomain("visible")
        removeAllRow(tableId, domainReplaceRow);
        if (domains.length > 0) {
            var html = partnerReplaceRow;
            for (var i = 0; i < domains.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> thead").html(domainReplaceHead);
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deleteDomain", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = domains[index];
                if (rowData && rowData.id) {
                    doDeleteDomain(rowData.id);
                }
            });
            setRowClickListener("avoidRegister", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = domains[index];
                if (rowData && rowData.id) {
                    avoidRegister(rowData.id);
                }
            });
            setRowClickListener("editDomain", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                selectedSourceTableRow = parseInt(index) + 1;
                var rowData = domains[index];
                if (rowData && rowData.id) {
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                    doEditDomain(rowData);
                }
            });
        }
    }

    function doDeleteDomain(id) {
        function onSuccess() {
            loadDomainUnregisters()
            clearPartnerOnClick();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        $.confirm({
            title: '',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteDomain(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function avoidRegister(id) {
        function onSuccess() {
            loadDomainUnregisters()
            clearPartnerOnClick();
        }
        function onError() {
            $.alert("取引先の削除に失敗しました。");
        }
        $.confirm({
            title: '',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">本当にこのドメインを無視しますか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        avoidRegisterDomain(id, onSuccess, onError);
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
