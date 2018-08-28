
(function () {
    var lastSelectedMailAccountId = "";
    var accountSelectorId = "#accountSelector";
    $(function () {
        loadMailData();
        loadUserData();
        $(accountSelectorId).change(function() {
            lastSelectedMailAccountId = this.value;
            loadMailData(this.value);
        });
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

    function loadUserData() {
        $('body').loadingModal('destroy');
        $('body').loadingModal({
            position: 'auto',
            text: 'ローディング...',
            color: '#fff',
            opacity: '0.7',
            backgroundColor: 'rgb(0,0,0)',
            animation: 'doubleBounce',
        });
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/user/dashboard/userStatistics",
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
        if(data && data.clickCount){
            pushDataToTable(data.clickCount, "clickCount");
        }
        if(data && data.sendPerClick){
            pushDataToTable(data.sendPerClick, "sendPerClick");
        }
    }
    
    function pushDataToTable(data, tableId) {
        for(var i = 0; i < data.length; i++) {
            var col   = (i%8) + 2;
            var row   = Math.floor(i/8) + 2;
            $("#" + tableId + " tr:nth-child(" + row + ") td:nth-child(" + col  + ")").html(data[i]);
        }
    }

})(jQuery);
