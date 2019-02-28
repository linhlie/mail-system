
(function () {
    var groupTableId = "groupTable";
    var addEmailGroupId = "#addEmailGroup";

    var emailGroups = [];
    var emailGroupCurrent;

    var replaceGroupRow = '<tr role="row" class="hidden">' +
        '<td name="showlistEmailAddress" rowspan="1" colspan="1" data="groupName" style="text-align: center; cursor: pointer;"><span></span></td>' +
        '<td name="editEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id">' +
        '<button type="button">Edit</button>' +
        '</td>' +
        '<td name="deleteEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id">' +
        '<button type="button">Delete</button>' +
        '</td>' +
        '</tr>';

    var replaceListRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="persionNme" style="text-align: center; cursor: pointer;"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="emailAddress" style="text-align: center; cursor: pointer;"><span></span></td>' +
        '<td name="editEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id">' +
        '<button type="button">Edit</button>' +
        '</td>' +
        '<td name="deleteEmailAddressGroup" class="fit action deleteEngineerRow" rowspan="1" colspan="1" data="id">' +
        '<button type="button">Delete</button>' +
        '</td>' +
        '</tr>';

    $(function () {
        loadListGroup();
        setButtonClickListenter(addEmailGroupId, addEmailGroupOnclick);
    });

    function setButtonClickListenter(id, callback) {
        $(id).off('click');
        $(id).click(function () {
            if (typeof callback === "function") {
                callback();
            }
        });
    }

    function loadListGroup() {
        function onSuccess(response) {
            if(response && response.status){
                emailGroups = response.list;
                showDataTable(groupTableId, emailGroups);
            }
        }

        function onError(error) {
        }

        getEmailAddressGroup(onSuccess, onError);
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
            setRowClickListener("sourceRow", function () {
                selectedRow($(this).closest('tr'))
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

    function addEmailGroupOnclick() {
        showAddEditGroupModal("Add Email Group", "", doAddEmailGroup);
    }

    function editEmailGroupOnclick(groupName) {
        showAddEditGroupModal("Edit Email Group", groupName, doUpdateEmailGroup);
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
            title: '<b>【Delete Email Group】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">Do you want delete group？<br/></div>',
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
                showError("#hasErrorModalAddGroup", " New group name must different old group name");
                return;
            }

            for(var i=0;i<emailGroups.length;i++){
                if(emailGroups[i].groupName == groupName){
                    showError("#hasErrorModalAddGroup", "Group Name is exist");
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

})(jQuery);
