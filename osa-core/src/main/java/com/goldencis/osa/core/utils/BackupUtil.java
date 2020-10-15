package com.goldencis.osa.core.utils;

import com.goldencis.osa.common.constants.PathConfig;

import java.io.*;

/**
 * 数据库备份
 */
public class BackupUtil {

    private String allFileName;

    private String fileName;

    private String refileName;

    /**
     * 拼接备份命令
     */
    private String splitJointBackup(String cfileName) {
        String str = PathConfig.LINUX_MYSQL_PATH + "mysqldump -h " + PathConfig.MYSQL_IP + " -u "
                + PathConfig.MYSQL_USENAME + " -p" + PathConfig.MYSQL_PWD + " " + PathConfig.MYSQL_DATABASE_NAME + " --routines";
        return str;
    }

    /**
     * 备份
     */
    public void backup(String cfileName) {
        String cmd = splitJointBackup(cfileName);
        PrintWriter p = null;
        BufferedReader reader = null;
        File filePath = new File(PathConfig.LINUX_FILE_PATH);
        FileOutputStream fileout = null;
        try {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            fileout = new FileOutputStream(cfileName);
            p = new PrintWriter(new OutputStreamWriter(fileout, "utf8"));
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");
            reader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                p.println(line);
            }
            p.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (p != null) {
                    p.close();
                }
                if (fileout != null) {
                    fileout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 还原
     * @author mll
     * @param cfileName void
     */
    public void restore(String cfileName) {
        try {
            String cmd = splitJointRestore(cfileName);
            //Process process = Runtime.getRuntime().exec("mysql -u root -p123456 -h 192.168.3.89 goldencis_tsa </opt/tsa/backup/1111492136868475.sql");
            Process process = Runtime.getRuntime().exec(cmd);
            // 输出执行结果
            InputStreamReader in = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            in.close();
            // 输出错误信息
            InputStreamReader in2 = new InputStreamReader(process.getErrorStream());
            BufferedReader br2 = new BufferedReader(in2);
            br2.close();
            in2.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 拼接还原命令
     * @return String
     */
    @SuppressWarnings("unused")
    private String splitJointRestore(String cfileName) {
        String str = "mysql -h " + PathConfig.MYSQL_IP + " -u" + PathConfig.MYSQL_USENAME + " -p" + PathConfig.MYSQL_PWD + " "
                + PathConfig.MYSQL_DATABASE_NAME;
        return str;
    }

    public String getAllFileName() {
        return allFileName;
    }

    public void setAllFileName(String allFileName) {
        this.allFileName = allFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRefileName() {
        return refileName;
    }

    public void setRefileName(String refileName) {
        this.refileName = refileName;
    }

}
