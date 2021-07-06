package xyz.jerez.sql;

import org.junit.Test;

import java.sql.*;

/**
 * @author liqilin
 * @since 2021/4/18 13:27
 */
@SuppressWarnings({"all"})
public class ConnectionExamples {

    private final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private String jdbcUrl = "jdbc:mysql://42.192.183.108:3306/jdbc_demo?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
    private String username = "lql";
    private String password = "lql";

    @Test
    public void driver() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//        不知道为何，无法直接new
//        new com.mysql.cj.jdbc.Driver();
//        new com.mysql.jdbc.Driver();
        final Class<?> clazz = Class.forName(driverClassName);
        final Driver o = (Driver) clazz.newInstance();
    }

    @Test
    public void connection() throws Exception {
//        DriverManager.setLogWriter(new PrintWriter(System.out));
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        final DatabaseMetaData metaData = connection.getMetaData();

        System.out.println(metaData.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
        System.out.println(metaData.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
        System.out.println(metaData.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));
        System.out.println();
        System.out.println(metaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        System.out.println(metaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
        System.out.println(metaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));
        System.out.println(metaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));
        System.out.println();
//        不支持
        System.out.println(metaData.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        System.out.println(metaData.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT));
    }

    @Test
    public void transactionIsolation() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
    }

    @Test
    public void autoCommit() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
//        connection.setAutoCommit(false);
//        connection.commit();
    }

    @Test
    public void statement1() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        final Statement statement = connection.createStatement();
        final ResultSet resultSet = statement.executeQuery("select * from user");
        System.out.println(resultSet.isFirst());
//        游标后移1位，
        resultSet.next();
//        游标前移1位
        resultSet.previous();
//        游标回到第一个
        resultSet.first();
//        游标回到最后一个
        resultSet.last();
//        游标回到第一个之前（最开始默认的状态）
        resultSet.beforeFirst();
//        到指定某行
        resultSet.absolute(3);
        resultSet.last();
//        相对于当前行，到第几行。正数往后，负数往前
        resultSet.relative(-2);
        System.out.println(resultSet.getInt(1));

//        connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Test
    public void statement2() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        final ResultSet resultSet = statement.executeQuery("select * from user");
        resultSet.last();
        System.out.println(resultSet.getInt(1));
        resultSet.first();
        System.out.println(resultSet.getInt(1));
    }

    @Test
    public void statement3() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
//        mysql 好像只支持 TYPE_SCROLL_INSENSITIVE，第一个参数无论怎么设置都不影响
//        只有第二个参数为 CONCUR_UPDATABLE，才可修改数据
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        final ResultSet resultSet = statement.executeQuery("select * from user");
        resultSet.last();
        System.out.println(resultSet.getInt(1));
        resultSet.first();
        System.out.println(resultSet.getInt(1));
        resultSet.updateInt(1, 11);
//        必须在游标离开当前行前，调用才会生效
        resultSet.updateRow();

//        该方法是用于判断这行记录有没有被修改过（当前连接/其他连接），但可能该特性不支持
//        resultSet.rowUpdated();
        System.out.println(resultSet.getInt(1));
    }

    @Test
    public void statement4() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
//        默认为true
        connection.setAutoCommit(false);
//        mysql 好像只支持 TYPE_SCROLL_INSENSITIVE，第一个参数无论怎么设置都不影响
//        只有第二个参数为 CONCUR_UPDATABLE，才可修改数据
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, ResultSet.CLOSE_CURSORS_AT_COMMIT);
        final ResultSet resultSet = statement.executeQuery("select * from user");
        resultSet.last();
        System.out.println(resultSet.getInt(1));
        resultSet.first();
        System.out.println(resultSet.getInt(1));
        resultSet.updateInt(1, 11);
//        必须在游标离开当前行前，调用才会生效
        resultSet.updateRow();
        System.out.println(resultSet.getInt(1));
        System.out.println(resultSet.isClosed());
        connection.commit();
//        mysql 好像是不支持 CLOSE_CURSORS_AT_COMMIT ，commit后 resultSet 并未 close
        System.out.println(resultSet.isClosed());
        resultSet.next();
        System.out.println(resultSet.getInt(1));

    }

    @Test
    public void statement5() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        final Statement statement = connection.createStatement();
        final int count = statement.executeUpdate("insert into user (username) values ('lql')");
        System.out.println(count);
    }

    @Test
    public void statement6() throws Exception {
        final long start = System.currentTimeMillis();
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        connection.setAutoCommit(false);
        final String sql = "insert into user (username) values ('lql')";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < 100; i++) {
           preparedStatement.executeUpdate();
        }
        connection.commit();
        final long end = System.currentTimeMillis();
//        2777
        System.out.println(end - start);
    }

    @Test
    public void statement7() throws Exception {
        final long start = System.currentTimeMillis();
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        connection.setAutoCommit(false);
        final String sql = "insert into user (username) values ('lql')";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < 100; i++) {
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
        }
        connection.commit();
        final long end = System.currentTimeMillis();
//        2777
        System.out.println(end - start);
    }

    @Test
    public void prepareStatement() throws Exception {
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        String sql = "select 123";
        final PreparedStatement statement = connection.prepareStatement(sql);
        connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    /**
     * -Xms16m -Xmx16m
     * 默认的方式是将数据库里所有数据都查询出来，数据量大时会造成oom
     * <p>
     * 可通过参数配置，改为流式查询（每调用next时，到数据库查询）
     * 流式查询的条件见{@link com.mysql.cj.jdbc.StatementImpl#createStreamingResultSet()}
     *
     * @see com.mysql.cj.jdbc.StatementImpl#createStreamingResultSet()
     * @see com.mysql.cj.jdbc.StatementImpl#enableStreamingResults()
     */
    @Test
    public void test() throws Exception {
        final long start = System.currentTimeMillis();
        Class.forName(driverClassName);
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
//        默认为true
//        mysql 好像只支持 TYPE_SCROLL_INSENSITIVE，第一个参数无论怎么设置都不影响
//        只有第二个参数为 CONCUR_UPDATABLE，才可修改数据
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
//        statement.setFetchSize(Integer.MIN_VALUE);
        statement.setFetchSize(500);
        final ResultSet resultSet = statement.executeQuery("select * from test");

        while (resultSet.next()) {

        }

        final long end = System.currentTimeMillis();
        System.out.println(end - start);

//        15585 默认全部
//        15300 流式
//        对比起来，速度好像并没有快，只是不会OOM了
    }

    @Test
    public void test1() throws Exception {
        final long start = System.currentTimeMillis();
        Class.forName(driverClassName);
        jdbcUrl = jdbcUrl + "&useCursorFetch=true";
        final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
//        默认为true
        connection.setAutoCommit(false);
//        mysql 好像只支持 TYPE_SCROLL_INSENSITIVE，第一个参数无论怎么设置都不影响
//        只有第二个参数为 CONCUR_UPDATABLE，才可修改数据
        final PreparedStatement preparedStatement = connection.prepareStatement("select * from test", ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
//        preparedStatement.setFetchSize(Integer.MIN_VALUE);
        preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
//        fetchSize大于0时，是作用于游标方式
        preparedStatement.setFetchSize(500);
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

        }

        final long end = System.currentTimeMillis();
        System.out.println(end - start);

//        15585 默认全部
//        15300 流式
//        游标：9170 但这种方式是把部分压力转移到了数据库服务器，会影响其他的语句，一般情况下不会用

    }
}
