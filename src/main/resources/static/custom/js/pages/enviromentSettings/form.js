
(function () {

    "use strict";
    var formChange = false;

    $(function () {
        setFormChangeListener();
        setGoBackListener('backBtn');
        setOpenModalListener('openDirectoriesModal');
    });

    function setFormChangeListener() {
        $('#enviromentSettingsForm').change(function() {
            formChange = true;
        });
    }

    function getTreeOptions(data) {
        return {
            data: data,
            collapseIcon: "glyphicon glyphicon-folder-open",
            expandIcon: "glyphicon glyphicon-folder-close",
            emptyIcon: "glyphicon glyphicon-folder-close",
            levels: 1
        };
    }
    
    function setOpenModalListener(name) {
        $("button[name='"+name+"']").click(function () {
            loadDirectoryTree("/", showDirectoryTree, function (e) {
                console.log("loadDirectoryTree: error: ", e);
            });
        })
    }
    
    function loadDirectoryTree(path, success, error) {
        var url = "/admin/enviromentSettings/storagePath";
        url = !path ? url : url+ "?path=" + encodeURIComponent(path);
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (data) {
                if(data.status){
                    if(data.list && data.list.length > 0){
                        if(typeof success === "function"){
                            success(data.list);
                        }
                    } else {
                        if(typeof error === "function"){
                            error("data not found");
                        }
                    }
                } else {
                    if(typeof error === "function"){
                        error("failed");
                    }
                }
            },
            error: function (e) {
                console.error("loadDirectoryTree ERROR : ", e);
                if(typeof error === "function"){
                    error(e);
                }
            }
        });
    }

    function showDirectoryTree(data){
        $('#tree').treeview(getTreeOptions(data));
        $('#tree').on('nodeExpanded', function(event, data) {
            var node = $('#tree').treeview('getNode', data.nodeId);
            if(node.nodes.length == 0){
                loadDirectoryTree.call(this, node.path, function (data) {
                    node.nodes = data[0].nodes;
                    $('#tree').treeview('setInitialStates', node, 0);
                    $('#tree').treeview('expandNode', [ node.nodeId, { levels: 1, silent: true } ]);
                })
            }
        });
        $('#tree').on('nodeSelected', function(event, data) {
            console.log("nodeSelected: data: ", data);
        });
        $('#directoriesModal').modal();
    }

    function setGoBackListener(name){
        $("button[name='"+name+"']").click(function () {
            if(formChange) {
                var isBack = confirm("離れたいですか。");
                if(isBack){
                    goBack();
                }
            } else {
                goBack();
            }
        })
    }

    function goBack() {
        window.history.back();
    }

})(jQuery);
