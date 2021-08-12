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
function isVisible(elment) {
    var vpH = $(window).height(), // Viewport Height
        st = $(window).scrollTop(), // Scroll Top
        y = $(elment).offset().top;
    return y <= (vpH + st);
}

function saveDataToLocalStorage(key, data) {
    localStorage.setItem(key, JSON.stringify(data));
}
function getDataFromLocalStorage(key) {
    return JSON.parse(localStorage.getItem(key));
}

$(window).scroll(function() {
    if($(this).scrollTop() <= 300) {
        $('#back-to-top').fadeOut();
    } else {
        $('#back-to-top').css('position','fixed');
        $('#back-to-top').fadeIn();
    }
});
