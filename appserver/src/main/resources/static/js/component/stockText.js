const _stockTextTemplate = `
    <div class="modal hide" ref="textModal" id="_textModal" aria-modal="true" role="dialog" tabindex='-1'>
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 v-text="title" class="modal-title">编辑文本</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    <textarea ref="content" id="summernote"></textarea>
                </div>
                <div class="modal-footer justify-content-between">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取 消</button>
                    <button @click="saveText()" type="button" class="btn btn-primary" data-dismiss="modal">保 存</button>
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
        title:String,
        code:String,
    },
    data: function () {
        return {
            content: {}
        }
    },
    methods:{
        doOnShow: function (){
            let _this = this;
            $('#summernote').summernote({
                height: 100,
                toolbar: false,
                hint: {
                    match: /:([\-+\w]+)$/,
                    search: function (keyword, callback) {
                        callback($.grep(Object.keys(window._hints), function (item) {
                            return item.indexOf(keyword)  === 0;
                        }));
                    },
                    template: function (item) {
                        var content = window._hints[item];
                        if(item === 'date'){
                            return "[当前时间]"
                        }else{
                            return ':[' + content + ']';
                        }
                    },
                    content: function (item) {
                        var content = window._hints[item];
                        if(item === 'date'){
                            return "["+ _this.tsFormat(new Date().getTime(), true) +"] "
                        }else{
                            return ':[' + content + '] ';
                        }
                    }
                }
            });
            let text = this.$store.state.currentText;
            $('#summernote').summernote('code', text === undefined? '':text.content);
            this.$refs.content.focus()
        },
        saveText: function (){
            var html = $('#summernote').summernote('code');
            let text = this.$store.state.currentText;
            let t = Object.assign(text || {}, {
                type:6,
                content: html
            });
            this.$store.commit('setCurrentText', t);
            axios.post("/text/"+this.code, t).then(function (res) {
                if (res.data.success) {
                    toastify({text: "保存成功"});
                } else {
                    toastify({text: "保存失败：\n" + res.data.data});
                }
                $('#_textModal').modal('hide');
            });
        }
    },
    computed: {

    },
    mounted() {
        let _this = this;
        $(this.$refs.textModal).on("show.bs.modal", this.doOnShow)

        window._hints = {
            "date": "date"
        }
    }

};