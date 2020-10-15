package com.goldencis.osa.common.constants;

import java.util.ResourceBundle;

/**
 * Created by limingchao on 2018/10/29.
 */
public class PathConfig {
    private final static ResourceBundle BUNDLE;

    public final static String HOMEPATH;

    public final static String BIN_PATH;

    public final static String READ_AUTH_PATH;//解密授权信息文件

    public final static String SESSION_REPLAY_DIRPATH;//会话回访文件存储的相对路径

    public final static String SESSION_REPLAY_ROOTPATH;//会话回访文件存储的根目录

    public final static String REPLAY_CONCAT_SHELL_PATH;//会话回访文件合并的shell脚本存放目录

    public final static String FFMPEG_PATH;//ffmpeg脚本存放目录

    public final static String GUACAMOLE_BLOCK_URL;//ffmpeg脚本存放目录

    public final static String IFCONFIGUTILS_LOADPATH;//网络配置本地接口的本地地址

    public final static String LINUX_MYSQL_PATH;

    public final static String LINUX_FILE_PATH;

    public final static String MYSQL_IP;

    public final static String MYSQL_USENAME;

    public final static String MYSQL_PWD;

    public final static String MYSQL_DATABASE_NAME;

    static {
        BUNDLE = ResourceBundle.getBundle("osa-common");

        HOMEPATH = BUNDLE.getString("homepath");

        BIN_PATH = BUNDLE.getString("binPath");

        READ_AUTH_PATH = BUNDLE.getString("readauthpath");

        SESSION_REPLAY_DIRPATH = BUNDLE.getString("sessionreplay.dirpath");

        SESSION_REPLAY_ROOTPATH = BUNDLE.getString("sessionreplay.rootpath");

        REPLAY_CONCAT_SHELL_PATH = BUNDLE.getString("sessionreplay.concatshellpath");

        FFMPEG_PATH = BUNDLE.getString("ffmpeg.path");

        GUACAMOLE_BLOCK_URL = BUNDLE.getString("guacamole.block.url");

        IFCONFIGUTILS_LOADPATH = BUNDLE.getString("ifconfigutils.loadpath");

        LINUX_MYSQL_PATH = BUNDLE.getString("backup.linuxMysqlPath");

        MYSQL_IP = BUNDLE.getString("backup.ip");

        MYSQL_USENAME = BUNDLE.getString("backup.username");

        MYSQL_PWD = BUNDLE.getString("backup.password");

        MYSQL_DATABASE_NAME = BUNDLE.getString("backup.databaseName");

        LINUX_FILE_PATH = BUNDLE.getString("backup.linuxFilePath");
    }
}
