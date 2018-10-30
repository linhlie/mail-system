
(function () {
    var lastSelectedMailAccountId = "";
    var accountSelectorId = "#accountSelector";
    var bulletinBoardEditorId = "bulletinBoardEditor";
    var bulletinBoardPreviewId = "bulletinBoardPreview";
    var updateBulletinBoardId = "#updateBulletinBoard";
    var clearBulletinBoardId = "#clearBulletinBoard";
    var historyEditId = "#historyEdit";
    var blockEdittorId = "blockEditor";
    var blockPreviewId = "blockPreview";
    var changeShowTypeId = "#changeShowType";
    var showType = "preview";
    var bulletinBoardTabsId = "bulletinBoardTabs";
    var btnAddTagId = "#btn-add-tab";
    var bulletinArray = [];
    var currentBulletinBoard;
    var currentIndex;

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
            tabNumber: bulletinArray.length+1,
            timeEdit: "",
            username: "",
        };
        saveBulletin(newBulletin, bulletinArray.length);
    }

    function setDataBulletinBoard(data) {
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
        $(historyEditId).text(history);
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
        if (typeof(tinyMCE) != "undefined") {
            var contentBulletin = getBulletinBoard();
            if(contentBulletin==null){
                contentBulletin = "";
            }
            currentBulletinBoard.bulletin = contentBulletin;
            // currentBulletinBoard.tabName = "tagName";
            saveBulletin(currentBulletinBoard, currentIndex);
        }

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
            console.log(response);
        }
        updateBulletinBoardPosition(start+"_"+end, onSuccess, onError);
    }

    function saveBulletin(newBulletin, index){
        function onSuccess(response) {
            if(response && response.status) {
                loadBulletinBoard(index);
                disableUpdateBulletinBoard(true);
            }
        }

        function onError(response) {
            console.log(response);
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
            $.confirm({
                title: '<b>【Delete】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">'+ $(this).siblings('span').text() + 'を削除しますか<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            var index = (idTab+"").slice(4,idTab.length);
                            var bulletin = bulletinArray[index];

                            if(bulletin != null && bulletin.timeEdit != ""){
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
                            }

                            isCloseTab = false;
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){
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

})(jQuery);
