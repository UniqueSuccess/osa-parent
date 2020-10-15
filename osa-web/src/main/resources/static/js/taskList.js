var treeData = '';

Array.prototype.remove = function(val) { 
	var index = this.indexOf(val); 
	if (index > -1) { 
		this.splice(index, 1); 
	} 
}

var taskConfig = new Vue({
    el: '#contentDiv',
    methods: {
    	cycleClick: function(val) {
        },
        pwtypeClick:function(val){

        },
        deleteAsset:function(index,val){

            var dom = gd.showConfirm({
                id: 'wind',
                content: '确定要删除吗？',
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true,
                        action: function(dom) {
                            taskConfig.assetListArr.splice(index,1);
                            taskConfig.assetListIdArr.remove(val);
                        }
                    },
                    {
                        text: '取消',
                        action: function() {
                            dom.close();
                        }
                    }
                ]
            })
        },
        saveConfig:function(val){

        	var validate = gd.validate('#autoUpdatePwd',{
                rules: [
                    {
                        name: 'requiredPwdRule',
                        msg: '必填项',
                        valid: function(value, el) {
                            if(taskConfig.passwordInfo.type == 'FIXED' && !value){
                                return false
                            }else{
                               return true 
                            }
                            
                        }
                    }
                ]
            });
            var result = validate.valid();
            if(result){
                if(!taskConfig.cycleInfo.time){
                    gd.showTip($('.gd-timepicker'), '请选择时间', {
                        id: 'tips',//如果传一个id，将关闭之前相同id的tip
                        time: 3000,//默认在3秒内关闭，可以自定义关闭时间，0为不自动关闭
                        position: 'top'//设置位置，默认自动
                    });
                    return false;
                }

                var params = {
     				// 执行资源
     				resourceId: taskConfig.assetListIdArr,
     				// 执行时间
     				cycle:taskConfig.cycleInfo.cycle,
                    day: taskConfig.cycleInfo.cycle == 'week' ? taskConfig.cycleInfo.weekDay : taskConfig.cycleInfo.monthDay,
                    time:taskConfig.cycleInfo.time,

     				// 密码类型
     				type: taskConfig.passwordInfo.type,
					password: taskConfig.passwordInfo.password,
     				length:taskConfig.passwordInfo.length,
     				number: taskConfig.passwordInfo.number,
					capital: taskConfig.passwordInfo.capital,
					lowercase: taskConfig.passwordInfo.lowercase,
					special: taskConfig.passwordInfo.special,

     				// 密码保存
					ftp: taskConfig.passwordSave.ftp,
					ftpAddr: taskConfig.passwordSave.ftpAddr,
					ftpDir: taskConfig.passwordSave.ftpDir,
					ftpAccount: taskConfig.passwordSave.ftpAccount,
					ftpPwd: taskConfig.passwordSave.ftpPwd,
					email: taskConfig.passwordSave.email,
					emailAddress: taskConfig.passwordSave.emailAddress
                }

	            $.ajax({
	                url:baseUrl + '/task/asset/save',
	                type:'post',
	                // dataType:'json',
	                contentType: "application/json",
	                data:JSON.stringify(params),
	                success:function(res){
	                    if(!res.resultCode){
                            gd.showSuccess('操作成功')
	                    }else{
	                        gd.showError(res.resultMsg);
	                    }
	                }
	            })
	        }
        },
        getImgUrl:function(icon){
           return "/osa/images/asset_type/"+icon+".png";
        },
        resourceAllChecked:function(){
            if(taskConfig.checkboxResource) {
                $('#resourceCheckTable input[type=checkbox]').prop('checked', true);
                // setToolBtnDisable(addAssetVue.toolbarWinConfig,'icon-delete',false);
            } else {
                $('#resourceCheckTable input[type=checkbox]').prop('checked', false);
                // setToolBtnDisable(addAssetVue.toolbarWinConfig,'icon-delete',true);
            }
        },
        resourceEveryChange:function(){
            var length = $('#resourceCheckTable input[type=checkbox]').length;
            var checkLength = $('#resourceCheckTable input[type=checkbox]:checked').length;
            if (length == checkLength) {
                taskConfig.checkboxResource = true;
            } else {
                taskConfig.checkboxResource = false;
            }
            // setToolBtnDisable(addAssetVue.toolbarWinConfig,'icon-delete',length==0);
        }
    },
    data:{
    	assetListArr:[],
    	assetListIdArr:[],
        checkboxResource:false,
    	cycleOption: [
    		{
                name: '单次',
                value: 'none'
            },
            {
                name: '每周',
                value: 'week'
            },
            {
                name: '每月',
                value: 'month'
            }
        ],
        passwordTypeOption:[
	        {
	        	name:'固定',
	        	value:'FIXED'
	        },
	        {	
	        	name:'随机',
	        	value:'RANDOM'
	        },
	        {
	        	name:'统一',
	        	value:'SAME'
	        }
	    ],
        weekCon: [
            {
                name: '周一',
                value: '1'
            },
            {
                name: '周二',
                value: '2'
            },
            {
                name: '周三',
                value: '3'
            },
            {
                name: '周四',
                value: '4'
            },
            {
                name: '周五',
                value: '5'
            },
            {
                name: '周六',
                value: '6'
            },
            {
                name: '周日',
                value: '7'
            }
        ],
        cycleInfo: {
            cycle: '',
            day: '',
            status: '',
            time: '',
            weekDay: '1',
            monthDay: ''
        },
        passwordInfo:{
        	type:'FIXED',
        	fixedCon:'',
        	pwdLength:'',
        	number:false,
        	capital:false,
        	lowercase:false,
        	special:false
        },
        passwordSave:{
        	ftp:'',
        	ftpAddr:'',
        	ftpDir:'',
        	ftpAccount:'',
        	ftpPwd:'',
        	email:'',
      		emailAddress: [],
        },
        timePointPickerConfig: {
            step: 5,
            value: '',
            change: function(time) {
                taskConfig.cycleInfo.time = time;
            }
        },
    	resourceConfig:[
			{
                type: 'button',
                icon: 'icon-add',
                title: '添加',
                action: function () {
                	var addAssetVue= '';
					gd.showLayer({
                        id: 'assetAdd_layer',
                        title: '添加设备',
                        content: $('#assetAddBox').html(),
                        size: [740, 530],
                        btn: [{
                            text: '确定',
                            enter: true,//响应回车
                            action: function (dom) {
                            	var arr3 = addAssetVue.assetDetail;
                            	if(taskConfig.assetListArr.length>0){
                            		arr3 = taskConfig.assetListArr.concat(addAssetVue.assetDetail);
                            	}
                            	taskConfig.assetListArr = arr3;
                            	taskConfig.assetListArr.map(function(item,index){
                            		taskConfig.assetListIdArr.push(item.id)
                            	})
                            }
                        }],
                        success:function(){

                        	addAssetVue = new Vue({
                        		el:'#assetAdd',
                        		data:{
                        			assetList:[],
                                    assetAuthListLen:0,
                                    assetArr:[],
                                    assetDetail:[],
                        			treeAuthConfig:{
                                        id: 'authorizedTree',
                                        accordion: true,
                                        data: '',
                                        showCheckBox: true,
                                        linkable: true,
                                        onChange: function (nodes) {
                                            addAssetVue.assetList = [];
                                            addAssetVue.assetArr = [];
                                            nodes.map(function(item,index){
                                                if(item.id.indexOf('asset')>-1){
                                                    if(!item.ignore){
                                                        var assetCon = gd.tree('authorizedTree').getNode(item.pId);
                                                        var obj = {
                                                            assetGroupName:assetCon.name,
                                                            assetName:item.name
                                                        }
                                                        var objDetail = {
                                                        	id:item.id,
                                                        	icon:item.secondaryIcon,
                                                        	name:item.name,
                                                        	ip:item.ip,
                                                        	groupName:assetCon.name
                                                        }
                                                        addAssetVue.assetDetail.push(objDetail);
                                                        addAssetVue.assetArr.push(item.id);
                                                        addAssetVue.assetList.push(obj);
                                                    }
                                                }
                                            })
                                            addAssetVue.assetAuthListLen = addAssetVue.assetList.length;
                                        },
                                        ready: function (nodes) {
                                        }
                                    },
                        		}
                        	})

                        	if(treeData){
                        		var newData = [];
                        		treeData.map(function(item,index){
                        			var trueOrFalse = false;
                        			if(taskConfig.assetListIdArr.indexOf(item.id) > -1){
										trueOrFalse =  true
                        			}
                        			newData.push({
                        				id:item.id,
                        				name:item.name,
                        				level:item.level,
                        				type:item.tpe,
                        				icon:item.icon,
                        				checked:trueOrFalse,
                        				disabled:trueOrFalse,
                        				ignore:trueOrFalse,
                        				expand:item.expand,
                        				pId:item.pId,
                        				ip:item.ip,
                        			})
                        		})
	                            gd.tree('authorizedTree').setData(newData);
                        	}else{
				                $.ajax({
					                type: 'get',
					                url: baseUrl + "/task/asset/tree",
					                success: function (res) {
					                	treeData = res;
	    	                            gd.tree('authorizedTree').setData(res);
					                }
				                })
			                }
                        }
                    })
                }
	    	},
	    	{
                type: 'button',
                icon: 'icon-delete',
                title: '删除',
                action: function () {
                    var lengthDel = $(".gd-table-body input[type='checkbox']:checked").length;
                    if(lengthDel <= 0){
                        gd.showWarning('请至少选择一条记录', {
                            id: 'warning'
                        });
                        return false;
                    }
                    var dom = gd.showConfirm({
                        id: 'wind',
                        content: '确定要删除吗？',
                        btn: [
                            {
                                text: '删除',
                                class: 'gd-btn-danger',
                                enter: true,
                                action: function(dom) {
                                	var deleteIds = [];
                                    

                					$(".gd-table-body input[type='checkbox']:checked").map(function(index,item){
                						deleteIds.push($(item).data('id'));
                						taskConfig.assetListIdArr.remove($(item).data('id'));
                					})
                					var assetlistArrNew = [];
                					taskConfig.assetListArr.map(function(item,index){
                						if(deleteIds.indexOf(item.id) <= -1){
                							assetlistArrNew.push(item)
                						}
                					})
                					taskConfig.assetListArr = assetlistArrNew
                                }
                            },
                            {
                                text: '取消',
                                action: function() {
                                    dom.close();
                                }
                            }
                        ]
                    })
                }
	    	}
    	]
    }
})


// 回显
$.ajax({
    url: baseUrl + '/task/asset/detail',
    type: 'get',
    success: function(res) {
        var data = res.data;

        // 执行资源
        taskConfig.assetListArr = data.assetList;
        taskConfig.assetListIdArr = [];

       	taskConfig.assetListArr.map(function(item,index){
    		taskConfig.assetListIdArr.push(item.id)
    	})

        // 执行周期
        taskConfig.cycleInfo = {
            cycle: data.cycle,
            day: '',
            status: true,
            time: data.time,
            weekDay: 1,
            monthDay: ''
        }

		if (data.cycle === 'week') {
		    taskConfig.cycleInfo.day = data.day;
		    taskConfig.cycleInfo.weekDay = data.day;
		} else if(data.cycle ==='none'){

		}else {
		    taskConfig.cycleInfo.day = data.day;
		    taskConfig.cycleInfo.monthDay = data.day;
		}

        // 执行时间
        taskConfig.timePointPickerConfig.value =  taskConfig.cycleInfo.time;

        // 密码类型
        taskConfig.passwordInfo = {
        	type:'FIXED',
        	password:'',
        	length:'',
        	number:false,
        	capital:false,
        	lowercase:false,
        	special:false
        }

        taskConfig.passwordInfo.type = data.type;

        if(data.type == 'FIXED'){
        	taskConfig.passwordInfo.password = data.password 
        }else if(data.type == 'RANDOM'){
        	taskConfig.passwordInfo.number = data.number;
        	taskConfig.passwordInfo.capital = data.capital;
        	taskConfig.passwordInfo.lowercase = data.lowercase;
        	taskConfig.passwordInfo.special = data.special;
        	taskConfig.passwordInfo.length = data.length
        }else{
        	taskConfig.passwordInfo.number = data.number;
        	taskConfig.passwordInfo.capital = data.capital;
        	taskConfig.passwordInfo.lowercase = data.lowercase;
        	taskConfig.passwordInfo.special = data.special;
        	taskConfig.passwordInfo.length = data.length
        }

        //密码保存
        taskConfig.passwordSave = {
        	ftp:data.ftp,
        	ftpAddr:data.ftpAddr,
        	ftpDir:data.ftpDir,
        	ftpAccount:data.ftpAccount,
        	ftpPwd:data.ftpPwd,
        	email:data.email,
      		emailAddress:data.emailAddress
        }
    }
})