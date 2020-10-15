package com.goldencis.osa.core.aop;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.entity.ResultMsg;
import com.goldencis.osa.common.utils.HttpKit;
import com.goldencis.osa.common.utils.NetworkUtil;
import com.goldencis.osa.core.entity.LogSystem;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.ILogSystemService;
import com.goldencis.osa.core.utils.SecurityUtils;
import io.swagger.annotations.Api;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by limingchao on 2018/11/30.
 */
@Component
@Aspect
public class SystemLogAspect {

    private Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    @Autowired
    private ILogSystemService systemService;

    @Pointcut("@annotation(com.goldencis.osa.core.aop.OsaSystemLog)")
    public void pointcut() {
    }

    @AfterReturning(pointcut = "pointcut()", returning = "retVal")
    public void doOsaSystemLog(JoinPoint joinPoint, Object retVal) {
        boolean success = true;
        String errorMsg = null;
        //判断返回类型和返回结果、错误信息。
        if (retVal instanceof ResultMsg) {
            ResultMsg resultMsg = (ResultMsg) retVal;
            if (ConstantsDto.RESPONSE_SUCCESS != resultMsg.getResultCode()) {
                success = false;
                errorMsg = resultMsg.getResultMsg();
            }
        }

        //生成日志
        this.generateSystemLog(joinPoint, success, errorMsg, retVal);
    }

    private void generateSystemLog(JoinPoint joinPoint, boolean success, String errorMsg, Object retVal) {
        try {
            User user = SecurityUtils.getCurrentUser();
            if (user == null) {
                logger.warn("user not logon,can not do log.");
                return;
            }

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            OsaSystemLog systemLog = method.getAnnotation(OsaSystemLog.class);
            // 排除登录成功的情况，日志记录移植到登录业务内部
//            if (success && pageLog.type() == LogType.LOGIN) {
//                return;
//            }
            String template = systemLog.template();
            if (template.contains("%s")) {
                String[] args = systemLog.args().split(",");
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];
                    Object value = invokeValue(joinPoint, arg);
                    args[i] = String.valueOf(value);
                }
                template = String.format(template, args);
            }

            if (template.contains("%ret")) {
                String ret = systemLog.ret();
                if (!StringUtils.isEmpty(ret) && retVal != null && retVal instanceof ResultMsg) {
                    String[] params = this.getRetrunParam(ret, retVal);
                    template = template.replaceAll("%ret", "%s");
                    template = String.format(template, params);
                }
            }

            String userUserName = user.getUsername();
            String userName = user.getName();

            HttpServletRequest request = HttpKit.getRequest();
            String ip = NetworkUtil.getIpAddress(request);
            String module;
            String classModule = null;
            if (joinPoint.getTarget().getClass().getAnnotation(Api.class) != null) {
                classModule = joinPoint.getTarget().getClass().getAnnotation(Api.class).tags()[0];
                module = String.format("在【%s】中执行【%s】", classModule, systemLog.module());
            } else {
                module = String.format("执行【%s】", systemLog.module());
            }

            String detail;
            if (success) {
                detail = String.format("%s %s操作成功：【%s】", userName, module, template);
            } else {
                detail = String.format("%s %s操作失败：【%s】，错误信息为【%s】", userName, module, template, errorMsg);
            }

            Integer logType = systemLog.type().getValue();
            String logPage = !StringUtils.isEmpty(classModule) ? classModule + "-" + systemLog.module() : systemLog.module();

            LogSystem log = new LogSystem(logType, logPage, detail, String.format("%s invoke", joinPoint.getSignature().toShortString()));
            systemService.saveLog(log);

            logger.info("{}--{}--user is {}", module, template, userUserName);
        } catch (Exception ex) {
            logger.error("create systemLog error,message is {}", ex.getMessage());
        }
    }

    private String[] getRetrunParam(String ret, Object retVal) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String[] params = ret.split(",");
        String[] retArr = new String[params.length];
        if (retVal instanceof ResultMsg) {
            ResultMsg result = (ResultMsg) retVal;
            for (int i = 0; i < params.length; i++) {
                String arg = params[i];
                String[] parts = arg.split("\\.");
                if (ConstantsDto.RESULT_MSG.equals(parts[0])) {
                    retArr[i] = parts[0];
                } else if (ConstantsDto.RESULT_DATA.equals(parts[0])) {
                    if (parts.length > 1) {
                        Object data = result.getData();
                        if (data != null) {
                            String field = parts[1];

                            if (data instanceof Map) {
                                retArr[i] = data != null ? ((Map) data).get(field).toString() : null;
                            } else {
                                try {
                                    Method getMethod = data.getClass().getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
                                    retArr[i] = getMethod.invoke(data).toString();
                                } catch (NoSuchMethodException exception) {
                                    Method getMethod = data.getClass().getMethod(field);
                                    retArr[i] = getMethod.invoke(data).toString();
                                }
                            }
                        } else {
                            retArr[i] = " ";
                        }
                    } else {
                        retArr[i] = result.getData().toString();
                    }
                }
            }
        }
        return retArr;
    }

    private Object invokeValue(JoinPoint joinPoint, String arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String[] parts = arg.split("\\.");
        int index = Integer.parseInt(parts[0]);
        Object value = joinPoint.getArgs()[index];
        if (parts.length > 1) {
            String field = parts[1];
            if (value instanceof Map) {
                value = value != null ? ((Map) value).get(field) : "";
            } else {
                try{
                    Method getMethod = value.getClass()
                            .getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
                    value = getMethod.invoke(value);
                }catch(NoSuchMethodException exception){
                    Method getMethod = value.getClass().getMethod(field);
                    value = getMethod.invoke(value);
                }
            }
        }
        return value;
    }
}
