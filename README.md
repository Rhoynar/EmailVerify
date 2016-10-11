# EmailVerify
Library for verifying receiving email notifications in Java

## What is this project about?
Many times we face a design pattern in our QA Automation jobs - how do I verify that my web application is sending the required email notifications? What if the user is unsubscribed - how do I verify that my web-app did NOT send the notificaction? How to verify email from a given sender is received? How to verify email contents? How to verify email contains a certain subject line?

Many times we solve this problem by assuming that email has been sent somehow, and we just rely on website notifications (success or failure messages displayed on UI). But in order to thouroughly test the web application it is important to have a framework or library that can help us perform the above activities easily.

## How does it work?
This library uses the credentials available in config.properties (credentials include: email id, password and POP/IMAP email account information). You can configure the credentials differently in config.properties - currently it is set to some gmail unsecure account names.

The project assumes that you are going to register this credential in the web-application. Any email that is sent to this address from the web-application can then be monitored and asserted upon.

## How to use this framework?
Integrate this library as a package in your Java application? (Sorry at the moment we have not integrated within Maven repository - but thats the hope some day!). And use the following commands to perform what you need.

````
  Start monitoring emails : MailVerify::startMonitor();
  Verify mail is received : MailVerify::verifyMailReceived();
  Verify mail from sender : MailVerify::verifyMailReceivedFrom(String sender);
  Verify mail with subject: MailVerify::verifyMailReceivedWithSub(String subject);
  Verify mail sent to     : MailVerify::verifyMailSentTo(String to);
  Verify mail with content: MailVerify::verifyMailWithContent(String content); 
  Verify mail not received: MailVerify::verifyMailNotReceived();
  Stop monitoring emails  : MailVerify::stopMonitor();
```

## Contact Us
Have questions? Do not hesitate to contact us at [contact@rhoynar.com](contact@rhoynar.com). Or you can visit our website at: [www.rhoynar.com](http://www.rhoynar.com)

