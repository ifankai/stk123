const app = Vue.createApp({
    data() {
        return {
            name: "哈哈哈",
            infos: {},
            notices: {}
        }
    },
    methods: {
        getNotices: function () {
            console.log(this.name);
            axios.get("/notice").then (function (res) {
                console.log(res.data);
                this.notices = res.data;
            });
            return this.notices;
        },
    },
    mounted() {
        // `this` 指向 vm 实例
        console.log('name is: ' + this.name); // => "count is: 1"
        initPosts('');
    }
});
const vm = app.mount('#app');