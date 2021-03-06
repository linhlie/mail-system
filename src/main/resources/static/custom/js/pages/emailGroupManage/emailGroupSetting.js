
(function () {
    var groupTableId = "groupTable";
    var emailListTableId = "emailListTable";
    var addEmailGroupId = "#addEmailGroup";
    var addEmailListId = "#addEmailList"
    var btnSearchGroupId = "#btnSearchGroup";
    var searchGroupNameId = "searchGroupName";
    var showEmailListId = "showEmailList";
    var searchEmailId = "searchEmail";
    var btnSearchEmailId = "#btnSearchEmail";

    var emailGroups = [];
    var emailList =[];
    var peopleInChargeList = [];
    var dataList = [];
    var emailGroupCurrent;
    var emailCurrent;

    var replaceGroupRow = '<tr role="row" class="hidden">' +
        '<td name="showlistEmailAddress" rowspan="1" colspan="1" data="groupName" style="padding-left:10px; cursor: pointer;"><span></span></td>' +
        '<td name="editEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id" style="text-align: center">' +
        '<button type="button">編集</button>' +
        '</td>' +
        '<td name="deleteEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id" style="text-align: center">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    var replaceListRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="name" style="cursor: pointer;"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="emailAddress" style="cursor: pointer;"><span></span></td>' +
        '<td name="deleteEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id" style="text-align: center">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        if(screen.width==768){
            showEmailListId = "showEmailListArrowDown";
        }
        loadListGroup();
        setButtonClickListenter(addEmailGroupId, addEmailGroupOnclick);
        setButtonClickListenter(addEmailListId, addEmailListOnclick);
        setButtonClickListenter(btnSearchGroupId, searchGroupOnclick);
        setButtonClickListenter(btnSearchEmailId, searchEmailOnclick);
        keyPressOnclick()
    });
    
    function keyPressOnclick() {
        $("#"+searchGroupNameId).keypress(function(event){
            var keycode = (event.keyCode ? event.keyCode : event.which);
            if(keycode == '13'){
                searchGroupOnclick();
            }
        });

        $("#"+searchEmailId).keypress(function(event){
            var keycode = (event.keyCode ? event.keyCode : event.which);
            if(keycode == '13'){
                searchEmailOnclick();
            }
        });
    }

    function setButtonClickListenter(id, callback) {
        $(id).off('click');
        $(id).click(function () {
            if (typeof callback === "function") {
                callback();
            }
        });
    }

    function loadListGroup(groupName) {
        function onSuccess(response) {
            if(response && response.status){
                emailGroups = response.list;
                showDataTable(groupTableId, emailGroups);
            }
        }

        function onError(error) {
        }

        clearAll();
        getEmailAddressGroup(groupName, onSuccess, onError);
    }

    function loadEmailList(id, search) {
        function onSuccess(response) {
            if(response && response.status){
                emailList = response.list;
                peopleInChargeList = response.listPeople;
                showEmailListTable(emailListTableId, emailList);
            }
        }

        function onError(error) {
        }

        emailList = [];
        emailCurrent = null;
        getEmailAddressList(id, search, onSuccess, onError);
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function removeAllRow(tableId, replaceHtml) {
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[2];
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


    function showDataTable(tableId, data) {
        removeAllRow(tableId, replaceGroupRow);
        if (data.length > 0) {
            var html = replaceGroupRow;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("showlistEmailAddress", function () {
                var rowSelect = $(this).closest('tr');
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = emailGroups[index];
                if (rowData && rowData.id) {
                    emailGroupCurrent = rowData;
                    showTableList(rowData.id);
                    rowSelect.addClass('highlight-selected').siblings().removeClass('highlight-selected');
                }
            });

            setRowClickListener("editEmailAddressGroup", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = emailGroups[index];
                if (rowData && rowData.id) {
                    emailGroupCurrent = rowData;
                    editEmailGroupOnclick(rowData.groupName);
                }
            });
            setRowClickListener("deleteEmailAddressGroup", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = emailGroups[index];
                if (rowData && rowData.id) {
                    doDeleteEmailGroup(rowData.id);
                }
            });
        }
    }

    function showEmailListTable(tableId, data) {
        removeAllRow(tableId, replaceListRow);
        if (data.length > 0) {
            var html = replaceGroupRow;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deleteEmailAddressGroup", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = emailList[index];
                if (rowData && rowData.id) {
                    doDeleteEmailInGroup(rowData.id);
                }
            });
        }
    }

    function addEmailGroupOnclick() {
        showAddEditGroupModal("メールグループ追加", "", doAddEmailGroup);
    }

    function editEmailGroupOnclick(groupName) {
        showAddEditGroupModal("メールグループ編集", groupName, doUpdateEmailGroup);
    }

    function doAddEmailGroup(groupName) {
        function onSuccess(response) {
            if(response){
                if(response.status){
                    loadListGroup();
                }else{
                    $.alert("保存に失敗しました");
                }
            }
        }
        function onError(error) {
            $.alert("保存に失敗しました");
        }

        addEmailAddressGroup({
            groupName : groupName
        }, onSuccess, onError);
    }

    function doUpdateEmailGroup(groupName) {
        function onSuccess(response) {
            if(response){
                if(response.status){
                    loadListGroup();
                }else{
                    $.alert("保存に失敗しました");
                }
            }
        }
        function onError(error) {
            $.alert("保存に失敗しました");
        }
        updateEmailAddressGroup({
            id: emailGroupCurrent.id,
            groupName: groupName,
            accountCreateId: emailGroupCurrent.accountCreateId
        }, onSuccess, onError);
    }

    function doDeleteEmailGroup(id) {
        function onSuccess(response) {
            loadListGroup();
        }
        function onError() {
            $.alert("保存に失敗しました。");
        }
        $.confirm({
            title: '<b>【メールグループ消除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">本当に消除したいですか。<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteEmailAddressGroup(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                }
            }
        });
    }

    function setInputAutoComplete(className) {
        $( "." + className ).off('click');
        $( "." + className ).off('mouseleave');
        $( "." + className ).on('click', function() {
            $(this).attr('placeholder',$(this).val());
            $(this).val('');
        });
        $( "." + className ).on('mouseleave', function() {
            if ($(this).val() == '') {
                $(this).val($(this).attr('placeholder'));
            }
        });
    }

    function showError(id, error) {
        $(id).text(error);
    }

    function showAddEditGroupModal(title, name, callback) {
        $('#modalAddGroup').modal();
        $('#modalAddGroupTitle').text(title);
        $('#groupName').val(name);
        showError("#hasErrorModalAddGroup", "");
        setInputAutoComplete("dataModalName");
        $('#modalAddGroupOk').off('click');
        $("#modalAddGroupOk").click(function () {
            var groupName = $('#groupName').val();

            if(groupName==null || groupName.trim()==""){
                showError("#hasErrorModalAddGroup", "除外グループ名");
                return;
            }

            if(groupName == name){
                showError("#hasErrorModalAddGroup", "新しいメールグループ名が現在のグループ名と違わなければなりません");
                return;
            }

            for(var i=0;i<emailGroups.length;i++){
                if(emailGroups[i].groupName == groupName){
                    showError("#hasErrorModalAddGroup", "グループ名が既存しています");
                    return;
                }
            }

            if(typeof callback === "function"){
                callback(groupName);
                $('#modalAddGroup').modal('hide');
            }
        });
        $('#modalAddGroupCancel').off('click');
        $("#modalAddGroupCancel").click(function () {
            $('#modalAddGroup').modal('hide');
        });
    }

    function searchGroupOnclick(){
        var groupName = $("#"+searchGroupNameId).val();
        if(!groupName || groupName==null || groupName.trim()==""){
            loadListGroup();
        }else{
            loadListGroup(groupName);
        }
    }

    function searchEmailOnclick(){
        var search = $("#"+searchEmailId).val();
        if(!search || search==null || search.trim()==""){
            loadEmailList(emailGroupCurrent.id);
        }else{
            loadEmailList(emailGroupCurrent.id, search);
        }
    }

    function addEmailListOnclick() {
        showAddEmailListModal(doAddEmailList);
    }

    function doAddEmailList(ids) {
        function onSuccess(response) {
            if(response){
                if(response.status){
                    loadEmailList(emailGroupCurrent.id)
                }else{
                    $.alert("保存に失敗しました");
                }
            }
        }
        function onError(error) {
            $.alert("保存に失敗しました");
        }

        addEmailAddressToGroup({
            groupId: emailGroupCurrent.id,
            listPeopleId: ids
        }, onSuccess, onError);
    }

    function doDeleteEmailInGroup(id) {
        function onSuccess(response) {
            loadEmailList(emailGroupCurrent.id);
        }
        function onError() {
            $.alert("メールリスト保存が失敗しました");
        }
        $.confirm({
            title: '<b>【メール消除】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">本当に消除したいですか。<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteEmailAddressInGroup(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                }
            }
        });
    }

    function showAddEmailListModal(callback) {
        $('#dataModal').modal();
        $( '#dataModalName').val("");
        showError("#hasErrorModalAddEmailList", "");
        buildDataList();
        setInputAutoComplete("dataModalName");
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var name = $( '#dataModalName').val();
            var ids = validateEmailAddressPeople(name);
            if(ids.length <= 0){
                showError("#hasErrorModalAddEmailList", "メールは取引先担当者一覧に既存しません");
                return;
            }

            $('#dataModal').modal('hide');
            if(typeof callback === "function"){
                callback(ids);
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

    function updateKeyList(datalist) {
        datalist = datalist || [];
        $('#keylist').html('');
        for(var i = 0; i < datalist.length; i++){
            $('#keylist').append("<option value='" + datalist[i].value + "'>");
        }
    }

    function clearAll() {
        $("#" + showEmailListId).addClass("hidden");
        $(addEmailListId).addClass("hidden");
        emailGroups = [];
        emailGroupCurrent = null;
        emailList = [];
        emailCurrent = null;
    }

    function showTableList(id) {
        $("#"+showEmailListId).removeClass("hidden");
        $(addEmailListId).removeClass("hidden");
        loadEmailList(id);
    }

    function buildDataList() {
        dataList = [];
        for(var i=0;i<peopleInChargeList.length;i++){
            dataList.push({
                id : peopleInChargeList[i].id,
                value : getPeopleName(peopleInChargeList[i]) + "/" + peopleInChargeList[i].emailAddress
            });
        }
        updateKeyList(dataList);
    }

    function getPeopleName(people) {
        if(!people || people==null) return "";
        if(people.lastName !=null && people.firstName !=null){
            return people.lastName+ " " + people.firstName;
        }
        if(people.lastName != null){
            return people.lastName;
        }
        if(people.firstName != null){
            return people.firstName;
        }
        return "";
    }

    function validateEmailAddressPeople(name){
        var listId = [];
        for(var i=0;i<dataList.length;i++){
            if(name == dataList[i].value){
                listId.push(dataList[i].id);
                return listId;
            }
        }

        if(name.charAt(0) == "*" && name.charAt(1) == "@"){
            for(var i=0;i<dataList.length;i++){
                var index = dataList[i].value.indexOf("@");
                if(index>0){
                    var domain = "*" + dataList[i].value.substring(index);
                    if(name == domain){
                        listId.push(dataList[i].id);
                    }
                }

            }
        }

        return listId;
    }

})(jQuery);
