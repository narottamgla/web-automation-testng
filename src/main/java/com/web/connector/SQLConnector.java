package com.web.connector;

import lombok.extern.log4j.Log4j2;

import java.sql.*;

@Log4j2
public class SQLConnector {

    public static Connection con;
    Statement stmt;
    ResultSet rs;

    public SQLConnector createConnection(
            final String dbUrl, final String dbUser, final String dbPswd, final String dbLoader)
            throws SQLException, ClassNotFoundException {
        con = DriverManager.getConnection(dbUrl, dbUser, dbPswd);
        Class.forName(dbLoader);
        return new SQLConnector();
    }

    public SQLConnector createQuery(final String query) throws SQLException {
        stmt = con.createStatement();
        return new SQLConnector();
    }

    public SQLConnector executeQuery(final String query) throws SQLException {
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        return new SQLConnector();
    }

    public SQLConnector executeUpdate(final String query) throws SQLException {
        stmt = con.createStatement();
        log.info("Query: {}", query);
        stmt.executeUpdate(query);
        return new SQLConnector();
    }

    public SQLConnector closeConnection() throws SQLException {
        con.close();
        return new SQLConnector();
    }
}
