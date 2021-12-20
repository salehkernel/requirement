package ir.salmanian.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {

    private Map<DBMS, String> driverClassMap = new HashMap<>();
    private Map<DBMS, String> dialectMap = new HashMap<>();
    private Configuration config;
    private static DatabaseConfig instance;

    private DatabaseConfig() {

        driverClassMap.put(DBMS.MYSQL, "com.mysql.cj.jdbc.Driver");
        driverClassMap.put(DBMS.POSTGRESQL, "org.postgresql.Driver");

        dialectMap.put(DBMS.MYSQL, "org.hibernate.dialect.MySQL8Dialect");
        dialectMap.put(DBMS.POSTGRESQL, "org.hibernate.dialect.PostgreSQLDialect");
    }

    public static DatabaseConfig getInstance() {
        if (instance == null)
            instance = new DatabaseConfig();
        return instance;
    }

    public SessionFactory useExternalDB(DBMS dbms, String hostAddress, int port, String dbName,
                                        String username, String password) {
        if (testConnection(dbms, hostAddress, port, dbName, username, password)) {
            setProperties(dbms, hostAddress, port, dbName, username, password);

            return config.buildSessionFactory();
        } else
            return null;

    }

    public SessionFactory useEmbeddedDB() {
        config = new Configuration().configure();
        return config.buildSessionFactory();
    }

    public boolean testConnection(DBMS dbms, String hostAddress, int port, String dbName,
                                  String username, String password) {
        setProperties(dbms, hostAddress, port, dbName, username, password);

        try {
            return config.buildSessionFactory() != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setProperties(DBMS dbms, String hostAddress, int port, String dbName, String username, String password) {
        config.setProperty("hibernate.connection.driver_class", driverClassMap.get(dbms));
        config.setProperty("hibernate.dialect", dialectMap.get(dbms));
        config.setProperty("hibernate.connection.url", "jdbc:" + dbms.toString().toLowerCase() + "://" + hostAddress + ":" + port + "/" + dbName);
        config.setProperty("hibernate.connection.username", username);
        config.setProperty("hibernate.connection.password", password);
        config.setProperty("hibernate.hbm2ddl.auto", "update");
    }
}
