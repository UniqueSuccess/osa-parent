package com.goldencis.osa.session.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.session.entity.TerminalCommand;
import com.goldencis.osa.session.service.ITerminalCommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author limingchao
 * @since 2018-11-09
 */
@Api(tags = "命令管理")
@RestController
@RequestMapping("/command")
public class TerminalCommandController {

    @Autowired
    private ITerminalCommandService commandService;

    @ApiOperation(value = "在审计详情中，获取命令列表的分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "sessionId", value = "会话id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "searchStr", value = "输入命令", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", paramType = "query", dataType = "String")
    })
    @GetMapping(value = "/getCommandListInPage")
    public ResultMsg getCommandListInPage(@RequestParam Map<String, String> params) {
        try {
            if (!params.containsKey("sessionId") || StringUtils.isEmpty(params.get("sessionId"))) {
                return ResultMsg.False("未指定正确的sessionId!");
            }

            //获取命令的分页列表
            IPage<TerminalCommand> page = commandService.getCommandListInPage(params);

            return ResultMsg.page(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMsg.build(ConstantsDto.RESPONSE_ERROR, e.getMessage());
        }
    }
}
