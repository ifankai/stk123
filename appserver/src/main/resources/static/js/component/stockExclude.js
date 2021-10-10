const _stockExcludeTemplate = `
    <div class="modal hide" id="_excludeModal" aria-modal="true" role="dialog" tabindex='-1'>
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">选择排除期限</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <div v-for="period in common.periods" :key="period.key" class="custom-control custom-radio">
                                <input @click="saveExcludeStock(period.key)" :value="period.key" class="custom-control-input" type="radio" :id="'customRadio-'+period.key" name="customRadio">
                                <label :for="'customRadio-'+period.key" class="custom-control-label">{{period.text}}</label>
                            </div>
                        </div>
                    </form>
                    <div v-text="desc"></div>
                </div>
                <div class="modal-footer justify-content-between">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取 消</button>
                    <button @click="cancelExcludeStock()" type="button" class="btn btn-default">取消排除</button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
`;

const _stockExclude = {
    template: _stockExcludeTemplate,
    props: {
        stock:{type:Object, default: {}},
    },
    data: function () {
        return {
            selectedPeriod:undefined,
            common: {},
        }
    },
    methods:{
        saveExcludeStock:function (key){
            let _this = this;
            let status = Object.assign(this.stock.statuses.find(s=>s.type===1) || {}, {
                code:_this.stock.code,
                type:1,
                subType:key,
                startTime:moment(),
                endTime:moment().add(key, 'day')
            });
            axios.post("/stock/status/1", status).then(function (res) {
                if(res.data.success){
                    toastify({text: "保存成功"});
                    _this.stock.statuses.push(status);
                }else{
                    toastify({text: "保存失败：\n"+res.data.data});
                }
                $('#_excludeModal').modal ('hide');
            });
        },
        cancelExcludeStock:function (){
            let _this = this;
            axios.delete("/stock/status/1/"+_this.stock.code).then(function (res) {
                if(res.data.success){
                    toastify({text: "取消成功"});
                    _this.stock.statuses = _.dropWhile(_this.stock.statuses, function(o) { return o.type===1; });
                }else{
                    toastify({text: "取消失败：\n"+res.data.data});
                }
                $('#_excludeModal').modal ('hide');
            });
        }
    },
    computed: {
        desc: function (){
            if(!this.stock.statuses)return ''
            let status = this.stock.statuses.find(s=>s.type===1);
            if(status !== undefined){
                return '当前排除到 '+ this.tsFormat(status.endTime);
            }
        }
    },
    mounted() {
        let _this = this;
        axios.get("/dict/detail/5000").then(function(res){
            _this.common.periods = res.data.data;
            //console.log('periods',_this.common.periods)
        });
    },
    watch: {
    }

};