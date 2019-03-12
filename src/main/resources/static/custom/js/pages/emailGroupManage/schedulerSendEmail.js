(function () {
    "use strict";
    var rdMailSubjectId = 'rdMailSubject';
    var rdMailBodyId = 'rdMailBodyTab';
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

    var selectAllReceiverId = "#select-all-receiver";

    var showEmailReceiverGroupId = "#scheduler-email-group-ul";
    var showEmailReceiverAddressId = "#scheduler-email-address-ul";

    var settingEmailBlockId = '#setting-email';
    var settingSchedulerBlockId = '#setting-scheduler';
    var nextToSettingSchedulerId = '#nextToSettingScheduler';
    var backToSettingEmailId = '#backToSettingEmail';
    var createSheduleSendEmailId = '#sendSuggestMail';

    var sendByHourDayId = '#sendByHourDay';
    var sendByHourHourId = '#sendByHourHour';
    var sendByDayHourId = '#sendByDayHour';
    var sendByMonthDayId = '#sendByMonthDay';
    var sendByMonthHourId = '#sendByMonthHour';

    var selectEmailReceiverId = '#selectEmailGroup';
    var showSchedulerKey = "show-detail-scheduler-send-email";
    var schedulerType = null;
    var schedulerId = null;

    var emailGroups = [];
    var listPeople = [];

    var checkedAllReceiverFlag = false;
    var checkedAllGroupReceiverFlag = false;
    var checkedAllEmailReceiverFlag = false;
    var emailGroupsSelected = [];
    var emailGroupNamesSelected = [];
    var emailAddressSelected = [];
    var emailSenders = [];

    var emailForm = null;

    $(function () {
        initDropzone();
        initTagInput();
        initTinyMCE();
        setupDatePickers();
        setupTimePickers();
        setButtonClickListenter(nextToSettingSchedulerId, nextToSettingSchedulerPage);
        setButtonClickListenter(backToSettingEmailId, backToSettingEmail);
        setButtonClickListenter(selectEmailReceiverId, selectEmailReceiverOnclick);
        setButtonClickListenter(createSheduleSendEmailId, createSheduleSendEmailOnclick);
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
                        removeFile(file.id)
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
                loadEmailSender();
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
                    checkSchedulerType();
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
                $('#' + rdMailCCId).importTags(accounts[i].cc);
            }
        }

        $('#' + rdMailSenderId).off('change');
        $('#' + rdMailSenderId).change(function () {
            lastSelectedSendMailAccountId = this.value;
            for(var i=0;i<accounts.length;i++){
                if(accounts[i].id == lastSelectedSendMailAccountId){
                    $('#' + rdMailCCId).importTags(accounts[i].cc);
                }
            }
        });
    }

    function getMailEditorContent() {
        var editor = tinymce.get(rdMailBodyId);
        return editor.getContent();
    }

    function nextToSettingSchedulerPage() {
        receiverValidate = validateAndShowEmailListInput(rdMailReceiverId, false);
        ccValidate = validateAndShowEmailListInput(rdMailCCId, true);
        if(!(receiverValidate && ccValidate)) return;
        var attachmentData = getAttachmentData(attachmentDropzone);
        emailForm = {
            subject: $( "#" + rdMailSubjectId).val(),
            receiver: $( "#" + rdMailReceiverId).val().replace(/\s*,\s*/g, ","),
            cc: $( "#" + rdMailCCId).val().replace(/\s*,\s*/g, ","),
            content: getMailEditorContent(),
            originAttachment: attachmentData.origin,
            uploadAttachment: attachmentData.upload,
            accountId: !!lastSelectedSendMailAccountId ? lastSelectedSendMailAccountId : undefined,
            sendType: 16,
            historyType: 16,
        };

        $(settingEmailBlockId).css("display", "none");
        $(settingSchedulerBlockId).css("display", "block");
    }

    function backToSettingEmail() {
        $(settingSchedulerBlockId).css("display", "none");
        $(settingEmailBlockId).css("display", "block");
    }

    function selectEmailReceiverOnclick(){
        loadListEmailGroup();
    }

    function createSheduleSendEmailOnclick() {
        var typeSendMail = $('input[name=send-email-scheduler]:checked').val();

        if(typeSendMail == 0){
            sendEmailNow();
        }

        if(typeSendMail == 1){
            sendEmailByHour();
        }

        if(typeSendMail == 2){
            sendEmailByDay();
        }

        if(typeSendMail == 3){
            sendEmailByMonth();
        }
    }

    function sendEmailNow() {
        var form = {
            sendMailForm: emailForm,
            typeSendEmail: 0
        }
        createScheduler(form);
    }

    function sendEmailByHour() {
        var day = $(sendByHourDayId).val();
        var hour = $(sendByHourHourId).find("input").val();
        if(!hour || hour == null){
            showAlertFillData();
            return;
        }
        if(!day || day == null){
            showAlertFillData();
            return;
        }
        var form = {
            sendMailForm: emailForm,
            typeSendEmail: 1,
            dateSendMail: day,
            hourSendMail: hour
        }
        createScheduler(form);
    }

    function sendEmailByDay() {
        var date = "";
        var dateArr = [];
        $('input[name=send-by-day_day]:checked').each(function() {
            dateArr.push(this.value);
        });
        date = dateArr.join();
        var hour = $(sendByDayHourId).find("input").val();
        if(!date || date == null){
            showAlertFillData();
            return;
        }
        if(!hour || hour == null){
            showAlertFillData();
            return;
        }
        var form = {
            sendMailForm: emailForm,
            typeSendEmail: 2,
            dateSendMail: date,
            hourSendMail: hour
        }
        createScheduler(form);
    }

    function sendEmailByMonth() {
        var typeSendDay = $('input[name=send-by-month-day]:checked').val();
        var day = "1";
        if(!typeSendDay || typeSendDay == null){
            showAlertFillData();
            return;
        }
        day = $(sendByMonthDayId).val();

        if(typeSendDay == "send-by-month"){
            day = $("#sendByMonthDay").val();
        }

        if(typeSendDay == "the-first-day"){
            day = "the-first-day";
        }

        if(typeSendDay == "the-last-day"){
            day = "the-last-day";
        }
        if(!day || day == null){
            showAlertFillData();
            return;
        }
        var hour = $(sendByMonthHourId).find("input").val();
        if(!hour || hour == null){
            showAlertFillData();
            return;
        }
        var form = {
            sendMailForm: emailForm,
            typeSendEmail: 3,
            dateSendMail: day,
            hourSendMail: hour
        }
        createScheduler(form);
    }

    function showAlertFillData(){
        $.alert("You must fill full data to create scheduler");
    }

    function createScheduler(form) {
        function onSuccess(response) {
            if(response){
                if(response.status){
                    $.alert({
                        title: "",
                        content: "created scheduler success",
                        onClose: function () {
                            location.reload();
                        }
                    });
                }else{
                    $.alert("created scheduler fail");
                }
            }
        }

        function onError(error) {
            console.log("create scheduler fail");
        }
        if(schedulerType == "update-scheduler"){
            form.id = schedulerId;
        }
        console.log(form);
        createSchedulerSendEmail(form, onSuccess, onError);
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

    function loadListEmailGroup(groupName) {
        function onSuccess(response) {
            if(response && response.status){
                emailGroups = response.list;
                listPeople = response.listPeople;
                showSelectModal(addReceiverEmailAddress);
            }
        }

        function onError(error) {
            console.log("loadListEmailGroup fail");
        }

        getEmailAddressAndGroup(groupName, onSuccess, onError);
    }
    
    function addReceiverEmailAddress() {
        
    }

    function showSelectModal(callback) {
        $('#dataModal').modal();
        updateEmailsReceiver();
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

    function updateEmailsReceiver() {
        var emailGroupHtml = "";
        $(showEmailReceiverGroupId).empty();
        var emailAddressHtml = "";
        $(showEmailReceiverAddressId).empty();
        if(!emailGroups || !listPeople){
            return;
        }
        for(var i=0;i<emailGroups.length;i++){
            var flag = false;
            for(var j=0;j<emailGroupsSelected.length;j++){
                if(emailGroupsSelected[j] == emailGroups[i].id){
                    flag = true;
                }
            }
            if(flag == true){
                emailGroupHtml = emailGroupHtml +
                    '<li class="scheduler-email-li"><label style="display: block">' +
                    '<input type="checkbox" class="select-email-group" value="' + emailGroups[i].id +'" checked="checked"/>&nbsp;' + emailGroups[i].groupName +
                    '</label></li>';
            }else{
                emailGroupHtml = emailGroupHtml +
                    '<li class="scheduler-email-li"><label style="display: block">' +
                    '<input type="checkbox" class="select-email-group" value="' + emailGroups[i].id +'"/>&nbsp;' + emailGroups[i].groupName +
                    '</label></li>';
            }
        }
        $(showEmailReceiverGroupId).append(emailGroupHtml);

        for(var i=0;i<listPeople.length;i++){
            var flag = false;
            for(var j=0;j<emailAddressSelected.length;j++) {
                if (emailAddressSelected[j] == listPeople[i].emailAddress) {
                    flag = true;
                }
            }
            if (flag == true) {
                emailAddressHtml = emailAddressHtml +
                    '<li class="scheduler-email-li"><label style="display: block">' +
                    '<input type="checkbox" class="select-email-address" value="' + listPeople[i].emailAddress + '" checked="checked"/>&nbsp;' + listPeople[i].emailAddress +
                    '</label></li>';
            } else {
                emailAddressHtml = emailAddressHtml +
                    '<li class="scheduler-email-li"><label style="display: block">' +
                    '<input type="checkbox" class="select-email-address" value="' + listPeople[i].emailAddress + '"/>&nbsp;' + listPeople[i].emailAddress +
                    '</label></li>';
            }
        }
        $(showEmailReceiverAddressId).append(emailAddressHtml);

        showList();
        setupSelectBoxes();
    }

    function showList() {
        $('.scheduler-email-show-list').off('click');
        $('.scheduler-email-show-list').click(function () {
            // var checkbox = $(this).siblings("label").children("input");
            // checkbox.prop("checked", !checkbox.prop("checked"));
            $(this).siblings("ul").slideToggle("slow");
            $(this).siblings(".sheduler-icon-arrow").toggleClass("glyphicon-chevron-down");
        })

        $('.sheduler-icon-arrow').off('click');
        $('.sheduler-icon-arrow').click(function () {
            $(this).toggleClass("glyphicon-chevron-down");
            $(this).siblings("ul").slideToggle("slow");
        })
    }

    function setupSelectBoxes() {
        $(selectAllReceiverId).prop("checked", checkedAllReceiverFlag);
        $('.select-all-email-group').prop("checked", checkedAllGroupReceiverFlag);
        $('.select-all-email-address').prop("checked", checkedAllEmailReceiverFlag);

        $(selectAllReceiverId).off('click');
        $(selectAllReceiverId).click(function () {
            setAllCheckBoxReceiver(this.checked);
        });

        $('.select-all-email-group').off('click');
        $('.select-all-email-group').click(function(){
            setAllCheckBoxEmailGroupReceiver(this.checked);
            if(this.checked) {
                checkAllCheckBoxReceiver();
            } else {
                $(selectAllReceiverId).prop("checked", false);
            }
        });

        $('.select-all-email-address').off('click');
        $('.select-all-email-address').click(function(){
            setAllCheckBoxEmailAddresseceiver(this.checked);
            if(this.checked) {
                checkAllCheckBoxReceiver();
            } else {
                $(selectAllReceiverId).prop("checked", false);
            }
        });

        $('.select-email-group').off('click');
        $('.select-email-group').click(function(){
            if($('.select-email-group').length == $(".select-email-group:checked").length) {
                $('.select-all-email-group').prop("checked", true);
            } else {
                $('.select-all-email-group').prop("checked", false);
            }
        });


        $('.select-email-address').off('click');
        $('.select-email-address').click(function(){
            if($('.select-email-address').length == $(".select-email-address:checked").length) {
                $('.select-all-email-address').prop("checked", false);
            } else {
                $('.select-all-email-address').prop("checked", false);
            }
        });
    }

    function setAllCheckBoxReceiver(checked) {
        $('.select-all-email-group').prop('checked', checked);
        $('.select-all-email-address').prop('checked', checked);

        $('.select-email-group').each(function() {
            $(this).prop('checked', checked);
        });

        $('.select-email-address').each(function() {
            $(this).prop('checked', checked);
        });
    }

    function setAllCheckBoxEmailGroupReceiver(checked) {
        $('.select-email-group').each(function() {
            $(this).prop('checked', checked);
        });
    }

    function setAllCheckBoxEmailAddresseceiver(checked) {
        $('.select-email-address').each(function() {
            $(this).prop('checked', checked);
        });
    }
    
    function checkAllCheckBoxReceiver() {
        var checkedGroup = $('.select-all-email-group').is(":checked");
        var checkedEmail = $('.select-all-email-address').is(":checked");
        if(checkedGroup && checkedEmail){
            $(selectAllReceiverId).prop('checked', true);
        }
    }

    function getEmailGroupsSelected() {
        emailGroupsSelected = [];
        emailGroupNamesSelected = [];
        emailAddressSelected = [];

        checkedAllReceiverFlag = $(selectAllReceiverId).is(":checked");
        checkedAllGroupReceiverFlag = $('.select-all-email-group').is(":checked");
        checkedAllEmailReceiverFlag = $('.select-all-email-address').is(":checked");

        $('.select-email-group').each(function() {
            if(this.checked){
                emailGroupsSelected.push($(this).val());
                emailGroupNamesSelected.push($(this).parent('label').text());
            }
        });

        $('.select-email-address').each(function() {
            if(this.checked) {
                emailAddressSelected.push($(this).val());
            }
        });
    }

    function loadEmailReceivers() {
        function onSuccess(response) {
            if(response && response.status){
                if(response.list){
                    var recivers  = '';
                    for(var i=0; i<response.list.length; i++) {
                        recivers = recivers + ", " + response.list[i];
                    }
                    $('#' + rdMailReceiverId).importTags(recivers);
                    if(emailGroupNamesSelected && emailGroupNamesSelected.length>0){
                        $("#rdEmailGroup").val(emailGroupNamesSelected.join());
                    }
                }
            }
        }

        function onError(error) {
            console.log("loadEmailReceivers fail");
        }

        getEmailReceivers({
            listEmailGroupId: emailGroupsSelected,
            listEmailAddress: emailAddressSelected
        }, onSuccess, onError);
    }

    function resetForm() {
        $(".setting-scheduler-block").trigger("reset");
    }

    function checkSchedulerType() {
        var schedulerData= localStorage.getItem(showSchedulerKey);
        if(schedulerData){
            var schedulerDataJson = JSON.parse(schedulerData);
            if(schedulerDataJson && schedulerDataJson.id){
                schedulerId = schedulerDataJson.id;
                schedulerType = schedulerDataJson.type;
                loadSchedulerEmail(schedulerId);
                var data = {
                    type: "create-scheduler",
                }
                localStorage.setItem(showSchedulerKey, JSON.stringify(data));
            }
        }
    }

    function loadSchedulerEmail(id) {
        function onSuccess(response) {
            if(response && response.status){
                if(response.list && response.list.length >0){
                    setFormData(response.list[0]);
                }
            }
        }

        function onError(error) {
            console.log("loadSchedulerEmail fail");
        }

        getSchedulerEmail(id, onSuccess, onError);
    }
    
    function setFormData(data) {
        if(!data || data==null){
            return;
        }
        console.log(data);
        $('#'+rdMailSenderId).val(data.accountSentMailId);
        $('#' + rdMailReceiverId).importTags(data.to);
        $('#'+rdMailSubjectId).val(data.subject);
        tinymce.get(rdMailBodyId).setContent(data.body);

        $('input[name=send-email-scheduler][value='+data.typeSendEmail+']').prop("checked",true);
        switch (data.typeSendEmail) {
            case 0:
                console.log("0");
                break;
            case 1:
                console.log("1");
                $(sendByHourDayId).val(data.dateSendEmail);
                $(sendByHourHourId).find("input").val(data.hourSendEmail);
                break;
            case 2:
                console.log("2");
                var dateArr = data.dateSendEmail.split(",");
                for(var i=0;i<dateArr.length;i++){
                    $('input[name=send-by-day_day][value='+dateArr[i]+']').prop("checked",true);
                }
                $(sendByDayHourId).find("input").val(data.hourSendEmail);
                break;
            case 3:
                console.log("3");
                if(data.dateSendEmail == "the-first-day"){
                    $('input[name=send-by-month-day][value=the-first-day]').prop("checked",true);
                }else{
                    if(data.dateSendEmail == "the-last-day"){
                        $('input[name=send-by-month-day][value=the-last-day]').prop("checked",true);
                    }else{
                        $('input[name=send-by-month-day][value=send-by-month]').prop("checked",true);
                        $("#sendByMonthDay").val(data.dateSendEmail);
                    }
                }
                $(sendByMonthHourId).find("input").val(data.hourSendEmail);
                break;
        }
    }

})(jQuery);
