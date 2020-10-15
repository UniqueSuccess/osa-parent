package cn.neiwang.osajni;

import com.goldencis.osa.common.constants.PathConfig;
import org.apache.log4j.Logger;

/**
 * 网络配置jni
 * @author Administrator
 */
public class IFConfig {
    static {
        try {
            System.load(PathConfig.IFCONFIGUTILS_LOADPATH);
        } catch (Exception e) {
            Logger log = Logger.getLogger(IFConfig.class);
            log.error(e);
        }
    }

    public static native String getIFConfig();

    public static native boolean setIFConfig(String config);
}
