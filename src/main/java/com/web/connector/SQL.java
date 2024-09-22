package com.web.connector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;

import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.web.utils.JSON;

@Log4j2
public final class SQL {

    private static final Map<String, HikariDataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>(2);
    private static Connection conn = null;

    public static Connection getOrInitConnection(final String dbName) throws Exception {
        String db = Optional.ofNullable(dbName).orElse("db");
        if (!DATA_SOURCE_MAP.containsKey(db)) {
            createDataSource(db);
        }
        HikariDataSource dataSource = DATA_SOURCE_MAP.get(db);
        return dataSource.getConnection();
    }

    private static void createDataSource(final String dbName) throws Exception {
        log.info("Creating datasource for " + dbName);
        Properties props = new Properties();
        try {
            props.load(SQL.class.getClassLoader().getResourceAsStream(dbName + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HikariConfig config = new HikariConfig(props);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        DATA_SOURCE_MAP.put(dbName, hikariDataSource);
    }

    public static JSONArray fetch(final String db, final String query) throws Exception {
            try (Connection connection = getOrInitConnection(db)) {
                return convertToJson(connection.prepareStatement(query).executeQuery());
            }
    }

    public static List<Map<String, String>> fetchDataMap(final String db, final String query) throws Exception {
            try (Connection connection = getOrInitConnection(db)) {
                return convertToMap(connection.prepareStatement(query).executeQuery());
        }
    }

    public static int executeUpdate(final Map<String, String> dataMap, final String tableName) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder placeholders = new StringBuilder();
        int affectedrows = 0;
        List<String> keysetData = new LinkedList<String>();
        for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
            String nextelement = iter.next();
            sql.append(nextelement);
            keysetData.add(nextelement);
            placeholders.append("?");
            if (iter.hasNext()) {
                sql.append(",");
                placeholders.append(",");
            }
        }
        sql.append(") VALUES (").append(placeholders).append(")");
        try {
            PreparedStatement preparedStatement = getOrInitConnection("db").prepareStatement(sql.toString());
            int i = 1;
            for (String key : keysetData) {
                preparedStatement.setObject(i++, dataMap.get(key));
            }
            affectedrows = preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return affectedrows;
    }

    private static JSONArray convertToJson(final ResultSet resultSet) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        while (resultSet.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= numColumns; i++) {
                String columnName = rsmd.getColumnName(i);
                if (resultSet.getObject(columnName) instanceof LocalDateTime) {
                    obj.put(columnName, ((LocalDateTime) resultSet.getObject(columnName)).toString());
                } else if (resultSet.getObject(columnName) instanceof Date) {
                    obj.put(columnName, ((Date) resultSet.getObject(columnName)).toString());
                } else if (resultSet.getObject(columnName) instanceof Integer) {
                    obj.put(columnName, ((Integer) resultSet.getObject(columnName)));
                } else if (resultSet.getObject(columnName) instanceof Double) {
                    obj.put(columnName, ((Double) resultSet.getObject(columnName)));
                } else if (resultSet.getObject(columnName) instanceof JSONArray) {
                    obj.put(columnName, ((JSONArray) resultSet.getObject(columnName)));
                } else if (resultSet.getObject(columnName) != null && resultSet.getObject(columnName).toString().contains("{") && !resultSet.getObject(columnName).toString().contains("[")) {
                    obj.put(columnName, ((JSONObject) JSON.parseString(resultSet.getObject(columnName).toString())));
                } else {
                    obj.put(columnName, resultSet.getObject(columnName));
                }
            }
            json.add(obj);
        }
        return json;
    }

    private static List<Map<String, String>> convertToMap(final ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        List<Map<String, String>> finaldata = new LinkedList<Map<String, String>>();
        while (resultSet.next()) {
            int numColumns = rsmd.getColumnCount();
            Map<String, String> obj = new LinkedHashMap<>();
            for (int i = 1; i <= numColumns; i++) {
                String columnName = rsmd.getColumnName(i);
                obj.put(columnName, resultSet.getString(columnName));
            }
            finaldata.add(obj);
        }
        return finaldata;
    }

    public static void closeConnection() throws Exception {
        log.info("Closing all SQL connection...");
        DATA_SOURCE_MAP.forEach(
                (k, v) -> {
                    if (!v.isClosed()) v.close();
                });
        if (conn != null && !conn.isClosed()) {
            log.info("Closing Database Connection");
            conn.close();
        }
    }

    @Deprecated
    public static ResultSet executeQueryRS(final String dbName, final String query) throws Exception {
        Connection connection = getOrInitConnection(dbName);
        return connection.prepareStatement(query).executeQuery();
    }
}
