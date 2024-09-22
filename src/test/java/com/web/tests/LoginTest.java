package com.web.tests;

import com.web.actions.LoginPageAction;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {


    @Test
    public void loginTest() {
        LoginPageAction loginPageAction= new LoginPageAction();
        loginPageAction.verifyNavigationLoginPage();
        loginPageAction.loginToApp("standard_user","secret_sauce");
        loginPageAction.verifyIsLoginSuccssful();
    }

}
