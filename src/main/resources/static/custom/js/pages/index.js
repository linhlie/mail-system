
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
        loadBulletinData();
        loadBulletinPreview();
    });

    function loadBulletinData() {
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
                loadBulletinBoard()
                editor.on('Change', function (e) {
                    disableUpdateBulletinBoard(false);
                });
            },
        });
        disableUpdateBulletinBoard(true);
    }

    function loadBulletinEditor(){
        $('#'+blockPreviewId).css('display', 'none');
        $('#'+blockEdittorId).css('display', 'block');
    }

    function loadBulletinPreview() {
        $('#'+blockEdittorId).css('display', 'none');
        $('#'+blockPreviewId).css('display', 'block');
    }
    
    function loadMailData(accountId) {
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


    function loadBulletinBoard() {
        function onSuccess(response) {
            if (response.bulletinBoardDTO && response.status) {
                pushBulletenBoardData(response.bulletinBoardDTO);
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

    function pushBulletenBoardData(data){
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
            var newBulletin = getBulletinBoard();
            if(newBulletin==null || newBulletin.trim()==""){
                newBulletin = "null";
            }
            function onSuccess(response) {
                if(response && response.status) {
                    $.alert({
                        title: "",
                        content: "保存に成功しました",
                        onClose: function () {
                            loadBulletinBoard();
                            disableUpdateBulletinBoard(true);
                        }
                    });
                } else {
                    $.alert("保存に失敗しました");
                }
            }

            function onError(response) {
                $.alert("保存に失敗しました");
            }
            updateBulletinBoard(newBulletin, onSuccess, onError);
        }

    }

    function disableUpdateBulletinBoard(disable){
        $(updateBulletinBoardId).prop('disabled', disable);
    }

    function changeShowTypeOnclick() {
        if(showType=="preview"){
            $(changeShowTypeId).html("ー");
            loadBulletinEditor();
            showType = "editor";
        }else{
            $(changeShowTypeId).html("+");
            loadBulletinPreview()
            showType = "preview";
        }
    }

})(jQuery);
