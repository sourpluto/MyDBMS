public class SelectFromTable {
    //select * from 表名
    public static void selectFromTb(String dbName, String tbName) throws DocumentException {
        //数据库是否合法
        if (IsLegal.isDatabaseEmpty()) {
            return;
        }
        //若表存在，则得到表的最后一个文件下标
        String file_num = IsLegal.lastFileName(dbName, tbName);
        // ... existing code ...
    }

    //select * from 表名 where 列名称=列值
    public static void selectAllFromTb(String dbName, String tbName, List<String> tmp1) throws DocumentException {
        //数据库是否合法
        if (IsLegal.isDatabaseEmpty()) {
            return;
        }
        //若表存在，则得到表的配置文件
        File configFile = IsLegal.isTable(dbName, tbName);
        // ... existing code ...
    }
}