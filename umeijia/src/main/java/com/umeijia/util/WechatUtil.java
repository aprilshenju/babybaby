package com.umeijia.util;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by hadoop on 2016/7/1.
 */
public class WechatUtil {
    public static final String appId ="wx10f06daffd439dd0";
    public static  final String appSecret="95c6e3340ab9f093f425d21e40812245";
    public static  final String wechatTokenUrl="https://api.weixin.qq.com/cgi-bin/token";
    public static  final String wechatTemplateMessageUrl="https://api.weixin.qq.com/cgi-bin/message/template/send";
    public static  final String templateId ="cQ-9X5tsfYYn5OHiStGup4EYCB1CP0ROiGrUvbfHMzo";
    public static  String accessToken = null;
    public static String obtainAccessToken() throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(wechatTokenUrl+"?grant_type=client_credential&appid="+appId+"&secret="+appSecret);
        HttpResponse response=httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode== HttpStatus.SC_OK){
            String result=EntityUtils.toString(response.getEntity(),
                    HTTP.UTF_8);
            if(result!=null){
                JSONObject jsonObject = JSONObject.fromObject(result);
                if(jsonObject.containsKey("access_token")){
                    String accessToken = jsonObject.getString("access_token");
                    if(accessToken!=null){
                        return accessToken;
                    }
                }
//                String expires_in = jsonObject.getString("expires_in");
            }

        }
        return null;
    }

    /**
     *
     * @param openId 推送对象的微信openId
     * @param first 模板消息的标题内容
     * @param name  baby的名字
     * @param time  刷卡时间
     * @param location 刷卡地点
     * @param remark 结束语
     * @param maxTimes 最大请求次数,至少2次
     * @throws IOException
     */
    public static void sendTemplateMessage(String openId,String first,String name,String time,String location,String remark,int maxTimes) throws IOException {
        if(maxTimes<=0){
            return;
        }
        HttpClient httpClient = new DefaultHttpClient();
        if(accessToken==null){
            accessToken=obtainAccessToken();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser",openId);
        jsonObject.put("template_id",templateId);
        JSONObject data = new JSONObject();
        JSONObject firstJob = new JSONObject();
        firstJob.put("value",first);
        firstJob.put("color","#173177");

        JSONObject nameJob = new JSONObject();
        nameJob.put("value",name);
        nameJob.put("color","#173177");
        JSONObject timeJob = new JSONObject();
        timeJob.put("value",time);
        timeJob.put("color","#173177");
        JSONObject locationJob = new JSONObject();
        locationJob.put("value",location);
        locationJob.put("color","#173177");
        JSONObject remarkJob = new JSONObject();
        remarkJob.put("value",remark);
        remarkJob.put("color","#173177");
        data.put("first",firstJob);
        data.put("name",nameJob);
        data.put("time",timeJob);
        data.put("location",locationJob);
        data.put("remark",remarkJob);
        jsonObject.put("data",data);
        HttpPost httpPost = new HttpPost(wechatTemplateMessageUrl+"?access_token="+accessToken);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonObject.toString(), HTTP.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            String result = EntityUtils.toString(response.getEntity(),
                    HTTP.UTF_8);
            System.out.println(result);
            if(result!=null){
                JSONObject job =JSONObject.fromObject(result) ;
                if(job.containsKey("errcode")){
                   int errcode = job.getInt("errcode");
                    switch (errcode){
                        case 0: //成功
                            return;
                        case 40014:
                        case 42001://access_token不合法或超时，如过期
                            System.out.println("access_token不合法，如过期,重新获取token");
                            accessToken = obtainAccessToken();
                            sendTemplateMessage(openId, first, name, time, location, remark,maxTimes-1);
                            break;
                        default://其他错误再发一次
                            sendTemplateMessage(openId, first, name, time, location, remark,maxTimes-1);
                            break;
                    }
                }
            }

        } else {
            System.err.println("微信服务器异常");
        }
    }
    public static  void main(String[] args){
        try {
            String openId ="o0JSAwntVdc6xbomFzu7o5ByQvEQ";
            sendTemplateMessage(openId,"亲爱的小山山同学家长，小山山同学有一条考勤信息","小山山","2013年11月18日8时30分","第一幼儿园","谢谢您的关注",3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
