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
                                <input v-model="selectedPeriod" :value="period.key" class="custom-control-input" type="radio" :id="'customRadio-'+period.key" name="customRadio">
                                <label :for="'customRadio-'+period.key" class="custom-control-label">{{period.text}}</label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer justify-content-between">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取 消</button>
                    <button @click="saveExcludeStock()" type="button" class="btn btn-primary">保 存</button>
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
        stock:{}
    },
    data: function () {
        return {
            selectedPeriod:undefined,
            common: {}
        }
    },
    methods:{
        saveExcludeStock:function (){
            let _this = this;
            let status = {
                code:_this.stock.code,
                type:1,
                startTime:moment(),
                endTime:moment().add(this.selectedPeriod, 'months')
            };
            axios.post("/stock/status/exclude", status).then(function (res) {
                if(res.data.success){
                    toastify({text: "保存成功"});
                    _this.stock.statuses.push(status);
                }else{
                    toastify({text: "保存失败：\n"+res.data.data});
                }
                $('#_excludeModal').modal ('hide');
            });
        }
    },
    mounted() {
        let _this = this;
        axios.get("/dict/detail/1").then(function(res){
            _this.common.periods = res.data.data;
            console.log('periods',_this.common.periods)
        });
    }

};