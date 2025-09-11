package function;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class DropDatabase {
    //delete database 数据库名称
    public static void deleteDB(String dbname) throws IOException {
        File file = new File("./mydatabase/" + dbname + "");
        if (!file.exists()) {
            System.out.println("database " + dbname + " is not exist");
            return;
        }
        //若数据库中有表存在，则提示用户
        if (file.listFiles().length > 0) {
            System.out.println("数据库" + dbname + "中有表存在，是否继续删除(Y/N)");
            
            // 检查是否在交互式环境中
            if (System.console() != null) {
                try (Scanner scanner = new Scanner(System.in)) {
                    String answer = scanner.next();
                    if (answer.toUpperCase().equals("Y")) {
                        deleteAllFilesAndDirectory(file, dbname);
                    } else {
                        return;
                    }
                }
            } else {
                // 非交互式环境，默认删除
                System.out.println("非交互式环境，自动删除数据库");
                deleteAllFilesAndDirectory(file, dbname);
            }
        }
        //若数据库为空，直接删除
        else {
            file.delete();
            System.out.println("数据库" + dbname + "删除成功");
        }
    }
    
    private static void deleteAllFilesAndDirectory(File file, String dbname) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFilesAndDirectory(f, f.getName());
                } else {
                    f.delete();
                }
            }
        }
        file.delete();
        System.out.println("数据库" + dbname + "删除成功");
    }
}
