package com.goldencis.osa.system.utils;

import org.hyperic.sigar.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by limingchao on 2018/1/30.
 */
public class ComputerInfoUtil {

    public static Map memory() throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();

        Map<String, Object> res = new HashMap<>();
        // 内存总量
        res.put("totalMem", mem.getTotal());
        // 当前内存使用量
        res.put("usedMem", mem.getActualUsed());

        return res;
    }

    public static double cpu() throws SigarException {
        Sigar sigar = new Sigar();
        CpuPerc[] cpuList = sigar.getCpuPercList();
        double cpu = 0;
        for (int i = 0; i < cpuList.length; i++) {
            cpu += cpuList[i].getCombined();
        }
        return cpu / cpuList.length;
    }

    public static Double mySQLUsage() throws Exception {
        Sigar sigar = new Sigar();
        FileSystem fslist[] = sigar.getFileSystemList();
        for (int i = 0; i < fslist.length; i++) {
            FileSystem fs = fslist[i];
            // 分区的盘符名称
            // 根据分区的盘符路径，匹配MySQL的磁盘分区
            if ("/gdsoft/mysql_db".equals(fs.getDirName())) {
                // 文件系统资源的利用率
                FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
                return usage.getUsePercent() * 100D;
            }
        }
        return null;
    }

    public static Map dbUsage() throws Exception {
        Sigar sigar = new Sigar();
        FileSystem fslist[] = sigar.getFileSystemList();
        long total = 0;
        long used = 0;
        for (int i = 0; i < fslist.length; i++) {
            FileSystem fs = fslist[i];
            // 文件系统资源的利用率
            FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
            total += usage.getTotal();
            used += usage.getUsed();
        }

        Map<String, Object> res = new HashMap<>();
        // 内存总量
        res.put("total", total);
        // 当前内存使用量
        res.put("used", used);

        return res;
    }
}
