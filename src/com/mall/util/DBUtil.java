package com.mall.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    private static final String URL ="jdbc:mysql://localhost:3306/simple_mall?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    //private static final String URL ="jdbc:mysql://root:86lcrjh6@dbconn.sealoshzh.site:34454/simple_mall?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // 改成你自己的密码

    static {
        try {
            // MySQL8 驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
