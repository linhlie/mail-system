
(function () {

    "use strict";

    $("#fuzzyWordForm").submit(function(e){
        e.preventDefault();

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
                $("#btn-submit-fuzzy-word").prop("disabled", false);
                if(data && data.status){
                    console.log("SUCCESS : ", data);
                    var original = fuzzyWordForm["original"];
                    if(typeof original === "string" && original.length > 0) {
                        window.location = '/user/fuzzyWord?search=' + original;
                    } else {
                        window.location = '/user/fuzzyWord';
                    }
                } else {
                    console.log("FAILED : ", data);
                    //TODO: failed add word
                }

            },
            error: function (e) {
                console.log("ERROR : ", e);
                $("#btn-submit-fuzzy-word").prop("disabled", false);

            }
        });
    });
})(jQuery);
