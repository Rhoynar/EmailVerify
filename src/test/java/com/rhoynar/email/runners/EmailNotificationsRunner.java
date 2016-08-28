package com.rhoynar.email.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

/**
 * Created by Harsh on 8/26/16.
 */
@CucumberOptions(features = "src/test/resources/features/EmailNotifications.feature",
        glue = "com.rhoynar.email.steps",
        monochrome = true)
public class EmailNotificationsRunner extends AbstractTestNGCucumberTests {
}
