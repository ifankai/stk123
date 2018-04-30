/**
 * @license almond 0.3.1 Copyright (c) 2011-2014, The Dojo Foundation All Rights Reserved.
 * Available via the MIT or new BSD license.
 * see: http://github.com/jrburke/almond for details
 */

(function() {
    var e, t, n;
    (function(r) {
        function v(e, t) {
            return h.call(e, t)
        }
        function m(e, t) {
            var n, r, i, s, o, u, a, f, c, h, p, v = t && t.split("/"),
            m = l.map,
            g = m && m["*"] || {};
            if (e && e.charAt(0) === ".") if (t) {
                e = e.split("/"),
                o = e.length - 1,
                l.nodeIdCompat && d.test(e[o]) && (e[o] = e[o].replace(d, "")),
                e = v.slice(0, v.length - 1).concat(e);
                for (c = 0; c < e.length; c += 1) {
                    p = e[c];
                    if (p === ".") e.splice(c, 1),
                    c -= 1;
                    else if (p === "..") {
                        if (c === 1 && (e[2] === ".." || e[0] === "..")) break;
                        c > 0 && (e.splice(c - 1, 2), c -= 2)
                    }
                }
                e = e.join("/")
            } else e.indexOf("./") === 0 && (e = e.substring(2));
            if ((v || g) && m) {
                n = e.split("/");
                for (c = n.length; c > 0; c -= 1) {
                    r = n.slice(0, c).join("/");
                    if (v) for (h = v.length; h > 0; h -= 1) {
                        i = m[v.slice(0, h).join("/")];
                        if (i) {
                            i = i[r];
                            if (i) {
                                s = i,
                                u = c;
                                break
                            }
                        }
                    }
                    if (s) break; ! a && g && g[r] && (a = g[r], f = c)
                } ! s && a && (s = a, u = f),
                s && (n.splice(0, u, s), e = n.join("/"))
            }
            return e
        }
        function g(e, t) {
            return function() {
                var n = p.call(arguments, 0);
                return typeof n[0] != "string" && n.length === 1 && n.push(null),
                s.apply(r, n.concat([e, t]))
            }
        }
        function y(e) {
            return function(t) {
                return m(t, e)
            }
        }
        function b(e) {
            return function(t) {
                a[e] = t
            }
        }
        function w(e) {
            if (v(f, e)) {
                var t = f[e];
                delete f[e],
                c[e] = ! 0,
                i.apply(r, t)
            }
            if (!v(a, e) && ! v(c, e)) throw new Error("No " + e);
            return a[e]
        }
        function E(e) {
            var t, n = e ? e.indexOf("!") : - 1;
            return n > - 1 && (t = e.substring(0, n), e = e.substring(n + 1, e.length)),
            [t, e]
        }
        function S(e) {
            return function() {
                return l && l.config && l.config[e] || {}
            }
        }
        var i, s, o, u, a = {},
        f = {},
        l = {},
        c = {},
        h = Object.prototype.hasOwnProperty,
        p = [].slice,
        d = /\.js$/;
        o = function(e, t) {
            var n, r = E(e),
            i = r[0];
            return e = r[1],
            i && (i = m(i, t), n = w(i)),
            i ? n && n.normalize ? e = n.normalize(e, y(t)) : e = m(e, t) : (e = m(e, t), r = E(e), i = r[0], e = r[1], i && (n = w(i))),
            {
                f: i ? i + "!" + e: e,
                n: e,
                pr: i,
                p: n
            }
        },
        u = {
            require: function(e) {
                return g(e)
            },
            exports: function(e) {
                var t = a[e];
                return typeof t != "undefined" ? t: a[e] = {}
            },
            module: function(e) {
                return {
                    id: e,
                    uri: "",
                    exports: a[e],
                    config: S(e)
                }
            }
        },
        i = function(e, t, n, i) {
            var s, l, h, p, d, m = [],
            y = typeof n,
            E;
            i = i || e;
            if (y === "undefined" || y === "function") {
                t = ! t.length && n.length ? ["require", "exports", "module"] : t;
                for (d = 0; d < t.length; d += 1) {
                    p = o(t[d], i),
                    l = p.f;
                    if (l === "require") m[d] = u.require(e);
                    else if (l === "exports") m[d] = u.exports(e),
                    E = ! 0;
                    else if (l === "module") s = m[d] = u.module(e);
                    else if (v(a, l) || v(f, l) || v(c, l)) m[d] = w(l);
                    else {
                        if (!p.p) throw new Error(e + " missing " + l);
                        p.p.load(p.n, g(i, ! 0), b(l), {}),
                        m[d] = a[l]
                    }
                }
                h = n ? n.apply(a[e], m) : undefined;
                if (e) if (s && s.exports !== r && s.exports !== a[e]) a[e] = s.exports;
                else if (h !== r || ! E) a[e] = h
            } else e && (a[e] = n)
        },
        e = t = s = function(e, t, n, a, f) {
            if (typeof e == "string") return u[e] ? u[e](t) : w(o(e, t).f);
            if (!e.splice) {
                l = e,
                l.deps && s(l.deps, l.callback);
                if (!t) return;
                t.splice ? (e = t, t = n, n = null) : e = r
            }
            return t = t || function() {},
            typeof n == "function" && (n = a, a = f),
            a ? i(r, e, t, n) : setTimeout(function() {
                i(r, e, t, n)
            },
            4),
            s
        },
        s.config = function(e) {
            return s(e)
        },
        e._defined = a,
        n = function(e, t, n) {
            if (typeof e != "string") throw new Error("See almond README: incorrect module build, no module name");
            t.splice || (n = t, t = []),
            ! v(a, e) && ! v(f, e) && (f[e] = [e, t, n])
        },
        n.amd = {
            jQuery: ! 0
        }
    })(),
    n("almond", function() {}),
    n("mathToolkit", [], function() {
        function e(e, t) {
            var n = Math.pow(10, t);
            return Math.round(e * n) / n
        }
        return {
            decimalRound: e
        }
    }),
    n("arrayToolkit", [], function() {
        function e(e, t) {
            var n = - 1,
            r = e.length,
            i, s;
            if (arguments.length === 1) {
                while (++n < r && ! ((i = e[n]) != null && i <= i)) i = undefined;
                while (++n < r)(s = e[n]) != null && i > s && (i = s)
            } else {
                while (++n < r && ! ((i = t.call(e, e[n], n)) != null && i <= i)) i = undefined;
                while (++n < r)(s = t.call(e, e[n], n)) != null && i > s && (i = s)
            }
            return i
        }
        function t(e, t) {
            var n = - 1,
            r = e.length,
            i, s;
            if (arguments.length === 1) {
                while (++n < r && ! ((i = e[n]) != null && i <= i)) i = undefined;
                while (++n < r)(s = e[n]) != null && s > i && (i = s)
            } else {
                while (++n < r && ! ((i = t.call(e, e[n], n)) != null && i <= i)) i = undefined;
                while (++n < r)(s = t.call(e, e[n], n)) != null && s > i && (i = s)
            }
            return i
        }
        function n(e, t) {
            var n = "";
            return $.map(e, function(e, r) {
                r === t && (n = e)
            }),
            n
        }
        function r(e) {
            var t, n = arguments,
            r = n.length,
            i;
            if (!Array.prototype.indexOf) while (r > 1 && e.length) {
                t = n[--r];
                while ((i = $.inArray(t, e)) !== - 1) e.splice(i, 1)
            } else while (r > 1 && e.length) {
                t = n[--r];
                while ((i = e.indexOf(t)) !== - 1) e.splice(i, 1)
            }
            return e
        }
        function i(e, t) {
            var n = e.length,
            r = t.length,
            i = n;
            if (n >= r) {
                for (var s = 0, o = t.length; s < o; s++) e.push(t[s]);
                return e
            }
            while (--i >= 0) t.unshift(e[i]);
            return t
        }
        function s(e, t) {
            return e.splice.apply(e, [0, t.length].concat(t))
        }
        return {
            min: e,
            max: t,
            remove: r,
            map: n,
            mergeLargeArr: i,
            unshiftArray: s
        }
    }),
    n("EventTarget", [], function() {
        var e = function() {
            this.eventTopics = {},
            this.eventSubUid = - 1
        };
        return e.prototype.trigger = function(e, t) {
            if (!this.eventTopics[e]) return ! 1;
            var n = this;
            return setTimeout(function() {
                var r = n.eventTopics[e],
                i = r ? r.length: 0;
                while (i--) r[i].func(t)
            },
            0),
            ! 0
        },
        e.prototype.on = function(e, t) {
            this.eventTopics[e] || (this.eventTopics[e] = []);
            var n = (++this.eventSubUid).toString();
            return this.eventTopics[e].push({
                token: n,
                func: t
            }),
            n
        },
        e.prototype.off = function(e) {
            for (var t in this.eventTopics) if (this.eventTopics[t]) for (var n = 0, r = this.eventTopics[t].length; n < r; n++) if (this.eventTopics[t][n].token === e) return this.eventTopics[t].splice(n, 1),
            e;
            return ! 1
        },
        e
    }),
    n("hexinStock/util", [], function() {
        function e(e) {
            function t() {}
            return t.prototype = e,
            new t
        }
        var t = {
            inheritPrototype: function(t, n) {
                var r = e(n.prototype);
                r.constructor = t,
                t.prototype = r
            },
            isGg: function(e) {
                return e.indexOf("HK") == 0 || e.indexOf("HS") == 0
            },
            isIndex: function(e) {
                var t = /^(1A|1B|1C|399|3C|HSI)/;
                return e.match(t) ? ! 0: ! 1
            },
            formatUnit: function(e, t, n) {
                var r, t = t || 0;
                return e >= (r = Math.pow(10, 8)) ? (e / r).toFixed(t) + (n ? "亿": "") : e >= (r = Math.pow(10, 4)) ? (e / r).toFixed(t) + (n ? "万": "") : e
            }
        };
        return t
    }),
    n("Xhr", [], function() {
        function e(e, t, n) {
            var r = {};
            $.ajax({
                url: e,
                dataType: "script",
                cache: ! 0,
                success: function() {
                    r = window["var_" + t],
                    n(r)
                }
            })
        }
        var t = function(e, t, n) {
            var r;
            return function() {
                window[n] = function(e) {
                    t(e)
                },
                r = $.ajax({
                    type: "get",
                    url: e,
                    dataType: "script",
                    cache: ! 0,
                    success: function(e) {
                        window[n] = undefined;
                        try {
                            delete window[n]
                        } catch(t) {}
                    },
                    error: function() {
                        window[n] = undefined;
                        try {
                            delete window[n]
                        } catch(e) {}
                    }
                })
            } (),
            r
        };
        return {
            jqGetScript: e,
            jqXhrWithoutCallback: t
        }
    }),
    n("UrlConfig", [], function() {
        var e = {
            urlHost: "http://d.10jqka.com.cn/"
        };
        return e
    }),
    n("KLineProvider", ["arrayToolkit", "mathToolkit", "EventTarget", "hexinStock/util", "Xhr", "UrlConfig"], function(e, t, n, r, i, s) {
        var o = function(e) {
            return ("" + e).slice(0, 4)
        },
        u = {
            0: "days",
            1: "weeks",
            2: "months",
            3: "fiveMin",
            4: "thirtyMin",
            5: "sixtyMin",
            6: "oneMin",
            7: "fourHour"
        },
        a = {
            0: "notRehabilitation",
            1: "forwardRehabilitation",
            2: "backwardRehabilitation"
        },
        f = function(e) {
            var t = {};
            return e.replace(/([0-7])([0-2])/g, function() {
                t.stockType = u[arguments[1]],
                t.rehabilitation = a[arguments[2]]
            }),
            t
        },
        l = function(e) {
            return e += "",
            new Date(e.slice(0, 4), e.slice(4, 6) - 1, e.slice(6, 8))
        },
        c = function(e, t, n) {
            if (n === "days") return e === t;
            if (n === "weeks") {
                var r = l(Math.min(e, t)),
                i = l(Math.max(e, t));
                return r.getDay() + (i.getTime() - r.getTime()) / 864e5 <= 6
            }
            return n === "months" ? e.slice(4, 6) === t.slice(4, 6) : e === t
        },
        h = function(e) {
            return [e[1], e[7], e[8], e[9], e[11], e[13], e[19], e[1968584]]
        },
        p = function(e, t) {
            var n = e.data.indexOf(t);
            if (n == - 1) throw t + " cann't find in the js file !";
            var r = e.data.split(t),
            i = r[1].split(";");
            return e.data.slice(0, n) + t + i[0]
        },
        d = function(e, t) {
            var n = e.data.indexOf(t);
            if (n == - 1) throw t + " cann't find in the js file !";
            return e.data.slice(n)
        },
        v = function(e) {
            var t = e.split(",");
            return + t[4]
        },
        m = function(e) {
            var t = e.split(",");
            return + t[5]
        },
        g = function(e) {
            var t = {},
            n = [];
            for (var r = e.length - 1; r >= 0; r--) t = {},
            t.key = o(e[r].data),
            t.data = e[r].data,
            n.push(t);
            return n.sort(function(e, t) {
                return e.key < t.key ? - 1: 1
            }),
            n
        },
        y = function(e, t) {
            var n = "quotebridge",
            r = e.split(t),
            i = [];
            return r[1].replace(/([^\/.]+)/g, function() {
                i.push(arguments[0])
            }),
            i.pop(),
            n + "_" + i.join("_")
        },
        b = function(e) {
            n.call(this);
            var t = {
                url: s.urlHost,
                urlVersion: "v2/line/",
                historyUrlName: "last",
                code: ["17_600000"],
                stockType: "00",
                ma: [5, 10, 30, 60],
                betweenTwoDays: null,
                historyDataAndNum: null,
                isKeepingGet: ! 1,
                drawWidth: 0,
                drawDataLength: 60,
                intervalTime: 6e4
            };
            this.dataLastTime = "",
            this.lastGetTime = "",
            this.yearsManagerObj = {},
            this.total = "",
            this.historyStart = "",
            this.historyCache = {},
            this.isOpen = 1,
            this.minEndDate = "",
            this.AMOpenTime = "",
            this.AMCloseTime = "",
            this.PMOpenTime = "",
            this.PMCloseTime = "",
            this.numInLastJsFile = 0,
            this.dataArray = [],
            this.configs = $.extend({},
            t, e)
        };
        return r.inheritPrototype(b, n),
        b.prototype.getFromWeb = function(e, t, n) {
            var r, s, o;
            n == "currentData" ? (r = this.getWebUrl(t, "today"), s = y(r, this.configs.url), i.jqXhrWithoutCallback(r, e, s)) : (r = this.getWebUrl(t), s = y(r, this.configs.url), i.jqXhrWithoutCallback(r, function(n) {
                n.code = t,
                e(n)
            },
            s))
        },
        b.prototype.setDataLastTime = function(e) {
            return e[e.length - 1].t
        },
        b.prototype._initByFirstGetData = function(e, t) {
            var n = e.rt.split(","),
            r = n[0].split("-"),
            i = n[1].split("-");
            this.AMOpenTime = r[0],
            this.AMCloseTime = r[1],
            this.PMOpenTime = i[0],
            this.PMCloseTime = i[1],
            this.yearsManagerObj = e.year,
            this.total = e.total,
            this.historyStart = e.start,
            this.numInLastJsFile = e.num,
            this.stockName = e.name,
            this.lastGetTime = t.dt,
            this.isOpen = t.open,
            this.currentDate = t[1]
        },
        b.prototype._tidyData = function(e, t) {
            var n = t[0],
            r = t[1][e],
            i = this;
            this._initByFirstGetData(n, r),
            this.trigger("getStockNameInfo", {
                stockName: this.stockName,
                code: this.configs.code[0]
            });
            var s = h(r),
            o = s.join(","),
            u = s[0],
            a = n.data.split(";"),
            l = a[a.length - 1].split(",")[0],
            p = f(this.configs.stockType);
            c(u, l, p.stockType) ? (a.pop(), a.push(o)) : a.push(o),
            this.setMaLastArr(a),
            this.dataArray = this.arrageDataByFirstGet(a),
            this.configs.drawDataLength > this.dataArray.length ? this.minEndDate = [this.dataArray[this.dataArray.length - 1].t, this.dataArray.length - 1] : this.minEndDate = [this.dataArray[this.configs.drawDataLength - 1].t, this.configs.drawDataLength - 1],
            this.dataLastTime = u,
            ! this.configs.betweenTwoDays && ! this.configs.historyDataAndNum ? (this.trigger("getDataByKeeping", this.dataArray), this.trigger("getFirstGetData", this.dataArray)) : this.configs.historyDataAndNum ? this.getDataByYearsAndNum(this.configs.historyDataAndNum, function(e) {
                i.trigger("getDataByKeeping", e),
                i.trigger("getFirstGetData", e)
            }) : this.getBetweenTwoDays(this.configs.betweenTwoDays, function(e) {
                i.trigger("getDataByKeeping", e),
                i.trigger("getFirstGetData", e)
            }),
            this.stopGetDataOnConditions(),
            this.configs.isKeepingGet && this.keepingGetData(e)
        },
        b.prototype.stopGetDataOnConditions = function() {
            if (this.isOpen === 0 || this.lastGetTime > this.AMCloseTime && this.lastGetTime < this.PMOpenTime || this.lastGetTime > this.PMCloseTime || this.lastGetTime < this.AMOpenTime) {
                this.stopGetData();
                return
            }
        },
        b.prototype.keepingGetEachStock = function(e, t) {
            var n = this.arrageDataByKeepGet(e, t),
            r = h(e[t]),
            i = this.getMaDefaultObj(this.configs.ma);
            if (n.t == this.dataLastTime) {
                this.maLastArr.pop(),
                this.maLastArr.push(r.join(","));
                var s = this.getMaData(this.maLastArr, this.maLastArr.length - 1, this.configs.ma, this.dataArray[this.dataArray.length - 2]);
                $.extend(n, i, s),
                this.dataArray.pop(),
                this.dataArray.push(n)
            } else {
                this.maLastArr.push(r.join(","));
                var s = this.getMaData(this.maLastArr, this.maLastArr.length - 1, this.configs.ma, this.dataArray[this.dataArray.length - 1]);
                $.extend(n, i, s),
                this.dataArray.push(n),
                this.dataLastTime = this.setDataLastTime(this.dataArray)
            }
        },
        b.prototype.keepingGetData = function(e) {
            var t = this;
            this.keepingGetHandle = setTimeout(function() {
                t.getFromWeb(function(n) {
                    t.keepingGetEachStock(n, e),
                    t.trigger("getDataByKeeping", t.dataArray),
                    t.lastGetTime = n[e].dt,
                    t.stopGetDataOnConditions()
                },
                e, "currentData"),
                t.keepingGetHandle = setTimeout(arguments.callee, t.configs.intervalTime)
            },
            t.configs.intervalTime)
        },
        b.prototype.stopGetData = function() {
            this.configs.isKeepingGet = ! 1,
            clearTimeout(this.keepingGetHandle)
        },
        b.prototype.getYearFromInfoByYearToAndNum = function(e, t, n) {
            var r = [],
            i,
            s,
            o = n ? n: 0;
            $.each(this.yearsManagerObj, function(t, n) {
                r.push(t),
                t === e && (i = r.length - 1)
            });
            var u = r[0];
            if (t >= this.total) return r[0];
            if (t == o || t <= n || i === 0) return e;
            var a = i,
            f;
            while (a-- > 0) {
                f = this.yearsManagerObj[r[a]],
                o += f;
                if (o >= t) {
                    s = r[a];
                    break
                }
                if (r[a] === u) {
                    s = r[a];
                    break
                }
                if (a === 1 && o < t) return r[0]
            }
            return s
        },
        b.prototype.getAfterDataByYearsAndNum = function(e, t) {
            var n = e[0],
            r = o(e[1]),
            i = this,
            s = e[3],
            u,
            a,
            f = "",
            l = "",
            c = "";
            $.each(this.yearsManagerObj, function(e, t) {
                e > r && n > 0 && (n -= t, u = e)
            }),
            u = u >> 0 ? u >> 0: r;
            var h = r >> 0 >= u ? u: r >> 0;
            this.getDataInterval(h, u, function(e) {
                a = g(e);
                for (var n = 0; n < a.length; n++) a[n].key == u && (f = a[n].data),
                a[n].key == h ? c = a[n].data + ";": a[n].key > h && a[n].key < u && (l += a[n].data + ";");
                var r = c + l + f;
                h === u && (r = f);
                var s = i.arrangeDataByYears(r);
                t(s)
            })
        },
        b.prototype.getDataByYearsAndNum = function(e, t) {
            if (e.length >= 3) this.getAfterDataByYearsAndNum(e, t);
            else {
                var n = e[0];
                if (e[1] <= this.minEndDate[0] && this.total <= this.configs.drawDataLength) return t(this.dataArray.slice(0, this.minEndDate[1] + 1)),
                ! 1;
                var r = o(e[1]),
                i = this,
                s = this.getYearFromInfoByYearToAndNum(r, n),
                u,
                a = "",
                f = "",
                l = "";
                this.getDataInterval(s, r >> 0, function(n) {
                    u = g(n);
                    for (var o = 0; o < u.length; o++) u[o].key == r && (a = p(u[o], "" + e[1])),
                    u[o].key === s ? l = u[o].data + ";": u[o].key > s && u[o].key < r && (f += u[o].data + ";");
                    var c = l + f + a;
                    s === r && (c = a);
                    var h = i.arrangeDataByYears(c);
                    if (i.configs.historyDataAndNum && h.length < i.configs.drawDataLength) {
                        var d = h[h.length - 1].t,
                        v = i.configs.drawDataLength - h.length,
                        m = 0;
                        for (var o = 0; o < i.dataArray.length; o++) i.dataArray[o].t > d && m < v && (h.push(i.dataArray[o]), m++)
                    }
                    t(h)
                })
            }
        },
        b.prototype.getBetweenTwoDays = function(e, t) {
            var n = o(e[0]) >> 0,
            r = o(e[1]) >> 0,
            i,
            s,
            u = this;
            this.getDataInterval(n - 1, r, function(o) {
                var a = "";
                for (var f = 0; f < o.length; f++) o[f].data.indexOf(n - 1) === 0 ? i = o[f].data: o[f].data.indexOf(r) === 0 ? s = p(o[f], "" + e[1]) : a += o[f].data + ";";
                var l = i + ";" + a + s,
                c = u.arrangeDataByYears(l);
                t(c);
                return
            })
        },
        b.prototype.getDataInterval = function(e, t, n) {
            var r = t - e + 1,
            s = this.configs.code[0],
            o = [],
            u;
            for (var a = 0; a < r; a++) {
                var f = "" + ( + e + a),
                l = this.getWebUrl(s, f);
                u = y(l, this.configs.url),
                i.jqXhrWithoutCallback(l, function(e) {
                    o.push(e),
                    o.length === r && n(o)
                },
                u)
            }
        },
        b.prototype.setKeepingGetStatus = function(e) {
            this.isKeepingGet = e ? ! 0: ! 1
        },
        b.prototype.getWebUrl = function(e, t) {
            var n, r;
            return t === "today" ? n = "today.js": t ? n = t + ".js": n = this.configs.historyUrlName + ".js",
            this.configs.url + this.configs.urlVersion + e + "/" + this.configs.stockType + "/" + n
        },
        b.prototype.arrageDataByFirstGet = function(e) {
            return this._getDataArray(e)
        },
        b.prototype.setMaLastArr = function(e) {
            var t = this.configs.ma,
            n = t[t.length - 1];
            n >= e.length ? this.maLastArr = e: this.maLastArr = e.slice( - n - 1)
        },
        b.prototype.arrageDataByKeepGet = function(e, t) {
            var n = h(e[t]);
            return this.getDataWithoutMa(n)
        },
        b.prototype.getDataWithoutMa = function(e) {
            var t = this.getKLineStatus(parseFloat(e[1]), parseFloat(e[4]));
            return {
                code: this.configs.code[0],
                s: t,
                t: e[0],
                o: parseFloat(e[1]),
                a: parseFloat(e[2]),
                i: parseFloat(e[3]),
                c: parseFloat(e[4]),
                n: parseFloat(e[5]),
                np: parseFloat(e[6]),
                h: parseFloat(e[7])
            }
        },
        b.prototype.arrangeDataByYears = function(e) {
            return this._getDataArray(e.split(";"))
        },
        b.prototype._getDataArray = function(e) {
            var t = [],
            n = e.length,
            r = this.configs.ma,
            i = this.getMaDefaultObj(r),
            s = [],
            o = {};
            for (var u = 0; u < n; u++) s = e[u].split(","),
            o = this.getMaData(e, u, r, t[u - 1]),
            t[u] = this.getDataWithoutMa(s),
            $.extend(t[u], i, o);
            return t
        },
        b.prototype.getMaData = function(e, n, r, i) {
            var s = {};
            for (var o = 0; o < r.length; o++) {
                var u = "ma" + r[o],
                a = + r[o],
                f = 0,
                l = 0,
                c = "nMa" + r[o],
                h = 0,
                p = 0;
                if (n + 1 < a) break;
                if (i[u] !== null) l = i[u] * a - v(e[n - a]) + v(e[n]),
                h = i[c] * a - m(e[n - a]) + m(e[n]);
                else for (var d = 0; d < a; d++) {
                    var g = e[n - d].split(",");
                    l += + g[4],
                    h += + g[5]
                }
                f = t.decimalRound(l / a, 4),
                p = t.decimalRound(h / a, 0),
                s[u] = f
            }
            return s
        },
        b.prototype.getMaDefaultObj = function(e) {
            var t = {};
            for (var n = 0; n < e.length; n++) {
                var r = "ma" + e[n];
                t[r] = null
            }
            return t
        },
        b.prototype.getKLineStatus = function(e, t) {
            var n = e - t;
            return n < 0 ? n = "ab": n > 0 ? n = "be": n == 0 && (n = "eq"),
            n
        },
        b.prototype.getData = function() {
            var e = this.configs.code[0],
            t = this,
            n = [],
            r,
            i;
            this.getFromWeb(function(r) {
                n[0] = r,
                n.length === 2 && t._tidyData(e, n)
            },
            e),
            this.getFromWeb(function(r) {
                n[1] = r,
                n.length === 2 && n[0] && t._tidyData(e, n)
            },
            e, "currentData")
        },
        b.prototype.getStockInfo = function(e) {
            this.on("getStockNameInfo", function(t) {
                e(t)
            })
        },
        b
    }),
    n("barBuilder", ["arrayToolkit"], function(e) {
        function t(e) {
            return typeof e == "function" ? e: function() {
                return e
            }
        }
        function n(e) {
            return e[0]
        }
        function r(e) {
            return e[1]
        }
        function i(e, t) {
            return arguments.length < 2 && (t = .5),
            (e >> 0) + t
        }
        function s(e, t, n, r, s, o, u, a, f) {
            for (var l = 0; l < t.length; l++) {
                var c = t[l].length,
                h = - 1,
                p;
                n.beginPath();
                while (++h < c) n.rect(i((p = t[l][h])[0], e), i(p[1], e), r, p[3]);
                n.fillStyle = s[l][1],
                n.fill(),
                r !== 1 && u && (n.lineWidth = a, n.strokeStyle = o[l][1], n.stroke())
            }
        }
        function o(e, t, n) {
            for (var r = 0; r < t.length; r++) {
                var s = t[r].length,
                o = - 1,
                u;
                e.beginPath();
                while (++o < s) e.moveTo(i((u = t[r][o])[0]), i(u[1])),
                e.lineTo(i(u[0]), i(u[2]));
                e.closePath(),
                e.strokeStyle = n[r][1],
                e.stroke()
            }
        }
        function u(e, t, n) {
            var r = t.length;
            for (var i = 0; i < r; i++) {
                e.beginPath();
                var s = t[i].length,
                o,
                u = - 1;
                while (++u < s) e.moveTo((o = t[i][u])[0], o[1]),
                e.lineTo(o[0], o[2]);
                e.strokeStyle = n[i][1],
                e.stroke(),
                e.closePath()
            }
        }
        function a(e, t, n, r, s, a, f, l, c, h) {
            if (r === 1) return o(n, h, s),
            ! 0;
            u(n, h, a);
            for (var p = 0; p < t.length; p++) {
                var d = t[p].length,
                v = - 1,
                m;
                n.beginPath();
                while (++v < d) n.rect(i((m = t[p][v])[0], e), i(m[1], e), r, m[3] >> 0);
                n.fillStyle = s[p][1],
                n.fill(),
                r !== 1 && f && (n.lineWidth = l, n.strokeStyle = a[p][1], n.stroke())
            }
        }
        function l() {
            function k(e, t) {
                if (u === "canvas") {
                    var n = e[0][2],
                    r = e.length > 1 ? e[1][0] - n - e[0][0] : 1,
                    i = n === 1 || l % 2 === 0 || a === 0 ? 0: .5;
                    v.length === 0 && (v = [["a", c], ["a", c], ["a", c]]),
                    t.save(),
                    o(i, y, t, n, d, v, a, l, c, m),
                    t.restore()
                } else if (u === "svg") return o(e);
                return ! 1
            }
            var o = s,
            u = "canvas",
            a = 1,
            l = 1,
            c = "black",
            h = "steelblue",
            p = "",
            d = [],
            v = [],
            m = [],
            g = [],
            y = [],
            b,
            w = n,
            E = r,
            S = n,
            x = r,
            T = n,
            N = r,
            C = r,
            L = function(e) {
                var n = t(w),
                r = t(E),
                s = t(S),
                o = t(x),
                u = t(T),
                a = t(N),
                f = t(C),
                l = [],
                c = [],
                h = - 1,
                v = e.length;
                if (p) {
                    for (var A = 0; A < d.length; A++) y[A] = [],
                    m[A] = [];
                    while (++h < v) {
                        var O = e[h];
                        O[p] == d[0][0] ? (y[0].push([ + n(O, h), + r(O, h), + s(O, h), + o(O, h)]), m[0].push([i( + u(O, h)), i( + a(O, h)), i( + f(O, h))])) : O[p] == d[1][0] ? (y[1].push([ + n(O, h), + r(O, h), + s(O, h), + o(O, h)]), m[1].push([i( + u(O, h)), i( + a(O, h)), i( + f(O, h))])) : d.length === 3 && O[p] == d[2][0] && (y[2].push([ + n(O, h), + r(O, h), + s(O, h), + o(O, h)]), m[2].push([i( + u(O, h)), i( + a(O, h)), i( + f(O, h))])),
                        g.push([i( + u(O, h)), i( + a(O, h)), i( + f(O, h))]),
                        c.push([ + n(O, h), + r(O, h), + s(O, h), + o(O, h)])
                    }
                } else {
                    while (++h < v) {
                        var O = e[h];
                        c.push([ + n(O, h), + r(O, h), + s(O, h), + o(O, h)])
                    }
                    y = c
                }
                return c.length && (l = k(c, b)),
                L
            };
            return L.x = function(e) {
                return w = e,
                L
            },
            L.y = function(e) {
                return E = e,
                L
            },
            L.width = function(e) {
                return S = e,
                L
            },
            L.height = function(e) {
                return x = e,
                L
            },
            L.ctx = function(e) {
                return u = "canvas",
                b = e,
                L
            },
            L.strokeStyle = function(e) {
                return typeof e == "boolean" ? a = e ? 1: 0: c = e,
                L
            },
            L.lineWidth = function(e) {
                return e ? l = e: a = 0,
                L
            },
            L.fillStyle = function(e) {
                return h = e,
                L
            },
            L.canvasBarTemplate = function(t) {
                return u = "canvas",
                typeof t == "function" ? o = t: o = e.map(f, t),
                L
            },
            L.barColor = function(e) {
                if (e.length < 3) throw new Error("infinite array");
                return p = e.shift(),
                d = e,
                L
            },
            L.barBorderColor = function(e) {
                if (e.length < 3) throw new Error("infinite array");
                return v = e,
                L
            },
            L.lineX = function(e) {
                return T = e,
                L
            },
            L.lineYTop = function(e) {
                return N = e,
                L
            },
            L.lineYBottom = function(e) {
                return C = e,
                L
            },
            L.linePosOnkLine = function() {
                return g
            },
            L
        }
        var f = {
            kBar: s,
            kLine: a
        },
        c = function() {
            return l()
        };
        return c
    }),
    n("scale", [], function() {
        var e = function() {
            function o(n) {
                if (typeof n == "object" && ! n) return null;
                if (isNaN(e[0]) || isNaN(e[1])) return 0;
                var r = (t[1] - t[0]) / (e[1] - e[0]);
                isFinite(r) || (r = 0);
                var i = t[0] - r * e[0];
                return r * n + i
            }
            function u(e, t) {
                var n = e[0],
                r = e[1],
                i = Math.max(Math.abs(r - t), Math.abs(n - t));
                return [t - i, t + i]
            }
            function a(e, t, n) {
                var r = [],
                i = 0;
                r.push(e);
                while (++i < n) r.push(e + i * t);
                return r
            }
            function f() {
                return e
            }
            function l(t, n) {
                if (!n) return o.domain(t);
                var r = u(t, + n);
                return e = r,
                o
            }
            var e, t, n, r, i, s;
            return o.domain = function(t, r) {
                return arguments.length == 0 && e ? e: (arguments.length == 2 ? (n = r, l(t, r)) : (n = null, (isNaN(t[0]) || ! isFinite(t[0]) || t[0] == 0) && (isNaN(t[1]) || ! isFinite(t[0]) || t[1] == 0) ? e = [0, 1] : e = t), o)
            },
            o.range = function(e) {
                return arguments.length == 0 && t ? t: (t = e, o)
            },
            o.ticks = function(t, r) {
                var i = + n;
                i && (t = (t || 5) % 2 ? t: t + 1);
                var s = (e[1] - e[0]) / (t - 1),
                o = 0,
                u = [],
                a = (t - 1) / 2;
                for (; o < t; o++) o == t - 1 ? u.push(r ? e[1].toFixed(r) : e[1]) : o == 0 ? u.push(r ? e[0].toFixed(r) : e[0]) : i && o == a ? u.push(r ? i.toFixed(r) : i) : u.push(r ? (e[0] + s * o).toFixed(r) : e[0] + s * o);
                return u
            },
            o.reverse = function(n) {
                var r = (t[1] - t[0]) / (e[1] - e[0]),
                i = t[0] - r * e[0];
                return (n - i) / r
            },
            o.rangeRoundBands = function(e, t) {
                arguments.length < 1 && (e = 0),
                arguments.length < 2 && (t = e);
                var n = this.domain(),
                u = this.range(),
                f,
                l = n[1] - n[0] + 1 >> 0,
                c = - 1,
                h = [];
                while (++c < l) h.push(c);
                var p = u[1] < u[0],
                d = u[p - 0],
                v = u[1 - p],
                m = Math.floor((v - d) / (l - e + 2 * t)),
                g = v - d - (l - e) * m;
                return r = a(d + Math.round(g / 2), m, l),
                p && r.reverse(),
                s = m * e,
                i = Math.floor(m * (1 - e)),
                s < 2 && (s = 2, i = Math.floor((v - d - s - s * l) / (2 + l))),
                i <= 1 && (s = 0, i = 1),
                o
            },
            o.barRangeBand = function() {
                return i
            },
            o.barRange = function() {
                return r
            },
            o.barPadding = function() {
                return s
            },
            o
        };
        return e
    }),
    n("Grid", [], function() {
        var e = function(e, t, n) {
            var r = {
                grid: {
                    x: 50,
                    y: 20,
                    width: 420,
                    height: 320,
                    color: "#ddd",
                    margin: 5,
                    offsetLeft: arguments[3]
                }
            };
            this.context = e,
            this.options = t || r,
            this.callback = n
        };
        return e.prototype.draw = function() {
            function e(e) {
                return (e >> 0) + .5
            }
            var t = this.options.grid || {},
            n = e(t.x),
            r = e(t.y),
            i = t.width,
            s = t.height,
            o = t.ysize || 5,
            u = t.xsize || 5,
            a = s / (o - 1),
            f = i / u,
            l = this.context,
            c = [],
            h = [],
            p = [],
            d = [];
            l.beginPath(),
            l.strokeStyle = "#ddd";
            for (var v = 0, m; v < t.pos.length; v++) {
                var g = t.pos[v];
                m = e(g),
                l.moveTo(n, m),
                l.lineTo(n + i, m),
                c.push([n, m])
            }
            l.stroke(),
            l.beginPath();
            for (var v = 0, y; v <= u; v++) {
                var g = t.pos[v];
                y = e(n + f * v),
                l.moveTo(y, r),
                l.lineTo(y, r + s),
                d.push([y, r + s])
            }
            l.stroke(),
            l.beginPath(),
            l.strokeStyle = "#555";
            for (var v = 0; v < c.length; v++) t.showTextLine && (l.moveTo(n - 3, c[v][1]), l.lineTo(n, c[v][1])),
            l.moveTo(n + i, c[v][1]),
            l.lineTo(n + i, c[v][1]);
            for (var v = 0; v < d.length; v++) {
                var b = d[v][0];
                l.moveTo(b, d[v][1]),
                t.showTextLine ? l.lineTo(b, d[v][1] + 3) : l.lineTo(b, d[v][1])
            }
            return l.stroke(),
            l.strokeStyle = t.color,
            t.topBorder ? (l.moveTo(n, r), l.lineTo(n, r + s), l.lineTo(n + i, r + s), l.lineTo(n + i, r)) : l.strokeRect(n, r, i, c[0][1] - c[c.length - 1][1] + 20),
            l.stroke(),
            {
                context: this.context,
                leftAxisPos: c,
                bottomAxisPos: d
            }
        },
        e.prototype.measureText = function(e) {
            var t = e,
            n = document.createElement("div"),
            r = typeof e == "object" ? t.join("<br />") : e;
            n.innerHTML = r,
            n.style.position = "absolute",
            n.style.top = "-999px",
            document.body.appendChild(n);
            var i = n.clientWidth,
            s = n.clientHeight / (typeof e == "object" ? e.length: 1);
            return document.body.removeChild(n),
            {
                width: i,
                height: s
            }
        },
        e.prototype.genTicks = function(e) {
            var t = this.options.grid.scale,
            n = this.options.grid.tickDecimals,
            r = t[0],
            i = t[1],
            s = 0,
            o = typeof n == "number" ? n: 2,
            e;
            this.ticks = i.ticks(e || 7, 2),
            e = this.ticks.length;
            for (; s < e; s++) this.ticks[s] = ( + this.ticks[s]).toFixed(o);
            return this.ticks
        },
        e.prototype.setTicks = function(e) {
            this.ticks = e
        },
        e.prototype.getTicks = function() {
            return this.ticks
        },
        e.prototype.parseAxis = function(e, t) {
            var n = [],
            r,
            i;
            for (r = 0; r < e.length; ++r) i = e[r],
            i && (n["x" + i.n] = i.c2p(pos.left));
            for (r = 0; r < t.length; ++r) i = t[r],
            i && i.used && (n["y" + i.n] = i.c2p(pos.top));
            return n.x1 !== undefined && (n.x = n.x1),
            n.y1 !== undefined && (n.y = n.y1),
            n
        },
        e.prototype.drawAxis = function(e) {
            var t = e.direction,
            n = e.position,
            r = e.autoIndent,
            i = e.formatter,
            s = e.type,
            o = t == "y" ? this.draw().leftAxisPos: this.draw().bottomAxisPos,
            u = this.measureText(o),
            a = u.width,
            f = u.height,
            l = document.createElement("div"),
            c = document.createElement("div"),
            h = o.length,
            p = this.genTicks(h),
            d = 0;
            l.className = e.cls || "";
            if (t == "x") {
                if (n == "bottom") {
                    var v = e.xText || ["09:30", "10:30", "11:30/13:00", "14:00", "15:00"];
                    for (d = 0; d < v.length; d++) {
                        var c = document.createElement("div"),
                        a;
                        c.style.position = "absolute",
                        c.style.top = o[d][1] + "px",
                        c.style.left = o[d][0] + "px",
                        c.innerHTML = v[d],
                        c.style.width = (a = this.measureText(v[d]).width) + "px",
                        r ? d == 0 ? c.style.marginLeft = 0: d == v.length - 1 ? c.style.marginLeft = - a + "px": c.style.marginLeft = - a / 2 + "px": s == "fiveday" ? (c.style.width = this.options.grid.width / 5 + "px", c.style.textAlign = "center") : c.style.marginLeft = - a / 2 + "px",
                        l.appendChild(c)
                    }
                }
            } else for (d = 0; d < o.length; d++) c = document.createElement("div"),
            c.style.position = "absolute",
            r ? d == o.length - 1 ? c.style.top = o[d][1] + "px": d == 0 ? c.style.top = o[d][1] - f + "px": c.style.top = o[d][1] - f / 2 + "px": (c.style.top = o[d][1] - f / 2 + "px", c.style.right = 5 + + this.options.grid.panel.offsetWidth - this.options.grid.x + "px"),
            n == "right" && (c.style.left = 5 + this.options.grid.width + this.options.grid.x + "px"),
            c.innerHTML = i ? i(p[d]) : p[d],
            l.appendChild(c);
            this.options.grid.panel.parentNode.appendChild(l);
            return;
            var t, o, u, a, f, l, m, c, h, p, d
        }, e.prototype.updateAxis = function() {},
        e
    }),
    n("kgrid", ["require", "Grid"], function(e) {
        e = t("Grid");
        var n = function() {
            e.apply(this, arguments)
        };
        return n.prototype = new e,
        n.prototype.draw = function() {
            function e(e) {
                return (e >> 0) + .5
            }
            var t = this.options.grid || {},
            n = e(t.x),
            r = e(t.y),
            i = t.width,
            s = t.height,
            o = t.ysize || 5,
            u = t.xsize || 5,
            a = s / (o - 1),
            f = i / u,
            l = this.context,
            c = [],
            h = [],
            p = [],
            d = [];
            l.beginPath(),
            l.strokeStyle = "#ccc";
            for (var v = 0, m; v < t.pos.length; v++) {
                var g = t.pos[v];
                m = e(g),
                l.moveTo(n, m),
                l.lineTo(n + i, m),
                c.push([n, m])
            }
            l.stroke(),
            l.beginPath();
            var y = [1 / 6, .5, 5 / 6];
            for (var v = 0, b; v < y.length; v++) {
                var g = t.pos[v];
                t.xTextPos ? b = t.xTextPos[v] : b = e(n + i * y[v]),
                d.push([b, r + s])
            }
            l.stroke(),
            l.beginPath(),
            l.strokeStyle = "#ccc";
            for (var v = 0; v < c.length; v++) l.moveTo(n + i, c[v][1]),
            l.lineTo(n + i + 3, c[v][1]);
            if (t.showTextLine) for (var v = 0; v < d.length; v++) {
                var w = d[v][0];
                l.moveTo(w, d[v][1]),
                l.lineTo(w, d[v][1] + 3)
            }
            return l.stroke(),
            l.strokeStyle = t.color,
            l.moveTo(n, r + s),
            l.lineTo(n + i, r + s),
            l.stroke(),
            {
                context: this.context,
                leftAxisPos: c,
                bottomAxisPos: d
            }
        },
        n
    }),
    n("dashedLine", [], function() {
        var e = window.CanvasRenderingContext2D && CanvasRenderingContext2D.prototype;
        e && e.lineTo && (e.dashedLine = function(e, t, n, r, i) {
            i || (i = [2, 5]),
            this.save();
            var s = n - e,
            o = r - t,
            u = Math.sqrt(s * s + o * o),
            a = Math.atan2(o, s);
            this.translate(e, t),
            this.moveTo(0, 0),
            this.rotate(a);
            var f = i.length,
            l = 0,
            c = ! 0;
            e = 0;
            while (u > e) e += i[l++ % f],
            e > u && (e = u),
            c ? this.lineTo(e, 0) : this.moveTo(e, 0),
            c = ! c;
            this.restore()
        })
    }),
    n("canvasLine", ["arrayToolkit"], function(e) {
        function t(e) {
            return typeof e == "function" ? e: function() {
                return e
            }
        }
        function n(e) {
            return e[0]
        }
        function r(e) {
            return e[1]
        }
        function i(e) {
            return e ? (e >> 0) + .5: null
        }
        function s(e, t) {
            var n = 0,
            r = e.length,
            i = e[0];
            if (isNaN(i[1])) for (var s = 1; s < e.length; s++) if (!isNaN(e[s][1])) {
                i = e[s],
                n = s;
                break
            }
            t.moveTo(i[0], i[1]);
            while (++n < r) t.lineTo(e[n][0], e[n][1])
        }
        function o(e, t) {
            var n = arguments[4],
            r = 0,
            i = e.length,
            s = e[0],
            o;
            t.moveTo(s[0], s[1]),
            t.lineTo(s[0], n);
            while (++r < i) t.beginPath(),
            t.moveTo((o = e[r])[0], o[1]),
            t.lineTo(o[0], n),
            e[r][1] > e[r - 1][1] ? t.strokeStyle = "#D85342": t.strokeStyle = "#6CA584",
            t.closePath(),
            t.stroke()
        }
        function u(e, t) {
            var n = arguments[4],
            r = 0,
            i = e.length,
            s = e[0],
            o;
            t.moveTo(s[0], s[1]),
            t.lineTo(s[0], n);
            while (++r < i) t.beginPath(),
            t.moveTo((o = e[r])[0], o[1]),
            t.lineTo(o[0], n),
            e[r][1] < n ? t.strokeStyle = "#D85342": t.strokeStyle = "#6CA584",
            t.closePath(),
            t.stroke()
        }
        function a(e, t, n, r) {
            var i = e.length;
            s(e, t),
            t.lineTo(e[i - 1][0], n),
            t.lineTo(e[0][0], n),
            t.fillStyle = r,
            t.fill()
        }
        function l() {
            function m(e, t) {
                return t.beginPath(),
                t.strokeStyle = a,
                o(e, t, u, l, c),
                t.stroke(),
                ! 0
            }
            var o = s,
            u = 0,
            a = "black",
            l = "rgba(232, 244, 233, .5)",
            c, h, p = [],
            d = n,
            v = r,
            g = function(e) {
                var n = t(d),
                r = t(v),
                s = [],
                o = - 1,
                u = e.length,
                a;
                while (++o < u) a = e[o],
                r(a, o) ? s.push([i(n(a, o)), i(r(a, o))]) : s.push([0]);
                return s.length && m(s, h),
                p = s.slice(0),
                s
            };
            return g.x = function(e) {
                return d = e,
                g
            },
            g.y = function(e) {
                return v = e,
                g
            },
            g.y0 = function(e) {
                return u = e,
                g
            },
            g.bottom = function(e) {
                return c = e,
                g
            },
            g.ctx = function(e) {
                return h = e,
                g
            },
            g.strokeStyle = function(e) {
                return a = e,
                g
            },
            g.fillStyle = function(e) {
                return l = e,
                g
            },
            g.canvasLineTemplate = function(t) {
                return typeof t == "function" ? o = t: o = e.map(f, t),
                g
            },
            g.linePos = function() {
                return p
            },
            g
        }
        var f = {
            linear: s,
            "linear-closed": a,
            "linear-bar": o,
            "linear-bar-macd": u
        },
        c = function() {
            return l()
        };
        return c
    }),
    n("techCal", ["arrayToolkit", "mathToolkit"], function(e, t) {
        function v(e, t, n) {
            if (e.length == 0 || ! e) return null;
            var r = [],
            i = e.length,
            s = 0,
            o = {},
            u = 0,
            a = 0,
            f = 0,
            l = 0;
            for (var c = 0; c < i; c++) if (c < t) o = e[c],
            f = + (n == "number" ? o: o.c),
            u += f,
            l = u / t,
            r.push(l);
            else {
                var h = c - t + 1;
                u = 0;
                for (var p = h; p <= c; p++) o = e[p],
                f = + (n == "number" ? o: o.c),
                u += f;
                l = u / t,
                r.push(l)
            }
            return r
        }
        function g(e) {
            return e ? 0: 1
        }
        function y(e, t, n) {
            return t = t || 0,
            (2 * e + (n - 1) * t) / (n + 1)
        }
        function b(e, t) {
            return + e > + t ? + e: + t
        }
        function w(e) {
            return Math.abs( + e)
        }
        function E(e, t, n, r) {
            return (n * e + (t - n) * r) / t
        }
        function S(e) {
            var t = 12,
            n = 26,
            r = 9,
            i = t - 1,
            s = t + 1,
            o = n - 1,
            u = n + 1,
            a = r - 1,
            f = r + 1,
            l = 0,
            c, h, p, d, v, m, g = [];
            for (l = 0; l < e.length; l++) c = e[l].c,
            l == 0 || ! c ? (v = m = c || 0, p = h = v - m, d = 0) : (v = (2 * c + i * v) / s, m = (2 * c + o * m) / u, h = v - m, p = (2 * h + a * p) / f, d = 2 * (h - p)),
            g.push({
                MACD: d,
                DEA: p,
                DIFF: h
            });
            return g
        }
        function x(e) {
            var t = 9,
            n = 3,
            r = 3,
            i, s, o, u = Infinity,
            a = - Infinity,
            f, l, c, h, p, d, v = [];
            for (p = 0; p < e.length; p++) {
                if (p == 0) l = 100,
                c = 100;
                else {
                    i = e[p].c,
                    s = e[p].i,
                    o = e[p].a,
                    u = Infinity,
                    a = - Infinity,
                    p > t ? d = p - t: d = 0;
                    for (; d < (p > t ? p: t); d++) u > e[d].i && (u = e[d].i),
                    a < e[d].a && (a = e[d].a);
                    f = (i - u) / (a - u) * 100,
                    l = (f + (n - 1) * l) / n,
                    c = (l + (r - 1) * c) / r
                }
                h = 3 * l - 2 * c,
                l < 0 && (l = 0) || l > 100 && (l = 100),
                c < 0 && (c = 0) || c > 100 && (c = 100),
                h < 0 && (h = 0) || h > 100 && (h = 100),
                v.push({
                    K: l,
                    D: c,
                    J: h
                })
            }
            return v
        }
        function T(e) {
            var t = 6,
            n = 12,
            r = 24,
            i, s, o, u, a, f, l, c, h, p, d, v, m, g = [];
            for (v = 0; v < e.length; v++) i = e[v].c,
            v == 0 ? (s = i, u = l = p = 0, a = c = d = 0) : (s = e[v - 1].c, u = E(b(i - s, 0), t, 1, u), a = E(w(i - s), t, 1, a), l = E(b(i - s, 0), n, 1, l), c = E(w(i - s), n, 1, c), p = E(b(i - s, 0), r, 1, p), d = E(w(i - s), r, 1, d)),
            o = u / a * 100,
            f = l / c * 100,
            h = p / d * 100,
            g.push({
                RSI6: o,
                RSI12: f,
                RSI24: h
            });
            return g
        }
        function N(e) {
            var t = 20,
            n = 2,
            r, i, s, o, u, a, f = [],
            l,
            c,
            h = 0;
            for (u = 0; u < e.length; u++) {
                r = e[u].c,
                h += r;
                if (u > t) {
                    i = h / t,
                    c = 0;
                    for (a = u - t; a < u; a++) l = e[a].c - i,
                    c += l * l;
                    c = Math.sqrt(c / t),
                    s = i + n * c,
                    o = i - n * c,
                    h -= e[u - t].c
                } else i = s = o = NaN;
                f.push({
                    MID: i,
                    UPPER: s,
                    LOWER: o
                })
            }
            return f
        }
        function C(e) {
            var t = 10,
            n = [6, t],
            r = "maxR" + n[0],
            i = "minR" + n[0],
            o = "maxR" + n[1],
            u = "minR" + n[1],
            a = d(e, n, "maxMinDataInRound", ["maxR", "minR"]),
            f = [],
            l = {},
            c = 0,
            h = 0;
            for (var p = 0; p < a.length; p++) l = a[p],
            l[r] && (c = 100 * (l[r] - s(e[p])) / (l[r] - l[i])),
            l[o] && (h = 100 * (l[o] - s(e[p])) / (l[o] - l[u])),
            f.push({
                WR1: h,
                WR2: c
            });
            return f
        }
        function k(e) {
            var t = 6,
            n = 12,
            r = 24,
            i = 0,
            o = 0,
            u = 0,
            a = {},
            f = [],
            l = [t, n, r],
            c = "ma" + l[0],
            h = "ma" + l[1],
            p = "ma" + l[2],
            v = d(e, l, "averageDataInRound", ["ma"]);
            for (var m = 0; m < v.length; m++) a = v[m],
            a[c] && (i = (s(e[m]) - a[c]) / a[c] * 100, a[h] && (o = (s(e[m]) - a[h]) / a[h] * 100, a[p] && (u = (s(e[m]) - a[p]) / a[p] * 100))),
            f.push({
                BIAS: i,
                BIAS2: o,
                BIAS3: u
            });
            return f
        }
        function L(e) {
            var n = 26,
            u = 10,
            a = [],
            f,
            l = [],
            c = 0,
            h = 0,
            p = 0,
            d = [],
            v,
            g,
            y,
            E,
            S,
            x,
            T,
            N,
            C,
            k,
            L,
            A,
            O,
            M,
            _,
            D,
            P,
            H,
            B,
            j,
            F,
            I,
            q = 0,
            R;
            for (var U = 0; U < e.length; U++) {
                g = e[U - 1],
                S = e[U],
                c = 0,
                h = 0,
                q = 0,
                g && (y = s(g), A = o(g), L = i(g), N = r(S), C = i(S), x = s(S), T = o(S), E = w(N - y), k = w(C - y), O = w(N - L), M = w(y - A), P = k > O && k > E, H = k + E / 2 + M / 4, B = O + M / 4, D = m(P, H, B), j = E > k && E > O, F = E + k / 2 + M / 4, _ = m(j, F, D), I = x - y + (x - T) / 2 + y - A, R = b(E, k), q = 16 * I / _ * R),
                a.push(q);
                if (U < n) {
                    d.push({
                        ASI: 0,
                        ASIT: 0
                    });
                    continue
                }
                for (f = 0; f < n; f++) c += a[U - f];
                l.push(c);
                if (U < n + u) {
                    d.push({
                        ASI: c,
                        ASIT: 0
                    });
                    continue
                }
                for (v = 0; v < u; v++) h += l[U - n - v];
                p = h / u,
                d.push({
                    ASI: t.decimalRound(c, 2),
                    ASIT: t.decimalRound(p, 2)
                })
            }
            return d
        }
        function A(e) {
            var t = 100,
            n = 200,
            r = 26,
            i, s, o = [r],
            u = "upVol" + o[0],
            a = "downVol" + o[0],
            f = [],
            l = NaN,
            c,
            h,
            p = d(e, o, "volStatusInRound", ["upVol", "downVol"]);
            for (var v = 0; v < e.length; v++) p[v][u] && (l = p[v][u] / p[v][a] * 100),
            f.push({
                VR: l,
                A: t,
                B: n
            });
            return f
        }
        function _(e) {
            var t = 12,
            r = 6,
            i = [],
            o,
            u,
            a = [],
            l,
            c = [],
            h = [r],
            p = ["ma"],
            d = f(h, p),
            v = n();
            for (var m = 0; m < e.length; m++) {
                if (m - t < 0) {
                    i.push({
                        MTM: NaN,
                        MAMTM: NaN
                    });
                    continue
                }
                l = s(e[m]),
                o = s(e[m - t]),
                u = l - o,
                a.push(u),
                roundObj = O(a, h, m - t, c[m - 1], v, p[0]),
                c[m] = $.extend({},
                d, roundObj),
                i.push({
                    MTM: u,
                    MAMTM: c[m].ma6
                })
            }
            return i
        }
        function D(e) {
            var t = 12,
            n = 6,
            r = {},
            i, s, o, u, a, f, l = e.length,
            c = [],
            h,
            p;
            for (h = 1; h < l; h++) {
                var d = h - t + 1;
                if (d < 0) continue;
                o = 0;
                for (p = d; p < h; p++) {
                    r = e[p - 1],
                    i = e[p];
                    if (!i || ! r) break;
                    i.c > r.c && o++
                }
                f = o / t * 100,
                c.push({
                    PSY: f
                })
            }
            return c
        }
        function P(e) {
            var t = 14,
            n = {},
            r = 0,
            i = [],
            s = [];
            for (var o = 0; o < e.length; o++) n = e[o],
            r = n.a + n.i + n.c,
            i.push(r);
            var u = 0,
            a = 0,
            f = [],
            l = - 1,
            c = 0,
            h = 0,
            p = [],
            d = [];
            for (var v = 1; v < e.length; v++) r = i[v],
            n = e[v],
            r > i[v - 1] ? c = r * n.n: c = 0,
            r < i[v - 1] ? h = r * n.n: h = 0,
            p.push(c),
            d.push(h);
            var m = 0,
            g = 0,
            y = [],
            b = [],
            w = [];
            for (var E = 0; E < e.length; E++) {
                l = E - t;
                if (l < 0) c = p[E],
                h = d[E],
                m += c,
                g += h,
                u = g != 0 ? m / g: 0,
                w.push(u);
                else {
                    m = 0,
                    g = 0;
                    for (var S = l; S < E; S++) c = p[S],
                    h = d[S],
                    m += c,
                    g += h;
                    u = g != 0 ? m / g: 0,
                    w.push(u)
                }
            }
            var x = 0;
            for (var T = 0; T < e.length; T++) n = e[T],
            u = w[T],
            x = u + 1 != 0 ? 100 - 100 / (u + 1) : 100,
            s.push({
                MFI: x
            });
            return s
        }
        function H(e) {
            var t = 23,
            n = 8,
            r = 0,
            i = 0,
            s = [],
            o = [],
            u = {},
            a = [];
            for (var f = 1; f < e.length; f++) u = e[f],
            u.o <= e[f - 1].o ? r = 0: r = Math.max(u.a - u.o, u.o - e[f - 1].o),
            u.o >= e[f - 1].o ? i = 0: i = Math.max(u.o - u.i, u.o - e[f - 1].o),
            s.push(r),
            o.push(i);
            var l = 0,
            c = 0,
            h = [],
            p = [],
            d = 0,
            m = 0,
            g = - 1;
            for (var y = 0; y < e.length; y++) {
                g = y - t;
                if (g < 0) r = s[y],
                i = o[y],
                d += r,
                m += i,
                h.push(d),
                p.push(m);
                else {
                    m = 0,
                    d = 0;
                    for (var b = g; b < y; b++) r = s[b],
                    i = o[b],
                    d += r,
                    m += i;
                    h.push(d),
                    p.push(m)
                }
            }
            var w = 0,
            E = 0,
            S = [],
            x = [];
            for (var T = 0; T < e.length; T++) l = h[T],
            c = p[T],
            l > c ? w = (l - c) / l: l == c ? w = 0: w = (l - c) / c,
            S.push(w);
            x = v(S, n, "number");
            for (var N = 0; N < e.length; N++) u = e[N],
            w = S[N],
            E = x[N],
            a.push({
                ADTM: w,
                MAADTM: E
            });
            return a
        }
        function B(e) {
            var t = 12,
            n = 6,
            r = [],
            i = [],
            s = [],
            o = {},
            u = 0,
            a = 0;
            for (var f = 0; f < e.length; f++) {
                o = e[f];
                if (f >= t) {
                    u = (o.c - e[f - t].c) / e[f - t].c * 100,
                    r.push(u);
                    continue
                }
                r.push(!1)
            }
            i = v(r, n, "number");
            for (var l = 0; l < e.length; l++) s.push({
                ROC: r[l],
                ROCMA: i[l]
            });
            return s
        }
        function j(e) {
            var t = 3,
            n = 6,
            r = 12,
            i = 24,
            s = [];
            if (!e || e.length == 0) return;
            var o = {},
            u, a = v(e, t),
            f = v(e, n),
            l = v(e, r),
            c = v(e, i);
            for (var h = 0; h < e.length; h++) o = e[h],
            u = (a[h] + f[h] + l[h] + l[h]) / 4,
            u = isNaN(u) ? o.c: u,
            s.push({
                BBI: u,
                A: o.c
            });
            return s
        }
        function F(e) {
            var t = 10,
            n = 50,
            r = 150,
            i = 0,
            s = 0,
            o = {},
            u = [],
            a = [],
            f = 0,
            l = 0,
            c = [];
            for (var h = 1; h < e.length; h++) o = e[h],
            f = o.a - e[h - 1].c,
            l = e[h - 1].c - o.i,
            u.push(isNaN(f) ? 0: f),
            a.push(isNaN(l) ? 0: l);
            var p = 0,
            d = 0,
            v = [],
            m = 0,
            g = 0;
            for (var y = 0; y < e.length; y++) if (h < t) f = u[y],
            l = a[y],
            p += f,
            d += l,
            m = d != 0 ? p / d * 100: 0,
            v.push(m);
            else {
                g = y - t,
                p = 0,
                d = 0;
                for (var b = g; b < y; b++) f = u[b],
                l = a[b],
                p += f,
                d += l;
                m = d != 0 ? p / d * 100: 0,
                v.push(m)
            }
            var w = 0,
            E = 0,
            S = [],
            x = [];
            for (var T = 1; T < e.length; T++) o = e[T],
            w = Math.max(0, o.a - o.o),
            E = Math.max(0, o.o - o.i),
            S.push(isNaN(w) ? 0: w),
            x.push(isNaN(E) ? 0: E);
            var N = [],
            C = 0,
            k = 0,
            L = 0,
            A = 0;
            for (var O = 0; O < e.length; O++) if (O < t) C = S[O],
            k = x[O],
            L += C,
            A += k,
            s = A != 0 ? L / A * 100: 0,
            N.push(s);
            else {
                g = O - t,
                L = 0,
                A = 0;
                for (var M = g; M < O; M++) C = S[M],
                k = x[M],
                L += C,
                A += k;
                s = A != 0 ? L / A * 100: 0,
                N.push(s)
            }
            for (var _ = 0; _ < e.length; _++) c.push({
                AR: v[_],
                BR: N[_],
                A: n,
                B: r
            });
            return c
        }
        function I(e) {
            var t = 14,
            n = 9;
            if (!e || e.length == 0) return;
            var r, i = [],
            s = 0,
            o = 0,
            u = [],
            a = [],
            f = {};
            for (var l = 0; l < e.length; l++) f = e[l],
            r = f.n,
            i.push(r),
            s = f.a + f.i,
            o = f.a - f.i,
            u.push(s),
            a.push(o);
            var c = v(i, t, "number"),
            h = v(a, t, "number"),
            p = 0,
            d = [],
            m = 0;
            for (var g = 0; g < e.length; g++) f = e[g],
            m = g > 0 ? u[g - 1] : f.a + f.i,
            p = (f.a + f.i - m) / (f.a + f.i) * 100,
            d.push(p);
            var y = 0,
            b = [],
            w = 0,
            E = 0;
            for (var S = 0; S < e.length; S++) f = e[S],
            p = d[S],
            w = c[S] / f.n,
            o = a[S],
            E = h[S],
            y = E != 0 ? p * w * o / E: 0,
            b.push(y);
            var x = v(b, t, "number"),
            T = v(x, n, "number"),
            N = 0,
            C = 0,
            k = [];
            for (var L = 0; L < e.length; L++) N = x[L],
            C = T[L],
            k.push({
                EMV: N,
                MAEMV: C,
                A: 0
            });
            return k
        }
        function q(e) {
            var t = 9,
            n = 3,
            r = 3,
            i, s = 0,
            o = 0,
            u = 0,
            a, f, l, c, h, p, d, v = [];
            for (s = 0; s < e.length; s++) {
                var m = e[s];
                i = m.c,
                a = s - t + 1,
                a < 0 && (a = 0);
                if (s == 0) l = e[a].i,
                c = e[a].a,
                o = 0,
                u = 0;
                else {
                    if (o < a) {
                        l = Infinity;
                        for (f = a; f <= s; f++) e[f].i < l && (l = e[f].i, o = f)
                    } else m.i < l && (l = m.i, o = s);
                    if (u < a) {
                        c = - Infinity;
                        for (f = a; f <= s; f++) e[f].a > c && (c = e[f].a, u = f)
                    } else m.a > c && (c = m.a, u = s)
                }
                h = (c - i) / (c - l) * 100,
                s == 0 ? (p = h, d = p) : (p = (1 * h + (n - 1) * p) / n, d = (1 * p + (r - 1) * d) / r),
                v.push({
                    LWR1: p,
                    LWR2: d,
                    A: 20,
                    B: 80
                })
            }
            return v
        }
        function R(e) {
            var t = 20,
            n = 10,
            r = 6;
            if (!e || e.length == 0) return;
            var i = {},
            s = {},
            o = [],
            u = [],
            a = [],
            f = [],
            l = [];
            for (var c = 0; c < e.length; c++) i = e[c],
            o.push(i.c);
            var h = v(o, t, "number"),
            p,
            d,
            m,
            g;
            for (var y = 0; y < e.length; y++) {
                var b = y - n;
                if (b < 0) {
                    u.push(!1);
                    continue
                }
                p = h[b],
                u.push(p)
            }
            for (var w = 0; w < e.length; w++) i = e[w],
            p = u[w],
            m = i.c - p,
            a.push(m),
            f = v(a, r, "number"),
            g = f[w],
            l.push({
                DPO: m,
                MADPO: g
            });
            return l
        }
        function U(e) {
            var t, n, r, i, o, f = [],
            l = [],
            c = [],
            h = [];
            for (var p = 0; p < e.length; p++) n = a(e[p]),
            r = u(e[p]),
            o = s(e[p]),
            p === 0 && f.push({
                CYS: NaN
            }),
            c[p] = y(n, c[p - 1], 13),
            h[p] = y(r, h[p - 1], 13),
            t = c[p] / h[p],
            i = (o - t) / t * 100,
            f.push({
                CYS: i
            });
            return f
        }
        function W(e) {
            var t, n, o, u, a, f, l, c, h, p, d, v, g, y, E, S, x, T = 14,
            N = 6,
            C, k, L, A, O = [],
            M = [],
            _ = [],
            D = [],
            P = [],
            H = [],
            B = [];
            O.push({
                ADX: NaN,
                DI1: NaN,
                DI2: NaN,
                ADXR: NaN
            });
            for (var j = 1; j < e.length; j++) p = s(e[j - 1]),
            d = r(e[j - 1]),
            v = i(e[j - 1]),
            C = r(e[j]),
            L = i(e[j]),
            g = b(C - L, w(C - p)),
            y = w(L - p),
            E = b(g, y),
            M.push(E),
            n = C - d,
            o = v - L,
            S = m(n > 0 && n > o, n, 0),
            _.push(S),
            x = m(o > 0 && o > n, o, 0),
            D.push(x),
            M.length >= T ? (A = z(M, _, D, T, N, j, H, B), H.push(A.tempADX), B.push(A.ADX), O.push({
                ADX: A.ADX,
                DI1: A.DI1,
                DI2: A.DI2,
                ADXR: A.ADXR
            })) : O.push({
                ADX: NaN,
                DI1: NaN,
                DI2: NaN,
                ADXR: NaN
            });
            return O
        }
        function X(e) {
            var t = 100,
            o = - 100,
            u = 14,
            a, l, c, h, p, d = [],
            v = [],
            m = [],
            g = {},
            y = [u],
            b = ["ma"],
            w = f(y, b),
            E = n();
            for (var S = 0; S < e.length; S++) l = r(e[S]),
            c = i(e[S]),
            h = s(e[S]),
            a = (l + c + h) / 3,
            m.push(a),
            g = M(m, y, S, v[S - 1], E, b[0]),
            v[S] = $.extend({},
            w, g),
            v[S].r ? p = v[S].r: p = NaN,
            d.push({
                CCI: p,
                A: t,
                B: o
            });
            return d
        }
        function V(e) {
            var t, n, r, i, o, a, f = [0],
            l = [];
            for (var c = 0; c < e.length; c++) {
                if (c === 0) {
                    l.push({
                        OBV: NaN
                    });
                    continue
                }
                n = s(e[c - 1]),
                t = s(e[c]),
                r = u(e[c]),
                i = m(t < n, - r, 0),
                o = m(t > n, r, i),
                a = f[c - 1] + o,
                f.push(a),
                l.push({
                    OBV: f[c] / 1e4
                })
            }
            return l
        }
        function K(e) {
            var t = 10,
            r = 50,
            i = 10,
            o, u, a, l = {},
            c = {},
            h, p, d = [],
            v = [],
            m = [],
            g = ["ma", "ma"],
            y = [t, r],
            b = g[0] + y[0],
            w = g[1] + y[1],
            E = n(),
            S = f(y, g),
            x = f([i], ["ma"]),
            T = [];
            for (var N = 0; N < e.length; N++) l = J(e, y, N, d[N - 1], s, g[0]),
            d[N] = $.extend({},
            S, l),
            o = d[N][b] - (d[N][w] ? d[N][w] : 0),
            m.push(o),
            c = O(m, [i], N, v[N - 1], E, "ma"),
            v[N] = $.extend({},
            x, c),
            T.push({
                DDD: o,
                AMA: v[N]["ma" + i]
            });
            return T
        }
        function Q(e) {
            var t = 12,
            r = 20,
            i, o = [],
            u = [],
            a = [],
            l,
            c,
            h,
            p = [],
            d = [],
            v = [],
            m = {},
            g = [r],
            b = ["ma"],
            w = f(g, b),
            E = n();
            for (var S = 0; S < e.length; S++) i = s(e[S]),
            l = y(i, o[S - 1], t),
            o.push(l),
            c = y(l, u[S - 1], t),
            u.push(c),
            h = y(c, a[S - 1], t),
            a.push(h),
            TRIX = (h - a[S - 1]) / a[S - 1] * 100,
            p.push(TRIX),
            m = O(p, g, S, v[S - 1], E, b[0]),
            v[S] = $.extend({},
            w, m),
            d.push({
                TRIX: TRIX,
                TRMA: v[S]["ma" + r]
            });
            return d
        }
        function Y(e) {
            var t = [],
            o = [],
            u,
            a = 9,
            l = 3,
            c = [],
            h = [],
            p,
            d,
            v,
            m,
            u = {},
            g = {},
            b,
            w,
            E,
            S = [],
            x = [a],
            T = ["maxR", "minR"],
            N = f(x, T),
            C = f([l], ["ma"]),
            k = n();
            for (var L = 0; L < e.length; L++) {
                if (!e[0]) continue;
                b = s(e[L]),
                u = G(e, x, L, o[L - 1]),
                o[L] = $.extend({},
                N, u),
                E = o[L].maxR9,
                w = o[L].minR9,
                E || (E = r(e[L]), w = i(e[L])),
                p = (b - w) / (E - w) * 100,
                d = y(p, c[L - 1], l),
                c.push(d),
                v = y(d, h[L - 1], l),
                h.push(v),
                g = O(h, [l], L, S[L - 1], k, "ma"),
                S[L] = $.extend({},
                C, g),
                t.push({
                    K: v,
                    D: S[L].ma3
                })
            }
            return t
        }
        function Z(e) {
            var t = 5,
            o = 10,
            u = 20,
            a = 26,
            l, c, h, p, d, v, m, g, y, w = [],
            E = [],
            S = {},
            x = {},
            T = [],
            N = [],
            C = [],
            k = [],
            L = 0,
            A = 0,
            M,
            _,
            D,
            P = t / 2.5 + 1,
            H = o / 2.5 + 1,
            B = u / 2.5 + 1,
            j = n(),
            F = [t, o, u],
            I = ["ma", "ma", "ma"],
            q = f(F, I);
            for (var R = 0; R < e.length; R++) {
                L = 0,
                A = 0,
                x = {},
                d = s(e[R]),
                m = r(e[R]),
                v = i(e[R]),
                l = (m + v + d) / 3,
                T.push(l),
                isNaN(T[R - 1]) ? (c = 0, h = 0) : (c = b(0, T[R - 1] - v), h = b(0, m - T[R - 1])),
                N.push(c),
                C.push(h),
                y = C.length < a ? C.length: a;
                for (g = 0; g < y; g++) L += C[R - g],
                A += N[R - g];
                A === 0 ? p = NaN: p = L / A * 100,
                k.push(p),
                S = O(k, F, R, E[R - 1], j, "ma"),
                E[R] = $.extend({},
                q, S),
                x.CR = p,
                (M = E[R - P]) ? x.MA1 = E[R - P]["ma" + t] : x.MA1 = NaN,
                (_ = E[R - H]) ? x.MA2 = E[R - H]["ma" + o] : x.MA2 = NaN,
                (D = E[R - B]) ? x.MA3 = E[R - B]["ma" + u] : x.MA3 = NaN,
                w.push(x)
            }
            return w
        }
        function tt() {
            var t = S,
            n = function(e) {
                var n = [];
                return e.length && (n = t(e)),
                n
            };
            return n.templateName = function(r) {
                return typeof r == "function" ? t = r: t = e.map(et, r),
                n
            },
            n
        }
        var n = function(e) {
            return typeof e == "function" ? e: function(e) {
                return e
            }
        },
        r = function(e) {
            return e.a
        },
        i = function(e) {
            return e.i
        },
        s = function(e) {
            return e.c
        },
        o = function(e) {
            return e.o
        },
        u = function(e) {
            return e.n
        },
        a = function(e) {
            return e.np
        },
        f = function(e, t) {
            var n = {},
            r = 0,
            i = 0,
            s = "";
            for (r = 0; r < e.length; r++) for (i = 0; i < t.length; i++) s = t[i] + e[r],
            n[s] = NaN;
            return n
        },
        l = function(e, t, n, s) {
            var o = [],
            u = {};
            for (var a = 0; a < t.length; a++) {
                var f = t[a],
                l = "maxR" + f,
                c = "minR" + f,
                h = 0,
                p = 1e6,
                d,
                v;
                if (n + 1 < f) break;
                if (s[l] !== null && s[l]) {
                    d = r(e[n]),
                    v = i(e[n]),
                    headA = r(e[n - f]),
                    headI = i(e[n - f]);
                    if (headA == s[l] || s[c] == headI) for (var m = 0; m < f; m++) d = r(e[n - m]),
                    v = i(e[n - m]),
                    h = d > h ? d: h,
                    p = v < p ? v: p;
                    else h = d > s[l] ? d: s[l],
                    p = v < s[c] ? v: s[c]
                } else for (var m = 0; m < f; m++) d = r(e[n - m]),
                v = i(e[n - m]),
                h = d > h ? d: h,
                p = v < p ? v: p;
                u[l] = h,
                u[c] = p
            }
            return u
        },
        c = function(e, n, r, i) {
            var o = [],
            u = {},
            a,
            f,
            l,
            c,
            h;
            for (var p = 0; p < n.length; p++) {
                l = n[p],
                f = "ma" + l,
                a = 0;
                if (r + 1 < l) break;
                if (i[f] !== null && i[f]) a = i[f] * l - s(e[r - l]) + s(e[r]);
                else for (h = 0; h < l; h++) a += s(e[r - h]);
                u[f] = t.decimalRound(a / l, 4)
            }
            return u
        },
        h = function(e, t, n, r) {
            var i = [],
            o = {},
            a,
            f,
            l,
            c,
            h,
            p,
            d,
            v,
            m,
            g,
            y,
            b;
            for (var w = 0; w < t.length; w++) {
                h = t[w],
                l = "upVol" + t[w],
                c = "downVol" + t[w],
                a = 0,
                f = 0;
                if (n < h) break;
                if (r[l] !== null && r[l]) m = s(e[n]) > s(e[n - 1]) ? ! 0: ! 1,
                v = s(e[n - h]) > s(e[n - h - 1]) ? ! 0: ! 1,
                b = r[l],
                rr2 = r[c],
                g = u(e[n]),
                y = u(e[n - h]),
                v && m ? (a = g + b - y, f = rr2) : ! v && m ? (a = g + b, f = rr2 - y) : v && ! m ? (a = b - y, f = g + rr2) : ! v && ! m && (a = b, f = g + rr2 - y);
                else for (d = 0; d < h; d++) s(e[n - d]) > s(e[n - d - 1]) ? a += u(e[n - d]) : f += u(e[n - d]);
                o[l] = a,
                o[c] = f
            }
            return o
        },
        p = {
            maxMinDataInRound: l,
            averageDataInRound: c,
            volStatusInRound: h
        },
        d = function(e, t, n, r) {
            var i = f(t, r),
            s = [],
            o = {};
            for (var u = 0; u < e.length; u++) o = p[n](e, t, u, s[u - 1]),
            s[u] = $.extend({},
            i, o);
            return s
        },
        m = function(e, t, n) {
            return e ? t: n
        },
        O = function(e, n, r, i, s, o) {
            var u = [],
            a = {},
            f,
            l,
            c,
            h;
            for (var p = 0; p < n.length; p++) {
                c = n[p],
                l = o + c,
                f = 0;
                if (r + 1 < c || e.length < c) break;
                if (i[l] !== null && i[l]) f = i[l] * c - s(e[r - c]) + s(e[r]);
                else for (h = 0; h < c; h++) f += s(e[r - h]);
                a[l] = t.decimalRound(f / c, 4)
            }
            return a
        },
        M = function(e, n, r, i, s, o) {
            var u = [],
            a = {},
            f,
            l,
            c,
            h,
            p,
            d;
            for (var v = 0; v < n.length; v++) {
                c = n[v],
                l = o + c,
                f = 0;
                if (r + 1 < c) break;
                if (i[l] !== null && i[l]) f = i[l] * c - s(e[r - c]) + s(e[r]);
                else for (h = 0; h < c; h++) f += s(e[r - h]);
                a[l] = t.decimalRound(f / c, 4),
                p = a[l],
                d = 0;
                for (h = 0; h < c; h++) d += w(e[r - h] - p);
                d = d / c * .015,
                a.r = t.decimalRound((s(e[r]) - p) / d, 3)
            }
            return a
        },
        z = function(e, t, n, r, i, s, o, u) {
            var a = [],
            f = {},
            l = 0,
            c,
            h,
            p,
            d,
            v = 0,
            m = 0,
            g;
            if (o) var y = o.length;
            var b = 0,
            E = [];
            h = r;
            for (d = 0; d < h; d++) l += e[s - d - 1],
            v += t[s - d - 1],
            m += n[s - d - 1];
            DI1 = v * 100 / l,
            DI2 = m * 100 / l,
            f.tempADX = w(DI2 - DI1) / (DI1 + DI2) * 100,
            f.DI1 = DI1,
            f.DI2 = DI2,
            f.ADX = NaN,
            f.ADXR = NaN;
            if (s >= r + i) {
                b = f.tempADX;
                for (p = 0; p < i - 1; p++) b += o[y - p - 1];
                f.ADX = b / i,
                f.ADXR = (f.ADX + u[u.length - i]) / 2
            }
            return f
        },
        J = function(e, n, r, i, s, o) {
            var u = [],
            a = {},
            f,
            l,
            c,
            h;
            for (var p = 0; p < n.length; p++) {
                c = n[p],
                l = o + c,
                f = 0;
                if (r + 1 < c || e.length < c) break;
                if (i[l] !== null && i[l]) f = i[l] * c - s(e[r - c]) + s(e[r]);
                else for (h = 0; h < c; h++) f += s(e[r - h]);
                a[l] = t.decimalRound(f / c, 4)
            }
            return a
        },
        G = function(e, t, n, s) {
            var o = [],
            u = {};
            for (var a = 0; a < t.length; a++) {
                var f = t[a],
                l = "maxR" + f,
                c = "minR" + f,
                h = 0,
                p = 1e6,
                d,
                v;
                if (n + 1 < f) break;
                if (s[l] !== null && s[l]) {
                    d = r(e[n]),
                    v = i(e[n]),
                    headA = r(e[n - f]),
                    headI = i(e[n - f]);
                    if (headA == s[l] || s[c] == headI) for (var m = 0; m < f; m++) d = r(e[n - m]),
                    v = i(e[n - m]),
                    h = d > h ? d: h,
                    p = v < p ? v: p;
                    else h = d > s[l] ? d: s[l],
                    p = v < s[c] ? v: s[c]
                } else for (var m = 0; m < f; m++) d = r(e[n - m]),
                v = i(e[n - m]),
                h = d > h ? d: h,
                p = v < p ? v: p;
                u[l] = h,
                u[c] = p
            }
            return u
        },
        et = {
            MACD: S,
            KDJ: x,
            RSI: T,
            BOLL: N,
            PSY: D,
            MFI: P,
            ADTM: H,
            ROC: B,
            BBI: j,
            ARBR: F,
            EMV: I,
            LWR: q,
            DPO: R,
            "W&R": C,
            ASI: L,
            BIAS: k,
            VR: A,
            CCI: X,
            MTM: _,
            CYS: U,
            OBV: V,
            DMA: K,
            TRIX: Q,
            SKDJ: Y,
            CR: Z,
            DMI: W
        },
        nt = function() {
            return tt()
        };
        return nt
    }),
    n("fixSupportCanvas", [], function() {
        var e = function(e) {
            Object.prototype.getContext || window.G_vmlCanvasManager && (e.getContext = window.G_vmlCanvasManager.initElement(e).getContext)
        };
        return e
    }),
    n("detectPR", ["fixSupportCanvas"], function(e) {
        function t(t) {
            window.globalDetectList = window.globalDetectList || {};
            if (globalDetectList[t]) return;
            globalDetectList[t] = ! 0;
            var t = document.getElementById(t);
            e(t);
            var n = t.getContext("2d"),
            r = window.devicePixelRatio || 1,
            i = n.webkitBackingStorePixelRatio || n.mozBackingStorePixelRatio || n.msBackingStorePixelRatio || n.oBackingStorePixelRatio || n.backingStorePixelRatio || 1,
            s = r / i,
            o,
            u;
            s !== 1 && (o = t.width, u = t.height, t.width = o * s, t.height = u * s, t.style.width = o + "px", t.style.height = u + "px", n.scale(s, s))
        }
        return t
    }),
    n("techConfig", [], function() {
        var e = {
            MACD: {
                MACD: "",
                DEA: "#ff0045",
                DIFF: "#1478fd"
            },
            KDJ: {
                K: "#1478fd",
                D: "#ff0045",
                J: "#9966dd"
            },
            RSI: {
                RSI6: "#1478fd",
                RSI12: "#ff0045",
                RSI24: "#9966dd"
            },
            BOLL: {
                MID: "#1478fd",
                UPPER: "#ff0045",
                LOWER: "#9966dd"
            },
            MTM: {
                MTM: "#1478fd",
                MAMTM: "#ff0045"
            },
            BIAS: {
                BIAS3: "#1478fd",
                BIAS12: "#ff0045",
                BIAS24: "#9966dd"
            },
            CCI: {
                CCI: "#1478fd"
            },
            SKDJ: {
                K: "#1478fd",
                D: "#ff0045"
            },
            DMA: {
                DDD: "#1478fd",
                AMA: "#ff0045"
            },
            "W&R": {
                WR1: "#1478fd",
                WR2: "#ff0045"
            },
            DMI: {
                DI1: "#1478fd",
                DI2: "#ff0045",
                ADX: "#9966dd",
                ADXR: "#e89055"
            },
            CR: {
                CR: "#1478fd",
                MA1: "#ff0045",
                MA2: "#9966dd",
                MA3: "#e89055"
            },
            TRIX: {
                TRIX: "#1478fd",
                TRMA: "#ff0045"
            },
            OBV: {
                OBV: "#1478fd"
            },
            CYS: {
                CYS: "#1478fd"
            },
            PSY: {
                PSY: "#1478fd"
            },
            MFI: {
                MFI: "#1478fd"
            },
            VR: {
                VR: "#1478fd"
            },
            ASI: {
                ASI: "#1478fd",
                ASIT: "#ff0045"
            },
            ADTM: {
                ADTM: "#1478fd",
                MAADTM: "#ff0045"
            },
            ROC: {
                ROC: "#1478fd",
                ROCMA: "#ff0045"
            },
            BBI: {
                BBI: "#1478fd",
                A: "#ff0045"
            },
            ARBR: {
                AR: "#1478fd",
                BR: "#ff0045"
            },
            EMV: {
                EMV: "#1478fd",
                MAEMV: "#ff0045"
            },
            DPO: {
                DPO: "#1478fd",
                MADPO: "#ff0045"
            },
            LWR: {
                LWR1: "#1478fd",
                LWR2: "#ff0045"
            },
            defaultColor: "#555"
        };
        return e
    }),
    n("drawKLine", ["arrayToolkit", "barBuilder", "scale", "kgrid", "dashedLine", "canvasLine", "techCal", "detectPR", "fixSupportCanvas", "techConfig", "hexinStock/util"], function(e, t, n, r, i, s, o, u, a, f, l) {
        function c(i, c, h, p) {
            function ut(e) {
                x.clearRect(0, 0, y, b);
                var t = "linear",
                n, r;
                for (var i in e[0]) i == "MACD" ? n = s().x(function(e, t) {
                    return at(t) + at.barPadding() / 2 + at.barRangeBand() / 2
                }).y(function(e) {
                    return St(e[i])
                }).canvasLineTemplate("linear-bar-macd").strokeStyle("#1FA3C9").bottom(St(0)).ctx(x) : (r = f[T][i] || f.defaultColor, n = s().x(function(e, t) {
                    return at(t) + at.barPadding() / 2 + at.barRangeBand() / 2
                }).y(function(e) {
                    return St(e[i])
                }).canvasLineTemplate("linear").strokeStyle(r).ctx(x)),
                n(e);
                $.each(St.ticks(3), function(e, t) {
                    $(".indexLabel > div").eq(e).html(t.toFixed(2))
                })
            }
            function Et(e) {
                T = e;
                var t = Array.prototype.concat.call([], p, c);
                mt = o().templateName(e),
                gt = mt(t),
                wt = gt.slice( - c.length),
                yt = - Infinity,
                bt = Infinity;
                for (st = 0; st < wt.length; st++) {
                    var r = wt[st];
                    for (var i in r) yt < r[i] && (yt = r[i]),
                    bt > r[i] && (bt = r[i])
                }
                St = n().domain([bt, yt]).range([L + j + O + M + H * 2, L + j + O + M + H + 17]),
                ut(wt)
            }
            function _t() {
                $("#canvasPanel").on(At, function(e, t) {
                    e.preventDefault(),
                    e = e.originalEvent || e,
                    e.touches && e.touches.length ? e = e.touches[0] : e.changedTouches && e.changedTouches.length && (e = e.changedTouches[0]);
                    var n = $("#canvasPanel"),
                    r = global.canvasWidth,
                    i = global.canvasHeight;
                    $("#kTipma").length > 0 || ($('<div id="kTip" /><div id="kTipma" /><div id="kvolTip" /><div id="kindexTip" /><div id="kTipTime" /><div id="kTipPrice" /><canvas id="canvasMask" />').css({
                        position: "absolute",
                        left: 0,
                        top: 0
                    }).appendTo(n), $("#canvasMask").get(0).width = global.canvasWidth, $("#canvasMask").get(0).height = global.canvasHeight, u("canvasMask"));
                    var s = 70;
                    $("#kTip").css({
                        left: 5,
                        top: 1
                    }),
                    $("#kTipma").css({
                        left: 5,
                        top: L
                    }),
                    $("#kvolTip").css({
                        left: 5,
                        top: L + j + O + M
                    }),
                    $("#kindexTip").css({
                        left: 5,
                        top: L + j + O + M + H
                    }),
                    $("#canvasMask").css("z-index", 9).attr({
                        width: r,
                        height: i
                    }),
                    $("#kTipTime, #kTipPrice").css({
                        position: "absolute",
                        background: "#555",
                        color: "#fff",
                        height: "18px",
                        "line-height": "16px",
                        width: s,
                        "text-align": "center",
                        "float": "left",
                        "z-index": 99
                    });
                    var o = (at.range()[1] - at.range()[0]) / (at.domain()[1] - at.domain()[0]),
                    h = (e.offsetX - (it[0][0] - at.barRangeBand() / 2) / 2) / o >> 0;
                    t && (h = c.length - 1);
                    var d = $("#canvasMask").get(0);
                    a(d);
                    var v = d.getContext("2d");
                    v.clearRect(0, 0, global.canvasWidth, global.canvasHeight),
                    $(d).hide(),
                    d.offsetHeight,
                    $(d).show();
                    if (e.offsetX <= 0 || e.offsetX > r - C.right || e.offsetY < L || e.offsetY > L + j + O + M + H * 2) {
                        $("#kTipTime, #kTipPrice").hide();
                        return
                    }
                    var m = L,
                    g = r - C.right + .5,
                    y = L + j + O + M + B + H + .5,
                    b = .5;
                    v.beginPath(),
                    v.strokeStyle = "#A0A0A0",
                    h >= c.length && (h = c.length - 1);
                    if (c[h]) {
                        t || (v.dashedLine(it[h][0], m, it[h][0], y, [2, 3]), v.dashedLine(b, vt(c[h].c), g, vt(c[h].c), [2, 3])),
                        v.stroke();
                        var w = "<%t%> 开：<%o%> 高：<%a%> 低：<%i%> 收：<%c%> 涨跌：<%zhang%> 涨幅：<%zhangfu%>",
                        E = "<%ma5%> <%ma10%> <%ma30%> <%ma60%>",
                        S = "成交量 量:<%n%>",
                        x = function(e, t) {
                            return e.replace(/<%([^%>]+)?%>/g, function(n, r) {
                                var i = t[h][r],
                                s = t[h].c,
                                o;
                                t[h - 1] ? o = t[h - 1].c: p && p.length > 0 ? o = p[p.length - 1].c: o = t[h].o;
                                var u;
                                if (r == "t") return i.substring(0, 8) + "," + i.substring(8, 12);
                                r == "n" && (t[0].code.split("_")[0].match(/^(usa|hk)/i) && (i *= 100), t[0].code.split("_")[0].match(/^(176|177|178|169|168|170|185|184|186|189)/) ? i = l.formatUnit(i, 2, ! 0) : i = l.formatUnit(i / 100, 2, ! 0));
                                if (r.indexOf("ma") == 0) {
                                    if (i) {
                                        var a = r.replace(/[^0-9]/ig, ""),
                                        u = W[a];
                                        return '<span style="color: ' + u + '">MA' + a + ":" + i.toFixed(2) + "</span>"
                                    }
                                    return ""
                                }
                                if (e.indexOf("指标") !== - 1) return u = f[T][r],
                                i = isNaN(i) ? 0: i,
                                '<span style="color:' + u + '">' + r + ":" + i.toFixed(2) + "</span>";
                                var c = t[h - 1] ? t[h - 1].c: t[h].o;
                                i >= c ? u = q: i < c && (u = R),
                                r == "zhang" && (i = (s - o).toFixed(2)),
                                r == "zhangfu" && (i = ((s - o) / o * 100).toFixed(2) + "%");
                                if (r == "zhang" || r == "zhangfu") + i.replace(/[%]/ig, "") >= 0 ? u = q: u = R;
                                return '<span style="color:' + u + '">' + i + "</span>"
                            })
                        };
                        $("#kTip").html(x(w, c)),
                        $("#kTipma").html(x(E, c)),
                        $("#kvolTip").html(x(S, c));
                        var N = it[h][0] - s / 2;
                        N <= 0 ? N = 0: N >= r - C.right - s && (N = r - C.right - s);
                        var k = c[h].t;
                        t ? $("#kTipTime, #kTipPrice").hide() : ($("#kTipTime").html(k.substring(0, 4) + "-" + k.substring(4, 6) + "-" + k.substring(6, 8)).css({
                            left: N,
                            top: L + j + 20 + 1
                        }).show(), $("#kTipPrice").css({
                            left: r - C.right,
                            top: vt(c[h].c),
                            width: "auto",
                            background: c[h].c >= c[h].o ? q: R,
                            "margin-top": "-9px",
                            padding: "0 5px"
                        }).html(c[h].c > 1e4 ? c[h].c.toFixed(1) : c[h].c.toFixed(2)).show()),
                        typeof klTouchmoveCallback == "function" && ! t && klTouchmoveCallback(c, h)
                    }
                    if (gt && gt[h]) {
                        var A = "指标量 ";
                        for (var _ in gt[h]) A += "<%" + _ + "%> ";
                        $("#kindexTip").html(x(A, wt))
                    }
                }).on(Ot, function() {
                    $("#kTipTime, #kTipPrice").html("").hide();
                    var e = $("#canvasMask").get(0),
                    t = e.getContext("2d");
                    t.clearRect(0, 0, global.canvasWidth, global.canvasHeight),
                    $(e).hide(),
                    e.offsetHeight,
                    $(e).show(),
                    typeof klTouchendCallback == "function" && klTouchendCallback()
                }).on(Mt, function(e) {
                    e = e.originalEvent || e,
                    e.preventDefault()
                }).trigger("touchmove", [!0])
            }
            i = i || $("#tcanvas");
            var d = i.data("klconfig");
            window.global = window.global || {},
            global.canvasWidth = global.canvasWidth || document.getElementById("tcanvas").width,
            global.canvasHeight = global.canvasHeight || document.getElementById("tcanvas").height;
            var c = c,
            v = c[0].code.split("_")[0],
            m = document.getElementById("tcanvas");
            $(m).siblings(":not(.unRemovePanel, #kTip, #kTipma, #kvolTip, #kTipTime, #kTipPrice, #canvasMask, #kindexTip, .opacity-stock-name)").remove(),
            $("#canvasPanel").unbind().css("font-family", "Tahoma"),
            $(m).css({
                position: "absolute",
                left: 0,
                top: 0
            }),
            ! $("#kGridMask").length > 0 && $("#canvasPanel").prepend($('<canvas id="kGridMask" />').css({
                position: "absolute",
                left: 0,
                top: 0
            })),
            d.drawStartFn && d.drawStartFn();
            var g = m.getContext("2d"),
            y = global.canvasWidth,
            b = global.canvasHeight;
            g.clearRect(0, 0, y, b),
            $(m).hide(),
            m.offsetHeight,
            $(m).show(),
            $("#canvasMask").length > 0 && $("#canvasMask").get(0).getContext("2d").clearRect(0, 0, y, b);
            var w = $("#kGridMask").get(0);
            w.width = y,
            w.height = b;
            var E;
            a(w),
            E = w.getContext("2d"),
            ! $("#kTech").length > 0 && $("#canvasPanel").append($('<canvas id="kTech" />').css({
                position: "absolute",
                left: 0,
                top: 0
            }));
            var S = $("#kTech").get(0);
            S.width = y,
            S.height = b,
            a(S);
            var x = S.getContext("2d"),
            T = "MACD";
            u("tcanvas"),
            u("kGridMask"),
            u("kTech");
            var N = {
                margin: {
                    top: 0,
                    right: 52,
                    bottom: 0,
                    left: 0
                }
            },
            C = N.margin,
            k = d.showIndex,
            L = 19,
            A = 0,
            O = 15,
            M = 24,
            _ = k ? 26: 20,
            D = 19,
            P = b - L - A - O - M - _,
            H = k ? P * .2 >> 0: 0,
            B = P * .2 >> 0,
            j = P - H - B,
            F = c.length >= D ? [1 / 6, .5, 5 / 6] : [1 / 6],
            I = c[c.length - 1].o,
            q = "#ec5f4c",
            R = "#6ba583",
            U = "#900f00",
            z = "#15713b",
            W = {
                5: "#1F6195",
                10: "#E0AC58",
                30: "#9C73AF",
                60: "#6CA582"
            },
            X,
            V,
            J,
            K,
            Q = [],
            G = [],
            Y = [],
            Z,
            et = [],
            tt,
            nt,
            rt = [],
            it,
            st;
            v == 41 && (j = P);
            var ot = function(e) {
                var t, n, r, i, s, o = Infinity,
                u = - Infinity,
                a = Infinity,
                f = - Infinity,
                l, c;
                for (r = 0; r < e.length; r++) for (i in e[r]) {
                    s = e[r][i];
                    if (s == null) continue;
                    i !== "s" && i !== "t" && i !== "n" && i !== "h" && i !== "np" && (i == "o" || i == "a" || i == "i" || i == "c" ? (u < s && (u = s, indexmax = r), o > s && (o = s, indexmin = r)) : (f < s && (f = s), a > s && (a = s)))
                }
                return f < u && (f = u),
                + a > o && (a = o),
                {
                    min: o,
                    max: u,
                    allmin: a,
                    allmax: f,
                    minindex: indexmin,
                    maxindex: indexmax
                }
            },
            at = n().domain([0, c.length <= D ? D: c.length]).range([5, y - C.right]).rangeRoundBands(.2),
            ft = n().domain([e.min(c, function(e) {
                return e.n
            }), e.max(c, function(e) {
                return e.n
            }) * 1.4]).range([L + j + O + M + (k ? H - 2: B), L + j + O + M]),
            lt = ot(c),
            ct = lt.allmin,
            ht = lt.allmax,
            pt = lt.minindex,
            dt = lt.maxindex,
            vt = scaleY = n().domain([ct - (ht - ct) * .1, ht + (ht - ct) * .2]).range([L + j, L]),
            mt,
            gt,
            yt,
            bt,
            wt,
            St = n().domain([bt, yt]).range([L + j + O + M + H * 2, L + j + O + M + H + 17]);
            k && Et($(m).data("techItem") || T);
            var xt = function() {
                var e = it[pt],
                t = it[dt],
                n = e[0],
                r = e[2],
                i = t[0],
                s = t[1],
                o,
                u,
                a,
                f;
                a = $('<div class="ktip-min-value" />').css({
                    position: "absolute",
                    left: n,
                    top: r,
                    "text-decoration": "overline"
                }).html(c[pt].i.toFixed(2)).appendTo("#canvasPanel"),
                at(pt) > y / 2 && (o = a.width(), a.css({
                    "margin-left": - o
                })),
                f = $('<div class="ktip-max-value" />').css({
                    position: "absolute",
                    left: i,
                    height: 20,
                    top: s,
                    "line-height": "20px",
                    "margin-top": - 20,
                    "text-decoration": "underline"
                }).html(c[dt].a.toFixed(2)).appendTo("#canvasPanel"),
                at(dt) > y / 2 && (u = f.width(), f.css({
                    "margin-left": - u
                }))
            },
            Tt = function() {
                var e = E;
                e.beginPath(),
                e.strokeStyle = "#ccc",
                e.moveTo(y - C.right + .5, .5),
                e.lineTo(y - C.right + .5, b - _ + 1),
                e.moveTo(.5, L + j + O + M + .5),
                e.lineTo(y - C.right, L + j + O + M + .5),
                e.stroke(),
                e.beginPath(),
                e.strokeStyle = "#CBCBCB",
                e.strokeRect(.5, .5, y - 1, b - 1),
                e.stroke()
            },
            Nt = function() {
                X.drawAxis({
                    direction: "y",
                    position: "right",
                    formatter: function(e) {
                        return e >= 1e4 ? ( + e).toFixed(0) : e > 1e3 && e < 1e4 ? ( + e).toFixed(1) : e
                    }
                }),
                X.drawAxis({
                    direction: "x",
                    position: "bottom",
                    xText: G
                }),
                V.drawAxis({
                    direction: "y",
                    position: "right",
                    autoIndent: ! 0,
                    formatter: function(e) {
                        return e /= 100,
                        e >= (temp = Math.pow(10, 8)) ? (e / temp).toFixed(0) + "亿": e >= (temp = Math.pow(10, 4)) ? (e / temp).toFixed(0) + "万": e
                    }
                }),
                k && J.drawAxis({
                    direction: "y",
                    autoIndent: ! 0,
                    position: "right",
                    cls: "indexLabel"
                })
            },
            Ct = function() {
                K = scaleY.ticks(5);
                for (st = 0; st < K.length; st++) Q.push(vt(K[st]));
                for (st = 0; st < F.length; st++) {
                    var e = c[c.length * F[st] >> 0].t;
                    G.push(e.substring(0, 4) + "-" + e.substring(4, 6) + "-" + e.substring(6, 8))
                }
                Z = ft.ticks(3),
                Z[Z.length - 1] / 100 > 1e4 ? tt = "万手": tt = "手";
                for (st = 0; st < Z.length; st++) et.push(ft(Z[st]));
                nt = St.ticks(3);
                for (st = 0; st < nt.length; st++) rt.push(St(nt[st]))
            },
            kt = function() {
                var n = t().x(function(e, t) {
                    return at(t) + at.barPadding() / 2
                }).y(function(e) {
                    return ft(e.n)
                }).width(function(e) {
                    return at.barRangeBand()
                }).height(function(e) {
                    return ft.range()[0] - Math.floor(ft(e.n))
                }).canvasBarTemplate("kBar").lineWidth(1).barColor(["s", ["ab", q], ["be", R], ["eq", q]]).barBorderColor([["ab", U], ["be", z], ["eq", U]]).ctx(g),
                r = t().x(function(e, t) {
                    return at(t) + at.barPadding() / 2
                }).y(function(t) {
                    return vt(e.max([t.o, t.c]))
                }).width(function(e) {
                    return at.barRangeBand()
                }).height(function(e) {
                    return Math.abs(vt(e.o) - vt(e.c))
                }).lineX(function(e, t) {
                    return at(t) + at.barPadding() / 2 + at.barRangeBand() / 2
                }).lineYTop(function(e) {
                    return vt(e.a)
                }).lineYBottom(function(e) {
                    return vt(e.i)
                }).canvasBarTemplate("kLine").lineWidth(1).barColor(["s", ["ab", q], ["be", R], ["eq", q]]).barBorderColor([["ab", U], ["be", z], ["eq", U]]).ctx(g);
                r(c),
                n(c),
                it = r.linePosOnkLine();
                var i = function() {
                    var e = {};
                    for (var t = 0; t < c.length; t++) e[c[t].t] = it[t];
                    return e
                };
                window.kLinePos = i(),
                E.strokeStyle = "#ddd",
                E.beginPath();
                for (st = 0; st < F.length; st++) {
                    var o = c.length * F[st] >> 0,
                    u = it[o][0];
                    Y.push(u),
                    E.dashedLine(u, .5, u, L + j + M + O + H * 2 + .5, [1, 4])
                }
                E.stroke();
                var a = s().x(function(e, t) {
                    return at(t)
                }).y(function(e) {
                    return St(e.n)
                }).canvasLineTemplate("linear").strokeStyle("#1FA3C9").ctx(g);
                a.y(function(e) {
                    return vt(e.ma5)
                }).strokeStyle(W[5]),
                a(c),
                a.y(function(e) {
                    return vt(e.ma10)
                }).strokeStyle(W[10]),
                a(c),
                a.y(function(e) {
                    return vt(e.ma30)
                }).strokeStyle(W[30]),
                a(c),
                a.y(function(e) {
                    return vt(e.ma60)
                }).strokeStyle(W[60]),
                a(c)
            },
            Lt = function() {
                X = new r(E, {
                    grid: {
                        panel: m,
                        x: .5,
                        y: .5,
                        width: y - C.right,
                        height: L + j + 20,
                        color: "#ccc",
                        pos: Q,
                        scale: [at, scaleY],
                        showTextLine: ! 0,
                        xTextPos: Y
                    }
                }),
                V = new r(E, {
                    grid: {
                        panel: m,
                        x: .5,
                        y: L + j + O + M,
                        width: y - C.right,
                        height: H + B,
                        pos: et,
                        scale: [at, ft],
                        tickDecimals: 0
                    }
                }),
                J = new r(E, {
                    grid: {
                        panel: m,
                        x: .5,
                        y: L + j + O + M + H,
                        width: y - C.right,
                        height: H,
                        pos: rt,
                        scale: [at, St],
                        tickDecimals: 2
                    }
                })
            };
            $("#changeColor").click(function(e) {
                bbb.barColor(["s", ["ab", "#D45947"], ["be", "#66A588"], ["eq", "red"]]),
                bbb(c)
            });
            var At = "touchmove" in window ? "touchmove": "mousemove",
            Ot = "touchend" in window ? "touchend": "mouseout",
            Mt = "touchstart" in window ? "touchstart": "mousedown";
            this.draw = kt,
            k && $(m).data("tech", Et),
            kt(),
            Lt(),
            Ct(),
            Nt(),
            Tt(),
            xt(),
            _t(),
            d.drawEndFn && d.drawEndFn()
        }
        return c
    }),
    n("KPainter", ["mathToolkit", "KLineProvider", "drawKLine", "arrayToolkit"], function(e, t, n, r) {
        function f(e, t) {
            clearTimeout(e.tId),
            e.tId = setTimeout(function() {
                e.call(t)
            },
            100)
        }
        var i = function(e, t) {
            var n = e.className,
            r = n != "" ? " ": "",
            i = n + r + t;
            e.className = i
        },
        s = function(e, t) {
            var n = " " + e.className + " ";
            n = n.replace(/(\s+)/gi, " ");
            var r = n.replace(" " + t + " ", " ");
            r = r.replace(/(^\s+)|(\s+$)/g, ""),
            e.className = r
        },
        o = function(e, t) {
            var n = e.className,
            r = n.split(/\s+/),
            i = 0;
            for (i in r) if (r[i] == t) return ! 0;
            return ! 1
        },
        u = function(e, t) {
            var n, r;
            $.each(e, function(e, u) {
                n = t[e],
                r = document.getElementById(n),
                ! u && ! o(r, t.disabledClass) ? (i(r, t.disabledClass), r.disabled = ! 0) : u && o(r, t.disabledClass) && (s(r, t.disabledClass), r.disabled = ! 1)
            })
        },
        a = function(e) {
            var r = {
                drawDataLength: 60,
                indicatorNeedMoreLength: 60,
                movePerDistance: .34,
                zoom: .25,
                minNumInScreen: 20,
                eventBtn: {
                    goNext: "nextEvent",
                    goPre: "preEvent",
                    zoomIn: "zoomInEvent",
                    zoomOut: "zoomOutEvent",
                    disabledClass: "disabled"
                },
                animateEnable: ! 0,
                getStockInfo: function() {},
                startDraw: function() {}
            },
            i = e.klineConfig;
            delete e.klineConfig,
            this.configs = $.extend({},
            r, e),
            i.drawDataLength = this.configs.drawDataLength,
            this.draw = {
                startDateInHistory: 0,
                startDate: "",
                goPreNeedNum: 0,
                goNextNeedNum: 0,
                drawData: [],
                preHandle: "",
                preHandleInHistory: 0
            },
            this.historyData = {
                data: [],
                startDate: "",
                endDate: "",
                lastDate: "",
                num: 0
            },
            this.eventStatus = {
                goPre: ! 0,
                goNext: ! 0,
                zoomIn: ! 0,
                zoomOut: ! 0
            },
            this.zoomSpeed = 0,
            this.maxZoomIn = 300;
            var s = this,
            o = 1;
            this.kLineDataObj = new t(i),
            this.kLineDataObj.on("getFirstGetData", function(e) {
                s.kLineDataObj.configs.historyDataAndNum && (document.getElementById(s.configs.eventBtn.zoomIn).style.display = "none", document.getElementById(s.configs.eventBtn.zoomOut).style.display = "none"),
                s.initDrawConfigs(e, s.kLineDataObj.historyStart, s.kLineDataObj.total, s.kLineDataObj.currentDate)
            }),
            this.kLineDataObj.getStockInfo(s.configs.getStockInfo),
            this.kLineDataObj.on("getDataByKeeping", function(e) {
                s.configs.startDraw();
                var t = s.getPosByDrawDataLength(e),
                r;
                if (e.length >= s.configs.drawDataLength && ! s.kLineDataObj.configs.betweenTwoDays) r = e.slice( - s.configs.drawDataLength);
                else if (s.kLineDataObj.configs.betweenTwoDays) {
                    var i = s.kLineDataObj.configs.betweenTwoDays[0];
                    for (var u = 0; u < e.length; u++) e[u].t === i && (r = e.slice(u))
                } else r = e.slice(0);
                var a = s.getIndicatorArr(e, t);
                if (o) n(null, r, null, a),
                o = 0;
                else {
                    var f = r[r.length - 1],
                    l = s.draw.drawData.length;
                    s.draw.drawData[l - 1].t === s.historyData.seemLastDate && (s.draw.drawData.pop(), s.draw.drawData.push(f), n(null, s.draw.drawData.slice(0), null, a))
                }
            }),
            this.kLineDataObj.getData()
        };
        a.prototype.getPosByDrawDataLength = function(e) {
            return e.length - this.configs.drawDataLength
        },
        a.prototype.getMaxMa = function() {
            var e = this.kLineDataObj.configs.ma;
            return + e[e.length - 1]
        },
        a.prototype.checkConfigsIsLegal = function(e) {
            var t = this.configs.drawDataLength;
            if (t) {
                if (typeof t != "number") throw "drawDataLength  need numeber type !";
                if (t > this.kLineDataObj.numInLastJsFile && t < e.length) throw "drawDataLength couldn't be bigger than the 'num' in last.js!"
            }
        },
        a.prototype.initDrawConfigs = function(e, t, n, r) {
            this.checkConfigsIsLegal(e);
            var i = this.getPosByDrawDataLength(e),
            s = [];
            e.length <= this.configs.drawDataLength ? (s = e, i = 0) : s = e.slice(i);
            var o = Math.ceil(s.length * (1 - this.configs.movePerDistance)) - 1;
            this.refreshHistoryData({
                data: e,
                startDate: e[0].t,
                endDate: e[e.length - 1].t,
                lastDate: e[e.length - 1].t,
                seemLastDate: r,
                historyStart: t,
                historyTotal: + n,
                num: e.length
            }),
            this.refreshAfterDraw({
                startDate: s[0].t,
                startDateInHistory: i,
                drawData: s,
                goPreNeedNum: s.length + this.getMaxMa() - 1,
                goNextNeedNum: s.length,
                preHandle: s[o].t,
                preHandleInHistory: o + i,
                preHandleInTrueDrawData: o
            }),
            this.initZoomSpeed(),
            this.initMoveSpeed(),
            this.refreshEventStatus(n)
        },
        a.prototype.refreshEventStatus = function(e) {
            e = e || this.kLineDataObj.total;
            var t = {},
            n = this,
            r = this.draw.drawData.length,
            i = this.draw.drawData[r - 1],
            s = this.maxZoomIn,
            o = this.configs.zoom,
            a = this.configs.minNumInScreen,
            f = Math.floor(o * a) + a;
            this.draw.startDate === this.historyData.historyStart ? t.goPre = ! 1: t.goPre = ! 0,
            i.t === this.historyData.seemLastDate ? t.goNext = ! 1: t.goNext = ! 0,
            r >= s || this.draw.startDate === this.historyData.historyStart ? t.zoomOut = ! 1: t.zoomOut = ! 0,
            r < f ? t.zoomIn = ! 1: t.zoomIn = ! 0,
            $.each(t, function(e, t) {
                n.eventStatus[e] = t
            }),
            u(this.eventStatus, this.configs.eventBtn)
        },
        a.prototype.initZoomSpeed = function() {
            this.zoomSpeed = this.draw.drawData.length * this.configs.zoom
        },
        a.prototype.initMoveSpeed = function() {
            var e = this.draw.drawData.slice(0);
            this.moveSpeed = this.getMoveDataNum(e)
        },
        a.prototype.refreshConfigs = function(e, t) {
            var n = e.slice(0),
            r = n[0].t,
            i,
            s,
            o = this.historyData.data,
            u = Math.ceil(n.length * (1 - this.configs.movePerDistance)) - 1,
            a = n[u].t;
            for (var f = 0; f < o.length; f++) if (o[f].t == r) i = f;
            else if (o[f].t === a) {
                s = f;
                break
            }
            return this.refreshAfterDraw({
                startDate: r,
                startDateInHistory: i,
                drawData: n,
                goPreNeedNum: n.length + this.getMaxMa() - 1,
                goNextNeedNum: n.length,
                preHandle: a,
                preHandleInHistory: s,
                preHandleInTrueDrawData: u
            }),
            i
        },
        a.prototype.tryGetFromHistory = function() {
            var e = this.draw.preHandleInHistory,
            t = this.draw.drawData.length,
            n = this.draw.goNextNeedNum,
            r = e - n + 1,
            i = this.getMaxMa();
            if (r >= i - 1) {
                var s = this.historyData.data.slice(r, r + t);
                return s
            }
            return this.historyData.num >= this.historyData.historyTotal ? (r < 0 && (r = 0), this.historyData.data.slice(r, r + t)) : ! 1
        },
        a.prototype.getIndicatorArrFromHistory = function() {
            var e = this.draw.preHandleInHistory,
            t = this.draw.goNextNeedNum,
            n = e - t + 1;
            return this.getIndicatorArr(this.historyData.data, n)
        },
        a.prototype.getIndicatorArr = function(e, t) {
            var n;
            return t < 0 ? [] : (t - this.configs.indicatorNeedMoreLength > 0 ? n = t - this.configs.indicatorNeedMoreLength: n = 0, e.slice(n, t))
        },
        a.prototype.getFromHistory = function(e, t, r, i) {
            var s = this;
            r = r || "getAfterDate";
            var o = this.draw.startDateInHistory,
            u = this.historyData.data,
            a = this.getMaxMa(),
            f = [],
            l = [],
            c = [],
            h = u[u.length - 1].t,
            p = this.historyData.num - this.draw.startDateInHistory <= t && this.historyData.endDate !== this.historyData.seemLastDate ? ! 0: ! 1,
            d = o - t;
            if (r == "getAfterDate" && ! p) {
                for (var v = o; v <= o + t; v++) {
                    u[v].t && f.push(u[v]);
                    if (u[v].t === this.historyData.seemLastDate) break
                }
                return f
            }
            if (r != "getAfterDate" || ! p) {
                if (this.historyData.num >= this.historyData.historyTotal) d < 0 && (d = 0);
                else if (d <= a - 1) return ! 1;
                for (d; d < o; d++) u[d].t && f.push(u[d]);
                return f
            }
            o + 2 > a ? l = u.slice(o - a + 2, o + 1) : l = u.slice(0, o + 1),
            this.kLineDataObj.getDataByYearsAndNum([t + 80, l[0].t, "getAfterData"], function(e) {
                for (var r = 0; r < e.length; r++) e[r].t > h && c.push(e[r]);
                s.refreshHistoryData({
                    data: c
                },
                "pushBackward");
                for (var r = o; r <= o + t; r++) {
                    s.historyData.data[r].t && f.push(s.historyData.data[r]);
                    if (s.historyData.data[r].t === s.historyData.seemLastDate || ! s.historyData.data[r + 1]) {
                        s.historyData.data[r].t === s.kLineDataObj.dataArray[s.kLineDataObj.dataArray.length - 1].t ? (f.pop(), f.push(s.kLineDataObj.dataArray[s.kLineDataObj.dataArray.length - 1])) : f.push(s.kLineDataObj.dataArray[s.kLineDataObj.dataArray.length - 1]);
                        break
                    }
                }
                var u = s.draw.drawData.length,
                a = f.slice(s.draw.goNextNeedNum),
                l = f.slice( - u),
                p = s.refreshConfigs(l, 0),
                d = s.getIndicatorArr(s.historyData.data, p);
                n(null, l, null, d),
                s.saveEventStauts(i),
                s.refreshEventStatus()
            })
        },
        a.prototype.refreshHistoryData = function(e, t) {
            if (t === "pushForward") {
                var n = this.draw.preHandleInHistory,
                i = this.historyData.lastDate,
                s = this.historyData.historyTotal,
                o = this.historyData.historyStart,
                u = this.historyData.seemLastDate,
                a = this.historyData.data.slice(n + 1),
                f = r.mergeLargeArr(e.data.slice(0), a);
                this.historyData = {
                    data: f,
                    startDate: f[0].t,
                    endDate: f[f.length - 1].t,
                    lastDate: i,
                    historyStart: o,
                    historyTotal: s,
                    seemLastDate: u,
                    num: f.length
                }
            } else if (t === "pushBackward") {
                var i = this.historyData.seemLastDate,
                s = this.historyData.historyTotal,
                o = this.historyData.historyStart,
                u = this.historyData.seemLastDate,
                f = r.mergeLargeArr(this.historyData.data.slice(0), e.data);
                this.historyData = {
                    data: f,
                    startDate: f[0].t,
                    endDate: f[f.length - 1].t,
                    lastDate: i,
                    historyStart: o,
                    historyTotal: s,
                    seemLastDate: u,
                    num: f.length
                }
            } else $.extend(this.historyData, e)
        },
        a.prototype.refreshAfterDraw = function(e) {
            $.extend(this.draw, e)
        },
        a.prototype.getNextData22 = function() {
            if (!this.eventStatus.goNext) return ! 1;
            var e = this.saveEventStauts(),
            t = this.draw.drawData.slice(0),
            r = this.draw.drawData.slice(0),
            i = this.draw.drawData.length,
            s = this.getMoveDataNum(r),
            o = this.draw.goNextNeedNum + s,
            u = this.getFromHistory(this.draw.startDate, o, null, e);
            if (u) {
                var a = u.slice(this.draw.goNextNeedNum),
                f = u.slice( - i),
                l = this.refreshConfigs(f, 0),
                c = this.getIndicatorArr(this.historyData.data, l);
                this.configs.animateEnable ? h(t, s, this.moveSpeed, a) : n(null, f, null, c),
                this.saveEventStauts(e),
                this.refreshEventStatus()
            }
        },
        a.prototype.getNextData = function() {
            f(this.getNextData22, this)
        },
        a.prototype.getMoveDataNum = function(e) {
            return e.length - this.draw.preHandleInTrueDrawData
        },
        a.prototype.saveEventStauts = function(e) {
            e = e || {
                goPre: ! 1,
                zoomOut: ! 1,
                goNext: ! 1,
                zoomIn: ! 1
            };
            var t = {};
            return $.each(this.eventStatus, function(e, n) {
                t[e] = n
            }),
            this.eventStatus = e,
            u(e, this.configs.eventBtn),
            t
        },
        a.prototype.getPreData = function() {
            f(this.getPreData22, this)
        },
        a.prototype.getPreData22 = function() {
            if (!this.eventStatus.goPre) return ! 1;
            var e = this.saveEventStauts(),
            t = this.draw.drawData.slice(0),
            i = this.getMoveDataNum(t),
            s = this,
            o = this.getMaxMa(),
            u = this.draw.startDate,
            a = [],
            f = this.tryGetFromHistory(),
            l,
            h,
            p = ! 1;
            if (f) {
                a = this.getIndicatorArrFromHistory();
                for (var d = f.length - 1; d >= 0; d--) if (f[d].t == u) {
                    l = d;
                    break
                }
                p = f.slice(0, l),
                this.saveEventStauts(e),
                this.refreshConfigs(f, 0),
                this.refreshEventStatus()
            }
            s.configs.animateEnable ? (c(t.slice(0), i, this.moveSpeed, p), h = 20 * (this.moveSpeed * 2 - 1)) : f && (n(null, f, null, a), h = 20);
            if (!f) {
                var v = i,
                m;
                setTimeout(function() {
                    s.kLineDataObj.getDataByYearsAndNum([s.draw.goPreNeedNum, s.draw.preHandle], function(i) {
                        for (var o = i.length - 1; o >= 0; o--) if (i[o].t == u) {
                            m = o;
                            break
                        }
                        a = s.getIndicatorArr(i, m - (v - 1));
                        if (m - (v - 1) < 0) var f = i.slice(0, m),
                        l = t.slice(0, t.length - v + 1 - (m - (v - 1)));
                        else var c = m - (v - 1),
                        f = i.slice(c, m),
                        l = t.slice(0, t.length - v + 1);
                        var h = r.mergeLargeArr(f, l);
                        n(null, h, null, a),
                        s.saveEventStauts(e),
                        s.refreshHistoryData({
                            data: i
                        },
                        "pushForward"),
                        s.refreshConfigs(h, 0),
                        s.refreshEventStatus()
                    })
                },
                h)
            }
        },
        a.prototype.getZoomIn = function() {
            f(this.getZoomIn22, this)
        },
        a.prototype.getZoomIn22 = function() {
            if (!this.eventStatus.zoomIn) return ! 1;
            var e = this.saveEventStauts(),
            t = this.draw.drawData.slice(0),
            r = Math.round(t.length * this.configs.zoom),
            i = t.slice( - (t.length - r)),
            s = this.refreshConfigs(i, 0),
            o = this.getIndicatorArr(this.historyData.data, s);
            if (this.configs.animateEnable) {
                var u = this.draw.drawData.slice(0);
                l(u, u.length, i.length, ! 1, this.zoomSpeed)
            } else n(null, i, null, o);
            this.saveEventStauts(e),
            this.refreshEventStatus()
        },
        a.prototype.getZoomOut = function() {
            f(this.getZoomOut22, this)
        },
        a.prototype.getZoomOut22 = function() {
            if (!this.eventStatus.zoomOut) return ! 1;
            var e = this.saveEventStauts(),
            t = this.configs.zoom,
            i = this.draw.drawData.slice(0),
            s = this.draw.drawData.slice(0),
            o = this,
            u = Math.round(s.length * t),
            a,
            f,
            c,
            h = this.getFromHistory(this.draw.startDate, u, "getBeforeDate");
            if (h) a = r.mergeLargeArr(h, s),
            f = this.refreshConfigs(a, 0),
            c = this.getIndicatorArr(this.historyData.data, f),
            this.configs.animateEnable ? l(a, i.length, a.length, ! 0, this.zoomSpeed) : n(null, a, null, c),
            this.saveEventStauts(e),
            this.refreshEventStatus();
            else {
                var p = this.draw.drawData.length,
                d = u + p,
                v = d + this.getMaxMa(),
                m = this.draw.drawData[p - 1];
                this.kLineDataObj.getDataByYearsAndNum([v, this.draw.drawData[p - 2].t], function(t) {
                    t.push(m),
                    a = t.slice( - d);
                    var r = t.length - (p - o.draw.preHandleInTrueDrawData);
                    o.refreshHistoryData({
                        data: t.slice(0, r + 1)
                    },
                    "pushForward"),
                    f = o.refreshConfigs(a, 0),
                    c = o.getIndicatorArr(o.historyData.data, f),
                    o.configs.animateEnable ? l(a, i.length, a.length, ! 0, o.zoomSpeed) : n(null, a, null, c),
                    o.saveEventStauts(e),
                    o.refreshEventStatus()
                })
            }
        },
        a.prototype.getAlreadyDrawArray = function() {
            return this.draw.drawData
        };
        var l = function(e, t, r, i, s) {
            function f() {
                if (i) {
                    if (t >= r) return;
                    t < s * a + u ? t += a: t >= s * a + u && t++
                } else {
                    if (t <= r) return;
                    t > t - s * a ? t -= a: t <= t - s * a && t--
                }
                var o = e.slice( - t);
                n(null, o, null),
                requestAnimationFrame(f)
            }
            var o = r - t,
            u = t,
            a = Math.floor(Math.abs(r - t) / s);
            f()
        },
        c = function(e, t, i, s) {
            var o = 0,
            u = [];
            t = s ? s.length + 1: t;
            var a = function(h) {
                var p = [];
                if (f >= t - 1) return ! 1;
                f < c * i ? (f += c, o = c) : f >= c * i && (f++, o = 1),
                e.splice( - o, o);
                if (s) {
                    var d = s.splice( - o, o);
                    u = r.mergeLargeArr(d, e)
                } else {
                    for (var v = 0; v < o; v++) p[v] = {
                        t: 0
                    };
                    u = r.mergeLargeArr(p, e)
                }
                n(null, u, null),
                l = requestAnimationFrame(a)
            },
            f = 0,
            l,
            c = Math.floor(t / i);
            a()
        },
        h = function(e, t, i, s) {
            var o = 0,
            u, a = s.length,
            f = s.slice(0),
            l = Math.floor(a / i);
            a = l ? a: a + 1;
            var c = l,
            h = [],
            p = function() {
                if (o >= a) return ! 1;
                e.splice(0, c),
                h = r.mergeLargeArr(e, f.splice(0, c)),
                n(null, h, null),
                o < l * i ? (o += l, c = l) : o >= l * i && (o++, c = 1),
                u = requestAnimationFrame(p)
            };
            p()
        };
        return a
    }),
    n("TimeShareProvider", ["arrayToolkit", "EventTarget", "hexinStock/util", "Xhr", "UrlConfig"], function(e, t, n, r, i) {
        var s = function(e, t, n) {
            if (n === "max") {
                if (e != null && e > t) return e
            } else if (n === "min" && e != null && e < t) return e;
            return t
        },
        o = function(e, t) {
            var n = "quotebridge",
            r = e.split(t),
            i = [];
            return r[1].replace(/([^\/.,]+)/g, function() {
                i.push(arguments[0])
            }),
            i.pop(),
            n + "_" + i.join("_")
        },
        u = function(e, t) {
            return arguments.length === 1 ? {
                t: e[0],
                nowp: parseFloat(e[1]),
                np: parseFloat(e[2]),
                av: parseFloat(e[3]),
                n: parseFloat(e[4])
            }: {
                t: e[0],
                nowp: t.nowp,
                np: t.np,
                av: t.av,
                n: t.n
            }
        },
        a = function(e) {
            t.call(this);
            var n = {
                url: i.urlHost,
                urlVersion: "v2/time/",
                fiveDaysEnable: ! 1,
                moneyFlowEnable: ! 1,
                stockType: "1000",
                code: ["33_300033"],
                isKeepingGet: ! 1,
                intervalTime: 6e4,
                drawWidth: 0
            };
            this.configs = $.extend({},
            n, e),
            this.dataLastTime = "",
            this.dataLastTimeQuery = "",
            this.keepingMoneyFlowHandle = "",
            this.dataArray = [],
            this.interval = ""
        };
        return n.inheritPrototype(a, t),
        a.prototype.getFromWeb = function(e, t, n) {
            var i = this.configs.code[0],
            s = t ? "1001": "1000",
            u = this.configs.url + this.configs.urlVersion + this.configs.code.join(","),
            a = t ? u + "/" + t + ".js": u + "/last.js",
            f = o(a, this.configs.url);
            r.jqXhrWithoutCallback(a, e, f)
        },
        a.prototype.getFiveDaysFromWeb = function(e) {
            var t = this.configs.code[0],
            n = this.configs.url + this.configs.urlVersion + t + "/lastFive.js",
            i = o(n, this.configs.url);
            r.jqXhrWithoutCallback(n, e, i)
        },
        a.prototype.setDataLastTime = function(e) {
            return e[e.length - 1].t
        },
        a.prototype.setSuspensionDisplay = function(e) {
            var t = [];
            for (var n = 0; n < e.length; n++) e[n].isSuspension && t.push(e[n].code);
            this.trigger("getSuspensions", t)
        },
        a.prototype.getDataLastTimeQuery = function(e) {
            var t = [],
            n = this.configs.code;
            for (var r = 0; r < n.length; r++) e[r].code == n[r] && t.push(e[r].dataLastTime);
            return t.join(",")
        },
        a.prototype.arrageEachStock = function(e, t) {
            var n = this.arrageDataByFirstGet(e);
            return n.dataLastTime = this.setDataLastTime(n.dataArray),
            n.code = t,
            n
        },
        a.prototype.arrageEachDates = function(e, t) {
            return this.getDataArrayFromFiveDays(e.split(";"), t)
        },
        a.prototype.getData = function() {
            var e = this,
            t = "";
            this.dataLastTime === "" && this.getFromWeb(function(t) {
                e.dataArray = $.map(t, function(t, n) {
                    return e.arrageEachStock(t, n)
                }),
                e.trigger("getTodayData", e.dataArray),
                e.trigger("firstGetTodayData", e.dataArray),
                e.needStopOnClosingTime(e.dataArray),
                e.dataLastTimeQuery = e.getDataLastTimeQuery(e.dataArray),
                e.setSuspensionDisplay(e.dataArray),
                e.configs.isKeepingGet && e.keepingGetData(e.dataArray)
            })
        },
        a.prototype.arrageMoneyFlow = function(e) {
            var t = [],
            n = {},
            r = {},
            i = e.data.split(";");
            r.isOpen = e.open,
            r.dataLastTime = i[i.length - 1].split(",")[0];
            var s = e.rt.split(","),
            o = s[0].split("-"),
            u = s[1].split("-");
            r.AMOpenTime = o[0],
            r.AMCloseTime = o[1],
            r.PMOpenTime = u[0],
            r.PMCloseTime = u[1],
            r.dataArray = [];
            for (var a = 0; a < i.length; a++) t = i[a].split(","),
            n = {
                t: t[0],
                mfsb: parseFloat(t[1] ? t[1] : 0),
                mfss: parseFloat(t[2] ? t[2] : 0),
                mfbb: parseFloat(t[3] ? t[3] : 0),
                mfbs: parseFloat(t[4] ? t[4] : 0),
                mflb: parseFloat(t[5] ? t[5] : 0),
                mfls: parseFloat(t[6] ? t[6] : 0),
                mfmb: parseFloat(t[7] ? t[7] : 0),
                mfms: parseFloat(t[8] ? t[8] : 0)
            },
            r.dataArray.push(n);
            return r
        },
        a.prototype.getMoneyFlow = function(e) {
            this.configs.moneyFlowEnable = ! 0;
            var t = this,
            n = this.configs.code[0],
            s = [],
            o = i.urlHost + "v2/moneyflow/" + n + "/last.js",
            u = "quotebridge_v2_moneyflow_" + n + "_last";
            r.jqXhrWithoutCallback(o, function(e) {
                var r = e[n];
                s[0] = t.arrageMoneyFlow(r),
                t.needStopOnClosingTime(s, "moneyFlow")
            },
            u),
            this.on("firstGetTodayData", function() {
                s.length != 0 ? e(s[0]) : r.jqXhrWithoutCallback(o, function(r) {
                    var i = r[n];
                    s[0] = t.arrageMoneyFlow(i),
                    t.needStopOnClosingTime(s, "moneyFlow"),
                    e(s[0])
                },
                u)
            }),
            t.keepingMoneyFlowHandle = setTimeout(function() {
                r.jqXhrWithoutCallback.call(t, o, function(r) {
                    var i = r[n];
                    s[0] = t.arrageMoneyFlow(i),
                    t.needStopOnClosingTime(s, "moneyFlow"),
                    e(s[0])
                },
                u),
                t.keepingMoneyFlowHandle = setTimeout(arguments.callee, t.configs.intervalTime)
            },
            t.configs.intervalTime)
        },
        a.prototype.getFiveDaysTotal = function(e) {
            var t = this,
            n = [],
            r = [],
            i = 0,
            o = 1e7;
            this.configs.fiveDaysEnable && this.getFiveDaysFromWeb(function(u) {
                var a = [];
                $.map(u.data, function(e, t) {
                    var n = {};
                    n.key = t,
                    n.d = e,
                    a.push(n),
                    n = null
                }),
                a.sort(function(e, t) {
                    return e.key < t.key ? - 1: 1
                });
                var f, l = [];
                for (var c = 0; c < a.length; c++) n.push(a[c].key),
                f = t.arrageEachDates(a[c].d, a[c].key),
                l.push(f),
                o = s(f.minPrice, o, "min"),
                i = s(f.maxPrice, i, "max");
                var h = t.getPreClosePrice(u.pre, u.data[n[0]]);
                if (!u.isLastdayIncluded) {
                    var p = t.dataArray;
                    if (p.length === 0) t.on("firstGetTodayData", function(u) {
                        u = u[0];
                        var a = t.currentDataConvertoIn5days(u.dataArray);
                        l.push(a);
                        var f = u.todayDate;
                        n.length !== l.length && n.push(f),
                        o = s(a.minPrice, o, "min"),
                        i = s(a.maxPrice, i, "max"),
                        r = {
                            data: l,
                            preClosePrice: h,
                            date: n,
                            max: i,
                            min: o
                        },
                        e(r)
                    });
                    else {
                        p = p[0];
                        var d = t.currentDataConvertoIn5days(p.dataArray);
                        l.push(d);
                        var v = p.todayDate;
                        n.length !== l.length && n.push(v),
                        o = s(d.minPrice, o, "min"),
                        i = s(d.maxPrice, i, "max"),
                        r = {
                            data: l,
                            preClosePrice: h,
                            date: n,
                            max: i,
                            min: o
                        },
                        e(r)
                    }
                    t.on("getTodayDataByKeeping", function(u) {
                        var a = t.currentDataConvertoIn5days(u[0].dataArray),
                        f = u[0].todayDate;
                        l.pop(),
                        l.push(a),
                        o = s(a.minPrice, o, "min"),
                        i = s(a.maxPrice, i, "max"),
                        r = {
                            data: l,
                            preClosePrice: h,
                            date: n,
                            max: i,
                            min: o
                        },
                        e(r)
                    })
                } else r = {
                    data: l,
                    preClosePrice: h,
                    date: n,
                    max: i,
                    min: o
                },
                e(r)
            })
        },
        a.prototype.currentDataConvertoIn5days = function(e) {
            var t = e.length,
            n = this.interval,
            r = [],
            i = 0,
            o = 1e8,
            u = e[t - 1],
            a = t - 1;
            for (var f = 0; f < a; f++) n && n !== 1 && f % n === 0 && r.push(e[f]),
            e[f].nowp && (i = s(e[f].nowp, i, "max"), o = s(e[f].nowp, o, "min"));
            return n && r.push(u),
            {
                compressed: r,
                maxPrice: i,
                minPrice: o,
                total: e
            }
        },
        a.prototype.needStopOnClosingTime = function(t, n) {
            n = n || "fs";
            if (n === "fs") {
                for (var r = 0; r < t.length; r++) if (t[r].isOpen === 0 || t[r].dataLastTime == t[r].AMCloseTime || t[r].dataLastTime == t[r].PMCloseTime) this.configs.code = e.remove(this.configs.code, t[r].code)
            } else(t[0].isOpen === 0 || t[0].dataLastTime == t[0].AMCloseTime || t[0].dataLastTime == t[0].PMCloseTime) && this.stopGetData();
            if (this.configs["code"].length == 0) {
                this.stopOnlyFs();
                return
            }
        },
        a.prototype.keepingGetEachStock = function(e, t) {
            var n = this.arrageDataByKeepGet(e);
            n.code = t;
            for (var r = 0; r < this.dataArray.length; r++) if (this.dataArray[r].code == t) {
                this.dataArray[r].dataArray.pop.apply(this.dataArray[r].dataArray),
                this.dataArray[r].dataArray.push.apply(this.dataArray[r].dataArray, n.dataArray),
                this.dataArray[r].dataLastTime = this.setDataLastTime(this.dataArray[r].dataArray),
                this.dataArray[r].isSuspension = e.stop;
                break
            }
        },
        a.prototype.keepingGetData = function() {
            var e = this;
            this.keepingGetHandle = setTimeout(function() {
                e.getFromWeb(function(t) {
                    $.map(t, function(t, n) {
                        e.keepingGetEachStock(t, n)
                    }),
                    e.needStopOnClosingTime(e.dataArray),
                    e.dataLastTimeQuery = e.getDataLastTimeQuery(e.dataArray),
                    e.setSuspensionDisplay(e.dataArray),
                    e.trigger("getTodayData", e.dataArray),
                    e.trigger("getTodayDataByKeeping", e.dataArray)
                },
                e.dataLastTimeQuery),
                e.keepingGetHandle = setTimeout(arguments.callee, e.configs.intervalTime)
            },
            e.configs.intervalTime)
        },
        a.prototype.stopOnlyFs = function() {
            this.configs.isKeepingGet = ! 1,
            clearTimeout(this.keepingGetHandle)
        },
        a.prototype.stopGetData = function() {
            this.configs.isKeepingGet = ! 1,
            clearTimeout(this.keepingGetHandle),
            this.configs.moneyFlowEnable && clearTimeout(this.keepingMoneyFlowHandle)
        },
        a.prototype.setKeepingGetStatus = function(e) {
            this.isKeepingGet = e ? ! 0: ! 1
        },
        a.prototype.arrageDataByFirstGet = function(e) {
            var t = {},
            n = e.rt.split(","),
            r = n[0].split("-"),
            i = n[1].split("-");
            return t.isOpen = e.open,
            t.isSuspension = e.stop,
            t.AMOpenTime = r[0],
            t.AMCloseTime = r[1],
            t.PMOpenTime = i[0],
            t.PMCloseTime = i[1],
            t.preClosePrice = this.getPreClosePrice(e.pre, e.data),
            t.todayDate = e.date,
            t.stockName = e.name,
            t.dataArray = this._getDataArray(e.data.split(";")),
            t
        },
        a.prototype.getPreClosePrice = function(e, t) {
            if (e) return e;
            var n = t.split(";"),
            r = n[0].split(",");
            return r[1]
        },
        a.prototype.arrageDataByKeepGet = function(e) {
            var t = {};
            return t.dataArray = this._getDataArray(e.data.split(";")),
            t
        },
        a.prototype._getDataArray = function(e) {
            var t = [],
            n = e.length;
            for (var r = 0; r < n; r++) {
                var i = e[r].split(",");
                i[1] && (t[r] = u(i))
            }
            return t
        },
        a.prototype.getDataArrayFromFiveDays = function(e, t) {
            var n = [],
            r = e.length,
            i = Math.floor(this.configs.drawWidth / 5),
            o = 1,
            a = [],
            f = 0,
            l = 1e7;
            i && r > 2 * i && (this.interval = Math.floor(r / i));
            for (var c = 0; c < r; c++) {
                var h = e[c].split(",");
                h[1] ? n[c] = u(h) : (n[c] = n[c - 1], n[c] = u(h, n[c - 1])),
                n[c].nowp && (f = s(n[c].nowp, f, "max"), l = s(n[c].nowp, l, "min")),
                this.interval !== 1 && c % this.interval === 0 && a.push(n[c])
            }
            return a.length !== 0 && (a.pop(), a.push(n[r - 1])),
            {
                compressed: a,
                total: n,
                maxPrice: f,
                minPrice: l
            }
        },
        a
    }),
    n("Axis", ["require"], function(e) {
        function n(e, t) {
            var n = e.ticks(t);
            for (var r = 0, i = []; r < n.length; r++) i.push(e(n[r]));
            return i
        }
        var t = function(e, t) {
            this.data = e,
            this.options = t || {
                tickDecimals: 2
            }
        };
        return t.prototype.measureText = function(e) {
            var t = e || this.tickGeneration("price"),
            n = document.createElement("div"),
            r = typeof e == "object" ? t.join("<br />") : e;
            n.innerHTML = r,
            n.style.position = "absolute",
            n.style.top = "-999px",
            document.body.appendChild(n);
            var i = n.clientWidth,
            s = n.clientHeight / (typeof e == "object" ? e.length: 1);
            return document.body.removeChild(n),
            {
                width: i,
                height: s
            }
        },
        t.prototype.tickGeneration = function(e) {
            var t = null;
            if (this.options.ticks) t = this.options.ticks;
            else {
                var n = this.options.ticksLength > 0 ? this.options.ticksLength: 6,
                r = this.getValue(e);
                t = scale.domain([r.min, r.max]).ticks(n, null, this.options.tickDecimals)
            }
            return t
        },
        t.prototype.getTicks = function(e) {
            return this.tickGeneration(e)
        },
        t.prototype.setTicks = function(e) {},
        t.prototype.getValue = function(t) {
            var n = e(this.data, function(e) {
                return typeof e == "object" ? e[t] : e
            });
            return {
                max: n[1],
                min: n[0]
            }
        },
        t.prototype.draw = n || function(e) {
            var t = e.context;
            if (e.leftAxisPos) {
                t.textAlign = "right",
                t.textBaseline = "middle";
                for (var n = 0; n < e.leftAxisPos.length; n++) t.fillText(this.tickGeneration("price")[n], e.leftAxisPos[n][0], e.leftAxisPos[n][1])
            }
            if (e.bottomAxisPos) {
                t.textAlign = "center",
                t.textBaseline = "top";
                for (var n = 0; n < e.bottomAxisPos.length; n++) t.fillText(this.tickGeneration("price")[n], e.bottomAxisPos[n][0], e.bottomAxisPos[n][1])
            }
        },
        t
    }),
    n("extent", [], function() {
        var e = function(e, t) {
            var n = - 1,
            r = e.length,
            i, s, o;
            if (arguments.length === 1) {
                while (++n < r && ! ((i = o = e[n]) != null && i <= i)) i = o = undefined;
                while (++n < r)(s = e[n]) != null && (i > s && (i = s), o < s && (o = s))
            } else {
                while (++n < r && ! ((i = o = t.call(e, e[n], n)) != null && i <= i)) i = undefined;
                while (++n < r)(s = t.call(e, e[n], n)) != null && (i > s && (i = s), o < s && (o = s))
            }
            return [i, o]
        };
        return e
    }),
    n("genTime", [], function() {
        var e = function(e, t, n, r) {
            function i(e) {
                return + e < 10 ? "0" + + e: "" + e
            }
            var s = e.substring(0, 2),
            o = e.substring(2, 4),
            u = t.substring(0, 2),
            a = t.substring(2, 4),
            f = n.substring(0, 2),
            l = n.substring(2, 4),
            c = r.substring(0, 2),
            h = r.substring(2, 4),
            p = c * 60 + + h - (f * 60 + + l) + (u * 60 + + a) - (s * 60 + + o) + 2,
            d = ["09:30", "10:30", "11:30/13:00", "14:00", "15:00"];
            return e == "0930" && t == "1200" && n == "1200" && r == "1600" ? d = ["09:30", "11:00", "13:00", "14:00", "16:00"] : e == "0930" && t == "1200" && n == "1300" && r == "1600" ? d = ["09:30", "10:30", "12:00/13:00", "14:00", "16:00"] : e == "0000" && t == "1200" && n == "1200" && r == "2359" && (d = ["06:00", "11:30", "17:00", "22:30", "04:00"], p = 1322),
            {
                length: p,
                xText: d
            }
        };
        return e
    }),
    t.config({
        shim: {
            jquery: {
                exports: "$"
            }
        },
        paths: {
            genTime: "hexinStock/genTime",
            arrayToolkit: "common/array/arrayToolkit",
            canvasLine: "hexinStock/canvas/line",
            scale: "hexinStock/scale",
            Axis: "hexinStock/axis",
            extent: "hexinStock/extent",
            Grid: "hexinStock/grid",
            genTime: "hexinStock/genTime",
            detectPR: "hexinStock/detectPR"
        }
    }),
    n("drawChart", ["canvasLine", "scale", "Axis", "extent", "Grid", "fixSupportCanvas", "genTime", "detectPR", "hexinStock/util"], function(e, t, n, r, i, s, o, u, a) {
        function f(n, a, f) {
            function I(e) {
                N = e
            }
            function q() {
                var t = e().x(function(e, t) {
                    return D(t)
                }).y(function(e) {
                    return P(e.nowp)
                }).canvasLineTemplate("linear").strokeStyle("#1FA3C9").ctx(m),
                n = e().x(function(e, t) {
                    return D(t)
                }).y(function(e) {
                    return P(e.nowp)
                }).canvasLineTemplate("linear-closed").y0(A + b.top + 20 - .5).strokeStyle("#fff").ctx(m),
                r = e().x(function(e, t) {
                    return D(t)
                }).y(function(e) {
                    return P(e.av)
                }).canvasLineTemplate("linear").strokeStyle("#EE7F09").ctx(m),
                i = e().x(function(e, t) {
                    return D(t)
                }).y(function(e) {
                    return H(e.n)
                }).canvasLineTemplate("linear-bar").strokeStyle("#1FA3C9").bottom(H.range()[0]).ctx(m);
                n(N),
                t(N),
                r(N),
                i(N),
                O = t.linePos()
            }
            function R(e) {
                return Math.abs(e) > 1e8 ? (e / 1e8).toFixed(2) + "亿": Math.abs(e) > 1e4 ? (e / 1e4).toFixed(2) + "万": e
            }
            window.global = window.global || {},
            global.canvasWidth = global.canvasWidth || document.getElementById("tcanvas").width,
            global.canvasHeight = global.canvasHeight || document.getElementById("tcanvas").height;
            var l = a[0].preClosePrice,
            c = n[0],
            h = global.canvasWidth,
            p = global.canvasHeight;
            var stockcode = a[0].code;
            u("tcanvas");
            var d;
            $(c).css({
                position: "absolute",
                left: 0,
                top: 0
            }),
            $("#canvasPanel").unbind(),
            $(c).siblings(":not(.unRemovePanel, #fTip, .opacity-stock-name)").remove();
            var v = n.data("fsconfig") || {};
            v.drawStartFn && v.drawStartFn();
            var m = c.getContext("2d");
            m.clearRect(0, 0, h, p);
            var g = document.createElement("canvas");
            g.id = "gridBase",
            g.width = h,
            g.height = p,
            g.style.position = "absolute",
            g.style.left = 0,
            g.style.top = 0,
            $("#canvasPanel").prepend($(g)),
            u("gridBase");
            if (!g.getContext) {
                if (!window.G_vmlCanvasManager) throw new Error("Canvas is not available. If you're using IE with a fall-back such as Excanvas, then there's either a mistake in your conditional include, or the page has no DOCTYPE and is rendering in Quirks Mode.");
                g = window.G_vmlCanvasManager.initElement(g)
            }
            var y = {
                margin: {
                    top: 20,
                    right: 54,
                    bottom: 30,
                    left: 60
                },
                body: {
                    height: "0.66"
                },
                vol: {
                    height: "0.24"
                },
                separation: 20,
                yAxis: {
                    length: 7
                },
                xAxis: {
                    length: (d = o(a[0].AMOpenTime, a[0].AMCloseTime, a[0].PMOpenTime, a[0].PMCloseTime)).length
                }
            },
            b = y.margin,
            w = y.separation,
            E = "#D45947",
            S = "#66A588",
            x = h - b.left - b.right,
            T = p - b.top - b.bottom,
            N = a[0].dataArray,
            C = {},
            k = y.yAxis.length,
            L = y.vol.height * T,
            A = v.showzjlx ? T - L * 2: T - L,
            O,
            M = r(N, function(e) {
                return e.nowp
            }),
            _ = r(N, function(e) {
                return e.n
            });
            (a[0].isSuspension || _[1] == 0) && M[0] == M[1] && (M[0] *= 1.01, M[1] /= 1.01, _[1] = 1);
            var D = t().domain([0, y.xAxis.length]).range([b.left + 2.5, h - b.right + .5]),
            P = t().domain(M, l).range([b.top + A + .5, b.top]),
            H = t().range([b.top + A + L, b.top + A + 2 * w]).domain([_[0], _[1]]),
            B = _[1] > 1e4 ? "万手": "手",
            j = P.ticks(k);
            this.draw = q,
            this.setData = I,
            this.drawzjlx = function(n) {
                var s = n.dataArray,
                o = [],
                u = [],
                a = [],
                f = [],
                l = [],
                h = g.getContext("2d");
                for (var p = 0; p < s.length; p++) p != "t" && (o.push(s[p].mfsb - s[p].mfss + s[p].mfbb - s[p].mfbs), u.push(s[p].mfbb - s[p].mfbs), a.push(s[p].mflb - s[p].mfls), f.push(s[p].mfmb - s[p].mfms));
                l.push(r(o, function(e) {
                    return Math.abs(e)
                })[1]),
                l.push(r(u, function(e) {
                    return Math.abs(e)
                })[1]),
                l.push(r(a, function(e) {
                    return Math.abs(e)
                })[1]),
                l.push(r(f, function(e) {
                    return Math.abs(e)
                })[1]),
                C.mfs = o,
                C.mfb = u,
                C.mfm = f,
                C.mfl = a;
                var d = Math.max.apply(null, l),
                v = t().range([b.top + A + L * 2, b.top + A + L + w]).domain([ - d, d], "0"),
                y = v.ticks(5, 2);
                for (var p = 0, E = []; p < y.length; p++) E.push(v(y[p]));
                var S = new i(h, {
                    grid: {
                        panel: c,
                        x: gridInfo.leftAxisPos[0][0],
                        y: gridInfo.leftAxisPos[0][1] + L,
                        width: x,
                        height: L,
                        color: "#555",
                        xsize: 4,
                        pos: E,
                        scale: [D, v],
                        tickDecimals: 0
                    }
                });
                S.drawAxis({
                    direction: "y",
                    position: "right",
                    formatter: function(e) {
                        return Math.abs(e) > 1e8 ? (e / 1e8).toFixed(0) + "亿": Math.abs(e) > 1e4 ? (e / 1e4).toFixed(0) + "万": e
                    }
                });
                var T = e().x(function(e, t) {
                    return D(t)
                }).y(function(e) {
                    return v(e)
                }).canvasLineTemplate("linear").strokeStyle("#C00C00").ctx(m);
                //m.clearRect(gridInfo.leftAxisPos[0][0], gridInfo.leftAxisPos[0][1] + L, x, L),
                T.strokeStyle("#C00C00"),
                T(o),
                T.strokeStyle("#FB8E04"),
                T(u),
                T.strokeStyle("#719F1F"),
                T(f)
            };
            var F = function() {
                var e = g.getContext("2d");
                e.strokeStyle = "#CBCBCB",
                e.strokeRect(.5, .5, h - 1, p - 1);
                for (var t = 0, n = []; t < j.length; t++) n.push(P(j[t]));
                var r = new i(e, {
                    grid: {
                        panel: c,
                        x: b.left,
                        y: 0,
                        width: x,
                        height: T + b.top,
                        color: "#555",
                        ysize: k,
                        xsize: 4,
                        pos: n,
                        topBorder: ! 0,
                        scale: [D, P],
                        showTextLine: ! 0
                    }
                });
                gridInfo = r.draw(),
                r.drawAxis({
                    direction: "y",
                    position: "right",
                    formatter: function(e) {
                        e = + e;
                        var t = "#929292";
                        return e > l ? t = E: e < l && (t = S),
                        '<span style="color:' + t + '">' + Math.abs((e - l) / l * 100).toFixed(2) + "%" + "</span>"
                    }
                }),
                r.drawAxis({
                    direction: "y",
                    position: "left"
                }),
                r.drawAxis({
                    direction: "x",
                    position: "bottom",
                    autoIndent: ! 0,
                    xText: d.xText
                });
                var s = H.ticks(4, 2);
                for (var t = 0, o = []; t < s.length; t++) o.push(H(s[t]));
                var u = new i(e, {
                    grid: {
                        panel: c,
                        x: gridInfo.leftAxisPos[0][0],
                        y: gridInfo.leftAxisPos[0][1] + w,
                        width: x,
                        height: L - w,
                        color: "#555",
                        xsize: 4,
                        pos: o,
                        scale: [D, H],
                        tickDecimals: 0
                    }
                });
                // 增加新三板判断
                var typeFromSanban = false;
                if (stockcode.split('_')[0] === 'sb') {
                    typeFromSanban = true;
                }
                u.drawAxis({
                    direction: "y",
                    position: "right",
                    formatter: function(e) {
                        if (typeFromSanban) {
                            return + e > 1e6 ? (e / 1e6).toFixed(0) + "万": e/100
                        }else{
                            return + e > 1e4 ? (e / 1e4).toFixed(0) + "万": e
                        }
                    }
                })
            };
            F(),
            $("#canvasPanel").mousemove(function(e) {
                var t = $("#canvasPanel"),
                n = t.width(),
                r = t.height(); ! $("#canvasMask").length > 0 && ($('<canvas id="canvasMask" />').css({
                    position: "absolute",
                    top: 0,
                    left: 0,
                    "z-index": 9
                }).attr({
                    width: n,
                    height: r
                }).appendTo(t), u("canvasMask"));
                var i = (D.range()[1] - D.range()[0]) / (D.domain()[1] - D.domain()[0]),
                o = (e.offsetX - b.left) / i >> 0,
                a = $("#canvasMask").get(0);
                s(a);
                var f = 40;
                $("#fTipTime").length > 0 || $('<div id="fTipTime" /><div id="fTipnowp" />').css({
                    position: "absolute",
                    background: "#555",
                    color: "#fff",
                    height: "18px",
                    "line-height": "16px",
                    width: f,
                    "text-align": "center",
                    "float": "left"
                }).appendTo(t);
                var c = a.getContext("2d");
                c.clearRect(0, 0, n, r),
                $("#fTipTime, #fTipnowp").html("").hide();
                if (e.offsetX <= b.left || e.offsetX > n - b.right || e.offsetY < b.top || e.offsetY > r - b.bottom) return;
                $("#fTip").length > 0 || $('<div id="fTip" />').css({
                    position: "absolute",
                    left: b.left + 5,
                    top: 2
                }).appendTo(t),
                ! ($("#zjlxTip").length > 0) && v.showzjlx && $('<div id="zjlxTip" />').css({
                    position: "absolute",
                    left: b.left + 5,
                    top: b.top + A + L
                }).appendTo(t),
                o >= N.length && (o = N.length - 1);
                if (N[o]) {
                    v.showzjlx && C && $("#zjlxTip").html( "大单净额:" +R(C.mfs[o])+ " 中单净额:" + R(C.mfm[o]) + " 小单净额:" +  R(C.mfl[o]) ),
                    c.beginPath(),
                    c.strokeStyle = "#A0A0A0",
                    c.dashedLine(b.left + .5, O[o][1], n - b.right + .5, O[o][1], [2, 3]),
                    c.dashedLine(O[o][0], b.top, O[o][0], r - b.bottom, [2, 3]),
                    c.stroke();
                    var h = "<%t%> 价:<%nowp%> 均:<%av%> 涨跌:<%zhang%> 涨幅:<%zhangfu%> 量:<%n%> 额:<%np%>",
                    p = O[o][0] - f / 2;
                    p <= b.left ? p = b.left: p >= n - b.right - f && (p = n - b.right - f);
                    var d = N[o].t;
                    $("#fTipTime").html(d.substring(0, 2) + ":" + d.substring(2, 4)).css({
                        left: p,
                        top: r - b.bottom
                    }).show(),
                    $("#fTipnowp").css({
                        left: n - b.right,
                        top: P(N[o].nowp),
                        width: "auto",
                        background: N[o].nowp >= l ? E: S,
                        "margin-top": "-9px",
                        padding: "0 5px"
                    }).html(N[o].nowp.toFixed(2)).show();
                    // 增加新三板判断
                    var typeFromSanban = false;
                    if (stockcode.split('_')[0] === 'sb') {
                        typeFromSanban = true;
                    }
                    var m = function(e) {
                        return e.replace(/<%([^%>]+)?%>/g, function(e, t) {
                            var n = N[o][t],
                            r = N[o].nowp;
                            if (t == "t") return n.substring(0, 2) + ":" + n.substring(2, 4);
                            t == "zhang" && (n = (r - l).toFixed(2)),
                            t == "zhangfu" && (n = ((r - l) / l * 100).toFixed(2) + "%");
                            if (t == "nowp" || t == "av") n = n.toFixed(2);
                            t == "np" && (n > 1e4 && (n = (n / 1e4).toFixed(2) + "万"), n > 1e8 && (n = (n / 1e8).toFixed(2) + "亿"));
                            if(t == "n"){
                                if (typeFromSanban) {
                                    B == "万手" && (n /= 1e6), n = (n/100).toFixed(2) + B
                                }else{
                                    B == "万手" && (n /= 1e4), n = n.toFixed(2) + B
                                }
                            }
                            var i;
                            return r >= l ? i = E: r < l && (i = S),
                            '<span style="color:' + i + '">' + n + "</span>"
                        })
                    };
                    $("#fTip").html(m(h))
                }
            })
        }
        return f
    }),
    t.config({
        shim: {
            jquery: {
                exports: "$"
            }
        },
        paths: {
            jquery: "../lib/jquery-1.9.1",
            genTime: "hexinStock/genTime",
            arrayToolkit: "common/array/arrayToolkit",
            mathToolkit: "common/math/mathToolkit",
            EventTarget: "common/event/eventTarget",
            Xhr: "common/xhr",
            canvasLine: "hexinStock/canvas/line",
            KPainter: "hexinStock/DataProvider/KPainter",
            KLineProvider: "hexinStock/DataProvider/KLineProvider",
            TimeShareProvider: "hexinStock/DataProvider/TimeShareProvider",
            UrlConfig: "hexinStock/DataProvider/UrlConfig",
            scale: "hexinStock/scale",
            Axis: "hexinStock/axis",
            extent: "hexinStock/extent",
            Grid: "hexinStock/grid",
            drawChart: "require-config-pc",
            drawKLine: "drawBarDemo-pc",
            kgrid: "hexinStock/KGrid",
            dashedLine: "hexinStock/dashedLine",
            genTime: "hexinStock/genTime",
            arrayToolkit: "common/array/arrayToolkit",
            barBuilder: "hexinStock/canvas/bar",
            d3: "../lib/d3.v3.min",
            techCal: "hexinStock/techCal",
            detectPR: "hexinStock/detectPR",
            techConfig: "hexinStock/techConfig"
        }
    }),
    t(["KPainter", "KLineProvider", "TimeShareProvider", "drawChart", "drawKLine"], function(e, t, n, r, i) {
        function f() {
            $("#canvasPanel").hide(),
            $(".canvas-btn-box").hide(),
            $(".canvas_target_fun").hide(),
            $(".canvas-panel-mask").show(),
            $(".canvas-panel-mask").append('<div class="canvas-loading-icon"></div>')
        }
        function l() {
            $(".canvas-panel-mask").hide(),
            $(".canvas-loading-icon").remove(),
            $("#canvasPanel").show()
        }
        var s = null,
        o = null,
        u = "",
        a = "",
        c = function(e, t) {
            t ? hx_stockc.hx_canvas_klineConfigs = {
                callFun: "showFs",
                historyDataAndNum: t.klConfigs.historyDataAndNum ? t.klConfigs.historyDataAndNum: null
            }: hx_stockc.hx_canvas_klineConfigs = {
                callFun: "showFs",
                historyDataAndNum: hx_stockc.hx_canvas_klineConfigs.historyDataAndNum
            };
            var i = $("#tcanvas"),
            c = i.data("fsconfig");
            f(),
            e && (a = e),
            u = "",
            $(".hx-powered-logo").css({
                top: 254,
                left: 53
            }),
            $("#suspensionId").hasClass("suspension-cc") ? $(".opacity-stock-name").css({
                top: 98,
                left: 104
            }) : $(".opacity-stock-name").css({
                top: 120,
                left: 104
            }),
            $(".fuquan-status").hide("400"),
            o && (o.kLineDataObj.stopGetData(), o = null),
            s && (s.stopGetData(), s = null),
            s = new n({
                isKeepingGet: ! 0,
                intervalTime: 6e4,
                code: [a]
            }),
            s.on("getTodayData", function(e) {
                l();
                if (!u) {
                    var t = e[0].code.split("_");
                    u = e[0].stockName + " " + t[1],
                    $("#canvasStockInfo").text(u),
                    $("#hx_canvas_stock_name").text(e[0].stockName)
                }
                window.todayData = e;
                if (!c.showzjlx) {
                    chart = new r(i, todayData, c);
                    chart.draw();
                }
            }),
            c && c.showzjlx && s.getMoneyFlow(function(e) {
                chart = new r(i, todayData, c);
                chart.draw();
                chart.drawzjlx(e);
            }),
            s.on("getSuspensions", function(e) {
                e.length !== 0 ? ($("#suspensionId").addClass("suspension-cc").show(), $(".opacity-stock-name").css("top", 98)) : $("#suspensionId").hasClass("suspension-cc") && ($("#suspensionId").removeClass("suspension-cc").hide(), $(".opacity-stock-name").css("top", 120))
            }),
            s.getData()
        },
        h = function(t, n) {
            hx_stockc.hx_canvas_klineConfigs = {
                callFun: "showKl",
                stockType: t.stockType,
                rehabilitationType: t.rehabilitationType,
                indicator: t.indicator ? t.indicator: null,
                historyDataAndNum: t.historyDataAndNum ? t.historyDataAndNum: null
            },
            f(),
            $(".canvas_target_fun").show(),
            $("#suspensionId").hasClass("suspension-cc") ? $(".opacity-stock-name").css({
                top: 76,
                left: 78
            }) : $(".opacity-stock-name").css({
                top: 98,
                left: 78
            }),
            $(".hx-powered-logo").css({
                top: 212,
                left: 0
            }),
            n && (a = n),
            u = "",
            s && (s.stopGetData(), s = null),
            o && (o.kLineDataObj.stopGetData(), o = null);
            var r = hx_stockc.hx_canvas_klineConfigs.stockType + hx_stockc.hx_canvas_klineConfigs.rehabilitationType;
            o = new e({
                animateEnable: ! 1,
                drawDataLength: hx_stockc.hx_canvas_klineConfigs.historyDataAndNum ? hx_stockc.hx_canvas_klineConfigs.historyDataAndNum[0] : 60,
                klineConfig: {
                    isKeepingGet: ! 0,
                    intervalTime: 6e4,
                    url: "http://d.10jqka.com.cn/",
                    code: [a],
                    stockType: r,
                    historyDataAndNum: hx_stockc.hx_canvas_klineConfigs.historyDataAndNum ? hx_stockc.hx_canvas_klineConfigs.historyDataAndNum: null
                },
                eventBtn: {
                    goNext: "nextEvent",
                    goPre: "preEvent",
                    zoomIn: "zoomInEvent",
                    zoomOut: "zoomOutEvent",
                    disabledClass: "disabled"
                },
                startDraw: function() {
                    l(),
                    $(".fuquan-status").show(),
                    $(".canvas-btn-box").show(),
                    hx_stockc.hx_canvas_klineConfigs.indicator && $(".canvas_target_fun").show()
                },
                getStockInfo: function(e) {
                    if (!u) {
                        var t = e.code.split("_");
                        u = e.stockName + " " + t[1],
                        $("#canvasStockInfo").text(u),
                        $("#hx_canvas_stock_name").text(e.stockName)
                    }
                }
            })
        },
        p = function(e) {
            e.siblings().removeClass("active"),
            e.hasClass("active") || e.addClass("active")
        },
        d = function(e) {
            var t = e.text(),
            n = e.attr("data-type"),
            r = e.parent();
            r.siblings("a")[0].firstChild.data = t,
            r.siblings("a").attr("data-fuquan", n),
            r.hide()
        },
        v = function(e) {
            e.addClass("active").siblings().removeClass("active")
        },
        m = function(e) {
            var t = $(".canvas_tab").find('[data-type="' + e.klConfigs.stockType + '"]').parent("li");
            p(t);
            var n = $(".canvas_target_fun").find('[data-type="' + e.klConfigs.indicator + '"]');
            if (n.length === 0) {
                hx_stockc.additionIndicator = e.klConfigs.indicator,
                $(".canvas_target_fun ul").prepend('<li> <a href="javascript:;" data-type="' + e.klConfigs.indicator + '" data-from="addition" >' + e.klConfigs.indicator + "</a> </li>");
                var r = $(".canvas_target_fun").find('[data-type="' + e.klConfigs.indicator + '"]').parent("li")
            } else {
                hx_stockc.additionIndicator && $(".canvas_target_fun ul").prepend('<li> <a href="javascript:;" data-type="' + hx_stockc.additionIndicator + '" data-from="addition" >' + hx_stockc.additionIndicator + "</a> </li>");
                var r = n.parent("li")
            }
            v(r);
            var i = $(".khover").find('[data-type="' + e.klConfigs.rehabilitationType + '"]');
            d(i)
        },
        g = function(e, t) {
            m(t);
            var n = $("#tcanvas");
            $(n).data("techItem", t.klConfigs.indicator),
            h(t.klConfigs, e)
        },
        y = function() {
            return $(".canvas_tab").find("[data-fuquan]").attr("data-fuquan")
        },
        b = function() {
            s && (s.stopGetData(), s = null),
            o && (o.kLineDataObj.stopGetData(), o = null),
            u = "",
            a = "",
            $(".canvas_target_fun").find('[data-from="addition"]').parent("li").remove() && $(".canvas_target_fun").find("li").eq(0).addClass("active"),
            $("#suspensionId").hasClass("suspension-cc") && ($("#suspensionId").removeClass("suspension-cc").hide(), $(".opacity-stock-name").css("top", 120)),
            $("#canvasStockInfo").text(""),
            $("#hx_canvas_stock_name").text("")
        },
        w = function(e, t) {
            var n = {
                callFun: "showFs"
            },
            r = $.extend({},
            n, e);
            hx_stockc[r.callFun](t, r)
        };
        window.hx_stockc = {
            showFs: c,
            additionIndicator: "",
            showKl: g,
            hx_canvas_klineConfigs: null,
            showCanvasStock: w,
            closeCanvasWindow: b
        },
        $(".canvas_tab").on("mouseenter", "[data-role=fuquan-status]", function(e) {
            $(this).children(".khover").show()
        }),
        $(".canvas_tab").on("mouseleave", "[data-role=fuquan-status]", function(e) {
            $(this).children(".khover").hide()
        }),
        $(".canvas_tab").on("click", ".draw-type", function(e) {
            var t = $(this).parent("li");
            p(t)
        }),
        $(".khover").on("click", "a", function(e) {
            e.preventDefault(),
            d($(this));
            var t = y(),
            n = $(".canvas_tab").find(".active").children("a"),
            r = $(n).attr("data-type"),
            i = $(".canvas_target_fun").find(".active").children("a").attr("data-type"),
            s = {
                rehabilitationType: t,
                stockType: r,
                indicator: i,
                historyDataAndNum: hx_stockc.hx_canvas_klineConfigs.historyDataAndNum
            };
            h(s, null)
        }),
        $(".klView").on("click", function() {
            var e = y(),
            t = $(this).attr("data-type"),
            n = $(".canvas_target_fun").find(".active").children("a").attr("data-type"),
            r = {
                rehabilitationType: e,
                stockType: t,
                indicator: n,
                historyDataAndNum: hx_stockc.hx_canvas_klineConfigs.historyDataAndNum
            };
            h(r, null)
        }),
        $("#changefsView").click(function() {
            c()
        }),
        $("#preEvent").click(function() {
            o.getPreData()
        }),
        $("#nextEvent").click(function() {
            o.getNextData()
        }),
        $("#zoomInEvent").click(function() {
            o.getZoomIn()
        }),
        $("#zoomOutEvent").click(function() {
            o.getZoomOut()
        }),
        $(".canvas_target_fun").on("click", "li", function() {
            var e = $("#tcanvas"),
            t = $.trim($(this).text());
            hx_stockc.hx_canvas_klineConfigs && (hx_stockc.hx_canvas_klineConfigs.indicator = t),
            $(this).addClass("active").siblings().removeClass("active"),
            e.data("techItem", t),
            e.data("tech")(t),
            hx_stockc.hx_canvas_klineConfigs.indicator = t,
            $("#canvasPanel").trigger("mousemove")
        })
    }),
    n("dataProvider", function() {})
})();