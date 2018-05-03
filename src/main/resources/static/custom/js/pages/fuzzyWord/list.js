
(function () {

    "use strict";

    console.log("Load fuzzyWord js");

    $.ajaxSetup({headers:{'X-CSRF-TOKEN':$("#csrf_token").attr("content")}});

    $(function () {
        // ajaxClick('lock','PUT');
        fuzzyWordDelete('trash','DELETE');
        initSort();
    });

    function fuzzyWordDelete(name, type) {
        $("span[name='"+name+"']").click(function () {
            $.ajax({
                type:type,
                url : "/user/fuzzyWord/" + $(this).attr("data") + "/delete",
                contentType: "application/json",
                dataType : 'json',
                success: function (result) {
                    console.log("fuzzyWordDelete result: ", result);
                    window.location.reload();
                },
                error: function (e) {
                    console.error("fuzzyWordDelete error: ", e);
                }
            })
        })
    }

    function ajaxClick(name,type){
        $("span[name='"+name+"']").click(function () {
            $.ajax({
                type:type,
                url:$(this).attr("data"),
                success:function (data) {
                    if(data){
                        alert(data);
                    }else{
                        window.location.reload();
                    }
                }
            })
        })
    }

    function initSort() {
        $("#wordList").tablesorter(
            {
                theme : 'default',
                headers: {
                    2: {
                        sorter: false
                    },
                    3: {
                        sorter: false
                    },
                    4: {
                        sorter: false
                    },
                },
                sortList: [[1,0], [0,0]]
            });
    }

})(jQuery);
