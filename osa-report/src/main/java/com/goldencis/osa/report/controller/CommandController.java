package com.goldencis.osa.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.domain.CommandItem;
import com.goldencis.osa.asset.excel.impl.CommandReportExcelHelper;
import com.goldencis.osa.asset.excel.provide.impl.FileProvider;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.report.service.ICommandReportService;
import com.goldencis.osa.session.entity.CommandEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = "报表")
@RestController
@RequestMapping("/report")
public class CommandController {

    private Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private Config config;

    @Autowired
    private CommandReportExcelHelper commandReportExcelHelper;

    @Autowired
    private ICommandReportService commandReportService;

    @Autowired
    private FileProvider fileProvider;

    @ApiOperation("查询违规命令图表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String")
    })
    @GetMapping("/getCommandReportChart")
    public ResultMsg getCommandReportChart(@RequestParam Map<String, String> params){
        try {
            String dateTime = params.get("dateTime");
            //参数验证
            if (dateTime.indexOf("~") == -1){
                return ResultMsg.False("时间参数错误");
            }
            Map<String, Object> data = commandReportService.getCommandReportChart(params);
            return ResultMsg.ok(data);
        } catch (Exception e) {
            logger.error("查询违规命令图表信息失败", e);
            return ResultMsg.False("查询违规命令图表信息失败");
        }
    }

    @ApiOperation("查询违规命令列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "类型", dataType = "String"),
            @ApiImplicitParam(name = "length", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "searchStr", value = "搜索内容", dataType = "String")
    })
    @GetMapping("getCommandReportList")
    public ResultMsg getCommandReportList(@RequestParam Map<String, String> params) {

        try {
            String dateTime = params.get("dateTime");
            //参数验证
            if (dateTime.indexOf("~") == -1){
                return ResultMsg.False("时间参数错误");
            }
            IPage<CommandEntity> page = commandReportService.getCommandReportList(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            logger.error("查询违规命令列表信息失败", e);
            return ResultMsg.False("查询违规命令列表信息失败");
        }
    }

    @ApiOperation("违规命令导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "类型", dataType = "String"),
            @ApiImplicitParam(name = "length", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "searchStr", value = "搜索内容", dataType = "String")
    })
    @GetMapping("commandReportExport")
    public void commandReportExport(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {

        try {
            String dateTime = params.get("dateTime");
            //参数验证
            if (dateTime.indexOf("~") == -1){
                logger.info("时间参数错误");
                return;
            }
            //导出参数处理
            String pageStart = params.get("pageStart");
            String pageEnd = params.get("pageEnd");
            if (!StringUtils.isEmpty(pageStart) && !StringUtils.isEmpty(pageEnd)){
                int end = Integer.parseInt(pageEnd);
                int start = Integer.parseInt(pageStart);
                int length = Integer.parseInt(params.get("length"));
                params.put("length", String.valueOf((end - start + 1) * length));
                params.put("start", String.valueOf((start - 1) * length));
            }

            IPage<CommandEntity> page = commandReportService.getCommandReportList(params);
            List<CommandEntity> list = page.getRecords();

            List<CommandItem> collect = list.stream().map(this::parse2CommandItem).filter(Objects::nonNull).collect(Collectors.toList());

            HSSFWorkbook workbook = new HSSFWorkbook();
            IExport.Builder<HSSFWorkbook, CommandItem> commandBuilder = new IExport.Builder<>();
            commandBuilder.setList(collect);
            commandBuilder.setT(workbook);
            commandReportExcelHelper.export(commandBuilder);
            FileProvider.Builder<HSSFWorkbook> builder = new FileProvider.Builder<>();
            builder.setPath(config.getExportPath());
            builder.setFileName("违规命令信息表" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            builder.setSuffix(".xls");
            builder.setInput(workbook);
            File file = fileProvider.provide(builder);
            FileDownLoad downLoad = new FileDownLoad();
            downLoad.download(response, request, file.getPath());
        } catch (Exception e) {
            logger.error("导出违规命令列表信息失败", e);
        }
    }

    private CommandItem parse2CommandItem(CommandEntity command) {
        CommandItem commandItem = new CommandItem();
        commandItem.setInput(command.getInput());
        String status = "";
        switch (command.getStatus()) {
            case "3":
                status = "禁止";
                break;
            case "4":
                status = "阻断";
                break;
            case "5":
                status = "无效";
                break;
            default:
                status = "审批";
                break;
        }
        commandItem.setStatus(status);
        commandItem.setTimestamp(command.getTimestamp());
        commandItem.setAsset(command.getAsset());
        commandItem.setTypename(command.getTypename());
        commandItem.setIp(command.getIp());
        commandItem.setAccount(command.getAccount());
        commandItem.setAssetId(command.getAssetId());
        commandItem.setUsername(command.getUsername());
        commandItem.setTruename(command.getTruename());
        commandItem.setGroupname(command.getGroupname());
        return commandItem;
    }
}
