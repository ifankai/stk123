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
var _tsFormat = function ( timestamp ) {
    if(timestamp == null) return '';
    function zeroize( num) {
        return (String(num).length === 1 ? '0' : '') + num;
    }
    timestamp = timestamp / 1000
    const curTimestamp = new Date().getTime() / 1000; //当前时间戳
    const timestampDiff = curTimestamp - timestamp; // 参数时间戳与当前时间戳相差秒数
    const curDate = new Date( curTimestamp * 1000 ); // 当前时间日期对象
    const tmDate = new Date( timestamp * 1000 ); // 参数时间戳转换成的日期对象
    const Y = tmDate.getFullYear(), m = tmDate.getMonth() + 1, d = tmDate.getDate();
    const H = tmDate.getHours(), i = tmDate.getMinutes();
    if(timestampDiff < 0){// 将来时间
        return Y + '年' + zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
    }else if ( timestampDiff < 60 ) { // 一分钟以内
        return "刚刚";
    } else if( timestampDiff < 3600 ) { // 一小时前之内
        return Math.floor( timestampDiff / 60 ) + "分钟前";
    } else if ( curDate.getFullYear() === Y && curDate.getMonth()+1 === m && curDate.getDate() === d ) {
        return '今天' + zeroize(H) + ':' + zeroize(i);
    } else { var newDate = new Date( (curTimestamp - 86400) * 1000 ); // 参数中的时间戳加一天转换成的日期对象
        if ( newDate.getFullYear() === Y && newDate.getMonth()+1 === m && newDate.getDate() === d ) {
            return '昨天' + zeroize(H) + ':' + zeroize(i);
        } else if ( curDate.getFullYear() === Y ) {
            return zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
        } else {
            return Y + '年' + zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
        }
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

async function _search(searchText, page, type, searchFields) {
    if(searchText === '')return[];
    let url = "/search/" + page + '/' + searchText + '?1=1' + (type ? '&type=' + type : '') + (searchFields? '&fields='+searchFields : '');
    return await axios.get(url).then(function (res) {
        let results = res.data.data;
        let prev = null;
        let list = [];
        for (let index in results.list) {
            let result = results.list[index];
            if (prev == null) {
                prev = result;
                list.push(prev)
            } else {
                if ((result.desc != null && result.desc === prev.desc)
                    || (result.content != null && result.content === prev.content)) { //聚合，把重复记录合并为一条
                    if (prev.stocks === undefined) {
                        prev.stocks = [prev.stock, result.stock];
                    } else {
                        prev.stocks.push(result.stock);
                    }
                } else {
                    prev = result;
                    list.push(prev)
                }
            }
        }
        results.list = list;
        return results;
    });
}

const store = Vuex.createStore({
    state: {
        msg: 'Hello World',
        count: 0,
        stockLookPool: [],
        searchText:'',
        searchResults:{list:[]},
        searchResultsShow:false,
        inited:false,
        initing:false,
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
        },
        setSearchText(state, payload){
            state.searchText = payload;
        },
        setSearchResults(state, payload){
            state.searchResults = payload;
        },
        setSearchResultsShow(state, payload){
            state.searchResultsShow = payload;
        },
        setInited(state, payload){
            state.inited = payload;
        },
        setIniting(state, payload){
            state.initing = payload;
        },
    },
    //异步执行，异步：访问服务器后等待响应。
    //actions,相当于其它语言的set，即赋值
    actions:{
        increment(context,payload){
            //setTimeout:模拟服务器调用且延迟2秒。
            setTimeout(() => {
                context.commit('increment',payload);//调用mutations中的increment()方法
            }, 2000);
        },
        async search({commit, state}, payload) {
            let results = await _search(state.searchText, 1, 'stock', 'code,title,name');
            commit('setSearchResults', results);
            console.log('results', state.searchResults)
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

let _searchInVuex = {
    searchSimple: _.debounce(function () {
        if(this.$store.state.searchText === '') {
            this.$store.commit('setSearchResultsShow', false);
            return;
        }
        this.$store.dispatch('search');
        this.$store.commit('setSearchResultsShow', true);
    },500),
    searchResultShow: _.debounce(function (show){
        if(this.$store.state.searchText === ''){
            this.$store.commit('setSearchResultsShow', false);
        }else {
            this.$store.commit('setSearchResultsShow', show);
        }
    }, 200),
    gotoSearch: function (){
        window.open('/q/'+this.$store.state.searchText);
    }
}


const _init = {
    template: `
        <a @click="init()" class="nav-link" :class="$store.state.inited?'disabled':''" role="button">
            <i class="fad fa-spinner-third" :class="$store.state.initing?'fa-spin':''"></i>
        </a>
    `,
    methods: {
        init: function () {
            if(this.$store.state.inited) return;
            this.$store.commit('setIniting', true);
            let _this = this;
            axios.get('/stock/init').then(function (res) {
                let results = res.data.data;
                _this.$store.commit('setInited', true);
                _this.$store.commit('setIniting', false);
            });
        }
    },
    mounted() {
        let _this = this;
        axios.get('/stock/inited').then(function (res) {
            let inited = res.data.data;
            _this.$store.commit('setInited', inited);
        });
    }
}

const _eye = {
    props: ['stock'],
    template: `
          <button @click.prevent="updateLookPool($event, stock)" :title="isInLookPool(stock.code)?'删除观察':'加入观察'"  type="button" class="btn btn-tool">
              <i class="fal fa-eye" :class="isInLookPool(stock.code)?'fa-eye-slash':''"></i>
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
        <div class="modal" :id="id" tabindex='-1'>
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

const _tag = {
    props: {
        stock:{},
        type:[]
    },
    template: `
        <template v-for="item in type">
            <template v-for="tag in stock.tags">            
                <small v-for="tag.type===item" v-text="tag.name" :title="tag.detail" class="badge mr-1" :class="'tag-'+tag.type"></small>
            </template>
        </template>
        `,
    methods:{
    }
};

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
}

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
}

function toggleDropdown (e) {
    const _d = $(e.target).closest('.dropdown'),
        _m = $('.dropdown-menu', _d);
    setTimeout(function(){
        const shouldOpen = e.type !== 'click' && _d.is(':hover');
        _m.toggleClass('show', shouldOpen);
        _d.toggleClass('show', shouldOpen);
        $('[data-toggle="dropdown"]', _d).attr('aria-expanded', shouldOpen);
    }, e.type === 'mouseleave' ? 300 : 0);
}

$(function (){
    //处理navbar下拉列表鼠标经过就显示
    $('#navbarCollapse')
        .on('mouseenter mouseleave','.dropdown',toggleDropdown)
        .on('click', '.dropdown-menu a', toggleDropdown);

    //处理modal垂直居中
    $(".modal").on('shown.bs.modal', function (){
        let $this = $(this);
        let $modal = $this.find('.modal-dialog');
        let m_top = ( $(window).height() - $modal.height() ) / 2;
        if(m_top < 20) m_top = 20;
        $modal.css({'margin' : m_top + 'px auto'})
    })
});

let _stockExcludeMixins = {
    openExcludeModal:function (stock){
        this.excludeStock = stock;
        $('#_excludeModal').modal ('show');
    }
}

const mixins = {
    data(){
        return {
            excludeStock:{}
        }
    },
    methods:{
        ..._stockLookPoolInVuex,
        ..._searchInVuex,
        ..._stockExcludeMixins
    },
    mounted(){
        if (localStorage.stockLookPool) {
            store.commit('setStockLookPool', getDataFromLocalStorage('stockLookPool') || []);
        }
    }
}
function createApp(config){
    //config.methods = Object.assign(config.methods, _stockLookPoolInVuex, _searchInVuex); //和mixins功能类似
    config.mixins = [mixins];
    const app = Vue.createApp(config);
    app.use(store)

    app.component('datatable', _datatable);
    app.component('eye', _eye);
    app.component('modal', _modal);
    app.component('init', _init);
    app.component('stockbody', _stockBody); //不能写成 stockBody，html元素不区分大小写
    app.component('tag', _tag);
    app.component('stockexclude', _stockExclude);

    app.config.globalProperties.tsFormat = _tsFormat;
    app.config.globalProperties.dateFormat = dateFormat;
    app.config.globalProperties._ = _;

    app.mount('#app');
    return app;
}