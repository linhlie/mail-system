
(function () {
    var domainTableId = "domainTable";
    var saveDomainsId = "#saveDomains";
    var revertDomainsId = "#revertDomains";
    
    var domains = [];
    var domainsOriginal = [];
    var domainsDelete = [];
    
	var domainReplaceHead = '<tr>'+
    	'<th class="dark">無視ドメイン</th>'+
    	'<th th:colspan="1"></th>'+
    	'</tr>';
	
    var domainReplaceRow = '<tr role="row" class="hidden">' +
		'<td name="editDomain" rowspan="1" colspan="1" data="domain" style="cursor: pointer;"> '+
		'<span></span>' +
    	'</td>' +
		'<td name="deleteDomain" class="fit action" rowspan="1" colspan="1" data="id">' +
		'<button type="button">削除</button>' +
		'</td>' +
		'</tr>';
    
    var domainReplaceAddRow = '<tr role="row">' +
		'<td name="addDomain" rowspan="1" colspan="1" data="domain" style="cursor: pointer;">'+
		'<span>&nbsp;</span>' +
		'</td>' +
		'<td name="deleteDomain" class="fit action" rowspan="1" colspan="1" data="id">' +
		'</td>' +
		'</tr>';
	
    $(function () {
        initStickyHeader();
        loadDomainsAvoidRegister();
        setButtonClickListenter(saveDomainsId, saveDomainsOnClick);
        setButtonClickListenter(revertDomainsId, revertDomainsOnClick);
    });
    
    function loadDomainsAvoidRegister(){
    	function onSuccess(response) {
            if(response && response.status){
                response.list.sort(function (a, b) {
                    if(a.domain>b.domain) return 1;
                    return -1;
                });
                domainsOriginal = [];
            	for(var i=0;i<response.list.length;i++){
            		var domain = {
        					id: response.list[i].id,
        					domain: response.list[i].domain,
        					status: response.list[i].status
        			}
            		domainsOriginal.push(domain);
            	}
            	domainsDelete = [];
                loadDomainDataTable(domainTableId, response.list);
            }
            if(typeof callback == 'function'){
            	callback(response.list);
            }
        }
        
        function onError(error) {

        }
        getDomainAvoidRegister(onSuccess, onError);
    }
    
    function loadDomainDataTable(tableId, data) {
        domains = data;
        removeAllRow(tableId, domainReplaceRow);
        if (domains.length >= 0) {
            var html = domainReplaceRow;
            for (var i = 0; i < domains.length; i++) {
                html = html + addRowWithData(tableId, data[i], i);
            }
            html = html + domainReplaceAddRow;
            $("#" + tableId + "> thead").html(domainReplaceHead);
            $("#" + tableId + "> tbody").html(html);
            setRowClickListener("deleteDomain", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = domains[index];
                if (rowData && rowData.domain) {
                	doDeleteDomain(rowData.domain);
                }
            });
            setRowClickListener("editDomain", function () {
                var row = $(this)[0].parentNode;
                var index = row.getAttribute("data");
                var rowData = domains[index];
                if (rowData) {
                    $(this).closest('tr').addClass('highlight-selected').siblings().removeClass('highlight-selected');
                    doEditDomain(rowData);
                }
            });
            setRowClickListener("addDomain", function () {
                 doAddDomain("");
            });
        }
    }
    
    function addRowWithData(tableId, data, index) {
        var table = document.getElementById(tableId);
        if (!table) return "";
        var rowToClone = table.rows[1];
        var row = rowToClone.cloneNode(true);
        row.setAttribute("data-id", data.id);
        row.setAttribute("data", index);
        row.className = undefined;
        var cells = row.cells;
        for (var i = 0; i < cells.length; i++) {
            var cell = cells.item(i);
            var cellKeysData = cell.getAttribute("data");
            if (!cellKeysData || cellKeysData.length == 0) continue;
            var cellKeys = cellKeysData.split(".");
            var cellNode = cell.childNodes.length > 1 ? cell.childNodes[1] : cell.childNodes[0];
            if (cellNode) {
                if (cellNode.nodeName == "SPAN") {
                    var cellData = cellKeys.length == 2 ? (data[cellKeys[0]] ? data[cellKeys[0]][cellKeys[1]] : undefined) : data[cellKeys[0]];
                    if (Array.isArray(cellData)) {
                        cellNode.textContent = cellData.length;
                    } else {
                        cellNode.textContent = cellData;
                    }
                }
            }
        }
        return row.outerHTML;
    }
    
    function removeAllRow(tableId, replaceHtml) { //Except header row
        $("#" + tableId + "> tbody").html(replaceHtml);
    }
    
    function saveDomainsOnClick(){
    	var domainsUpdate=[];
    	for(var i=0;i<domains.length;i++){
    		var index = -1;
    		for(var j=0;j<domainsOriginal.length;j++){
    			if(domains[i].domain == domainsOriginal[j].domain){
    				index = j;
    			}
    		}
    		if(index == -1){
    			domainsUpdate.push(domains[i]);
    		}
    	}
    	
        function onSuccess(response) {
            if(response && response.status) {
                $.alert({
                    title: "",
                    content: "保存に成功しました",
                    onClose: function () {
                        loadDomainsAvoidRegister();

                    }
                });
            } else {
                $.alert("保存に失敗しました");
            }
        }
        
        function onError(response) {
            $.alert("保存に失敗しました");
        }
        
        saveDomainAvoidRegister({
        	domainsUpdate: domainsUpdate,
        	domainsDelete: domainsDelete,
        }, onSuccess, onError)
        
    }
    
    function revertDomainsOnClick(){
    	if(domainsOriginal){
    	    domains = [];
    	    domainsDelete = [];
    		for(var i=0;i<domainsOriginal.length;i++){
    			var domain = {
    					id: domainsOriginal[i].id,
    					domain: domainsOriginal[i].domain,
    					status: domainsOriginal[i].status
    			}
    			domains.push(domain);
    		}		
    		loadDomainDataTable(domainTableId, domains)
    	}
    }
    
    function setRowClickListener(name, callback) {
        $("td[name='" + name + "']").off('click');
        $("td[name='" + name + "']").click(function () {
            if (typeof callback == "function") {
                callback.apply(this);
            }
        })
    }
    
    function doDeleteDomain(domain) {
        $.confirm({
            title: '',
            titleClass: 'text-center',
            content: '<div class="text-center" style="font-size: 16px;">削除してもよろしいですか？<br/></div>',
            buttons: {
                confirm: {
                    text: 'はい',
                    action: function(){
                    	for(var i=0;i<domains.length;i++){
                    		if(domains[i].domain == domain){
                    			if(domains[i].id){
                    				domainsDelete.push(domains[i]);
                    			}
                    			domains.splice(i,1);                			
                    		}
                    	}
                    	loadDomainDataTable(domainTableId, domains);
                    }
                },
                cancel: {
                    text: 'いいえ',
                    action: function(){}
                },
            }
        });
    }
    
    function doAddDomain(data){
        showNamePrompt(data, function (domain) {
            if (domain != null && domain.length > 0) {
            	var newDomain = {
            		domain : domain,
            		status : 2
            	}
            	domains.push(newDomain);
            	loadDomainDataTable(domainTableId, domains);
            }
        })
    }
    
    function doEditDomain(data){
        showNamePrompt(data.domain, function (domain) {
            if (domain != null && domain.length > 0) {
            	data.domain = domain;
            	loadDomainDataTable(domainTableId, domains);
            }
        })
    }
    
    function showNamePrompt(domain, callback) {
    	removeError.apply($( '#dataModalName'));
        $('#dataModal').modal();
        $('#dataModalName').val(domain);
        $('#dataModalOk').off('click');
        $("#dataModalOk").click(function () {
            var domain = $( '#dataModalName').val();
            if(domain){
                domain = domain.toLowerCase();
                domain = domain.trim();
            }
            var test = "abc@"+domain;
            var isValid = isValidEmailAddress(test);
            var isExist = isExistDomain(domain)
            if(isExist){
            	showError.apply($( '#dataModalName'), ["ドメイン存在した。"]);
            }
            if(!isValid){
            	showError.apply($( '#dataModalName'), ["ドメイン無効な。"]);
            }
            
            if(isValid && !isExist){
                domain = domain.trim();
                domain = domain.toLowerCase();
            	$('#dataModal').modal('hide');
                if(typeof callback === "function"){
                    callback(domain);
                }
            }
        });
        $('#dataModalCancel').off('click');
        $("#dataModalCancel").click(function () {
            $('#dataModal').modal('hide');
            if(typeof callback === "function"){
                callback();
            }
        });
    }
    
    function showError(error, selector) {
        selector = selector || "div.form-group.row";
        var container = $(this).closest(selector);
        container.addClass("has-error");
        container.find("span.form-error").text(error);
    }
    
    function removeError(error, selector) {
        selector = selector || "div.form-group.row";
        var container = $(this).closest(selector);
        container.removeClass("has-error");
        container.find("span.form-error").text("");
    }
    
    function isValidEmailAddress(emailAddress) {
        var pattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
        return pattern.test(emailAddress);
    }
    
    function isExistDomain(domain) {
    	for(var i=0;i<domains.length;i++){
    		if(domains[i].domain == domain){
    			return true;
    		}
    	}
        return false;
    }
    
})(jQuery);
