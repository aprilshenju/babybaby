package com.umeijia.service;

import com.umeijia.wechat.TextMessage;
import com.umeijia.wechat.WechatUtil;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

//import org.springframework.util.DigestUtils;

/**
 * Created by hadoop on 2016/6/29.
 */

@Service
@Path("/wechat_service")
public class WechatService {
    private final String serverIp = "http://xiaoxiaomi.imwork.net/umeijiaServer/";
    private final String bindUrl = serverIp+"bind.jsp";
    @Path("/wechat")
    @POST
    public void wechat(@Context HttpServletRequest request, @Context HttpServletResponse response){
        System.out.println("收到微信服务器的Post请求");
        PrintWriter out = null;
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        try {
//            wechatValid(request,response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            out = response.getWriter();
            Map<String,String> xmlMap = WechatUtil.xmlToMap(request);
            String toUserName = xmlMap.get("ToUserName");
            String fromUserName = xmlMap.get("GromUserName");
            String createTime = xmlMap.get("CreateTime");
            String msgType = xmlMap.get("MsgType");

            String resultMessage = null;
            switch (msgType){
                case "event"://事件
                    String event = xmlMap.get("Event");
                    String eventKey = xmlMap.get("EventKey");
                    switch (eventKey){
                        case "bind"://点击绑定
                            System.out.println("点击绑定");
                            TextMessage textMessage = new TextMessage();
                            textMessage.setToUserName(fromUserName);
                            textMessage.setFromUserName(toUserName);
                            textMessage.setCreateTime(String.valueOf(new Date().getTime()));
                            textMessage.setMsgType("text");
                            textMessage.setContent(bindUrl+"?openId = "+fromUserName);
                            resultMessage = WechatUtil.objectToXml(textMessage);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    System.out.println("收到其他类型消息，暂不处理");
                    break;
            }
            out.print(resultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }finally {
            out.close();
        }
    }
    private void wechatValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = "umeijia";
        String encodingAESKey ="Pr9qsEaB2cf6vwNx3Ai9BP0dfVFOjCfZ17hAzjuusZq";
        String corpId = "umeijia";
        // 微信加密签名

        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        String result = null;
//        try {
//            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey,
//                    corpId);
//            result = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
//        } catch (AesException e) {
//            e.printStackTrace();
//        }
//        if (result == null) {
//            result = token;
//        }
        String[] values = {token,timestamp,nonce};
        Arrays.sort(values);
        String value = values[0]+values[1]+values[2];
        String sign = DigestUtils.shaHex(value);
        if(signature.equals(sign)){
            result = echostr;
        }
        PrintWriter out = response.getWriter();
        out.print(result);
        out.close();
    }
}
