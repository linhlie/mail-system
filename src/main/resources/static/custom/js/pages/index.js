
(function () {
    var lastSelectedMailAccountId = "";
    var accountSelectorId = "#accountSelector";
    var bulletinBoardEditorId = "bulletinBoardEditor";
    var bulletinBoardPreviewId = "bulletinBoardPreview";
    var updateBulletinBoardId = "#updateBulletinBoard";
    var clearBulletinBoardId = "#clearBulletinBoard";
    var historyEditId = "#historyEdit";
    var historyCreateId = "#historyCreate";
    var blockEdittorId = "blockEditor";
    var blockPreviewId = "blockPreview";
    var changeShowTypeId = "#changeShowType";
    var showType = "preview";
    var bulletinBoardTabsId = "bulletinBoardTabs";
    var btnAddTagId = "#btn-add-tab";
    var settingPermissionId = ".settingPermissionBulletinBoard";
    var permissionTableId = "permissionTable";
    var selectViewAllId = "#selectViewAll";
    var selectEditAllId = "#selectEditAll";
    var selectDeleteAllId = "#selectDeleteAll";
    var selectChangePermissionAllId = "#selectChangePermissionAll";

    var bulletinArray = [];
    var currentBulletinBoard;
    var currentIndex;
    var accountloggedId;

    var replaceRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="accountName" name="accountName"><span></span></td>' +
        '<td align="center" rowspan="1" colspan="1" data="canView">' +
        '<input type="checkbox" class="selectViewAll"/>' +
        '</td>' +
        '<td align="center" rowspan="1" colspan="1" data="canEdit">' +
        '<input type="checkbox" class="selectEditAll"/>' +
        '</td>' +
        '<td align="center" rowspan="1" colspan="1" data="canDelete">' +
        '<input type="checkbox" class="selectDeleteAll"/>' +
        '</td>' +
        '<td align="center" rowspan="1" colspan="1" data="canChangePermission">' +
        '<input type="checkbox" class="selectCanChangePermissionAll changePermissionCell"/>' +
        '</td>' +
        '</tr>';

    $(function () {
        loadMailData();
        $(accountSelectorId).change(function() {
            lastSelectedMailAccountId = this.value;
            loadMailData(this.value);
        });
        setButtonClickListenter("#forceFetchMailBtn", doForceFetchMail);
        setButtonClickListenter(updateBulletinBoardId, updateBulletinBoardOnclick);
        setButtonClickListenter(clearBulletinBoardId, clearBulletinBoardOnclick);
        setButtonClickListenter(changeShowTypeId, changeShowTypeOnclick);
        setButtonClickListenter(settingPermissionId, settingPermissionOnclick);
        initPrevew();
        loadBulletinPreview();
        initTab();
    });

    function initPrevew() {
        tinymce.init({
            force_br_newlines : true,
            force_p_newlines : false,
            forced_root_block : '',
            selector: '#' + bulletinBoardEditorId,
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 850,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            init_instance_callback: function (editor) {
                loadBulletinBoard(0)
                editor.on('keyup', function (e) {
                    disableUpdateBulletinBoard(false);
                });
            },
        });
        disableUpdateBulletinBoard(true);
    }

    function loadBulletinEditor(){
        $(changeShowTypeId).html("ー");
        $('#'+blockPreviewId).css('display', 'none');
        $('#'+blockEdittorId).css('display', 'block');
        showType = "editor";
    }

    function loadBulletinPreview() {
        $(changeShowTypeId).html("+");
        $('#'+blockEdittorId).css('display', 'none');
        $('#'+blockPreviewId).css('display', 'block');
        showType = "preview";
    }
    
    function loadMailData(accountId) {
        function onSuccess(response) {
            if (response && response.status) {
                pushMailData(response);
            } else {
                console.error("[ERROR] dashboard load data failed: ");
            }
        }

        function onError(error) {
            $('body').loadingModal('hide');
            console.error("[ERROR] dashboard load data error: ", error);
        }

        getMailDataAPI(accountId, onSuccess, onError);
    }


    function loadBulletinBoard(index) {
        $('body').loadingModal('destroy');
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });

        function onSuccess(response) {
            $('body').loadingModal('hide');
            if (response&& response.status) {
                bulletinArray = response.listBulletinBoardDTO;
                accountloggedId = response.accountId;
                pushBulletenBoardData(response.listBulletinBoardDTO, index);
                loadBulletinPreview();
            } else {
                console.warn("[WARN] Bulletin database is empty");
            }
        }

        function onError(error) {
            $('body').loadingModal('hide');
            console.error("[ERROR] dashboard load data error: ", error);
        }

        getBulletinBoardAPI(onSuccess, onError);
    }

    function pushBulletenBoardData(data, index){
        var showIndex = false;
        $("#"+bulletinBoardTabsId).empty();
        if(data==null || data.length==0){
            createNewtab();
        }else{
            for(var i=0;i<data.length;i++){
                if(i == index){
                    showIndex = true;
                    $("#"+bulletinBoardTabsId).append(
                        '<li class="active" ><a href="#tab'+i+'" class="bulletinTab" role="tab" data-toggle="tab">' +
                        '<span>' +data[i].tabName + '</span>'+
                        '<button class="close" type="button" title="Remove this page">×</button>' +
                        '<input id="'+ "tagName"+i +'" class="textTagname">' +
                        '</a></li>'
                    );
                }else{
                    $("#"+bulletinBoardTabsId).append(
                        '<li><a href="#tab'+i+'" class="bulletinTab" role="tab" data-toggle="tab">' +
                        '<span>' +data[i].tabName + '</span>'+
                        '<button class="close" type="button" title="Remove this page">×</button>' +
                        '<input class="textTagname">' +
                        '</a></li>'
                    );
                }
            }
            $("#"+bulletinBoardTabsId).append(
                '<li><a class="btn-add-tag" id="btn-add-tab" href="#" role="tab" data-toggle="tab"> + </a></li>'
            );
            setButtonClickListenter(btnAddTagId, addTagOnclick);

            $( ".bulletinTab" ).each(function() {
                $(this).width($(this).width()+1);
            });

            if(showIndex){
                setDataBulletinBoard(data[index]);
                currentIndex = index;
            }else{
                var tabFirst = $('#'+bulletinBoardTabsId+' a:first');
                tabFirst.tab('show');
                setDataBulletinBoard(data[0]);
                currentIndex = 0;
            }
        }
    }

    function createNewtab() {
        var newBulletin = {
            bulletin: "",
            tabName: "新しいTAB",
            tabNumber: 0,
            timeEdit: "",
            username: "",
        };
        saveBulletin(newBulletin, bulletinArray.length);
    }

    function setDataBulletinBoard(data) {
        var showSettingPermissionType = data.changePermission==true? "visible" : "hidden";
        showSettingPermission(showSettingPermissionType);
        currentBulletinBoard = data;
        setBulletinBoardPreview(data.bulletin);
        setBulletinBoard(data.bulletin);
        setHistoryEditBulletin(data);
    }
    
    function pushMailData(data) {
        data && data.hasSystemError ? $("#hasSystemError").show() : $("#hasSystemError").hide();
        if(data && data.emailAccounts) {
            updateMailAcountSelector(data.emailAccounts)
        }
        if(data && data.latestReceive){
            $("#latestReceive").text(data.latestReceive);
        }
        if(data && data.checkMailInterval){
            $("#checkMailInterval").text(data.checkMailInterval + "分間隔で新着メール受信中");
        }
    }

    function updateMailAcountSelector(accounts) {
        accounts = accounts || [];
        $(accountSelectorId).empty();
        $(accountSelectorId).append($('<option>', {
            selected: lastSelectedMailAccountId === "",
            value: "",
            text : "全ての受信アカウント",
        }));
        $.each(accounts, function (i, item) {
            $(accountSelectorId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id.toString() === lastSelectedMailAccountId)
            }));
        });
    }


    function doForceFetchMail() {
        function onSuccess(response) {
            hideloading();
            if(response && response.status) {
                loadMailData(lastSelectedMailAccountId);
            } else {
                $.alert("受信に失敗しました");
            }
        }

        function onError(response) {
            hideloading();
            $.alert("受信に失敗しました");
        }
        showFetchMailLoading();
        forceFetchMail(onSuccess, onError);
    }

    function showFetchMailLoading() {
        showLoading("最新のメールを受信中");
    }

    function showLoading(message) {
        hideloading();
        $('body').loadingModal({
            position: 'auto',
            text: message,
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
    }

    function hideloading() {
        $('body').loadingModal('hide');
        $('body').loadingModal('destroy');
    }

    function setBulletinBoard(data) {
        if(data==null){
            data = "";
        }
        if (typeof(tinyMCE) != "undefined") {
            var editor = tinymce.get(bulletinBoardEditorId);
            editor.setContent(data);
            editor.undoManager.clear();
            editor.undoManager.add();
        }
    }

    function setBulletinBoardPreview(data) {
        if(data==null){
            data = "";
        }
        $('#'+bulletinBoardPreviewId).html(data);
    }

    function setHistoryEditBulletin(data){
        var history = data.timeEdit+" "+data.username+"により更新"
        var create = data.timeCreate+" "+data.usernameCreate+"により作成";
        $(historyEditId).text(history);
        $(historyCreateId).text(create);
    }

    function getBulletinBoard() {
        var editor = tinymce.get(bulletinBoardEditorId);
        return editor.getContent();
    }
    
    function clearBulletinBoardOnclick() {
        setBulletinBoard();
        disableUpdateBulletinBoard(false);
    }

    function updateBulletinBoardOnclick(){
        function onSuccess(response) {
            if(response && response.status){
                if (typeof(tinyMCE) != "undefined") {
                    var contentBulletin = getBulletinBoard();
                    if(contentBulletin==null){
                        contentBulletin = "";
                    }
                    currentBulletinBoard.bulletin = contentBulletin;
                    saveBulletin(currentBulletinBoard, currentIndex);
                }
            }else{
                $.alert("編集権限がありません");
            }
        }

        function onError(error) {
        }

        checkPermissionEdit(currentBulletinBoard.id, onSuccess, onError);
    }

    function updateBulletinBoardTagname(tagName){
        if (typeof(tinyMCE) != "undefined") {
            var contentBulletin = getBulletinBoard();
            if(contentBulletin==null){
                contentBulletin = "";
            }
            currentBulletinBoard.bulletin = contentBulletin;
            currentBulletinBoard.tabName = tagName;
            saveBulletin(currentBulletinBoard, currentIndex);
        }

    }

    function updateBulletinBoardTagnamePosition(start, end){
        function onSuccess(response) {
            if(response && response.status) {
                loadBulletinBoard(end);
                disableUpdateBulletinBoard(true);
            }
        }
        function onError(response) {
            loadBulletinBoard(0);
            disableUpdateBulletinBoard(true);
        }
        updateBulletinBoardPosition({
            bulletionBoard : bulletinArray[start],
            position: bulletinArray[end].tabNumber -1
        }, onSuccess, onError);
    }

    function saveBulletin(newBulletin, index){
        function onSuccess(response) {
            if(response && response.status) {
                loadBulletinBoard(index);
                disableUpdateBulletinBoard(true);
            }
        }

        function onError(response) {
            console.error(response);
        }
        saveBulletinBoard(newBulletin, onSuccess, onError);
    }

    function disableUpdateBulletinBoard(disable){
        $(updateBulletinBoardId).prop('disabled', disable);
    }

    function changeShowTypeOnclick() {
        if(showType=="preview"){
            loadBulletinEditor();
        }else{
            loadBulletinPreview()
        }
    }

    function initTab() {
        var isCloseTab = false;
        $('#'+bulletinBoardTabsId).on('click','.close',function(){
            isCloseTab = true;
            var idTab = $(this).parents('a').attr('href');
            var liTag = $(this).parents('li');
            var liOld = $("#"+bulletinBoardTabsId).find(".active");
            $.confirm({
                title: '<b>【掲示板タブ消除】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">'+ $(this).siblings('span').text() + 'を削除しますか<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            var index = (idTab+"").slice(4,idTab.length);
                            var bulletin = bulletinArray[index];

                            if(bulletin != null && bulletin.timeEdit != ""){
                                function onSuccess(response) {
                                    if(response && response.status){
                                        function onSuccess() {
                                            if(currentIndex == index && currentIndex>0){
                                                loadBulletinBoard(currentIndex-1);
                                            }else{
                                                loadBulletinBoard(currentIndex);
                                            }
                                            liTag.remove();
                                            $(idTab).remove();
                                        }
                                        function onError() {
                                            $.alert("don't remove");
                                        }

                                        deleteBulletinBoard(bulletin.id, onSuccess, onError);
                                    }else{
                                        $.alert("消除権限がありません");
                                    }
                                }
                                function onError(error) {}

                                checkPermissionDelete(bulletin.id, onSuccess, onError);
                            }

                            isCloseTab = false;
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){
                            liTag.removeClass('active');
                            liOld.addClass('active');
                            isCloseTab = false;
                        }
                    },
                }
            });
        });

        $('#'+bulletinBoardTabsId).on('click','.bulletinTab',function(){
            var tabId = $(this).attr('href');
            var index = (tabId+"").slice(4,tabId.length);
            if(!isCloseTab){
                setDataBulletinBoard(bulletinArray[index]);
                disableUpdateBulletinBoard(true);
                currentIndex = index;
            }
        });

        $('#'+bulletinBoardTabsId).on('dblclick','.bulletinTab',function(){
            var widthLi = $(this).width();
            var liTag = this;
            $(this).width("auto");
            var spanTag = $(this).find('span');
            var inputTag = $(this).find('input');
            spanTag.css('display','none');
            inputTag.val(spanTag.text());
            inputTag.css('display','inline-block');
            $(document).click(function(event) {
                if(!$(event.target).closest(inputTag).length) {
                    var oldName = spanTag.text();
                    var newName = inputTag.val();
                    if(newName!="" && oldName!=newName){
                        spanTag.text(newName);
                        updateBulletinBoardTagname(newName);
                    }else{
                        $(liTag).width(widthLi);
                    }
                    spanTag.css('display','inline-block');
                    inputTag.css('display','none');
                }
            });
        });

        $("#"+bulletinBoardTabsId).sortable({
            start: function(event, ui) {
                var start_pos = ui.item.index();
                ui.item.data('start_pos', start_pos);
            },
            update: function (event, ui) {
                var start_pos = ui.item.data('start_pos');
                var end_pos = ui.item.index();
                if(start_pos==bulletinArray.length || end_pos==bulletinArray.length){
                    $(this).sortable('cancel');
                }else{
                    updateBulletinBoardTagnamePosition(start_pos, end_pos);
                }
            }
        });
    }

    function addTagOnclick() {
        createNewtab();
    }

    function settingPermissionOnclick() {
        function onSuccess(response) {
            if(response && response.status){
                if (response.list && response.list.length > 0){
                    showSettingModal(response.list, saveBulletinPermission);
                }
            }
        }

        function onError(error) {
        }

        getBulletinPermission(currentBulletinBoard.id, onSuccess, onError);
    }

    function showSettingPermission(type) {
        $(settingPermissionId).css("visibility", type);
    }

    function saveBulletinPermission(permissionChange) {
        if(permissionChange.length == 0) return;
        function onSuccess(response) {
            if(response && response.status) {
                loadBulletinBoard(currentIndex);
                disableUpdateBulletinBoard(true);
            }
        }

        function onError(response) {
            loadBulletinBoard(0);
            disableUpdateBulletinBoard(true);
        }
        changeBulletinPermision(permissionChange, onSuccess, onError);
    }

    function showSettingModal(permissionlist, callback) {
        $('#dataModal').modal();

        var showChangePermissionTable = accountloggedId==currentBulletinBoard.accountCreateId? false : true;
        setBulletinPermissionTable(permissionlist, showChangePermissionTable);

        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var word = $( '#word').val();
            var wordExclusion = $( '#wordExclusion').val();
            if(typeof callback === "function"){
                var permissionChange = getPermissionChange(permissionlist);
                callback(permissionChange);
                $('#dataModal').modal('hide');
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

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function setBulletinPermissionTable(permissions, showChangePermissionTable) {
        removeAllRow(permissionTableId, replaceRow);
        if (permissions.length > 0) {
            var html = replaceRow;
            for (var i = 0; i < permissions.length; i++) {
                html = html + addRowWithData(permissionTableId, permissions[i], i);
            }
            $("#" + permissionTableId + "> tbody").html(html);
            setupSelectBoxes();
            $(".changePermissionCell").prop('disabled', showChangePermissionTable);
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
            var cellKey = cell.getAttribute("data");
            if (!cellKey) continue;
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if(cellNode.nodeName == "INPUT") {
                    var cellData = data[cellKey];
                    cellNode.defaultChecked = cellData;
                    cellNode.value = data['id'];
                    if(data['canChangePermission']){
                        cellNode.className = cellNode.className + " changePermissionCell";
                    }
                    switch (cellKey) {
                        case "canView":
                            cellNode.name = "permissionView";
                            break;
                        case "canEdit":
                            cellNode.name = "permissionEdit";
                            break;
                        case "canDelete":
                            cellNode.name = "permissionDelete";
                            break;
                        case "canChangePermission":
                            cellNode.name = "permissionChange";
                            break;
                    }
                } else{
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
                }
            }
        }
        return row.outerHTML;
    }

    function getPermissionChange(permissionlist) {
        var permissionChange = [];
        $("input[name=permissionView]").each(function( index ) {
            var permissionView = $(this);
            var trTag = permissionView.parents('tr');
            var permissionEdit = trTag.find('input[name=permissionEdit]');
            var permissionDelete = trTag.find('input[name=permissionDelete]');
            var permissionChangeCB = trTag.find('input[name=permissionChange]');

            var id = permissionView.val()
            var canView = permissionView.is(":checked");
            var canEdit = permissionEdit.is(":checked");
            var canDelete = permissionDelete.is(":checked")
            var canChangePermission = permissionChangeCB.is(":checked")

            for(var i=0;i<permissionlist.length;i++){
                if(permissionlist[i].id == id){
                    if(accountloggedId == currentBulletinBoard.accountId){
                        if(permissionlist[i].canView != canView || permissionlist[i].canEdit != canEdit
                            || permissionlist[i].canDelete !=canDelete  || permissionlist[i].canChangePermission != canChangePermission){
                            var permission = {};
                            permission.id = permissionlist[i].id;
                            permission.accountId = permissionlist[i].accountId;
                            permission.accountName = permissionlist[i].accountName;
                            permission.bulletinBoardId = permissionlist[i].bulletinBoardId;
                            permission.canView = canView;
                            permission.canEdit = canEdit;
                            permission.canDelete = canDelete;
                            permission.canChangePermission = canChangePermission;

                            permissionChange.push(permission);
                        }
                    }else{
                        if(permissionlist[i].canView != canView || permissionlist[i].canEdit != canEdit || permissionlist[i].canDelete !=canDelete){
                            var permission = {};
                            permission.id = permissionlist[i].id;
                            permission.accountId = permissionlist[i].accountId;
                            permission.accountName = permissionlist[i].accountName;
                            permission.bulletinBoardId = permissionlist[i].bulletinBoardId;
                            permission.canView = canView;
                            permission.canEdit = canEdit;
                            permission.canDelete = canDelete;

                            permissionChange.push(permission);
                        }
                    }
                }
            }
        });
        return permissionChange;
    }

    function setupSelectBoxes() {
        var permissionViewAll = $("input[name=permissionView]").length == $("input[name=permissionView]:checked").length? true: false;
        var permissionEditAll = $("input[name=permissionEdit]").length == $("input[name=permissionEdit]:checked").length? true: false;
        var permissionDeleteAll = $("input[name=permissionDelete]").length == $("input[name=permissionDelete]:checked").length? true: false;
        var permissionChangeAll = $("input[name=permissionChange]").length == $("input[name=permissionChange]:checked").length? true: false;

        $(selectViewAllId).prop("checked", permissionViewAll);
        $(selectEditAllId).prop("checked", permissionEditAll);
        $(selectDeleteAllId).prop("checked", permissionDeleteAll);
        $(selectChangePermissionAllId).prop("checked", permissionChangeAll);

        $(selectViewAllId).off('click');
        $(selectViewAllId).click(function () {
            setAllCheckBoxPermissionView(this.checked);
            if(! $(this).is(':checked')){
                $(selectEditAllId).prop('checked', false);
                setAllCheckBoxPermissionEdit(false);

                $(selectDeleteAllId).prop('checked', false);
                setAllCheckBoxPermissionDelete(false);

                $(selectChangePermissionAllId).prop('checked', false);
                setAllCheckBoxPermissionChange(false);
            }
        });

        $(selectEditAllId).off('click');
        $(selectEditAllId).click(function () {
            setAllCheckBoxPermissionEdit(this.checked)
            if($(this).is(':checked')){
                $(selectViewAllId).prop('checked', true);
                setAllCheckBoxPermissionView(true);
            }else{
                $(selectChangePermissionAllId).prop('checked', false);
                setAllCheckBoxPermissionChange(false);
            }
        });

        $(selectDeleteAllId).off('click');
        $(selectDeleteAllId).click(function () {
            setAllCheckBoxPermissionDelete(this.checked)
            if($(this).is(':checked')){
                $(selectViewAllId).prop('checked', true);
                setAllCheckBoxPermissionView(true);
            }else{
                $(selectChangePermissionAllId).prop('checked', false);
                setAllCheckBoxPermissionChange(false);
            }
        });

        $(selectChangePermissionAllId).off('click');
        $(selectChangePermissionAllId).click(function () {
            setAllCheckBoxPermissionChange(this.checked)
            if($(this).is(':checked')){
                $(selectViewAllId).prop('checked', this.checked);
                setAllCheckBoxPermissionView(true);

                $(selectEditAllId).prop('checked', this.checked);
                setAllCheckBoxPermissionEdit(true);

                $(selectDeleteAllId).prop('checked', this.checked);
                setAllCheckBoxPermissionDelete(true);
            }
        });

        $('input[name=permissionView]').off('click');
        $('input[name=permissionView]').click(function(){
            if($("input[name=permissionView]").length == $("input[name=permissionView]:checked").length) {
                $(selectViewAllId).prop("checked", true);
            } else {
                $(selectViewAllId).prop("checked", false);
            }

            if(! $(this).is(':checked')){
                var trTag = $(this).parents('tr');
                var permissionEdit = trTag.find('input[name=permissionEdit]');
                var permissionDelete = trTag.find('input[name=permissionDelete]');
                var permissionChange = trTag.find('input[name=permissionChange]');

                permissionEdit.prop("checked", false);
                $(selectEditAllId).prop("checked", false);
                permissionDelete.prop("checked", false);
                $(selectDeleteAllId).prop("checked", false);
                permissionChange.prop("checked", false);
                $(selectChangePermissionAllId).prop("checked", false);
            }
        });

        $('input[name=permissionEdit]').off('click');
        $('input[name=permissionEdit]').click(function(){
            if($("input[name=permissionEdit]").length == $("input[name=permissionEdit]:checked").length) {
                $(selectEditAllId).prop("checked", true);
            } else {
                $(selectEditAllId).prop("checked", false);
            }

            if($(this).is(':checked')){
                var trTag = $(this).parents('tr');
                var permissionView = trTag.find('input[name=permissionView]');
                permissionView.prop("checked", true);

                if($("input[name=permissionView]").length == $("input[name=permissionView]:checked").length) {
                    $(selectViewAllId).prop("checked", true);
                } else {
                    $(selectViewAllId).prop("checked", false);
                }
            }else{
                var trTag = $(this).parents('tr');
                var permissionChange = trTag.find('input[name=permissionChange]');

                permissionChange.prop("checked", false);
                $(selectChangePermissionAllId).prop("checked", false);
            }
        });

        $('input[name=permissionDelete]').off('click');
        $('input[name=permissionDelete]').click(function(){
            if($("input[name=permissionDelete]").length == $("input[name=permissionDelete]:checked").length) {
                $(selectDeleteAllId).prop("checked", true);
            } else {
                $(selectDeleteAllId).prop("checked", false);
            }

            if($(this).is(':checked')){
                var trTag = $(this).parents('tr');
                var permissionView = trTag.find('input[name=permissionView]');
                permissionView.prop("checked", true);

                if($("input[name=permissionView]").length == $("input[name=permissionView]:checked").length) {
                    $(selectViewAllId).prop("checked", true);
                } else {
                    $(selectViewAllId).prop("checked", false);
                }
            }else{
                var trTag = $(this).parents('tr');
                var permissionChange = trTag.find('input[name=permissionChange]');

                permissionChange.prop("checked", false);
                $(selectChangePermissionAllId).prop("checked", false);
            }
        });

        $('input[name=permissionChange]').off('click');
        $('input[name=permissionChange]').click(function(){
            if($("input[name=permissionChange]").length == $("input[name=permissionChange]:checked").length) {
                $(selectChangePermissionAllId).prop("checked", true);
            } else {
                $(selectChangePermissionAllId).prop("checked", false);
            }

            if($(this).is(':checked')){
                var trTag = $(this).parents('tr');
                var permissionView = trTag.find('input[name=permissionView]');
                var permissionEdit = trTag.find('input[name=permissionEdit]');
                var permissionDelete = trTag.find('input[name=permissionDelete]');
                permissionView.prop("checked", true);
                permissionEdit.prop("checked", true);
                permissionDelete.prop("checked", true);

                if($("input[name=permissionView]").length == $("input[name=permissionView]:checked").length) {
                    $(selectViewAllId).prop("checked", true);
                } else {
                    $(selectViewAllId).prop("checked", false);
                }

                if($("input[name=permissionEdit]").length == $("input[name=permissionEdit]:checked").length) {
                    $(selectEditAllId).prop("checked", true);
                } else {
                    $(selectEditAllId).prop("checked", false);
                }

                if($("input[name=permissionDelete]").length == $("input[name=permissionDelete]:checked").length) {
                    $(selectDeleteAllId).prop("checked", true);
                } else {
                    $(selectDeleteAllId).prop("checked", false);
                }
            }
        });
    }

    function setAllCheckBoxPermissionView(checked){
        $("input[name=permissionView]").each(function( index ) {
            if($(this).attr('disabled') != 'disabled'){
                $(this).prop('checked', checked);
            }
        });
    }

    function setAllCheckBoxPermissionEdit(checked){
        $("input[name=permissionEdit]").each(function( index ) {
            if($(this).attr('disabled') != 'disabled'){
                $(this).prop('checked', checked);
            }
        });
    }

    function setAllCheckBoxPermissionDelete(checked){
        $("input[name=permissionDelete]").each(function( index ) {
            if($(this).attr('disabled') != 'disabled'){
                $(this).prop('checked', checked);
            }
        });
    }

    function setAllCheckBoxPermissionChange(checked){
        $("input[name=permissionChange]").each(function( index ) {
            if($(this).attr('disabled') != 'disabled'){
                $(this).prop('checked', checked);
            }
        });
    }

})(jQuery);
