
(function () {

    "use strict";
    var formChange = false;
    var selectedPath;

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
            levels: 10,
            customize: function(treeItem, node){
                // Add button
                var html = '<a class="btn btn-default" style="float: right; padding: 0; background-color: transparent; border: none;">' +
                        '<i class="icon add-node-icon glyphicon glyphicon-plus"></i>' +
                        '</a>';
                var tag = $(html);
                tag.attr("data-nodeid", node.nodeId);
                treeItem.append(tag);
            }
        };
    }
    
    function setOpenModalListener(name) {
        $("button[name='"+name+"']").click(function () {
            var currentPath = $("#storagePath").val();
            if(!currentPath || currentPath == null){
                currentPath = "/";
            }
            getPath(currentPath, false, showDirectoryTree, function (e) {
                console.log("loadDirectoryTree: error: ", e);
            });
            // loadDirectoryTree("/", false, showDirectoryTree, function (e) {
            //     console.log("loadDirectoryTree: error: ", e);
            // });            // loadDirectoryTree("/", false, showDirectoryTree, function (e) {
            //             //     console.log("loadDirectoryTree: error: ", e);
            //             // });
        })
    }
    
    function loadDirectoryTree(path, subFolders, success, error) {
        var url = "/admin/enviromentSettings/storagePath";
        url = !path ? url : url+ "?path=" + encodeURIComponent(path) + "&subFolders=" + subFolders;
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
        selectedPath = undefined;
        $('#tree').treeview(getTreeOptions(data));
        initState(data[0]);
        $('#tree').treeview('selectNode', [ 40, { silent: true } ]);
        $('#tree').on('nodeExpanded', function(event, data) {
            var node = $('#tree').treeview('getNode', data.nodeId);
            if(node.nodes.length == 0){
                loadDirectoryTree.call(this, node.path, true, function (data) {
                    node.nodes = data[0].nodes;
                    $('#tree').treeview('setInitialStates', node, 0);
                    $('#tree').treeview('expandNode', [ node.nodeId, { levels: 1, silent: true } ]);
                })
            }
        });
        $('#tree').on('nodeSelected', function(event, data) {
            selectedPath = data.path;
        });
        $('#tree').on('nodeAddNode', function(event, data) {
            var node = $('#tree').treeview('getNode', data.nodeId);
            $('#addSubDirectoryModal').modal();
            $('#parentPath').val(node.path);
            $("#createSubFolder").off('click');
            $("#createSubFolder").click(function () {
                var fullNodeName = $("#subFolderName").val();
                $('#addSubDirectoryModal').modal('hide');
                fullNodeName = fullNodeName.trim();
                if (!fullNodeName) {
                    return;
                }
                if(fullNodeName.indexOf("/") == 0){
                    fullNodeName = fullNodeName.substring(1);
                }
                var fullPath = node.path + "/" + fullNodeName;
                var nodeName = fullNodeName;
                var index = fullNodeName.indexOf("/");
                if(index >= 1){
                    nodeName = fullNodeName.substring(0, index);
                }
                createSubFolder(fullPath, function () {
                    console.log("createSubFolder: done: ", node);
                    if(node.nodes.length == 0){
                        console.log("createSubFolder: reloadDirectoryTree");
                        loadDirectoryTree.call(this, node.path, true, function (data) {
                            node.nodes = data[0].nodes;
                            $('#tree').treeview('setInitialStates', node, 0);
                            $('#tree').treeview('expandNode', [ node.nodeId, { levels: 1, silent: true } ]);
                        })
                    } else {
                        console.log("createSubFolder: push new node: " + nodeName);
                        node.nodes.push({
                            text: nodeName,
                            path: node.path + "/" + nodeName,
                            nodes: []
                        });
                        $('#tree').treeview('setInitialStates', node, 0);
                        $('#tree').treeview('expandNode', [ node.nodeId, { levels: 1, silent: true } ]);
                    }
                }, function (error) {
                    console.log("createSubFolder: end with error: ", error);
                    //TODO: show create subfolder error:
                });
            })
        });
        $('#directoriesModal').modal();
        $("#selectDirectory").click(function () {
            if(selectedPath){
                $("#storagePath").val(selectedPath);
            }
            $('#directoriesModal').modal('hide');
        })
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
    
    function createSubFolder(path, success, error) {
        var url = "/admin/enviromentSettings/createSubFolder";
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: url + "?path="+encodeURIComponent(path),
            cache: false,
            timeout: 600000,
            success: function (data) {
                console.log("createSubFolder success");
                if(data.status){
                    if(typeof success === "function"){
                        success();
                    }
                } else {
                    if(typeof error === "function"){
                        error("failed");
                    }
                }
            },
            error: function (e) {
                console.error("createSubFolder ERROR : ", e);
                if(typeof error === "function"){
                    error(e);
                }
            }
        });
    }

    function getPath(folderName, subFolders, success, error) {

        var url = "/admin/enviromentSettings/getFullPath?folderName="+folderName;
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: url,
            cache: false,
            timeout: 600000,
            success: function (response) {
                if(response && response.status) {
                    success(response.list);
                } else {
                    $.alert("Get full folder false");
                }
            },
            error: function (e) {
                $.alert("Get full folder false");
            }
        });
    }

    function initState(node) {
        if(node.nodes){
            $('#tree').treeview('setInitialStates', node, 0);
            for(var i=0;i<node.nodes.length;i++){
                initState(node.nodes);
            }
        }
    }

})(jQuery);
