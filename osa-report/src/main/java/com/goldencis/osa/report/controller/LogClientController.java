package com.goldencis.osa.report.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.domain.ClientFileOpsItem;
import com.goldencis.osa.asset.excel.impl.ClientFileOpsExcelHelper;
import com.goldencis.osa.asset.excel.impl.UserReportExcelHelper;
import com.goldencis.osa.asset.excel.provide.impl.FileProvider;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.report.constants.FileMonType;
import com.goldencis.osa.report.entity.LogClient;
import com.goldencis.osa.report.entity.LogClientFileType;
import com.goldencis.osa.report.service.ILogClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

/**
 * <p>
 *  报表--文件操作 前端控制器
 * </p>
 *
 * @author wangtt
 * @since 2019-01-28
 */
@Api(tags = "报表")
@RestController
@RequestMapping("/report")
public class LogClientController {
    private final Logger logger = LoggerFactory.getLogger(UserReportExcelHelper.class);
    @Autowired
    ILogClientService logClientService;

    @Autowired
    private ClientFileOpsExcelHelper clientFileOpsExcelHelper;

    @Autowired
    private Config config;

    @Autowired
    private FileProvider fileProvider;

    @ApiOperation(value = "分页获取文件操作列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "起始条数", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "每页条数", dataType = "Integer"),
            @ApiImplicitParam(name = "searchStr", value = "名称查询", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序字段", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序类型", dataType = "String"),
            @ApiImplicitParam(name = "fileOpsType", value = "文件操作类型筛选条件（多个英文;分割）", dataType = "String")
    })
    @GetMapping(value = "/getFileOpsReportInPage")
    public ResultMsg getFileOpsReportInPage(@RequestParam Map<String, String> params) {
        try{
            //获取分页数据
            IPage<LogClient> page =  logClientService.getFileOpsInPage( params);
            return ResultMsg.page(page);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation(value = "获取文件操作图标数据")
    @GetMapping(value = "/getFileOpsReportChart")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String")
    })
    public ResultMsg getFileOpsReportChart(@RequestParam Map<String, String> params) {
        try{
            //获取分页数据
            List<LogClientFileType>  logClientFileTypes =  logClientService.getLogClientFileType(params);
            return ResultMsg.ok(logClientFileTypes);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.error( e.getMessage());
        }
    }

    @ApiOperation("导出文件操作信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "assetType", value = "设备类型", dataType = "String"),
            @ApiImplicitParam(name = "length", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "searchStr", value = "搜查内容", dataType = "String")
    })
    @GetMapping("/fileOpsReportExport")
    public void export(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {

        try {
            String dateTime = params.get("dateTime");
            String orderType = params.get("orderType");
            String orderColumn = params.get("orderColumn");

            //参数验证
            if (dateTime.indexOf("~") == -1){
                logger.info("时间参数错误");
                return;
            }
            String[] splitTime = dateTime.split("~");
            params.put("startTime", splitTime[0] + ":00");
            params.put("endTime", splitTime[1] + ":00");
            params.put("orderType", StringUtils.isEmpty(orderType) ? "desc" : orderType);
            params.put("orderColumn", StringUtils.isEmpty(orderColumn) ? "create_time" : orderColumn);

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

             //获取分页数据
            IPage<LogClient> page =  logClientService.getFileOpsInPage( params);
            List<LogClient> list = page.getRecords();
            List<ClientFileOpsItem> collect = list.stream().map(this::parse2FileOpsItem).filter(Objects::nonNull).collect(Collectors.toList());

            HSSFWorkbook workbook = new HSSFWorkbook();
            IExport.Builder<HSSFWorkbook, ClientFileOpsItem> assetBuilder = new IExport.Builder<>();
            assetBuilder.setList(collect);
            assetBuilder.setT(workbook);
            clientFileOpsExcelHelper.export(assetBuilder);
            FileProvider.Builder<HSSFWorkbook> builder = new FileProvider.Builder<>();
            builder.setPath(config.getExportPath());
            builder.setFileName("行为审计--文件操作表" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            builder.setSuffix(".xls");
            builder.setInput(workbook);
            File file = fileProvider.provide(builder);
            FileDownLoad downLoad = new FileDownLoad();
            downLoad.download(response, request, file.getPath());
        } catch (Exception e) {
            logger.error("导出行为审计--文件操作失败", e);
        }
    }

    private ClientFileOpsItem parse2FileOpsItem(LogClient logClient) {
        ClientFileOpsItem clientFileOpsItem = new ClientFileOpsItem();
        if (logClient.getOptype() != null){
            logClient.setOptypeName(FileMonType.getNameByValue(logClient.getOptype()));
        }
        BeanUtils.copyProperties(logClient, clientFileOpsItem);
        clientFileOpsItem.setTime(DateUtil.getStringByLocalDateTime(logClient.getTime()));
        return clientFileOpsItem;
    }

}
