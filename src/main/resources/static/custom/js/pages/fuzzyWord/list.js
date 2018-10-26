
(function () {
    var divWordsId = "#divWords";
    var wordExclusionTableId = "wordExclusionTable";
    var addExclusionWordId = "addExclusionWord";
    var searchWordTxtId = "searchWordTxt";
    var btnSearchWord = "#btnSearchWord";

    var words = [];
    var wordsExclusion = [];
    var groupWordCurrent = [];

    var addGroupStart = '<ul class="visible" style="padding-left: 30px; margin-top: 10px;">' +
        '<li class="treeview">' +
        '<span class="groupFuzzyWordName col-xs-8"><b>Group: ';

    var addGroupEnd = '</b> </span>'+
        '<span class="glyphicon glyphicon-trash fuzzyword-icon-trash col-xs-1.5"></span>' +
        '<span class="glyphicon glyphicon-play fuzzyword-icon-play pull-right"></span>' +
        '<ul class="visible groupFuzzyWord" style="padding-left: 30px; display: none;"></ul>' +
        '</li> </ul>';

    var addWordStart ='<li><span class="col-xs-6 word-value">';

    var addWordEnd ='</span> <span class="glyphicon glyphicon-pencil word-icon-edit col-xs-2"></span>' +
        '<span class="glyphicon glyphicon-trash word-icon-delete col-xs-4"></span></li>';

    var replaceRow = '<tr role="row" class="hidden">' +
        '<td rowspan="1" colspan="1" data="word"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="wordExclusion"><span></span></td>' +
        '<td name="deleteWordExclusion" class="fit action" rowspan="1" colspan="1" data="id">' +
        '<button type="button">削除</button>' +
        '</td>' +
        '</tr>';

    var replaceRowNull = '<tr role="row">' +
        '<td rowspan="1" colspan="3" data="word"><span>Have not exclusion word</span></td>' +
        '</tr>';


    $(function () {
        loadListWord();
        addExclusionWord();
        setButtonClickListenter(btnSearchWord, searchWordOnClick);
    });

    function setButtonClickListenter(id, callback) {
        $(id).off('click');
        $(id).click(function () {
            if (typeof callback === "function") {
                callback();
            }
        });
    }

    function loadListWord() {
        function onSuccess(response) {
            if(response && response.status){
                console.log(response.list);
                words = response.list;
                showData(response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }

        getWords(onSuccess, onError);
    }

    function loadExclusion(word) {
        function onSuccess(response) {
            if(response && response.status){
                console.log(response.list);
                showExclusionData(wordExclusionTableId, response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }

        getExclusion(word, onSuccess, onError);
    }

    function searchWord(word) {
        function onSuccess(response) {
            if(response && response.status){
                console.log(response.list);
                showData(response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }

        searchWordAPI(word, onSuccess, onError);
    }


    function saveExclusionWord(fuzzyWord) {
        function onSuccess(response) {
            if(response){
                if(response.status){
                    console.log(response.list);
                    loadExclusion(fuzzyWord.word);
                    $.alert("Save Success");
                }else{
                    $.alert("Save Fail");
                }
            }
        }

        function onError(error) {
            $.alert("Save Fail");
            console.log(error);
        }
        console.log("add ",fuzzyWord);
        addFuzzyWord(fuzzyWord, onSuccess, onError);
    }

    function searchWordOnClick(){
        var wordSearch = $("#"+searchWordTxtId).val();
        if(wordSearch == null || wordSearch ==""){
            loadListWord();
        }else{
            searchWord(wordSearch);
        }
    }

    function showExclusionData(tableId, data) {
        wordsExclusion = data;
        removeAllRow(tableId, replaceRow);
        if (wordsExclusion.length > 0) {
            var html = replaceRow;
            for (var i = 0; i < wordsExclusion.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deleteWordExclusion", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = wordsExclusion[index];
                if (rowData && rowData.id) {
                    console.log(rowData);
                }
            });
        }else{
            $("#" + tableId + "> tbody").html(replaceRowNull);
        }
        displayAddExclusion(false);
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data-id", data.id);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells.item(i);
            var cellKey = cell.getAttribute("data");
            if (!cellKey) continue;
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if(cellNode.nodeName == "IMG") {
                    var cellData = data[cellKey];
                    cellNode.className = !!cellData ? undefined : cellNode.className;
                } else if (cellNode.nodeName == "SPAN") {
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
                }
            }
        }
        return row.outerHTML;
    }

    function showData(data) {
        if(data == null) return;
        var groupname = "";
        $(divWordsId).empty();
        for(var i=0;i<data.length;i++){
            if (data[i].group != groupname){
                $(divWordsId).append(addGroupStart + data[i].group + addGroupEnd);
                $(".groupFuzzyWord").last().append(addWordStart+ data[i].word + addWordEnd);
                groupname = data[i].group;
            } else{
                $(".groupFuzzyWord").last().append(addWordStart+ data[i].word + addWordEnd);
            }
        }
        showGroup();
        addWordToGroup();
        deleteGroup();
        showExclusionGroup();
        editWord();
        deleteword();
        displayAddExclusion(true);
    }

    function showGroup() {
        $(".groupFuzzyWordName").click(function () {
            console.log($(this).text());
            $(this).siblings("ul").slideToggle("slow");
        })
    }

    function addWordToGroup() {
        $(".fuzzyword-icon-plus").click(function () {
            var text = $(this).siblings(".groupFuzzyWordName").text();
            console.log(text);
        })
    }

    function deleteGroup() {
        $(".fuzzyword-icon-trash").click(function () {
            var text = $(this).siblings(".groupFuzzyWordName").text();
            console.log(text);
        })
    }

    function showExclusionGroup() {
        $(".fuzzyword-icon-play").click(function () {
            var text = $(this).siblings(".groupFuzzyWordName").text();
            text = text.substr(7, text.length);
            if(text!=null){
                text = text.trim();
            }
            groupWordCurrent = [];
            for(var i=0;i<words.length;i++){
                if(words[i].group == text){
                    groupWordCurrent.push(words[i]);
                }
            }
            loadExclusion(text);
        })
    }

    function editWord() {
        $(".word-icon-edit").click(function () {
            var text = $(this).siblings(".word-value").text();
            console.log(text);
        })
    }

    function deleteword() {
        $(".word-icon-delete").click(function () {
            var text = $(this).siblings(".word-value").text();
            console.log(text);
        })
    }

    function addExclusionWord() {
        $("#"+addExclusionWordId).click(function () {
            console.log("add exclusion word");
            showNamePrompt("Add Exclusion Word", groupWordCurrent, saveExclusionWord);
        })
    }

    function showNamePrompt(title, datalist, callback) {
        $('#dataModal').modal();
        $( '#dataModalTitle').text(title);
        $( '#word').val("");
        $( '#wordExclusion').val("");
        updateKeyList(datalist);
        setInputAutoComplete("dataModalName");
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var word = $( '#word').val();
            var wordExclusion = $( '#wordExclusion').val();
            if(typeof callback === "function"){
                if(word != null && word.trim()!="" && wordExclusion!=null && wordExclusion.trim() != ""){
                    var isValid = checkValidWord(datalist, word, wordExclusion);
                    if(!isValid){
                        showError("Word invalid");
                    }

                    if(isValid){
                        var fuzzyWord = {
                            word: word,
                            wordExclusion: wordExclusion
                        }
                        callback(fuzzyWord);
                        $('#dataModal').modal('hide');
                    }
                }else{
                    showError("Word invalid");
                }
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

    function displayAddExclusion(flag) {
        if(flag){
            $("#"+addExclusionWordId).css("display","none");
        }else{
            $("#"+addExclusionWordId).css("display","block");
        }
    }

    function updateKeyList(datalist) {
        datalist = datalist || [];
        $('#keylist').html('');
        for(var i = 0; i < datalist.length; i++){
            $('#keylist').append("<option value='" + datalist[i].word + "'>");
        }
    }

    function setInputAutoComplete(className) {
        $( "." + className ).off('click');
        $( "." + className ).off('mouseleave');
        $( "." + className ).on('click', function() {
            $(this).attr('placeholder',$(this).val());
            $(this).val('');
        });
        $( "." + className ).on('mouseleave', function() {
            if ($(this).val() == '') {
                $(this).val($(this).attr('placeholder'));
            }
        });
    }

    function checkValidWord(datalist, word, wordExclusion) {
        if(word.toUpperCase() == wordExclusion.toUpperCase()){
            return false;
        }
        for(var i=0;i<datalist.length;i++){
            if(datalist[i].word == word){
                return true;
            }
        }
        return false;
    }

    function showError(error) {
        $("#hasErrorModal").text(error);
    }


})(jQuery);
