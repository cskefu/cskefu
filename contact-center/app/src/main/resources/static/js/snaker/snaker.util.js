function expandAll() {
    var divs = document.all.tags("div");
    for (var i = 0; i < divs.length; i++) {
    	var idvalue = divs[i].id;
    	if (idvalue.lastIndexOf("DIV") > 0) {
    	    divs[i].style.display = "block";
    	}
    }
}
function expandNode(base, divId) {
    var div = document.getElementById(divId+"_DIV");
	if (div.style.display == "none"){
		div.style.display = "block";
		document.all[divId+"_img"].src = base + "/styles/images/flowopen.gif";
	}else{
		div.style.display = "none";
		document.all[divId+"_img"].src = base + "/styles/images/flowclose.gif";
	}
}
function collapseAll() {
    var divs = document.all.tags("div");
    for (var i = 0; i < divs.length; i++ ) {
    	var idvalue = divs[i].id;
    	if (idvalue.lastIndexOf("DIV") > 0) {
    	    divs[i].style.display = "none";
    	}
    }
}
function currentNode() {
	collapseAll();
	document.getElementById("${task.taskName}_DIV").style.display = "block";
}