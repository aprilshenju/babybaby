package com.umeijia.service;

import com.umeijia.dao.*;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.MD5;
import com.umeijia.vo.*;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Service
@Path("/smsMessage_service")
public class SMSMessageService {
    private static String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";
    private static String URI_TPL_SEND_SMS = "https://sms.yunpian.com/v2/sms/tpl_single_send.json";
    private static String ENCODING = "UTF-8";
    @Autowired
    @Qualifier("smsmessagedao")
    private SMSMessageDao smsmessagedao;
    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherdao;
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    @Autowired
    @Qualifier("agentdao")
    private AgentDao agentdao;
    @Autowired
    @Qualifier("administratordao")
    private AdministratorDao administratordao;


    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2() {
        return "welcom to UMJ smsMessage service....";
    }


    /**
     * 用户点击重置密码
     * <p/>
     * *
     */
    @Path("/requestToResetSMSMessage")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String RequestToResetSMSMessage(@RequestBody String messageInfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(messageInfo);
        JSONObject job_out = new JSONObject();
        try {
            String phoneNum = job.getString("phoneNum");
            String verifyCode = GenerateRandomNumber();
            SMSMessage sMSMessage = smsmessagedao.querySMSMessageByPhone(phoneNum);

            if (sMSMessage == null) { //没有记录就添加，始终为每个手机号保留一条记录
                sMSMessage = new SMSMessage();
                sMSMessage.setPhoneNum(phoneNum);
                sMSMessage.setLastRequestTime(new Date());
                sMSMessage.setLastInputVerifyCodeTime(new Date());
                sMSMessage.setVerifyCode(verifyCode);
                sMSMessage.setUnUsedOne("");
                sMSMessage.setUnUsedTwo("");
                sMSMessage.setValidTimeDeadLine(new Date(new Date().getTime() + 1200000)); //2分钟有效期
                callThirdPartySmsInterface(phoneNum, verifyCode);  //调用三方给用户发送短信
                smsmessagedao.addSMSMessage(sMSMessage);
            } else { //有记录直接更新
                if (new Date().getTime() < (sMSMessage.getLastRequestTime().getTime() + 5000)) { //防止不断请求,5秒内不能重复请求
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc", "请求太频繁");
                    return job_out.toString();
                }
                sMSMessage.setPhoneNum(phoneNum);
                sMSMessage.setLastRequestTime(new Date());
                sMSMessage.setLastInputVerifyCodeTime(new Date());
                sMSMessage.setVerifyCode(verifyCode);
                sMSMessage.setUnUsedOne("");
                sMSMessage.setUnUsedTwo("");
                sMSMessage.setValidTimeDeadLine(new Date(new Date().getTime() + 1200000)); //2分钟有效期
                callThirdPartySmsInterface(phoneNum, verifyCode);//调用三方给用户发送短信
                smsmessagedao.updateAgent(sMSMessage);
            }
            job_out.put("resultCode", GlobalStatus.succeed.toString());
            job_out.put("resultDesc", "操作成功");
        } catch (JSONException e) {
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc", "无记录");
            return job_out.toString();  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }


    /**
     * 调用第三方给用户发送短信
     * <p/>
     * *
     */

    public int callThirdPartySmsInterface(String phoneNum, String verifyCode) {
        try {

            String apikey = "cd7101046f359ea766471e1851646067";
            String mobile = phoneNum;
            String text = "【幼美加】您的验证码是 " + verifyCode;
            System.out.println(sendSms(apikey, text, mobile));
//            System.out.println(tpl_value);
//            System.out.println(tplSendSms(apikey, tpl_id, tpl_value, mobile));
        } catch (Exception e) {

        }
        return -1;
    }


    public static String sendSms(String apikey, String text, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }


    /**
     * 通过模板发送短信(不推荐)
     *
     * @param apikey    apikey
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     */

    public String tplSendSms(String apikey, long tpl_id, String tpl_value, String mobile) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("apikey", apikey);
            params.put("tpl_id", String.valueOf(tpl_id));
            params.put("tpl_value", tpl_value);
            params.put("mobile", mobile);
            return post(URI_TPL_SEND_SMS, params);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalStatus.error.toString();
        }
    }

    /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

    public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    org.apache.http.NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(response!=null){
                    response.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }


    /**
     * 用户收到验证码后重置密码
     * <p/>
     * *
     */
    @Path("resetSMSMessage")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String resetSMSMessage(@RequestBody String messageInfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(messageInfo);
        JSONObject job_out = new JSONObject();
        try {
            String phoneNum = job.getString("phoneNum");
            String verifyCode = job.getString("verifyCode");
            String newPassWord = job.getString("newPassword");
            int roleType = job.getInt("roleType");
            int roleId = job.getInt("roleId");
            SMSMessage sMSMessage = smsmessagedao.querySMSMessageByPhone(phoneNum);
            if (sMSMessage == null) {
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc", "无记录");
            } else {
                if (new Date().getTime() > sMSMessage.getValidTimeDeadLine().getTime()) {
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc", "验证码过期");
                    return job_out.toString();
                } else if (new Date().getTime() < (sMSMessage.getLastInputVerifyCodeTime().getTime() + 5000)) { //防止不断请求,5秒内不能重复请求
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc", "请求太频繁");
                    return job_out.toString();
                }

                if (!sMSMessage.getVerifyCode() .equals( verifyCode) ){
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc", "验证码错误");
                    return job_out.toString();
                } else {
                    switch (roleType) {
                        case 1:
                        case 2:
                            Teacher teacher = teacherdao.queryTeacher(roleId);
                            teacher.setPwd_md(MD5.GetSaltMD5Code(newPassWord));
                            teacherdao.updateTeacher(teacher);
                            break;
                        case 3:
                            Parents parents = parentsdao.queryParents(roleId);
                            parents.setPwd_md(MD5.GetSaltMD5Code(newPassWord));
                            parentsdao.updateParents(parents);
                            break;
                        case 4:
                            Agent agent = agentdao.queryAgent(roleId);
                            agent.setPwd_md(MD5.GetSaltMD5Code(newPassWord));
                            agentdao.updateAgent(agent);
                            break;
                        case 5:
                            Administrator administrator = administratordao.queryAdministrator(roleId);
                            administrator.setPwd_md(MD5.GetSaltMD5Code(newPassWord));
                            administratordao.updateAdministrator(administrator);
                            break;
                    }
                    sMSMessage.setLastInputVerifyCodeTime(new Date()); //更新上一次请求时间
                    smsmessagedao.updateAgent(sMSMessage);
                }
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc", "操作成功");
            }
        } catch (JSONException e) {
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc", "无记录");
            return job_out.toString();  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }


    /**
     * 生成6位随机验证码
     *
     * @return
     */
    public String GenerateRandomNumber() {
        String result = "";
        for (int i = 0; i < 6; i++) {
            Random rd = new Random();
            result += "" + rd.nextInt(10);
        }
        return result;
    }


}
