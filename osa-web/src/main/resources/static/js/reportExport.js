/**
 *此js文件为 报表的弹窗导出公共文件
 * html还得写写啊  没写公共的  复制即可  后面用的大佬恕罪
 * 使用引进去 传参数即可
 * 导出弹窗
 * @param  {[type]}  tableObj  [表格id]
 * @param  {Boolean} isReport  [是哪个报表的]
 * @param  {[type]}  exportUrl [导出url]
 * @return {[type]}            [description]
 */
function initExport(tableObj,isReport,exportUrl) {
    var indexPage = 1;
    gd.showLayer({
        id: 'exportWind', //可传一个id作为标识
        title: '导出', //窗口标题
        content: $("#export_window").html(),
        size: [430, 240],
        btn: [{
            text: '确定',
            enter: true, //响应回车
            action: function(dom) { //参数为当前窗口dom对象
                var parameData = gd.table(tableObj).getAjaxParam(true);
                if(isReport == "action"){
                     parameData.dateTime = parameData.startTime + " ~ " + parameData.endTime;
                    delete parameData.startTime;
                    delete parameData.endTime;
                }
                if (indexPage == 2) {
                    var validate = gd.validate('#export_box', {});
                    if (!validate.valid()) {
                        return false;
                    } else {
                        var endPage = Number($(".end-page").val().trim());
                        var startPage = Number($(".start-page").val().trim());
                        log(parameData.length)
                        var page = Math.ceil(pageAllNum / parameData.length);
                        if (endPage < startPage) {
                            gd.showWarning("开始页码应小于结束页码");
                            return false;
                        }
                        if(page > 0){
                            if ((endPage > page) || (startPage > page)) {
                                gd.showWarning("导出页码数应小于页码总数");
                                return false;
                            }
                        }
                    }
                    parameData.pageStart = $(".start-page").val().trim();
                    parameData.pageEnd = $(".end-page").val().trim();

                    location.href = baseUrl + exportUrl + $.param(parameData);
                } else {
                    location.href = baseUrl + exportUrl + $.param(parameData);
                }
                dom.close();
                return false; //阻止弹窗自动关闭
            }
        }, {
            text: '取消',
            action: function() {

            }
        }],
        success: function(dom) { //参数为当前窗口dom对象
            var exportVue = new Vue({
                el: "#export_box",
                data: {
                    isNowPage: 1
                },
                mounted: function() {

                },
                methods: {
                    radioChange: function(index) {
                        indexPage = index;
                        this.isNowPage = this.isNowPage == 1 ? 0 : 1;
                    }
                }
            })
        },
        end: function(dom) { //参数为当前窗口dom对象
        }
    });
}