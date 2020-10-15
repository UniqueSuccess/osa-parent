package com.goldencis.osa.report.constants;

/**
 * 文件操作类型
 */
public enum FileMonType {

    //操作类型   0 打开  1移动  2复制 3删除文件 4重命名 5创建目录 6新建文件 7修改文件 8删除目录  9恢复(从回收站)

    FILE_OPEN("打开",0),FILE_MOVE("移动",1),FILE_COPY("复制",2),FILE_DELETE_FILE("删除",3),
    FILE_RENAME("重命名",4),FILE_CREATE_DIRECTORY("创建目录",5),FILE_CREATE_FILE("新建",6),
    FILE_UPDATE_FILE("编辑",7),FILE_DELETE_DIRECTORY("删除目录",8),
    FILE_RESTORE("恢复",9);

    private String name;
    private Integer value;

    FileMonType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public static String getNameByValue(int value){
        for (FileMonType c : FileMonType.values()) {
            if (c.getValue() == value) {
                return c.name;
            }
        }
        return null;
    }


}
