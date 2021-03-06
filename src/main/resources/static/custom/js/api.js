
function composeEmailAPI(url, onSuccess, onError) {
    _get(url, onSuccess, onError);
}

function showMailReSendHistories(id, callback){
    var url = "/user/sendMailHistory/getFileAttach/" + id;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var files;
            if (data.status) {
                files = data.list;
            }
            if (typeof callback === "function") {
                callback(files);
            }
        },
        error: function (e) {
            console.error("getDetailMailHistory ERROR : ", e);
            if (typeof callback === "function") {
                callback();
            }
        }
    });
}

function showFileAttachEmailInbox(messageId, callback){
    messageId = messageId.replace(/\+/g, '%2B');
    var url = "/user/mailbox/getFileAttach?messageId=" + messageId;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var files;
            if (data.status) {
                files = data.list;
            }
            if (typeof callback === "function") {
                callback(files);
            }
        },
        error: function (e) {
            console.error("getFileAttachEmailInbox ERROR : ", e);
            if (typeof callback === "function") {
                callback();
            }
        }
    });
}

function getDetailMailHistory(id, callback){
    var url = "/user/sendMailHistory/detail/" + id;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var email;
            var accounts;
            var files;
            if (data.status) {
                email = data.sentMailHistory;
                accounts = data.list;
                files = data.listFile;
            }
            if (typeof callback === "function") {
                callback(email, accounts, files);
            }
        },
        error: function (e) {
            console.error("getDetailMailHistory ERROR : ", e);
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

function emptyTrashBox(success, error) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/admin/trashbox/empty",
        cache: false,
        timeout: 600000,
        success: success,
        error: error
    });
}

function moveToInbox(msgIds, onSuccess, onError){
    var data = {
        msgIds: msgIds
    };
    var url = "/admin/trashbox/moveToInbox";
    _post(url, data, onSuccess, onError);
}

function deleteFromTrashBox(msgIds, onSuccess, onError){
    var data = {
        msgIds: msgIds
    };
    var url = "/admin/trashbox/delete";
    _post(url, data, onSuccess, onError);
}

function deleteFromInBox(msgIds, onSuccess, onError){
    var data = {
        msgIds: msgIds
    };
    var url = "/admin/mailbox/deleteFromInbox";
    _post(url, data, onSuccess, onError);
}

function deleteFromErrorBox(msgIds, onSuccess, onError){
    var data = {
        msgIds: msgIds
    };
    var url = "/admin/mailbox/deleteFromErrorbox";
    _post(url, data, onSuccess, onError);
}

function filterInbox(data, onSuccess, onError){
    var url = "/user/inbox/filter";
    _post(url, data, onSuccess, onError);
}

function addPartner(data, onSuccess, onError) {
    var url = "/expansion/businessPartner/add";
    _post(url, data, onSuccess, onError);
}

function updatePartner(id, data, onSuccess, onError) {
    var url = "/expansion/businessPartner/update/" + id;
    _post(url, data, onSuccess, onError);
}

function getBusinessPartners(onSuccess, onError) {
    var url = "/expansion/businessPartner/list";
    _get(url, onSuccess, onError);
}

function getBusinessPartnerGroup(partnerId, onSuccess, onError) {
    var url = "/expansion/businessPartner/group/list/" + partnerId;
    _get(url, onSuccess, onError);
}

function deletePartner(id, onSuccess, onError) {
    var url = "/expansion/businessPartner/delete/" + id;
    _delete(url, onSuccess, onError);
}

function getDomainUnregisters(onSuccess, onError) {
    var url = "/expansion/domain/list";
    _get(url, onSuccess, onError);
}

function deleteDomain(id, onSuccess, onError) {
    var url = "/expansion/domain/delete/" + id;
    _delete(url, onSuccess, onError);
}

function avoidRegisterDomain(id, onSuccess, onError) {
    var url = "/expansion/domain/avoidRegister/" + id;
    _delete(url, onSuccess, onError);
}

function saveDomainAvoidRegister(data, onSuccess, onError) {
    var url = "/expansion/domainAvoidRegister/update";
    _post(url, data, onSuccess, onError);
}

function getDomainAvoidRegister(onSuccess, onError) {
    var url = "/expansion/domainAvoidRegister/list";
    _get(url, onSuccess, onError);
}

function getEngineers(data, onSuccess, onError) {
    var url = "/expansion/engineer/list";
    _post(url, data, onSuccess, onError);
}

function getEngineersToMatching(data, onSuccess, onError) {
    var url = "/user/engineerMatching/list";
    _post(url, data, onSuccess, onError);
}

function getEngineer(id, onSuccess, onError) {
    var url = "/expansion/engineer/info/" + id;
    _get(url, onSuccess, onError);
}

function addEngineer(data, onSuccess, onError) {
    var url = "/expansion/engineer/add";
    _post(url, data, onSuccess, onError);
}

function updateEngineer(id, data, onSuccess, onError) {
    var url = "/expansion/engineer/update/" + id;
    _post(url, data, onSuccess, onError);
}

function deleteEngineer(id, onSuccess, onError) {
    var url = "/expansion/engineer/delete/" + id;
    _delete(url, onSuccess, onError);
}

function getBusinessPartnersNotGood(engineerId, onSuccess, onError) {
    var url = "/expansion/engineer/partnerNotGood/list/" + engineerId;
    _get(url, onSuccess, onError);
}

function getPartnerExport(includeHeader) {
    includeHeader = !!includeHeader;
    var url = "/expansion/exportCSV?type=partner&&header=" + includeHeader;
    _doDownload(url);
}

function getPartnerGroupExport(includeHeader) {
    includeHeader = !!includeHeader;
    var url = "/expansion/exportCSV?type=groupPartner&&header=" + includeHeader;
    _doDownload(url);
}

function getEngineerExport(includeHeader) {
    includeHeader = !!includeHeader;
    var url = "/expansion/exportCSV?type=engineer&&header=" + includeHeader;
    _doDownload(url);
}

function getPeopleInChargePartnerExport(includeHeader) {
    includeHeader = !!includeHeader;
    var url = "/expansion/exportCSV?type=peopleInChargePartner&&header=" + includeHeader;
    _doDownload(url);
}

function forceFetchMail(onSuccess, onError) {
    var url ="/user/dashboard/forceFetchMail";
    _get(url, onSuccess, onError);
}

function getGreetingAPI(emailAccountId, onSuccess, onError) {
    var url ="/user/greetingRegistration/"+emailAccountId;
    _get(url, onSuccess, onError);
}

function addGreetingAPI(data, onSuccess, onError) {
    var url ="/user/greetingRegistration/add";
    _post(url, data, onSuccess, onError);
}

function updateGreetingAPI(data, onSuccess, onError) {
    var url ="/user/greetingRegistration/update";
    _post(url, data, onSuccess, onError);
}

function deleteGreetingAPI(id, onSuccess, onError) {
    var url ="/user/greetingRegistration/delete/"+id;
    _delete(url, onSuccess, onError);
}

function getBulletinBoardAPI(onSuccess, onError) {
    var url = "/user/dashboard/getBulletinBoard";
    _get(url, onSuccess, onError);
}

function getBulletinPermission(bulletinBoardId, onSuccess, onError) {
    var url = "/user/dashboard/getBulletinPermission/"+ bulletinBoardId;
    _get(url, onSuccess, onError);
}

function  changeBulletinPermision(data, onSuccess, onError) {
    var url = "/user/dashboard/changeBulletinPermission";
    _post(url, data, onSuccess, onError);
}

function checkPermissionEdit(bulletinBoardId, onSuccess, onError) {
    var url = "/user/dashboard/checkPermissionEdit/"+ bulletinBoardId;
    _get(url, onSuccess, onError);
}

function checkPermissionDelete(bulletinBoardId, onSuccess, onError) {
    var url = "/user/dashboard/checkPermissionDelete/"+ bulletinBoardId;
    _get(url, onSuccess, onError);
}

function getMailDataAPI(accountId, onSuccess, onError) {
    var url = "/user/dashboard/mailStatistics";
    if(accountId && accountId.length > 0) {
        url = url + "?accountId=" + accountId;
    }
    _get(url, onSuccess, onError);
}

function getWords(onSuccess, onError) {
    var url = "/user/fuzzyWord/getListWord";
    _get(url, onSuccess, onError);
}

function getExclusion(groupWord, onSuccess, onError) {
    var url = "/user/fuzzyWord/getExclusion";
    _postString(url, groupWord, onSuccess, onError);
}

function addFuzzyWord(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/addFuzzyWord";
    _post(url, data, onSuccess, onError);
}

function editFuzzyWord(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/editFuzzyWord";
    _post(url, data, onSuccess, onError);
}

function deleteFuzzyWord(id, onSuccess, onError) {
    var url = "/user/fuzzyWord/deleteFuzzyWord/" + id;
    _delete(url, onSuccess, onError);
}

function editGroupWord(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/editGroupWord";
    _post(url, data, onSuccess, onError);
}

function deleteGroupWord(group, onSuccess, onError) {
    var url = "/user/fuzzyWord/deleteGroupWord/" + group;
    _delete(url, onSuccess, onError);
}

function deleteWordInGroup(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/deleteWordInGroup";
    _post(url, data, onSuccess, onError);
}

function editWordAPI(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/editWord";
    _post(url, data, onSuccess, onError);
}

function addWordToGroupAPI(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/addWordToGroup";
    _post(url, data, onSuccess, onError);
}

function addListWord(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/addListWord";
    _post(url, data, onSuccess, onError);
}

function searchWordAPI(data, onSuccess, onError) {
    var url = "/user/fuzzyWord/searchWord";
    _postString(url, data, onSuccess, onError);
}

function saveBulletinBoard(bulletin, onSuccess, onError){
    var url = "/user/dashboard/saveBulletin";
    _post(url, bulletin, onSuccess, onError);
}

function deleteBulletinBoard(id, onSuccess, onError) {
    var url = "/user/dashboard/deleteBulletin/" + id;
    _delete(url, onSuccess, onError);
}

function updateBulletinBoardPosition(data, onSuccess, onError){
    var url = "/user/dashboard/updateBulletinPosition";
    _post(url, data, onSuccess, onError);
}

function importPartners(data, includeHeader, deleteOld, onSuccess, onError) {
    var url = '/expansion/importPartner?header=' + includeHeader + "&deleteOld=" + deleteOld;
    _import(url, data, onSuccess, onError);
}

function importEngineers(data, includeHeader, deleteOld, onSuccess, onError) {
    var url = '/expansion/importEngineer?header=' + includeHeader + "&deleteOld=" + deleteOld;
    _import(url, data, onSuccess, onError);
}

function importPeopleInChargePartners(data, includeHeader, deleteOld, onSuccess, onError) {
    var url = '/expansion/importPeopleInChargePartners?header=' + includeHeader + "&deleteOld=" + deleteOld;
    _import(url, data, onSuccess, onError);
}

function importPartnerGroups(data, includeHeader, deleteOld, onSuccess, onError) {
    var url = '/expansion/importPartnerGroup?header=' + includeHeader + "&deleteOld=" + deleteOld;
    _import(url, data, onSuccess, onError);
}

function getBusinessPartnersForPeopleInCharge(onSuccess, onError) {
    var url = "/expansion/peopleInChargePartner/getPartners";
    _get(url, onSuccess, onError);
}


function getPeopleInChargePartners(partnerId, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartner/getPeopleInChargePartners/" + partnerId;
    _get(url, onSuccess, onError);
}

function getDetailPeopleInChargePartner(id, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartner/info/" + id;
    _get(url, onSuccess, onError);
}

function addPeopleInChargePartner(data, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartner/add";
    _post(url, data, onSuccess, onError);
}

function updatePeopleInChargePartner(data, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartner/edit";
    _post(url, data, onSuccess, onError);
}

function deletePeopleInChargePartner(id, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartner/delete/" + id;
    _delete(url, onSuccess, onError);
}

function getPeopleInChargePartnerUnregisters(onSuccess, onError) {
    var url = "/expansion/peopleInChargePartnerUnregister/list";
    _get(url, onSuccess, onError);
}

function deletePeopleInChargePartnerUnregister(id, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartnerUnregister/delete/" + id;
    _delete(url, onSuccess, onError);
}

function avoidRegisterPeopleInChargeUnregister(id, onSuccess, onError) {
    var url = "/expansion/peopleInChargePartnerUnregister/avoidRegister/" + id;
    _delete(url, onSuccess, onError);
}

function getEmailsAvoidRegisterPeopleInCharge(onSuccess, onError) {
    var url = "/expansion/emailsAvoidRegisterPeopleInCharge/list";
    _get(url, onSuccess, onError);
}

function saveEmailAvoidRegister(data, onSuccess, onError) {
    var url = "/expansion/emailsAvoidRegisterPeopleInCharge/update";
    _post(url, data, onSuccess, onError);
}

function getMailAccountsAPI(onSuccess, onError) {
    var url = "/user/sendReplyRecommendationMail/getMailAccounts";
    _get(url, onSuccess, onError);
}

function deletenumberTreatment(onSuccess, onError) {
    var url = "/user/numberTreatment/delete";
    _delete(url, onSuccess, onError);
}

function getMatchingConditionNotification(onSuccess, onError) {
    var url = "/user/matchingSettings/matchingConditionNotification";
    _get(url, onSuccess, onError);
}

function getEngineerMatchingConditionNotification(onSuccess, onError) {
    var url = "/user/engineerMatching/matchingConditionNotification";
    _get(url, onSuccess, onError);
}

function getStatisticConditionNotification(onSuccess, onError) {
    var url = "/user/emailStatistic/statisticConditionNotification";
    _get(url, onSuccess, onError);
}

function addConditionNotification(data, onSuccess, onError) {
    var url = "/user/conditionNotification/add";
    _post(url, data, onSuccess, onError);
}

function updateConditionNotification(data, onSuccess, onError) {
    var url = "/user/conditionNotification/update";
    _post(url, data, onSuccess, onError);
}

function showMoreConditionNotification(data, onSuccess, onError) {
    var url = "/user/conditionNotification/showMore";
    _post(url, data, onSuccess, onError);
}

function getEmailAddressGroup(groupName, onSuccess, onError) {
    var url = "";
    if(groupName){
        url = "/user/emailGroupSetting/getListEmailAddressGroup?groupName="+groupName;
    }else{
        url = "/user/emailGroupSetting/getListEmailAddressGroup";
    }
    _get(url, onSuccess, onError);
}

function getEmailAddressAndGroup(groupName, onSuccess, onError) {
    var url = "/user/emailGroupSetting/getListEmailAddressAndGroup";
    _get(url, onSuccess, onError);
}

function addEmailAddressGroup(data, onSuccess, onError) {
    var url = "/user/emailGroupSetting/addEmailAddressGroup";
    _post(url, data, onSuccess, onError);
}

function updateEmailAddressGroup(data, onSuccess, onError) {
    var url = "/user/emailGroupSetting/editEmailAddressGroup";
    _post(url, data, onSuccess, onError);
}

function deleteEmailAddressGroup(id, onSuccess, onError) {
    var url = "/user/emailGroupSetting/deleteEmailAddressGroup/" + id;
    _delete(url, onSuccess, onError);
}

function getEmailAddressList(id, search, onSuccess, onError) {
    var url = "";
    if(search){
        url = "/user/emailGroupSetting/getListEmailAddressList?id=" +id +"&search="+search;
    }else{
        url = "/user/emailGroupSetting/getListEmailAddressList?id=" +id;
    }
    _get(url, onSuccess, onError);
}

function addEmailAddressToGroup(data, onSuccess, onError) {
    var url = "/user/emailGroupSetting/addEmailAddressToList";
    _post(url, data, onSuccess, onError);
}

function deleteEmailAddressInGroup(id, onSuccess, onError) {
    var url = "/user/emailGroupSetting/deleteEmailAddressInGroup/" + id;
    _delete(url, onSuccess, onError);
}

function getEmailSenders(onSuccess, onError) {
    var url = "/user/schedulerSendEmail/getListEmailSender";
    _get(url, onSuccess, onError);
}

function getEmailReceivers(data, onSuccess, onError) {
    var url = "/user/schedulerSendEmail/getEmailReceivers";
    _post(url, data, onSuccess, onError);
}

function createSchedulerSendEmail(data, onSuccess, onError) {
    var url = "/user/schedulerSendEmail/createSchedulerSendEmail";
    _post(url, data, onSuccess, onError);
}

function getSchedulerData(onSuccess, onError) {
    var url = "/user/schedulerSendEmail/getListSchedulerData";
    _get(url, onSuccess, onError);
}

function getSchedulerEmail(id, onSuccess, onError) {
    var url = "/user/schedulerSendEmail/getSchedulerData/"+id;
    _get(url, onSuccess, onError);
}

function deleteSchedulerEmail(id, onSuccess, onError) {
    var url = "/user/schedulerSendEmail/deleteSchedulerSendEmail/" + id;
    _delete(url, onSuccess, onError);
}

function changeStatusSchedulerEmail(data, onSuccess, onError) {
    var url = "/user/schedulerSendEmail/changeStatusSchedulerEmail";
    _post(url, data, onSuccess, onError);
}

function getConditionSaved(onSuccess, onError) {
    var url = "/user/extractConditionManagement/getListConditionSaved";
    _get(url, onSuccess, onError);
}

function getAllConditionSaved(conditionType,onSuccess,onError) {
    var url = "/user/extractConditionManagement/get?conditionType="+conditionType;
    _get(url, onSuccess, onError);
}

function addConditionSaved(data, onSuccess, onError) {
    var url = "/user/extractConditionManagement/add";
    _post(url, data, onSuccess, onError);
}

function deleteConditionSaved(id, onSuccess, onError) {
    var url = "/user/extractConditionManagement/delete/" + id;
    _delete(url, onSuccess, onError);
}

function getEngineerMatchingResult(data, onSuccess, onError) {
    var url = "/user/emailMatchingEngineer/submitForm";
    _postString(url, data, onSuccess, onError);
}

function getEmailMatchingResult(data, onSuccess, onError) {
    var url = "/user/matchingSettings/submitForm";
    _postString(url, data, onSuccess, onError);
}

function getStatisticMatchingResult(data, onSuccess, onError) {
    var url = "/user/emailStatistic/submitForm";
    _postString(url, data, onSuccess, onError);
}

function _doDownload(href){
    var a = document.createElement('A');
    a.href = href;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

function _post(url, data, onSuccess, onError) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: url,
        data: JSON.stringify(data),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: onSuccess,
        error: onError
    });
}

function _get(url, onSuccess, onError) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        cache: false,
        timeout: 600000,
        success: onSuccess,
        error: onError
    });
}

function _postString(url, data, onSuccess, onError) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: url,
        data: data,
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: onSuccess,
        error: onError
    });
}

function _import(url, data, onSuccess, onError) {
    $.ajax({
        url: url,
        type: 'POST',
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        success: onSuccess,
        error: onError
    });
}

function _delete(url, onSuccess, onError) {
    $.ajax({
        type: "DELETE",
        url: url,
        success: onSuccess,
        error: onError
    });
}