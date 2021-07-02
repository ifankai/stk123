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
    <table :id="'_datatable_'+datatableId"  class="table table-striped table-bordered" style="width:100%"></table>
</div>
`;
let _datatable_id = 0;
const _datatable = {
    template: _datatableTemplate,
    props: {
        id: undefined,
        title: {},
        columns: {},
        data: {}
    },
    //emits: ['click'],
    data: function () {
        return {
            datatableId: 0
        }
    },
    methods: {},
    created() {
        //一定要在created里写，如果在mounted里写，则datatable不能加载
        //原因：在mounted里写的话，dom上的id还是datatableId的默认值0，还没有渲染，$('#id')是找不到的，之后id才会被渲染
        //this.datatableId = this.id === undefined ? ++_datatable_id : this.id;
    },
    mounted() {
        this.datatableId = this.id === undefined ? ++_datatable_id : this.id;
        console.log('datatable id:', '_datatable_'+this.datatableId);

        //当你修改了data 的值然后马上获取这个 dom 元素的值，是不能获取到更新后的值，
        //你需要使用 $nextTick 这个回调，让修改后的 data 值渲染更新到 dom 元素之后再获取，才能成功。
        this.$nextTick(function() {
            $('#_datatable_' + this.datatableId).DataTable({
                language: _datatableLang,
                "dom": 'l<"toolbar">frtip',
                //data: this.data,
                //columns: this.columns
                ...this.$props
            });
            $("div.toolbar").html('<span>Custom tool bar! Text/images etc.</span>');
        });
    }
};



