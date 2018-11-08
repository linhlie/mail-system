(function () {

    var peopleAddBtnId = "#peopleAdd";
    var peopleUpdateBtnId = "#peopleUpdate";
    var checkboxNextSelectId = "#checkboxNext"
    var peopleClearBtnId = "#peopleClear";

    var peopleTableId = "peopleTable";
    var formId = "#peopleForm";
    var formPartnerId = "#partnerForm";

    var selectPartnerId = "selectPartner";
    var labelDomainId = "labelDomain";

    var currenntPartnerId;
    var updatingPeopleId;
    var listPeopleInChargePartner;
    var selectedSourceTableRow=-1;

    var formFields = [
        {type: "input", name: "lastName"},
        {type: "input", name: "firstName"},
        {type: "input", name: "department"},
        {type: "input", name: "position"},
        {type: "input", name: "emailAddress"},
        {type: "checkbox", name: "emailInChargePartner"},
        {type: "input", name: "numberPhone1"},
        {type: "input", name: "numberPhone2"},
        {type: "textarea", name: "note"},
        {type: "checkbox", name: "pause"}
    ];

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
        '<td name="sourceRow" rowspan="1" colspan="1" data="name" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="department" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="position" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="emailAddress" style="cursor: pointer"><span></span></td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="pause">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '<td name="deletePeople" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="emailInChargePartner">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/checkmark.png">' +
        '</td>' +
        '</tr>';


    $(function () {
        initStickyHeader();
        partnerComboBoxListener();
        setButtonClickListenter(peopleAddBtnId, addPeopleOnClick);
        setButtonClickListenter(peopleUpdateBtnId, updatePeopleOnClick);
        setButtonClickListenter(peopleClearBtnId, clearPeopleOnClick);
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

    function partnerComboBoxListener() {
        $('#' + selectPartnerId).off('change');
        $('#' + selectPartnerId).change(function() {
            var selected = $(this).find("option:selected");
            var partnerId = selected.attr("data-id");
            var domain = this.value;
            $('#' + labelDomainId).text(domain);
            currenntPartnerId = partnerId;
            clearFormValidate();
            clearPeopleOnClick();
            loadPeopleInChargePartners(partnerId);
        });
    }

    function addPeopleOnClick() {
        clearFormValidate();
        var validated = formValidate();
        if(!validated) return;
        var data = getFormData();
        data.partnerId = currenntPartnerId;
        console.log(data);
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    content: "保存に成功しました",
                    onClose: function () {
                        loadPeopleInChargePartners(currenntPartnerId);
                        clearPeopleOnClick();
                    }
                });
            } else {
                $.alert(response.msg);
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }
        addPeopleInChargePartner(data, onSuccess, onError)
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
        var validated = formValidate();
        if(!validated) return;
        var data = getFormData();
        data.partnerId = currenntPartnerId;
        data.id = updatingPeopleId;
        console.log(data);
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        loadPeopleInChargePartners(currenntPartnerId, selectNextRow);
                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }

        updatePeopleInChargePartner(data, onSuccess, onError);
    }

    function formValidate() {
        var validate1 = lastNameValidate();
        var validate2 = firstNameValidate();
        var validate3 = departmentValidate();
        var validate4 = positionValidate();
        var validate5 = emailAddressValidate();
        var validate6 = numberphone1Validate();
        var validate7 = numberphone2Validate();
        var validate8 = partnerValidate();
        return validate1 && validate2 && validate3 && validate4 && validate5 && validate6 && validate7 && validate8;
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
        var input = $("input[name='emailAddress']");
        var vaulue = input.val();
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

        if(!vaulue) {
            showError.apply(input, ["必要"]);
            return false;
        }

        if(!re.test(String(vaulue).toLowerCase())){
            showError.apply(input, ["email invalid"]);
            return false;
        }
        return true;
    }

    function numberphone1Validate() {
        var input = $("input[name='numberPhone1']");
        var vaulue = input.val();
        var reg1 = /^\+\d+$/;
        var reg2 = /^\d+$/;
        if(!vaulue) {
            showError.apply(input, ["必要"]);
            return false;
        }

        if(!reg1.test(String(vaulue).toLowerCase()) && !reg2.test(String(vaulue).toLowerCase())){
            showError.apply(input, ["number phone invalid"]);
            return false;
        }
        return true;
    }

    function numberphone2Validate() {
        var input = $("input[name='numberPhone2']");
        var vaulue = input.val();
        var reg1 = /^\+\d+$/;
        var reg2 = /^\d+$/;

        if(vaulue && !reg1.test(String(vaulue).toLowerCase()) && !reg2.test(String(vaulue).toLowerCase())){
            showError.apply(input, ["number phone invalid"]);
            return false;
        }
        return true;
    }

    function partnerValidate() {
        var input = $("#partnerError");
        if(!currenntPartnerId) {
            showError.apply(input, ["You must select business partner"]);
            return false;
        }
        return true;
    }

    function clearPeopleOnClick() {
        resetForm();
        clearFormValidate();
        disableUpdatePeople(true);
        updatingPeopleId = null;
        resetPeopleTable();
    }

    function resetPeopleTable() {
        $("#" + peopleTableId).find('tr.highlight-selected').removeClass('highlight-selected');
    }

    function resetForm() {
        $(formId).trigger("reset");
    }

    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
        $(formPartnerId).find(".has-error").removeClass('has-error');
    }

    function loadBusinessPartners() {
        function onSuccess(response) {
            if(response && response.status){
                // console.log(response.list);
                setDataComboboxPartner(response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }
        getBusinessPartnersForPeopleInCharge(onSuccess, onError);
    }

    function loadPeopleInChargePartners(partnerId, callback){
        function onSuccess(response) {
            if(response && response.status){
                // console.log(response.list);
                setDataTablePeople(response.list);
                clearPeopleOnClick();
            }
            if(typeof callback == 'function'){
                callback(response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }
        getPeopleInChargePartners(partnerId, onSuccess, onError);
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

    function setDataTablePeople(listPeople) {
        listPeopleInChargePartner = listPeople;
        removeAllRow(peopleTableId, peopleReplaceRow);
        if (listPeople.length > 0) {
            var html = peopleReplaceRow;
            for (var i = 0; i < listPeople.length; i++) {
                html = html + addRowWithData(peopleTableId, listPeople[i], i);
            }
            $("#" + peopleTableId + "> thead").html(peopleReplaceHead);
            $("#" + peopleTableId + "> tbody").html(html);

            setRowClickListener("deletePeople", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = listPeopleInChargePartner[index];
                if (rowData && rowData.id) {
                    doDeletePeople(rowData.id);
                }
            });

            setRowClickListener("sourceRow", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                selectedSourceTableRow = parseInt(index) + 1;
                var rowData = listPeopleInChargePartner[index];
                if (rowData && rowData.id) {
                    function onSuccess(response) {
                        if(response && response.status) {
                            if(response.list && response.list.length > 0) {
                                var data = response.list[0];
                                selectedRow($('#' + peopleTableId).find(' tbody tr:eq('+selectedSourceTableRow+')'));
                                doEditPeople(data);
                            } else {
                                $.alert("the people doesn't esxit");
                            }
                        } else {
                            $.alert("unload infor the people");
                        }
                    }

                    function onError() {
                        $.alert("unload infor the people");
                    }
                    getDetailPeopleInChargePartner(rowData.id, onSuccess, onError);
                }
            });
        }else{
            $.alert("Don't have the people in charge of this partner");
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
            var cellKey = cell.getAttribute("data");
            if (!cellKey) continue;
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if(cellNode.nodeName == "IMG") {
                    var cellData = data[cellKey];
                    cellNode.className = !!cellData ? undefined : cellNode.className;
                } else if (cellNode.nodeName == "SPAN") {
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
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

    function doDeletePeople(id) {
        function onSuccess() {
            loadPeopleInChargePartners(currenntPartnerId);
        }
        function onError() {
            $.alert("can't delete");
        }
        $.confirm({
            title: '<b>【Delete peopple】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">do you want to delete it？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deletePeopleInChargePartner(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function doEditPeople(data) {
        // console.log(data);
        updatingPeopleId = data.id;
        clearFormValidate();
        disableUpdatePeople(false);
        setFormData(data);
    }


    function disableUpdatePeople(disable) {
        $(peopleUpdateBtnId).prop('disabled', disable);
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
        console.log(data);
        if ($(checkboxNextSelectId).is(":checked")){
            selectedSourceTableRow = selectedSourceTableRow+1;
            selectNext(selectedSourceTableRow, data);
        }else{
            clearPeopleOnClick();
        }
    }

    function selectNext(index, data) {
        console.log(index+" "+data.length);
        if(index>data.length) {
            $.alert("最終行まで更新しました");
            clearPeopleOnClick();
        } else {
            var row = $('#' + peopleTableId).find(' tbody tr:eq('+index+')');
            selectedRow(row);
            var rowData = data[index-1];

            function onSuccess(response) {
                if(response && response.status) {
                    if(response.list) {
                        var data = response.list[0];
                        doEditPeople(data);
                    } else {
                        $.alert("people doesn't esxit");
                    }
                } else {
                    $.alert("unload the people");
                }
            }

            function onError() {
                $.alert("unload the people");
            }
            getDetailPeopleInChargePartner(rowData.id, onSuccess, onError);

        }
    }

    function selectedRow(row) {
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

})(jQuery);
