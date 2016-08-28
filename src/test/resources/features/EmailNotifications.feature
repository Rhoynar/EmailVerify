Feature: Email Notifications
  Verify email notifications functionality.

  Scenario: Confirmation email for contact-us form
    Given Give that website contact us page is loaded
    When I submit question on contact us form
    Then I get a confirmation email
