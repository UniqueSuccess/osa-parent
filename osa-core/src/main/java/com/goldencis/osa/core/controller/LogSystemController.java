package com.goldencis.osa.core.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.core.entity.HomeCounts;
import com.goldencis.osa.core.entity.HomeLogSystem;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.LogSystemType;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统日志（操作、授权、审批）  前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
@Api(tags = "系统日志（操作、授权、审批、策略、运维）")
@RestController
@RequestMapping("/logSystem")
public class LogSystemController {

    @Autowired
    ILogSystemService logSystemService;

    @Autowired
    IUserService userService;

    @ApiOperation(value = "获取操作分页列表",notes = "分页获取 操作、授权、审批 日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataTypeClass =  Integer.class ),
            @ApiImplicitParam(name = "length", value = "每页条数", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "searchStr", value = "查询", dataTypeClass = String.class),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataTypeClass = String.class),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataTypeClass = String.class),
            @ApiImplicitParam(name = "logBigType", value = "大类型筛选条件（1操作、2授权、3审批）", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "logSmallType", value = "小类型筛选条件（多个;分割）", dataTypeClass = String.class)
    })
    @GetMapping(value = "/getLogSystemsInPage")
    public ResultMsg getLogSystemsInPage(@RequestParam Map<String, String> params) {
        try {
            //分页查询
            IPage<LogSystem> page = logSystemService.getLogSystemsInPage(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "新增系统日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "登录ip地址", paramType = "query", dataTypeClass =  String.class ),
            @ApiImplicitParam(name = "userId", value = "用户guid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userUsername", value = "用户名", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userName", value = "用户姓名",  paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "logType", value = "日志类型（枚举LogType）", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "logPage", value = "对应页面",  paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "logOperateParam", value = "请求参数", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "logDesc", value = "描述", paramType = "query", dataTypeClass = String.class)
    })
    @PostMapping(value = "/logSystem")
    public ResultMsg save(LogSystem logSystem) {
        try {
            //用户guid不得能为空
            if (StringUtils.isEmpty(logSystem.getUserId())) {
                return ResultMsg.error("用户guid不能为空");
            }
            //用户名不得能为空
            if (StringUtils.isEmpty(logSystem.getUserUsername())) {
                return ResultMsg.error("用户名不能为空");
            }
            if (Objects.isNull( userService.findUserByGuid(logSystem.getUserId()))){
                return ResultMsg.error("用户不存在");
            }
            //设置时间
            logSystem.setTime(LocalDateTime.now());
            logSystemService.save(logSystem);
            return ResultMsg.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }


    @ApiOperation(value = "获取日志子类型",notes = "获取日志类型的子类型")
    @ApiImplicitParam(name = "logType", value = "日志类型（1 系统操作； 2 系统授权； 3 系统审批; 4 策略； 5运维）", paramType = "query", dataTypeClass = Integer.class)
    @GetMapping(value = "/getLogSmallType")
    public ResultMsg getLogSmallType(Integer logType){
        try{
            if (Objects.isNull(logType)){
                throw new IllegalArgumentException("日志类型不能为空");
            }
            List<LogSystemType> logSmallTypes = new ArrayList<>();
            switch (logType){
                case ConstantsDto.LOG_SYSTEM_OPERATE:
                    logSmallTypes = LogType.getAllSystemOperateLogType().stream().map(logType1 -> new LogSystemType(logType1.getName(), logType1.getValue())).collect(Collectors.toList());
                    break;
                case ConstantsDto.LOG_SYSTEM_GRANTED:
                    logSmallTypes = LogType.getAllGrantedLogType().stream().map(logType1 -> new LogSystemType(logType1.getName(), logType1.getValue())).collect(Collectors.toList());
                    break;
                case ConstantsDto.LOG_SYSTEM_APPROVAL:
                    logSmallTypes = LogType.getAllApprovalLogType().stream().map(logType1 -> new LogSystemType(logType1.getName(), logType1.getValue())).collect(Collectors.toList());
                    break;
                case ConstantsDto.LOG_OPERATION_STRATEGY:
                    logSmallTypes = LogType.getAllStrategyLogType().stream().map(logType1 -> new LogSystemType(logType1.getName(), logType1.getValue())).collect(Collectors.toList());
                    break;
                case ConstantsDto.LOG_OPERATION_OPERATE:
                    logSmallTypes = LogType.getAllOperationLogType().stream().map(logType1 -> new LogSystemType(logType1.getName(), logType1.getValue())).collect(Collectors.toList());
                    break;
            }
            return ResultMsg.ok(logSmallTypes);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "首页接口相关",notes = "首页-活跃账号Top5、资源运维Top5、运维日志Top20")
    @GetMapping(value = "/getLogSystemHome")
    public ResultMsg getLogSystemHome(){
        try{
            HomeLogSystem logSystemHome = new HomeLogSystem();
            //活跃账号Top5
            logSystemHome.setUserTop(logSystemService.getLoginTimesTop5());
            //资源运维Top5
            logSystemHome.setAssetTop(logSystemService.getAssetOperationsTop5());
            //运维日志Top20
            logSystemHome.setLogOperation(logSystemService.getLoginOperationTop());
            return ResultMsg.ok(logSystemHome);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

    @ApiOperation(value = "首页接口相关",notes = "首页-数量统计")
    @GetMapping(value = "/getHomeCounts")
    public ResultMsg getHomeCounts(HttpSession session){
        try{
            HomeCounts homeCounts = logSystemService.getHomeCounts();
            //获取用户在线数目
            ServletContext application = session.getServletContext();
            List onlineUserList = (List) application.getAttribute("onlineUserList");
            List newList = (List) onlineUserList.stream().distinct().collect(Collectors.toList());
            if (ListUtils.isEmpty(onlineUserList)) {
                homeCounts.setUserOnlineNums(0);
            } else {
                homeCounts.setUserOnlineNums(newList.size());
            }
            return ResultMsg.ok(homeCounts);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error(e.getMessage());
        }
    }

}