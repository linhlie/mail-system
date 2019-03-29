
(function () {
    "use strict";
    var sourceTableId = 'sourceMatch';
    var destinationTableId = 'destinationMatch';
    var mailSubjectDivId = 'mailSubject';
    var mailBodyDivId = 'mailBody';
    var mailAttachmentDivId = 'mailAttachment';
    var sakiPreviewContainerId = 'saki-preview-container';
    var mailSakiSubjectDivId = 'mailSakiSubject';
    var mailSakiBodyDivId = 'mailSakiBody';
    var mailSakiAttachmentDivId = 'mailSakiAttachment';
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBody';
    var rdMailSenderId = 'rdMailSender';
    var rdMailReceiverId = 'rdMailReceiver';
    var rdMailCCId = 'rdMailCC';
    var printBtnId = 'printBtn';
    var printElementId = 'printElement';
    var printSakiBtnId = 'printSakiBtn';
    var printSakiElementId = 'printSakiElement';
    var matchingResult = null;
    var mailList = {};
    var currentDestinationResult = [];
    var onlyDisplayNonZeroRow = true;
    var destinationMatchDataTable;
    var selectedRowData;
    var motoReplaceSelectorId = "#motoReplaceSelector";
    var sakiReplaceSelectorId = "#sakiReplaceSelector";
    var totalSourceMatchingContainId = "totalSourceMatching";
    var totalDestinationMatchingContainId = "totalDestinationMatching";

    var attachmentDropzoneId = "#attachment-dropzone";
    var attachmentDropzone;

    var selectedSourceTableRow;
    var selectedDestinationTableRow;

    var sourceFirstBtnId = "source-first";
    var sourceLastBtnId = "source-last";
    var sourcePrevBtnId = "source-prev";
    var sourceNextBtnId = "source-next";
    var desFirstBtnId = "des-first";
    var desLastBtnId = "des-last";
    var desPrevBtnId = "des-prev";
    var desNextBtnId = "des-next";

    var originalContentWrapId = "ows-mail-body";

    var isDebug = true;
    var debugMailAddress = "ows-test@world-link-system.com";

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";
    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
    var lastReceiver;
    var lastMessageId;
    var lastTextRange;
    var lastTextMatchRange;
    var lastReplaceType;
    var lastSendTo;

    var dataLinesConfirm;

    var standardEscape = ["+", "?", "(", ")", "{", "}", "|"];

    var spaceEffective = false;
    var distinguish = false;

    var RULE_NUMBER_ID = 4;
    var RULE_NUMBER_UP_RATE_ID = 5;
    var RULE_NUMBER_DOWN_RATE_ID = 6;

    var ruleNumberId = "ruleNumber";
    var ruleNumberUpRateId = "ruleNumberUpRate";
    var ruleNumberDownRateId = "ruleNumberDownRate";

    var ruleNumberDownRateName = "";
    var ruleNumberUpRateName = "";
    var ruleNumberName = "";

    var markOptions = {
        "element": "mark",
        "separateWordSearch": false,
        "acrossElements": false,
    };

    var invisibleMarkOptions = {
        "element": "mark",
        "className": "mark-invisible",
        "separateWordSearch": false,
        "acrossElements": true,
    };

    var rangeMarkOptions = {
        "element": "mark",
        "className": "mark-range",
        "separateWordSearch": false,
        "acrossElements": true,
    };

    var markSearchOptions = {
        "element": "mark",
        "className": "mark-search",
        "separateWordSearch": false,
        "acrossElements": true,
    };

    var headerOriginSource = '<tr>'+
        '<th class="col-sm-1" >ワード</th>'+
        '<th class="col-sm-1" >マッチ件数</th>'+
        '<th class="col-sm-2" >受信日時</th>'+
        '<th class="col-sm-2" >送信者</th>'+
        '<th class="col-sm-6" >件名</th>';
        // </tr>

    var headerAlertPartner = '<th class="col-sm-1" style="color: red">取引先アラート</th>';
    var headerAlertPeople = '<th class="col-sm-1" style="color: red">担当アラート</th>';

    var replaceSourceHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="word"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="destinationList"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="source.receivedAt"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="source.from"><span></span></td>' +
        '<td class="clickable" name="sourceRow" rowspan="1" colspan="1" data="source.subject"><span></span></td>';
        // '</tr>';

    var replaceSourceAlertPartner = '<td name="alertLevelSourceRow" rowspan="1" colspan="1" data="source.alertLevel" style="text-align: center;">' +
        '<span style="display: inline-block;"></span></td>';

    var replaceSourceAlertPeople = '<td name="alertLevelPeopleInChargeSource" rowspan="1" colspan="1" data="source.peopleInChargeAlertLevel" style="text-align: center;">' +
        '<span style="display: inline-block;"></span></td>' ;

    var headerDestination = '<tr>'+
        '<th class="col-sm-2">ワード</th>'+
        '<th class="col-sm-2">元数値</th>'+
        '<th class="col-sm-2">先数値</th>'+
        '<th class="col-sm-4">受信日時</th>'+
        '<th class="col-sm-4">送信者</th>'+
        '<th class="col-sm-10">件名</th>'+
        '<th class="col-sm-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>'+
        '<th class="col-sm-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>';

    var replaceDestinationHTML = '<tr role="row" class="hidden">' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="word"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="range"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="matchRange"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="receivedAt"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="from"><span></span></td>' +
        '<td class="clickable" name="showDestinationMail" rowspan="1" colspan="1" data="subject"><span></span></td>' +
        '<td class="clickable text-center" name="sendToMoto" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">元へ</button>' +
        '</td>' +
        '<td class="clickable text-center" name="sendToSaki" rowspan="1" colspan="1">' +
        '<button type="button" class="btn btn-xs btn-default">先へ</button>' +
        '</td>';

    var replaceDestinationAlertPartner = '<td name="alertLevelDestinationMail" rowspan="1" colspan="1" data="alertLevel" style="text-align: center;">' +
        '<span style="display: inline-block;"></span></td>' ;

    var replaceDestinationAlertPeople = '<td name="alertLevelPeopleInCharge" rowspan="1" colspan="1" data="peopleInChargeAlertLevel" style="text-align: center;">' +
        '<span style="display: inline-block;"></span></td>';

    $(function () {
        ruleNumberDownRateName = $('#'+ruleNumberDownRateId).text();
        ruleNumberUpRateName = $('#'+ruleNumberUpRateId).text();
        ruleNumberName = $('#'+ruleNumberId).text();

        previewDraggingSetup();
        previewDraggingSetup2();
        initSearch(mailBodyDivId, "moto");
        initSearch(mailSakiBodyDivId, "saki");
        initReplaceSelector();
        initDropzone();
        initSortDestination();
        setButtonClickListenter(printBtnId, function () {
            printPreviewEmail(printElementId);
        });
        setButtonClickListenter(printSakiBtnId, function () {
            printPreviewEmail(printSakiElementId);
        });
        setButtonClickListenter(sourceFirstBtnId, sourceFirst);
        setButtonClickListenter(sourceLastBtnId, sourceLast);
        setButtonClickListenter(sourcePrevBtnId, sourcePrev);
        setButtonClickListenter(sourceNextBtnId, sourceNext);
        setButtonClickListenter(desFirstBtnId, destinationFirst);
        setButtonClickListenter(desLastBtnId, destinationLast);
        setButtonClickListenter(desPrevBtnId, destinationPrev);
        setButtonClickListenter(desNextBtnId, destinationNext);

        getEnvSettings();
        fixingForTinyMCEOnModal();
        var matchingConditionStr;
        matchingConditionStr = sessionStorage.getItem("matchingConditionData");
        if(matchingConditionStr){
            var matchingConditionJson = JSON.parse(matchingConditionStr);
            if(matchingConditionJson && matchingConditionJson.matchingConditionData){
                replaceCondition(matchingConditionJson.matchingConditionData);
            }
            matchingConditionStr = JSON.stringify(matchingConditionJson);
        }
        var spaceEffectiveStr = sessionStorage.getItem("spaceEffective");
        var distinguishStr = sessionStorage.getItem("distinguish");
        spaceEffective = spaceEffectiveStr ? !!JSON.parse(spaceEffectiveStr) : false;
        distinguish = distinguishStr ? !!JSON.parse(distinguishStr) : false;
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
        if(matchingConditionStr){
            $('body').loadingModal({
                position: 'auto',
                text: 'マッチング中...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });

            function onSuccess(response) {
                $('body').loadingModal('hide');
                if(response && response.status){
                    matchingResult = response.list;
                    mailList = response.mailList || {};
                } else {
                    console.error("[ERROR] submit failed: ");
                }
                updateData();
            }

            function onError(error) {
                console.error("[ERROR] submit error: ", error);
                $('body').loadingModal('hide');
                updateData();
            }

            getEmailMatchingResult(matchingConditionStr, onSuccess, onError);

        } else {
            updateData();
        }
        initStickyHeader();
        $(document).on("keyup", keydownHandler);
    });

    function keydownHandler(e) {
        var button = undefined;
        if(e.shiftKey && (e.which || e.keyCode) == 113) {
            e.preventDefault();
            button = $("#" + sourceFirstBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 115) {
            e.preventDefault();
            button = $("#" + sourceLastBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 113) {
            e.preventDefault();
            button = $("#" + sourcePrevBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 115) {
            e.preventDefault();
            button = $("#" + sourceNextBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 119) {
            e.preventDefault();
            button = $("#" + desFirstBtnId);
        } else if(e.shiftKey && (e.which || e.keyCode) == 120) {
            e.preventDefault();
            button = $("#" + desLastBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 119) {
            e.preventDefault();
            button = $("#" + desPrevBtnId);
        } else if(!e.shiftKey && (e.which || e.keyCode) == 120) {
            e.preventDefault();
            button = $("#" + desNextBtnId);
        }
        if(button && !button.is(":disabled")) {
            button.click();
        }
    }

    function initReplaceSelector() {
        var motoSelectedValue = localStorage.getItem("motoSelectedValue");
        var sakiSelectedValue = localStorage.getItem("sakiSelectedValue");

        $(motoReplaceSelectorId).empty();
        $(motoReplaceSelectorId).append('<option value="3">元の数値</option>');
        if(ruleNumberUpRateName){
            $(motoReplaceSelectorId).append('<option value="5">元の上代</option>');
        }
        if(ruleNumberDownRateName){
            $(motoReplaceSelectorId).append('<option value="4">元の下代</option>');
        }
        $(motoReplaceSelectorId).append('<option value="0">先の数値</option>');
        if(ruleNumberUpRateName){
            $(motoReplaceSelectorId).append('<option value="2">先の上代</option>');
        }
        if(ruleNumberDownRateName){
            $(motoReplaceSelectorId).append('<option value="1">先の下代</option>');
        }

        $(sakiReplaceSelectorId).empty();
        $(sakiReplaceSelectorId).append('<option value="0">元の数値</option>');
        if(ruleNumberUpRateName){
            $(sakiReplaceSelectorId).append('<option value="2">元の上代</option>');
        }
        if(ruleNumberDownRateName){
            $(sakiReplaceSelectorId).append('<option value="1">元の下代</option>');
        }
        $(sakiReplaceSelectorId).append('<option value="3">先の数値</option>');
        if(ruleNumberUpRateName){
            $(sakiReplaceSelectorId).append('<option value="5">先の上代</option>');
        }
        if(ruleNumberDownRateName){
            $(sakiReplaceSelectorId).append('<option value="4">先の下代</option>');
        }

        $(motoReplaceSelectorId).val(3);
        $(sakiReplaceSelectorId).val(0);

        if(!!motoSelectedValue) {
            if(motoSelectedValue%3==2 &&  ruleNumberUpRateName){
                $(motoReplaceSelectorId).val(motoSelectedValue);
            }
            if(motoSelectedValue%3==1 &&  ruleNumberDownRateName){
                $(motoReplaceSelectorId).val(motoSelectedValue);
            }
        }
        if(!!sakiSelectedValue) {
            if(sakiSelectedValue%3==2 &&  ruleNumberUpRateName){
                $(sakiReplaceSelectorId).val(sakiSelectedValue);
            }
            if(sakiSelectedValue%3==1 &&  ruleNumberDownRateName){
                $(sakiReplaceSelectorId).val(sakiSelectedValue);
            }
        }

        $(motoReplaceSelectorId).change(function() {
            localStorage.setItem("motoSelectedValue", $(motoReplaceSelectorId).val());
        });
        $(sakiReplaceSelectorId).change(function() {
            localStorage.setItem("sakiSelectedValue", $(sakiReplaceSelectorId).val());
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
            init: function() {
                this.on("success", function(file, response) {
                    if(response && response.status){
                        var data = response.list && response.list.length > 0 ? response.list[0] : null;
                        if(data){
                            file.id = data.id;
                        }
                    }
                });
                this.on("removedfile", function(file) {
                    if(!!file && !!file.upload && !!file.id){
                        removeFile(file.id)
                    }
                });
            },
            thumbnail: function(file, dataUrl) {}
        })
    }
    
    function enableResizeSourceColumns() {
        enableResizeColums(sourceTableId);
    }

    function enableResizeDestinationColumns() {
        enableResizeColums(destinationTableId);
    }

    function enableResizeColums(tableId) {
        $("#" + tableId).colResizable(
            {
                disable: true,
            }
        );
        if(screen.width > 768){
            $("#" + tableId).colResizable(
                {
                    resizeMode:'overflow',
                    minWidth: 30
                }
            );
        }else{
            $("#" + tableId).colResizable(
                {
                    minWidth: 30
                }
            );
        }
    }

    function setButtonClickListenter(id, callback) {
        $('#' + id).off('click');
        $('#' + id).click(function () {
            if(typeof callback === "function"){
                callback();
            }
        });
    }

    function fixingForTinyMCEOnModal() {
        $(document).on('focusin', function(e) {
            if ($(e.target).closest(".mce-window").length) {
                e.stopImmediatePropagation();
            }
        });
    }
    
    function updateData() {
        var replaceBody = replaceSourceHTML;
        var replaceHeader = headerOriginSource;
        var isAlertpartner = false;
        var isAlertpeople = false;

        if(matchingResult.length > 0){
            for(var i = 0; i < matchingResult.length; i ++){
                if(onlyDisplayNonZeroRow && matchingResult[i]
                    && matchingResult[i].destinationList && matchingResult[i].destinationList.length == 0){
                    continue;
                }
                var messageId = matchingResult[i].source.messageId;
                var data = Object.assign(matchingResult[i].source, mailList[messageId]);
                if(data.alertLevel != null && data.alertLevel != ""){
                    isAlertpartner = true;
                }
                if(data.peopleInChargeAlertLevel != null && data.peopleInChargeAlertLevel !=""){
                    isAlertpeople = true;
                }
            }
        }

        if(isAlertpartner){
            replaceBody = replaceBody + replaceSourceAlertPartner;
            replaceHeader = replaceHeader + headerAlertPartner;
        }

        if(isAlertpeople){
            replaceBody = replaceBody + replaceSourceAlertPeople;
            replaceHeader = replaceHeader + headerAlertPeople;
        }
        replaceBody = replaceBody + '</tr>';
        replaceHeader = replaceHeader + '</tr>';
        showSourceData(sourceTableId, matchingResult, replaceHeader, replaceBody);
    }

    function showSourceData(tableId, data, replaceHeader, replaceBody) {
        $("#" + tableId + "> thead").html(replaceHeader);
        removeAllRow(tableId, replaceBody);
        var sourceMatchingCounter = 0;
        if(data.length > 0){
            var html = replaceBody;
            for(var i = 0; i < data.length; i ++){
                if(onlyDisplayNonZeroRow && data[i]
                    && data[i].destinationList && data[i].destinationList.length == 0){
                    continue;
                }
                var messageId = data[i].source.messageId;
                data[i].source = Object.assign(data[i].source, mailList[messageId]);
                html = html + addRowWithData(tableId, data[i], i);
                sourceMatchingCounter++;
            }
            $("#"+ tableId + "> tbody").html(html);
            setRowClickListener("sourceRow", function () {
                selectedRow($(this).closest('tr'))
            });
            setRowClickListener("alertLevelSourceRow", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = data[index];
                if (rowData && rowData.source.alertLevel) {
                    $.alert({
                        title: '',
                        content: '' +
                            '<form action="" class="formName">' +
                                '<div class="form-group form-alert">' +
                                    '<label>取引先アラート:'+ rowData.source.alertLevel +'</label>' +
                                    '<label>取 引 先 名:' + rowData.source.partnerName + '</label>' +
                                    '<hr>'+
                                    '<span>' + rowData.source.alertContent + '</span>' +
                            '</form>',
                    });
                }
            });
            setRowClickListener("alertLevelPeopleInChargeSource", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = data[index];
                if (rowData && rowData.source.peopleInChargeAlertLevel) {
                    $.alert({
                        title: '',
                        content: '' +
                            '<form action="" class="formName">' +
                            '<div class="form-group form-alert">' +
                            '<label>取引先アラート:'+ rowData.source.peopleInChargeAlertLevel +'</label>' +
                            '<label>取 引 先 名:' + rowData.source.partnerName + '</label>' +
                            '<label>担 当 者 名:' + rowData.source.peopleInChargeName + '</label>' +
                            '<label>メールアドレス:' + rowData.source.peopleinChargeEmail + '</label>' +
                            '<hr>'+
                            '<span>' + rowData.source.peopleInChargeAlertContent + '</span>' +
                            '</form>',
                    });
                }
            });
        }
        initSortSource();
        selectFirstRow();
        enableResizeSourceColumns();
        updateTotalSourceMatching(sourceMatchingCounter);
    }
    
    function initSortSource() {
        $("#sourceMatch").tablesorter(
            {
                theme : 'default',
                sortList: [[2,1], [3,0]]
            })
            .bind("sortEnd",function() {
                var index = selectedSourceTableRow ? selectedSourceTableRow.index() : -1;
                var total = matchingResult ? matchingResult.length : 0;
                updateSourceControls(index, total);
            });

    }
    
    function showDestinationDataTable(tableId, data, replaceHeader, replaceBody, range) {
        setTimeout(function () {
            $('body').loadingModal('destroy');
            $('body').loadingModal({
                position: 'auto',
                text: 'Loading...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });
            var word = data.word;
            var source = data.source;
            currentDestinationResult = data.destinationList;
            removeAllRow(tableId, replaceBody);
            setTimeout(function () {
                if(currentDestinationResult.length > 0){
                    var html = replaceBody;
                    for(var i = 0; i < currentDestinationResult.length; i++){
                        currentDestinationResult[i].word = word;
                        var messageId = currentDestinationResult[i].messageId;
                        currentDestinationResult[i] = Object.assign(currentDestinationResult[i], mailList[messageId]);
                        currentDestinationResult[i].range = range;
                        html = html + addRowWithData(tableId, currentDestinationResult[i], i);
                    }
                    $("#"+ tableId + "> thead").html(replaceHeader);
                    $("#"+ tableId + "> tbody").html(html);
                    setRowClickListener("showDestinationMail", function () {
                        showDestinationMail($(this).closest('tr'))
                    });
                    setRowClickListener("sendToMoto", function () {
                        var replaceType = $(motoReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData && rowData.messageId){
                            lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
                            showMailEditor(lastSelectedSendMailAccountId, rowData.messageId, selectedRowData.source, rowData.matchRange, rowData.range, replaceType, "moto")
                        }
                    });
                    setRowClickListener("sendToSaki", function () {
                        var replaceType = $(sakiReplaceSelectorId).val();
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if(rowData){
                            if(selectedRowData && selectedRowData.source && selectedRowData.source.messageId){
                                lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
                                showMailEditor(lastSelectedSendMailAccountId, selectedRowData.source.messageId, rowData, rowData.range, rowData.matchRange, replaceType, "saki")
                            }
                        }
                    });
                    setRowClickListener("alertLevelDestinationMail", function () {
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if (rowData && rowData.alertLevel) {
                            $.alert({
                                title: '',
                                content: '' +
                                    '<form action="" class="formName">' +
                                    '<div class="form-group form-alert">' +
                                    '<label>取引先アラート:'+ rowData.alertLevel +'</label>' +
                                    '<label>取 引 先 名:' + rowData.partnerName + '</label>' +
                                    '<hr>'+
                                    '<span>' + rowData.alertContent + '</span>' +
                                    '</form>',
                            });
                        }
                    });
                    setRowClickListener("alertLevelPeopleInCharge", function () {
                        var row = $(this)[0].parentNode;
                        var index = row.getAttribute("data");
                        var rowData = currentDestinationResult[index];
                        if (rowData && rowData.peopleInChargeAlertLevel) {
                            $.alert({
                                title: '',
                                content: '' +
                                    '<form action="" class="formName">' +
                                    '<div class="form-group form-alert">' +
                                    '<label>取引先アラート:'+ rowData.peopleInChargeAlertLevel +'</label>' +
                                    '<label>取 引 先 名:' + rowData.partnerName + '</label>' +
                                    '<label>担 当 者 名:' + rowData.peopleInChargeName + '</label>' +
                                    '<label>メールアドレス:' + rowData.peopleinChargeEmail + '</label>' +
                                    '<hr>'+
                                    '<span>' + rowData.peopleInChargeAlertContent + '</span>' +
                                    '</form>',
                            });
                        }
                    });
                }
                updateDestinationDataTrigger();
                $('body').loadingModal('hide');
                enableResizeDestinationColumns();
                updateTotalDestinationMatching(currentDestinationResult.length);
                $("#"+ tableId).closest('.table-container-wrapper').scrollTop(0);
            }, 10)
        }, 10)
    }

    function destroySortDestination() {
        if(!!destinationMatchDataTable){
            destinationMatchDataTable.destroy();
        }
    }

    function initSortDestination() {
        $("#destinationMatch").tablesorter(
            {
                theme : 'default',
                headers: {
                    6: {
                        sorter: false
                    },
                    7: {
                        sorter: false
                    }
                },
                sortList: [[3,1], [4,0]]
            })
            .bind("sortEnd",function() {
                var index = selectedDestinationTableRow ? selectedDestinationTableRow.index() : -1;
                var total = currentDestinationResult ? currentDestinationResult.length : 0;
                updateDestinationControls(index, total);
            });
    }
    
    function updateDestinationDataTrigger() {
        $("#destinationMatch").trigger("updateAll", [ true, function () {
            
        } ]);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if(!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for(var i = 0; i < cells.length; i++){
            var cell = cells.item(i);
            var cellKeysData = cell.getAttribute("data");
            if(!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if(cellNode){
                if(cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if(Array.isArray(cellData)){
                        cellNode.textContent = cellData.length;
                    } else {
                        if( (cellData != null && cellData != "") && (cellKeysData === "alertLevel" || cellKeysData === "source.alertLevel" || cellKeysData === "source.peopleInChargeAlertLevel" || cellKeysData === "peopleInChargeAlertLevel" )){
                            cell.setAttribute("Class", "clickable");
                        }
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        return row.outerHTML;
        // body.appendChild(row);
    }
    
    function setRowClickListener(name, callback) {
        $("td[name='"+name+"']").off('click');
        $("td[name='"+name+"']").click(function () {
            if(typeof callback == "function"){
                callback.apply(this);
            }
        })
    }
    
    function showSourceMail(index, rowData, callback) {
        var rowData = matchingResult[index];
        if(rowData && rowData.source && rowData.source.messageId){
            $('#' + sakiPreviewContainerId).hide();
            $('#' + printSakiBtnId).prop("disabled", true);
            selectedDestinationTableRow = undefined;
            showMail(rowData.source.messageId, rowData.word, function (result) {
                showMailContent(result, [mailSubjectDivId, mailBodyDivId, mailAttachmentDivId]);
                updatePreviewMailToPrint(result, printElementId);
                if(typeof callback == "function"){
                    if(result){
                        callback(rowData, result.highLightRanges);
                    }
                }
            });
        }
    }

    function showDestinationMail(row) {
        selectedDestinationTableRow = row;
        updateDestinationControls(row.index(), currentDestinationResult.length);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var index = row[0].getAttribute("data");
        var rowData = currentDestinationResult[index];
        if(rowData && rowData.messageId){
            showMail(rowData.messageId, rowData.word, function (result) {
                showMailContent(result, [mailSakiSubjectDivId, mailSakiBodyDivId, mailSakiAttachmentDivId]);
                updatePreviewMailToPrint(result, printSakiElementId);
                $('#' + sakiPreviewContainerId).show();
                $('#' + printSakiBtnId).prop("disabled", false);
            }, rowData.matchRange);
        }
    }
    
    function selectFirstRow() {
        if(matchingResult && matchingResult.length > 0){
            var firstTr = $('#' + sourceTableId).find(' tbody tr:first');
            if(!firstTr[0]) return;
            var index = firstTr[0].getAttribute("data");
            if(index == null){
                $('#' + sourceTableId).find(' tbody tr:first').remove();
                selectFirstRow();
                return;
            }
            selectedRow(firstTr);
        }
    }
    
    function selectedRow(row) {
        selectedSourceTableRow = row;
        updateSourceControls(row.index(), matchingResult.length);
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
        var index = row[0].getAttribute("data");
        var rowData = matchingResult[index];
        selectedRowData = rowData;
        showSourceMail(index, rowData, showListMailDestination);

    }
    
    function showListMailDestination(rowData, ranges) {
        var range = "";
        if(ranges && ranges.length>0){
            range = ranges[0];
        }
        showDestinationData(destinationTableId, rowData, range);
    }

    function showDestinationData(destinationTableId, rowData, range){
        var replaceBody = replaceDestinationHTML;
        var replaceHeader = headerDestination;
        var isAlertpartner = false;
        var isAlertpeople = false;
        var dataDes = rowData.destinationList;
        for(var i=0;i<dataDes.length;i++){
            var messageId = dataDes[i].messageId;
            var data = Object.assign(dataDes[i], mailList[messageId]);
            if(data.alertLevel != null && data.alertLevel != ""){
                isAlertpartner = true;
            }
            if(data.peopleInChargeAlertLevel != null && data.peopleInChargeAlertLevel !=""){
                isAlertpeople = true;
            }
        }
        if(isAlertpartner){
            replaceBody = replaceBody + replaceDestinationAlertPartner;
            replaceHeader = replaceHeader + headerAlertPartner;
        }
        if(isAlertpeople){
            replaceBody = replaceBody + replaceDestinationAlertPeople;
            replaceHeader = replaceHeader + headerAlertPeople;
        }
        replaceBody = replaceBody + '</tr>';
        replaceHeader = replaceHeader + '</tr>';
        showDestinationDataTable(destinationTableId, rowData, replaceHeader, replaceBody, range);
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#"+ tableId + "> tbody").html(replaceHtml);
    }
    
    function showMail(messageId, highlightWord, callback, matchRange) {
        messageId = messageId.replace(/\+/g, '%2B');
        var url = "/user/matchingResult/email?messageId=" + messageId;
        if(highlightWord && highlightWord.length > 0) {
            highlightWord = highlightWord.replace(/\+/g, '%2B');
            url = url + "&highlightWord=" + highlightWord
                + "&spaceEffective=" + spaceEffective
                + "&distinguish=" + distinguish;
        }
        if(matchRange && matchRange.length > 0) {
            url = url + "&matchRange=" + matchRange
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                var result;
                if(data.status){
                    if(data.list && data.list.length > 0){
                        result = data.list[0];
                    }
                }
                if(typeof callback === "function"){
                    callback(result);
                }
            },
            error: function (e) {
                console.error("getMail ERROR : ", e);
                if(typeof callback === "function"){
                    callback();
                }
            }
        });
    }

    function showMailContent(data, elementIds) {
        var mailSubjectDiv = document.getElementById(elementIds[0]);
        var mailAttachmentDiv = document.getElementById(elementIds[2]);
        mailSubjectDiv.innerHTML = "";
        showMailBodyContent(elementIds[1], {originalBody: ""});
        mailAttachmentDiv.innerHTML = "";
        if(data){
            mailSubjectDiv.innerHTML = '<div class="mailbox-read-info">' +
                '<h5><b>' + data.subject + '</b></h5>' +
            '<h6>送信者: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h6>' +
            '</div>';
            showMailBodyContent(elementIds[1], data);
            var files = data.files ? data.files : [];
            showAttachFile(mailAttachmentDiv, files);
        }
    }
    
    function showMailBodyContent(id, data) {
        data.originalBody = wrapText(data.originalBody);
        var mailBodyDiv = document.getElementById(id);
        mailBodyDiv.scrollTop = 0;
        mailBodyDiv.innerHTML = data.originalBody;
        highlight(id, data);
    }

    function showMailContenttToEditor(data, accounts, receiverData, sendTo) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        getInforPartner(receiverListStr, function(partnerInfor){
        	showMailContentToEditorFinal(data, accounts, receiverData, sendTo, partnerInfor);
        });
    }
    
    function showMailContentToEditorFinal(data, accounts, receiverData, sendTo, partnerInfor) {
        var receiverListStr = receiverData.replyTo ? receiverData.replyTo : receiverData.from;
        resetValidation();
        document.getElementById(rdMailReceiverId).value = receiverListStr;
        updateMailEditorContent("");
        if(data){
            updateSenderSelector(data, accounts);
            senderGlobal = data.account;
            var to = data.to ? data.to.replace(/\s*,\s*/g, ",").split(",") : [];
            var cc = data.cc ? data.cc.replace(/\s*,\s*/g, ",").split(",") : [];
            var externalCC = data.externalCC ? data.externalCC.replace(/\s*,\s*/g, ",").split(",") : [];
            externalCCGlobal = externalCC;
            cc = updateCCList(cc,to);
            var indexOfSender = cc.indexOf(data.account);
            if(indexOfSender > -1){
                cc.splice(indexOfSender, 1);
            }
            var receiverList = receiverListStr.replace(/\s*,\s*/g, ",").split(",");
            if(receiverList.length > 0) {
                cc = updateCCList(cc, [receiverData.from]);
            }
            for(var i = 0; i < receiverList.length; i++) {
                var receiver = receiverList[i];
                var indexOfReceiver = cc.indexOf(receiver);
                if (indexOfReceiver > -1) {
                    cc.splice(indexOfReceiver, 1)
                }
            }
            cc = updateCCList(cc, externalCC);
            $('#' + rdMailCCId).importTags(cc.join(","));
            document.getElementById(rdMailSubjectId).value = data.subject;
            data.replacedBody = data.replacedBody ? (isHTML(data.originalBody) ? data.replacedBody : wrapPlainText(data.replacedBody)) : data.replacedBody;
            data.originalBody = wrapText(data.originalBody);
            data.originalBody = wrapInDivWithId(originalContentWrapId,data.originalBody);
            var stripped = strip(data.originalBody, originalContentWrapId);
            dataLinesConfirm = getHeaderFooterLines(stripped);
            data.replyOrigin = data.replyOrigin ? wrapText(data.replyOrigin) : data.replyOrigin;
            data.replyOrigin = getReplyWrapper(data);
            data.originalBody = data.replyOrigin ? data.originalBody + data.replyOrigin : data.originalBody;
            data.excerpt = getDecorateExcerpt(data.excerpt, sendTo, $('#' + rdMailSenderId+' option:selected').text());
            data.originalBody = data.excerpt + data.originalBody;
            data.originalBody = data.originalBody + data.signature;
            if(partnerInfor != null && partnerInfor != ""){
                data.originalBody = partnerInfor + data.originalBody;
            }
            updateMailEditorContent(data.originalBody);
            if( data.replacedBody != null){
                data.replacedBody = wrapInDivWithId(originalContentWrapId, data.replacedBody);
                stripped = strip(data.replacedBody, originalContentWrapId);
                dataLinesConfirm = getHeaderFooterLines(stripped);
                data.replacedBody = data.replyOrigin ? data.replacedBody + data.replyOrigin : data.replacedBody;
                data.replacedBody = data.excerpt + data.replacedBody;
                data.replacedBody = data.replacedBody + data.signature;
                if(partnerInfor != null && partnerInfor != ""){
                    data.replacedBody = partnerInfor + data.replacedBody;
                }
                updateMailEditorContent(data.replacedBody, true);
            }
            var files = data.files ? data.files : [];
            updateDropzoneData(attachmentDropzone, files);
        }
    }

    function updateSenderSelector(email, accounts) {
        accounts = accounts || [];
        $('#' + rdMailSenderId).empty();
        $.each(accounts, function (i, item) {
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id.toString() === lastSelectedSendMailAccountId)
            }));
        });
    }

    function updateMailEditorContent(content, preventClear){
        var editor = tinymce.get(rdMailBodyId);
        editor.setContent(content);
        if(!preventClear){
            editor.undoManager.clear();
        }
        editor.undoManager.add();
    }
    
    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function disableButton(buttonId, disabled) {
        if(buttonId && buttonId.length > 0){
            $(buttonId).prop("disabled", disabled);
        }
    }
    
    function showMailEditor(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo) {
        var cachedSeparateTab = getCachedSeparateTabSetting();
        if(cachedSeparateTab) {
            showMailEditorInNewTab(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo);
        } else {
            showMailEditorInTab(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo);
        }
    }
    
    function showMailEditorInNewTab(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo) {
        var data = {
            "accountId" : accountId,
            "messageId" : messageId,
            "receiver" : receiver,
            "textRange" : textRange,
            "textMatchRange" : textMatchRange,
            "replaceType" : replaceType,
            "sendTo" : sendTo,
            "historyType": getHistoryType(),
        };
        sessionStorage.setItem("separateSendMailData", JSON.stringify(data));
        var win = window.open('/user/sendTab', '_blank');
        if (win) {
            win.focus();
        } else {
            alert('Please allow popups for this website');
        }
    }
    
    function showMailEditorInTab(accountId, messageId, receiver, textRange, textMatchRange, replaceType, sendTo) {
        $('#sendMailModal').modal();
        if(sendTo === "moto") {
            $('#sendSuggestMailTitle').text("マッチング【元】へメール送信");
        } else {
            $('#sendSuggestMailTitle').text("マッチング【先】へメール送信");
        }
        lastMessageId = messageId;
        lastReceiver = receiver;
        lastTextRange = textRange;
        lastTextMatchRange = textMatchRange;
        lastReplaceType = replaceType;
        lastSendTo = sendTo;
        showMailWithReplacedRange(accountId, messageId, receiver.messageId, textRange, textMatchRange, replaceType, sendTo, function (email, accounts) {
            showMailContenttToEditor(email, accounts, receiver, sendTo)
        });
        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function() {
            lastSelectedSendMailAccountId = this.value;
            showMailWithReplacedRange(this.value, lastMessageId, lastReceiver.messageId, lastTextRange, lastTextMatchRange, lastReplaceType, lastSendTo, function (email, accounts) {
                showMailContenttToEditor(email, accounts, lastReceiver, lastSendTo)
            });
        });
        $("button[name='sendSuggestMailClose']").off('click');
        $('#cancelSendSuggestMail').button('reset');
        $("button[name='sendSuggestMailClose']").click(function() {
            var btn = $('#cancelSendSuggestMail');
            btn.button('loading');
            var attachmentData = getAttachmentData(attachmentDropzone);
            if(attachmentData.upload.length == 0) {
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
            var btn = $(this);
            var attachmentData = getAttachmentData(attachmentDropzone);
            var form = {
                messageId: lastReceiver.messageId,
                subject: $( "#" + rdMailSubjectId).val(),
                receiver: $( "#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
                cc: $( "#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
                content: getMailEditorContent(),
                originAttachment: attachmentData.origin,
                uploadAttachment: attachmentData.upload,
                accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
                matchingMessageId: messageId,
                sendType: getHistoryType(),
                historyType: getHistoryType(),
            };
            var stripped = strip(form.content, originalContentWrapId);
            var afterEditDataLines = getHeaderFooterLines(stripped);
            checkDataLines(dataLinesConfirm, afterEditDataLines, function (allowSend) {
                if(allowSend) {
                    btn.button('loading');
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
                            if(data && data.status){
                                //TODO: noti send mail success
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
                }
            });
        })
    }
    
    function getEnvSettings() {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/matchingResult/envSettings",
            cache: false,
            timeout: 600000,
            success: function (data) {
                try {
                    var result = data && data.msg ? JSON.parse(data.msg) : null;
                    if(result){
                        isDebug = result["debug_on"];
                        debugMailAddress = result["debug_receive_mail_address"];
                    }
                } catch (error){
                    console.error("getEnvSettings ERROR : ", error);
                }
            },
            error: function (e) {
                console.error("getEnvSettings FAILED : ", e);
            }
        });
    }

    function updatePreviewMailToPrint(data, printElmentId) {
        var printElment = document.getElementById(printElmentId);
        printElment.innerHTML = "";
        if(data){
            data.originalBody = wrapText(data.originalBody);
            var innerHtml = '<div class="box-body no-padding">' +
                '<div class="mailbox-read-info">' +
                '<h3>' + data.subject + '</h3>' +
                '<h5>From: ' + data.from + '<span class="mailbox-read-time pull-right">' + data.receivedAt + '</span></h5>' +
                '</div>' +
                '<div class="mailbox-read-message">' + data.originalBody + '</div>' +
                '</div>' +
                '<div class="box-footer"> ' +
                '<ul class="mailbox-attachments clearfix"> ';
            var files = data.files ? data.files : [];
            if(files.length > 0){
                var filesInnerHTML = "";
                for(var i = 0; i < files.length; i++ ){
                    var file = files[i];
                    var fileName = file.fileName;
                    var fileSize = getFileSizeString(file.size);
                    var fileInnerHTML = '<li> <span class="mailbox-attachment-icon">' +
                        '<i class="fa fa-file-o"></i>' +
                        '</span> ' +
                        '<div class="mailbox-attachment-info"> ' +
                        '<a href="#" class="mailbox-attachment-name">' +
                        '<i class="fa fa-paperclip"></i>' + fileName +
                        '</a> ' +
                        '<span class="mailbox-attachment-size">' + fileSize + '<a href="#" class="btn btn-default btn-xs pull-right"><i class="fa fa-cloud-download"></i></a> </span> ' +
                        '</div> ' +
                        '</li>';
                    filesInnerHTML += fileInnerHTML
                }
                innerHtml = innerHtml + filesInnerHTML;
            }
            innerHtml = innerHtml + '</ul></div>';
            printElment.innerHTML = innerHtml;
        }
    }

    function getFileSizeString(fileSize) {
        return fileSize >= 1000 ? (Math.round( (fileSize/1000) * 10 ) / 10) + " KB " : fileSize + " B"
    }

    function printPreviewEmail(id) {
        $("#" + id).show();
        $("#" + id).print();
        $("#" + id).hide();
    }

    function updateTotalSourceMatching(total) {
        $('#' + totalSourceMatchingContainId).text("絞り込み元: " + total + "件")
    }

    function updateTotalDestinationMatching(total) {
        $('#' + totalDestinationMatchingContainId).text("絞り込み先: " + total + "件")
    }

    function setInputChangeListener(id, acceptEmpty, callback) {
        $('#' + id).on('input', function() {
            var valid = validateEmailListInput(id);
            if(!acceptEmpty) {
                var value = $('#' + id).val();
                valid = valid && (value.length > 0);
            }
            valid ? $('#' + id + '-container').removeClass('has-error') : $('#' + id + '-container').addClass('has-error');
            if(typeof callback === "function") {
                callback(valid);
            }
        });
    }

    function resetValidation() {
        receiverValidate = true;
        ccValidate = true;
        $('#' + rdMailCCId + '-container').removeClass('has-error')
        $('#' + rdMailReceiverId + '-container').removeClass('has-error')
    }

    
    function highlight(id, data) {
        data = data || {};
        var highLightWords = data.highLightWords || [];
        highLightWords = buildHighlightWordNonSpace(highLightWords);
        var highLightWordRegexs = buildHighlightWordRegex(highLightWords);
        var excludeWords = data.excludeWords || [];
        excludeWords = buildHighlightWordNonSpace(excludeWords);
        var excludeWordRegexs = buildHighlightWordRegex(excludeWords);
        var highLightRanges = data.highLightRanges || [];
        $("input[data-search='"+ id +"']").val("");
        $("#" + id).unmark({
            done: function() {
                for(var i = 0; i < highLightWordRegexs.length; i++){
                    $("#" + id).markRegExp(highLightWordRegexs[i], markOptions);
                }
                for(var y = 0; y < excludeWordRegexs.length; y++){
                    $("#" + id).markRegExp(excludeWordRegexs[y], invisibleMarkOptions);
                }
                $("#" + id).mark(highLightRanges, rangeMarkOptions);
            }
        });
    }
    
    function buildHighlightWordNonSpace(highLightWords) {
        var highLightWordsExtended = [];
        for(var i = 0; i < highLightWords.length; i++) {
            var highLightWord = highLightWords[i];
            // highLightWordsExtended.push(highLightWord);
            var highLightWordNonSpace = highLightWord.replace(/ /g,'');
            if(highLightWordsExtended.indexOf(highLightWordNonSpace) == -1) {
                highLightWordsExtended.push(highLightWordNonSpace);
            }
        }
        return highLightWordsExtended;
    }
    
    function buildHighlightWordRegex(highLightWords) {
        var regexs = [];
        try {
            for(var i = 0; i < highLightWords.length; i++){
                var highLightWord = highLightWords[i];
                if(highLightWord && highLightWord.length > 0) {
                    var parts = getHighlightWordParts(highLightWord);
                    var regex = new RegExp(parts.join("\\s*"), "gmi");
                    regexs.push(regex);
                }
            }
        } catch (e) {
            console.error("[ERR] buildHighlightWordRegex: ", e);
        }
        return regexs;
    }

    function getHighlightWordParts(word) {
        var parts = word.split("");
        var result = [];
        for(var i = 0; i < parts.length; i++) {
            var part = word[i];
            if(standardEscape.indexOf(word[i]) > -1) {
                 part = "\\" + part;
            }
            result.push(part);
        }
        return result;
    }

    function initSearch(id, type) {
        // the input field
        var $input = $("input[data-search='"+ id +"']"),
            // clear button
            $clearBtn = $("button[data-search='" + type + "-clear']"),
            // prev button
            $prevBtn = $("button[data-search='" + type + "-prev']"),
            // next button
            $nextBtn = $("button[data-search='" + type + "-next']"),
            // the context where to search
            $content = $("#" + id),
            // jQuery object to save <mark> elements
            $results,
            // the class that will be appended to the current
            // focused element
            currentClass = "current",
            // the current index of the focused element
            currentIndex = 0;

        $input.keyup(function(event) {
            if (event.keyCode === 10 || event.keyCode === 13)
                event.preventDefault();
        });

        function jumpTo() {
            if ($results.length) {
                var position,
                    $current = $results.eq(currentIndex);
                $results.removeClass(currentClass);
                if ($current.length) {
                    $current.addClass(currentClass);
                    $content.scrollTop($content.scrollTop() + $current.position().top
                        - $content.height()/2 + $current.height()/2);
                }
            }
        }

        $input.on("input", function() {
            var searchVal = this.value;
            $content.unmark(
                Object.assign(
                    {},
                    markSearchOptions,
                    {
                        done: function() {
                            $content.mark(searchVal, Object.assign({},
                                markSearchOptions,
                                {
                                    done: function() {
                                        $results = $content.find("mark.mark-search");
                                        currentIndex = 0;
                                        jumpTo();
                                    }
                                }
                            ));
                        }
                    }
                )
            );
        });

        /**
         * Clears the search
         */
        $clearBtn.on("click", function() {
            $content.unmark(markSearchOptions);
            $input.val("").focus();
        });

        /**
         * Next and previous search jump to
         */
        $nextBtn.add($prevBtn).on("click", function() {
            if ($results.length) {
                currentIndex += $(this).is($prevBtn) ? -1 : 1;
                if (currentIndex < 0) {
                    currentIndex = $results.length - 1;
                }
                if (currentIndex > $results.length - 1) {
                    currentIndex = 0;
                }
                jumpTo();
            }
        });
    }
    
    function previewDraggingSetup() {
        var i = 0;
        var dragging = false;
        $('#dragbar').mousedown(function(e){
            e.preventDefault();

            dragging = true;
            var main = $('#saki-preview-wrapper');
            var dragbar = $('#dragbar');
            var ghostbar = $('<div>',
                {id:'ghostbar',
                    css: {
                        height: dragbar.outerHeight(),
                        top: dragbar.offset().top,
                        left: dragbar.offset().left
                    }
                }).appendTo('body');

            $(document).mousemove(function(e){
                ghostbar.css("left",e.pageX+2);
            });

        });

        $(document).mouseup(function(e){
            if (dragging)
            {
                var container = $('#preview-section');
                var leftWidth = (e.pageX - container.offset().left);
                leftWidth = leftWidth <= (container.width()/4) ? 3 : leftWidth;
                if(leftWidth == 3) {
                    var keeperHeight = $('#saki-preview-content-wrapper').outerHeight();
                    keeperHeight = keeperHeight > 0 ? keeperHeight : 600;
                    $('#moto-preview-content-keeper').css("height", keeperHeight + "px");
                    $('#moto-preview-content-keeper').show();
                    $('#moto-preview-content-wrapper').hide();
                } else {
                    $('#moto-preview-content-keeper').hide();
                    $('#moto-preview-content-wrapper').show();
                }
                var percentage = (leftWidth / container.width()) * 100;
                percentage = percentage > 75 ? 100 : percentage;
                var mainPercentage = 100-percentage;
                if(mainPercentage == 0) {
                    $('#saki-preview-content-wrapper').hide();
                } else {
                    $('#saki-preview-content-wrapper').show();
                }

                $('#moto-preview-wrapper').css("width",percentage + "%");
                $('#saki-preview-wrapper').css("width",mainPercentage + "%");
                $('#ghostbar').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }

    function previewDraggingSetup2() {
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
                var tableHeight = Math.floor((topHeight - 78) / 2);
                tableHeight = tableHeight > 60 ? tableHeight : 60;
                tableHeight = tableHeight < 420 ? tableHeight : 420;
                var previewHeightChange = 500 - tableHeight * 2;
                var previewHeight = 444 + previewHeightChange;
                $('.matching-result .table-container').css("height", tableHeight + "px");
                $('.matching-result .mail-body').css("height", previewHeight + "px");
                $('#ghostbar2').remove();
                $(document).unbind('mousemove');
                dragging = false;
            }
        });
    }
    
    function sourceFirst() {
        var firstTr = $('#' + sourceTableId).find(' tbody tr:first');
        selectedRow(firstTr);
    }

    function sourcePrev() {
        if(!selectedSourceTableRow) {
            sourceLast();
        } else {
            selectedRow(selectedSourceTableRow.prev());
        }
    }

    function sourceNext() {
        if(!selectedSourceTableRow) {
            sourceNext();
        } else {
            selectedRow(selectedSourceTableRow.next());
        }
    }

    function sourceLast() {
        var lastTr = $('#' + sourceTableId).find(' tbody tr:last');
        selectedRow(lastTr.prev());
    }

    function destinationFirst() {
        var firstTr = $('#' + destinationTableId).find(' tbody tr:first');
        showDestinationMail(firstTr);
    }

    function destinationPrev() {
        if(!selectedDestinationTableRow) {
            destinationLast();
        } else {
            showDestinationMail(selectedDestinationTableRow.prev());
        }
    }

    function destinationNext() {
        if(!selectedDestinationTableRow) {
            destinationFirst();
        } else {
            showDestinationMail(selectedDestinationTableRow.next());
        }
    }

    function destinationLast() {
        var lastTr = $('#' + destinationTableId).find(' tbody tr:last');
        showDestinationMail(lastTr.prev());
    }

    function getHistoryType() {
        return lastSendTo === "moto" ? 1 : 2;
    }

    function replaceCondition(rule) {
        if(rule && rule.condition != null){
            var rules = rule.rules;
            for(var i=0;i<rules.length;i++){
                replaceCondition(rules[i]);
            }
        }else{
            if((rule.id == RULE_NUMBER_ID || rule.id == RULE_NUMBER_DOWN_RATE_ID || rule.id == RULE_NUMBER_UP_RATE_ID)){
                switch (rule.value) {
                    case ruleNumberName:
                        rule.value = "数値";
                        break;
                    case ruleNumberDownRateName:
                        rule.value = "数値(下代)";
                        break;
                    case ruleNumberUpRateName:
                        rule.value = "数値(上代)";
                        break;
                }
            }
        }
    }
})(jQuery);
