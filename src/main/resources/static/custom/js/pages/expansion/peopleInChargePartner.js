(function () {

    var peopleAddBtnId = "#peopleAdd";
    var peopleUpdateBtnId = "#peopleUpdate";
    var checkboxNextSelectId = "#checkboxNext"
    var peopleClearBtnId = "#peopleClear";

    var peopleTableId = "peopleTable";
    var formId = "#peopleForm";
    var formPartnerId = "#partnerForm";
    var styleShowTableId = "#styleShowTable";
    var countPeopleInChargeUnregisterId = "#countPeopleInChargeUnregister";

    var selectPartnerId = "selectPartner";
    var labelDomainId = "labelDomain";

    var currenntPartnerId;
    var updatingPeopleId;
    var listPeopleInChargePartner;
    var listPeopleInChargePartnerUnregister;
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
        '<th class="fit dark" style="text-align: center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>' +
        '<th class="fit dark" style="text-align: center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>' +
        '</tr>';

    var peopleReplaceRow = '<tr role="row" class="hidden">' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="name" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="department" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="position" style="cursor: pointer"><span></span></td>' +
        '<td name="sourceRow" rowspan="1" colspan="1" data="emailAddress" style="cursor: pointer"><span></span></td>' +
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
            var type = $(styleShowTableId + ' option:selected').text();
            if(type == '担当者一覧'){
                loadPeopleInChargePartners(partnerId);
            }
        });
    }

    function addPeopleOnClick() {
        clearFormValidate();
        var validated = formValidate();
        if(!validated) return;
        var data = getFormData();
        data.partnerId = currenntPartnerId;
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        var type = $(styleShowTableId + ' option:selected').text();
                        if(type == '担当者一覧'){
                            loadPeopleInChargePartners(currenntPartnerId);
                        }else{
                            loadPeoleInChargeUnregisters();
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
        var validate5 = emailAddressValidate();
        var validate8 = partnerValidate();
        var validate6 = numberphone1Validate();
        var validate7 = numberphone2Validate();
        return validate5 && validate6 && validate7 && validate8;
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
            showError.apply(input, ["メールアドレス無効な"]);
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
            showError.apply(input, ["電話番号無効"]);
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
            showError.apply(input, ["電話番号無効"]);
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
            selectedRow(row);
            var rowData = data[index-1];

            function onSuccess(response) {
                if(response && response.status) {
                    if(response.list) {
                        var data = response.list[0];
                        doEditPeople(data);
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
            var type = $(styleShowTableId + ' option:selected').text();
            //Show list partner table
            if(type == '担当者一覧'){
                loadPeopleInChargePartners(currenntPartnerId);
                clearPeopleOnClick();
                setVisibleCountPeopleUnregister("hidden")
            }

            //Show list domainUnregister table
            if(type == '未登録担当者一覧'){
                loadPeoleInChargeUnregisters();
                clearPeopleOnClick();
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
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                    doEditPeopleInChargeUnregister(rowData);
                }
            });
        }
    }

    function doEditPeopleInChargeUnregister(data) {
        clearFormValidate();
        $("#emailAddress").val(data.email);
        disableUpdatePeople(true);
    }

    function doAvoidRegisterPeopleInChargeUnregister(id) {
        function onSuccess() {
            loadPeoleInChargeUnregisters();
        }
        function onError() {
            $.alert("don't avoid register people in charge partner。");
        }
        $.confirm({
            title: '<b>【avoid register people in charge partner】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">do you want to avoid register people in charge partner？<br/></div>',
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
})(jQuery);
