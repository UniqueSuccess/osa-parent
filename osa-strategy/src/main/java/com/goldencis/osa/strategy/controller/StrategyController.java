package com.goldencis.osa.strategy.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.strategy.entity.ProtocolControl;
import com.goldencis.osa.strategy.entity.Strategy;
import com.goldencis.osa.strategy.service.IFileTypeService;
import com.goldencis.osa.strategy.service.IStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  策略表-定义策略基本信息  前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Api(tags="策略管理")
@RestController
@RequestMapping("/strategy")
public class StrategyController {
    //策略服务
    @Autowired
    IStrategyService strategyService;

    //文件类型
    @Autowired
    IFileTypeService fileTypeService;

    @ApiOperation(value = "分页获取策略列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "名称查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String")
    })
    @GetMapping(value = "/getStrategyInPage")
//    @OsaSystemLog(module = "查看策略列表", type = LogType.SYSTEM_SELECT)
    public ResultMsg getStrategyInPage(@RequestParam Map<String, String> params) {
        try{
            //获取分页数据
            IPage<Strategy> page =  strategyService.getStrategyInPage( params);
            return ResultMsg.page(page);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "通过guid获取策略信息")
    @ApiImplicitParam(name = "guid", value = "策略guid", required = true, paramType = "path", dataType = "String")
    @GetMapping(value = "/strategy/{guid}")
//    @OsaSystemLog(module = "查看策略详细", type = LogType.SYSTEM_SELECT)
    public ResultMsg findStrategyByGuid(@PathVariable("guid") String guid) {
        try {
            if (StringUtils.isEmpty(guid)){
                return ResultMsg.error("策略id不能为空" );
            }
            Strategy strategy = strategyService.findStrategyByGuid(guid);
            return ResultMsg.ok(strategy);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "获取所有策略信息列表")
    @GetMapping(value = "/getStrategyAll")
    public ResultMsg getStrategyAll() {
        try {
            List<Strategy> strategies = strategyService.getStrategyAll();
            return ResultMsg.ok(strategies);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "新建策略")
    @ApiImplicitParams({
            @ApiImplicitParam(name="name",value = "策略名称",paramType = "query",dataType = "String"),
//            @ApiImplicitParam(name="sessionType",value = "会话选项（多个英文,分割）",paramType = "query",dataType = "String"),
//            @ApiImplicitParam(name="rdp",value = "RDP选项（多个英文,分割）",paramType = "query",dataType = "String"),
//            @ApiImplicitParam(name="ssh",value = "SSH选项（多个英文,分割）",paramType = "query",dataType = "String"),
//            @ApiImplicitParam(name="strategyLoginTime",value = "登录时间 拼接格式：010101....0101,选中1，未选中0（周一到周天依次累加）",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="strategyLoginTime",value = "登录时间 拼接格式：" +
                    "[{\"endTime\":\"11:30\",\"startTime\":\"09:30\",\"type\":1},{\"endTime\":\"23:30\",\"startTime\":\"21:30\",\"type\":2},{\"type\":3},{\"type\":4},{\"type\":5},{\"endTime\":\"07:30\",\"startTime\":\"06:30\",\"type\":6},{\"type\":7}]",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="strategyCommandBlock",value = "命令控制（阻断会话命令）",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="strategyCommandPending",value = "命令控制（待审核会话命令）",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="strategyCommandProhibit",value = "命令控制（禁止会话命令）",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="screenWatermark",value = "屏幕水印json,示例： {\"content\":{\"computername\":0,\"depname\":1,\"macaddress\":0,\"ip\":1,\"time\":0,\"manualtext\":\"\",\"mode\":0,\"username\":1,\"manual\":0,\"direction\":0,\"localtion\":0,\"locaktiontemp\":\"[0]\",\"color\":16777215,\"tcolor\":\" 白色 \",\"opacity\":125,\"fontsize\":14,\"isshow\":0},\"enable\":1}",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="fileMon",value = "文件审计（多个英文;分割），示例：{\"enable\":0,\"content\":{\"fileExt\":\".txt;.doc;.docx\",\"resourceManager\":0}}",paramType = "query",dataType = "String")
    })
    @PostMapping(value = "/strategy")
    @OsaSystemLog(module = "新建策略", template = "策略名称：%s", args = "0.name", type = LogType.SYSTEM_ADD)
    public ResultMsg saveStrategy(@RequestBody Strategy strategy) {
        try{
            if (Objects.isNull(strategy)){
                return ResultMsg.error("策略信息为空");
            }
//            strategy.setScreenWatermark("{\"content\":{\"computername\":0,\"depname\":1,\"macaddress\":0,\"ip\":1,\"time\":0,\"manualtext\":\"\",\"mode\":0,\"username\":1,\"manual\":0,\"direction\":0,\"localtion\":0,\"locaktiontemp\":\"[0]\",\"color\":16777215,\"tcolor\":\" 白色 \",\"opacity\":125,\"fontsize\":14,\"isshow\":0},\"enable\":1}");
//            strategy.setFileMon("{\"enable\":0,\"content\":{\"fileExt\":\".txt;.doc;.docx\",\"resourceManager\":0}}");
            strategyService.saveStrategy(strategy);
            return ResultMsg.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "编辑策略")
    @ApiImplicitParams({
            @ApiImplicitParam(name="name",value = "策略名称",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="sessionType",value = "会话选项",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="rdp",value = "RDP选项",paramType = "query",dataType = "String"),
            @ApiImplicitParam(name="ssh",value = "SSH选项",paramType = "query",dataType = "String")
    })
    @PutMapping(value = "/strategy")
    @OsaSystemLog(module = "编辑策略",  template = "策略名称：%s", args = "0.name",type = LogType.SYSTEM_UPDATE)
    public ResultMsg editStrategy(@RequestBody Strategy strategy) {
        try{
            if (Objects.isNull(strategy)){
                return ResultMsg.error("策略信息为空");
            }
//            strategy.setScreenWatermark("{\"content\":{\"computername\":1,\"depname\":1,\"macaddress\":1,\"ip\":1,\"time\":1,\"manualtext\":\"\",\"mode\":1,\"username\":1,\"manual\":0,\"direction\":0,\"localtion\":0,\"locaktiontemp\":\"[0]\",\"color\":16777215,\"tcolor\":\" 白色 \",\"opacity\":125,\"fontsize\":14,\"isshow\":0},\"enable\":1}");
//            strategy.setFileMon("{\"enable\":0,\"resourceManager\":0,\"content\":{\"fileExt\":\"*.xls;*.xlsx;*.docx\"}}");
            strategyService.editStrategy(strategy);
            return ResultMsg.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "通过guid删除策略信息")
    @ApiImplicitParam(name = "guid", value = "策略guid", required = true, paramType = "path", dataType = "String")
    @DeleteMapping(value = "/strategy/{guid}")
    @OsaSystemLog(module = "删除策略", template = "策略名称：%ret", ret = "data.name", type = LogType.SYSTEM_DELETE)
    public ResultMsg deleteStrategyByGuid(@PathVariable("guid") String guid) {
        try{
            if (StringUtils.isEmpty(guid)){
                return ResultMsg.error("策略id为空");
            }
            Strategy strategy = strategyService.deleteStrategyByGuid(guid);
            return ResultMsg.ok(strategy);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "通过策略guids批量删除策略信息")
    @ApiImplicitParam(name = "strategyIds", value = "批量策略guids，多个策略id 用英文,分割", required = true, paramType = "query", dataType = "String")
    @DeleteMapping(value = "/deleteStrategiesByGuids")
    @OsaSystemLog(module = "批量删除策略", template = "批量删除的名称为：%ret", ret = "data", type = LogType.SYSTEM_DELETE)
    public ResultMsg deleteStrategiesByGuids(String strategyIds) {
        try{
            if (StringUtils.isEmpty(strategyIds)){
                return ResultMsg.error("策略id为空");
            }
            String strategyNames = strategyService.deleteStrategiesByGuids(Arrays.stream(strategyIds.split(",")).collect(Collectors.toList()));
            return ResultMsg.ok(strategyNames);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "获取协议控制--会话选项")
    @ApiImplicitParam(name = "guid", value = "策略guid", paramType = "query", dataType = "String")
    @GetMapping(value = "/getStrategySessionType")
    public ResultMsg getStrategySessionType(String guid){
        try{
            List<ProtocolControl> list = strategyService.getProtocolControl("HHLX", guid);
            return ResultMsg.ok(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "获取协议控制--RDP选项")
    @ApiImplicitParam(name = "guid", value = "策略guid", paramType = "query", dataType = "String")
    @GetMapping(value = "/getStrategyRDP")
    public ResultMsg getStrategyRDP(String guid){
        try{
            List<ProtocolControl> list = strategyService.getProtocolControl("RDP", guid);
            return ResultMsg.ok(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "获取协议控制--SSH选项")
    @ApiImplicitParam(name = "guid", value = "策略guid", paramType = "query", dataType = "String")
    @GetMapping(value = "/getStrategySSH")
    public ResultMsg getStrategySSH(String guid) {
        try{
            List<ProtocolControl> list = strategyService.getProtocolControl("SSH", guid);
            return ResultMsg.ok(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "通过guid获取客户端策略信息")
//    @ApiImplicitParam(name = "policyGuid", value = "策略guid", required = true, paramType = "query", dataType = "String")
    @ApiImplicitParam(name = "argv", value = "策略拼接，示例：{\"policyGuid\":\"a4ff224b-5944-4ff7-9995-c966de4c7285\"}", required = true, paramType = "query", dataType = "String")
    @PostMapping(value = "/getPolicy")
    public JSONObject findPolicyByGuid(String argv) {
        JSONObject result = new JSONObject();
        try {
            if (StringUtils.isEmpty(argv)){
                result.put("msg", "argv can not be null!");
                return result;
            }

            JSONObject jsonObject = JSONObject.parseObject(argv);
            String guid = jsonObject.getString("policyGuid");

            result = strategyService.findPolicyByGuid(guid);

            //完成策略数据库表设计后添加
            /*Object policy = strategyService.findPolicyByGuid(guid);
            if (Objects.isNull(policy)){
                result.put("msg", "policyGuid can not be null!");
                return result;
            }

            result = strategyService.formatPolicyToJson(policy);*/
//            result = JSONObject.parseObject("{\"screenWatermark\":{\"enable\":0,\"content\":{}},\"fileMon\":{\"enable\":0,\"content\":{\"fileExt\":\".txt;.doc;.docx\"}}}");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("msg", "error");
            return result;
        }
    }

    @ApiOperation(value = "获取文件类型树")
    @GetMapping(value = "/getFileTypeTree")
    public Object getAssetTypeTree() {
        return fileTypeService.getEnabledFileTypeList();
    }
}
