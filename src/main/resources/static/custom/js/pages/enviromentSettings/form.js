
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
                state: {
                    expanded: true
                },
                nodes: [
                    {
                        text: "Child 1",
                        state: {
                            expanded: true
                        },
                        nodes: [
                            {
                                text: "Grandchild 1"
                            },
                            {
                                text: "Grandchild 2",
                                state: {
                                    selected: true
                                },
                            }
                        ]
                    },
                    {
                        text: "Child 2"
                    }
                ]
            },
            {
                text: "Parent 2",
                state: {
                    expanded: false
                },
                nodes: [
                    {
                        text: "Grandchild 2.1"
                    },
                    {
                        text: "Grandchild 2.2",
                    }
                ]
            },
            {
                text: "Parent 3"
            },
            {
                text: "Parent 4"
            },
            {
                text: "Parent 5"
            }
        ];
        return {
            data: tree,
            collapseIcon: "glyphicon glyphicon-folder-open",
            expandIcon: "glyphicon glyphicon-folder-open",
            emptyIcon: "glyphicon glyphicon-folder-close",
        };
    }
    
    function setOpenModalListener(name) {
        $("button[name='"+name+"']").click(function () {
            $('#tree').treeview(getTreeOptions());
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
