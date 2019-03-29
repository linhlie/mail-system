var extensionCommands = {
    ".pdf": "ms-word:ofv|u|",
    ".docx": "ms-word:ofv|u|",
    ".doc": "ms-word:ofv|u|",
    ".xls": "ms-excel:ofv|u|",
    ".xlsx": "ms-excel:ofv|u|",
    ".xlsm": "ms-excel:ofv|u|",
    ".ppt": "ms-powerpoint:ofv|u|",
    ".pptx": "ms-powerpoint:ofv|u|",
}

function isWindows() {
    var os = getOS();
    return (os === "Windows");
}

function showAttachFile(mailAttachmentDiv, files) {
    if (files.length > 0) {
        var filesInnerHTML = "";
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var fileExtension = getFileExtension(file.fileName);
            var command = extensionCommands[fileExtension];
            command = (isWindows() && !!command) ? command : "nope";
            var url = window.location.origin + "/download/" + encodeURIComponent(file.digest) + "/" + file.fileName;
            if (i > 0) {
                filesInnerHTML += "<br/>";
            }
            filesInnerHTML += ("<button type='button' class='btn btn-link download-link' data-filename='" + file.fileName + "' data-command='" + command + "' data-download='" + url + "'>" + file.fileName + "(" + getFileSizeString(file.size) + "); </button>")
        }
        mailAttachmentDiv.innerHTML = filesInnerHTML;
        setDownloadLinkClickListener();
    } else {
        mailAttachmentDiv.innerHTML = "添付ファイルなし";
    }
}

function setDownloadLinkClickListener() {
    $('button.download-link').off("click");
    $('button.download-link').click(function(event) {
        var command = $(this).attr("data-command");
        var href = $(this).attr("data-download");
        var fileName = $(this).attr('data-filename');
        if(command.startsWith("nope")) {
            alert("Not support features");
        } else {
            doDownload(command+href, fileName);
        }
    });

    $.contextMenu({
        selector: 'button.download-link',
        callback: function(key, options) {
            var m = "clicked: " + key;
            var command = $(this).attr("data-command");
            var href = $(this).attr("data-download");
            var fileName = $(this).attr('data-filename');
            if(key === "open") {
                if(command.startsWith("nope")) {
                    alert("Not support features");
                } else {
                    doDownload(command+href, fileName);
                }
            } else if (key === "save_as") {
                doDownload(href, fileName);
            }
        },
        items: {
            "open": {"name": "Open"},
            "save_as": {"name": "Save as"},
        }
    });
}

function doDownload(href, fileName){
    var a = document.createElement('A');
    a.href = href;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}