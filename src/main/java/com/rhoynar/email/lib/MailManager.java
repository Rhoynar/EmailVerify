package com.rhoynar.email.lib;

import com.rhoynar.email.config.ReadProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Harsh on 7/27/16.
 */
public class MailManager {
    private static final Logger log = LogManager.getLogger(MailManager.class);
    private static Store store;
    private static Session emailSession;
    private static Folder emailFolder;
    private static boolean loggedIn = false;
    private static boolean pendingMsgDelete = false;
    private static MailManager instance = null;
    private static Date lastTime;
    private static int timeout = 60;


    /**
     * Constructor - private to avoid creating multiple instances.
     */
    private MailManager() {
    }

    /**
     * Get singleton instance of the MailManager framework.
     *
     * @return singleton MailManager object
     */
    public static MailManager getInstance() {
        if (instance == null) {
            instance = new MailManager();
        }
        return instance;
    }

    /**
     * Get last time when mailbox was read.
     *
     * @return get last time when mailbox was read.
     */
    public static Date getLastTime() {
        return lastTime;
    }

    /**
     * Update last time when mailbox was read.
     */
    public static void updateLastTime() {
        lastTime = new Date();
    }

    /**
     * Login into the mailbox with specified configurations
     * provided through config.properties file.
     * <p>
     * Currently support POP3 emails. No support for IMAP yet.
     */
    public static void login() {
        try {
            Properties cfg = ReadProperties.getProperties();
            String server = cfg.getProperty("pop3-server");
            String port = cfg.getProperty("pop3-port");
            String user = cfg.getProperty("pop3-username");
            String password = cfg.getProperty("pop3-password");

            if (!loggedIn) {
                //create properties field
                Properties properties = new Properties();

                properties.put("mail.pop3.host", server);
                properties.put("mail.pop3.port", port);
                properties.put("mail.pop3.starttls.enable", "true");
                emailSession = Session.getDefaultInstance(properties);

                //create the POP3 store object and connect with the pop server
                store = emailSession.getStore("pop3s");
                store.connect(server, user, password);

                // Open Inbox
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_WRITE);

                // Store the open time.
                updateLastTime();

                //store state
                loggedIn = true;
                pendingMsgDelete = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logout of the email box, expunge any deleted messages.
     */
    public static void logout() {
        try {
            // Close inbox
            emailFolder.close(pendingMsgDelete);

            // Close connection
            store.close();

            loggedIn = false;
            pendingMsgDelete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all emails from the mailbox.
     *
     * @return All emails returned as an array of messages.
     */
    public static Message[] getAllMsgs() {
        try {
            if (!loggedIn) {
                login();
            }
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();

            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the latest message from inbox folder.
     *
     * @return Latest message.
     */
    public static Message getLatestMsg() {
        try {
            if (!loggedIn) {
                login();
            }

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            Message message = messages[messages.length - 1];

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void printDate(String text, Date date) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        log.debug(text + format.format(date));
    }

    /**
     * Get the latest message from inbox folder.
     *
     * @return Latest message.
     */
    public static Message getLatestMsgBlock(int secs) {
        try {
            /* block until we get a new message */
            printDate("Inbox read at: ", lastTime);
            for (int idx = 0; idx < secs; idx++) {
                Thread.sleep(1000);

                // Reopen the mailbox everytime to force refresh.
                emailFolder.close(pendingMsgDelete);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_WRITE);

                Message msg = getLatestMsg();

                String comparison = "(Waiting: " + msg.getSentDate().compareTo(lastTime) + "): ";
                printDate(comparison + "Latest email was received at: ", msg.getSentDate());
                if (msg.getSentDate().compareTo(lastTime) > 0) {
                    log.info("==== Found a new message! === ");
                    printMessage(msg);
                    updateLastTime();
                    return msg;
                }

            }

            log.error("=== Unable to find new message in " + timeout + " seconds.. Bailing.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Message getLatestMsgBlock() {
        return getLatestMsgBlock(timeout);
    }

    /**
     * Get the latest message from a given person.
     * <p>
     * The given person can be a name or email address.
     *
     * @param from - Who the message is from
     * @return null if unable to find message from this user.
     */
    public static Message getLatestMsgFrom(String from) {
        try {
            if (!loggedIn) {
                login();
            }

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            for (int idx = messages.length - 1; idx >= 0; idx--) {
                Message msg = messages[idx];
                if (StringUtils.containsIgnoreCase(getMessageFrom(msg), from)) {
                    return msg;
                }
            }

            log.error("No messages found from: " + from);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the oldest message in the mailbox.
     *
     * @return returns the oldest message in mailbox.
     */
    public static Message getOldestMessage() {
        return getAllMsgs()[0];
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) {
        String result = "";
        try {
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break;
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * Given a message array, print basic details for all of them
     *
     * @param messages - input email to print.
     */
    public static void printMessages(Message[] messages) {
        try {
            log.debug("Number of messages: " + messages.length);

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                log.debug("---------------------------------");
                log.debug("Email Number " + (i + 1));
                log.debug("Subject: " + message.getSubject());
                log.debug("From: " + getMessageFrom(message));
                log.debug("To: " + message.getReplyTo()[0].toString());
                log.debug("Text: " + getMessageText(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Given a message print its basic details.
     *
     * @param message - Message to be printed.
     */
    public static void printMessage(Message message) {
        try {
            log.debug("---------------------------------");
            log.debug("Sent time: " + message.getSentDate());
            log.debug("Subject: " + message.getSubject());
            log.debug("From: " + getMessageFrom(message));
            log.debug("To: " + message.getReplyTo()[0].toString());
            log.debug("Text: " + getMessageText(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMessage(Message msg) {
        try {
            log.debug("Deleting email from: " + getMessageFrom(msg) + " with sub: " + msg.getSubject());
            msg.setFlag(Flags.Flag.DELETED, true);
            pendingMsgDelete = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllMsgs() {
        try {
            log.debug("Deleting all emails");
            Message[] msgs = getAllMsgs();
            for (Message msg : msgs) {
                msg.setFlag(Flags.Flag.DELETED, true);
            }
            pendingMsgDelete = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ====== Message Parsing Utility Functions ======= */

    /**
     * Parse Body of the email in text format.
     *
     * @return Body of email
     */
    public static String getMessageText(Message message) {
        try {
            String result = "";
            if (message.isMimeType("text/plain")) {
                result = message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = getTextFromMimeMultipart(mimeMultipart);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get Message subject in string form.
     *
     * @param message - message to be parsed
     * @return Subject from the message
     */
    public static String getMessageSubject(Message message) {
        try {
            return message.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Get who the message was sent from
     *
     * @param message - email message
     * @return from address
     */
    public static String getMessageFrom(Message message) {
        try {
            return message.getFrom()[0].toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Test case to verify getting an email after opening inbox.
     */
    @Test
    public void getLastMailBlockTest() {
        login();
        Message msg = getLatestMsgBlock();
        Assert.assertNotNull(msg);
        logout();
    }

    @Test
    public void readAllMailsTest() throws Exception {
        login();
        Message[] messages = getAllMsgs();
        printMessages(messages);
        logout();
    }

    @Test
    public void getLastMailTest() {
        login();
        Message message = getLatestMsg();
        printMessage(message);
        logout();
    }

    @Test
    public void getLastMailFromTest() {
        login();

        Message message = getLatestMsgFrom("MCN Policy Manager Support");
        printMessage(message);

        message = getLatestMsgFrom("support@mcnhealthcare.com");
        printMessage(message);

        logout();
    }


    @Test
    public void deleteMsgTest() {
        login();

        Message msg = getOldestMessage();
        deleteMessage(msg);

        log.debug("Deleting message:");
        printMessage(msg);

        log.debug("Done deleting..");

        logout();
    }


}
