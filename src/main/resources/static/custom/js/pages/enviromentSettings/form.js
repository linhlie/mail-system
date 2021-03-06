
(function () {

    "use strict";
    var fullOldPath = "";
    var formChange = false;
    var selectedPath;

    $(function () {
        setFormChangeListener();
        setGoBackListener('backBtn');
        setOpenModalListener('openDirectoriesModal');
        setCCChangeListener();
        validateSenderCC();
    });

    function setFormChangeListener() {
        $('#enviromentSettingsForm').change(function() {
            formChange = true;
        });
    }

    function getTreeOptions(data, level) {
        return {
            data: data,
            collapseIcon: "glyphicon glyphicon-folder-open",
            expandIcon: "glyphicon glyphicon-folder-close",
            emptyIcon: "glyphicon glyphicon-folder-close",
            levels: level,
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
        var foldereName = fullOldPath.split("/");
        var  level = foldereName.length + 1;
        $('#tree').treeview(getTreeOptions(data, level));
        var nodeIdFolderCurrent = getNodeIdFolderCurent();
        $('#tree').treeview('selectNode', [ nodeIdFolderCurrent, { silent: true } ]);
        $('#tree').on('nodeExpanded', function(event, data) {
            var node = $('#tree').treeview('getNode', data.nodeId);
            if(node.nodes.length == 0){
                loadDirectoryTree.call(this, node.path, true, function (data) {
                    node.nodes = data[0].nodes;
                    console.log(node);
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
                    fullOldPath = response.msg;
                    success(response.list);
                } else {
                   console.error("Get full folder false");
                }
            },
            error: function (e) {
                console.error("Get full folder false");
            }
        });
    }

    function getAllNodes(){
        var treeViewObject = $('#tree').data('treeview'),
            allCollapsedNodes = treeViewObject.getCollapsed(),
            allExpandedNodes = treeViewObject.getExpanded(),
            allNodes = allCollapsedNodes.concat(allExpandedNodes);

        return allNodes;
    }

    function getNodeIdFolderCurent(){
        var arrAllNode = getAllNodes();
        if(arrAllNode){
            for(var i=0;i<arrAllNode.length;i++){
                if(arrAllNode[i].path == fullOldPath){
                    return arrAllNode[i].nodeId;
                }
            }
        }
        return -1;
    }

    function setCCChangeListener() {
        $('#senderCC').on('input', function() {
            validateSenderCC();
        });
    }

    function validateSenderCC() {
        var rawCC = $('#senderCC').val();
        rawCC = rawCC || "";
        var ccText = rawCC.replace(/\s*,\s*/g, ",");
        var cc = ccText.split(",");
        var senderValid = true;
        if(cc.length === 1 && cc[0] == "") {
            senderValid = true;
        } else {
            for(var i = 0; i < cc.length; i++) {
                var email = cc[i];
                var valid = validateEmail(email);
                if(!valid) {
                    senderValid = false;
                    break;
                }
            }
        }
        senderValid ? $('#cc-container').removeClass('has-error') : $('#cc-container').addClass('has-error');
        disableSubmitBtn(!senderValid);
    }

    function disableSubmitBtn(disabled) {
        $('#submitBtn').prop("disabled", disabled);
    }


})(jQuery);
