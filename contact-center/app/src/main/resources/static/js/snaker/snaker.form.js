function dbTable() {
    $.ajax({
        type:"GET",
        url: parent.formData.ctx + "/form/form/tables?formId=" + parent.formData.id,
        success:function(data){
            var selectElement = document.getElementById("dbTable");
            for(var i=0;i<data.length;i++) {
                selectElement.options.add(new Option(data[i].name + '[' + data[i].displayName + ']', data[i].name));
            }
        }
    });
}

function dbField(tableName) {
    $.ajax({
        type:"GET",
        url: parent.formData.ctx + "/form/dbtable/fields?table=" + tableName,
        success:function(data){
            var selectElement = document.getElementById("dbField");
            selectElement.options.length = 1;
            for(var i=0;i<data.length;i++) {
                selectElement.options.add(new Option(data[i].displayName,data[i].name));
            }
        }
    });
}
function dict(element, config) {
    $.ajax({
        type:"GET",
        url: parent.formData.ctx + "/config/dictionary/items?config=" + config,
        success:function(data){
            var selectElement = document.getElementById(element);
            for(var i=0;i<data.length;i++) {
                selectElement.options.add(new Option(data[i].name,data[i].code));
            }
        }
    });
}

function dictionary(element) {
    $.ajax({
        type:"GET",
        url: parent.formData.ctx + "/config/dictionary/dicts",
        success:function(data){
            var selectElement = document.getElementById(element);
            for(var i=0;i<data.length;i++) {
                selectElement.options.add(new Option(data[i].cnName,data[i].name));
            }
        }
    });
}