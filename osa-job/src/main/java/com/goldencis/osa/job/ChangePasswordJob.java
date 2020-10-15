package com.goldencis.osa.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetAccount;
import com.goldencis.osa.asset.resource.domain.Linux;
import com.goldencis.osa.asset.service.IAssetAccountService;
import com.goldencis.osa.asset.service.IAssetService;
import com.goldencis.osa.asset.service.IAssetTypeService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.constants.PathConfig;
import com.goldencis.osa.common.entity.MailConfig;
import com.goldencis.osa.common.entity.MailInfo;
import com.goldencis.osa.common.export.ExcelExporter;
import com.goldencis.osa.common.utils.FtpUtil;
import com.goldencis.osa.common.utils.MailManager;
import com.goldencis.osa.common.utils.SpringUtil;
import com.goldencis.osa.core.entity.QuartzJob;
import com.goldencis.osa.core.service.impl.TaskService;
import com.goldencis.osa.job.domain.CPResult;
import com.goldencis.osa.system.service.ISystemSetService;
import com.goldencis.osa.task.domain.Type;
import com.goldencis.osa.task.entity.TaskAsset;
import com.goldencis.osa.task.entity.TaskAssetAsset;
import com.goldencis.osa.task.entity.TaskAssetEmail;
import com.goldencis.osa.task.service.ITaskAssetAssetService;
import com.goldencis.osa.task.service.ITaskAssetEmailService;
import com.goldencis.osa.task.service.ITaskAssetService;
import org.apache.commons.mail.EmailAttachment;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 自动改密计划
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-22 10:34
 **/
public class ChangePasswordJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * linux类型设备的标示
     */
    private static final Integer LINUX_ID = new Linux().typeId();
    /**
     * 修改密码成功的标记
     */
    private static final String FLAG_SUCCESS = "all authentication tokens updated successfully";
    /**
     * 改密脚本名称
     */
    private static final String FILE_NAME_SH = "gdmkpasswd.sh";

    private static final char[] NUMBER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] LOWERCASE = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] CAPITAL = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] SPECIAL = new char[]{'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};

    private IAssetService assetService;
    private TaskService taskService;
    private IAssetAccountService assetAccountService;
    private IAssetTypeService assetTypeService;
    private ITaskAssetEmailService taskAssetEmailService;

    public void changePassword() {
        ITaskAssetService taskAssetService = SpringUtil.getBean(ITaskAssetService.class);
        ITaskAssetAssetService taskAssetAssetService = SpringUtil.getBean(ITaskAssetAssetService.class);
        taskAssetEmailService = SpringUtil.getBean(ITaskAssetEmailService.class);
        assetService = SpringUtil.getBean(IAssetService.class);
        assetAccountService = SpringUtil.getBean(IAssetAccountService.class);
        assetTypeService = SpringUtil.getBean(IAssetTypeService.class);
        if (Objects.isNull(taskAssetService)) {
            logger.warn("taskAssetService is null...");
            return;
        }
        TaskAsset task = taskAssetService.getById(ITaskAssetService.TASK_ID);
        Type type = Type.valueOf(task.getType());
        List<Asset> assetList = taskAssetAssetService.list(new QueryWrapper<TaskAssetAsset>().eq("task_id", ITaskAssetService.TASK_ID)).stream()
                .filter(item -> Objects.nonNull(item) && Objects.nonNull(item.getAssetId()))
                .map(item -> assetService.getById(item.getAssetId()))
                .filter(Objects::nonNull)
                .filter(this::isLinux)
                .collect(Collectors.toList());
        List<CPResult> list = new ArrayList<>();
        Integer rule = task.getRule();
        Integer length = task.getLength();
        switch (type) {
            case FIXED:
                if (StringUtils.isEmpty(task.getPassword())) {
                    logger.warn("用户指定了固定密码,但是没有给出具体的值!");
                    return;
                }
                list.addAll(internalChangePassword(assetList, task.getPassword()));
                break;
            case RANDOM:
                list.addAll(internalChangePassword(assetList, rule, length));
                break;
            case SAME:
                String password = generatePassword(rule, length);
                list.addAll(internalChangePassword(assetList, password));
                break;
            default:
                logger.warn("错误的密码类型 {}", task.getType());
                break;
        }
        if (CollectionUtils.isEmpty(list)) {
            logger.debug("密码修改完成,集合为空...");
            return;
        }
        ExcelExporter excelExporter = new ExcelExporter.Builder<CPResult>()
                .setOutputPath(PathConfig.HOMEPATH)
                .setClazz(CPResult.class)
                .setData(list)
                .setOutputName("")
                .build();
        File file = excelExporter.export();
            // 保存到ftp
        if (Objects.nonNull(task.getFtp()) && task.getFtp()) {
            String[] addr = task.getFtpAddr().split(":");
            try {
                FtpUtil.uploadFile(addr[0], Integer.parseInt(addr[1]), task.getFtpAccount(), task.getFtpPwd(), task.getFtpDir(), "", file.getName(), new FileInputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 发送邮件
        if (Objects.nonNull(task.getEmail()) && task.getEmail()) {
            sendEmail(file);
        }

        // 删除文件
        boolean delete = file.delete();
        logger.debug("文件删除完成 {}, 路径: {}", delete, file.getAbsolutePath());

        // 如果计划被设定为只执行一次,执行完成后,更改cron表达式
        if (Objects.nonNull(task.getOnce()) && task.getOnce()) {
            QuartzJob job = taskService.getJob(ITaskAssetService.JOB_NAME, ITaskAssetService.GROUP_NAME);
            job.setCronEx(ConstantsDto.NEVER_CRONF);
            try {
                taskService.updateCronExpression(job, 1);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送邮件
     * @param file
     */
    private void sendEmail(File file) {
        List<TaskAssetEmail> list = taskAssetEmailService.list(new QueryWrapper<TaskAssetEmail>().eq("task_id", ITaskAssetService.TASK_ID));
        List<String> to = list.stream()
                .filter(Objects::nonNull)
                .map(TaskAssetEmail::getEmail)
                .filter(item -> !StringUtils.isEmpty(item))
                .collect(Collectors.toList());
        MailManager mailManager = SpringUtil.getBean(MailManager.class);
        ISystemSetService systemSetService = SpringUtil.getBean(ISystemSetService.class);
        try {
            MailConfig config = systemSetService.getMailConfig();
            MailInfo info = new MailInfo();
            info.setContent("自动改密完成,附件是改密后的账号密码.");
            info.setSubject("自动改密计划");
            info.setToAddress(to);
            EmailAttachment attachment = new EmailAttachment();
            attachment.setURL(file.toURI().toURL());
            attachment.setName(file.getName());
            info.addAttachment(attachment);
            logger.debug("mailInfo: {}, mailConfig: {}", info, config);
            mailManager.send(info, config);
        } catch (Exception e) {
            logger.error("发送邮件失败", e);
        }
    }

    private List<CPResult> internalChangePassword(List<Asset> assetList, String password) {
        List<CPResult> list = new ArrayList<>();
        for (Asset asset : assetList) {
            List<AssetAccount> accountList = assetAccountService.list(new QueryWrapper<AssetAccount>().eq("asset_id", asset.getId()));
            for (AssetAccount account : accountList) {
                if (execute(asset.getIp(), asset.getAccount(), asset.getPassword(), account.getUsername(), password)) {
                    // 添加到执行结果中
                    list.add(new CPResult(asset, account, password));
                    // 更新数据库记录
                    account.setPassword(password);
                    assetAccountService.updateById(account);
                }
            }
        }
        return list;
    }

    private List<CPResult> internalChangePassword(List<Asset> assetList, int rule, int length) {
        List<CPResult> list = new ArrayList<>();
        for (Asset asset : assetList) {
            List<AssetAccount> accountList = assetAccountService.list(new QueryWrapper<AssetAccount>().eq("asset_id", asset.getId()));
            for (AssetAccount account : accountList) {
                String password = generatePassword(rule, length);
                if (execute(asset.getIp(), asset.getAccount(), asset.getPassword(), account.getUsername(), password)) {
                    // 添加到执行结果中
                    list.add(new CPResult(asset, account, password));
                    // 更新数据库记录
                    account.setPassword(password);
                    assetAccountService.updateById(account);
                }
            }
        }
        return list;
    }

    /**
     * 执行修改密码的脚本
     *
     * @param ip            目标设备ip
     * @param adminAccount  管理员账号
     * @param adminPassword 管理员密码
     * @param account       待修改账号
     * @param password      新密码
     * @return
     */
    private boolean execute(String ip, String adminAccount, String adminPassword, String account, String password) {
        String dir = PathConfig.BIN_PATH + File.separator + FILE_NAME_SH;
        logger.debug("dir {} ip {} adminAccount {} adminPassword {} account {} password {}", dir, ip, adminAccount, adminPassword, account, password);
        ProcessBuilder processBuilder = new ProcessBuilder(dir, ip, "22", adminAccount, adminPassword, account, password);
        try {
            Process process = processBuilder.start();
            String s = execRuntime(process);
            logger.debug("process: {}", s);
            return s.contains(FLAG_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取脚本返回值
     */
    private String execRuntime(Process process) {
        if (process != null) {
            try (BufferedReader brStat = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                StringBuilder fileContent = new StringBuilder();
                while ((line = brStat.readLine()) != null) {
                    logger.debug("line: {}", line);
                    fileContent.append(line);
                }
                return fileContent.toString();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
            process.destroy();
        } else {
            logger.debug("process == null");
        }
        return "";
    }

    private boolean isLinux(Asset asset) {
        return LINUX_ID.equals(assetTypeService.getMostSuperiorAssetTypeById(asset.getType()).getId());
    }

    private String generatePassword(Integer rule, Integer length) {
        Objects.requireNonNull(rule, "密码规则不能为空");
        Objects.requireNonNull(length, "密码长度不能为空");
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (i < length) {
            if (TaskAsset.lowercaseFlag(rule)) {
                sb.append(LOWERCASE[randomIndex(LOWERCASE.length)]);
                i++;
            }
            if (TaskAsset.capitalFlag(rule)) {
                sb.append(CAPITAL[randomIndex(CAPITAL.length)]);
                i++;
            }
            if (TaskAsset.numberFlag(rule)) {
                sb.append(NUMBER[randomIndex(NUMBER.length)]);
                i++;
            }
            if (TaskAsset.specialFlag(rule)) {
                sb.append(SPECIAL[randomIndex(SPECIAL.length)]);
                i++;
            }
        }
        // 避免超长
        if (sb.length() > length) {
            sb.substring(0, length);
        }
        return sb.toString();
    }

    private int randomIndex(int length) {
        return new Random().nextInt(length);
    }
}
