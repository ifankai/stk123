
var downloadable = "";
var popup = "";
var timer = null

function init(tab, options) {
    chrome.cookies.getAll({}, function (cookies) {
        if (!cookies || !cookies.length) {
            chrome.runtime.sendMessage({type: "send", code: -1, info: {msg: '当前页面没有cookie'}});
            clearTimer()
            return
        }
        var content = "";
        for (var i in cookies) {
            cookie = cookies[i];
            if (cookie.domain.indexOf(domain) != -1) {
                /*content += escapeForPre(cookie.domain);
                content += "\t";
                content += escapeForPre((!cookie.hostOnly).toString().toUpperCase());
                content += "\t";
                content += escapeForPre(cookie.path);
                content += "\t";
                content += escapeForPre(cookie.secure.toString().toUpperCase());
                content += "\t";
                content += escapeForPre(cookie.expirationDate ? Math.round(cookie.expirationDate) : "0");
                content += "\t";*/
                content += escapeForPre(cookie.name);
                content += "=";
                content += escapeForPre(cookie.value);
                content += "; ";
            }
        }

        let otherData = {}
        if (options.other) {
            otherData = Object.assign({}, JSON.parse(options.other))
        }
        $.post({
			url:options.url, 
			contentType:"application/json; charset=utf-8",
			data:JSON.stringify({
				value: content,
				...otherData
            })
		}).done(function (data) {
            chrome.runtime.sendMessage({type: "send", info: data});
        })
    });
}

function escapeForPre(text) {
    return String(text).replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function getDomain(url) {
    //console.log("url=["+url+"]")
    server = url.match(/:\/\/(.[^/:#?]+)/)[1];
    //console.log("server=["+server+"]")
    parts = server.split(".");
    //console.log("parts=["+parts+"]")

    isip = !isNaN(parseInt(server.replace(".", ""), 10));
    //console.log("parts=["+isip+"]")

    if (parts.length <= 1 || isip) {
        domain = server;
    } else {
        //search second level domain suffixes
        var domains = new Array();
        domains[0] = parts[parts.length - 1];
        //assert(parts.length > 1)
        for (i = 1; i < parts.length; i++) {
            domains[i] = parts[parts.length - i - 1] + "." + domains[i - 1];
            //console.log("domains=["+domains[i]+"]");
            //domainlist defines in domain_list.js 
            if (!domainlist.hasOwnProperty(domains[i])) {
                domain = domains[i];
                //console.log("found "+domain);
                break;
            }
        }

        if (typeof (domain) == "undefined") {
            domain = server;
        }
    }

    return domain;
}
// chrome.tabs.onHighlighted.addListener(function () {
//     chrome.tabs.query({highlighted: true}, function(tabs) {
//         if (tabs && tabs[0]) {
//             domain = getDomain(tabs[0].url)
//             if (domain.indexOf('qq.com') === -1) {
//                 // clearInterval(timer)
//                 return
//             }
//             timer && clearInterval(timer)
//             timer = null
//             timer = setInterval(function () {
//                 chrome.tabs.reload(() => {
//                     setTimeout(() => {
//                         init(tabs[0])
//                     }, 20000)
//                 })
//             }, 40 * 60 * 1000)
//             init(tabs[0])
//         }
//     })
    
// })
function clearTimer() {
    timer && clearInterval(timer)
    timer = null
}
function start (options) {
    chrome.tabs.query({highlighted: true}, function(tabs) {
        if (tabs && tabs[0]) {
            domain = getDomain(tabs[0].url)
            if (options.rootSite && domain.indexOf(options.rootSite) === -1) {
                chrome.runtime.sendMessage({type: "send", code: -1, info: {msg: '当前域名非设置根域名'}});
                return
            }
            clearTimer()
            if (options.times === '一次') {
                init(tabs[0], options)
            } else {
                timer = setInterval(function () {
                    chrome.tabs.reload(() => {
                        setTimeout(() => {
                            init(tabs[0], options)
                        }, 20000)
                    })
                }, options.interval * 60 * 1000)
                init(tabs[0], options)
            }
        }
    })
}