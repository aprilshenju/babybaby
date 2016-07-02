package com.umeijia.service;

import com.sun.jersey.api.view.Viewable;
import com.umeijia.wechat.TextMessage;
import com.umeijia.wechat.WechatUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    private final String serverIp = "http://xiaoxiaomi.imwork.net/umeijiaServer/";
    private final String bindUrl = serverIp + "rest/wechat_service/login";

    @Path("/login")
    @GET
    public Viewable login(@Context HttpServletRequest request, @Context HttpServletResponse response){
        if(request!=null){
            String openId = request.getParameter("openId");
            HttpSession session= request.getSession();
            session.setAttribute("openId",openId);
            System.out.println("跳转到绑定界面");
            return new Viewable("/bind.jsp");
        }else{
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
                            textMessage.setContent(bindUrl + "?openId = " + fromUserName);
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

    @Path("/bindwechat")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Viewable bindWechat(@FormParam("name") String account, @FormParam("password") String passwd, @FormParam
            ("openId") String openId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
        System.out.println("收到绑定的post请求");
        if (request != null) {
            System.out.println("account = " + account);
            System.out.println("passwd = " + passwd);
            System.out.println("openId = " + openId);
        }
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new Viewable("/succeed.jsp");
    }

//    /**
//     * 文件上传
//     * 单个文件上传
//     *
//     * @param ins
//     * @param reqJson
//     * @return
//     */
//    @Path("/fileUpload")
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public String fileUpload(@FormDataParam("fileData") InputStream ins, @FormDataParam("jsonArgs") String reqJson) {
//        System.out.println("收到文件上传的请求...");
//        JSONObject job = JSONObject.fromObject(reqJson);
//        JSONObject returnJsonObject = new JSONObject();
//        if (job == null) {
//            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//            returnJsonObject.put("resultDesc", "获取json失败");
//            return returnJsonObject.toString();
//        }
//        String checkReqJson = judgeValidationOfInputJson(reqJson, "fileType", "recordId", "gardenId", "classId",
//                "babyId", "interfaceType", "fileName");
//        if (!checkReqJson.equals("")) {
//            return checkReqJson;
//        }
//
//        int fileType = -1;
//        long recordId = -1;
//        long gardenId = -1;
//        long classId = -1;
//        long babyId = -1;
//        String interfaceType;
//        String fileName;
//
//        fileType = job.getInt("fileType");
//        recordId = job.getLong("recordId");
//        gardenId = job.getLong("gardenId");
//        classId = job.getLong("classId");
//        babyId = job.getLong("babyId");
//        interfaceType = job.getString("interfaceType");
//        fileName = job.getString("fileName");
//        //根据接口类型处理文件上传
//        switch (interfaceType) {
//            case "publishOrUpdateSchoolNews"://发布编辑校园新闻接口
//                GartenNews gartenNews = gartennewsdao.queryGartenNews(recordId);
//                if (gartenNews != null) {
//                    filePath = "/garden/" + gardenId + "/news/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = gartenNews.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//
//                    gartenNews.setImage_urls(imgUrls);
//                    //更新表中的imgUrls字段
//                    if (gartennewsdao.updateGartenNews(gartenNews)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的新闻记录");
//                }
//                break;
//            case "publishOrUpdateClassNotification"://发布或更新班级通知
//                ClassNotification classNotification = classnotificationdao.queryClassNotification(recordId);
//                if (classNotification != null) {
//                    filePath = "/garden/" + gardenId + "/class/" + classId + "/notification/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = classNotification.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    classNotification.setImage_urls(imgUrls);
//                    //更新数据库表中的imgUrls字段
//                    if (classnotificationdao.updateClassNotification(classNotification)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的班级通知");
//                }
//                break;
//            case "addHomeWork"://新增班级作业接口
//                HomeWork homeWork = homeworkdao.queryHomeWork(recordId);
//                if (homeWork != null) {
//                    filePath = "/garden/" + gardenId + "/class/" + classId + "/homework/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = homeWork.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    homeWork.setImage_urls(imgUrls);
//                    if (homeworkdao.updateHomeWork(homeWork)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的班级作业");
//                }
//                break;
//            case "publishClassActivity"://发布班级活动接口
//                ClassActivity classActivity = classactivitydao.queryClassActivity(recordId);
//                if (classActivity != null) {
//                    filePath = "/garden/" + gardenId + "/class/" + classId + "/activity/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = classActivity.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    classActivity.setImage_urls(imgUrls);
//                    if (classactivitydao.updateClassActivity(classActivity)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的班级活动");
//                }
//                break;
//            case "addBabyShowTime"://新增宝贝动态接口
//                BabyShowtime babyShowtime = babyshowtimedao.queryBabyShowtime(recordId);
//                if (babyShowtime != null) {
//                    switch (fileType) {
//                        case 1://图片
//                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
//                                    "/showTime/img";
//                            handlerFileUpload(filePath, fileName, ins, true);
//                            break;
//                        case 2://视频
//                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
//                                    "/showTime/video";
//                            handlerFileUpload(filePath, fileName, ins, false);
//                            break;
//                        default://未知的文件类型
//                            returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
//                            returnJsonObject.put("resultDesc", "未知的文件类型");
//                            return returnJsonObject.toString();
//                    }
//
//                    imgUrls = babyShowtime.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    babyShowtime.setImage_urls(imgUrls);
//                    if (babyshowtimedao.updateBabyShowtime(babyShowtime)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的宝贝动态");
//                }
//                break;
//            case "addOrEditFootPrint"://新增或编辑宝贝足迹接口
//                BabyFootPrint babyFootPrint = babyfootprintdao.queryBabyFootPrint(recordId);
//                if (babyFootPrint != null) {
//                    switch (fileType) {
//                        case 1://图片
//                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
//                                    "/footprint/img";
//                            handlerFileUpload(filePath, fileName, ins, true);
//                            break;
//                        case 2://视频
//                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
//                                    "/footprint/video";
//                            handlerFileUpload(filePath, fileName, ins, true);
//                            break;
//                        default:
//                            returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
//                            returnJsonObject.put("resultDesc", "未知的文件类型");
//                            return returnJsonObject.toString();
//                    }
//                    imgUrls = babyFootPrint.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    babyFootPrint.setImage_urls(imgUrls);
//                    if (babyfootprintdao.updateBabyFootPrint(babyFootPrint)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的宝贝足迹");
//                }
//                break;
//            case "addOrEditBabyFood"://新增或编辑宝贝饮食接口
//                FoodRecord foodRecord = foodrecorddao.queryFoodRecord(recordId);
//                if (foodRecord != null) {
//                    filePath = "/garden/" + gardenId + "/class/" + classId + "/food/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = foodRecord.getImage_urls();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    foodRecord.setImage_urls(imgUrls);
//                    if (foodrecorddao.updateFoodRecord(foodRecord)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的宝贝饮食");
//                }
//                break;
//            case "addCheckinRecord"://新增宝贝考勤接口
//                CheckinRecords checkinRecords = checkinrecorddao.queryCheckinRecords(recordId);
//                if (checkinRecords != null) {
//                    filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/checkin/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = checkinRecords.getImage_path();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    checkinRecords.setImage_path(imgUrls);
//                    if (checkinrecorddao.updateCheckinRecords(checkinRecords)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的考勤记录");
//                }
//                break;
//            case "uploadAvatar"://上传头像接口
//                filePath = "/avatar";
//                File dir = new File(baseDir + filePath);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                    System.out.println("创建图片目录:" + dir.getPath());
//                }
//                //存储原图
//                imgPath = dir.getPath() + "/" + fileName;
//                storeImg(imgPath, ins);
//                returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                returnJsonObject.put("resultDesc", "操作成功");
//                break;
//            case "addTeacherPresence"://上传教师风采图片
//                Kindergarten kindergarten = kindergartendao.queryKindergarten(recordId);
//                if (kindergarten != null) {
//                    filePath = "/garden/" + gardenId + "/teacher_presence/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = kindergarten.getTeacher_presence_imgs();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    kindergarten.setTeacher_presence_imgs(imgUrls);
//                    if (kindergartendao.updateKindergarten(kindergarten)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的幼儿园");
//                }
//                break;
//            case "addFacilities"://上传幼儿园设施图片
//                Kindergarten kindergarten1 = kindergartendao.queryKindergarten(recordId);
//                if (kindergarten1 != null) {
//                    filePath = "/garden/" + gardenId + "/facilities/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = kindergarten1.getGarten_instrument_imgs();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    kindergarten1.setGarten_instrument_imgs(imgUrls);
//                    if (kindergartendao.updateKindergarten(kindergarten1)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的幼儿园");
//                }
//                break;
//            case "addGardenPresence"://上传幼儿园设施图片
//                Kindergarten kindergarten2 = kindergartendao.queryKindergarten(recordId);
//                if (kindergarten2 != null) {
//                    filePath = "/garden/" + gardenId + "/garden_presence/img";
//                    handlerFileUpload(filePath, fileName, ins, true);
//                    imgUrls = kindergarten2.getGarten_presence_imgs();
//                    if (imgUrls != null) {
//                        if (imgUrls.length() == 0) {
//                            imgUrls = fileName;
//                        } else {
//                            imgUrls += ";" + fileName;
//                        }
//                    } else {
//                        imgUrls = fileName;
//                    }
//                    kindergarten2.setGarten_presence_imgs(imgUrls);
//                    if (kindergartendao.updateKindergarten(kindergarten2)) {
//                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
//                        returnJsonObject.put("resultDesc", "操作成功");
//                    } else {
//                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                        returnJsonObject.put("resultDesc", "更新数据库失败");
//                    }
//                } else {
//                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
//                    returnJsonObject.put("resultDesc", "没有找到对应的幼儿园");
//                }
//                break;
//            default:
//                returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
//                returnJsonObject.put("resultDesc", "未知的接口类型");
//                break;
//        }
//        return returnJsonObject.toString();
//    }
}
