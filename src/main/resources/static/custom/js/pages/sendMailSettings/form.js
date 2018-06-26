
(function () {

    $(function () {
        var cachedIncludeAttachmentStr = localStorage.getItem("includeAttachment");
        var cachedIncludeAttachment = typeof cachedIncludeAttachmentStr !== "string" ? false : !!JSON.parse(cachedIncludeAttachmentStr);
        $('#includeAttachment').prop('checked', cachedIncludeAttachment);
        var selectedSendMailAccountId = localStorage.getItem("selectedSendMailAccountId");
        if(selectedSendMailAccountId) {
            $("#sendMailAccountSelect option").each(function()
            {
                if($(this).val() == selectedSendMailAccountId)
                    $(this).prop('selected', true);
            });
        }
        $('#includeAttachment').change(function() {
            var includeAttachment = $(this).is(":checked");
            localStorage.setItem("includeAttachment", includeAttachment);
        });
        $('#sendMailAccountSelect').on('change', function() {
            localStorage.setItem("selectedSendMailAccountId", this.value);
        });
    });

})(jQuery);
