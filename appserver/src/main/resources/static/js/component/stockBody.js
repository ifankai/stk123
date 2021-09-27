const _stockBodyTemplate = `
    <div class="row stock-tabs">
        <div class="col-1 col-md-1 pr-0">
            <div class="nav flex-column nav-tabs h-100" role="tablist" aria-orientation="vertical">
                <a v-if="tabs.includes('dayBar')" @click.prevent="stock.tabShow='dayBar'" :class="{'nav-link':true, active:isTabShow('dayBar')}" :href="'#tab-1-'+stock.code" data-toggle="tab">日线</a>
                <a v-if="tabs.includes('weekBar')" @click.prevent="stock.tabShow='weekBar'" :class="{'nav-link':true, active:isTabShow('weekBar')}" :href="'#tab-2-'+stock.code" data-toggle="tab">周线</a>
                <a v-if="tabs.includes('monthBar')" @click.prevent="stock.tabShow='monthBar'" :class="{'nav-link':true, active:isTabShow('monthBar')}" :href="'#tab-3-'+stock.code" data-toggle="tab">月线</a>
                <a v-if="tabs.includes('news')" @click.prevent="stock.tabShow='news'" :class="{'nav-link':true, active:isTabShow('news')}" :href="'#tab-4-'+stock.code" data-toggle="tab">新闻</a>
            </div>
        </div>
        <div class="col-11 col-sm-11">
            <div class="tab-content p-0">
                <div v-if="tabs.includes('dayBar')" :class="{'tab-pane':true, active:isTabShow('dayBar')}" :id="'tab-1-'+stock.code">
                    <span v-html="stock.dayBarImage"></span>
                    <span style="display: flex;margin-right: 6px;">
                        <img height="80" style="margin-top: -80px;width: 100%" :src="'data:image/png;base64,'+stock.dayFlowImage">
                    </span>
                </div>
                <div v-if="tabs.includes('weekBar')" class="" :class="{'tab-pane':true, active:isTabShow('weekBar')}" :id="'tab-2-'+stock.code">
                    <span v-html="stock.weekBarImage"></span>
                </div>
                <div v-if="tabs.includes('monthBar')" :class="{'tab-pane':true, active:isTabShow('monthBar')}" :id="'tab-3-'+stock.code">
                    <span v-html="stock.monthBarImage"></span>
                </div>
                <div v-if="tabs.includes('news') && stock.news" :class="{'tab-pane':true, active:isTabShow('news')}" :id="'tab-4-'+stock.code">
                    <datatable :="{...common.news, ...newsList}"></datatable>
                </div>

                <div class="tab-pane" :id="'tab-12-'+stock.code" style="display: none">
                    <div class="row">
                        <div class="col-12  col-sm-12">
                            <div class="d-flex justify-content-between align-items-center border-bottom">
                                <p class="text-info">[调研]</p>
                                <p class="d-flex flex-column text-right mb-0">
                                    <span class="font-weight-bold"><i class="ion ion-android-arrow-up text-success"></i> 12% </span>
                                    <span class="text-muted">CONVERSION RATE</span>
                                </p>
                            </div>
                            <div class="d-flex justify-content-between align-items-center border-bottom">
                                <p class="text-success text-xl"><i class="ion ion-ios-refresh-empty"></i></p>
                                <p class="d-flex flex-column text-right mb-0"><span class="font-weight-bold"><i class="ion ion-android-arrow-up text-success"></i> 12% </span><span class="text-muted">CONVERSION RATE</span></p>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-12  col-sm-12">
                            <div class="float-right">
                                <ul class="pagination pagination-sm">
                                    <li class="page-item"><a href="#" class="page-link">&laquo;</a></li>
                                    <li class="page-item"><a href="#" class="page-link">1</a></li>
                                    <li class="page-item"><a href="#" class="page-link">2</a></li>
                                    <li class="page-item"><a href="#" class="page-link">3</a></li>
                                    <li class="page-item"><a href="#" class="page-link">&raquo;</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
`;

const _stockBody = {
    template: _stockBodyTemplate,
    props: {
        stock: {
            type: Object,
            default:{news:[]}
        },
        tabs:{
            type:Array,
            default: ['dayBar','weekBar','monthBar','news']
        },
        tabShow: {type:String, default:'dayBar'}
    },
    //emits: ['click'],
    data: function () {
        return {
            common: {
                news:{
                    type: 1,
                    columns: [{data:"type"},{data:"title"},{data:"infoCreateTime"}],
                    _dom: "ipft",
                    //columns: [{title:"a"},{title:"b"}],
                    columnDefs: [
                        { className: "text-nowrap", "targets": [ 0,2 ] }
                    ],
                    tableClass:{'table-hover':true}
                }
            },
        }
    },
    methods: {
        isTabShow:function (tab){
            if(this.stock.tabShow === undefined){
                return this.tabShow === tab;
            }else{
                return this.stock.tabShow === tab;
            }
        }
    },
    computed:{
        newsList:{
            get:function (){
                this.stock.news.forEach(n => {
                    n.type = '['+n.dict.text+']';
                    n.title = '<a target="_blank" href="'+n.urlTarget+'">'+n.title+'</a>';
                })
                return {data: this.stock.news};
            }
        }
    },
    created() {
    },
    mounted() {

    },
    watch: {
    }
};



