package com.goldencis.osa.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.common.utils.ZipFileUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.core.entity.QuartzJob;
import com.goldencis.osa.core.mapper.LogSystemMapper;
import com.goldencis.osa.core.service.impl.TaskService;
import com.goldencis.osa.core.utils.BackupUtil;
import com.goldencis.osa.system.entity.BackUp;
import com.goldencis.osa.system.entity.SystemSet;
import com.goldencis.osa.system.mapper.BackUpMapper;
import com.goldencis.osa.system.mapper.SystemSetMapper;
import com.goldencis.osa.system.service.IBackUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shigd
 * @since 2018-12-27
 */
@Service
public class BackUpServiceImpl extends ServiceImpl<BackUpMapper, BackUp> implements IBackUpService {
    private final static String IS_BACKUP = "1";
    private final static String JOB_NAME = "BackUpJob";
    private final static String JOB_GROUP = "DEFAULT";
    @Autowired
    private BackUpMapper backUpDOMapper;

    @Autowired
    private SystemSetMapper systemDOMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LogSystemMapper logSystemMapper;

    @Override
    public Long getBackUpCnt(BackUp backUpDO, Map<String, Object> param) {
        // TODO Auto-generated method stub
        return backUpDOMapper.getBackUpCnt(backUpDO, param);
    }

    @Override
    public List<BackUp> getBackUpList(BackUp backUpDO, Map<String, Object> param) {
        // TODO Auto-generated method stub
        return backUpDOMapper.getBackUpList(backUpDO, param);
    }

    @Override
    @Transactional
    public void addBackUp(BackUp backUpDO) {
        // TODO Auto-generated method stub
        try {
            String filePath = PathConfig.HOMEPATH + "/backup/" + System.currentTimeMillis() + ".sql";
            BackupUtil util = new BackupUtil();
            util.backup(filePath);
            backUpDO.setStatus(IS_BACKUP);
            backUpDO.setFilePath(filePath);
            backUpDOMapper.addBackUp(backUpDO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExitBackUpName(String name) {
        // TODO Auto-generated method stub
        Long cnt = backUpDOMapper.isExitBackUpName(name);
        if (cnt > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void addAutoBackUp(BackUp backUpDO) {
        // TODO Auto-generated method stub
        try {
            SystemSet systemSetDO = new SystemSet();
            systemSetDO.setType("AutoBackUp");
            systemSetDO.setCode("AutoBackUp");
            Map<String, String> contentMaps = new HashMap<String, String>();
            contentMaps.put("status", backUpDO.getStatus());
            //拼装cron
            String cron = "";
            //未启用
            if ("0".equals(backUpDO.getStatus())) {
                cron = ConstantsDto.NEVER_CRONF;
            } else {
                contentMaps.put("cycle", backUpDO.getCycle());
                contentMaps.put("day", backUpDO.getDay());
                contentMaps.put("time", backUpDO.getTime());
                String[] time = backUpDO.getTime().split(":");
                String day = backUpDO.getDay();
                if ("month".equals(backUpDO.getCycle())) {
                    cron = "0 " + time[1] + " " + time[0] + " " + day + " * ?";
                }
                if ("week".equals(backUpDO.getCycle())) {
                    if ("1".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * MON";
                    }
                    if ("2".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * TUE";
                    }
                    if ("3".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * WED";
                    }
                    if ("4".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * THU";
                    }
                    if ("5".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * FRI";
                    }
                    if ("6".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * SAT";
                    }
                    if ("7".equals(day)) {
                        cron = "0 " + time[1] + " " + time[0] + " ? * SUN";
                    }
                }
            }
            QuartzJob bean = taskService.getJob(JOB_NAME, JOB_GROUP);
            bean.setCronEx(cron);
            taskService.updateCronExpression(bean,1);
            String content = JSON.toJSONString(contentMaps, SerializerFeature.PrettyFormat);
            systemSetDO.setContent(content);
            systemDOMapper.deleteSystemSet(systemSetDO.getCode());
            systemDOMapper.addSystemSet(systemSetDO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Map<String, Object> getAutoBackUp() {
        // TODO Auto-generated method stub
        Map<String, Object> data = systemDOMapper.getPlatform("AutoBackUp");
        String content = String.valueOf(data.get("content"));
        JSONObject json = JSONObject.fromObject(content);
        data = (Map) json;
        return data;
    }

    @Override
    @Transactional
    public String deleteBackUp(String ids) {
        // TODO Auto-generated method stub
        String deletes = "";
        try {
            List<String> idList = Arrays.asList(ids.split(";"));
            File file = null;
            for (String id : idList) {
                Map<String, Object> back = backUpDOMapper.getBackUpById(id);
                String name = String.valueOf(back.get("name"));
                String filePath = String.valueOf(back.get("filePath"));
                file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
                backUpDOMapper.deleteBackUp(id);
                deletes += name + ";";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deletes;
    }

    @Override
    @Transactional
    public void backUpExec() {
        List<Map<String, String>> list = backUpDOMapper.getBackUpExec();
        if (!ListUtils.isEmpty(list)) {
            for (Map<String, String> back : list) {
                try {
                    String filePath = back.get("filePath");
                    //备份
                    BackupUtil instance = new BackupUtil();
                    instance.backup(filePath);
                    //修改状态为1
                    back.put("status", "1");
                    backUpDOMapper.updateBackupStatus(back);
                } catch (Exception e) {
                    //修改状态为0
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public String getBackupFilePath(String ids) {
        // TODO Auto-generated method stub
        String filePath = PathConfig.HOMEPATH + "/backup/" + System.currentTimeMillis() + ".zip";
        try {
            List<String> idList = Arrays.asList(ids.split(";"));
            List<Map<String, Object>> backupList = new ArrayList<Map<String, Object>>();
            for (String id : idList) {
                backupList.add(backUpDOMapper.getBackUpById(id));
            }
            ZipFileUtils.scZip(backupList, filePath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return filePath;
    }

    @Override
    public void databaseClean(String day) {
        try {
            SystemSet systemSetDO = new SystemSet();
            systemSetDO.setType("DataBaseClean");
            systemSetDO.setCode("DataBaseClean");
            Map<String, String> contentMaps = new HashMap<String, String>();
            if (StringUtils.isEmpty(day)){
                contentMaps.put("status", "0");
            } else {
                contentMaps.put("status", "1");
                contentMaps.put("day", day);
            }
            String content = JSON.toJSONString(contentMaps, SerializerFeature.PrettyFormat);
            systemSetDO.setContent(content);
            systemDOMapper.deleteSystemSet(systemSetDO.getCode());
            systemDOMapper.addSystemSet(systemSetDO);

            //触发日志清理
            if (!StringUtils.isEmpty(day)) {
                int time = Integer.parseInt("-" + day);
                String formatDate = DateUtil.getFormatDate(DateUtil.FMT_DAY + " 00:00:00");
                String date = DateUtil.getDateAddDay(time, formatDate, DateUtil.FMT_DATE);
                logSystemMapper.logClean(date);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
