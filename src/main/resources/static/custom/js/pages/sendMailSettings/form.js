
(function () {

    $(function () {
        var cachedIncludeAttachmentStr = localStorage.getItem("includeAttachment");
        var cachedIncludeAttachment = typeof cachedIncludeAttachmentStr !== "string" ? false : !!JSON.parse(cachedIncludeAttachmentStr);
        $('#includeAttachment').prop('checked', cachedIncludeAttachment);
        $('#includeAttachment').change(function() {
            var includeAttachment = $(this).is(":checked");
            localStorage.setItem("includeAttachment", includeAttachment);
        });
    });

})(jQuery);
