(function () {
    "use strict";
    var usersTableId = 'usersTable';
    var users = [];
    var userDataTable;
    var selectedRowData;
    var accountInput = "userName";
    var lastNameInput = "lastName";
    var firstNameInput = "firstName";
    var newPasswordInput = "newPassword";
    var confirmNewPasswordInput = "confirmNewPassword";
    var expansionCheckboxInput = "expansion";

    var editingUserIndex = null;

    var replaceUserHTML = '<tr role="row" class="hidden">' +
        '<td name="userRow" rowspan="1" colspan="3" data="userName"><span></span></td>' +
        '<td name="userRow" rowspan="1" colspan="3" data="name"><span></span></td>' +
        '<td class="fit text-center" name="userRow" rowspan="1" colspan="1" data="expansion"><input type="checkbox" disabled="true" /></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id"><button name="selectUser" type="button">編集</button></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id"><button name="deleteUser" type="button">削除</button></td>' +
        '</tr>';
    $(function () {
        loadUserData();
        initStickyHeader();
        addEventListeners();
    });

    function addEventListeners() {
        setButtonClickListener("userAdd", addUser);
        setButtonClickListener("userUpdate", updateUser);
        setButtonClickListener("userClear", clearEditingUser);
    }

    function enableResizeColums() {
        $("#" + usersTableId).colResizable(
            {
                disable: true,
            }
        );
        $("#" + usersTableId).colResizable(
            {
                resizeMode: 'overflow',
            }
        );
    }

    function loadUserData() {
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/admin/userManagementData",
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    users = data.list;
                } else {
                    console.error("[ERROR] submit failed: ");
                }
                updateData();
            },
            error: function (e) {
                console.error("[ERROR] submit error: ", e);
                $('body').loadingModal('hide');
                updateData();
            }
        });
    }

    function updateData() {
        showUserData(usersTableId, users);
    }

    function showUserData(tableId, data) {
        destroySortUser();
        removeAllRow(tableId, replaceUserHTML);
        if (data && data.length > 0) {
            var html = replaceUserHTML;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
        }
        setButtonClickListener("selectUser", function () {
            var index = $(this).closest('tr')[0].getAttribute("data");
            var rowData = users[index];
            selectedRow.call(this);
            selectUser(rowData, index);
        });
        setButtonClickListener("deleteUser", function () {
            var index = $(this).closest('tr')[0].getAttribute("data");
            deleteUser(index);
        });
        initSortUser();
        updateUserDataTrigger(tableId);
        enableResizeColums();
    }

    function deleteUser(index) {
        var user = users[index];
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/admin/deleteUser?id=" + user.id,
            cache: false,
            timeout: 600000,
            success: function (data) {
                if(data && data.status){
                    window.location.reload();
                } else {
                    console.error("[ERROR] deleteAccount failed: ");
                }
            },
            error: function (e) {
                console.error("[ERROR] deleteAccount error: ", e);
            }
        });

    }

    function updateUserDataTrigger(tableId) {
        $("#" + tableId).trigger("updateAll", [ true, function () {

        } ]);
    }

    function destroySortUser() {
        if (!!userDataTable) {
            userDataTable.destroy();
        }
    }

    function initSortUser() {
        $("#" + usersTableId).tablesorter(
            {
                theme: 'default',
                headers: {
                    2: {
                        sorter: false
                    },
                },
                sortList: []
            });
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
                        if(cellKeys[0] === "name"){
                            cellNode.textContent = getAccountName(data);
                        }else{
                            cellNode.textContent = cellData;
                        }
                    }
                } else if (cellNode.type === "checkbox") {
                    var cellData = data[cellKeys[0]];
                    $(cell).find('input:checkbox').attr('checked', cellData);
                }
            }
        }
        return row.outerHTML;
    }

    function setButtonClickListener(name, callback) {
        $("button[name='" + name + "']").off('click');
        $("button[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = users[index];
        selectedRowData = rowData;
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

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

    function selectUser(user, index) {
        editingUserIndex = index;
        updateEnableUpdateUserAccountBtn();
        setAccount(user.userName);
        setLastName(user.lastName);
        setFirstName(user.firstName);
        setPassword();
        setConfirmPassword();
        setExpansion(user.expansion);
    }
    
    function addUser() {
        var user = {
            userName: getAccount(),
            lastName: getLastName(),
            firstName: getFirstName(),
            newPassword: getPassword(),
            confirmNewPassword: getConfirmassword(),
            expansion: getExpansion(),
        };
        postSaveUser(user);
    }
    
    function updateUser() {
        var user = {
            id: users[editingUserIndex] ? users[editingUserIndex].id : undefined,
            userName: getAccount(),
            lastName: getLastName(),
            firstName: getFirstName(),
            newPassword: getPassword(),
            confirmNewPassword: getConfirmassword(),
            expansion: getExpansion(),
        };
        postSaveUser(user);
    }

    function postSaveUser(payload) {
        payload = payload ? payload : {};
        var payloadStr = JSON.stringify(payload);
        $('body').loadingModal({
            position: 'auto',
            text: '保存中...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/admin/saveUser",
            data: payloadStr,
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if(data && data.status) {
                    window.location.reload();
                } else {
                    handleErrors(data);
                }
            },
            error: function (e) {
                console.error("[ERROR] submit error: ", e);
                $('body').loadingModal('hide');
            }
        });
    }

    function handleErrors(data) {
        clearErrors();
        if(data && data.list) {
            var errors = data.list;
            for(var i = 0; i < errors.length; i++ ){
                var error = errors[i];
                $("span[name='" + error.field + "']").text(error.defaultMessage);
                $("span[name='" + error.field + "']").closest('div.has-feedback').addClass('has-error');
            }
        }
    }
    
    function clearErrors() {
        var fields = [accountInput, lastNameInput, firstNameInput, newPasswordInput, confirmNewPasswordInput];
        for(var i = 0; i < fields.length; i++ ){
            var field = fields[i];
            $("span[name='" + field + "']").text();
            $("span[name='" + field + "']").closest('div.has-feedback').removeClass('has-error');
        }
    }

    function clearEditingUser() {
        $("#" + usersTableId).find("tr").removeClass('highlight-selected');
        clearUserFormInput();
        editingUserIndex = null;
        updateEnableUpdateUserAccountBtn();
    }
    
    function clearUserFormInput() {
        setAccount();
        setLastName();
        setFirstName();
        setPassword();
        setConfirmPassword();
        setExpansion(false);
    }

    function setAccount(account){
        account = account || "";
        $("input[name='" + accountInput + "']").val(account);
    }

    function getAccount() {
        var account = $("input[name='" + accountInput + "']").val();
        account = account || "";
        return account;
    }

    function setLastName(userName) {
        userName = userName || "";
        $("input[name='" + lastNameInput + "']").val(userName);
    }

    function setFirstName(userName) {
        userName = userName || "";
        $("input[name='" + firstNameInput + "']").val(userName);
    }

    function getLastName() {
        var userName = $("input[name='" + lastNameInput + "']").val();
        userName = userName || "";
        return userName;
    }

    function getFirstName() {
        var userName = $("input[name='" + firstNameInput + "']").val();
        userName = userName || "";
        return userName;
    }
    
    function setPassword(password) {
        password = password || "";
        $("input[name='" + newPasswordInput + "']").val(password);
    }

    function getPassword() {
        var password = $("input[name='" + newPasswordInput + "']").val();
        password = password || "";
        return password;
    }
    
    function setConfirmPassword(confirmPassword) {
        confirmPassword = confirmPassword || "";
        $("input[name='" + confirmNewPasswordInput + "']").val(confirmPassword);
    }

    function getConfirmassword() {
        var confirmPassword = $("input[name='" + confirmNewPasswordInput + "']").val();
        confirmPassword = confirmPassword || "";
        return confirmPassword;
    }
    
    function setExpansion(expansion) {
        $("input[name='" + expansionCheckboxInput + "']").prop('checked', expansion);
    }
    
    function getExpansion() {
        var expansion = $("input[name='" + expansionCheckboxInput + "']").is(":checked");
        return !!expansion;
    }

    function updateEnableUpdateUserAccountBtn() {
        var disabled = !isUpdate();
        $("button[name='userUpdate']").prop("disabled", disabled);
    }

    function isUpdate() {
        return !!editingUserIndex;
    }

    function getAccountName(data) {
        if(data.firstName && data.lastName){
            return data.lastName + "　" + data.firstName;
        }

        if(data.firstName){
            return data.firstName;
        }

        return data.lastName;
    }
})(jQuery);
