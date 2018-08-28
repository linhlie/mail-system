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
        excerpt = '<div class="gmail_extra"><span>【送り先は】マッチング元へ送信</span></div>' + excerpt;
    } else if (sendTo === "saki") {
        excerpt = getExcerptWithGreeting(excerpt, "先");
        excerpt = '<div class="gmail_extra"><span>【送り先は】マッチング先へ送信</span></div>' + excerpt;
    }
    return excerpt;
}

function wrapInDivWithId(id, content) {
    var div = '<div class="gmail_extra" id="'+ id +'">' + content + '</div>';
    return div;
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
    var exceprtLine = '<div class="gmail_extra"><span>' + line + '</span></div>';
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
    files = files || [];
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

//add Element with id is strip-el to html
function strip(html, selectorId) {
    var rv = '';
    $( "#strip-el").html('');
    html = html.replace(/<!--([\s\S]*?)-->/mig, "");
    $("#strip-el").append(html);
    var element = selectorId ? $( "#strip-el").find("#" + selectorId)[0] : $( "#strip-el")[0];
    rv = getText(element);
    return rv;
}

function getText(n) {
    var rv = '';
    if(!!n) {
        if (n.nodeType == 3) {
            rv = n.nodeValue;
        } else {
            for (var i = 0; i < n.childNodes.length; i++) {
                rv += getText(n.childNodes[i]);
            }
            var d = getComputedStyle(n).getPropertyValue('display');
            if (d.match(/^block/) || d.match(/list/) || n.tagName == 'BR') {
                rv += "\n";
            }
        }
    }

    return rv;
}

function getHeaderFooterLines(text) {
    var data = {
        header: [],
        footer: []
    }
    var textLines = text.split("\n");
    for(var i = 0; i < textLines.length; i++){
        var line = textLines[i];
        line = line.trim();
        if(line.length == 0) continue;
        if(data.header.length < 5) {
            data.header.push(line);
        }
    }
    for(var i = textLines.length; i > 0; i--){
        var line = textLines[i-1];
        line = line.trim();
        if(line.length == 0) continue;
        if(data.footer.length < 5) {
            data.footer.push(line);
        }
    }

    return {
        header: data.header.join("\n"),
        footer: data.footer.join("\n"),
    }
}

function checkDataLines(dataLines1, dataLines2, callback) {
    if(dataLines1.header === dataLines2.header || dataLines1.footer === dataLines2.footer) {
        $.confirm({
            title: '<b>【送信確認】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">マッチング先の文章が修正されていません。<br/><br/>このまま送信しますか?<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        callback(true);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){
                        callback(false);
                    }
                },
            }
        });
    } else {
        callback(true);
    }
}

function setButtonClickListenter(id, callback) {
    $(id).off('click');
    $(id).click(function () {
        if (typeof callback === "function") {
            callback();
        }
    });
}

function locationReload() {
    window.location.reload();
}

function getFileExtension(fileName) {
    var parts = fileName.split(".");
    var extension = "." + parts[(parts.length - 1)];
    return extension.toLowerCase();
}

function getOS() {
    var userAgent = window.navigator.userAgent,
        platform = window.navigator.platform,
        macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K'],
        windowsPlatforms = ['Win32', 'Win64', 'Windows', 'WinCE'],
        iosPlatforms = ['iPhone', 'iPad', 'iPod'],
        os = null;

    if (macosPlatforms.indexOf(platform) !== -1) {
        os = 'Mac OS';
    } else if (iosPlatforms.indexOf(platform) !== -1) {
        os = 'iOS';
    } else if (windowsPlatforms.indexOf(platform) !== -1) {
        os = 'Windows';
    } else if (/Android/.test(userAgent)) {
        os = 'Android';
    } else if (!os && /Linux/.test(platform)) {
        os = 'Linux';
    }

    return os;
}

function isWindows() {
    var os = getOS();
    return (os === "Windows");
}

function getFileSizeString(fileSize) {
    return fileSize >= 1000 ? (Math.round( (fileSize/1000) * 10 ) / 10) + " KB " : fileSize + " B"
}

function comparePartner(a,b) {
    if (a.kanaName < b.kanaName)
        return -1;
    if (a.kanaName > b.kanaName)
        return 1;
    return 0;
}

function fullWidthNumConvert(fullWidthNum){
    return fullWidthNum.replace(/[\uFF10-\uFF19]/g, function(m) {
        return String.fromCharCode(m.charCodeAt(0) - 0xfee0);
    });
}

function numberValidator(value) {
    if (!value || value.trim().length === 0) {
        return false;
    } else {
        value = fullWidthNumConvert(value);
        value = value.replace(/，/g, ",");
        var pattern = /^\d+(,\d{3})*(\.\d+)?$/;
        var match = pattern.test(value);
        if(!match){
            return false;
        }
    }
    return true;
}

function addDaysToDate(date, days) {
    var newDate = new Date(date.getTime());
    newDate.setDate(date.getDate() + days);
    return newDate;
}

function addMonthsToDate(date, months) {
    var newDate = new Date(date.getTime());
    newDate.setMonth(date.getMonth() + months);
    return newDate;
}

function formatDate(date) {
    var year = date.getFullYear();
    var month = date.getMonth()+1;
    var day = date.getDate();

    if (day < 10) {
        day = '0' + day;
    }
    if (month < 10) {
        month = '0' + month;
    }

    var formattedDate = year + '-' + month + '-' + day;
    return formattedDate;
}