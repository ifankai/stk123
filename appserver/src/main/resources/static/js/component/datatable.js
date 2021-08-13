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
    <table :id="'_datatable_'+datatableId"  class="table table-valign-middle" :class="tableClass" style="width:100%"></table>
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
    if(_this._dom != null){
        opt.dom = _this._dom;
    }
    return opt;
}
const _datatable = {
    template: _datatableTemplate,
    props: {
        id: undefined,
        title: {},
        _dom: {}, //如果直接用 dom='lfrtip' 和 不设置 dom 表格的样式不一样，只能判断一下了
        columns: {},
        data: {},
        ordering: {type: Boolean, default: false},
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
            datatableId: 0
        }
    },
    methods: {

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
        let opt = getDataTableOpt(this);
        $('#_datatable_' + this.datatableId).DataTable(opt);
    },
    watch: {
        data: function (newVal, oldVal){
            let opt = getDataTableOpt(this);
            $('#_datatable_' + this.datatableId).DataTable().clear().destroy();
            $('#_datatable_' + this.datatableId).DataTable(opt);
        }
    }
};



