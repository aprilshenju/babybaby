package com.umeijia.service;

import com.sun.jersey.multipart.FormDataParam;
import com.umeijia.dao.GartenNewsDao;
import com.umeijia.dao.ParentsDao;
import com.umeijia.dao.StudentDao;
import com.umeijia.dao.TeacherDao;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.MD5;
import com.umeijia.vo.*;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// ip/umeijia/teacher_service/hello

@Service
@Path("/teacher_service")
public class TeacherService {
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    @Autowired
    @Qualifier("studentdao")
    private StudentDao studentdao;
    @Autowired
    @Qualifier("gartennewsdao")
    private GartenNewsDao gartennewsdao;
    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherdao;
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {

        Teacher te = teacherdao.queryTeacher(1);
        Kindergarten garten = te.getKindergarten();

        return "welcome to umj server   "+garten.getName()+"   "+garten.getId();
    }


    /***
     * 添加家长
     * curl -X POST -H "Content-Type:application/json" -d {"phone":"13534456644","password":"134df","name":"ltt4aoshou","email":"12345@qq.com","class_id":"1","baby_id":"1","relation":"dad","avatar":"fdef.jpg","gender":"0"}
     * http://127.0.0.1/umeijiaServer/teacher_service/addParents
     * **/
    @Path("/addParents")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addParents(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try {
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone = job.getString("phone");
            String email = job.getString("email");
            String pwd = job.getString("password");
            String name = job.getString("name");
            long class_id = job.getLong("class_id");
            long stu_id=job.getLong("baby_id");
            String relation = job.getString("relation");
            String avatar = job.getString("avatar");
            int gender=job.getInt("gender"); // 0 男 1 女
            String pwd_md = MD5.GetSaltMD5Code(pwd);
            Student baby = studentdao.queryStudent(stu_id);
            if(baby==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","宝贝id不能为空");
                return job_out.toString();
            }

            Parents p = new Parents(phone,email,name,baby,class_id,pwd_md,relation,avatar);
            if(parentsdao.addParents(p)){
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功添加家长");
            }else{
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","添加家长失败");
            }

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }


    @Path("/imgUpload")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String imgUpload(@FormDataParam("imgData") InputStream ins, @FormDataParam("jsonArgs") String reqJson) {
        String path = "D:/work/";
        File dir = new File("D:/imgs");
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("创建图片目录...");
        }
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();
        String imgName = job.getString("imgName");
        File img = new File(path + "/" + imgName);
        try {
            OutputStream os = new FileOutputStream(img);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            returnJsonObject.put("status", "error");
            return returnJsonObject.toString();
        }
        returnJsonObject.put("status", "success");
        return returnJsonObject.toString();
    }

    @Path("/publishOrUpdateSchoolNews")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String publishOrUpdateSchoolNews(@RequestBody String reqJson) {
        System.out.println("接收到发布或更新校园新闻的请求");
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsoObject = new JSONObject();

        int optType = job.getInt("type");
        long teacherId = job.getLong("teacher_id");
        Teacher teacher = teacherdao.queryTeacher(teacherId);
        Kindergarten kindergarten = teacher.getKindergarten();
        String title = job.getString("title");
        String summary = job.getString("summary");
        String description = job.getString("description");
//        String teacherName = teacher.getName();
        String publishDateStr = job.getString("publishDate");
        String modifyDateStr = job.getString("modifyDate");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        Date publisDate;
        Date modifyDate;
        try {
            publisDate = simpleDateFormat.parse(publishDateStr);
            modifyDate = simpleDateFormat.parse(modifyDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            returnJsoObject.put("resultCode","000002");
            returnJsoObject.put("resultDesc","日期格式有误");
            return returnJsoObject.toString();
        }

        GartenNews gartenNews = new GartenNews();
        gartenNews.setTeacher(teacherdao.queryTeacher(teacherId));
        gartenNews.setKindergarten(kindergarten);
        gartenNews.setTitle(title);
        gartenNews.setSummary(summary);
        gartenNews.setDescription(description);
        gartenNews.setPublishDate(publisDate);
        gartenNews.setModifyDate(modifyDate);
        switch (optType){
            case 0: //发布
                if(gartennewsdao.addGartenNews(gartenNews)){
                    long newsId = gartenNews.getId();
                    returnJsoObject.put("id",newsId);
                    returnJsoObject.put("resultCode","000000");
                    returnJsoObject.put("resultDesc","操作成功");
                }else{
                    returnJsoObject.put("resultCode","000001");
                    returnJsoObject.put("resultDesc","操作失败");
                }
                break;
            case 1: //更新
                long newsId = job.getLong("id");
                gartenNews.setId(newsId);
                if(gartennewsdao.updateGartenNews(gartenNews)){
                    returnJsoObject.put("id",newsId);
                    returnJsoObject.put("resultCode","000000");
                    returnJsoObject.put("resultDesc","操作成功");
                }else{
                    returnJsoObject.put("resultCode","000001");
                    returnJsoObject.put("resultDesc","操作失败");
                }
                break;
            default:
                break;
        }

        return returnJsoObject.toString();
    }
}