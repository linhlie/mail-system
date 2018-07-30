function updateCCList(currentCCs, newCCs) {
    for (var i = 0; i < newCCs.length; i++) {
        if (currentCCs.indexOf(newCCs[i]) == -1) {
            currentCCs.push(newCCs[i]);
        }
    }
    return currentCCs;
}

function wrapText(text) {
    return isHTML(text) ? text : wrapPlainText(text);
}

function isHTML(str) {
    return str && (str.toLowerCase().indexOf("<html") > -1);
}

function wrapPlainText(text) {
    if(text)
        return text.replace(/(?:\r\n|\r|\n)/g, '<br />');
    return text;
}

function getReplyWrapper(data) {
    var wrapperText = '<div class="gmail_extra"><br>' +
        '<div class="gmail_quote">' +
        data.replySentAt +
        ' <span dir="ltr">&lt;<a href="mailto:' +
        data.replyFrom +
        '" target="_blank" rel="noopener">' +
        data.replyFrom +
        '</a>&gt;</span>:<br />' +
        '<blockquote class="gmail_quote" style="margin: 0 0 0 .8ex; border-left: 1px #ccc solid; padding-left: 1ex;">' +
        '<div dir="ltr">' + data.replyOrigin + '</div></blockquote></div></div>';
    return wrapperText;
}

function getDecorateExcerpt(excerpt, sendTo) {
    if(sendTo === "moto") {
        excerpt = getExcerptWithGreeting(excerpt, "元");
        excerpt = '<div class="gmail_extra"><span style="color: #ff0000;">【送り先は】マッチング元へ送信</span></div>' + excerpt;
    } else if (sendTo === "saki") {
        excerpt = getExcerptWithGreeting(excerpt, "先");
        excerpt = '<div class="gmail_extra"><span style="color: #ff0000;">【送り先は】マッチング先へ送信</span></div>' + excerpt;
    }
    return excerpt;
}

function getExcerptWithGreeting(excerpt, type) {
    var greeting = loadGreeting(type);
    if(greeting == null) {
        greeting = getExceprtLine("---------------------");
        greeting = greeting + '<br /><br /><br /><br /><br />';
    } else {
        greeting = '<br /><br />' + greeting + '<br /><br />';
    }
    excerpt = excerpt + greeting;
    return excerpt;
}

function getExceprtLine(line) {
    var exceprtLine = '<div class="gmail_extra"><span style="color: #ff0000;">' + line + '</span></div>';
    return exceprtLine;
}

function loadGreeting(type) {
    var greeting = null;
    var greetingData = loadGreetingData();
    for(var i = 0; i < greetingData.length; i++){
        var item = greetingData[i];
        if(item && item.type === type) {
            greeting = item.greeting;
            break;
        }
    }
    return greeting
}

function loadGreetingData() {
    var greetingDataInStr = localStorage.getItem("greetingData");
    var greetingData = greetingDataInStr == null ? [] : JSON.parse(greetingDataInStr);
    greetingData = Array.isArray(greetingData) ? greetingData : [];
    return greetingData;
}

function updateDropzoneData(dropzone, files) {
    var cachedIncludeAttachmentStr = localStorage.getItem("includeAttachment");
    var cachedIncludeAttachment = typeof cachedIncludeAttachmentStr !== "string" ? false : !!JSON.parse(cachedIncludeAttachmentStr);
    dropzone.removeAllFiles(true);
    if(cachedIncludeAttachment && files.length > 0){
        for(var i = 0; i < files.length; i++ ){
            var file = files[i];
            var mockFile = { id: file.id, name: file.fileName, size: file.size, type: 'text/plain'};
            dropzone.emit("addedfile", mockFile);
            dropzone.emit("processing", mockFile);
            dropzone.emit("success", mockFile);
            dropzone.emit("complete", mockFile);
            dropzone.files.push( mockFile );
        }
    }
}

function getAttachmentData(dropzone) {
    var result = {
        origin: [],
        upload: []
    };
    for(var i = 0; i < dropzone.files.length; i++){
        var file = dropzone.files[i];
        if(!!file.id){
            if(!!file.upload){
                result.upload.push(file.id);
            } else {
                result.origin.push(file.id);
            }
        }
    }
    return result;
}

function validateAndShowEmailListInput(id, acceptEmpty) {
    var valid = validateEmailListInput(id);
    if (!acceptEmpty) {
        var value = $('#' + id).val();
        valid = valid && (value.length > 0);
    }
    valid ? $('#' + id + '-container').removeClass('has-error') : $('#' + id + '-container').addClass('has-error');
    return valid;
}

function validateEmailListInput(id) {
    var rawCC = $('#' + id).val();
    rawCC = rawCC || "";
    var ccText = rawCC.replace(/\s*,\s*/g, ",");
    var cc = ccText.split(",");
    var senderValid = true;
    if(cc.length === 1 && cc[0] == "") {
        senderValid = true;
    } else {
        for(var i = 0; i < cc.length; i++) {
            var email = cc[i];
            var valid = validateEmail(email);
            if(!valid) {
                senderValid = false;
                break;
            }
        }
    }
    return senderValid;
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function getEmailDomain(email) {
    if(typeof email === "string"  && email.indexOf("@") >= 0) {
        return email.split("@")[1]
    }
    return "";
}

function updateSourceControls(index, total) {
    var container = $("#source-control");
    updateControls(container, index, total);
}

function updateDestinationControls(index, total) {
    var container = $("#destination-control");
    updateControls(container, index, total);
}

function updateControls(container, index, total) {
    var firstDisable = (total <= 1 || index == 0);
    var lastDisable = (total <= 1 || index == (total - 1));
    var backDisable = (total <= 1 || index == 0);
    var nextDisable = (total <= 1 || index == (total - 1));
    container.find("button[name='first']").prop("disabled", firstDisable);
    container.find("button[name='last']").prop("disabled", lastDisable);
    container.find("button[name='prev']").prop("disabled", backDisable);
    container.find("button[name='next']").prop("disabled", nextDisable);
}