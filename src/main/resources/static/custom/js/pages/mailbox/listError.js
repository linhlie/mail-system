
(function () {

    "use strict";



    $(function () {
        retryListener('retry');
    });

    function retryListener(name, type) {
        $("button[name='"+name+"']").click(function () {
            $('body').loadingModal({
                position: 'auto',
                text: '再試行中...',
                color: '#fff',
                opacity: '0.7',
                backgroundColor: 'rgb(0,0,0)',
                animation: 'doubleBounce',
            });
            var messageId = $(this).attr("data");
            messageId = messageId.replace(/\+/g, '%2B');
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: "/admin/retry?messageId=" + messageId,
                cache: false,
                timeout: 600000,
                success: function (data) {
                    $('body').loadingModal('hide');
                    window.location.reload();
                },
                error: function (e) {
                    console.error("[ERROR] submit error: ", e);
                    $('body').loadingModal('hide');
                    window.location.reload();
                }
            });
        })
    }

})(jQuery);
