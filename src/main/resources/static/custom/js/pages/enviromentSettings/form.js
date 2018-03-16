
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

    function getTree() {
        // Some logic to retrieve, or generate tree structure
        var tree = [
            {
                text: "Node 1",
                state: {
                    expanded: false,
                    selected: true
                },
                tags: ['available'],
                nodes: [
                    {
                        text: "Child 1",
                        nodes: [
                            {
                                text: "Grandchild 1"
                            },
                            {
                                text: "Grandchild 2"
                            }
                        ]
                    },
                    {
                        text: "Child 2"
                    }
                ]
            },
            {
                text: "Parent 2"
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
        return tree;
    }
    
    function setOpenModalListener(name) {
        $("button[name='"+name+"']").click(function () {
            console.log("ahaha");
            $('#tree').treeview({data: getTree()});
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
