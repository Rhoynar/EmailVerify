package com.rhoynar.email.steps;

import com.rhoynar.email.pages.EmailNotificationsPage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Harsh on 8/26/16.
 */
public class EmailNotificationSteps {
    private static final Logger log = LogManager.getLogger(EmailNotificationSteps.class);
    private static EmailNotificationsPage emailNotificationsPage;


    @Given("^Give that website contact us page is loaded$")
    public void give_that_website_contact_us_page_is_loaded() throws Throwable {
    }

    @When("^I submit question on contact us form$")
    public void i_submit_question_on_contact_us_form() throws Throwable {
    }

    @Then("^I get a confirmation email$")
    public void i_get_a_confirmation_email() throws Throwable {
    }

}
