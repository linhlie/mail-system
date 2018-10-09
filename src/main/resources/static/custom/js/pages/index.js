
(function () {
    var lastSelectedMailAccountId = "";
    var lastSelectedUserAccountId = "";
    var accountSelectorId = "#accountSelector";
    var userSelectorId = "#userSelector";
    $(function () {
        loadMailData();
        loadUserData();
        $(accountSelectorId).change(function() {
            lastSelectedMailAccountId = this.value;
            loadMailData(this.value);
        });
        $(userSelectorId).change(function() {
            lastSelectedUserAccountId = this.value;
            loadUserData(this.value);
        });
        setButtonClickListenter("#forceFetchMailBtn", doForceFetchMail);
    });
    
    function loadMailData(accountId) {
        $('body').loadingModal('destroy');
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        var url = "/user/dashboard/mailStatistics";
        if(accountId && accountId.length > 0) {
            url = url + "?accountId=" + accountId;
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    pushMailData(data);
                } else {
                    console.error("[ERROR] dashboard load data failed: ");
                }
            },
            error: function (e) {
                $('body').loadingModal('hide');
                console.error("[ERROR] dashboard load data error: ", e);
            }
        });
    }

    function loadUserData(accountId) {
        $('body').loadingModal('destroy');
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        var url = "/user/dashboard/userStatistics";
        if(accountId && accountId.length > 0) {
            url = url + "?accountId=" + accountId;
        }
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('body').loadingModal('hide');
                if (data && data.status) {
                    console.log("[LOG] dashboard load data  success: ", data);
                    pushUserData(data);
                } else {
                    console.error("[ERROR] dashboard load data failed: ");
                }
            },
            error: function (e) {
                $('body').loadingModal('hide');
                console.error("[ERROR] dashboard load data error: ", e);
            }
        });
    }
    
    function pushMailData(data) {
        data && data.hasSystemError ? $("#hasSystemError").show() : $("#hasSystemError").hide();
        if(data && data.emailAccounts) {
            updateMailAcountSelector(data.emailAccounts)
        }
        if(data && data.latestReceive){
            $("#latestReceive").text(data.latestReceive);
        }
        if(data && data.checkMailInterval){
            $("#checkMailInterval").text(data.checkMailInterval + "分間隔で新着メール受信中");
        }
        if(data && data.receiveMailNumber){
            pushDataToTable(data.receiveMailNumber, "receiveMailNumber");
        }
    }

    function updateMailAcountSelector(accounts) {
        accounts = accounts || [];
        $(accountSelectorId).empty();
        $(accountSelectorId).append($('<option>', {
            selected: lastSelectedMailAccountId === "",
            value: "",
            text : "全ての受信アカウント",
        }));
        $.each(accounts, function (i, item) {
            $(accountSelectorId).append($('<option>', {
                value: item.id,
                text : item.account,
                selected: (item.id.toString() === lastSelectedMailAccountId)
            }));
        });
    }
    
    function pushUserData(data) {
        if(data && data.users) {
            updateUserSelector(data.users)
        }
        if(data && data.clickCount){
            pushDataToTable(data.clickCount, "clickCount");
        }
        if(data && data.sendPerClick){
            pushDataToTable(data.sendPerClick, "sendPerClick");
        }
        if(data && data.clickEmailMatchingEngineerCount){
            pushDataToTable(data.clickEmailMatchingEngineerCount, "clickEmailMatchingEngineerCount");
        }
        if(data && data.sendMailEmailMatchingEngineerClick){
            pushDataToTable(data.sendMailEmailMatchingEngineerClick, "sendMailEmailMatchingEngineerClick");
        }
    }
    
    function pushDataToTable(data, tableId) {
        for(var i = 0; i < data.length; i++) {
            var col   = (i%8) + 2;
            var row   = Math.floor(i/8) + 2;
            $("#" + tableId + " tr:nth-child(" + row + ") td:nth-child(" + col  + ")").html(data[i]);
        }
    }
    
    function updateUserSelector(users) {
        users = users || [];
        $(userSelectorId).empty();
        $(userSelectorId).append($('<option>', {
            selected: lastSelectedUserAccountId === "",
            value: "",
            text : "全てのユーザー",
        }));
        $.each(users, function (i, item) {
            $(userSelectorId).append($('<option>', {
                value: item.id,
                text : item.name ? item.name : item.userName,
                selected: (item.id.toString() === lastSelectedUserAccountId)
            }));
        });
    }
    
    function doForceFetchMail() {
        function onSuccess(response) {
            hideloading();
            if(response && response.status) {
                loadMailData(lastSelectedMailAccountId);
                loadUserData(lastSelectedUserAccountId);
            } else {
                $.alert("受信に失敗しました");
            }
        }

        function onError(response) {
            hideloading();
            $.alert("受信に失敗しました");
        }
        showFetchMailLoading();
        forceFetchMail(onSuccess, onError);
    }

    function showFetchMailLoading() {
        showLoading("最新のメールを受信中");
    }

    function showLoading(message) {
        hideloading();
        $('body').loadingModal({
            position: 'auto',
            text: message,
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
    }

    function hideloading() {
        $('body').loadingModal('hide');
        $('body').loadingModal('destroy');
    }

})(jQuery);
