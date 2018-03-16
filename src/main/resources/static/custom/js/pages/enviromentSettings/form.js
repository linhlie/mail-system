
(function () {

    "use strict";
    var formChange = false;

    $(function () {
        setFormChangeListener();
        setGoBackListener('backBtn');
        setOpenModalListener('openDirectoriesModal')
    });

    function setFormChangeListener() {
        $('#enviromentSettingsForm').change(function() {
            formChange = true;
        });
    }

    function getTreeOptions() {
        // Some logic to retrieve, or generate tree structure
        var tree = [
            {
                text: "Node 1",
                nodes: [
                    {
                        text: "Child 1",
                        nodes: [
                            {
                                text: "Grandchild 1",
                                nodes: []
                            },
                            {
                                text: "Grandchild 2",
                                state: {
                                    selected: true,
                                },
                                nodes: []
                            }
                        ]
                    },
                    {
                        text: "Child 2",
                        nodes: []
                    }
                ]
            },
            {
                text: "Parent 2",
                nodes: [
                    {
                        text: "Grandchild 2.1",
                        nodes: []
                    },
                    {
                        text: "Grandchild 2.2",
                        nodes: []
                    }
                ]
            },
            {
                text: "Parent 3",
                nodes: []
            },
            {
                text: "Parent 4",
                nodes: []
            },
            {
                text: "Parent 5",
                nodes: []
            }
        ];
        return {
            data: tree,
            collapseIcon: "glyphicon glyphicon-folder-open",
            expandIcon: "glyphicon glyphicon-folder-close",
            emptyIcon: "glyphicon glyphicon-folder-close",
        };
    }
    
    function setOpenModalListener(name) {
        $("button[name='"+name+"']").click(function () {
            $('#tree').treeview(getTreeOptions());
            $('#tree').treeview('revealNode', [ "test", { silent: true } ])
            $('#tree').on('nodeExpanded', function(event, data) {
                console.log("nodeExpanded: data: ", $('#tree').treeview('getNode', data.nodeId));
                var node = $('#tree').treeview('getNode', data.nodeId);
                if(node.nodes.length == 0){
                    node.nodes = [{
                        text: "Parent 4",
                        nodes: []
                    }]
                    $('#tree').treeview('setInitialStates', node, 0);
                }
            });
            $('#tree').on('nodeSelected', function(event, data) {
                // Your logic goes here
                console.log("nodeSelected: event: ", event);
                console.log("nodeSelected: data: ", data);
            });
            $('#directoriesModal').modal();
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

})(jQuery);
