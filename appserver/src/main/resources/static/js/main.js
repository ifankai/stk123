const app = Vue.createApp({
    data() {
        return {
            name: "哈哈哈",
            infos: {},
            posts: {}
        }
    },
    methods: {
        getNotices: function () {
            console.log(this.name);
            axios.get("/notice").then (function (res) {
                console.log(res.data);
                this.posts = res.data;
            });
            return this.posts;
        },
    },
    mounted() {
        // `this` 指向 vm 实例
        console.log('name is: ' + this.name); // => "count is: 1"
        this.posts = initPosts('');
    }
});
const vm = app.mount('#app');