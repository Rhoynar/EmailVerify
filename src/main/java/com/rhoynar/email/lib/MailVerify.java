package com.rhoynar.email.lib;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.mail.Message;

/**
 * Created by Harsh on 7/28/16.
 */
public class MailVerify {

    //private static MailManager mailMgr = MailManager.getInstance();
    private static boolean monitor = false;

    public static void startMonitor() {
        MailManager.login();
        monitor = true;
    }

    public static void stopMonitor() {
        if (monitor) {
            MailManager.logout();
            monitor = false;
        }
    }

    public static boolean verifyMailReceived() {
        boolean ret = true;
        if (!monitor) {
            startMonitor();
        }

        Message mail = MailManager.getLatestMsgBlock();
        ret = ret && (mail != null);
        stopMonitor();
        return ret;
    }

    public static boolean verifyMailReceivedFrom(String from) {
        boolean ret = true;
        if (!monitor) {
            startMonitor();
        }

        Message mail = MailManager.getLatestMsgBlock();
        ret = ret && (mail != null);
        ret = ret && StringUtils.containsIgnoreCase(MailManager.getMessageFrom(mail), from);
        stopMonitor();
        return ret;
    }

    public static boolean verifyMailReceivedWithSub(String sub) {
        boolean ret = true;
        if (!monitor) {
            startMonitor();
        }

        Message mail = MailManager.getLatestMsgBlock();
        ret = ret && (mail != null);
        ret = ret && StringUtils.containsIgnoreCase(MailManager.getMessageSubject(mail), sub);
        stopMonitor();
        return ret;
    }

    public static boolean verifyMailReceivedWithContent(String content) {
        boolean ret = true;
        if (!monitor) {
            startMonitor();
        }
        Message mail = MailManager.getLatestMsgBlock();
        ret = ret && (mail != null);
        ret = ret && StringUtils.containsIgnoreCase(MailManager.getMessageText(mail), content);
        stopMonitor();
        return ret;
    }

    public static boolean verifyMailNotReceived() {
        boolean ret = true;
        if (!monitor) {
            startMonitor();
        }

        Message mail = MailManager.getLatestMsgBlock(30);
        ret = ret && (mail == null);
        stopMonitor();
        return ret;
    }


    @Test
    public void verifyMailReceivedTest() {
        Assert.assertTrue(verifyMailReceived());
    }

    @Test
    public void verifyMailReceivedFromTest() {
        Assert.assertTrue(verifyMailReceivedFrom("jenkinsmcn@gmail.com"));
    }

    @Test
    public void verifyMailReceivedWithSubTest() {
        Assert.assertTrue(verifyMailReceivedWithSub("Test subject"));
    }

    @Test
    public void verifyMailReceivedWithContentTest() {
        Assert.assertTrue(verifyMailReceivedWithContent("Test email"));
    }

}
