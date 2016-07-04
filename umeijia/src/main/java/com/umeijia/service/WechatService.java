package com.umeijia.service;

import com.sun.jersey.api.view.Viewable;
import com.umeijia.dao.ParentsDao;
import com.umeijia.util.MD5;
import com.umeijia.vo.Parents;
import com.umeijia.wechat.TextMessage;
import com.umeijia.wechat.WechatUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

//import org.springframework.util.DigestUtils;

/**
 * Created by hadoop on 2016/6/29.
 */

@Service
@Path("/wechat_service")
public class WechatService {
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;

    private final String serverIp = "http://182.150.6.36/umeijiaServer/";
    private final String bindUrl = serverIp + "rest/wechat_service/login";

    @Path("/login/{openId}")
    @GET
    public Viewable login(@Context HttpServletRequest request, @Context HttpServletResponse response,@PathParam("openId") String openId) {
        if (request != null) {
            System.out.println("login------------>openId = " + openId);
            request.setAttribute("openId", openId);
            System.out.println("跳转到绑定界面");
            return new Viewable("/bind.jsp",null);
        } else {
            return null;
        }
    }

    @Path("/wechat")
    @POST
    public void wechat(@Context HttpServletRequest request, @Context HttpServletResponse response) {
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
            Map<String, String> xmlMap = WechatUtil.xmlToMap(request);
            String toUserName = xmlMap.get("ToUserName");
            String fromUserName = xmlMap.get("FromUserName");
            String createTime = xmlMap.get("CreateTime");
            String msgType = xmlMap.get("MsgType");
            String content = xmlMap.get("Content");
            String resultMessage = null;
            switch (msgType) {
                case "event"://事件
                    System.out.println("触发了事件");
                    String event = xmlMap.get("Event");
                    String eventKey = xmlMap.get("EventKey");
                    switch (eventKey) {
                        case "bind"://点击绑定
                            System.out.println("点击绑定");
                            TextMessage textMessage = new TextMessage();
                            textMessage.setToUserName(fromUserName);
                            textMessage.setFromUserName(toUserName);
                            textMessage.setCreateTime(createTime);
                            textMessage.setMsgType("text");
                            textMessage.setContent(bindUrl+"/" + fromUserName);
                            resultMessage = WechatUtil.objectToXml(textMessage);
                            break;
                        default:
                            break;
                    }
                    break;
                case "text"://文本
                    System.out.println("输入了" + content);
                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(createTime);
                    textMessage.setMsgType("text");
                    textMessage.setContent(bindUrl + "?openId=" + fromUserName);
                    resultMessage = WechatUtil.objectToXml(textMessage);
                    break;
                default:
                    System.out.println("收到其他类型消息，暂不处理");
                    break;
            }
            System.out.println(resultMessage);
            out.print(resultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    private void wechatValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = "umeijia";
        String encodingAESKey = "Pr9qsEaB2cf6vwNx3Ai9BP0dfVFOjCfZ17hAzjuusZq";
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
        String[] values = {token, timestamp, nonce};
        Arrays.sort(values);
        String value = values[0] + values[1] + values[2];
        String sign = DigestUtils.shaHex(value);
        if (signature.equals(sign)) {
            result = echostr;
        }
        PrintWriter out = response.getWriter();
        out.print(result);
        out.close();
    }

    @Path("/bindW")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Viewable bindWechat(@FormParam("name") String account, @FormParam("password") String passwd, @FormParam
            ("openId") String openId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
        System.out.println("收到绑定的post请求");
        if (request != null) {
            try {
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("account = " + account);
            System.out.println("passwd = " + passwd);
            System.out.println("openId = " + openId);
//            return new Viewable("/succeed.jsp");
            String passwd_md5 = MD5.GetSaltMD5Code(passwd);
            Parents parents = null;
            if (account != null) {
//
//                if (WechatUtil.isPhone(account)) {
//                    parents = parentsdao.loginCheckByPhone(account, passwd_md5);
//                    System.out.println("手机登录");
//                } else if (WechatUtil.isEmail(account)) {
//                    parents = parentsdao.loginCheckByEmail(account, passwd_md5);
//                    System.out.println("邮箱登录");
//                }
                if(account.contains("@")){
                    parents = parentsdao.loginCheckByEmail(account, passwd_md5);
                    System.out.println("邮箱登录");
                }else{
                    parents = parentsdao.loginCheckByPhone(account, passwd_md5);
                    System.out.println("手机登录");
                }
                if (parents != null) {
                    System.out.println("验证通过");
                    if (openId != null) {
                        parents.setWechat_open_id(openId);
                        if (parentsdao.updateParents(parents)) {
                            System.out.println("绑定成功");
                            return new Viewable("/succeed.jsp");
                        }
                    }
                }
                System.out.println("验证失败");
            }
        }
        return new Viewable("/failed.jsp");
    }

}
