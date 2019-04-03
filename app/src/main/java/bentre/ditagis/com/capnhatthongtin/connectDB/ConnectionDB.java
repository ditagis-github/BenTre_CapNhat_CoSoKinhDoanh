package bentre.ditagis.com.capnhatthongtin.connectDB;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static final String PROTOCOL = "jdbc:jtds:sqlserver://";
    private static final String SERVER = "112.78.5.173";
    private static final String INSTANCT_NAME = "MSSQLSERVER2012";
    private static final int PORT = 1433;
    private static final String DB = "CHOLONGIS";
    private static final String USER = "sa";
    private static final String PASSWORD = "268@lTk";
    private static final String CLASSNAME = "net.sourceforge.jtds.jdbc.Driver";
    private static final ConnectionDB _instance = new ConnectionDB();
    private Connection connection;
    private Connection connection_image;

    private ConnectionDB() {
        connection = getConnect();
//        connection_image = getConnect_image();
    }

    public static final ConnectionDB getInstance() {
        return _instance;
    }

    public static void main(String[] args) {
        ConnectionDB cndb = new ConnectionDB();
        System.out.println(cndb.getConnect());
    }

    public Connection getConnection() {
        if (connection == null)
            connection = getConnect();
        return connection;
    }

    public Connection getConnectionImage() {
//        if (connection_image == null)
//            connection_image = getConnect_image();
        return connection_image;
    }

    public Connection getConnection(boolean isLogin) {
        connection = getConnect();
//        connection_image = getConnect_image();
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void reConnect() {
        connection = null;
    }

    private Connection getConnect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = String.format("%s%s:%s/%s;instance=%s",PROTOCOL, SERVER, PORT, DB, INSTANCT_NAME);
        Connection cnn = null;
        try {
            Class.forName(CLASSNAME);
            cnn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cnn;
    }
}