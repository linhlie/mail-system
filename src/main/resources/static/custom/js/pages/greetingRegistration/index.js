
(function () {
    var formChange = false;

    var greetingTableId = "greetingTable";
    var greetingSettingId = "greetingSetting";
    var greetingTittleId = "greetingTittle";
    var accountSelectId = "sendMailAccountSelect";
    var activeId = "activeGreeting"
    var showGuideSettingId = "showGuideSetting";

    var firstRowHTML = '<tr class="hidden" role="row">' +
        '<td rowspan="1" colspan="1" data="title" style="cursor: pointer" name="editGreeting"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="greetingType"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="1" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="greetingType"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="2" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="greetingType"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="3" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="greetingType"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="4" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="greetingType"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="5" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="active" style="text-align: center;"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="true" src="/custom/img/dot.png"/></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id" name="removeGreeting"><button type="button">削除</button></td>' +
        '</tr>';
    
    var greetingData = [];
    var currentGreetingId = null;
    var currentEmailAccountId = null;
    
    $(function () {
        tinymce.init({
            force_br_newlines : true,
            force_p_newlines : false,
            forced_root_block : '',
            selector: '#' + greetingSettingId,
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 150,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor  colorpicker'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            fontsize_formats: '6pt 8pt 10pt 11pt 12pt 13pt 14pt 16pt 18pt 20pt 24pt 28pt 32pt 36pt 40pt 45pt 50pt',
            init_instance_callback: function (editor) {
                editor.on('Change', function (e) {

                });
            }
        });
        onChangeAccountListenner();
        onClickShowGuildeListenner();
        initStickyHeader();
        disableUpdateGreetingBtn(true);
        setButtonClickListener("greetingAdd", onAddGreetingListenner);
        setButtonClickListener("greetingUpdate", onUpdateGreetingListenner);
        setButtonClickListener("greetingClear", clearGreetingForm);
        disableUpdateGreetingBtn(true);
    });

    function onChangeAccountListenner() {
        $('#' + accountSelectId).change(function () {
            currentEmailAccountId = $(this).find("option:selected").val();
            loadGreetingData();
        })
    }

    function onClickShowGuildeListenner() {
        $('#'+showGuideSettingId).click(function () {
            showGuideModal();
        })
    }

    function loadGreetingData() {
        if (!currentEmailAccountId || currentEmailAccountId == null) return;
        console.log(currentEmailAccountId);

        function onSuccess(response) {
            if(response && response.status) {
                greetingData = response.list;
                showGreetingTable(greetingData);
            } else {
                $.alert("load greeting fail");
            }
        }

        function onError(response) {
            $.alert("load greeting fail");
        }
        getGreetingAPI(currentEmailAccountId, onSuccess, onError)
    }
    
    function showGreetingTable(data) {
        removeAllRow(greetingTableId, firstRowHTML);
        var html = firstRowHTML;
        if (data.length > 0) {
            for(var i = 0; i < data.length; i++) {
                html = html + addRowWithData(greetingTableId, data[i], i);
            }
            $("#" + greetingTableId + "> tbody").html(html);

            setRowClickListener("editGreeting", function () {
                var rowSelected = $(this);
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = greetingData[index];
                if (rowData && rowData.id) {
                    console.log(rowData);
                    selectedRow(rowSelected.closest('tr'));
                    setGreetingForm(rowData);
                }
            });

            setRowClickListener("removeGreeting", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = greetingData[index];
                console.log(rowData);
                if (rowData && rowData.id) {
                    console.log(rowData);
                    doDeleteGreeting(rowData.id);
                }
            });
        }
    }

    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function removeAllRow(tableId, replaceHtml) {
        $("#" + tableId + "> tbody").html(replaceHtml);
    }

    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
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
                    if(cellKey == "active"){
                        if(cellData==true){
                            cellNode.className = undefined;
                        }
                    }else{
                        cellNode.className = cellNode.getAttribute("data") == cellData ? undefined : cellNode.className;
                    }
                }
                if (cellNode.nodeName == "SPAN") {
                    var cellData = data[cellKey];
                    cellNode.textContent = cellData;
                }
            }
        }
        return row.outerHTML;
    }

    function setButtonClickListener(name, callback) {
        $("button[name='" + name + "']").off('click');
        $("button[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }

    function setGreeting(greeting) {
        greeting = greeting || "";
        var editor = tinymce.get(greetingSettingId);
        editor.setContent(greeting);
        editor.undoManager.clear();
        editor.undoManager.add();
    }
    
    function getGreeting() {
        var editor = tinymce.get(greetingSettingId);
        return editor.getContent();
    }

    function onAddGreetingListenner() {
        var form = getGreetingForm();
        if(!checkValidateForm(form)) return;

        console.log(form);
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("add greeting success");
                clearGreetingForm();
                loadGreetingData();
            } else {
                $.alert("add greeting fail");
            }
        }

        function onError(response) {
            $.alert("add greeting fail");
        }
        addGreetingAPI(form, onSuccess, onError)
    }

    function onUpdateGreetingListenner() {
        var form = getGreetingForm();
        if(!checkValidateForm(form)) return;
        if(currentGreetingId == null){
            $.alert("You must select greeting first");
        }
        form.id = currentGreetingId;
        console.log(form);
        function onSuccess(response) {
            if(response && response.status) {
                $.alert("update greeting success");
                clearGreetingForm();
                loadGreetingData();
            } else {
                $.alert("update greeting fail");
            }
        }

        function onError(response) {
            $.alert("update greeting fail");
        }
        updateGreetingAPI(form, onSuccess, onError)
    }

    function doDeleteGreeting(id) {
        function onSuccess() {
            clearGreetingForm();
            loadGreetingData();
        }
        function onError() {
            $.alert("Delete fail");
        }
        $.confirm({
            title: '<b>【Delete Greeting】</b>',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">Do you want delete it？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                        deleteGreetingAPI(id, onSuccess, onError);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }


    function disableUpdateGreetingBtn(disabled) {
        $("button[name='greetingUpdate']").prop("disabled", disabled);
    }

    function clearGreetingForm(){
        currentGreetingId = null;
        $('#' + greetingTittleId).val("");
        setGreeting("");
        $('input[name=greetingType]').prop("checked", false);
        $('#' + activeId).prop("checked", false);
        disableUpdateGreetingBtn(true);
    }

    function getGreetingForm() {
        var title = $('#' + greetingTittleId).val();
        var greeting = getGreeting();
        var greetingType = $('input[name=greetingType]:checked').val();
        var active = $('#' + activeId).is(":checked")

        return{
            title: title,
            greeting: greeting,
            greetingType: greetingType,
            emailAccountId: currentEmailAccountId,
            active: active
        }
    }

    function setGreetingForm(data) {
        currentGreetingId = data.id;
        $('#' + greetingTittleId).val(data.title);
        setGreeting(data.greeting);
        $('input[name=greetingType][value ='+ data.greetingType +']').prop("checked", true);
        $('#' + activeId).prop("checked", data.active);
        disableUpdateGreetingBtn(false);
    }

    function checkValidateForm(form) {
        if (!currentEmailAccountId || currentEmailAccountId == null){
            $.alert("You must select email account first");
            return false;
        }
        if(!form || form == null) return false;

        if(form.title==null){
            $.alert("you must fill greeting title");
            return false;
        }

        if(form.greetingType==null){
            $.alert("you must select  greeting type");
            return false;
        }
        return true;
    }

    function selectedRow(row) {
        row.addClass('highlight-selected').siblings().removeClass('highlight-selected');
    }

    function showGuideModal() {
        $('#dataModal').modal();

        $('#dataModalClose').off('click');
        $("#dataModalClose").click(function () {
            $('#dataModal').modal('hide');
        });
    }

})(jQuery);
