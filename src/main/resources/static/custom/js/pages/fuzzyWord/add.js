
(function () {

    "use strict";

    $("#fuzzyWordForm").submit(function(e){
        e.preventDefault();
        // var original = $("input[name='original']").val();
        // var associatedWord = $("input[name='associatedWord']").val();
        // var fuzzyType = $("select[name='fuzzyType']").val();
        // console.log("fuzzyWordForm submit prevented: " , $(this).attr("data") + "?" + $( this ).serialize());
        // $.ajax({
        //     type: "POST",
        //     url : $(this).attr("data") + "?" + $( this ).serialize(),
        //     contentType: "application/json",
        //     dataType : 'json',
        //     success: function (result) {
        //         console.log("fuzzyWordAdd result: ", result);
        //         // window.location.reload();
        //     },
        //     error: function (e) {
        //         console.error("fuzzyWordAdd error: ", e);
        //     }
        // })

        var fuzzyWordForm = {}
        fuzzyWordForm["original"] = $("input[name='original']").val();
        fuzzyWordForm["associatedWord"] = $("input[name='associatedWord']").val();
        fuzzyWordForm["fuzzyType"] = $("select[name='fuzzyType']").val();

        $("#btn-submit-fuzzy-word").prop("disabled", true);

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: $(this).attr("data"),
            data: JSON.stringify(fuzzyWordForm),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("SUCCESS : ", data);
                $("#btn-submit-fuzzy-word").prop("disabled", false);

            },
            error: function (e) {
                console.log("ERROR : ", e);
                $("#btn-submit-fuzzy-word").prop("disabled", false);

            }
        });
    });
})(jQuery);
