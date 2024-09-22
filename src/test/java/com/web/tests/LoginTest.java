package com.web.tests;

import com.web.actions.LoginPageAction;
import com.web.connectors.SQLConnector;
import com.web.query.WMXQuery;
import org.json.simple.JSONArray;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {


    @Test
    public void loginTest() {
        LoginPageAction loginPageAction= new LoginPageAction();
        loginPageAction.verifyNavigationLoginPage();
        loginPageAction.loginToApp("standard_user","secret_sauce");
        loginPageAction.verifyIsLoginSuccssful();
    }

    @Test
    public void userAddressValidation() {
        try {
            JSONArray jsonArray = SQLConnector.fetch("db", WMXQuery.GET_USER_ADDRESS.getStatement().replace("USER_ID", "123"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
