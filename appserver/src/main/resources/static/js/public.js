function _dateFormat(value, pattern) { //var displayDate = _dateFormat('19700101', '####-##-##');
    var i = 0,
    date = value.toString();
    return pattern.replace(/#/g, _ => date[i++]);
}
function dateFormat(value, pattern) {
    if(pattern == undefined){
        return _dateFormat(value, '####-##-##');
    }else {
        return _dateFormat(value, pattern);
    }
}
