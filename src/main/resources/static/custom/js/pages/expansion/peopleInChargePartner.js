(function () {

    var peopleAddBtnId = "#peopleAdd";
    var peopleUpdateBtnId = "#peopleUpdate";
    var checkboxNextSelectId = "#checkboxNext"
    var peopleClearBtnId = "#peopleClear";

    var selectPartnerDivId = "selectPartnerDiv";
    var partnerIdDivId = "partnerIdDiv";

    var peopleTableId = "peopleTable";
    var formId = "#peopleForm";
    var formPartnerId = "#partnerForm";
    var styleShowTableId = "#styleShowTable";
    var countPeopleInChargeUnregisterId = "#countPeopleInChargeUnregister";

    var selectPartnerId = "selectPartner";
    var partnerId = "partnerId";
    var labelDomainId = "labelDomain";

    var currenntPartnerId;
    var updatingPeopleId;
    var listPartner;
    var listPeopleInChargePartner;
    var listPeopleInChargePartnerUnregister;
    var selectedSourceTableRow=-1;
    var alertContentValue = "";
    var alertLevelValue = "";

    var formFields = [
        {type: "select", name: "partnerId"},
        {type: "input", name: "lastName"},
        {type: "input", name: "firstName"},
        {type: "input", name: "department"},
        {type: "input", name: "position"},
        {type: "input", name: "emailAddress"},
        {type: "checkbox", name: "emailInChargePartner"},
        {type: "input", name: "numberPhone1"},
        {type: "input", name: "numberPhone2"},
        {type: "textarea", name: "note"},
        {type: "radio", name: "alertLevel"},
        {type: "textarea", name: "alertContent"},
        {type: "checkbox", name: "pause"},
    ];

    var peopleReplaceHead = '<tr> ' +
        '<th class="dark">担当者氏名</th>' +
        '<th class="dark">所属部署</th>' +
        '<th class="dark">役職</th>' +
        '<th class="dark">メールアドレス</th>' +
        '<th class="fit dark" style="text-align: center">アラート</th>' +
        '<th class="fit dark" style="text-align: center">休止</th>' +
        '<th class="fit dark" style="text-align: center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>' +
        '<th class="fit dark" style="text-align: center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>' +
        '</tr>';

    var peopleReplaceRow = '<tr role="row" class="hidden">' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="name" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="department" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="position" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="emailAddress" style="cursor: pointer"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="alertLevel" style="text-align: center"><span></span></td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="pause">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/dot.png">' +
        '</td>' +
        '<td name="deletePeople" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '<td class="fit" style="text-align: center" rowspan="1" colspan="1" data="emailInChargePartner">' +
        '<img class="hidden" style="padding: 5px; width:20px; height: 20px;" src="/custom/img/dot.png">' +
        '</td>' +
        '</tr>';


    var peopleInChargeUnregisterReplaceHead = '<tr>' +
        '<th class="dark">メールアドレス</th>' +
        '<th colspan="2"></th>' +
        '</tr>';

    var peopleInChargeUnregisterReplaceRow = '<tr role="row" class="hidden">' +
        '<td name="editPeopleInChargeUnregister" rowspan="1" colspan="1" data="email" style="cursor: pointer"><span></span></td>' +
        '<td name="avoidRegister" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">無視</button>' +
        '</td>' +
        '<td name="deletePeopleInChargeUnregister" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
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
        styleShowTableChangeListener();
        showAlertLevelListener();
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
            if(domain){
                var domains = domain.split(",");
                $('#' + labelDomainId).text(domains[0]);
            }
            currenntPartnerId = partnerId;
            clearFormValidate();
            clearPeopleOnClick();
            var type = getShowType();
            if(type == '取引先担当者一覧'){
                loadPeopleInChargePartners(partnerId);
            }
        });

        $('#' + partnerId).off('change');
        $('#' + partnerId).change(function() {
            var selected = $(this).find("option:selected");
            var partnerId = selected.attr("data-id");
            var domain = this.value;
            if(domain){
                var domains = domain.split(",");
                $("#labelDomainPartner").text(domains[0]);
            }
        });
    }

    function addPeopleOnClick() {
        clearFormValidate();
        var validated = formValidate();
        if(!validated) return;
        var data = getFormData();
        var type = getShowType();
        if(type == '取引先担当者一覧'){
            data.partnerId = currenntPartnerId;
        }
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        if(type == '取引先担当者一覧'){
                            loadPeopleInChargePartners(currenntPartnerId);
                        }else{
                            loadPeoleInChargeUnregisters();
                            $('#labelDomainPartner').text("");
                        }
                        clearPeopleOnClick();
                    }
                });
            } else {
                $.alert("保存に失敗しました");
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
            } else if(field.type == "select"){
                form[field.name] = $("" + field.type + "[name='" + field.name + "']").find("option:selected").attr('data-id');
            }else{
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

        alertLevelValue = form.alertLevel;
        alertContentValue = form.alertContent;
        var alertType = form.alertLevel > 0? 1 : 0;
        var showAlertLevel = form.alertLevel > 0? "visible" : "hidden";
        var disibaleAlertContent = form.alertLevel > 0? false : true;
        $("input[name = alertType][value=" + alertType +"]").prop('checked', true);
        $(".showAlertLevel").css("visibility", showAlertLevel);
        $("#alertContent").prop("disabled", disibaleAlertContent);
    }

    function updatePeopleOnClick() {
        clearFormValidate();
        var validated = formValidate();
        if(!validated) return;
        var data = getFormData();
        data.partnerId = currenntPartnerId;
        data.id = updatingPeopleId;
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
        var type = getShowType();
        if(type == "取引先担当者一覧"){
            var validate5 = emailAddressValidate();
            var validate8 = partnerValidate();
            var validate6 = numberphone1Validate();
            var validate7 = numberphone2Validate();
            return validate5 && validate6 && validate7 && validate8;
        }else{
            var validate5 = emailAddressValidate();
            var validate8 = partnerValidate2();
            var validate6 = numberphone1Validate();
            var validate7 = numberphone2Validate();
            return validate5 && validate6 && validate7 && validate8;
        }
    }


    function showError(error) {
        var container = $(this).closest("div.showError");
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }

    function emailAddressValidate() {
        var input = $("input[name='emailAddress']");
        var vaulue = input.val();
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

        if(!vaulue) {
            showError.apply(input, ["入力必須"]);
            return false;
        }

        if(!re.test(String(vaulue).toLowerCase())){
            showError.apply(input, ["無効なメールアドレス"]);
            return false;
        }
        return true;
    }

    function numberphone1Validate() {
        var input = $("input[name='numberPhone1']");
        var vaulue = input.val();
        var reg1 = /^\+\d+$/;
        var reg2 = /^\d+$/;

        if(vaulue && !reg1.test(String(vaulue).toLowerCase()) && !reg2.test(String(vaulue).toLowerCase())){
            showError.apply(input, ["無効な電話番号"]);
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
            showError.apply(input, ["無効な電話番号"]);
            return false;
        }
        return true;
    }


    function partnerValidate() {
        var input = $("#partnerError");
        if(!currenntPartnerId) {
            showError.apply(input, ["取引先を選択してください。"]);
            return false;
        }
        return true;
    }

    function partnerValidate2() {
        var input = $("select[name='partnerId']");
        var value = input.val();
        if(!value) {
            showError.apply(input, ["選んでください"]);
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
        $(".showAlertLevel").css("visibility", "hidden");
        $("#alertContent").prop("disabled", true);
        alertLevelValue=0;
        alertContentValue="";
    }

    function clearFormValidate() {
        $(formId).find(".has-error").removeClass('has-error');
        $(formPartnerId).find(".has-error").removeClass('has-error');
    }

    function loadBusinessPartners() {
        function onSuccess(response) {
            if(response && response.status){
                listPartner = response.list;
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
        // options.sort(comparePartner);
        $('#' + selectPartnerId).empty();
        $('#' + selectPartnerId).empty();
        $('#' + selectPartnerId).append($('<option>', {
            selected: true,
            disabled: true,
            value: "",
            text : "所属企業",
        }));

        $('#' + partnerId).empty();
        $('#' + partnerId).empty();
        $('#' + partnerId).append($('<option>', {
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

            $('#' + partnerId).append($('<option>', {
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
                                var data = response.list[0]
                                doEditPeople(data);
                                selectedRow($('#' + peopleTableId).find(' tbody tr:eq('+selectedSourceTableRow+')'));
                            } else {
                                $.alert("担当者が存在しません。");
                            }
                        } else {
                            $.alert("担当者情報のロードに失敗しました。");
                        }
                    }

                    function onError() {
                        $.alert("担当者情報のロードに失敗しました。");
                    }
                    getDetailPeopleInChargePartner(rowData.id, onSuccess, onError);
                }
            });
        }else{
            $.alert("この取引先はまだ担当者がいません。");
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
                    if(cellKey === "alertLevel"){
                        switch (cellData) {
                            case 1:cellData = "底";
                                break;
                            case 2:cellData = "中";
                                break;
                            case 3:cellData = "高";
                                break;
                            default: cellData="";
                        }
                    }
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
            $.alert("担当者の削除に失敗しました。");
        }
        $.confirm({
            title: '<b>【担当者の削除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
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
        clearFormValidate();
        clearPeopleOnClick();
        disableUpdatePeople(false);
        setFormData(data);
        updatingPeopleId = data.id;
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
        if ($(checkboxNextSelectId).is(":checked")){
            selectedSourceTableRow = selectedSourceTableRow+1;
            selectNext(selectedSourceTableRow, data);
        }else{
            clearPeopleOnClick();
        }
    }

    function selectNext(index, data) {
        if(index>data.length) {
            $.alert("最終行まで更新しました");
            clearPeopleOnClick();
        } else {
            var row = $('#' + peopleTableId).find(' tbody tr:eq('+index+')');
            var rowData = data[index-1];
            function onSuccess(response) {
                if(response && response.status) {
                    if(response.list) {
                        var data = response.list[0];
                        doEditPeople(data);
                        selectedRow(row);
                    } else {
                        $.alert("担当者が存在しません。");
                    }
                } else {
                    $.alert("担当者情報のロードに失敗しました。");
                }
            }

            function onError() {
                $.alert("担当者情報のロードに失敗しました。");
            }
            getDetailPeopleInChargePartner(rowData.id, onSuccess, onError);

        }
    }

    function selectedRow(row) {
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

    function styleShowTableChangeListener(){
        $(styleShowTableId).change(function(){
            $("#" + peopleTableId).parent().scrollTop(0);
            var type = getShowType();
            //Show list people table
            if(type == '取引先担当者一覧'){
                if(currenntPartnerId){
                    loadPeopleInChargePartners(currenntPartnerId);
                }else{
                    $("#" + peopleTableId + "> thead").html(peopleReplaceHead);
                    $("#" + peopleTableId + "> tbody").html(peopleReplaceRow);
                }
                clearPeopleOnClick();
                setVisibleCountPeopleUnregister("hidden");
                $("#" + selectPartnerDivId).css("display", "block");
                $("#" + partnerIdDivId).css("display", "none");
            }

            //Show list peopleUnregister table
            if(type == '未登録取引先担当者'){
                loadPeoleInChargeUnregisters();
                clearPeopleOnClick();
                $("#" + selectPartnerDivId).css("display", "none");
                $("#" + partnerIdDivId).css("display", "block");
            }

        });
    }

    function setVisibleCountPeopleUnregister(visibility){
        $(countPeopleInChargeUnregisterId).css('visibility', visibility);
    }

    function loadPeoleInChargeUnregisters(callback) {
        function onSuccess(response) {
            if(response && response.status){
                loadPeopleInChargeUnregisterData(response.list);
            }
            if(typeof callback == 'function'){
                callback(response.list);
            }
        }

        function onError(error) {

        }
        getPeopleInChargePartnerUnregisters(onSuccess, onError);
    }

    function loadPeopleInChargeUnregisterData(data) {
        listPeopleInChargePartnerUnregister = data;
        $(countPeopleInChargeUnregisterId).html("<u>未登録の担当者が"+data.length+"件あります</u>");
        setVisibleCountPeopleUnregister("visible")
        removeAllRow(peopleTableId, peopleInChargeUnregisterReplaceRow);
        if (listPeopleInChargePartnerUnregister.length > 0) {
            var html = peopleInChargeUnregisterReplaceRow;
            for (var i = 0; i < listPeopleInChargePartnerUnregister.length; i++) {
                html = html + addRowWithData(peopleTableId, listPeopleInChargePartnerUnregister[i], i);
            }
            $("#" + peopleTableId + "> thead").html(peopleInChargeUnregisterReplaceHead);
            $("#" + peopleTableId + "> tbody").html(html);
            setRowClickListener("deletePeopleInChargeUnregister", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = listPeopleInChargePartnerUnregister[index];
                if (rowData && rowData.id) {
                    doDeletePeopleInChargeUnregister(rowData.id);
                }
            });
            setRowClickListener("avoidRegister", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = listPeopleInChargePartnerUnregister[index];
                if (rowData && rowData.id) {
                    doAvoidRegisterPeopleInChargeUnregister(rowData.id);
                }
            });
            setRowClickListener("editPeopleInChargeUnregister", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                selectedSourceTableRow = parseInt(index) + 1;
                var rowData = listPeopleInChargePartnerUnregister[index];
                if (rowData && rowData.id) {
                    doEditPeopleInChargeUnregister(rowData);
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                }
            });
        }
    }

    function doEditPeopleInChargeUnregister(data) {
        clearFormValidate();
        clearPeopleOnClick();
        $("#emailAddress").val(data.email);
        var domain = getDomainFormEmail(data.email);
        selectPartner(domain);
        disableUpdatePeople(true);
    }

    function doAvoidRegisterPeopleInChargeUnregister(id) {
        function onSuccess() {
            loadPeoleInChargeUnregisters();
        }
        function onError() {
            $.alert("担当者登録無視に失敗しました。");
        }
        $.confirm({
            title: '<b>【担当者登録無視】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">担当者登録を無視してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        avoidRegisterPeopleInChargeUnregister(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function doDeletePeopleInChargeUnregister(id) {
        function onSuccess() {
            loadPeoleInChargeUnregisters();
        }
        function onError() {
            $.alert("未登録の担当の削除に失敗しました。");
        }
        $.confirm({
            title: '<b>【未登録の担当の削除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deletePeopleInChargePartnerUnregister(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function getShowType() {
        return type = $(styleShowTableId + ' option:selected').text();
    }

    function getDomainFormEmail(email){
        var index  = email.indexOf("@");
        if(index>0){
            return email.substring(index+1, email.length);
        }else{
            return null;
        }
    }

    function selectPartner(domain){
        var index = 1 ;
        for(var i=0;i<listPartner.length;i++){
            var domains = [];
            if(listPartner[i].domain){
                domains = listPartner[i].domain.split(",");
            }
            for(var j=0; j<domains.length; j++){
                if(domains[j] && domain == domains[j].trim()){
                    index = i + 2;
                    break;
                }
            }
        }
        $('#'+ partnerId +' :nth-child('+ index +')').prop('selected', true);
        if(index>1){
            if(listPartner[index-2].domain){
                var domains = listPartner[index-2].domain.split(",");
                $("#labelDomainPartner").text(domains[0]);
            }
        }else{
            $("#labelDomainPartner").text("");
        }
    }

    function showAlertLevelListener() {
        $('input[type=radio][name=alertType]').change(function() {
            showAlertPartner(this.value);
        });
    }

    function showAlertPartner(value){
        if(value == 0){
            alertLevelValue = $("input[name = alertLevel]:checked").val();
            $("input[name = alertLevel][value=" + 0 +"]").prop('checked', true);
            $(".showAlertLevel").css("visibility", "hidden");
            alertContentValue = $("#alertContent").val();
            $("#alertContent").val("");
            $("#alertContent").prop("disabled", true);
        }else{
            if(!alertLevelValue || alertLevelValue == 0){
                $("input[name = alertLevel][value=" + 3 +"]").prop('checked', true);
                alertContentValue = "";
            }else{
                $("input[name = alertLevel][value=" + alertLevelValue +"]").prop('checked', true);
            }
            $(".showAlertLevel").css("visibility", "visible");
            $("#alertContent").prop("disabled", false);
            $("#alertContent").val(alertContentValue);
        }
    }

})(jQuery);
