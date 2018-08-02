function showMailWithReplacedRange(accountId, messageId, replyId, range, matchRange, replaceType, sendTo, callback) {
    messageId = messageId.replace(/\+/g, '%2B');
    replyId = replyId.replace(/\+/g, '%2B');
    var url = "/user/matchingResult/editEmail?messageId=" + messageId + "&replyId=" + replyId + "&range=" + range + "&matchRange=" + matchRange + "&replaceType=" + replaceType;
    var type = sendTo === "moto" ? 4 : 5
    url = url + "&type=" + type;
    if(!!accountId){
        url = url + "&accountId=" + accountId;
    }
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var email;
            var accounts;
            if(data.status){
                email = data.mail;
                accounts = data.list;
            }
            if(typeof callback === "function"){
                callback(email, accounts);
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

function showReplyMail(accountId, messageId, callback) {
    messageId = messageId.replace(/\+/g, '%2B');
    var type = window.location.href.indexOf("extractSource") >= 0 ? 6 : 7;
    var url = "/user/matchingResult/replyEmail?messageId=" + messageId + "&type=" + type;
    if(!!accountId){
        url = url + "&accountId=" + accountId;
    }
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var email;
            var accounts;
            if (data.status) {
                email = data.mail;
                accounts = data.list;
            }
            if (typeof callback === "function") {
                callback(email, accounts);
            }
        },
        error: function (e) {
            console.error("showReplyMail ERROR : ", e);
            if (typeof callback === "function") {
                callback();
            }
        }
    });
}

function removeFile(fileId){
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/removeUploadedFile?fileId=" + fileId,
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("removeFile SUCCESS : ", data);
        },
        error: function (e) {
            console.error("removeFile ERROR : ", e);
        }
    });
}

function restartServer(){
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin/restart",
        data: "{}",
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            $.alert("Restarting server. It will take for a while. Please refresh (F5) browser for continue");
        },
        error: function (e) {
            $.alert("Restart server failed");
        }
    });
}