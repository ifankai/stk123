const _stockHeartTemplate = `
    <div class="modal hide" id="_heartModal" aria-modal="true" role="dialog" tabindex='-1'>
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">加入自选</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <div v-for="label in common.labels" :key="label.key" class="custom-control custom-checkbox">
                                <input @click="saveHeartStock(label.key)" :checked="isChecked(label.key)" :value="label.key" class="custom-control-input" type="checkbox" :id="'customCheckbox-'+label.key" name="customCheckbox">
                                <label :for="'customCheckbox-'+label.key" class="custom-control-label">{{label.text}}</label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer justify-content-center">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取 消</button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
`;

const _stockHeart = {
    template: _stockHeartTemplate,
    props: {
        stock:{}
    },
    data: function () {
        return {
            selectedLabels:[],
            common: {}
        }
    },
    methods:{
        saveHeartStock:function (key){
            let _this = this;
            let existing = this.stock.statuses.find(s=>s.type===2 && s.subType===key);
            let status = {
                code: _this.stock.code,
                type: 2,
                subType: key,
                startTime:moment(),
            }
            if(existing === undefined) {
                axios.post("/stock/status/2", status).then(function (res) {
                    if (res.data.success) {
                        toastify({text: "保存成功"});
                        _this.stock.statuses.push(status);
                    } else {
                        toastify({text: "保存失败：\n" + res.data.data});
                    }
                    //$('#_heartModal').modal('hide');
                });
            }else {
                axios.delete("/stock/status/2/"+this.stock.code+"/"+key).then(function (res) {
                    if (res.data.success) {
                        toastify({text: "删除成功"});
                        _this.stock.statuses = _.dropWhile(_this.stock.statuses, function(o) { return o.type===2 && o.subType===key });
                    } else {
                        toastify({text: "删除失败：\n" + res.data.data});
                    }
                    //$('#_heartModal').modal('hide');
                });
            }
        },
        isChecked: function (key) {
            return this.stock.statuses && this.stock.statuses.find(s=>s.type===2 && s.subType === key) !== undefined;
        }
    },
    computed: {

    },
    mounted() {
        let _this = this;
        axios.get("/dict/detail/5010").then(function(res){
            _this.common.labels = res.data.data;
            //console.log('labels',_this.common.labels)
        });
    }

};