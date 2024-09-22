package com.web.actions;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.web.driver.DriverManager;
import com.web.listeners.ExtentListeners;
import com.web.listeners.ExtentManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

public class BaseAction {


    public void click(WebElement element, String elementName) {
        try {
            element.click();
            logInfo("Clicking on : " + elementName);
        } catch (Exception e) {
            logError("Can't Click on : " + elementName);
        }
    }

    public void clear(WebElement element, String elementName) {
        element.clear();
        logInfo("Clearing content of " + elementName + ".");
    }

    public void enterText(WebElement element, String value, String elementName) {
        element.sendKeys(value);
        logInfo("Typing in : " + elementName + " entered the value as : " + value);
    }

    public void waitForElementToDisplay(WebElement element, String elementName) {
        try {

            Wait<WebDriver> waitOnElement = new FluentWait<WebDriver>(DriverManager.getDriver()).withTimeout(Duration.ofSeconds(60))
                    .pollingEvery(Duration.ofSeconds(1)).ignoring(Exception.class);
            waitOnElement.until(ExpectedConditions.visibilityOf(element));
            logInfo("Element Loaded: " + elementName);
        }catch (Exception e){
            //logError("Element not loaded: "+ elementName);
        }
    }

    /***
     * Given a message Then write as information to the report engine And if
     * StoryMode is enabled: Capture a screenshot
     *
     * @param message
     *            : String of what to write to the report
     */
    public static void logInfo(String message) {
        ExtentListeners.testReport.get().info(message);
        ExtentManager.captureScreenshot();
        try {
            ExtentListeners.testReport.get().info(
                    "<b>" + "<font color=" + "blue>" + "Screenshot" + "</font>" + "</b>",
                    MediaEntityBuilder.createScreenCaptureFromPath(ExtentManager.screenshotName).build());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void logError(String message) {
        ExtentListeners.testReport.get().log(Status.FAIL,message);
        ExtentManager.captureScreenshot();
        try {
            ExtentListeners.testReport.get().fail(
                    "<b>" + "<font color=" + "red>" + "Screenshot of failure" + "</font>" + "</b>",
                    MediaEntityBuilder.createScreenCaptureFromPath(ExtentManager.screenshotName).build());
        } catch (Exception e) {
            ExtentListeners.testReport.get().info("!Unable to Capture Screen Shot!");
        }
        ExtentListeners.overAllFailure = true;
    }

    public static void logWarning(String message) {
        ExtentListeners.testReport.get().warning(message);
    }

    public static void logAnnotation(String message) {
        ExtentListeners.testReport.get().log(Status.PASS,"<span style='background-color: darkblue'> <b>" + " <font color="
                + "white>" + message + "</font>" + "</b></span>");
    }
}
