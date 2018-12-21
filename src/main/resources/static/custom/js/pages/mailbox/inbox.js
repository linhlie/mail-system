(function () {
    "use strict";
    var inboxTableId = 'inboxTable';
    var totalEmailId = 'totalEmail';
    var paginationInboxId = 'paginationInbox';
    var inboxBuilderId = 'inbox-builder';
    var btnFilterId = "#btnFilter";

    var listEmailInbox = null;
    var totalEmail = null;
    var start = null;
    var end = null;
    var totalPages = null;
    var currentPage = null;
    var flagCheckReload = true;

    var filterConditionKey = 'filterConditionInboxEmail';
    var filterCondition = null;

    var default_filter_condition = {
        "condition": "AND",
        "rules": [
        ],
        "valid": true
    };

    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var historyQuickFilterId = 'historyQuickFilter';
    var fromDateId = 'historyFromDate';
    var toDateId = 'historyToDate';
    var historySearchBtnId = 'historySearchBtn';

    var lastSelectedSendMailAccountId;
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailCCId = 'rdMailCC';
    var attachmentDropzoneId = "#reply-dropzone";
    var attachmentDropzone;

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";
    var reSendEmail;

    var replaceBody = '<tr role="row" class="hidden">' +
        '<td class="clickable tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="to"><span></span></td>' +
        '<td class="clickable tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="hasAttachment"><i></i></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="status"><i></i></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="relativeDate"><span></span></td>' +
        '<td class="clickable text-center tableInbox" name="showEmailInbox" rowspan="1" colspan="1" data="mark"><span></span></td>' +
        '<td class="text-center" rowspan="1" colspan="1">' +
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
            id: '4',
            label: '数値',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidator
            },
        }, {
            id: '5',
            label: '数値(上代)',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidator
            },
        }, {
            id: '6',
            label: '数値(下代)',
            type: 'string',
            operators: ['equal', 'not_equal', 'greater_or_equal', 'greater', 'less_or_equal', 'less', 'in'],
            validation: {
                callback: numberValidator
            },
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
        }];

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

        initTagsInput();
        initDropzone();
        initStickyHeader();
    });

    function initTagsInput() {
        $('#' + rdMailCCId).tagsInput({
            defaultText: '',
            minInputWidth: 150,
            maxInputWidth: 600,
            width: 'auto',
            pattern: /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
            validationMsg: 'メールアドレスを入力してください',
            onChange: function (elem, elem_tags) {
                $('.tag').each(function () {
                    var tag = $(this).text();
                    var email = tag.substring(0, tag.length - 3);
                    var globalMailDoman = getEmailDomain(senderGlobal);
                    var emailDomain = getEmailDomain(email);
                    if (globalMailDoman == emailDomain) {
                        $(this).css('background-color', 'yellow');
                    } else if(externalCCGlobal.indexOf(email) >= 0) {
                        $(this).css('background-color', 'yellow');
                    }
                });
            }
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

    function loadEmailData(page) {
        filterCondition = getBeforeFilterCondition();
        $('body').loadingModal({
            position: 'auto',
            text: '抽出中...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        function onSuccess(response) {
            $('body').loadingModal('hide');
            if (response && response.status) {
                var data  = response.list[0];
                console.log(data);
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
            $('body').loadingModal('hide');
        }
        filterInbox({filterRule: filterCondition, page: page,}, onSuccess, onError);
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
    }

    function updateTotalEmail(start, end) {
        var total = "Showing " + (start+1) + " to " + end + " of " + totalEmail + " entries";
        $('#'+totalEmailId).text(total);
    }

    function updatePageActive(){
        if(listEmailInbox && listEmailInbox.length>0){
            $('#'+paginationInboxId).css('visibility', 'visible');
            $('#'+paginationInboxId).twbsPagination({
                totalPages: totalPages,
                visiblePages: 5,
                startPage: currentPage+1,
                next: 'Next',
                prev: 'Prev',
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
                    cellNode.className = 'replyEmail';
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
        $('#'+inboxBuilderId).queryBuilder('setRules', conditionBefore);
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var word = $( '#word').val();
            var wordExclusion = $( '#wordExclusion').val();
            if(typeof callback === "function"){
                var condition = $('#'+inboxBuilderId).queryBuilder('getRules');
                console.log(condition);
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
        if(condition){
            return JSON.parse(condition);
        }
        return default_filter_condition;
    }

    function saveFilterCondition(condition) {
        sessionStorage.setItem(filterConditionKey, JSON.stringify(condition));
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
            console.log(firstTr);
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
                    console.log(files);
                    showMailContentDetail(data, files);
                });
            }else{
                showMailContentDetail(data);
            }
        }
    }

    function showMailContentDetail(data, files) {
        console.log(data);
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

    function resetMailContentDetail() {
        var mailSubjectDiv = document.getElementById(mailSubjectDivId);
        var mailAttachmentDiv = document.getElementById(mailAttachmentDivId);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent({body: ""});
        mailAttachmentDiv.innerHTML = "";
    }

    function showFileAttach(divFileAttachId, files, type){
        console.log(files);
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
            removeAttachOriginListener()
        } else {
            divFileAttachId.innerHTML = "添付ファイルなし";
        }
    }

    function removeAttachOriginListener() {
        $(".remove-mail-attachment").off('click');
        $(".remove-mail-attachment").click(function () {
            var fileAttachmentBtn = $(this).prev();
            if(fileAttachmentBtn){
                var brTag = fileAttachmentBtn.prev();
                if(brTag.is("br")){
                    brTag.remove();
                }else{
                    $(this).next().remove();
                }
                fileAttachmentBtn.remove();
                $(this).remove();
            }
        })
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

    function showMailEditor(id) {
        var cachedSeparateTab = getCachedSeparateTabSetting();
        if(cachedSeparateTab) {
            showMailEditorInNewTab(id);
        } else {
            showMailEditorInTab(id);
        }
    }

    function showMailEditorInNewTab(id) {
        var data = {
            "id" : id
        };
        sessionStorage.setItem("separateReSendMailData", JSON.stringify(data));
        var win = window.open('/user/reSendNewTab', '_blank');
        if (win) {
            win.focus();
        } else {
            alert('Please allow popups for this website');
        }
    }

    function showMailEditorInTab(id) {
        $('#sendMailModal').modal();
        lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
        getDetailMailHistory(id, function (email, accounts, files) {
            reSendEmail = email;
            updateSenderSelector(email, accounts, function (account) {
                showMailContentToEditor(email, accounts, files);
                $('#' + rdMailSenderId).off('change');
                $('#' + rdMailSenderId).change(function() {
                    showMailContentToEditor(email, accounts, files);
                });
            });
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
            receiverValidate = validateAndShowEmailListInput(rdMailReceiverId, false);
            ccValidate = validateAndShowEmailListInput(rdMailCCId, true);
            if(!(receiverValidate && ccValidate)) return;
            if(!reSendEmail) return;
            var btn = $(this);
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            var filesAttachOrigin = getFileAtachOrigin();
            for(var i=0;i<filesAttachOrigin.length;i++){
                attachmentData.upload.push(filesAttachOrigin[i]);
            }
            var form = {
                messageId: reSendEmail.messageId,
                subject: $("#" + rdMailSubjectId).val(),
                receiver: $("#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $("#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
                accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
                sendType: reSendEmail.sendType,
                historyType: 11,
            };
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/user/sendRecommendationMail",
                data: JSON.stringify(form),
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {
                    btn.button('reset');
                    $('#sendMailModal').modal('hide');
                    if (data && data.status) {
                        //TODO: noti send mail success
                        var payload = getSearchPayload();
                        loadHistoryData(payload);
                        resetMailContentDetail();
                    } else {
                        //TODO: noti send mail failed
                    }

                },
                error: function (e) {
                    btn.button('reset');
                    console.error("ERROR : sendSuggestMail: ", e);
                    $('#sendMailModal').modal('hide');
                    //TODO: noti send mail error
                }
            });
        })
    }

    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function updateSenderSelector(email, accounts, callback) {
        accounts = accounts || [];
        var flagAccount = false;
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            if(item.account == email.from){
                flagAccount = true;
                lastSelectedSendMailAccountId = item.id;
            }
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.account.toString() === email.from)
            }));
        });

        if(!flagAccount){
            $('#' + rdMailSenderId).empty();
            $.each(accounts, function (i, item) {
                $('#' + rdMailSenderId).append($('<option>', {
                    value: item.id,
                    text : item.account,
                    selected: (item.id.toString() === lastSelectedSendMailAccountId)
                }));
            });
        }
        if (typeof callback === "function") {
            callback();
        }
    }

    function resetValidation() {
        receiverValidate = true;
        ccValidate = true;
        $('#' + rdMailCCId + '-container').removeClass('has-error')
        $('#' + rdMailReceiverId + '-container').removeClass('has-error')
    }

    function showMailContentToEditor(data, accounts, files) {
        var receiverListStr = data.to;
        resetValidation();
        lastSelectedSendMailAccountId = $('#' + rdMailSenderId).val();
        var account;
        for(var i=0;i<accounts.length;i++){
            if(accounts[i].id == lastSelectedSendMailAccountId){
                account = accounts[i];
            }
        }
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        if (data && account) {
            senderGlobal = account.account;
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = account.cc ? account.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,externalCC);
            $('#' + rdMailCCId).importTags(cc.join(","));

            document.getElementById(rdMailSubjectId).value = data.subject;
            updateMailEditorContent(data.body);
            var rdMailAttachment = document.getElementById(rdMailAttachmentId);
            showFileAttach(rdMailAttachment, files, "reSend");
        }
        updateDropzoneData(attachmentDropzone);
    }


    function updateMailEditorContent(content, preventClear) {
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent(content);
        if (!preventClear) {
            editor.undoManager.clear();
        }
        editor.undoManager.add();
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

    function getFileAtachOrigin() {
        var files = [];
        $('#rdMailAttachment button').each(function(){
            var id = $(this).attr('data-id');
            files.push(id);
        });
        return files;
    }

})(jQuery);