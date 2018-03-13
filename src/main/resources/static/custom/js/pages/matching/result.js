
(function () {
    "use strict";
    var formId = '#matchingResultForm';
    var matchingResultStr;
    matchingResultStr = sessionStorage.getItem("matchingResultData");
    matchingResultStr = matchingResultStr || "null";
    var matchingResult = null;
    try {
        matchingResult  = JSON.parse(matchingResultStr);
    } catch (error) {
        console.error("[ERROR] parse matching result error: ", error);
    }
    console.log("window.matchingResultData: ", matchingResult);
})(jQuery);
