package xyz.jerez.sql;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author liqilin
 * @since 2021/5/29 16:36
 */
public class MycatTests {

    private final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private String jdbcUrl = "jdbc:mysql://127.0.0.1:8066/TESTDB?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
    private String username = "root";
    private String password = "123456";

    @Test
    public void select() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
//        如果手动管理事务，则会到写库中去读，读不到slave中写的数据
        connection.setAutoCommit(false);
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        final ResultSet resultSet = statement.executeQuery("select * from demo");
        resultSet.last();
        System.out.println(resultSet.getInt(1));
    }
}
