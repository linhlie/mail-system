
(function () {
    "use strict";
    var statisticTableId = 'statisticTable';
    var statisticResult = [];

    var statisticByDayId = '#statisticByDay';
    var statisticByHourId = '#statisticByHour';
    var statisticByDomainId = '#statisticByDomain';
    var statisticByWordId = '#statisticByWord';

    var SHOW_DETAIL_EMAIL_STATISTIC_KEY = "show-detail-email-statistic";
    var STATISTIC_CONDITION_DATA_KEY = "statistic-condition-data";
    var STATISTIC_BY_DAY_KEY = "statistic-by-day";
    var STATISTIC_BY_HOUR_KEY = "statistic-by-hour";
    var STATISTIC_BY_DOMAIN_KEY = "statistic-by-domain";
    var STATISTIC_BY_WORD_KEY = "statistic-by-word"


    var replaceHeaderStart = '<tr>';
    var statisticByDayHeader = '<th>日別</th>';
    var statisticByHourHeader =   '<th>時間別 </th>';
    var statisticByDomainHeader =   '<th>送信者別</th>';
    var statisticByWordHeader =    '<th>ワード別</th>';
    var countEmailHeader =   '<th>件数</th>';
    var openButtonHeader =   '<th class="fit" ></th>';

    var replaceBodyStart = '<tr role="row" class="hidden">';
    var statisticByDayBody = '<td name="emailStatistic" rowspan="1" colspan="1" data="date"><span></span></td>';
    var statisticByHourBody =   '<td name="emailStatistic" rowspan="1" colspan="1" data="hour"><span></span></td>';
    var statisticByDomainBody =   '<td name="emailStatistic" rowspan="1" colspan="1" data="domain"><span></span></td>';
    var statisticByWordBody =    '<td name="emailStatistic" rowspan="1" colspan="1" data="word"><span></span></td>';
    var countEmailBody =   '<td name="emailStatistic" rowspan="1" colspan="1" data="count"><span></span></td>';
    var openButtonBody =   '<td name="open-email-statistic" rowspan="1" colspan="1" data=""><button type="button" style="margin-left: 5px; margin-right: 5px; width: 50px;">開く</button></td>';


    $(function () {
        loadConditionFilter();
        onChangeConditionFilter();
        loadResultStatistic();
        initStickyHeader();
    });
    
    function enableResizeColumns() {
        enableResizeColums(statisticTableId);
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
    
    function updateData() {
        var statisticByDay = $(statisticByDayId).is(":checked");
        var statisticByHour = $(statisticByHourId).is(":checked");
        var statisticByDomain = $(statisticByDomainId).is(":checked");
        var statisticByWord = $(statisticByWordId).is(":checked");
        var replaceBodyHTML = replaceBodyStart;
        var replaceHeaderHTML = replaceHeaderStart;
        if(statisticByDay){
            replaceBodyHTML = replaceBodyHTML + statisticByDayBody;
            replaceHeaderHTML = replaceHeaderHTML + statisticByDayHeader;
        }

        if(statisticByHour){
            replaceBodyHTML = replaceBodyHTML + statisticByHourBody;
            replaceHeaderHTML = replaceHeaderHTML + statisticByHourHeader;
        }

        if(statisticByDomain){
            replaceBodyHTML = replaceBodyHTML + statisticByDomainBody
            replaceHeaderHTML = replaceHeaderHTML + statisticByDomainHeader;
        }

        if(statisticByWord){
            replaceBodyHTML = replaceBodyHTML + statisticByWordBody;
            replaceHeaderHTML = replaceHeaderHTML + statisticByWordHeader;
        }

        replaceBodyHTML= replaceBodyHTML + countEmailBody + openButtonBody + '</tr>';
        replaceHeaderHTML = replaceHeaderHTML + countEmailHeader + openButtonHeader + '</tr>';
        showResultData(statisticTableId, statisticResult,replaceHeaderHTML, replaceBodyHTML);
    }

    function showResultData(tableId, data,replaceHeader, replaceBody) {
        $("#" + tableId + "> thead").html(replaceHeader);
        removeAllRow(tableId, replaceBody);
        var statisticCounter = 0;
        if(data.length > 0){
            var html = replaceBody;
            for(var i = 0; i < data.length; i ++){
                html = html + addRowWithData(tableId, data[i], i);
                statisticCounter++;
            }
            $("#"+ tableId + "> tbody").html(html);
            setRowClickListener("open-email-statistic", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = statisticResult[index];
                if (rowData && rowData.listMessageId) {
                    var data = {
                        "listMessageId" : rowData.listMessageId,
                    };
                    sessionStorage.setItem(SHOW_DETAIL_EMAIL_STATISTIC_KEY, JSON.stringify(data));
                    var win = window.open('/user/showDetail', '_blank');
                    if (win) {
                        win.focus();
                    } else {
                        alert('Please allow popups for this website');
                    }
                }
            });
        }
        enableResizeColumns();
        updateTotalStatisticResult(statisticCounter);
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

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#"+ tableId + "> tbody").html(replaceHtml);
    }

    function updateTotalStatisticResult(total) {
        $('#totalStatisticResult').text("絞りこみの結果:" + total + "件")
    }

    function initStickyHeader() {
        $(".table-container-wrapper").scroll(function () {
            $(this).find("thead.sticky-header")
                .css({
                    "user-select": "none",
                    "position": "relative",
                    "z-index": "10",
                    "transform": "translate(0px, " + $(this).scrollTop() + "px)"
                });
        });
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

    function loadConditionFilter(){
        var statisticByDay = localStorage.getItem(STATISTIC_BY_DAY_KEY);
        var statisticByDayValue = typeof statisticByDay !== "string" ? false : !!JSON.parse(statisticByDay);
        var statisticByHour = localStorage.getItem(STATISTIC_BY_HOUR_KEY);
        var statisticByHourValue = typeof statisticByHour !== "string" ? false : !!JSON.parse(statisticByHour);
        var statisticByDomain = localStorage.getItem(STATISTIC_BY_DOMAIN_KEY);
        var statisticByDomainValue = typeof statisticByDomain !== "string" ? false : !!JSON.parse(statisticByDomain);
        var statisticByWord = localStorage.getItem(STATISTIC_BY_WORD_KEY);
        var statisticByWordValue = typeof statisticByWord !== "string" ? false : !!JSON.parse(statisticByWord);

        $(statisticByDayId).prop('checked', statisticByDayValue);
        $(statisticByHourId).prop('checked', statisticByHourValue);
        $(statisticByDomainId).prop('checked', statisticByDomainValue);
        $(statisticByWordId).prop('checked', statisticByWordValue);
    }

    function onChangeConditionFilter(){
        $(".filter-statistic").change(function() {
            var statisticByDay = $(statisticByDayId).is(":checked");
            var statisticByHour = $(statisticByHourId).is(":checked");
            var statisticByDomain = $(statisticByDomainId).is(":checked");
            var statisticByWord = $(statisticByWordId).is(":checked");

            localStorage.setItem(STATISTIC_BY_DAY_KEY, statisticByDay);
            localStorage.setItem(STATISTIC_BY_HOUR_KEY, statisticByHour);
            localStorage.setItem(STATISTIC_BY_DOMAIN_KEY, statisticByDomain);
            localStorage.setItem(STATISTIC_BY_WORD_KEY, statisticByWord);
            loadResultStatistic();
        });
    }

    function loadResultStatistic() {
        var statisticConditionStr = sessionStorage.getItem(STATISTIC_CONDITION_DATA_KEY);
        if(!statisticConditionStr || statisticConditionStr==null) return;

        var statisticConditionJson = JSON.parse(statisticConditionStr);
        if(statisticConditionJson && statisticConditionJson.statisticConditionData){
            replaceCondition(statisticConditionJson.statisticConditionData);
        }
        var statisticByDay = $(statisticByDayId).is(":checked");
        var statisticByHour = $(statisticByHourId).is(":checked");
        var statisticByDomain = $(statisticByDomainId).is(":checked");
        var statisticByWord = $(statisticByWordId).is(":checked");

        statisticConditionJson.statisticByDay = statisticByDay;
        statisticConditionJson.statisticByHour = statisticByHour;
        statisticConditionJson.statisticByDomain = statisticByDomain;
        statisticConditionJson.statisticByWord = statisticByWord;

        statisticConditionStr = JSON.stringify(statisticConditionJson);
        if(statisticConditionStr){
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
                    statisticResult = response.list;
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

            getStatisticMatchingResult(statisticConditionStr, onSuccess, onError);

        } else {
            updateData();
        }
    }
})(jQuery);
