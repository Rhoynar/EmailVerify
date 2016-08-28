package com.rhoynar.email.framework;

import com.rhoynar.email.pages.EmailNotificationsPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by Harsh on 8/26/16.
 */
public class WebApp {
    /* WebDriver variables */
    private static WebDriver driver = null;
    private static WebApp instance = null;
    private static final int timeout = 5;
    private static final String pageLoadedText = "Rhoynar Software Consulting, Denver, CO";

    /* Logging variables */
    private static final Logger log = LogManager.getLogger(WebApp.class);

    /* Private constructor - only one instance possible for this class */
    private WebApp() {
        instance = this;
    }

    public static WebApp getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new WebApp();
            return instance;
        }
    }

    public static WebDriver initializeDriver() {
        if (driver != null) {
            driver.quit();
        }

        driver = new FirefoxDriver();
        return driver;
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            driver = initializeDriver();
        }
        return driver;
    }

}
