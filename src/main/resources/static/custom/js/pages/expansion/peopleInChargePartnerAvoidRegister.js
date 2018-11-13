
(function () {
    var emailTableId = "emailTable";
    var saveEmailsId = "#saveEmails";
    var revertEmailId = "#revertEmails";

    var emails = [];
    var emailsOriginal = [];
    var emailsDelete = [];

    var emailReplaceHead = '<tr>'+
        '<th class="dark">メールアドレス</th>'+
        '<th th:colspan="1"></th>'+
        '</tr>';

    var emailReplaceRow = '<tr role="row" class="hidden">' +
        '<td name="editEmail" rowspan="1" colspan="1" data="email" style="cursor: pointer;">'+
        '<span></span>' +
        '</td>' +
        '<td name="deleteEmail" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    var emailReplaceAddRow = '<tr role="row">' +
        '<td name="addEmail" rowspan="1" colspan="1" data="email" style="cursor: pointer;">'+
        '<span>&nbsp;</span>' +
        '</td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id">' +
        '</td>' +
        '</tr>';

    $(function () {
        initStickyHeader();
        loadEmailsAvoidRegister();
        setButtonClickListenter(saveEmailsId, saveEmailsOnClick);
        setButtonClickListenter(revertEmailId, revertEmailsOnClick);
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

    function loadEmailsAvoidRegister(){
        function onSuccess(response) {
            if(response && response.status){
                response.list.sort(function (a, b) {
                    if(a.email>b.email) return 1;
                    return -1;
                });
                emailsOriginal = [];
                for(var i=0;i<response.list.length;i++){
                    var email = {
                        id: response.list[i].id,
                        email: response.list[i].email,
                        status: response.list[i].status
                    }
                    emailsOriginal.push(email);
                }
                emailsDelete = [];
                loadEmailDataTable(response.list);
            }
            if(typeof callback == 'function'){
                callback(response.list);
            }
        }

        function onError(error) {

        }
        getEmailsAvoidRegisterPeopleInCharge(onSuccess, onError);
    }

    function loadEmailDataTable(data) {
        emails = data;
        removeAllRow(emailTableId, emailReplaceRow);
        if (emails.length >= 0) {
            var html = emailReplaceRow;
            for (var i = 0; i < emails.length; i++) {
                html = html + addRowWithData(emailTableId, data[i], i);
            }
            html = html + emailReplaceAddRow;
            $("#" + emailTableId + "> thead").html(emailReplaceHead);
            $("#" + emailTableId + "> tbody").html(html);
            setRowClickListener("deleteEmail", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = emails[index];
                if (rowData && rowData.email) {
                    doDeleteEmail(rowData.email);
                }
            });
            setRowClickListener("editEmail", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = emails[index];
                if (rowData) {
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                    doEditEmail(rowData);
                }
            });
            setRowClickListener("addEmail", function () {
                doAddEmail("");
            });
        }
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

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function saveEmailsOnClick(){
        var emailsUpdate=[];
        for(var i=0;i<emails.length;i++){
            var index = -1;
            for(var j=0;j<emailsOriginal.length;j++){
                if(emails[i].email == emailsOriginal[j].email){
                    index = j;
                }
            }
            if(index == -1){
                emailsUpdate.push(emails[i]);
            }
        }

        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        loadEmailsAvoidRegister();

                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }

        function onError(response) {
            $.alert("保存に失敗しました");
        }

        saveEmailAvoidRegister({
            emailsUpdate: emailsUpdate,
            emailsDelete: emailsDelete,
        }, onSuccess, onError)

    }

    function revertEmailsOnClick(){
        if(emailsOriginal){
            emails = [];
            emailsDelete = [];
            for(var i=0;i<emailsOriginal.length;i++){
                var email = {
                    id: emailsOriginal[i].id,
                    email: emailsOriginal[i].email,
                    status: emailsOriginal[i].status
                }
                emails.push(email);
            }
            loadEmailDataTable(emails)
        }
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function doDeleteEmail(email) {
        $.confirm({
            title: '',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        for(var i=0;i<emails.length;i++){
                            if(emails[i].email == email){
                                if(emails[i].id){
                                    emailsDelete.push(emails[i]);
                                }
                                emails.splice(i,1);
                            }
                        }
                        loadEmailDataTable(emails);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }

    function doAddEmail(data){
        showNamePrompt(data, function (email) {
            if (email != null && email.length > 0) {
                var newEmail = {
                    email : email,
                    status : 2
                }
                emails.push(newEmail);
                loadEmailDataTable(emails);
            }
        })
    }

    function doEditEmail(data){
        showNamePrompt(data.email, function (email) {
            if (email != null && email.length > 0) {
                data.email = email;
                loadEmailDataTable(emails);
            }
        })
    }

    function showNamePrompt(email, callback) {
        removeError.apply($( '#dataModalName'));
        $('#dataModal').modal();
        $('#dataModalName').val(email);
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var email = $( '#dataModalName').val();
            var isValid = isValidEmailAddress(email);
            var isExist = isExistEmail(email)
            if(isExist){
                showError.apply($( '#dataModalName'), ["メールアドレス存在した。"]);
            }
            if(!isValid){
                showError.apply($( '#dataModalName'), ["メールアドレス無効な。"]);
            }

            if(isValid && !isExist){
                $('#dataModal').modal('hide');
                if(typeof callback === "function"){
                    callback(email);
                }
            }
        });
        $('#dataModalCancel').off('click');
        $("#dataModalCancel").click(function () {
            $('#dataModal').modal('hide');
            if(typeof callback === "function"){
                callback();
            }
        });
    }

    function showError(error, selector) {
        selector = selector || "div.form-group.row";
        var container = $(this).closest(selector);
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }

    function removeError(error, selector) {
        selector = selector || "div.form-group.row";
        var container = $(this).closest(selector);
        container.removeClass("has-error");
        container.find("span.form-error").text("");
    }

    function isValidEmailAddress(emailAddress) {
        var pattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
        return pattern.test(emailAddress);
    }

    function isExistEmail(email) {
        for(var i=0;i<emails.length;i++){
            if(emails[i].email == email){
                return true;
            }
        }
        return false;
    }

})(jQuery);
