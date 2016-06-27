package com.umeijia.util;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;

import static cn.jpush.api.push.model.notification.PlatformNotification.ALERT;


/**
 * Created by hadoop on 2016/6/27.
 */
public class JpushUtil {
    private static final String appKey = "057ad0a73ac0eb4633812d01";
    private static  final String masterSecret = "a3ae8eee34cf1ff37184fb44";
    private static  final int MAX_RETRY_TIMES = 3;
    private static JPushClient jPushClient = new JPushClient(masterSecret,appKey,MAX_RETRY_TIMES);

    public static void notificationToTargetClient(String alias,Message message){
        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_all_alias_alert(alias,message);

        try {
            PushResult result = jPushClient.sendPush(payload);
//            LOG.info("Got result - " + result);
            System.out.println("Got result - " + result);

        } catch (APIConnectionException e) {
            // Connection error, should retry later
//            LOG.error("Connection error, should retry later", e);
            System.err.println("Connection error, should retry later");
            System.err.println(e);

        } catch (APIRequestException e) {
            // Should review the error, and fix the request
//            LOG.error("Should review the error, and fix the request", e);
//            LOG.info("HTTP Status: " + e.getStatus());
//            LOG.info("Error Code: " + e.getErrorCode());
//            LOG.info("Error Message: " + e.getErrorMessage());

            System.err.println("Should review the error, and fix the request");
            System.err.println(e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
        }
    }

    public static PushPayload buildPushObject_all_all_alert() {
        return PushPayload.alertAll(ALERT);
    }

    public static PushPayload buildPushObject_all_alias_alert(String alias,Message message) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setMessage(message)
                .build();
    }
}

