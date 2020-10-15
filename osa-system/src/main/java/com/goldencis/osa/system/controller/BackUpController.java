package com.goldencis.osa.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.LogType;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.aop.OsaSystemLog;
import com.goldencis.osa.system.entity.BackUp;
import com.goldencis.osa.system.service.IBackUpService;
import com.goldencis.osa.system.service.ISystemSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
@Api(tags = "数据库备份")
@RestController
@RequestMapping("/backUp")
public class BackUpController {

    private static final Log logger = LogFactory.getLog(BackUpController.class);

    @Autowired
    private ISystemSetService systemSetService;

    @Autowired
    private IBackUpService backUpService;

    /**
     * 获取备份列表信息
     */
    @ApiOperation("查询数据库备份列表信息")
    @GetMapping("/getBackUpList")
    public ResultMsg getBackUpList(BackUp backUpDO, HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> param = new HashMap<String, Object>();
            long count = backUpService.getBackUpCnt(backUpDO, param);
            List<BackUp> dataBaseList = backUpService.getBackUpList(backUpDO, param);

            IPage<Map<String, String>> page = new Page<>();
            page.setTotal(count);
            page.setRecords(formatBackUp(dataBaseList));
            return ResultMsg.page(page);
        } catch (Exception e) {
            logger.error("查询数据库备份列表信息失败", e);
            return ResultMsg.False("查询数据库备份列表信息失败");
        }
    }

    private List<Map<String, String>> formatBackUp(List<BackUp> backUpList) {
        List<Map<String, String>> list = new ArrayList<>();
        if (!ListUtils.isEmpty(backUpList)) {
            for (BackUp backUp : backUpList) {
                Map<String, String> map = new HashMap<>();
                map.put("id", backUp.getId());
                map.put("name", backUp.getName());
                map.put("mark", backUp.getMark());
                map.put("status", backUp.getStatus());
                map.put("createTime", backUp.getCreateTime().substring(0, 19));
                map.put("typeName", backUp.getTypeName());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 新增实时备份
     */
    @ApiOperation(value = "新增实时备份")
    @PostMapping(value = "/addActualBackUp")
    @OsaSystemLog(module = "新建实时数据库备份", template = "备份名称：%s", args = "2.name", type = LogType.SYSTEM_ADD)
    public ResultMsg addActualBackUp(HttpServletRequest request, HttpSession session, BackUp backUpDO) {

        try {
            if(StringUtils.isEmpty(backUpDO.getName())){
                return ResultMsg.False("备份名称为空");
            }
            if(backUpService.isExitBackUpName(backUpDO.getName())) {
                return ResultMsg.False("备份名称已存在");
            }
            if (! com.goldencis.osa.common.utils.StringUtils.isInLength(backUpDO.getName(),100)){
                return ResultMsg.False("实时备份名称最大长度为100");
            }
            if (! com.goldencis.osa.common.utils.StringUtils.isInLength(backUpDO.getMark(),200)){
                return ResultMsg.False("实时备份备注最大长度为200");
            }
            backUpService.addBackUp(backUpDO);
            return ResultMsg.ok();
        } catch (Exception e) {
            logger.error("新增实时数据库备份失败", e);
            return ResultMsg.False("新增实时数据库备份失败");
        }
    }

    /**
     * 设置自动备份
     */
    @ApiOperation(value = "设置自动备份")
    @PostMapping(value = "/addAutoBackUp")
    @OsaSystemLog(module = "编辑自动备份",  template = "", args = "",type = LogType.SYSTEM_UPDATE)
    public ResultMsg addAutoBackUp(HttpServletRequest request, HttpSession session,BackUp backUpDO) {
        try {
            backUpService.addAutoBackUp(backUpDO);
            return ResultMsg.ok();
        } catch (Exception e) {
            logger.error("设置自动数据库备份失败", e);
            return ResultMsg.False("设置自动数据库备份失败");
        }
    }

    /**
     * 查询自动备份配置信息
     */
    @ApiOperation("查询自动备份配置信息")
    @GetMapping("/getAutoBackUp")
    public ResultMsg getAutoBackUp() {
        try {
            Map<String, Object> data = backUpService.getAutoBackUp();
            return ResultMsg.ok(data);
        } catch (Exception e) {
            logger.error("查询自动备份配置信息失败", e);
            return ResultMsg.False("查询自动备份配置信息失败");
        }
    }

    /**
     *删除备份
     */
    @ApiOperation(value = "删除数据库备份")
    @PostMapping(value = "/deleteBackUp")
    @OsaSystemLog(module = "删除数据库备份", template = "备份名称：%ret", ret = "data", type = LogType.SYSTEM_DELETE)
    public ResultMsg deleteBackUp(HttpServletRequest request, String ids,String names,HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultCode", ConstantsDto.CONST_FALSE);
        try {
            if (StringUtils.isEmpty(ids)) {
                return ResultMsg.False("删除参数错误");
            }
            String deletes = backUpService.deleteBackUp(ids);
            return ResultMsg.ok(deletes);
        } catch (Exception e) {
            logger.error("删除数据库备份备份失败", e);
            return ResultMsg.False("删除数据库备份备份失败");
        }
    }

    /**
     * 导出备份
     * @param request
     * @return
     */
    @ApiOperation("导出数据库备份信息")
    @GetMapping("/exportBackup")
    public void exportBackup(HttpServletRequest request, HttpServletResponse response, HttpSession session,String ids) {
        InputStream fis = null;
        OutputStream toClient = null;
        FileInputStream fisa = null;
        File file = null;
        String result;
        try {
            result = backUpService.getBackupFilePath(ids);
            // path是指欲下载的文件的路径。
            file = new File(result);
            // 以流的形式下载文件。
            fisa = new FileInputStream(file);
            fis = new BufferedInputStream(fisa);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String( "备份.zip".getBytes("gb2312"), "ISO8859-1" ) );
            response.addHeader("Content-Length", "" + file.length());
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fisa != null) {
                try {
                    fisa.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (toClient != null) {
                try {
                    toClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(file.exists()){
                file.delete();
            }
        }
    }

    /**
     * 数据库维护
     */
    @ApiOperation(value = "数据库维护")
    @PostMapping(value = "/dbClean")
    @OsaSystemLog(module = "修改数据库维护",  template = "日志保存期：%s", args = "2",type = LogType.SYSTEM_UPDATE)
    public ResultMsg dbClean(HttpServletRequest request, HttpSession session,String day) {
        try {
            backUpService.databaseClean(day);
            return ResultMsg.ok();
        } catch (Exception e) {
            logger.error("设置数据库维护失败", e);
            return ResultMsg.False("设置数据库维护失败");
        }
    }

    /**
     * 获取数据库维护信息
     */
    @ApiOperation(value = "获取数据库维护")
    @GetMapping(value = "/getDbCleanSet")
    public ResultMsg getDbCleanSet(HttpServletRequest request, HttpSession session,String day) {
        try {
            Map<String, Object> dataBaseClean = systemSetService.getPlatform("DataBaseClean");
            String content = String.valueOf(dataBaseClean.get("content"));
            JSONObject json = JSONObject.fromObject(content);
            dataBaseClean = (Map)json;
            if (json.get("status").equals("0")) {
                dataBaseClean.put("day", "");
            }
            return ResultMsg.ok(dataBaseClean);
        } catch (Exception e) {
            logger.error("获取数据库维护失败", e);
            return ResultMsg.False("获取数据库维护失败");
        }
    }
}
