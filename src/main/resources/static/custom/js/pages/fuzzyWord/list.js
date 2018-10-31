
(function () {
    var divWordsId = "#divWords";
    var wordExclusionTableId = "wordExclusionTable";
    var addExclusionWordId = "addExclusionWord";
    var searchWordTxtId = "searchWordTxt";
    var btnSearchWord = "#btnSearchWord";

    var wordsExclusion = [];
    var groupWordCurrent = [];
    var currentGroupWord ;

    var addGroupStart = '<ul class="visible" style="padding-left: 30px; margin-top: 10px;">' +
        '<li class="treeview">' +
        '<span class="groupFuzzyWordName col-xs-8"><b>Group: ';

    var addGroupEnd = '</b> </span>'+
        '<span class="glyphicon glyphicon-trash fuzzyword-icon-trash col-xs-1.5"></span>' +
        '<span class="glyphicon glyphicon-play fuzzyword-icon-play pull-right"></span>' +
        '<ul class="visible groupFuzzyWord" style="padding-left: 30px; display: none;"></ul>' +
        '</li> </ul>';

    var addWordStart ='<li style="height: 26px; margin: 1px;"><span class="col-xs-6 word-value">';

    var addWordEnd ='</span> <input class="word-edit-text col-xs-6"></input>' +
        '<span class="glyphicon glyphicon-pencil word-icon-edit col-xs-2" data="edit"></span>' +
        '<span class="glyphicon glyphicon-trash word-icon-delete col-xs-4"></span></li>';

    var addWord ='<li style="height: 26px; margin: 1px;"> ' +
        '<span class="glyphicon glyphicon-plus-sign fuzzyword-icon-plus col-xs-6" data="add">&nbsp;add word</span> ' +
        '<input class="word-edit-text col-xs-6"></input>' +
        '<span class="glyphicon glyphicon-ok word-icon-save-add col-xs-2" data="edit"></span>' +
        '<span class="glyphicon glyphicon glyphicon-remove word-icon-cancel-add col-xs-4" style="color: red"></span>' +
        '</li>';

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
                showData(response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }

        getWords(onSuccess, onError);
    }

    function loadExclusion(group) {
        function onSuccess(response) {
            if(response && response.status){
                console.log(response.list);
                groupWordCurrent = response.listWord;
                showExclusionData(wordExclusionTableId, response.list);
            }
        }

        function onError(error) {
            console.log(error);
        }

        getExclusion(group, onSuccess, onError);
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
                    $.alert("Save Success");
                    loadExclusion(currentGroupWord);
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
                    deleteWordExclusion(rowData);
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
            if (data[i].groupWord != groupname){
                $(".groupFuzzyWord").last().append(addWord);
                $(divWordsId).append(addGroupStart + data[i].groupWord + addGroupEnd);
                $(".groupFuzzyWord").last().append(addWordStart+ data[i].word + addWordEnd);
                groupname = data[i].groupWord;
            } else{
                $(".groupFuzzyWord").last().append(addWordStart+ data[i].word + addWordEnd);
            }
        }
        if(data.length>0){
            $(".groupFuzzyWord").last().append(addWord);
        }
        addGroupWord();
        showGroup();
        deleteGroup();
        showExclusionGroup();
        addWordToGroup();
        cancelAddWordToGroup();
        saveAddWordToGroup();
        editWord();
        deleteword();
        displayAddExclusion(true);
        clearTableExclusionWord();
    }

    function showGroup() {
        $(".groupFuzzyWordName").click(function () {
            console.log($(this).text());
            $(this).siblings("ul").slideToggle("slow");
        })
    }

    function addGroupWord() {
        $("#addGroupWord").click(function () {
            console.log("add word");
        })
    }

    function deleteGroup() {
        $(".fuzzyword-icon-trash").click(function () {
            var text = $(this).siblings(".groupFuzzyWordName").text();
            var ulTag = $(this).parents('ul');
            var group = text.substring(7, text.length);
            if(group!=null){
                group = group.trim();
            }
            function onSuccess() {
                ulTag.remove();
            }
            function onError() {
                $.alert("の削除に失敗しました。");
            }
            $.confirm({
                title: '<b>【Delete Group Word】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            deleteGroupWord(group, onSuccess, onError);
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        })
    }

    function showExclusionGroup() {
        $(".fuzzyword-icon-play").click(function () {
            var text = $(this).siblings(".groupFuzzyWordName").text();
            text = text.substr(7, text.length);
            if(text!=null){
                text = text.trim();
            }
            currentGroupWord = text;
            loadExclusion(currentGroupWord);
        })
    }

    function addWordToGroup() {
        $(".fuzzyword-icon-plus").click(function () {
            var spanAdd = $(this);
            var inputWord = $(this).siblings("input");
            var spanSave = $(this).siblings(".word-icon-save-add");
            var spanCancel = $(this).siblings(".word-icon-cancel-add");

            spanAdd.css('display','none');
            inputWord.val("");
            inputWord.css('display','inline-block');
            spanSave.css('display','inline-block');
            spanCancel.css('display','inline-block');

        })
    }

    function saveAddWordToGroup() {
        $(".word-icon-save-add").click(function () {
            var spanSave = $(this);
            var inputWord = $(this).siblings("input");
            var spanAdd = $(this).siblings(".fuzzyword-icon-plus");
            var spanCancel = $(this).siblings(".word-icon-cancel-add");
            var liTag = $(this).parent("li");
            var ulTag = liTag.parent("ul");
            var spanGroup = ulTag.siblings(".groupFuzzyWordName");
            var word = inputWord.val();
            var text = spanGroup.text();
            var group = text.substring(7, text.length);
            if(word != null && word != ""){
                function onSuccess(response) {
                    if(response){
                        if(response.status){
                            spanAdd.css('display','inline-block');
                            inputWord.val("");
                            inputWord.css('display','none');
                            spanSave.css('display','none');
                            spanCancel.css('display','none');

                            ulTag.find('li:last').before(addWordStart+ word + addWordEnd);

                            $(".word-icon-edit").off('click');
                            $(".word-icon-delete").off('click');
                            editWord();
                            deleteword();
                        }else{
                            $.alert("Save Fail");
                        }
                    }
                }
                function onError(error) {
                    $.alert("Save Fail");
                    console.log(error);
                }

                addWordToGroupAPI({
                    word : word.trim(),
                    groupWord : group.trim()
                }, onSuccess, onError);
            }else{
                $.alert("Word invalid");
            }
        })
    }

    function cancelAddWordToGroup() {
        $(".word-icon-cancel-add").click(function () {
            var spanCancel = $(this);
            var inputWord = $(this).siblings("input");
            var spanSave = $(this).siblings(".word-icon-save-add");
            var spanAdd = $(this).siblings(".fuzzyword-icon-plus");

            spanAdd.css('display', 'inline-block');
            inputWord.val("");
            inputWord.css('display', 'none');
            spanSave.css('display', 'none');
            spanCancel.css('display', 'none');
        })
    }



    function editWord() {
        $(".word-icon-edit").click(function () {
            var spanWord = $(this).siblings(".word-value");
            var inputWord = $(this).siblings("input");
            var spanEdit = this;
            var flag = $(this).attr('data');
            if(flag == "edit"){

                spanWord.css('display','none');
                inputWord.val(spanWord.text());
                inputWord.css('display','inline-block');

                $(this).removeClass("glyphicon-pencil");
                $(this).addClass("glyphicon-ok");
                $(this).attr('data','save');
            }else{
                var oldWord = spanWord.text();
                var newWord = inputWord.val();
                if(newWord != "" && newWord != oldWord){
                    function onSuccess(response) {
                        if(response){
                            if(response.status){
                                spanWord.text(newWord);
                                inputWord.css('display','none');
                                spanWord.css('display','inline-block');

                                $(spanEdit).removeClass("glyphicon-ok");
                                $(spanEdit).addClass("glyphicon-pencil");
                                $(spanEdit).attr('data','edit');

                            }else{
                                $.alert("Save Fail");
                            }
                        }
                    }
                    function onError(error) {
                        $.alert("Save Fail");
                        console.log(error);
                    }

                    editWordAPI({
                        oldWord : oldWord,
                        newWord : newWord.trim()
                    }, onSuccess, onError);
                }else{
                    inputWord.css('display','none');
                    spanWord.css('display','inline-block');

                    $(spanEdit).removeClass("glyphicon-ok");
                    $(spanEdit).addClass("glyphicon-pencil");
                    $(spanEdit).attr('data','edit');
                }
            }
        })
    }

    function deleteword() {
        $(".word-icon-delete").click(function () {
            var word = $(this).siblings(".word-value").text();
            var liTag = $(this).parent('li');
            function onSuccess(response) {
                if(response){
                    console.log(response.msg);
                    if(response.msg == '0'){
                        liTag.parents('ul').remove();
                    }else{
                        liTag.remove();
                    }
                    clearTableExclusionWord();
                }
            }
            function onError(error) {
                $.alert("の削除に失敗しました。");
            }
            $.confirm({
                title: '<b>【Delete Word】</b>',
                titleClass: 'text-center',
                content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
                buttons: {
                    confirm: {
                        text: 'はい',
                        action: function(){
                            deleteWordInGroup({
                                oldWord : word
                            }, onSuccess, onError);
                        }
                    },
                    cancel: {
                        text: 'いいえ',
                        action: function(){}
                    },
                }
            });
        })
    }

    function addExclusionWord() {
        $("#"+addExclusionWordId).click(function () {
            console.log("add exclusion word");
            showNamePrompt("Add Exclusion Word", groupWordCurrent, saveExclusionWord);
        })
    }

    function deleteWordExclusion(fuzzyWord){
        function onSuccess() {
            loadExclusion(currentGroupWord)
        }
        function onError() {
            $.alert("の削除に失敗しました。");
        }
        $.confirm({
            title: '<b>【Delete FuzzyWord】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteFuzzyWord(fuzzyWord.id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
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

    function clearTableExclusionWord(){
        $("#" + wordExclusionTableId + "> tbody").html(replaceRow);
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
