package com.web.tests;

import com.web.connector.SQL;
import com.web.driver.DriverManager;
import com.web.executiondata.AppData;
import com.web.executiondata.GlobalData;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;

import java.io.InputStream;
import java.util.Properties;

@Log4j2
public abstract class BaseTest {

    @BeforeTest(alwaysRun = true)
    public static void setup(){
        //load properties
        String activeProfile = System.getProperty("env","qa");
        log.info("Active profile: " + activeProfile);
        Properties properties = new Properties();
        log.info("Loading properties from property file");

        log.info("Resource URI:"+ BaseTest.class.getClassLoader().getResource("config.properties"));

        try (InputStream resourceStream = BaseTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (resourceStream == null) {
                throw new RuntimeException("Resource not found: " + "config.properties");
            }
            properties.load(resourceStream);
            log.info("Properties loaded: " + properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //load properties
        AppData.APP_URL.setUrl(properties.getProperty("app_url"));
        log.info("Setting AppData done");
    }

    @BeforeTest(alwaysRun = true)
    public static void setupDriver() {
        log.info("Setting up driver");
        DriverManager.setDriver(GlobalData.IS_REMOTE, GlobalData.EXECUTION_BROWSER);
        log.info("Driver setup done");
        try {
            SQL.getOrInitConnection("db");
        } catch (Exception e) {
            log.info("Exception while getting connection");
            throw new RuntimeException(e);
        }

    }

    @AfterSuite()
    public static void tearDown(){
        DriverManager.quitDriver();
        log.info("Quit Driver done");
    }

}
