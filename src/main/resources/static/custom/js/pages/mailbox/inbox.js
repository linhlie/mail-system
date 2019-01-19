(function () {
    "use strict";
    var inboxTableId = 'inboxTable';
    var totalEmailId = 'totalEmail';
    var paginationInboxId = 'paginationInbox';
    var inboxBuilderId = 'inbox-builder';
    var btnFilterId = "#btnFilter";
    var selectPageSizeId = "#selectPageSize"
    var cbReplyAllEmailId = '#cbReplyAllEmail';
    var btnSendMailInboxId = '#btnSendMailInbox';

    var listEmailInbox = null;
    var totalEmail = null;
    var start = null;
    var end = null;
    var totalPages = null;
    var currentPage = null;
    var flagCheckReload = true;

    var filterConditionKey = 'filterConditionInboxEmail';
    var pageSizeKey = 'pageSizeInboxEmail';
    var filterCondition = null;
    var pageSize = null;

    var default_filter_condition = {
        "condition": "AND",
        "rules": [
        ],
        "valid": true
    };

    var default_page_size = 15;

    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';

    var rdMailBodyId = 'rdMailBodyInbox';
    var rdMailSenderId = 'rdMailSender';
    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");

    var attachmentDropzoneId = "#reply-dropzone";
    var attachmentDropzone;

    var ruleInvalidateIds = [];

    var RULE_NUMBER_ID = 4;
    var RULE_NUMBER_UP_RATE_ID = 5;
    var RULE_NUMBER_DOWN_RATE_ID = 6;

    var ruleNumberId = "ruleNumber";
    var ruleNumberUpRateId = "ruleNumberUpRate";
    var ruleNumberDownRateId = "ruleNumberDownRate";

    var ruleNumberDownRateName = "";
    var ruleNumberUpRateName = "";
    var ruleNumberName = "";

    var replaceBody = '<tr role="row" class="hidden">' +
        '<td class="clickable tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="to"><span></span></td>' +
        '<td class="clickable tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="hasAttachment"><i></i></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="status"><i></i></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="receiveAt"><span></span></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="mark"><span></span></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="replyTimes"><span></span></td>' +
        '<td class="text-center" rowspan="1" colspan="1" data="messageId">' +
        '<input type="checkbox" class="selectEmailInbox"/>' +
        '</td>' +
        '</tr>';

    var extensionCommands = {
        ".pdf": "ms-word:ofv|u|",
        ".docx": "ms-word:ofv|u|",
        ".doc": "ms-word:ofv|u|",
        ".xls": "ms-excel:ofv|u|",
        ".xlsx": "ms-excel:ofv|u|",
        ".xlsm": "ms-excel:ofv|u|",
        ".ppt": "ms-powerpoint:ofv|u|",
        ".pptx": "ms-powerpoint:ofv|u|",
    }


    $(function () {
        var default_plugins = [
            'sortable',
            'filter-description',
            'unique-filter',
            'bt-tooltip-errors',
            'bt-selectpicker',
            'bt-checkbox',
            'invert',
        ];

        var default_filters = [{
            id: '0',
            label: '送信者',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '1',
            label: '受信者',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '9',
            label: 'CC',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '10',
            label: 'BCC',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '11',
            label: '全て(受信者・CC・BCC)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '12',
            label: 'いずれか(受信者・CC・BCC)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '2',
            label: '件名',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '3',
            label: '本文',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '13',
            label: '全て(件名・本文)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '14',
            label: 'いずれか(件名・本文)',
            type: 'string',
            operators: ['contains', 'not_contains', 'equal', 'not_equal']
        }, {
            id: '7',
            label: '添付ファイル',
            type: 'integer',
            input: 'radio',
            values: {
                1: '有り',
                0: '無し'
            },
            colors: {
                1: 'success',
                0: 'danger'
            },
            operators: ['equal']
        }, {
            id: '8',
            label: '受信日',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less']
        }, {
            id: '15',
            label: 'マーク',
            type: 'string',
            operators: ['equal', 'not_equal']
        }, {
            id: '16',
            label: '受信ルール合致',
            type: 'integer',
            input: 'radio',
            values: {
                1: '有り',
                0: '無し'
            },
            colors: {
                1: 'success',
                0: 'danger'
            },
            operators: ['equal']
        }];

        ruleNumberDownRateName = $('#'+ruleNumberDownRateId).text();
        if(!ruleNumberDownRateName || ruleNumberDownRateName==null){
            ruleInvalidateIds.push(RULE_NUMBER_DOWN_RATE_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_DOWN_RATE_ID,
                label: ruleNumberDownRateName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }

        ruleNumberUpRateName = $('#'+ruleNumberUpRateId).text();
        if(!ruleNumberUpRateName || ruleNumberUpRateName==null){
            ruleInvalidateIds.push(RULE_NUMBER_UP_RATE_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_UP_RATE_ID,
                label: ruleNumberUpRateName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }

        ruleNumberName = $('#'+ruleNumberId).text();
        if(!ruleNumberName || ruleNumberName==null){
            ruleInvalidateIds.push(RULE_NUMBER_ID);
        }else{
            default_filters.splice(10,0,{
                id: RULE_NUMBER_ID,
                label: ruleNumberName,
                type: 'string',
                operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
                validation: {
                    callback: numberValidator
                },
            })
        }

        var default_configs = {
            plugins: default_plugins,
            allow_empty: true,
            filters: default_filters,
            rules: null,
            lang: globalConfig.default_lang,
        };

        $('#'+inboxBuilderId).queryBuilder(default_configs);
        loadEmailData(0);
        setButtonClickListenter(btnFilterId, showSettingCondition);
        setButtonClickListenter(btnSendMailInboxId, sendMailOnclick);

        initTinyMCE();
        initDropzone();
        initStickyHeader();
        previewDraggingSetup();
        settingPageSize();
    });
    
    function initTinyMCE() {
        tinymce.init({
            force_br_newlines : true,
            force_p_newlines : false,
            forced_root_block : '',
            selector: '#' + rdMailBodyId,
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 350,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor colorpicker',
                'placeholder',
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            fontsize_formats: '6pt 8pt 10pt 11pt 12pt 13pt 14pt 16pt 18pt 20pt 24pt 28pt 32pt 36pt 40pt 45pt 50pt',
        });
    }

    function initDropzone() {
        Dropzone.autoDiscover = false;
        attachmentDropzone = new Dropzone("div" + attachmentDropzoneId, {
            url: "/upload",
            addRemoveLinks: true,
            maxFilesize: 2,
            filesizeBase: 1000,
            dictRemoveFile: "削除",
            dictCancelUpload: "キャンセル",
            dictDefaultMessage: "Drop files here or click to upload.",
            init: function () {
                this.on("success", function (file, response) {
                    if (response && response.status) {
                        var data = response.list && response.list.length > 0 ? response.list[0] : null;
                        if (data) {
                            file.id = data.id;
                        }
                    }
                });
                this.on("removedfile", function (file) {
                    if (!!file && !!file.upload && !!file.id) {
                        removeFile(file.id)
                    }
                });
            },
            thumbnail: function (file, dataUrl) {
            }
        })
    }

    function previewDraggingSetup() {
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
                var container = $('#table-section');
                var topHeight = (e.pageY - container.offset().top);
                var tableHeight = Math.floor(topHeight - 55);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 800 ? tableHeight : 800;
                var previewHeightChange = 450 - tableHeight;
                var previewHeight = 444 + previewHeightChange;
                $('.matching-result .table-container').css("height", tableHeight + "px");
                $('.matching-result .mail-body-container').css("height", previewHeight + "px");
                $('.matching-result .mail-body').css("height", previewHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

    function loadEmailData(page) {
        filterCondition = getBeforeFilterCondition();
        pageSize = getPageSize();
        $('body').loadingModal({
            position: 'auto',
            text: 'ロード中...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        function onSuccess(response) {
            hideloading();
            if (response && response.status) {
                var data  = response.list[0];
                if(data){
                    listEmailInbox = data.listEmail? data.listEmail : [];
                    totalEmail = data.totalEmail;
                    start = data.start;
                    end = data.end;
                    totalPages = data.totalPages;
                }else{
                    listEmailInbox = [];
                    totalEmail = 0;
                    start = -1;
                    end = 0;
                    totalPages = 0;
                }
            } else {
                console.error("[ERROR] submit failed: ");
            }
            currentPage = page;
            updateData();
        }

        function onError(error) {
            console.error("[ERROR] submit error: ", error);
            hideloading();
        }
        filterInbox({
            filterRule: filterCondition,
            page: page,
            pageSize: pageSize
        }, onSuccess, onError);
    }

    function enableResizeColums() {
        $("#" + inboxTableId).colResizable(
            {
                disable: true,
            }
        );
        $("#" + inboxTableId).colResizable(
            {
                resizeMode: 'overflow',
            }
        );
    }

    function updateData() {
        if(listEmailInbox){
            showInboxTable(listEmailInbox)
            updateTotalEmail(start, end);
            updatePageActive();
        }
    }

    function showInboxTable(data) {
        removeAllRow(inboxTableId, replaceBody);
        if (data.length > 0) {
            var html = replaceBody;
            for (var i = 0; i < data.length; i++) {
                html = html + addRowWithData(data[i], i);
            }
            $("#" + inboxTableId + "> tbody").html(html);
            setRowClickListener("showEmailInbox", selectedRow);
        }
        selectFirstRow();
        updateHistoryDataTrigger();
        enableResizeColums();
        setupSelectBoxes();
    }

    function updateTotalEmail(start, end) {
        var total = '<span style="color:blue">' + totalEmail+ '</span>' + ' エントリーの ' + '<span style="color:blue">' + (start+1)+ '</span>' +' から' +'<span style="color:blue">' + end+ '</span>'+ ' を表示しています';
        $('#'+totalEmailId).html(total);
    }

    function updatePageActive(){
        if(listEmailInbox && listEmailInbox.length>0){
            $('#'+paginationInboxId).css('visibility', 'visible');
            $('#'+paginationInboxId).twbsPagination({
                totalPages: totalPages,
                visiblePages: 5,
                startPage: currentPage+1,
                next: '次へ',
                prev: '前へ',
                first: '最初',
                last: '最終',
                onPageClick: function (event, page) {
                    //fetch content and render here
                    if(flagCheckReload){
                        flagCheckReload = false;
                    }else{
                        loadEmailData(page-1)
                    }
                }
            });
        }else{
            $('#'+paginationInboxId).css('visibility', 'hidden');
        }
    }

    function setupSelectBoxes() {
        var replyAllEmail = $("input[name=replyEmail]").length == $("input[name=replyEmail]:checked").length? true: false;
        $(cbReplyAllEmailId).prop("checked", replyAllEmail);

        $(cbReplyAllEmailId).off('click');
        $(cbReplyAllEmailId).click(function () {
            if($(this).is(':checked')){
                $("input[name=replyEmail]").prop("checked", true);
            }else{
                $("input[name=replyEmail]").prop("checked", false);
            }
        });

        $('input[name=replyEmail]').off('click');
        $('input[name=replyEmail]').click(function(){
            if($("input[name=replyEmail]").length == $("input[name=replyEmail]:checked").length) {
                $(cbReplyAllEmailId).prop("checked", true);
            } else {
                $(cbReplyAllEmailId).prop("checked", false);
            }
        });
    }

    function getEmailSelected() {
        var listMailIdSelected=[];
        $("input[name=replyEmail]").each(function( index ) {
            if($(this).is(':checked')){
                var msgId = $(this).attr("value");
                listMailIdSelected.push(msgId);
            }
        });
        return listMailIdSelected;
    }

    function addRowWithData(data, index) {
        var table = document.getElementById(inboxTableId);
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
                        cellNode.textContent = cellData;
                    }
                }

                if (cellNode.nodeName == "I") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if(cellKeys[0] && cellKeys[0] == 'status'){
                        if(cellData==1){
                            cellNode.className = 'fa fa-circle-o';
                        }else{
                            cellNode.className = 'fa fa-times';
                        }
                    }else{
                        if (cellData) {
                            cellNode.className = 'fa fa-paperclip';
                        }
                    }
                }

                if (cellNode.nodeName == "INPUT") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    cellNode.name = 'replyEmail';
                    cellNode.value = cellData;
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

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function numberValidator(value, rule) {
        if (!value || value.trim().length === 0) {
            return "Value can not be empty!";
        } else if (rule.operator.type !== 'in') {
            value = fullWidthNumConvert(value);
            value = value.replace(/，/g, ",");
            var pattern = /^\d+(,\d{3})*(\.\d+)?$/;
            var match = pattern.test(value);
            if(!match){
                return "Value must be a number greater than or equal to 0";
            }
        }
        return true;
    }

    function showSettingCondition() {
        showModal(loadEmailData);
    }

    function showModal(callback) {
        $('#dataModal').modal();
        var conditionBefore = getBeforeFilterCondition();
        replaceCondition(conditionBefore);
        $('#'+inboxBuilderId).queryBuilder('setRules', conditionBefore);
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var word = $( '#word').val();
            var wordExclusion = $( '#wordExclusion').val();
            if(typeof callback === "function"){
                var condition = $('#'+inboxBuilderId).queryBuilder('getRules');
                if(condition != null){
                    saveFilterCondition(condition);
                    location.reload();
                    $('#dataModal').modal('hide');
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

    function getBeforeFilterCondition() {
        var condition = sessionStorage.getItem(filterConditionKey);
        if(condition && condition!=null ){
            return JSON.parse(condition);
        }
        return default_filter_condition;
    }

    function saveFilterCondition(condition) {
        sessionStorage.setItem(filterConditionKey, JSON.stringify(condition));
    }

    function settingPageSize(){
        pageSize = getPageSize();
        $(selectPageSizeId).val(pageSize);

        $(selectPageSizeId).off('change');
        $(selectPageSizeId).change(function() {
            savePageSize($(selectPageSizeId).val());
            location.reload();
        });
    }

    function getPageSize() {
        var pageSize = sessionStorage.getItem(pageSizeKey);
        if(pageSize && pageSize!=null){
            return pageSize;
        }
        return default_page_size;
    }

    function savePageSize(pageSize) {
        sessionStorage.setItem(pageSizeKey, pageSize);
    }

    function updateHistoryDataTrigger() {
        $("#" + inboxTableId).trigger("updateAll", [ true, function () {

        } ]);
    }

    function showEmailDetailSelected() {
        var row = $(this)[0].parentNode;
        var index = row.getAttribute("data");
        var rowData = listEmailInbox[index];
        showMailContent(rowData);
    }

    function selectFirstRow() {
        if (listEmailInbox && listEmailInbox.length > 0) {
            var firstTr = $('#' + inboxTableId).find(' tbody tr:eq('+1+')');
            firstTr.addClass('highlight-selected').siblings().removeClass('highlight-selected');
            var rowData = listEmailInbox[0];
            showMailContent(rowData);
        }
    }

    function selectedRow() {
        $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
        showEmailDetailSelected.call(this);
    }

    function showMailContent(data) {
        if(data){
            if(data.hasAttachment){
                showFileAttachEmailInbox(data.messageId, function (files) {
                    showMailContentDetail(data, files);
                });
            }else{
                showMailContentDetail(data);
            }
        }
    }

    function showMailContentDetail(data, files) {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({body: ""});
        mailAttachmentDiv.innerHTML = "";
        if (data) {
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
                '<h6>送信者:&nbsp;' + data.from + '&nbsp;&nbsp;&nbsp;&nbsp;受信者:&nbsp;' + data.to + '<span class="mailbox-read-time pull-right">' + data.sentAt + '</span></h6>' +
                '</div>';
            showMailBodyContent(data);
            showFileAttach(mailAttachmentDiv, files, "show");
        }
    }

    function showFileAttach(divFileAttachId, files, type){
        if(files && files.length > 0){
            var filesInnerHTML = "";
            for(var i = 0; i < files.length; i++ ){
                var file = files[i];
                var fileExtension = getFileExtension(file.fileName);
                var command = extensionCommands[fileExtension];
                command = (isWindows() && !!command) ? command : "nope";
                var url = window.location.origin + "/download/" + encodeURIComponent(file.digest) + "/" + file.fileName;
                if(i > 0){
                    filesInnerHTML += "<br/>";
                }
                filesInnerHTML += ("<button type='button' class='btn btn-link download-link' data-id='"+file.id+"' data-filename='" + file.fileName + "' data-command='" + command + "' data-download='" + url + "'>" + file.fileName + "(" + getFileSizeString(file.size) + ") </button>");
                if(type == "reSend"){
                    filesInnerHTML += "<span class='remove-mail-attachment'>&nbsp;x&nbsp;</span>";
                }
            }
            divFileAttachId.innerHTML = filesInnerHTML;
            setDownloadLinkClickListener();
        } else {
            divFileAttachId.innerHTML = "添付ファイルなし";
        }
    }

    function sendMailOnclick() {
        var listMailIdSelected = getEmailSelected();
        if(listMailIdSelected.length<=0){
            $.alert("最初にメールを選択してください");
            return;
        }
        var listEmailSelected = [];
        for (var i = 0; i < listEmailInbox.length; i++) {
            for (var j = 0; j < listMailIdSelected.length; j++) {
                if (listEmailInbox[i].messageId == listMailIdSelected[j]) {
                    listEmailSelected.push(listEmailInbox[i]);
                }
            }
        }

        showPopupTyping(listEmailSelected);
    }

    function showMailBodyContent(data) {
        data.body = data.body.replace(/(?:\r\n|\r|\n)/g, '<br />');
        var mailBodyDiv = document.getElementById(mailBodyDivId);
        mailBodyDiv.innerHTML = data.body;
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

    function showPopupTyping(listEmailSelected) {
        $('#sendMailModal').modal();
        clearPopupTyping();
        var accountId = listEmailSelected[0].accountId;
        for(var i=0;i<listEmailSelected.length-1;i++){
            if(listEmailSelected[i].accountId != listEmailSelected[i+1].accountId){
                accountId = -1;
            }
        }
        initChooseAccount();
        getMailAccounts(function (accounts) {
            if(accountId == -1){
                showDefaultAccount();
                updateSenderSelector(lastSelectedSendMailAccountId, accounts);
            }else{
                showSelectAccount();
                updateSenderSelector(accountId, accounts);
            }
        });

        $("button[name='sendSuggestMailClose']").off('click');
        $('#cancelSendSuggestMail').button('reset');
        $("button[name='sendSuggestMailClose']").click(function () {
            var btn = $('#cancelSendSuggestMail');
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            if (attachmentData.upload.length == 0) {
                btn.button('reset');
                $('#sendMailModal').modal('hide');
                return;
            }
            var form = {
                uploadAttachment: attachmentData.upload,
            };
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/removeUploadedFiles",
                data: JSON.stringify(form),
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {
                    btn.button('reset');
                    $('#sendMailModal').modal('hide');

                },
                error: function (e) {
                    console.error("ERROR : sendSuggestMail: ", e);
                    btn.button('reset');
                    $('#sendMailModal').modal('hide');
                }
            });
        });
        $('#sendSuggestMail').off('click');
        $('#sendSuggestMail').button('reset');
        $("#sendSuggestMail").click(function () {
            var btn = $(this);
            var content = getMailEditorContent();
            if(!content || content==null || content.trim()==""){
                $.alert("内容を入力してください。");
                return;
            }
            $.confirm({
                title: '',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">メールを本当に送信しますか？<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            var listId = [];
                            for(var i=0;i<listEmailSelected.length;i++){
                                listId.push(listEmailSelected[i].messageId);
                            }
                            var attachmentData = getAttachmentData(attachmentDropzone);
                            var accountIdSend = -1;
                            if($('input[name=chooseUser]:checked').val()==1){
                                accountIdSend = $('#' + rdMailSenderId).val();
                            }
                            var form = {
                                listId: listId,
                                accountId: accountIdSend,
                                content: content,
                                originAttachment: attachmentData.origin,
                                uploadAttachment: attachmentData.upload,
                                sendType: 7,
                                historyType: 7,
                            };

                            $('body').loadingModal({
                                position: 'auto',
                                text: '送信中...',
                                color: '#fff',
                                opacity: '0.7',
                                backgroundColor: 'rgb(0,0,0)',
                                animation: 'doubleBounce',
                            });
                            $.ajax({
                                type: "POST",
                                contentType: "application/json",
                                url: "/user/sendReplyRecommendationMail",
                                data: JSON.stringify(form),
                                dataType: 'json',
                                cache: false,
                                timeout: 600000,
                                success: function (data) {
                                    hideloading();
                                    if (data && data.status) {
                                        $.alert({
                                            title: "",
                                            content: "メールの送信に成功しました。",
                                            onClose: function () {
                                                $('#sendMailModal').modal('hide');
                                                loadEmailData(currentPage);
                                            }
                                        });
                                    } else {
                                        hideloading();
                                        $.alert("メールの送信に失敗しました。");
                                    }

                                },
                                error: function (e) {
                                    hideloading();
                                    console.error("ERROR : sendSuggestMail: ", e);
                                    $.alert("メールの送信に失敗しました。");
                                    $('#sendMailModal').modal('hide');
                                    //TODO: noti send mail error
                                }
                            });
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        })
    }

    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function setDownloadLinkClickListener() {
        $('button.download-link').off("click");
        $('button.download-link').click(function(event) {
            var command = $(this).attr("data-command");
            var href = $(this).attr("data-download");
            var fileName = $(this).attr('data-filename');
            if(command.startsWith("nope")) {
                alert("Not support features");
            } else {
                doDownload(command+href, fileName);
            }
        });

        $.contextMenu({
            selector: 'button.download-link',
            callback: function(key, options) {
                var m = "clicked: " + key;
                var command = $(this).attr("data-command");
                var href = $(this).attr("data-download");
                var fileName = $(this).attr('data-filename');
                if(key === "open") {
                    if(command.startsWith("nope")) {
                        alert("Not support features");
                    } else {
                        doDownload(command+href, fileName);
                    }
                } else if (key === "save_as") {
                    doDownload(href, fileName);
                }
            },
            items: {
                "open": {"name": "Open"},
                "save_as": {"name": "Save as"},
            }
        });
    }


    function doDownload(href, fileName){
        var a = document.createElement('A');
        a.href = href;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

    function clearPopupTyping() {
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent("");
        editor.undoManager.clear();
        updateDropzoneData(attachmentDropzone);
    }

    function hideloading() {
        $('body').loadingModal('hide');
        $('body').loadingModal('destroy');
    }

    function getMailAccounts(callback){
        function onSuccess(response) {
            if(response && response.status) {
                if(typeof callback == 'function'){
                    callback(response.list);
                }
            }
        }
        function onError() {
            alert('メール送信アカウント情報取得が失敗しました');
        }

        getMailAccountsAPI(onSuccess, onError);
    }

    function updateSenderSelector(accountId, accounts) {
        accounts = accounts || [];
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id.toString() == accountId)
            }));
        });
    }
    
    function initChooseAccount() {
        $('input[name=chooseUser]').change(function() {
            if (this.value == '1') {
                $('#'+rdMailSenderId).prop('disabled', false);
            }else{
                $('#'+rdMailSenderId).prop('disabled', true);
            }
        });
    }

    function showSelectAccount() {
        $("input[name=chooseUser][value='1']").prop("checked",true);
        $('#'+rdMailSenderId).prop('disabled', false);
    }

    function showDefaultAccount() {
        $("input[name=chooseUser][value='2']").prop("checked",true);
        $('#'+rdMailSenderId).prop('disabled', true);
    }

    function replaceCondition(rule) {
        var rules = rule.rules;
        if(rules){
            for(var i=rules.length-1;i>=0;i--){
                if(rules[i].id){
                    for(var j=0;j<ruleInvalidateIds.length;j++){
                        if(rules[i].id == ruleInvalidateIds[j]){
                            rules.splice(i, 1);
                            break;
                        }
                    }
                }else{
                    replaceCondition(rules[i]);
                }
            }
        }
    }

})(jQuery);