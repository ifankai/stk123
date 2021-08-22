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
Date.prototype.format = function (fmt) { //调用：var time1 = new Date().Format("yyyy-MM-dd HH:mm:ss");
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o){
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
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


const store = Vuex.createStore({
    state: {
        msg: 'Hello World',
        count: 0,
        stockLookPool: []
    },
    //同步执行
    //mutations相当于其它语言的set,即赋值
    mutations: {
        increment(state,payload) {
            state.count+=payload;
        },
        setStockLookPool(state, payload){
            state.stockLookPool = payload;
        },
        pushStockToLookPool(state, payload){
            state.stockLookPool.push(payload)
        },
        removeStockFromLookPool(state, payload){
            state.stockLookPool = state.stockLookPool.filter(s => s.code !== payload.code);
        },
        removeAllStocksFromLookPool(state){
            state.stockLookPool = [];
        }
    },
    //异步执行，异步：访问服务器后等待响应。
    //actions,相当于其它语言的set，即赋值
    actions:{
        increment(context,payload){
            //setTimeout:模拟服务器调用且延迟2秒。
            setTimeout(() => {
                context.commit('increment',payload);//调用mutations中的increment()方法
            }, 2000);
        }
    },
    //get属性，所有组件通过get获取值可以得到表现一致的内容
    getters:{
        msgUpper(state){
            return state.msg.toUpperCase();
        },
        count(state){
            return state.count;
        },
        isInStockLookPool: (state) => (code) => {
            return state.stockLookPool.find(s => s.code==code)
        }
    },

});

let _stockLookPoolInVuex = {
    updateLookPool: function (e, stk) {
        let _this = this;
        if (!_this.$store.getters.isInStockLookPool(stk.code)) {
            var btn = $(e.target);

            var offset = btn.offset();
            var posY = offset.top - $(window).scrollTop();
            var posX = offset.left - $(window).scrollLeft();

            //var image = $('<img width="30px" height="30px" src=""/>').css({
            var image = $('<i class="fas fa-eye" ></i>').css({
                "position": "fixed",
                "z-index": "99999",
                "top": posY,
                "left" : posX
            });
            btn.prepend(image);

            var position = $('#stock-look-pool').position();
            image.animate({
                top: position.top,
                left: position.left+30
            }, 500, "linear", function () {
                image.remove();
                _this.$store.commit('setStockLookPool', getDataFromLocalStorage('stockLookPool') || [])
                _this.$store.commit('pushStockToLookPool', stk);
                saveDataToLocalStorage('stockLookPool', _this.$store.state.stockLookPool);
            });
        } else {
            _this.removeStockFromLookPool(stk);
        }
    },
    isInLookPool: function (code) {
        return this.$store.getters.isInStockLookPool(code);
    },
    removeStockFromLookPool:function(stk){
        this.$store.commit('removeStockFromLookPool', stk);
        saveDataToLocalStorage('stockLookPool', this.$store.state.stockLookPool);
    },
    showAllStocksInLookPool:function (){
        window.open('/S/'+this.$store.state.stockLookPool.map(d => d.code).join(','));
    },
    clearAllStocksInLookPool:function (){
        this.$store.commit('removeAllStocksFromLookPool');
        saveDataToLocalStorage('stockLookPool', []);
    }
}

const _eye = {
    props: ['stock'],
    template: `
          <button @click.prevent="updateLookPool($event, stock)" :title="isInLookPool(stock.code)?'删除观察':'加入观察'"  type="button" class="btn btn-tool">
              <i class="fas fa-eye" :class="isInLookPool(stock.code)?'fa-eye-slash':''"></i>
          </button>
        `,
    methods:{
        ..._stockLookPoolInVuex
    }
};

//:title="isInLookPool(stk.code)?'删除观察':'加入观察'"

function createEye(stock, id){
    const vm = Vue.createApp(Object.assign(_eye, {
        data(){
            return {
                stock: stock
            }
        }
    }));
    vm.use(store)
    const wrapper = document.createElement("div")
    console.log(id)
    vm.mount('#'+id)
    return wrapper.innerHTML;
}

const _modal = {
    props: {
        id:String,
        title:String,
        content:String
    },
    template: `
        <div class="modal" :id="id">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div v-if="title" class="modal-header">
                        <h4 v-html="title" class="modal-title"></h4>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <p v-html="content"></p>
                    </div>
                    <div class="modal-footer justify-content-between">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary">Save changes</button>
                    </div>
                </div>
              <!-- /.modal-content -->
            </div>
        <!-- /.modal-dialog -->
      </div>
    `,
}

if (typeof elem == "undefined") {

    function elem(tagName, attributes, children, isHTML) {
        let parent;

        if (typeof tagName == "string") {
            parent = document.createElement(tagName);
        } else if (tagName instanceof HTMLElement) {
            parent = tagName;
        }

        // I'm tired of using null as the attributes, e.g.: elem("div", null, ["some", "elements"])
        // Wouldn't it be nice if I could just do: elem("div", ["some", "elements"])
        // attributes expects a plain object; we can use that to differentiate
        if (typeof attributes != "undefined" && ["undefined", "boolean"].includes(typeof children) && typeof isHTML == "undefined") {
            let attrType = typeof attributes;
            if (["string", "number"].includes(attrType)
                || (attrType == "object" && attributes instanceof Array)
                || (attrType == "object" && attributes instanceof HTMLElement) ) {
                isHTML = children;
                children = attributes;
                attributes = null;
            }
        }

        if (attributes) {
            for (let attribute in attributes) {
                if (attribute.startsWith("on")) {
                    let callback = attributes[attribute];
                    if (typeof callback == "string") {
                        parent.setAttribute(attribute, callback);
                    }
                    else if (typeof callback == "function") {
                        let eventMatch = attribute.match(/^on([a-zA-Z]+)/);
                        if (eventMatch) {
                            let event = eventMatch[1];
                            // TODO: make sure it's a valid event?
                            parent.addEventListener(event, callback);
                            parent.eventListeners = parent.eventListeners || {};
                            parent.eventListeners[event] = parent.eventListeners[event] || [];
                            parent.eventListeners[event].push(callback);
                        }
                    }
                } else {
                    parent.setAttribute(attribute, attributes[attribute]);
                }
            }
        }

        if (typeof children != "undefined" || children === 0) {
            elem.append(parent, children, isHTML);
        }
        return parent;
    };

    elem.append = function (parent, children, isHTML) {
        if (parent instanceof HTMLTextAreaElement || parent instanceof HTMLInputElement) {
            if (children instanceof Text || typeof children == "string" || typeof children == "number") {
                parent.value = children;
            }
            else if (children instanceof Array) {
                children.forEach(function (child) {
                    elem.append(parent, child);
                });
            }
            else if (typeof children == "function") {
                elem.append(parent, children());
            }
        } else {
            if (children instanceof HTMLElement || children instanceof Text) {
                parent.appendChild(children);
            }
            else if (typeof children == "string" || typeof children == "number") {
                if (isHTML) {
                    parent.innerHTML += children;
                } else {
                    parent.appendChild(document.createTextNode(children));
                }
            }
            else if (children instanceof Array) {
                children.forEach(function (child) {
                    elem.append(parent, child);
                });
            }
            else if (typeof children == "function") {
                elem.append(parent, children());
            }
        }
    };

} else {
    if (typeof elem == "function" && typeof elem.hasOwnProperty("append")) {
        console.warn("elem() is already initialized.");
    } else {
        console.warn("The name \"elem\" is already in use by some other script.");
    }
}