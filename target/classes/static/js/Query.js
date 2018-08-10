!
    function(a, b) {
        function c(a, b) {
            for (var c in b) a[c] = b[c];
            return a
        }
        function d(a) {
            this.callback && this.callback(a, !0)
        }
        function e(a) {
            this.callback && this.callback(a, !1)
        }
        function f(a) {
            this._options = c({
                mode: "msg",
                text: "缃戦〉鎻愮ず",
                useTap: !1
            }, a || {}), this._init()
        }
        var g, h, i, j, k, l, m = a.document,
            n = (m.body, !1);
        g = m.createElement("div"), g.className = "c-float-popWrap msgMode hide", g.innerHTML = ['<div class="c-float-modePop">', '<div class="warnMsg"></div>', '<div class="content"></div>', '<div class="doBtn">', '<button class="ok">纭畾</button>', '<button class="cancel">鍙栨秷</button>', "</div>", "</div>"].join(""), h = g.querySelector(".warnMsg"), i = g.querySelector(".content"), j = g.querySelector(".doBtn .ok"), k = g.querySelector(".doBtn .cancel"), c(f.prototype, {
            _init: function() {
                var b = this,
                    c = b._options,
                    f = c.mode,
                    l = c.text,
                    o = c.content,
                    p = c.callback,
                    q = c.background,
                    r = c.useTap ? "touchend" : "click",
                    s = g.className;
                s = s.replace(/(msg|alert|confirm)Mode/i, f + "Mode"), g.className = s, q && (g.firstChild.style.background = q), l && (h.innerHTML = l), o && (i.innerHTML = o), j.removeEventListener("touchend", d), j.removeEventListener("click", d), k.removeEventListener("touchend", d), k.removeEventListener("click", d), j.addEventListener(r, d, !1), k.addEventListener(r, e, !1), j.callback = k.callback = function() {
                    p.apply(b, arguments)
                }, n || (n = !0, m.body.appendChild(g), a.addEventListener("resize", function() {
                    setTimeout(function() {
                        b._pos()
                    }, 500)
                }, !1))
            },
            _pos: function() {
                var b, c, d, e, f, h, i, j, k = this;
                k.isHide() || (b = m.body.getBoundingClientRect(), c = -b.top, d = -b.left, e = a.innerWidth, f = a.innerHeight, h = g.getBoundingClientRect(), i = h.width, j = h.height, g.style.top = c + (f - j) / 2 + "px", g.style.left = d + (e - i) / 2 + "px")
            },
            isShow: function() {
                return g.className.indexOf("show") > -1
            },
            isHide: function() {
                return g.className.indexOf("hide") > -1
            },
            _cbShow: function() {
                var a = this,
                    b = a._options,
                    c = b.onShow;
                g.style.opacity = "1", g.className = g.className.replace(/\b(?:show|hide)/, "show"), c && c.call(a)
            },
            show: function() {
                var a = this;
                l && (clearTimeout(l), l = void 0), a.isShow() ? a._cbShow() : (g.style.opacity = "0", g.className = g.className.replace("hide", ""), a._pos(), setTimeout(function() {
                    a._cbShow()
                }, 300), setTimeout(function() {
                    g.style.webkitTransition = "opacity 0.4s linear 0", g.style.opacity = "1"
                }, 1))
            },
            _cbHide: function() {
                var a = this,
                    b = a._options,
                    c = b.onHide;
                g.style.opacity = "0", g.className = g.className.replace(/\s*show|hide/, "") + " hide", c && c.call(a)
            },
            hide: function() {
                var a = this;
                a.isHide() ? a._cbHide() : (g.style.opacity = "1", g.className = g.className.replace("show", ""), setTimeout(function() {
                    a._cbHide()
                }, 300), setTimeout(function() {
                    g.style.webkitTransition = "opacity 0.4s linear 0", g.style.opacity = "0"
                }, 1))
            },
            flash: function(a) {
                var b = this;
                opt = b._options, opt.onShow = function() {
                    l = setTimeout(function() {
                        l && b.hide()
                    }, a)
                }, b.show()
            }
        }), b.notification = new function() {
            this.simple = function(a, b, c) {
                2 == arguments.length && "number" == typeof arguments[1] && (c = arguments[1], b = void 0);
                var d = new f({
                    mode: "msg",
                    text: a,
                    background: b
                });
                return d.flash(c || 2e3), d
            }, this.msg = function(a, b) {
                return new f(c({
                    mode: "msg",
                    text: a
                }, b || {}))
            }, this.alert = function(a, b, d) {
                return new f(c({
                    mode: "alert",
                    text: a,
                    callback: b
                }, d || {}))
            }, this.confirm = function(a, b, d, e) {
                return new f(c({
                    mode: "confirm",
                    text: a,
                    content: b,
                    callback: d
                }, e || {}))
            }, this.pop = function(a) {
                return new f(a)
            }
        }
    }(window, window.lib || (window.lib = {}));
!
    function(a) {
        function b(a) {
            var b, c, d, e = "",
                f = 0;
            for (b = c = d = 0; f < a.length;) b = a.charCodeAt(f), 128 > b ? (e += String.fromCharCode(b), f++) : b > 191 && 224 > b ? (d = a.charCodeAt(f + 1), e += String.fromCharCode((31 & b) << 6 | 63 & d), f += 2) : (d = a.charCodeAt(f + 1), c = a.charCodeAt(f + 2), e += String.fromCharCode((15 & b) << 12 | (63 & d) << 6 | 63 & c), f += 3);
            return e
        }
        function c(a) {
            a = a.replace(/\r\n/g, "\n");
            for (var b = "", c = 0; c < a.length; c++) {
                var d = a.charCodeAt(c);
                128 > d ? b += String.fromCharCode(d) : d > 127 && 2048 > d ? (b += String.fromCharCode(192 | d >> 6), b += String.fromCharCode(128 | 63 & d)) : (b += String.fromCharCode(224 | d >> 12), b += String.fromCharCode(128 | 63 & d >> 6), b += String.fromCharCode(128 | 63 & d))
            }
            return b
        }
        var d = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        a.encode || (a.encode = {}), a.encode.base64_utf8 = {
            encode: function(a) {
                var b, e, f, g, h, i, j, k = "",
                    l = 0;
                for (a = c(a); l < a.length;) b = a.charCodeAt(l++), e = a.charCodeAt(l++), f = a.charCodeAt(l++), g = b >> 2, h = (3 & b) << 4 | e >> 4, i = (15 & e) << 2 | f >> 6, j = 63 & f, isNaN(e) ? i = j = 64 : isNaN(f) && (j = 64), k = k + d.charAt(g) + d.charAt(h) + d.charAt(i) + d.charAt(j);
                return k
            },
            decode: function(a) {
                var c, e, f, g, h, i, j, k = "",
                    l = 0;
                for (a = a.replace(/[^A-Za-z0-9\+\/\=]/g, ""); l < a.length;) g = d.indexOf(a.charAt(l++)), h = d.indexOf(a.charAt(l++)), i = d.indexOf(a.charAt(l++)), j = d.indexOf(a.charAt(l++)), c = g << 2 | h >> 4, e = (15 & h) << 4 | i >> 2, f = (3 & i) << 6 | j, k += String.fromCharCode(c), 64 !== i && (k += String.fromCharCode(e)), 64 !== j && (k += String.fromCharCode(f));
                return k = b(k)
            }
        }
    }(window.lib || (window.lib = {}));
!
    function(a, b, c) {
        function d(a) {
            var b = new RegExp("(?:^|;\\s*)" + a + "\\=([^;]+)(?:;\\s*|$)").exec(u.cookie);
            return b ? b[1] : c
        }
        function e(a) {
            return a.preventDefault(), !1
        }
        function f(b, c) {
            var d = this,
                f = a.dpr || 1,
                g = document.createElement("div"),
                h = document.documentElement.getBoundingClientRect(),
                i = Math.max(h.width, window.innerWidth) / f,
                j = Math.max(h.height, window.innerHeight) / f;
            g.style.cssText = ["-webkit-transform:scale(" + f + ") translateZ(0)", "-ms-transform:scale(" + f + ") translateZ(0)", "transform:scale(" + f + ") translateZ(0)", "-webkit-transform-origin:0 0", "-ms-transform-origin:0 0", "transform-origin:0 0", "width:" + i + "px", "height:" + j + "px", "z-index:999999", "position:absolute", "left:0", "top:0px", "background:#FFF", "display:none"].join(";");
            var k = document.createElement("div");
            k.style.cssText = ["width:100%", "height:52px", "background:#EEE", "line-height:52px", "text-align:left", "box-sizing:border-box", "padding-left:20px", "position:absolute", "left:0", "top:0", "font-size:16px", "font-weight:bold", "color:#333"].join(";"), k.innerText = b;
            var l = document.createElement("a");
            l.style.cssText = ["display:block", "position:absolute", "right:0", "top:0", "height:52px", "line-height:52px", "padding:0 20px", "color:#999"].join(";"), l.innerText = "鍏抽棴";
            var m = document.createElement("iframe");
            m.style.cssText = ["width:100%", "height:100%", "border:0", "overflow:hidden"].join(";"), k.appendChild(l), g.appendChild(k), g.appendChild(m), u.body.appendChild(g), m.src = c, l.addEventListener("click", function() {
                d.hide();
                var a = u.createEvent("HTMLEvents");
                a.initEvent("close", !1, !1), g.dispatchEvent(a)
            }, !1), this.addEventListener = function() {
                g.addEventListener.apply(g, arguments)
            }, this.removeEventListener = function() {
                g.removeEventListener.apply(g, arguments)
            }, this.show = function() {
                document.addEventListener("touchmove", e, !1), g.style.display = "block", window.scrollTo(0, 0)
            }, this.hide = function() {
                document.removeEventListener("touchmove", e), window.scrollTo(0, -h.top), u.body.removeChild(g)
            }
        }
        function g(a) {
            if (!a || "function" != typeof a || !b.mtop) {
                var d = this.getUserNick();
                return !!d
            }
            b.mtop.request({
                api: "mtop.user.getUserSimple",
                v: "1.0",
                data: {
                    isSec: 0
                },
                H5Request: !0
            }, function(d) {
                d.retType === b.mtop.RESPONSE_TYPE.SUCCESS ? a(!0, d) : d.retType === b.mtop.RESPONSE_TYPE.SESSION_EXPIRED ? a(!1, d) : a(c, d)
            })
        }
        function h(a) {
            var c;
            return b.promise && (c = b.promise.defer()), this.isLogin(function(b, d) {
                a && a(b, d), b === !0 ? c.resolve(d) : c.reject(d)
            }), c ? c.promise : void 0
        }
        function i(a) {
            if (!a || "function" != typeof a) {
                var b = "",
                    e = d("_w_tb_nick"),
                    f = d("_nk_") || d("snk");
                return e && e.length > 0 && "null" != e ? b = decodeURIComponent(e) : f && f.length > 0 && "null" != f && (b = unescape(unescape(f).replace(/\\u/g, "%u"))), b = b.replace(/\</g, "&lt;").replace(/\>/g, "&gt;")
            }
            this.isLogin(function(b, d) {
                b === !0 && d && d.data && d.data.nick ? a(d.data.nick) : b === !1 ? a("") : a(c)
            })
        }
        function j(a) {
            var c;
            return b.promise && (c = b.promise.defer()), this.getUserNick(function(b) {
                a && a(b), b ? c.resolve(b) : c.reject()
            }), c ? c.promise : void 0
        }
        function k(a, b) {
            var c = "//" + F + "." + G.subDomain + "." + D + "/" + G[(a || "login") + "Name"];
            if (b) {
                var d = [];
                for (var e in b) d.push(e + "=" + encodeURIComponent(b[e]));
                c += "?" + d.join("&")
            }
            return c
        }
        function l(a, b) {
            if (b) location.replace(a);
            else {
                var c = u.createElement("a"),
                    d = u.createEvent("HTMLEvents");
                c.style.display = "none", c.href = a, u.body.appendChild(c), d.initEvent("click", !1, !0), c.dispatchEvent(d)
            }
        }
        function m(b, c, d) {
            function e() {
                j.removeEventListener("close", e), a.removeEventListener("message", g), d("CANCEL")
            }
            function g(b) {
                j.removeEventListener("close", e), a.removeEventListener("message", g), j.hide();
                var c = b.data || {};
                c && "child" === c.type && c.content.indexOf("SUCCESS") > -1 ? d("SUCCESS") : d("FAILURE")
            }
            var h = location.protocol + "//h5." + G.subDomain + ".taobao.com/" + ("waptest" === G.subDomain ? "src" : "other") + "/" + b + "end.html?origin=" + encodeURIComponent(location.protocol + "//" + location.hostname),
                i = k(b, {
                    ttid: "h5@iframe",
                    tpl_redirect_url: h
                }),
                j = new f(c.title || "鎮ㄩ渶瑕佺櫥褰曟墠鑳界户缁闂�", i);
            j.addEventListener("close", e, !1), a.addEventListener("message", g, !1), j.show()
        }
        function n(b, c, d) {
            var e = k(b, {
                wvLoginCallback: "wvLoginCallback"
            });
            a.wvLoginCallback = function(b) {
                delete a.wvLoginCallback, b.indexOf(":SUCCESS") > -1 ? d("SUCCESS") : b.indexOf(":CANCEL") > -1 ? d("CANCEL") : d("FAILURE")
            }, l(e)
        }
        function o(a, b, c) {
            if ("function" == typeof b ? (c = b, b = null) : "string" == typeof b && (b = {
                        redirectUrl: b
                    }), b = b || {}, c && z) n(a, b, c);
            else if (c && !y && "login" === a) m(a, b, c);
            else {
                var d = k(a, {
                    tpl_redirect_url: b.redirectUrl || location.href
                });
                l(d, b.replace)
            }
        }
        function p(a, c, d) {
            var e;
            return b.promise && (e = b.promise.defer()), o(a, c, function(a) {
                d && d(a), "SUCCESS" === a ? e.resolve(a) : e.reject(a)
            }), e ? e.promise : void 0
        }
        function q(a) {
            o("login", a)
        }
        function r(a) {
            return p("login", a)
        }
        function s(a) {
            o("logout", a)
        }
        function t(a) {
            return p("logout", a)
        }
        var u = a.document,
            v = a.navigator.userAgent,
            w = location.hostname,
            x = (a.location.search, v.match(/WindVane[\/\s]([\d\.\_]+)/)),
            y = v.match(/AliApp\(([^\/]+)\/([\d\.\_]+)\)/i),
            z = !! (y && "TB" === y[1] && x && parseFloat(x[1]) > 5.2),
            A = ["taobao.net", "taobao.com"],
            B = new RegExp("([^.]*?)\\.?((?:" + A.join(")|(?:").replace(/\./g, "\\.") + "))", "i"),
            C = w.match(B) || [],
            D = function() {
                var a = C[2] || "taobao.com";
                return a.match(/\.?taobao\.net$/) ? "taobao.net" : "taobao.com"
            }(),
            E = function() {
                var a = D,
                    b = C[1] || "m";
                return "taobao.net" === a && (b = "waptest"), b
            }(),
            F = "login";
        b.login = b.login || {};
        var G = {
            loginName: "login.htm",
            logoutName: "logout.htm",
            subDomain: E
        };
        b.login.config, b.login.isLogin = g, b.login.isLoginAsync = h, b.login.getUserNick = i, b.login.getUserNickAsync = j, b.login.generateUrl = k, b.login.goLogin = q, b.login.goLoginAsync = r, b.login.goLogout = s, b.login.goLogoutAsync = t
    }(window, window.lib || (window.lib = {}));
!
    function(a, b) {
        function c(a) {
            Object.defineProperty(this, "val", {
                value: a.toString(),
                enumerable: !0
            }), this.gt = function(a) {
                return c.compare(this, a) > 0
            }, this.gte = function(a) {
                return c.compare(this, a) >= 0
            }, this.lt = function(a) {
                return c.compare(this, a) < 0
            }, this.lte = function(a) {
                return c.compare(this, a) <= 0
            }, this.eq = function(a) {
                return 0 === c.compare(this, a)
            }
        }
        b.env = b.env || {}, c.prototype.toString = function() {
            return this.val
        }, c.prototype.valueOf = function() {
            for (var a = this.val.split("."), b = [], c = 0; c < a.length; c++) {
                var d = parseInt(a[c], 10);
                isNaN(d) && (d = 0);
                var e = d.toString();
                e.length < 5 && (e = Array(6 - e.length).join("0") + e), b.push(e), 1 === b.length && b.push(".")
            }
            return parseFloat(b.join(""))
        }, c.compare = function(a, b) {
            a = a.toString().split("."), b = b.toString().split(".");
            for (var c = 0; c < a.length || c < b.length; c++) {
                var d = parseInt(a[c], 10),
                    e = parseInt(b[c], 10);
                if (window.isNaN(d) && (d = 0), window.isNaN(e) && (e = 0), e > d) return -1;
                if (d > e) return 1
            }
            return 0
        }, b.version = function(a) {
            return new c(a)
        }
    }(window, window.lib || (window.lib = {})), function(a, b) {
    b.env = b.env || {};
    var c = a.location.search.replace(/^\?/, "");
    if (b.env.params = {}, c) for (var d = c.split("&"), e = 0; e < d.length; e++) {
        d[e] = d[e].split("=");
        try {
            b.env.params[d[e][0]] = decodeURIComponent(d[e][1])
        } catch (f) {
            b.env.params[d[e][0]] = d[e][1]
        }
    }
}(window, window.lib || (window.lib = {})), function(a, b) {
    b.env = b.env || {};
    var c, d = a.navigator.userAgent;
    if (c = d.match(/Windows\sPhone\s(?:OS\s)?([\d\.]+)/)) b.env.os = {
        name: "Windows Phone",
        isWindowsPhone: !0,
        version: c[1]
    };
    else if (c = d.match(/Android[\s\/]([\d\.]+)/)) b.env.os = {
        name: "Android",
        isAndroid: !0,
        version: c[1]
    };
    else if (c = d.match(/(iPhone|iPad|iPod)/)) {
        var e = c[1];
        c = d.match(/OS ([\d_\.]+) like Mac OS X/), b.env.os = {
            name: e,
            isIPhone: "iPhone" === e || "iPod" === e,
            isIPad: "iPad" === e,
            isIOS: !0,
            version: c[1].split("_").join(".")
        }
    } else b.env.os = {
        name: "unknown",
        version: "0.0.0"
    };
    b.version && (b.env.os.version = b.version(b.env.os.version))
}(window, window.lib || (window.lib = {})), function(a, b) {
    b.env = b.env || {};
    var c, d = a.navigator.userAgent;
    (c = d.match(/(?:UCWEB|UCBrowser\/)([\d\.]+)/)) ? b.env.browser = {
        name: "UC",
        isUC: !0,
        version: c[1]
    } : (c = d.match(/MQQBrowser\/([\d\.]+)/)) ? b.env.browser = {
        name: "QQ",
        isQQ: !0,
        version: c[1]
    } : (c = d.match(/MSIE\s([\d\.]+)/)) || (c = d.match(/IEMobile\/([\d\.]+)/)) ? (b.env.browser = {
        name: "IE",
        isIE: !0,
        version: c[1]
    }, d.match(/IEMobile/) && (b.env.browser.isIEMobile = !0), d.match(/Android|iPhone/) && (b.env.browser.isIELikeWebkit = !0)) : (c = d.match(/(?:Chrome|CriOS)\/([\d\.]+)/)) ? (b.env.browser = {
        name: "Chrome",
        isChrome: !0,
        version: c[1]
    }, d.match(/Version\/[\d+\.]+\s*Chrome/) && (b.env.browser.isWebview = !0)) : d.match(/Mobile Safari/) && (c = d.match(/Android[\s\/]([\d\.]+)/)) ? b.env.browser = {
        name: "Android",
        isAndroid: !0,
        version: c[1]
    } : d.match(/iPhone|iPad|iPod/) ? d.match(/Safari/) ? (c = d.match(/Version\/([\d\.]+)/), b.env.browser = {
        name: "Safari",
        isSafari: !0,
        version: c[1]
    }) : (c = d.match(/OS ([\d_\.]+) like Mac OS X/), b.env.browser = {
        name: "iOS Webview",
        isWebview: !0,
        version: c[1].replace(/\_/, ".")
    }) : b.env.browser = {
        name: "unknown",
        version: "0.0.0"
    }, b.version && (b.env.browser.version = b.version(b.env.browser.version))
}(window, window.lib || (window.lib = {})), function(a, b) {
    b.env = b.env || {};
    var c, d, e = b.env.params.ttid,
        f = a.navigator.userAgent;
    (d = f.match(/WindVane[\/\s]([\d\.\_]+)/)) && (c = d[1]);
    var g = !1,
        h = "",
        i = "",
        j = "";
    if (d = f.match(/AliApp\(([A-Z\-]+)\/([\d\.]+)\)/)) g = !0, h = d[1], j = d[2], i = h.indexOf("-PD") > 0 ? b.env.os.isIOS ? "iPad" : b.env.os.isAndroid ? "aPad" : b.env.os.name : b.env.os.name;
    else if (d = f.match(/@([^_@]+)_(iphone|android|ipad|apad)_([\d\.]+)/)) g = !0, h = d[1], i = d[2].replace(/^ip/, "iP").replace(/^a/, "A"), j = d[3];
    else if (e && (d = e.match(/@([^_@]+)_(iphone|android|ipad|apad)_([\d\.]+)/))) g = !0, h = d[1], i = d[2].replace(/^ip/, "iP").replace(/^a/, "A"), j = d[3];
    else if (c) {
        g = !0, i = b.env.os.name;
        var k = b.version(c);
        b.env.os.isAndroid ? k.gte("2.5.1") && k.lte("2.5.5") ? (h = "taobao", j = "3.9.2") : k.eq("2.5.6") ? (h = "taobao", j = "3.9.3") : k.eq("2.6.0") && (h = "taobao", j = "3.9.5") : b.env.os.isIOS && (k.gte("2.5.0") && k.lt("2.6.0") ? (h = "taobao", j = "3.4.0") : k.eq("2.6.0") && (h = "taobao", j = "3.4.5"))
    }!h && f.indexOf("TBIOS") > 0 && (h = "TB"), b.env.aliapp = g ? {
        windvane: b.version(c || "0.0.0"),
        appname: ("taobao" === h ? "TB" : h) || "unkown",
        version: b.version(j || "0.0.0"),
        platform: i || b.env.os.name
    } : !1, b.env.taobaoApp = b.env.aliapp
}(window, window.lib || (window.lib = {}));
!
    function a(b, c, d) {
        function e(g, h) {
            if (!c[g]) {
                if (!b[g]) {
                    var i = "function" == typeof require && require;
                    if (!h && i) return i(g, !0);
                    if (f) return f(g, !0);
                    var j = new Error("Cannot find module '" + g + "'");
                    throw j.code = "MODULE_NOT_FOUND", j
                }
                var k = c[g] = {
                    exports: {}
                };
                b[g][0].call(k.exports, function(a) {
                    var c = b[g][1][a];
                    return e(c ? c : a)
                }, k, k.exports, a, b, c, d)
            }
            return c[g].exports
        }
        for (var f = "function" == typeof require && require, g = 0; g < d.length; g++) e(d[g]);
        return e
    }({
        1: [function(a, b) {
            function c() {}
            var d = b.exports = {};
            d.nextTick = function() {
                var a = "undefined" != typeof window && window.setImmediate,
                    b = "undefined" != typeof window && window.postMessage && window.addEventListener;
                if (a) return function(a) {
                    return window.setImmediate(a)
                };
                if (b) {
                    var c = [];
                    return window.addEventListener("message", function(a) {
                        var b = a.source;
                        if ((b === window || null === b) && "process-tick" === a.data && (a.stopPropagation(), c.length > 0)) {
                            var d = c.shift();
                            d()
                        }
                    }, !0), function(a) {
                        c.push(a), window.postMessage("process-tick", "*")
                    }
                }
                return function(a) {
                    setTimeout(a, 0)
                }
            }(), d.title = "browser", d.browser = !0, d.env = {}, d.argv = [], d.on = c, d.addListener = c, d.once = c, d.off = c, d.removeListener = c, d.removeAllListeners = c, d.emit = c, d.binding = function() {
                throw new Error("process.binding is not supported")
            }, d.cwd = function() {
                return "/"
            }, d.chdir = function() {
                throw new Error("process.chdir is not supported")
            }
        }, {}],
        2: [function(a, b) {
            "use strict";

            function c(a) {
                function b(a) {
                    return null === i ? void k.push(a) : void f(function() {
                        var b = i ? a.onFulfilled : a.onRejected;
                        if (null === b) return void(i ? a.resolve : a.reject)(j);
                        var c;
                        try {
                            c = b(j)
                        } catch (d) {
                            return void a.reject(d)
                        }
                        a.resolve(c)
                    })
                }
                function c(a) {
                    try {
                        if (a === l) throw new TypeError("A promise cannot be resolved with itself.");
                        if (a && ("object" == typeof a || "function" == typeof a)) {
                            var b = a.then;
                            if ("function" == typeof b) return void e(b.bind(a), c, g)
                        }
                        i = !0, j = a, h()
                    } catch (d) {
                        g(d)
                    }
                }
                function g(a) {
                    i = !1, j = a, h()
                }
                function h() {
                    for (var a = 0, c = k.length; c > a; a++) b(k[a]);
                    k = null
                }
                if ("object" != typeof this) throw new TypeError("Promises must be constructed via new");
                if ("function" != typeof a) throw new TypeError("not a function");
                var i = null,
                    j = null,
                    k = [],
                    l = this;
                this.then = function(a, c) {
                    return new l.constructor(function(e, f) {
                        b(new d(a, c, e, f))
                    })
                }, e(a, c, g)
            }
            function d(a, b, c, d) {
                this.onFulfilled = "function" == typeof a ? a : null, this.onRejected = "function" == typeof b ? b : null, this.resolve = c, this.reject = d
            }
            function e(a, b, c) {
                var d = !1;
                try {
                    a(function(a) {
                        d || (d = !0, b(a))
                    }, function(a) {
                        d || (d = !0, c(a))
                    })
                } catch (e) {
                    if (d) return;
                    d = !0, c(e)
                }
            }
            var f = a("asap");
            b.exports = c
        }, {
            asap: 4
        }],
        3: [function(a, b) {
            "use strict";

            function c(a) {
                this.then = function(b) {
                    return "function" != typeof b ? this : new d(function(c, d) {
                        e(function() {
                            try {
                                c(b(a))
                            } catch (e) {
                                d(e)
                            }
                        })
                    })
                }
            }
            var d = a("./core.js"),
                e = a("asap");
            b.exports = d, c.prototype = d.prototype;
            var f = new c(!0),
                g = new c(!1),
                h = new c(null),
                i = new c(void 0),
                j = new c(0),
                k = new c("");
            d.resolve = function(a) {
                if (a instanceof d) return a;
                if (null === a) return h;
                if (void 0 === a) return i;
                if (a === !0) return f;
                if (a === !1) return g;
                if (0 === a) return j;
                if ("" === a) return k;
                if ("object" == typeof a || "function" == typeof a) try {
                    var b = a.then;
                    if ("function" == typeof b) return new d(b.bind(a))
                } catch (e) {
                    return new d(function(a, b) {
                        b(e)
                    })
                }
                return new c(a)
            }, d.all = function(a) {
                var b = Array.prototype.slice.call(a);
                return new d(function(a, c) {
                    function d(f, g) {
                        try {
                            if (g && ("object" == typeof g || "function" == typeof g)) {
                                var h = g.then;
                                if ("function" == typeof h) return void h.call(g, function(a) {
                                    d(f, a)
                                }, c)
                            }
                            b[f] = g, 0 === --e && a(b)
                        } catch (i) {
                            c(i)
                        }
                    }
                    if (0 === b.length) return a([]);
                    for (var e = b.length, f = 0; f < b.length; f++) d(f, b[f])
                })
            }, d.reject = function(a) {
                return new d(function(b, c) {
                    c(a)
                })
            }, d.race = function(a) {
                return new d(function(b, c) {
                    a.forEach(function(a) {
                        d.resolve(a).then(b, c)
                    })
                })
            }, d.prototype["catch"] = function(a) {
                return this.then(null, a)
            }
        }, {
            "./core.js": 2,
            asap: 4
        }],
        4: [function(a, b) {
            (function(a) {
                function c() {
                    for (; e.next;) {
                        e = e.next;
                        var a = e.task;
                        e.task = void 0;
                        var b = e.domain;
                        b && (e.domain = void 0, b.enter());
                        try {
                            a()
                        } catch (d) {
                            if (i) throw b && b.exit(), setTimeout(c, 0), b && b.enter(), d;
                            setTimeout(function() {
                                throw d
                            }, 0)
                        }
                        b && b.exit()
                    }
                    g = !1
                }
                function d(b) {
                    f = f.next = {
                        task: b,
                        domain: i && a.domain,
                        next: null
                    }, g || (g = !0, h())
                }
                var e = {
                        task: void 0,
                        next: null
                    },
                    f = e,
                    g = !1,
                    h = void 0,
                    i = !1;
                if ("undefined" != typeof a && a.nextTick) i = !0, h = function() {
                    a.nextTick(c)
                };
                else if ("function" == typeof setImmediate) h = "undefined" != typeof window ? setImmediate.bind(window, c) : function() {
                    setImmediate(c)
                };
                else if ("undefined" != typeof MessageChannel) {
                    var j = new MessageChannel;
                    j.port1.onmessage = c, h = function() {
                        j.port2.postMessage(0)
                    }
                } else h = function() {
                    setTimeout(c, 0)
                };
                b.exports = d
            }).call(this, a("_process"))
        }, {
            _process: 1
        }],
        5: [function() {
            "function" != typeof Promise.prototype.done && (Promise.prototype.done = function() {
                var a = arguments.length ? this.then.apply(this, arguments) : this;
                a.then(null, function(a) {
                    setTimeout(function() {
                        throw a
                    }, 0)
                })
            })
        }, {}],
        6: [function(a) {
            a("asap");
            "undefined" == typeof Promise && (Promise = a("./lib/core.js"), a("./lib/es6-extensions.js")), a("./polyfill-done.js")
        }, {
            "./lib/core.js": 2,
            "./lib/es6-extensions.js": 3,
            "./polyfill-done.js": 5,
            asap: 4
        }]
    }, {}, [6]);
!
    function(a, b) {
        function c() {
            var a = {},
                b = new p(function(b, c) {
                    a.resolve = b, a.reject = c
                });
            return a.promise = b, a
        }
        function d(a, b) {
            for (var c in b) void 0 === a[c] && (a[c] = b[c]);
            return a
        }
        function e(a) {
            var b = document.getElementsByTagName("head")[0] || document.getElementsByTagName("body")[0] || document.firstElementChild || document;
            b.appendChild(a)
        }
        function f(a) {
            var b = [];
            for (var c in a) a[c] && b.push(c + "=" + encodeURIComponent(a[c]));
            return b.join("&")
        }
        function g(a) {
            return a.substring(a.lastIndexOf(".", a.lastIndexOf(".") - 1) + 1)
        }
        function h(a) {
            function b(a, b) {
                return a << b | a >>> 32 - b
            }
            function c(a, b) {
                var c, d, e, f, g;
                return e = 2147483648 & a, f = 2147483648 & b, c = 1073741824 & a, d = 1073741824 & b, g = (1073741823 & a) + (1073741823 & b), c & d ? 2147483648 ^ g ^ e ^ f : c | d ? 1073741824 & g ? 3221225472 ^ g ^ e ^ f : 1073741824 ^ g ^ e ^ f : g ^ e ^ f
            }
            function d(a, b, c) {
                return a & b | ~a & c
            }
            function e(a, b, c) {
                return a & c | b & ~c
            }
            function f(a, b, c) {
                return a ^ b ^ c
            }
            function g(a, b, c) {
                return b ^ (a | ~c)
            }
            function h(a, e, f, g, h, i, j) {
                return a = c(a, c(c(d(e, f, g), h), j)), c(b(a, i), e)
            }
            function i(a, d, f, g, h, i, j) {
                return a = c(a, c(c(e(d, f, g), h), j)), c(b(a, i), d)
            }
            function j(a, d, e, g, h, i, j) {
                return a = c(a, c(c(f(d, e, g), h), j)), c(b(a, i), d)
            }
            function k(a, d, e, f, h, i, j) {
                return a = c(a, c(c(g(d, e, f), h), j)), c(b(a, i), d)
            }
            function l(a) {
                for (var b, c = a.length, d = c + 8, e = (d - d % 64) / 64, f = 16 * (e + 1), g = new Array(f - 1), h = 0, i = 0; c > i;) b = (i - i % 4) / 4, h = i % 4 * 8, g[b] = g[b] | a.charCodeAt(i) << h, i++;
                return b = (i - i % 4) / 4, h = i % 4 * 8, g[b] = g[b] | 128 << h, g[f - 2] = c << 3, g[f - 1] = c >>> 29, g
            }
            function m(a) {
                var b, c, d = "",
                    e = "";
                for (c = 0; 3 >= c; c++) b = a >>> 8 * c & 255, e = "0" + b.toString(16), d += e.substr(e.length - 2, 2);
                return d
            }
            function n(a) {
                a = a.replace(/\r\n/g, "\n");
                for (var b = "", c = 0; c < a.length; c++) {
                    var d = a.charCodeAt(c);
                    128 > d ? b += String.fromCharCode(d) : d > 127 && 2048 > d ? (b += String.fromCharCode(d >> 6 | 192), b += String.fromCharCode(63 & d | 128)) : (b += String.fromCharCode(d >> 12 | 224), b += String.fromCharCode(d >> 6 & 63 | 128), b += String.fromCharCode(63 & d | 128))
                }
                return b
            }
            var o, p, q, r, s, t, u, v, w, x = [],
                y = 7,
                z = 12,
                A = 17,
                B = 22,
                C = 5,
                D = 9,
                E = 14,
                F = 20,
                G = 4,
                H = 11,
                I = 16,
                J = 23,
                K = 6,
                L = 10,
                M = 15,
                N = 21;
            for (a = n(a), x = l(a), t = 1732584193, u = 4023233417, v = 2562383102, w = 271733878, o = 0; o < x.length; o += 16) p = t, q = u, r = v, s = w, t = h(t, u, v, w, x[o + 0], y, 3614090360), w = h(w, t, u, v, x[o + 1], z, 3905402710), v = h(v, w, t, u, x[o + 2], A, 606105819), u = h(u, v, w, t, x[o + 3], B, 3250441966), t = h(t, u, v, w, x[o + 4], y, 4118548399), w = h(w, t, u, v, x[o + 5], z, 1200080426), v = h(v, w, t, u, x[o + 6], A, 2821735955), u = h(u, v, w, t, x[o + 7], B, 4249261313), t = h(t, u, v, w, x[o + 8], y, 1770035416), w = h(w, t, u, v, x[o + 9], z, 2336552879), v = h(v, w, t, u, x[o + 10], A, 4294925233), u = h(u, v, w, t, x[o + 11], B, 2304563134), t = h(t, u, v, w, x[o + 12], y, 1804603682), w = h(w, t, u, v, x[o + 13], z, 4254626195), v = h(v, w, t, u, x[o + 14], A, 2792965006), u = h(u, v, w, t, x[o + 15], B, 1236535329), t = i(t, u, v, w, x[o + 1], C, 4129170786), w = i(w, t, u, v, x[o + 6], D, 3225465664), v = i(v, w, t, u, x[o + 11], E, 643717713), u = i(u, v, w, t, x[o + 0], F, 3921069994), t = i(t, u, v, w, x[o + 5], C, 3593408605), w = i(w, t, u, v, x[o + 10], D, 38016083), v = i(v, w, t, u, x[o + 15], E, 3634488961), u = i(u, v, w, t, x[o + 4], F, 3889429448), t = i(t, u, v, w, x[o + 9], C, 568446438), w = i(w, t, u, v, x[o + 14], D, 3275163606), v = i(v, w, t, u, x[o + 3], E, 4107603335), u = i(u, v, w, t, x[o + 8], F, 1163531501), t = i(t, u, v, w, x[o + 13], C, 2850285829), w = i(w, t, u, v, x[o + 2], D, 4243563512), v = i(v, w, t, u, x[o + 7], E, 1735328473), u = i(u, v, w, t, x[o + 12], F, 2368359562), t = j(t, u, v, w, x[o + 5], G, 4294588738), w = j(w, t, u, v, x[o + 8], H, 2272392833), v = j(v, w, t, u, x[o + 11], I, 1839030562), u = j(u, v, w, t, x[o + 14], J, 4259657740), t = j(t, u, v, w, x[o + 1], G, 2763975236), w = j(w, t, u, v, x[o + 4], H, 1272893353), v = j(v, w, t, u, x[o + 7], I, 4139469664), u = j(u, v, w, t, x[o + 10], J, 3200236656), t = j(t, u, v, w, x[o + 13], G, 681279174), w = j(w, t, u, v, x[o + 0], H, 3936430074), v = j(v, w, t, u, x[o + 3], I, 3572445317), u = j(u, v, w, t, x[o + 6], J, 76029189), t = j(t, u, v, w, x[o + 9], G, 3654602809), w = j(w, t, u, v, x[o + 12], H, 3873151461), v = j(v, w, t, u, x[o + 15], I, 530742520), u = j(u, v, w, t, x[o + 2], J, 3299628645), t = k(t, u, v, w, x[o + 0], K, 4096336452), w = k(w, t, u, v, x[o + 7], L, 1126891415), v = k(v, w, t, u, x[o + 14], M, 2878612391), u = k(u, v, w, t, x[o + 5], N, 4237533241), t = k(t, u, v, w, x[o + 12], K, 1700485571), w = k(w, t, u, v, x[o + 3], L, 2399980690), v = k(v, w, t, u, x[o + 10], M, 4293915773), u = k(u, v, w, t, x[o + 1], N, 2240044497), t = k(t, u, v, w, x[o + 8], K, 1873313359), w = k(w, t, u, v, x[o + 15], L, 4264355552), v = k(v, w, t, u, x[o + 6], M, 2734768916), u = k(u, v, w, t, x[o + 13], N, 1309151649), t = k(t, u, v, w, x[o + 4], K, 4149444226), w = k(w, t, u, v, x[o + 11], L, 3174756917), v = k(v, w, t, u, x[o + 2], M, 718787259), u = k(u, v, w, t, x[o + 9], N, 3951481745), t = c(t, p), u = c(u, q), v = c(v, r), w = c(w, s);
            var O = m(t) + m(u) + m(v) + m(w);
            return O.toLowerCase()
        }
        function i(a) {
            return "[object Object]" == {}.toString.call(a)
        }
        function j(a, b, c) {
            var d = c || {};
            document.cookie = a.replace(/[^+#$&^`|]/g, encodeURIComponent).replace("(", "%28").replace(")", "%29") + "=" + b.replace(/[^+#$&\/:<-\[\]-}]/g, encodeURIComponent) + (d.domain ? ";domain=" + d.domain : "") + (d.path ? ";path=" + d.path : "") + (d.secure ? ";secure" : "") + (d.httponly ? ";HttpOnly" : "")
        }
        function k(a) {
            var b = new RegExp("(?:^|;\\s*)" + a + "\\=([^;]+)(?:;\\s*|$)").exec(document.cookie);
            return b ? b[1] : void 0
        }
        function l(a, b, c) {
            var d = new Date;
            d.setTime(d.getTime() - 864e5);
            var e = "/";
            document.cookie = a + "=;path=" + e + ";domain=." + b + ";expires=" + d.toGMTString(), document.cookie = a + "=;path=" + e + ";domain=." + c + "." + b + ";expires=" + d.toGMTString()
        }
        function m() {
            var b = a.location.hostname;
            if (!b) {
                var c = a.parent.location.hostname;
                c && ~c.indexOf("zebra.alibaba-inc.com") && (b = c)
            }
            var d = ["taobao.net", "taobao.com", "tmall.com", "tmall.hk", "alibaba-inc.com"],
                e = new RegExp("([^.]*?)\\.?((?:" + d.join(")|(?:").replace(/\./g, "\\.") + "))", "i"),
                f = b.match(e) || [],
                g = f[2] || "taobao.com",
                h = f[1] || "m";
            "taobao.net" !== g || "x" !== h && "waptest" !== h && "daily" !== h ? "taobao.net" === g && "demo" === h ? h = "demo" : "alibaba-inc.com" === g && "zebra" === h ? h = "zebra" : "waptest" !== h && "wapa" !== h && "m" !== h && (h = "m") : h = "waptest";
            var i = "h5api";
            r.mainDomain = g, r.subDomain = h, r.prefix = i
        }
        function n() {
            var b = a.navigator.userAgent,
                c = b.match(/WindVane[\/\s]([\d\.\_]+)/);
            c && (r.WindVaneVersion = c[1]);
            var d = b.match(/AliApp\(([^\/]+)\/([\d\.\_]+)\)/i);
            d && (r.AliAppName = d[1], r.AliAppVersion = d[2])
        }
        function o(a) {
            this.id = ++v, this.params = d(a || {}, {
                v: "*",
                data: {},
                type: "get",
                dataType: "jsonp"
            }), this.params.type = this.params.type.toLowerCase(), "object" == typeof this.params.data && (this.params.data = JSON.stringify(this.params.data)), this.middlewares = s.slice(0)
        }
        var p = a.Promise,
            q = (p || {
                resolve: function() {
                    return void 0
                }
            }).resolve();
        String.prototype.trim || (String.prototype.trim = function() {
            return this.replace(/^[\s﻿ ]+|[\s﻿ ]+$/g, "")
        });
        var r = {
                useJsonpResultType: !1,
                safariGoLogin: !0,
                useAlipayJSBridge: !1
            },
            s = [],
            t = {
                ERROR: -1,
                SUCCESS: 0,
                TOKEN_EXPIRED: 1,
                SESSION_EXPIRED: 2
            };
        m(), n();
        var u = "AP" === r.AliAppName && parseFloat(r.AliAppVersion) >= 10.1,
            v = 0,
            w = "2.4.8";
        o.prototype.use = function(a) {
            if (!a) throw new Error("middleware is undefined");
            return this.middlewares.push(a), this
        }, o.prototype.__processRequestMethod = function(a) {
            var b = this.params,
                c = this.options;
            "get" === b.type && "jsonp" === b.dataType ? c.getJSONP = !0 : "get" === b.type && "originaljsonp" === b.dataType ? c.getOriginalJSONP = !0 : "get" === b.type && "json" === b.dataType ? c.getJSON = !0 : "post" === b.type && (c.postJSON = !0), a()
        }, o.prototype.__processRequestType = function(c) {
            var d = this,
                e = this.params,
                f = this.options;
            if (r.H5Request === !0 && (f.H5Request = !0), r.WindVaneRequest === !0 && (f.WindVaneRequest = !0), f.H5Request === !1 && f.WindVaneRequest === !0) {
                if (!u && (!b.windvane || parseFloat(f.WindVaneVersion) < 5.4)) throw new Error("WINDVANE_NOT_FOUND::缂哄皯WindVane鐜");
                if (u && !a.AlipayJSBridge) throw new Error("ALIPAY_NOT_READY::鏀粯瀹濋€氶亾鏈噯澶囧ソ锛屾敮浠樺疂璇疯 https://lark.alipay.com/mtbsdkdocs/mtopjssdkdocs/pucq6z")
            } else if (f.H5Request === !0) f.WindVaneRequest = !1;
            else if ("undefined" == typeof f.WindVaneRequest && "undefined" == typeof f.H5Request && (b.windvane && parseFloat(f.WindVaneVersion) >= 5.4 ? f.WindVaneRequest = !0 : f.H5Request = !0, u)) if (f.WindVaneRequest = f.H5Request = void 0, a.AlipayJSBridge) if (i(e.data)) f.WindVaneRequest = !0;
            else try {
                    i(JSON.parse(e.data)) ? f.WindVaneRequest = !0 : f.H5Request = !0
                } catch (g) {
                    f.H5Request = !0
                } else f.H5Request = !0;
            var h = a.navigator.userAgent.toLowerCase();
            return h.indexOf("youku") > -1 && f.mainDomain.indexOf("youku.com") < 0 && (f.WindVaneRequest = !1, f.H5Request = !0), f.mainDomain.indexOf("youku.com") > -1 && h.indexOf("youku") < 0 && (f.WindVaneRequest = !1, f.H5Request = !0), c ? c().then(function() {
                var a = f.retJson.ret;
                if (a instanceof Array && (a = a.join(",")), f.WindVaneRequest === !0 && u && f.retJson.error || !a || a.indexOf("PARAM_PARSE_ERROR") > -1 || a.indexOf("HY_FAILED") > -1 || a.indexOf("HY_NO_HANDLER") > -1 || a.indexOf("HY_CLOSED") > -1 || a.indexOf("HY_EXCEPTION") > -1 || a.indexOf("HY_NO_PERMISSION") > -1) {
                    if (!u || !(isNaN(f.retJson.error) || a.indexOf("FAIL_SYS_ACCESS_DENIED") > -1)) return u && i(e.data) && (e.data = JSON.stringify(e.data)), r.H5Request = !0, d.__sequence([d.__processRequestType, d.__processToken, d.__processRequestUrl, d.middlewares, d.__processRequest]);
                    "undefined" == typeof f.retJson.api && "undefined" == typeof f.retJson.v && (f.retJson.api = e.api, f.retJson.v = e.v, f.retJson.ret = [f.retJson.error + "::" + f.retJson.errorMessage], f.retJson.data = {})
                }
            }) : void 0
        };
        var x = "_m_h5_c",
            y = "_m_h5_tk",
            z = "_m_h5_tk_enc";
        o.prototype.__getTokenFromAlipay = function() {
            var b = c(),
                d = this.options,
                e = (a.navigator.userAgent, !! location.protocol.match(/^https?\:$/));
            return d.useAlipayJSBridge === !0 && !e && u && a.AlipayJSBridge && a.AlipayJSBridge.call ? a.AlipayJSBridge.call("getMtopToken", function(a) {
                a && a.token && (d.token = a.token), b.resolve()
            }, function() {
                b.resolve()
            }) : b.resolve(), b.promise
        }, o.prototype.__getTokenFromCookie = function() {
            var a = this.options;
            return a.CDR && k(x) ? a.token = k(x).split(";")[0] : a.token = a.token || k(y), a.token && (a.token = a.token.split("_")[0]), p.resolve()
        }, o.prototype.__waitWKWebViewCookie = function(b) {
            var c = this.options;
            c.waitWKWebViewCookieFn && c.H5Request && a.webkit && a.webkit.messageHandlers ? c.waitWKWebViewCookieFn(b) : b()
        }, o.prototype.__processToken = function(a) {
            var b = this,
                c = this.options;
            this.params;
            return c.token && delete c.token, c.WindVaneRequest !== !0 ? q.then(function() {
                return b.__getTokenFromAlipay()
            }).then(function() {
                return b.__getTokenFromCookie()
            }).then(a).then(function() {
                var a = c.retJson,
                    d = a.ret;
                if (d instanceof Array && (d = d.join(",")), d.indexOf("TOKEN_EMPTY") > -1 || c.CDR === !0 && d.indexOf("ILLEGAL_ACCESS") > -1 || d.indexOf("TOKEN_EXOIRED") > -1) {
                    if (c.maxRetryTimes = c.maxRetryTimes || 5, c.failTimes = c.failTimes || 0, c.H5Request && ++c.failTimes < c.maxRetryTimes) return b.__sequence([b.__waitWKWebViewCookie, b.__processToken, b.__processRequestUrl, b.middlewares, b.__processRequest]);
                    c.maxRetryTimes > 0 && (l(x, c.pageDomain, "*"), l(y, c.mainDomain, c.subDomain), l(z, c.mainDomain, c.subDomain)), a.retType = t.TOKEN_EXPIRED
                }
            }) : void a()
        }, o.prototype.__processRequestUrl = function(b) {
            var c = this.params,
                d = this.options;
            if (d.hostSetting && d.hostSetting[a.location.hostname]) {
                var e = d.hostSetting[a.location.hostname];
                e.prefix && (d.prefix = e.prefix), e.subDomain && (d.subDomain = e.subDomain), e.mainDomain && (d.mainDomain = e.mainDomain)
            }
            if (d.H5Request === !0) {
                var f = "//" + (d.prefix ? d.prefix + "." : "") + (d.subDomain ? d.subDomain + "." : "") + d.mainDomain + "/h5/" + c.api.toLowerCase() + "/" + c.v.toLowerCase() + "/",
                    g = c.appKey || ("waptest" === d.subDomain ? "4272" : "12574478"),
                    i = (new Date).getTime(),
                    j = h(d.token + "&" + i + "&" + g + "&" + c.data),
                    k = {
                        jsv: w,
                        appKey: g,
                        t: i,
                        sign: j
                    },
                    l = {
                        data: c.data,
                        ua: c.ua
                    };
                Object.keys(c).forEach(function(a) {
                    "undefined" == typeof k[a] && "undefined" == typeof l[a] && (k[a] = c[a])
                }), d.getJSONP ? k.type = "jsonp" : d.getOriginalJSONP ? k.type = "originaljsonp" : (d.getJSON || d.postJSON) && (k.type = "originaljson"), "undefined" != typeof c.valueType && ("original" === c.valueType ? d.getJSONP || d.getOriginalJSONP ? k.type = "originaljsonp" : (d.getJSON || d.postJSON) && (k.type = "originaljson") : "string" === c.valueType && (d.getJSONP || d.getOriginalJSONP ? k.type = "jsonp" : (d.getJSON || d.postJSON) && (k.type = "json"))), d.useJsonpResultType === !0 && "originaljson" === k.type && delete k.type, d.dangerouslySetProtocol && (f = d.dangerouslySetProtocol + ":" + f), d.querystring = k, d.postdata = l, d.path = f
            }
            b()
        }, o.prototype.__processUnitPrefix = function(a) {
            a()
        };
        var A = 0;
        o.prototype.__requestJSONP = function(a) {
            function b(a) {
                if (k && clearTimeout(k), l.parentNode && l.parentNode.removeChild(l), "TIMEOUT" === a) window[j] = function() {
                    window[j] = void 0;
                    try {
                        delete window[j]
                    } catch (a) {}
                };
                else {
                    window[j] = void 0;
                    try {
                        delete window[j]
                    } catch (b) {}
                }
            }
            var d = c(),
                g = this.params,
                h = this.options,
                i = g.timeout || 2e4,
                j = "mtopjsonp" + (g.jsonpIncPrefix || "") + ++A,
                k = setTimeout(function() {
                    a(h.timeoutErrMsg || "TIMEOUT::鎺ュ彛瓒呮椂"), b("TIMEOUT")
                }, i);
            h.querystring.callback = j;
            var l = document.createElement("script");
            return l.src = h.path + "?" + f(h.querystring) + "&" + f(h.postdata), l.async = !0, l.onerror = function() {
                b("ABORT"), a(h.abortErrMsg || "ABORT::鎺ュ彛寮傚父閫€鍑�")
            }, window[j] = function() {
                h.results = Array.prototype.slice.call(arguments), b(), d.resolve()
            }, e(l), d.promise
        }, o.prototype.__requestJSON = function(b) {
            function d(a) {
                l && clearTimeout(l), "TIMEOUT" === a && i.abort()
            }
            var e = c(),
                g = this.params,
                h = this.options,
                i = new a.XMLHttpRequest,
                j = g.timeout || 2e4,
                l = setTimeout(function() {
                    b(h.timeoutErrMsg || "TIMEOUT::鎺ュ彛瓒呮椂"), d("TIMEOUT")
                }, j);
            h.CDR && k(x) && (h.querystring.c = decodeURIComponent(k(x))), i.onreadystatechange = function() {
                if (4 == i.readyState) {
                    var a, c, f = i.status;
                    if (f >= 200 && 300 > f || 304 == f) {
                        d(), a = i.responseText, c = i.getAllResponseHeaders() || "";
                        try {
                            a = /^\s*$/.test(a) ? {} : JSON.parse(a), a.responseHeaders = c, h.results = [a], e.resolve()
                        } catch (g) {
                            b("PARSE_JSON_ERROR::瑙ｆ瀽JSON澶辫触")
                        }
                    } else d("ABORT"), b(h.abortErrMsg || "ABORT::鎺ュ彛寮傚父閫€鍑�")
                }
            };
            var m, n, o = h.path + "?" + f(h.querystring);
            if (h.getJSON ? (m = "GET", o += "&" + f(h.postdata)) : h.postJSON && (m = "POST", n = f(h.postdata)), i.open(m, o, !0), i.withCredentials = !0, i.setRequestHeader("Accept", "application/json"), i.setRequestHeader("Content-type", "application/x-www-form-urlencoded"), g.headers) for (var p in g.headers) i.setRequestHeader(p, g.headers[p]);
            return i.send(n), e.promise
        }, o.prototype.__requestWindVane = function(a) {
            function d(a) {
                g.results = [a], e.resolve()
            }
            var e = c(),
                f = this.params,
                g = this.options,
                h = f.data,
                i = f.api,
                j = f.v,
                k = g.postJSON ? 1 : 0,
                l = g.getJSON || g.postJSON || g.getOriginalJSONP ? "originaljson" : "";
            "undefined" != typeof f.valueType && ("original" === f.valueType ? l = "originaljson" : "string" === f.valueType && (l = "")), g.useJsonpResultType === !0 && (l = "");
            var m, n, o = "https" === location.protocol ? 1 : 0,
                p = f.isSec || 0,
                q = f.sessionOption || "AutoLoginOnly",
                r = f.ecode || 0;
            return n = "undefined" != typeof f.timer ? parseInt(f.timer) : "undefined" != typeof f.timeout ? parseInt(f.timeout) : 2e4, m = 2 * n, f.needLogin === !0 && "undefined" == typeof f.sessionOption && (q = "AutoLoginAndManualLogin"), "undefined" != typeof f.secType && "undefined" == typeof f.isSec && (p = f.secType), b.windvane.call("MtopWVPlugin", "send", {
                api: i,
                v: j,
                post: String(k),
                type: l,
                isHttps: String(o),
                ecode: String(r),
                isSec: String(p),
                param: JSON.parse(h),
                timer: n,
                sessionOption: q,
                ext_headers: {
                    referer: location.href
                }
            }, d, d, m), e.promise
        }, o.prototype.__requestAlipay = function(b) {
            function d(a) {
                g.results = [a], e.resolve()
            }
            var e = c(),
                f = this.params,
                g = this.options,
                h = {
                    apiName: f.api,
                    apiVersion: f.v,
                    needEcodeSign: !! f.ecode,
                    usePost: !! g.postJSON
                };
            return i(f.data) || (f.data = JSON.parse(f.data)), h.data = f.data, f.ttid && (h.ttid = f.ttid), (g.getJSON || g.postJSON || g.getOriginalJSONP) && (h.type = "originaljson"), "undefined" != typeof f.valueType && ("original" === f.valueType ? h.type = "originaljson" : "string" === f.valueType && delete h.type), g.useJsonpResultType === !0 && delete h.type, a.AlipayJSBridge.call("mtop", h, d), e.promise
        }, o.prototype.__processRequest = function(a, b) {
            var c = this;
            return q.then(function() {
                var a = c.options;
                if (a.H5Request && (a.getJSONP || a.getOriginalJSONP)) return c.__requestJSONP(b);
                if (a.H5Request && (a.getJSON || a.postJSON)) return c.__requestJSON(b);
                if (a.WindVaneRequest) return u ? c.__requestAlipay(b) : c.__requestWindVane(b);
                throw new Error("UNEXCEPT_REQUEST::閿欒鐨勮姹傜被鍨�")
            }).then(a).then(function() {
                var a = c.options,
                    b = (c.params, a.results[0]),
                    d = b && b.ret || [];
                b.ret = d, d instanceof Array && (d = d.join(","));
                var e = b.c;
                a.CDR && e && j(x, e, {
                    domain: a.pageDomain,
                    path: "/"
                }), d.indexOf("SUCCESS") > -1 ? b.retType = t.SUCCESS : b.retType = t.ERROR, a.retJson = b
            })
        }, o.prototype.__sequence = function(a) {
            function b(a) {
                if (a instanceof Array) a.forEach(b);
                else {
                    var g, h = c(),
                        i = c();
                    e.push(function() {
                        return h = c(), g = a.call(d, function(a) {
                            return h.resolve(a), i.promise
                        }, function(a) {
                            return h.reject(a), i.promise
                        }), g && (g = g["catch"](function(a) {
                            h.reject(a)
                        })), h.promise
                    }), f.push(function(a) {
                        return i.resolve(a), g
                    })
                }
            }
            var d = this,
                e = [],
                f = [];
            a.forEach(b);
            for (var g, h = q; g = e.shift();) h = h.then(g);
            for (; g = f.pop();) h = h.then(g);
            return h
        };
        var B = function(a) {
                a()
            },
            C = function(a) {
                a()
            };
        o.prototype.request = function(c) {
            var e = this;
            if (this.options = d(c || {}, r), !p) {
                var f = "褰撳墠娴忚鍣ㄤ笉鏀寔Promise锛岃鍦╳indows瀵硅薄涓婃寕杞絇romise瀵硅薄鍙弬鑰冿紙http://gitlab.alibaba-inc.com/mtb/lib-es6polyfill/tree/master锛変腑鐨勮В鍐虫柟妗�";
                throw b.mtop = {
                    ERROR: f
                }, new Error(f)
            }
            var h = p.resolve([B, C]).then(function(a) {
                var b = a[0],
                    c = a[1];
                return e.__sequence([b, e.__processRequestMethod, e.__processRequestType, e.__processToken, e.__processRequestUrl, e.middlewares, e.__processRequest, c])
            }).then(function() {
                var a = e.options.retJson;
                return a.retType !== t.SUCCESS ? p.reject(a) : e.options.successCallback ? void e.options.successCallback(a) : p.resolve(a)
            })["catch"](function(a) {
                var b;
                return a instanceof Error ? (console.error(a.stack), b = {
                    ret: [a.message],
                    stack: [a.stack],
                    retJson: t.ERROR
                }) : b = "string" == typeof a ? {
                    ret: [a],
                    retJson: t.ERROR
                } : void 0 !== a ? a : e.options.retJson, e.options.failureCallback ? void e.options.failureCallback(b) : p.reject(b)
            });
            return this.__processRequestType(), e.options.H5Request && (e.constructor.__firstProcessor || (e.constructor.__firstProcessor = h), B = function(a) {
                e.constructor.__firstProcessor.then(a)["catch"](a)
            }), ("get" === this.params.type && "json" === this.params.dataType || "post" === this.params.type) && (c.pageDomain = c.pageDomain || g(a.location.hostname), c.mainDomain !== c.pageDomain && (c.maxRetryTimes = 4, c.CDR = !0)), h
        }, b.mtop = function(a) {
            return new o(a)
        }, b.mtop.request = function(a, b, c) {
            var d = {
                H5Request: a.H5Request,
                WindVaneRequest: a.WindVaneRequest,
                LoginRequest: a.LoginRequest,
                AntiCreep: a.AntiCreep,
                AntiFlood: a.AntiFlood,
                successCallback: b,
                failureCallback: c || b
            };
            return new o(a).request(d)
        }, b.mtop.H5Request = function(a, b, c) {
            var d = {
                H5Request: !0,
                successCallback: b,
                failureCallback: c || b
            };
            return new o(a).request(d)
        }, b.mtop.middlewares = s, b.mtop.config = r, b.mtop.RESPONSE_TYPE = t, b.mtop.CLASS = o
    }(window, window.lib || (window.lib = {})), function(a, b) {
    function c(a) {
        return a.preventDefault(), !1
    }
    function d(a) {
        var b = new RegExp("(?:^|;\\s*)" + a + "\\=([^;]+)(?:;\\s*|$)").exec(document.cookie);
        return b ? b[1] : void 0
    }
    function e(b, d) {
        var e = this,
            f = a.dpr || 1,
            g = document.createElement("div"),
            h = document.documentElement.getBoundingClientRect(),
            i = Math.max(h.width, window.innerWidth) / f,
            j = Math.max(h.height, window.innerHeight) / f;
        g.style.cssText = ["-webkit-transform:scale(" + f + ") translateZ(0)", "-ms-transform:scale(" + f + ") translateZ(0)", "transform:scale(" + f + ") translateZ(0)", "-webkit-transform-origin:0 0", "-ms-transform-origin:0 0", "transform-origin:0 0", "width:" + i + "px", "height:" + j + "px", "z-index:999999", "position:" + (i > 800 ? "fixed" : "absolute"), "left:0", "top:0px", "background:" + (i > 800 ? "rgba(0,0,0,.5)" : "#FFF"), "display:none"].join(";");
        var k = document.createElement("div");
        k.style.cssText = ["width:100%", "height:52px", "background:#EEE", "line-height:52px", "text-align:left", "box-sizing:border-box", "padding-left:20px", "position:absolute", "left:0", "top:0", "font-size:16px", "font-weight:bold", "color:#333"].join(";"), k.innerText = b;
        var l = document.createElement("a");
        l.style.cssText = ["display:block", "position:absolute", "right:0", "top:0", "height:52px", "line-height:52px", "padding:0 20px", "color:#999"].join(";"), l.innerText = "鍏抽棴";
        var m = document.createElement("iframe");
        m.style.cssText = ["width:100%", "height:100%", "border:0", "overflow:hidden"].join(";"), i > 800 && (k.style.cssText = ["width:370px", "height:52px", "background:#EEE", "line-height:52px", "text-align:left", "box-sizing:border-box", "padding-left:20px", "position:absolute", "left:" + (i / 2 - 185) + "px", "top:40px", "font-size:16px", "font-weight:bold", "color:#333"].join(";"), m.style.cssText = ["position:absolute", "top:92px", "left:" + (i / 2 - 185) + "px", "width:370px", "height:480px", "border:0", "background:#FFF", "overflow:hidden"].join(";")), k.appendChild(l), g.appendChild(k), g.appendChild(m), g.className = "J_MIDDLEWARE_FRAME_WIDGET", document.body.appendChild(g), m.src = d, l.addEventListener("click", function() {
            e.hide();
            var a = document.createEvent("HTMLEvents");
            a.initEvent("close", !1, !1), g.dispatchEvent(a)
        }, !1), this.addEventListener = function() {
            g.addEventListener.apply(g, arguments)
        }, this.removeEventListener = function() {
            g.removeEventListener.apply(g, arguments)
        }, this.show = function() {
            document.addEventListener("touchmove", c, !1), g.style.display = "block", window.scrollTo(0, 0)
        }, this.hide = function() {
            document.removeEventListener("touchmove", c), window.scrollTo(0, -h.top), g.parentNode && g.parentNode.removeChild(g)
        }
    }
    function f(a) {
        var c = this,
            d = this.options,
            e = this.params;
        return a().then(function() {
            var a = d.retJson,
                f = a.ret,
                g = navigator.userAgent.toLowerCase(),
                h = g.indexOf("safari") > -1 && g.indexOf("chrome") < 0 && g.indexOf("qqbrowser") < 0;
            if (f instanceof Array && (f = f.join(",")), (f.indexOf("SESSION_EXPIRED") > -1 || f.indexOf("SID_INVALID") > -1 || f.indexOf("AUTH_REJECT") > -1 || f.indexOf("NEED_LOGIN") > -1) && (a.retType = l.SESSION_EXPIRED, !d.WindVaneRequest && (k.LoginRequest === !0 || d.LoginRequest === !0 || e.needLogin === !0))) {
                if (!b.login) throw new Error("LOGIN_NOT_FOUND::缂哄皯lib.login");
                if (d.safariGoLogin !== !0 || !h || "taobao.com" === d.pageDomain) return b.login.goLoginAsync().then(function(a) {
                    return c.__sequence([c.__processToken, c.__processRequestUrl, c.__processUnitPrefix, c.middlewares, c.__processRequest])
                })["catch"](function(a) {
                    throw "CANCEL" === a ? new Error("LOGIN_CANCEL::鐢ㄦ埛鍙栨秷鐧诲綍") : new Error("LOGIN_FAILURE::鐢ㄦ埛鐧诲綍澶辫触")
                });
                b.login.goLogin()
            }
        })
    }
    function g(a) {
        var b = this.options;
        this.params;
        return b.H5Request !== !0 || k.AntiFlood !== !0 && b.AntiFlood !== !0 ? void a() : a().then(function() {
            var a = b.retJson,
                c = a.ret;
            c instanceof Array && (c = c.join(",")), c.indexOf("FAIL_SYS_USER_VALIDATE") > -1 && a.data.url && (b.AntiFloodReferer ? location.href = a.data.url.replace(/(http_referer=).+/, "$1" + b.AntiFloodReferer) : location.href = a.data.url)
        })
    }
    function h(b) {
        var c = this,
            f = this.options,
            g = this.params;
        return g.forceAntiCreep !== !0 && f.H5Request !== !0 || k.AntiCreep !== !0 && f.AntiCreep !== !0 ? void b() : b().then(function() {
            var b = f.retJson,
                h = b.ret;
            if (h instanceof Array && (h = h.join(",")), h.indexOf("RGV587_ERROR::SM") > -1 && b.data.url) {
                var j = "_m_h5_smt",
                    k = d(j),
                    l = !1;
                if (f.saveAntiCreepToken === !0 && k) {
                    k = JSON.parse(k);
                    for (var m in k) g[m] && (l = !0)
                }
                if (f.saveAntiCreepToken === !0 && k && !l) {
                    for (var m in k) g[m] = k[m];
                    return c.__sequence([c.__processToken, c.__processRequestUrl, c.__processUnitPrefix, c.middlewares, c.__processRequest])
                }
                return new i(function(d, h) {
                    function i() {
                        m.removeEventListener("close", i), a.removeEventListener("message", k), h("USER_INPUT_CANCEL::鐢ㄦ埛鍙栨秷杈撳叆")
                    }
                    function k(b) {
                        var e;
                        try {
                            e = JSON.parse(b.data) || {}
                        } catch (l) {}
                        if (e && "child" === e.type) {
                            m.removeEventListener("close", i), a.removeEventListener("message", k), m.hide();
                            var n;
                            try {
                                n = JSON.parse(decodeURIComponent(e.content)), "string" == typeof n && (n = JSON.parse(n));
                                for (var o in n) g[o] = n[o];
                                f.saveAntiCreepToken === !0 ? (document.cookie = j + "=" + JSON.stringify(n) + ";", a.location.reload()) : c.__sequence([c.__processToken, c.__processRequestUrl, c.__processUnitPrefix, c.middlewares, c.__processRequest]).then(d)
                            } catch (l) {
                                h("USER_INPUT_FAILURE::鐢ㄦ埛杈撳叆澶辫触")
                            }
                        }
                    }
                    var l = b.data.url,
                        m = new e("", l);
                    m.addEventListener("close", i, !1), a.addEventListener("message", k, !1), m.show()
                })
            }
        })
    }
    if (!b || !b.mtop || b.mtop.ERROR) throw new Error("Mtop 鍒濆鍖栧け璐ワ紒璇峰弬鑰僊top鏂囨。http://gitlab.alibaba-inc.com/mtb/lib-mtop");
    var i = a.Promise,
        j = b.mtop.CLASS,
        k = b.mtop.config,
        l = b.mtop.RESPONSE_TYPE;
    b.mtop.middlewares.push(f), b.mtop.loginRequest = function(a, b, c) {
        var d = {
            LoginRequest: !0,
            H5Request: !0,
            successCallback: b,
            failureCallback: c || b
        };
        return new j(a).request(d)
    }, b.mtop.antiFloodRequest = function(a, b, c) {
        var d = {
            AntiFlood: !0,
            successCallback: b,
            failureCallback: c || b
        };
        return new j(a).request(d)
    }, b.mtop.middlewares.push(g), b.mtop.antiCreepRequest = function(a, b, c) {
        var d = {
            AntiCreep: !0,
            successCallback: b,
            failureCallback: c || b
        };
        return new j(a).request(d)
    }, b.mtop.middlewares.push(h)
}(window, window.lib || (window.lib = {}));
!
    function(a, b) {
        function c(a, b) {
            a = a.toString().split("."), b = b.toString().split(".");
            for (var c = 0; c < a.length || c < b.length; c++) {
                var d = parseInt(a[c], 10),
                    e = parseInt(b[c], 10);
                if (window.isNaN(d) && (d = 0), window.isNaN(e) && (e = 0), d < e) return -1;
                if (d > e) return 1
            }
            return 0
        }
        var d = a.Promise,
            e = a.document,
            f = a.navigator.userAgent,
            g = /Windows\sPhone\s(?:OS\s)?[\d\.]+/i.test(f) || /Windows\sNT\s[\d\.]+/i.test(f),
            h = g && a.WindVane_Win_Private && a.WindVane_Win_Private.call,
            i = /iPhone|iPad|iPod/i.test(f),
            j = /Android/i.test(f),
            k = f.match(/WindVane[\/\s](\d+[._]\d+[._]\d+)/),
            l = Object.prototype.hasOwnProperty,
            m = b.windvane = a.WindVane || (a.WindVane = {}),
            n = (a.WindVane_Native, Math.floor(65536 * Math.random())),
            o = 1,
            p = [],
            q = 3,
            r = "hybrid",
            s = "wv_hybrid",
            t = "iframe_",
            u = "param_",
            v = "chunk_",
            w = 6e5,
            x = 6e5,
            y = 6e4;
        k = k ? (k[1] || "0.0.0").replace(/\_/g, ".") : "0.0.0";
        var z = {
                isAvailable: 1 === c(k, "0"),
                call: function(a, b, c, e, f, g) {
                    var h, i;
                    "number" == typeof arguments[arguments.length - 1] && (g = arguments[arguments.length - 1]), "function" != typeof e && (e = null), "function" != typeof f && (f = null), d && (i = {}, i.promise = new d(function(a, b) {
                        i.resolve = a, i.reject = b
                    })), h = A.getSid();
                    var j = {
                        success: e,
                        failure: f,
                        deferred: i
                    };
                    if (g > 0 && (j.timeout = setTimeout(function() {
                            z.onFailure(h, {
                                ret: "HY_TIMEOUT"
                            })
                        }, g)), A.registerCall(h, j), A.registerGC(h, g), z.isAvailable ? A.callMethod(a, b, c, h) : z.onFailure(h, {
                            ret: "HY_NOT_IN_WINDVANE"
                        }), i) return i.promise
                },
                fireEvent: function(a, b, c) {
                    var d = e.createEvent("HTMLEvents");
                    d.initEvent(a, !1, !0), d.param = A.parseData(b || A.getData(c)), e.dispatchEvent(d)
                },
                getParam: function(a) {
                    return A.getParam(a)
                },
                setData: function(a, b) {
                    A.setData(a, b)
                },
                onSuccess: function(a, b) {
                    A.onComplete(a, b, "success")
                },
                onFailure: function(a, b) {
                    A.onComplete(a, b, "failure")
                }
            },
            A = {
                params: {},
                chunks: {},
                calls: {},
                getSid: function() {
                    return (n + o++) % 65536 + ""
                },
                buildParam: function(a) {
                    return a && "object" == typeof a ? JSON.stringify(a) : a || ""
                },
                getParam: function(a) {
                    return this.params[u + a] || ""
                },
                setParam: function(a, b) {
                    this.params[u + a] = b
                },
                parseData: function(a) {
                    var b;
                    if (a && "string" == typeof a) try {
                        b = JSON.parse(a)
                    } catch (a) {
                        b = {
                            ret: ["WV_ERR::PARAM_PARSE_ERROR"]
                        }
                    } else b = a || {};
                    return b
                },
                setData: function() {
                    this.chunks[v + sid] = this.chunks[v + sid] || [], this.chunks[v + sid].push(chunk)
                },
                getData: function(a) {
                    return this.chunks[v + a] ? this.chunks[v + a].join("") : ""
                },
                registerCall: function(a, b) {
                    this.calls[a] = b
                },
                unregisterCall: function(a) {
                    var b = {};
                    return this.calls[a] && (b = this.calls[a], delete this.calls[a]), b
                },
                useIframe: function(a, b) {
                    var c = t + a,
                        d = p.pop();
                    d || (d = e.createElement("iframe"), d.setAttribute("frameborder", "0"), d.style.cssText = "width:0;height:0;border:0;display:none;"), d.setAttribute("id", c), d.setAttribute("src", b), d.parentNode || setTimeout(function() {
                        e.body.appendChild(d)
                    }, 5)
                },
                retrieveIframe: function(a) {
                    var b = t + a,
                        c = e.querySelector("#" + b);
                    p.length >= q ? e.body.removeChild(c) : p.indexOf(c) < 0 && p.push(c)
                },
                callMethod: function(b, c, d, e) {
                    if (d = A.buildParam(d), g) h ? a.WindVane_Win_Private.call(b, c, e, d) : this.onComplete(e, {
                        ret: "HY_NO_HANDLER_ON_WP"
                    }, "failure");
                    else {
                        var f = r + "://" + b + ":" + e + "/" + c + "?" + d;
                        if (i) this.setParam(e, d), this.useIframe(e, f);
                        else if (j) {
                            var k = s + ":";
                            window.prompt(f, k)
                        } else this.onComplete(e, {
                            ret: "HY_NOT_SUPPORT_DEVICE"
                        }, "failure")
                    }
                },
                registerGC: function(a, b) {
                    var c = this,
                        d = Math.max(b || 0, w),
                        e = Math.max(b || 0, y),
                        f = Math.max(b || 0, x);
                    setTimeout(function() {
                        c.unregisterCall(a)
                    }, d), i ? setTimeout(function() {
                        c.params[u + a] && delete c.params[u + a]
                    }, e) : j && setTimeout(function() {
                            c.chunks[v + a] && delete c.chunks[v + a]
                        }, f)
                },
                onComplete: function(a, b, c) {
                    var d = this.unregisterCall(a),
                        e = d.success,
                        f = d.failure,
                        g = d.deferred,
                        h = d.timeout;
                    h && clearTimeout(h), b = b ? b : this.getData(a), b = this.parseData(b);
                    var k = b.ret;
                    "string" == typeof k && (b = b.value || b, b.ret || (b.ret = [k])), "success" === c ? (e && e(b), g && g.resolve(b)) : "failure" === c && (f && f(b), g && g.reject(b)), i ? (this.retrieveIframe(a), this.params[u + a] && delete this.params[u + a]) : j && this.chunks[v + a] && delete this.chunks[v + a]
                }
            };
        for (var B in z) l.call(m, B) || (m[B] = z[B])
    }(window, window.lib || (window.lib = {}));
!
    function(a, b) {
        var c = "data-img",
            d = "data-size",
            e = /_(\d+x\d+|cy\d+i\d+|sum|m|b)?(xz|xc)?(q\d+)?(s\d+)?\.jpg?/i,
            f = 200,
            g = a.document,
            h = /^http:\/\/.*(?:alicdn|taobaocdn|taobao)\.(com|net)\/.*(?:\.(jpg|png|gif|jpeg|webp))?$/gi,
            i = {
                extend: function(a, b) {
                    for (var c in b) a[c] = b[c]
                },
                filter: function(a) {
                    return a.filter(function(a) {
                        return !!a
                    })
                },
                addEvent: function(a, b, c) {
                    a.addEventListener(b, c, !1)
                },
                removeEvent: function(a, b, c) {
                    a.removeEventListener(b, c, !1)
                },
                getTime: function() {
                    return Date.now ? Date.now() : (new Date).getTime()
                },
                isJsonObject: function(a) {
                    return "object" == typeof a && Object.getPrototypeOf(a) === Object.prototype
                },
                uaInTaobaoApp: function() {
                    var a = navigator.userAgent;
                    return null != a.match(/WindVane/i) ? !0 : !1
                },
                getParamVal: function(b) {
                    var c = a.location.search.slice(1),
                        d = {};
                    if ("" !== c) for (var e, f = c.split("&"), g = 0, h = f.length; h > g; g++) e = f[g].split("="), d[e[0]] = e[1] || "";
                    return b ? d[b] : d
                },
                getQ: function(a) {
                    var b = a ? a : this.getParamVal("getStatus"),
                        c = "";
                    switch (b) {
                        case "false":
                            c = "q90";
                            break;
                        case "true":
                            c = "q75";
                            break;
                        case "_noq":
                            c = "";
                            break;
                        default:
                            c = b
                    }
                    return c
                },
                fetchVersion: function() {
                    var a = navigator.appVersion.match(/(iPhone\sOS)\s([\d_]+)/),
                        b = a && !0 || !1,
                        c = b && a[2].split("_");
                    c = c && parseFloat(c.length > 1 ? c.splice(0, 2).join(".") : c[0], 10);
                    var d = b && 6 > c;
                    return this.fetchVersion = function() {
                        return d
                    }, d
                },
                getOffset: function(b, c) {
                    if (b) {
                        if (c || (c = {
                                x: 0,
                                y: 0
                            }), b != a) var d = b.getBoundingClientRect(),
                            e = d.left,
                            f = d.top,
                            g = d.right,
                            h = d.bottom;
                        else e = 0, f = 0, g = e + b.innerWidth, h = f + b.innerHeight;
                        return {
                            left: e,
                            top: f,
                            right: g + c.x,
                            bottom: h + c.y
                        }
                    }
                },
                compare: function(a, b) {
                    var c = b.right > a.left && b.left < a.right,
                        d = b.bottom > a.top && b.top < a.bottom;
                    return c && d
                },
                setImgSrc: function(a, b, c) {
                    if (a) {
                        if (null == a.match(h)) return a;
                        c = c || "", b = b || "";
                        var d = a.lastIndexOf("_."),
                            f = -1 != d ? a.slice(d + 2) : null,
                            g = f && "webp" == f.toLowerCase() ? !0 : !1,
                            i = g ? a.slice(0, d) : a,
                            j = i.match(e);
                        if (null != j) var k = j[1] || b,
                            l = j[3] || c,
                            m = i.replace(e, "_" + k + (j[2] || "") + l + (j[4] || "") + ".jpg");
                        else(b || c) && (i += "_", i += b, i += c, i += ".jpg"), m = i;
                        return g && m + "_.webp" || m
                    }
                },
                clearImgSrc: function(a) {
                    return a && "string" == typeof a ? a.replace(e, "") : void 0
                }
            },
            j = function(b, c) {
                b || c ? i.isJsonObject(b) && (c = b, b = a) : b = a;
                var d = "string" == typeof b ? g.querySelector(b) : b;
                d && (this.Container = d, this.reset(c || {}), this.attachMethod(), this.getItem(), this.filterItem())
            };
        j.prototype = {
            reset: function(b) {
                var c = {
                        lazyHeight: 400,
                        lazyWidth: 0,
                        lazyClass: "lb-lazy",
                        definition: !1,
                        autoDestroy: !0,
                        q: ""
                    },
                    d = i.extend,
                    e = this.defaultAttr;
                e && d(b, e), d(c, b);
                var f = c.q;
                "Boolean" == typeof f && (f = f.toString()), c.q = i.getQ(f);
                var g = a.devicePixelRatio;
                c.definition = c.definition && g && g > 1 || !1;
                var h = c.size;
                h && (h = "object" == typeof h ? h[g] || h["default"] : h), c.size = h || "";
                var j = c.lazyClass;
                c.lazyClass = "." !== j.charAt(0) ? j : j.slice(1), c.lazyClassReg = new RegExp("(^|\\s)" + c.lazyClass + "(\\s|$)"), this.defaultAttr = b, this.attr = c
            },
            get: function(a) {
                return a ? this.attr[a] : null
            },
            fireEvent: function(a) {
                var b = this;
                a && b.reset(a), b.getItem(), b.filterItem()
            },
            handleEvent: function(a) {
                var b = a.type;
                "scroll" === b ? this.filterItem() : "touchstart" === b ? this.tstart() : "touchend" === b && this.tend()
            },
            tstart: function() {
                this._touchLazy = {
                    sy: a.pageYOffset,
                    time: i.getTime()
                }, this._timerPhone && clearTimeout(this._timerPhone)
            },
            tend: function() {
                var b = this,
                    c = b._touchLazy,
                    d = Math.abs(a.pageYOffset - c.sy);
                if (d > 5) {
                    var e = i.getTime() - c.time;
                    b._timerPhone = setTimeout(function() {
                        b.filterItem(), b.touchLazy = {}, clearTimeout(b._timerPhone), b._timerPhone = null
                    }, e > 100 ? 0 : 10)
                }
            },
            attachMethod: function() {
                var b = i.addEvent;
                b(a, "scroll", this);
                var c = i.fetchVersion();
                c && (b(g, "touchstart", this), b(g, "touchend", this))
            },
            destroy: function() {
                var b = i.removeEvent;
                b(a, "scroll", this);
                var c = i.fetchVersion();
                c && (b(g, "touchstart", this), b(g, "touchend", this))
            },
            getItem: function() {
                var b = this,
                    c = b.Container,
                    d = b.itemList;
                if (d) for (var e = 0, f = d.length; f > e; e++) d[e] && (d[e].onerror = d[e].onload = null);
                c = c === a ? g : c;
                for (var h = c.querySelectorAll("." + b.get("lazyClass")), i = [], e = 0, f = h.length; f > e; e++) i.push(h[e]);
                b.itemList = i
            },
            filterItem: function() {
                var b = this,
                    c = i,
                    d = (b.Container, {
                        x: b.get("lazyWidth"),
                        y: b.get("lazyHeight")
                    }),
                    e = b.itemList,
                    f = c.filter(e);
                if (f && f.length) for (var g = 0, h = e.length; h > g; g++) e[g] && c.compare(c.getOffset(a, d), c.getOffset(e[g])) && b.fetchItem(e[g], g);
                else b.get("autoDestroy") && b.destroy()
            },
            fetchItem: function(a, b) {
                var e = this,
                    f = a.getAttribute(c),
                    g = a.getAttribute(d),
                    h = e.get("q");
                f && (e.get("definition") && (g || (g = e.get("size"))), f = i.setImgSrc(f, g, h), a.removeAttribute(c), a.removeAttribute(d), a.src = f, a.onload || (a.onload = function() {
                    e.loadItem(this, b)
                }, a.onerror = function() {
                    e.errorItem(this, b)
                }))
            },
            loadItem: function(a, b) {
                this.removeItemAttr(a, b), this.get("loadCallback") && this.get("loadCallback").call(a)
            },
            errorItem: function(a, b) {
                this.removeItemAttr(a, b), this.get("errorCallback") && this.get("errorCallback").call(a)
            },
            removeItemAttr: function(a, b) {
                var c = this;
                a.className = a.className.replace(c.get("lazyClassReg"), ""), a.onerror = a.onload = null, c.itemList[b] = null
            }
        }, j.prototype.constructor = j, b.lazyload = function(a, b) {
            return new j(a, b)
        }, b.lazyload.setImgSrc = i.setImgSrc, b.lazyload.clearImgSrc = i.clearImgSrc, b.lazyload.getNetWork = function(b) {
            var c = a.WindVane;
            i.uaInTaobaoApp() ? c && c.call("TBWeakNetStatus", "getStatus", "", function(a) {
                    b(a.WeakNetStatus)
                }, function() {
                    b()
                }, f) : b()
        }
    }(window, window.lib || (window.lib = {}));
!
    function(a, b) {
        b.secureFilters = function(a) {
            var b = /[---]/g,
                c = /[^\t\n\v\f\r ,\.0-9A-Z_a-z\- -￿]/g,
                d = String(a || "");
            return d = d.replace(b, " "), d.replace(c, function(a) {
                var b = a.charCodeAt(0);
                switch (b) {
                    case 34:
                        return "&quot;";
                    case 38:
                        return "&amp;";
                    case 60:
                        return "&lt;";
                    case 62:
                        return "&gt;";
                    default:
                        if (100 > b) {
                            var c = b.toString(10);
                            return "&#" + c + ";"
                        }
                        var d = b.toString(16).toUpperCase();
                        return "&#x" + d + ";"
                }
            })
        }
    }(window, window.lib || (window.lib = {}));
!
    function(a, b) {
        function c(a) {
            var b = {};
            Object.defineProperty(this, "params", {
                set: function(a) {
                    if ("object" == typeof a) {
                        for (var c in b) delete b[c];
                        for (var c in a) b[c] = a[c]
                    }
                },
                get: function() {
                    return b
                },
                enumerable: !0
            }), Object.defineProperty(this, "search", {
                set: function(a) {
                    if ("string" == typeof a) {
                        0 === a.indexOf("?") && (a = a.substr(1));
                        var c = a.split("&");
                        for (var d in b) delete b[d];
                        for (var e = 0; e < c.length; e++) {
                            var f = c[e].split("=");
                            if (f[0]) try {
                                b[decodeURIComponent(f[0])] = decodeURIComponent(f[1] || "")
                            } catch (g) {
                                b[f[0]] = f[1] || ""
                            }
                        }
                    }
                },
                get: function() {
                    var a = [];
                    for (var c in b) if (b[c]) try {
                        a.push(encodeURIComponent(c) + "=" + encodeURIComponent(b[c]))
                    } catch (d) {
                        a.push(c + "=" + b[c])
                    } else try {
                        a.push(encodeURIComponent(c))
                    } catch (d) {
                        a.push(c)
                    }
                    return a.length ? "?" + a.join("&") : ""
                },
                enumerable: !0
            });
            var c;
            Object.defineProperty(this, "hash", {
                set: function(a) {
                    a && a.indexOf("#") < 0 && (a = "#" + a), c = a || ""
                },
                get: function() {
                    return c
                },
                enumerable: !0
            }), this.set = function(a) {
                a = a || "";
                var b;
                if (!(b = a.match(new RegExp("^([a-z0-9-]+:)?[/]{2}(?:([^@/:?]+)(?::([^@/:]+))?@)?([^:/?#]+)(?:[:]([0-9]+))?([/][^?#;]*)?(?:[?]([^?#]*))?(#[^#]*)?$", "i")))) throw new Error("Wrong uri scheme.");
                this.protocol = b[1] || location.protocol, this.username = b[2] || "", this.password = b[3] || "", this.hostname = this.host = b[4], this.port = b[5] || "", this.pathname = b[6] || "/", this.search = b[7] || "", this.hash = b[8] || "", this.origin = this.protocol + "//" + this.hostname
            }, this.toString = function() {
                var a = this.protocol + "//";
                return this.username && (a += this.username, this.password && (a += ":" + this.password), a += "@"), a += this.host, this.port && "80" !== this.port && (a += ":" + this.port), this.pathname && (a += this.pathname), this.search && (a += this.search), this.hash && (a += this.hash), a
            }, a && this.set(a.toString())
        }
        b.httpurl = function(a) {
            return new c(a)
        }
    }(window, window.lib || (window.lib = {}));