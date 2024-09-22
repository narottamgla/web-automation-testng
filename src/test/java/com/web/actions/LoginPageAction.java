package com.web.actions;

import com.web.drivers.DriverManager;
import com.web.executiondata.AppData;
import com.web.locator.LoginPageLocator;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

@Log4j2
public class LoginPageAction extends BaseAction{

    LoginPageLocator loginPageLocator;
    public LoginPageAction(){
     loginPageLocator = new LoginPageLocator();
     PageFactory.initElements(DriverManager.getDriver(),loginPageLocator);
    }

    public void verifyNavigationLoginPage(){
        DriverManager.getDriver().get(AppData.APP_URL.getUrl());
        Assert.assertTrue(loginPageLocator.homePageLogo.isDisplayed(),"Login Page Logo Not displayed");
    }

    public void loginToApp(String userName, String password)
    {
     clear(loginPageLocator.userNameTxBx,"User Name TextBox");
     enterText(loginPageLocator.userNameTxBx,userName,"UserName TextBox");
     clear(loginPageLocator.passwordTxBx,"Password TextBox");
     enterText(loginPageLocator.passwordTxBx,password,"Password TextBox");
     click(loginPageLocator.loginButton,"Login Button");
    }

    public void verifyIsLoginSuccssful(){
        Assert.assertEquals(loginPageLocator.productHomePageHeader.getText(),"Products");
    }






}
