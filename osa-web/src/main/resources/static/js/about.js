var app = new Vue({
    el:'#app',
    mounted:function(){

    },
    data:{
        companyName:"Test股份有限公司",
        periodVlidity:"--",
        maintenanceCutOff:"--",
        authorizedNums:0,
        authorizationCode:"--",
        isDisabled:1,
        nameProject:"OSA",
    },
    methods:{
        exportFile:function () {
           $.get(baseUrl + '/system/about/isFileExist',function (res) {
               if(res.resultCode == 0){
                    if(res.data == 'success'){
                       location.href = baseUrl + '/system/about/fileload';
                   }else{
                       gd.showError('授权文件不存在！');
                   }
               }else{
                    gd.showError(res.resultMsg);
               }
           });
        },
        fileChange:function () {
            $('#value_text').html($('#import_file').val());
            this.isDisabled = 0;
        },
        authClick:function () {
            if(this.isDisabled == 1){
                return false;
            }
            if($('#import_file').val()){
                gd.showLoading();
                var formData = new FormData($('#uploadForm')[0]);
                $.ajax({
                    url:baseUrl + '/system/about/fileupload',
                    type: "post",
                    cache: false,
                    contentType: false,
                    processData: false,
                    dataType: "json",
                    data: formData,
                    success: function (res) {
                        if (!res.resultCode) {
                            gd.closeLoading();//关闭loading
                            gd.showSuccess('授权成功！');
                            var timer = setTimeout(function () {
                                window.location.reload();
                                clearTimeout(timer);
                            },3000);
                        }else{
                            gd.showError(res.resultMsg);
                            gd.closeLoading();//关闭loading
                        }
                    }
                })
            }else{
                gd.showWarning('请选择授权文件！');
            }
        }
    },
    mounted:function(){
        var _self = this;
        /**
         * 获取显示数据
         */
        $.get(baseUrl + '/system/about/authInfo',function(msg){
            if(msg.resultCode == 0){
                var jsonData = JSON.parse(msg.data);
                _self.companyName = jsonData.company;
                _self.periodVlidity = jsonData.beginEndDate;
                _self.maintenanceCutOff = jsonData.endDate;
                _self.authorizedNums = jsonData.authDeviceNum;
                _self.authorizationCode = jsonData.deviceUnique;
            }
        })

        /**
         * 获取项目名称
         * @param  {[type]} msg) {                       _self.nameProject [description]
         * @return {[type]}      [description]
         */
        gd.get(baseUrl + '/system/systemSet/platform/systemName', function (msg) {
            _self.nameProject = msg.data;
        })
    }

})