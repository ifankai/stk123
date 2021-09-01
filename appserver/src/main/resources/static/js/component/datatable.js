var _datatableLang =
    {
        "sProcessing":   "处理中...",
        "sLengthMenu":   "显示 _MENU_ 行",
        "sZeroRecords":  "没有匹配结果",
        "sInfo":         "第_START_至_END_行，共_TOTAL_行",
        "sInfoEmpty":    "第0至0行结果，共0行",
        "sInfoFiltered": "(由_MAX_行过滤)",
        "sInfoPostFix":  "",
        "sSearch":       "搜索:",
        "sUrl":          "",
        "sEmptyTable":     "表中数据为空",
        "sLoadingRecords": "载入中...",
        "sInfoThousands":  ",",
        "oPaginate": {
            "sFirst":    "首页",
            "sPrevious": "<",
            "sNext":     ">",
            "sLast":     "末页"
        },
        "oAria": {
            "sSortAscending":  ": 以升序排列此列",
            "sSortDescending": ": 以降序排列此列"
        }
    };

var _datatableTemplate = `
<div class="dataTables_wrapper dt-bootstrap4">
    <table :id="'_datatable_'+datatableId"  class="table table-valign-middle" :class="tableClass" style="width:100%">
        <thead v-show="columns[0].title">
            <tr>
                <th v-for="column in columns" v-html="column.title"></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="row in data">
                <template v-for="column in columns">
                    <td v-if="column.data == 'stockAndBk'">
                        <table style="margin: auto;font-size: 1rem">
                            <tr><td><span v-html="row.nameAndCodeWithLink"></span><eye :stock="row"></eye></td></tr>
                            <tr><td v-html="dateFormat(row.strategyDate)"></td></tr>
                            <tr><td>
                                <span v-if="row.strategyName.length <= 1" v-html="'策略：'+row.strategyName"></span>
                                <table v-else>
                                    <tr><td :rowspan="row.strategyName.length" style="vertical-align: middle;">策略</td><td v-html="row.strategyName[0]"></td></tr>
                                    <tr v-for="item in row.strategyName.slice(1)"><td v-text="item"></td></tr>
                                </table>
                            </td></tr>
                            <tr><td>&nbsp;</td></tr>
                            <tr v-if="row.bks"><td>
                                <table class="subtable text-nowrap">
                                    <tr><td>板块</td>
                                        <td v-for="bk in row.bks" v-html="bk.bk.nameWithLink" class="text-center"></td>
                                    </tr>
                                    <tr><td>策略</td>
                                        <td v-for="bk in row.bks" v-html="bk.bkRpsName" class="text-center"></td>
                                    </tr>
                                    <tr><td>百分位</td>
                                        <td v-for="bk in row.bks" v-html="bk.bkRpsPercentile" class="text-center"></td>
                                    </tr>
                                    <tr><td>查看</td>
                                        <td v-for="bk in row.bks" class="text-center">
                                            <a title="查看板块精选个股" target="_blank" :href="'/S/'+bk.bkRpsStockCode"><i class="fas fa-th"></i></a>
                                        </td>
                                    </tr>
                                </table>
                            </td></tr>
                        </table>
                    </td>
                    <td v-else-if="column.data == 'stockAndEye'">
                        <span v-html="row.nameAndCodeWithLink"></span><eye :stock="row"></eye>
                    </td>
                    <td v-else-if="column.data == 'titleAndDetail'">
                        <b v-if="row.title"><span v-html="row.title"></span></b>
                        <template v-if="row.desc">
                            <br v-if="row.title"><span v-html="row.desc"></span>
                        </template>
                        <i v-if="row.content != null && row.content.length > row.desc.length" @click="openModalDetail(row)" class="fal fa-file" title="查看详情" data-toggle="modal" :data-target="'#modal-'+row.id"></i>
                    </td>
                    <td v-else v-html="row[column.data]"></td>
                </template>
            </tr>
        </tbody>
    </table>
    <modal :id="modalId" :title="modalTitle" :content="modalContent"></modal>
</div>
`;
let _datatable_id = 0;
function getDataTableOpt (_this){
    let opt = {
        language: _datatableLang,
        //dom:'lfrtip',
        //data: this.data,
        //columns: this.columns
        //paging:   false,

        ..._this.$props
    }
    delete opt.columns;
    delete opt.data;
    if(_this._dom != null){
        opt.dom = _this._dom;
    }
    return opt;
}
const _datatable = {
    template: _datatableTemplate,
    props: {
        type: {type: Number, default: 0},
        id: undefined,
        title: {},
        _dom: {}, //如果直接用 dom='lfrtip' 和 不设置 dom 表格的样式不一样，只能判断一下了
        columns: {},
        data: {},
        ordering: {type: Boolean, default: false},
        order:{},
        paging: {type: Boolean, default: true},
        info: {type: Boolean, default: true},
        pageLength: {type: Number, default: 10},
        searching: {type: Boolean, default: true},
        columnDefs:{},
        tableClass:{},
    },
    //emits: ['click'],
    data: function () {
        return {
            datatableId: 0,
            modalId:'',
            modalTitle:'',
            modalContent:''
        }
    },
    methods: {
        dateFormat:function (date){
            return dateFormat(date);
        },
        openModalDetail:function (row) {
            this.modalId = 'modal-'+row.id;
            this.modalTitle = row.title;
            this.modalContent = row.content;
        }
    },
    created() {
        //一定要在created里写，如果在mounted里写，则datatable不能加载
        //原因：在mounted里写的话，dom上的id还是datatableId的默认值0，还没有渲染，$('#id')是找不到的，之后id才会被渲染
        this.datatableId = this.id === undefined ? ++_datatable_id : this.id;
    },
    mounted() {
        //this.datatableId = this.id === undefined ? ++_datatable_id : this.id;
        //console.log('datatable id:', '_datatable_'+this.datatableId);

        //当你修改了data 的值然后马上获取这个 dom 元素的值，是不能获取到更新后的值，
        //你需要使用 $nextTick 这个回调，让修改后的 data 值渲染更新到 dom 元素之后再获取，才能成功。
        /*this.$nextTick(function() {
            let opt = getDataTableOpt(this);
            $('#_datatable_' + this.datatableId).DataTable(opt);
            //$("div.toolbar").html('<span>Custom tool bar! Text/images etc.</span>');
        });*/
        if(this.type == 1) {
            let opt = getDataTableOpt(this);
            $('#_datatable_' + this.datatableId).DataTable(opt);
        }
    },
    watch: {
        data: function (newVal, oldVal){
            /*let opt = getDataTableOpt(this);
            $('#_datatable_' + this.datatableId).DataTable().clear().destroy();
            $('#_datatable_' + this.datatableId).DataTable(opt);*/

            if(this.type == 0) {
                this.$nextTick(function () {
                    let opt = getDataTableOpt(this);
                    //$('#_datatable_' + this.datatableId).DataTable().clear().destroy();
                    $('#_datatable_' + this.datatableId).DataTable(opt);
                    //$("div.toolbar").html('<span>Custom tool bar! Text/images etc.</span>');
                });
            }
        }
    }
};



