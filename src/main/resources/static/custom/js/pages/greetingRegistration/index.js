
(function () {
    var formChange = false;

    var greetingDataCacheKey = "greetingData";
    var greetingTableId = "greetingTable";
    var greetingTittleId = "greetingTittle";

    var editingGreetingId = null;

    var firstRowHTML = '<tr class="hidden" role="row">' +
        '<td rowspan="1" colspan="1" data="title"><span></span></td>' +
        '<td rowspan="1" colspan="1" data="type"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="元" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="type"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="先" src="/custom/img/checkmark.png"/></td>' +
        '<td rowspan="1" colspan="1" data="type"><img class="hidden" style="padding: 5px; width:20px; height: 20px;" data="返" src="/custom/img/checkmark.png"/></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id"><button name="editGreetimg" type="button">修正</button></td>' +
        '<td class="fit action" rowspan="1" colspan="1" data="id"><button name="removeGreeting" type="button">削除</button></td>' +
        '</tr>';

    var defaultGreetingData = [
        {
            id: 1,
            title: "人材提案",
            greeting: "お世話になっております。<br/> ワールドリンク　システム営業部　〇〇です。<br/><br/> 頂いた案件に、人材をご提案させていただきます。",
            type: "元",
            created_at: new Date().getTime(),
        },
        {
            id: 2,
            title: "お世話にな",
            greeting: "ワールドリンク　システム営業部　〇〇です。<br/><br/>",
            type: "先",
            created_at: new Date().getTime(),
        },
        {
            id: 3,
            title: "ワールドリンク",
            greeting: "お世話になっております。<br/> ワールドリンク　システム営業部　〇〇です。<br/><br/>",
            type: "返",
            created_at: new Date().getTime(),
        },
        {
            id: 4,
            title: "ワールドリンク",
            greeting: "お世話になっております。<br/> ワールドリンク　システム営業部　〇〇です。<br/><br/>",
            type: "",
            created_at: new Date().getTime(),
        },
    ];
    var greetingData = [];

    $(function () {
        tinymce.init({
            selector: '#greetingSetting',
            language: 'ja',
            theme: 'modern',
            statusbar: false,
            height: 150,
            plugins: [
                'advlist autolink link image lists charmap preview hr anchor pagebreak',
                'searchreplace visualblocks visualchars code insertdatetime nonbreaking',
                'table contextmenu directionality template paste textcolor'
            ],
            menubar: 'edit view insert format table',
            toolbar: 'undo redo | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image',
            init_instance_callback: function (editor) {
                editor.on('Change', function (e) {
                    formChange = true;
                });
            }
        });
        localStorage.setItem(greetingDataCacheKey, JSON.stringify(defaultGreetingData));
        initStickyHeader();
        initGreetingTable();
    });

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
    
    function initGreetingTable() {
        loadGreetingData();
        pushGreetingData(greetingTableId);
    }

    function loadGreetingData() {
        var greetingDataInStr = localStorage.getItem(greetingDataCacheKey);
        greetingData = greetingDataInStr == null ? [] : JSON.parse(greetingDataInStr);
        greetingData = Array.isArray(greetingData) ? greetingData : [];
    }
    
    function pushGreetingData(tableId) {
        removeAllRow(tableId, firstRowHTML);
        var html = firstRowHTML;
        if (greetingData.length > 0) {
            for(var i = 0; i < greetingData.length; i++) {
                html = html + addRowWithData(tableId, greetingData[i], i);
            }
            $("#" + tableId + "> tbody").html(html);
            setButtonClickListener("editGreetimg", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                var rowData = greetingData[index];
                selectGreeting(rowData);
            });
            setButtonClickListener("removeGreeting", function () {
                var index = $(this).closest('tr')[0].getAttribute("data");
                removeGreeting(index);
            });
        }
    }

    function saveGreetingData() {
        localStorage.setItem(greetingDataCacheKey, JSON.stringify(greetingData));
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
                    cellNode.className = cellNode.getAttribute("data") === cellData ? undefined : cellNode.className;
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

    function selectGreeting(greeting) {
        editingGreetingId = greeting.id;
        setGreetingTitle(greeting.title);
        setGreeting(greeting.greeting);
        setGreetingType(greeting.type);
    }
    
    function removeGreeting(index) {
        var greeting = greetingData[index];
        if(greeting.id === editingGreetingId) {
            clearGreetingInput();
            editingGreetingId = null;
        }
        greetingData.splice(index, 1);
        saveGreetingData();
        pushGreetingData(greetingTableId);
    }

    function clearGreetingInput() {
        setGreetingTitle();
        setGreeting();
        setGreetingType();
    }
    
    function setGreetingTitle(title) {
        title = title || "";
        $('#' + greetingTittleId).val(title);
    }
    
    function getGreetingTittle() {
        var title = $('#' + greetingTittleId).val();
        title = title || "";
        return title;
    }
    
    function setGreeting(greeting) {
        greeting = greeting || "";
    }
    
    function getGreeting() {
        
    }
    
    function setGreetingType(type) {
        type = type || "";
        $("#greetingType1").prop("checked", (type==="元"));
        $("#greetingType2").prop("checked", (type==="先"));
        $("#greetingType3").prop("checked", (type==="返"));
    }
    
    function getGreetingType() {
        var type = $('input[name=greetingType]:checked').val();
        type = type || "";
        return type;
    }

})(jQuery);
