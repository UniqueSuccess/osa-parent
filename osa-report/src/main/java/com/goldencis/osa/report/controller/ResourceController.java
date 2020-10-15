package com.goldencis.osa.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.asset.entity.ResourceEntity;
import com.goldencis.osa.asset.excel.IExport;
import com.goldencis.osa.asset.excel.impl.AssetReportExcelHelper;
import com.goldencis.osa.asset.excel.provide.impl.FileProvider;
import com.goldencis.osa.common.config.Config;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.FileDownLoad;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.report.service.IResourceReportService;
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

@Api(tags = "报表")
@RestController
@RequestMapping("/report")
public class ResourceController {

    private Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private Config config;

    @Autowired
    private IResourceReportService resourceReportService;

    @Autowired
    private AssetReportExcelHelper assetReportExcelHelper;

    @Autowired
    private FileProvider fileProvider;

    @ApiOperation("查询授权资源图表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String")
    })
    @GetMapping("/getResourceReportChart")
    public ResultMsg getResourceReportChart(@RequestParam Map<String, String> params){
        try{
            String dateTime = params.get("dateTime");
            //参数验证
            if (StringUtils.isEmpty(dateTime) || dateTime.indexOf("~") == -1){
                return ResultMsg.False("时间参数错误");
            }
            String[] splitTime = params.get("dateTime").toString().split("~");
            params.put("startTime", splitTime[0] + ":00");
            params.put("endTime", splitTime[1] + ":00");

            List<Map<String, Object>> data = resourceReportService.getResourceReportChart(params);
            return ResultMsg.ok(data);
        } catch (Exception e) {
            logger.error("查询授权资源图表信息失败", e);
            return ResultMsg.False("查询授权资源图表信息失败");
        }
    }


    @ApiOperation("查询授权资源列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "assetType", value = "设备类型", dataType = "String"),
            @ApiImplicitParam(name = "length", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "searchStr", value = "搜查内容", dataType = "String")
    })
    @GetMapping("getResourceReportList")
    public ResultMsg getResourceReportList(@RequestParam Map<String, String> params) {

        try {
            String dateTime = params.get("dateTime");
            String orderType = params.get("orderType");
            String orderColumn = params.get("orderColumn");

            //参数验证
            if (dateTime.indexOf("~") == -1){
                return ResultMsg.False("时间参数错误");
            }
            String[] splitTime = dateTime.split("~");
            params.put("startTime", splitTime[0] + ":00");
            params.put("endTime", splitTime[1] + ":00");
            params.put("orderType", StringUtils.isEmpty(orderType) ? "desc" : orderType);
            params.put("orderColumn", StringUtils.isEmpty(orderColumn) ? "create_time" : orderColumn);

            IPage<ResourceEntity> page = resourceReportService.getResourceReportList(params);
            return ResultMsg.page(page);
        } catch (Exception e) {
            logger.error("查询授权资源列表信息失败", e);
            return ResultMsg.False("查询授权资源列表信息失败");
        }
    }

    @ApiOperation("导出授权设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "时间区间", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "orderColumn", value = "排序参数", dataType = "String"),
            @ApiImplicitParam(name = "start", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "assetType", value = "设备类型", dataType = "String"),
            @ApiImplicitParam(name = "length", value = "取值参数", dataType = "String"),
            @ApiImplicitParam(name = "searchStr", value = "搜查内容", dataType = "String")
    })
    @GetMapping("assetReportExport")
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

            IPage<ResourceEntity> page = resourceReportService.getResourceReportList(params);
            List<ResourceEntity> list = page.getRecords();

            HSSFWorkbook workbook = new HSSFWorkbook();
            IExport.Builder<HSSFWorkbook, ResourceEntity> assetBuilder = new IExport.Builder<>();
            assetBuilder.setList(list);
            assetBuilder.setT(workbook);
            assetReportExcelHelper.export(assetBuilder);
            FileProvider.Builder<HSSFWorkbook> builder = new FileProvider.Builder<>();
            builder.setPath(config.getExportPath());
            builder.setFileName("授权设备信息表" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            builder.setSuffix(".xls");
            builder.setInput(workbook);
            File file = fileProvider.provide(builder);
            FileDownLoad downLoad = new FileDownLoad();
            downLoad.download(response, request, file.getPath());
        } catch (Exception e) {
            logger.error("导出授权设备信息失败", e);
        }
    }
}
