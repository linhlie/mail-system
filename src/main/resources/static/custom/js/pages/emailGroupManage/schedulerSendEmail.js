(function () {
    "use strict";
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBodyTab';
    var rdMailAttachmentId = 'rdMailAttachment';
    var rdMailSenderId = 'rdMailSender';
    var rdMailCCId = 'rdMailCC';
    var rdMailReceiverId = 'rdMailReceiver';

    var receiverValidate = true;
    var ccValidate = true;

    var externalCCGlobal = [];
    var senderGlobal = "";

    var attachmentDropzoneId = "#attachment-dropzone-tab";
    var attachmentDropzone;

    var lastSelectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
    var lastReceiver;
    var lastMessageId;
    var lastTextRange;
    var lastTextMatchRange;
    var lastReplaceType;
    var lastSendTo;
    var lastHistoryType;
    var engineer;
    var type;
    var matching = false;

    var originalContentWrapId = "ows-mail-body";
    var dataLinesConfirm;

    var emailGroupTableId = 'emailGroupTable';
    var selectAllEmailGroupId = '#selectAllEmailGroup';

    var settingEmailBlockId = '#setting-email';
    var settingSchedulerBlockId = '#setting-scheduler';
    var nextToSettingSchedulerId = '#nextToSettingScheduler';
    var backToSettingEmailId = '#backToSettingEmail';

    var sendByHourDayId = '#sendByHourDay';
    var sendByHourHourId = '#sendByHourHour';
    var sendByDayHourId = '#sendByDayHour';
    var sendByMonthDayId = '#sendByMonthDay';
    var sendByMonthHourId = '#sendByMonthHour';

    var selectEmailGroupId = '#selectEmailGroup';

    var emailGroups = [];
    var emailGroupsSelected = [];
    var emailSenders = [];

    var replaceRow = '<tr role="row" class="hidden">' +
        '<td align="center" rowspan="1" colspan="1" data="selectGroup">' +
        '<input type="checkbox" class="selectAll"/>' +
        '</td>' +
        '<td rowspan="1" colspan="1" data="groupName" name="groupName"><span></span></td>' +
        '</tr>';

    $(function () {
        initDropzone();
        initTagInput();
        initTinyMCE();
        setupDatePickers();
        setupTimePickers();
        loadEmailSender();
        setButtonClickListenter(nextToSettingSchedulerId, nextToSettingSchedulerPage);
        setButtonClickListenter(backToSettingEmailId, backToSettingEmail);
        setButtonClickListenter(selectEmailGroupId, selectEmailGroupOnclick);
    });

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
                        file(file.id)
                    }
                });
            },
            thumbnail: function(file, dataUrl) {}
        })
    }

    function initTagInput(){
        $('#' + rdMailReceiverId).tagsInput({
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

    function initTinyMCE(){
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
                'table contextmenu directionality template paste textcolor colorpicker'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            fontsize_formats: '6pt 8pt 10pt 11pt 12pt 13pt 14pt 16pt 18pt 20pt 24pt 28pt 32pt 36pt 40pt 45pt 50pt',
            init_instance_callback: function (editor) {
            }
        });
    }
    
    function loadEmailSender() {
        function onSuccess(response) {
            if(response && response.status){
                emailSenders = response.list;
                console.log(response.list);
                if(emailSenders && emailSenders.length>0){
                    updateSenderSelector(emailSenders);
                }
            }
        }

        function onError(error) {
            console.error("loadEmailSender fail");
        }

        getEmailSenders(onSuccess, onError);
    }

    function updateSenderSelector(accounts) {
        accounts = accounts || [];
        $('#' + rdMailSenderId).empty();

        $.each(accounts, function (i, item) {
            $('#' + rdMailSenderId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id.toString() === lastSelectedSendMailAccountId)
            }));
        });
        for(var i=0;i<accounts.length;i++){
            if(accounts[i].id == lastSelectedSendMailAccountId){
                console.log(accounts[i].cc);
                $('#' + rdMailCCId).importTags(accounts[i].cc);
            }
        }

        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function () {
            lastSelectedSendMailAccountId = this.value;
            for(var i=0;i<accounts.length;i++){
                if(accounts[i].id == lastSelectedSendMailAccountId){
                    console.log(accounts[i].cc);
                    $('#' + rdMailCCId).importTags(accounts[i].cc);
                }
            }
        });
    }

    function nextToSettingSchedulerPage() {
        $(settingEmailBlockId).css("display", "none");
        $(settingSchedulerBlockId).css("display", "block");
    }

    function backToSettingEmail() {
        $(settingSchedulerBlockId).css("display", "none");
        $(settingEmailBlockId).css("display", "block");
    }

    function setupDatePickers() {
        var datepicker = $.fn.datepicker.noConflict();
        $.fn.bootstrapDP = datepicker;
        $(sendByHourDayId).datepicker({
            dateFormat: 'mm-dd-yy',
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });

        $(sendByMonthDayId).datepicker({
            dateFormat: 'mm-dd-yy',
            beforeShow: function() {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
    }

    function setupTimePickers() {
        $(sendByHourHourId).datetimepicker({
            format: 'HH:mm'
        });

        $(sendByDayHourId).datetimepicker({
            format: 'HH:mm'
        });

        $(sendByMonthHourId).datetimepicker({
            format: 'HH:mm'
        });
    }

    function selectEmailGroupOnclick(){
        loadListEmailGroup();
    }

    function loadListEmailGroup(groupName) {
        function onSuccess(response) {
            if(response && response.status){
                emailGroups = response.list;
                if(emailGroups && emailGroups.length>0){
                    console.log(emailGroups);
                    showSelectModal(addReceiverEmailAddress);
                }
            }
        }

        function onError(error) {
            console.log("loadListEmailGroup fail");
        }

        getEmailAddressGroup(groupName, onSuccess, onError);
    }
    
    function addReceiverEmailAddress() {
        
    }

    function showSelectModal(callback) {
        $('#dataModal').modal();
        updateEmailGroupTable();
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {

            if(typeof callback === "function"){
                getEmailGroupsSelected();
                loadEmailReceivers();
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

    function removeAllRow(tableId, replaceHtml) {
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function updateEmailGroupTable() {
        removeAllRow(emailGroupTableId, replaceRow);
        if (emailGroups.length > 0) {
            var html = replaceRow;
            for (var i = 0; i < emailGroups.length; i++) {
                html = html + addRowWithData(emailGroupTableId, emailGroups[i], i);
            }
            $("#" + emailGroupTableId + "> tbody").html(html);
        }
        setupSelectBoxes();
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
                    if (cellKey == "selectGroup") {
                        cellNode.name = "selectEmailGroup";
                    }
                } else{
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
                }
            }
        }
        return row.outerHTML;
    }

    function setupSelectBoxes() {
        var selectAll = $("input[name=selectEmailGroup]").length == $("input[name=selectEmailGroup]:checked").length? true: false;
        $(selectAllEmailGroupId).prop("checked", selectAll);

        $(selectAllEmailGroupId).off('click');
        $(selectAllEmailGroupId).click(function () {
            setAllCheckBoxEmailGroup(this.checked);
        });

        $('input[name=selectEmailGroup]').off('click');
        $('input[name=selectEmailGroup]').click(function(){
            if($("input[name=selectEmailGroup]").length == $("input[name=selectEmailGroup]:checked").length) {
                $(selectAllEmailGroupId).prop("checked", true);
            } else {
                $(selectAllEmailGroupId).prop("checked", false);
            }
        });
    }

    function setAllCheckBoxEmailGroup(checked) {
        $("input[name=selectEmailGroup]").each(function( index ) {
            $(this).prop('checked', checked);
        });
    }

    function getEmailGroupsSelected() {
        emailGroupsSelected = [];
        $("input[name=selectEmailGroup]:checked").each(function( index ) {
            emailGroupsSelected.push($(this).val());
        });
    }

    function loadEmailReceivers() {
        function onSuccess(response) {
            if(response && response.status){
                console.log(response.list);
            }
        }

        function onError(error) {
            console.log("loadEmailReceivers fail");
        }

        getEmailReceivers({
            listId: emailGroupsSelected
        }, onSuccess, onError);
    }

})(jQuery);
