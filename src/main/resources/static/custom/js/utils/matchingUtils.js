
function getInforPartner(sentTo, callback){
    function onSuccess(response) {
        if(response) {
            if(response.status){
                if(typeof callback == 'function'){
                    callback(response.msg);
                }
            }else{
                if(typeof callback == 'function'){
                    callback();
                }
            }
        }
    }
    function onError() {
        if(typeof callback == 'function'){
            callback();
            alert('所属企業の情報の取得に失敗しました。');
        }
    }

    getInforPartnerAPI(sentTo, onSuccess, onError);
}

function getMoreInformationMailContent(sentTo, enginnerId, callback){
    function onSuccess(response) {
        if(response && response.status) {
            if(response.list && response.list.length > 0) {
                var data = response.list[0];
                if(typeof callback == 'function'){
                    callback(data);
                }
            } else {
                $.alert('所属企業の情報の取得に失敗しました。');
            }
        }
    }
    function onError() {
        if(typeof callback == 'function'){
            callback();
            alert('所属企業の情報の取得に失敗しました。');
        }
    }
    getInforPartnerAndEngineerIntroductionAPI(
        {
            "emailAddress": sentTo,
            "engineerId": enginnerId,
        },
        onSuccess,
        onError
    );
}