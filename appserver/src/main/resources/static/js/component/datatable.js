var _datatableLang =
    {
        "sProcessing":   "处理中...",
        "sLengthMenu":   "显示 _MENU_ 行",
        "sZeroRecords":  "没有匹配结果",
        "sInfo":         "第 _START_ 至 _END_ 行，共 _TOTAL_ 行",
        "sInfoEmpty":    "第 0 至 0 行结果，共 0 行",
        "sInfoFiltered": "(由 _MAX_ 行过滤)",
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
<div>
    <table :id="'_datatable_'+id"  class="table table-striped table-bordered" style="width:100%"></table>
</div>
`;
var _datatable;
_datatable = {
    template: _datatableTemplate,
    props: {
        id: {default : 1},
        title: {},
        columns: {},
        rows: {}
    },
    //emits: ['click'],
    data() {
        return {}
    },
    methods: {},
    mounted() {
        console.log(this.id)

        $('#_datatable_' + this.id).DataTable({
            language: _datatableLang,
            "dom": 'l<"toolbar">frtip',
            data: this.rows,
            columns: this.columns
        });
        $("div.toolbar").html('<span>Custom tool bar! Text/images etc.</span>');
    }
};



