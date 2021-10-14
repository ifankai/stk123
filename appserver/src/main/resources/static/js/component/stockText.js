const _stockTextTemplate = `
    <div class="modal hide" ref="textModal" id="_textModal" aria-modal="true" role="dialog" tabindex='-1' data-backdrop="static">
        <div class="modal-dialog" :style="text.modalWidth?text.modalWidth:{'max-width': '800px'}">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 v-if="text.modalNotEdit !== undefined" v-text="text.title" class="modal-title">编辑文本</h5>
                    <input v-else="text.modalNotEdit === undefined" v-model="text.title" class="modal-title form-control" type="text" placeholder="输入标题..."/>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    <!--<textarea ref="content" id="summernote"></textarea>-->
                    <div ref="content" id="summernote"></div>
                </div>
                <div class="modal-footer justify-content-between">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取 消</button>
                    <template v-if="text.modalNotEdit">
                        <button @click="editText()" type="button" class="btn btn-primary" data-dismiss="modal">编 辑</button>
                    </template>
                    <template v-else>
                        <button @click="saveText()" type="button" class="btn btn-primary" data-dismiss="modal">保 存</button>
                    </template>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
`;

const _stockText = {
    template: _stockTextTemplate,
    props: {
        //toolbar:{type:Boolean, default: false},
        //focus:{type:Boolean, default: true},
    },
    data: function () {
        return {
            text: {}
        }
    },
    methods:{
        getConfig: function (){
            return {
                height: window.innerHeight / 2,
                hint: {
                    match: /:([\-+\w]+)$/,
                    search: function (keyword, callback) {
                        callback($.grep(Object.keys(window._hints), function (item) {
                            return item.indexOf(keyword) === 0;
                        }));
                    },
                    template: function (item) {
                        var content = window._hints[item];
                        if (item === 'date') {
                            return ":[当前时间]"
                        } else {
                            return ':[' + content + ']';
                        }
                    },
                    content: function (item) {
                        var content = window._hints[item];
                        if (item === 'date') {
                            return "[" + moment().format("YYYY-MM-DD") + "] "
                        } else {
                            return ':[' + content + '] ';
                        }
                    }
                }
            }
        },
        doOnShow: function () {
            let _this = this;
            this.text = this.$store.state.currentText;
            console.log('text', this.text)
            $('#summernote').summernote(this.getConfig());
            $('#summernote').summernote('code', this.text.content === undefined ? '' : this.text.content);
            if (this.text.modalNotEdit !== undefined)
                $('#summernote').summernote('destroy');
            //this.$refs.content.focus()
        },
        doOnHide: function (){
            $('#summernote').summernote('destroy');
        },
        saveText: function (){
            var html = $('#summernote').summernote('code');
            let t = Object.assign(this.text || {}, {
                content: html
            });
            this.$store.commit('setCurrentText', t);
            axios.post("/text/"+this.text.code, t).then(function (res) {
                if (res.data.success) {
                    toastify({text: "保存成功"});
                } else {
                    toastify({text: "保存失败：\n" + res.data.data});
                }
                $('#_textModal').modal('hide');
            });
        },
        editText:function (){
            $('#summernote').summernote(Object.assign(this.getConfig(),{focus: true}));
            this.text.modalNotEdit = undefined;
        }
    },
    computed: {

    },
    mounted() {
        let _this = this;
        $(this.$refs.textModal).on("show.bs.modal", this.doOnShow)
        $(this.$refs.textModal).on("hide.bs.modal", this.doOnHide)

        window._hints = {
            "date": "date"
        }
    }

};