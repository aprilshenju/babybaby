package com.umeijia.service;

import com.sun.jersey.multipart.FormDataParam;
import com.umeijia.dao.GartenNewsDao;
import com.umeijia.dao.TeacherDao;
import com.umeijia.vo.GartenNews;
import com.umeijia.vo.Kindergarten;
import com.umeijia.vo.Teacher;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
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
    @Qualifier("gartennewsdao")
    private GartenNewsDao gartenNewsDao;
    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherDao;
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {

        return "welcome to umj server";
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
        System.out.println("接收到请");
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsoObject = new JSONObject();

        int optType = job.getInt("type");
        long teacherId = job.getLong("teacher_id");
        Teacher teacher = teacherDao.queryTeacher(teacherId);
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
        gartenNews.setTeacher_id(teacherId);
        gartenNews.setKindergarten(kindergarten);
        gartenNews.setTitle(title);
        gartenNews.setSummary(summary);
        gartenNews.setDescription(description);
        gartenNews.setPublishDate(publisDate);
        gartenNews.setModifyDate(modifyDate);
        switch (optType){
            case 0: //发布
                if(gartenNewsDao.addGartenNews(gartenNews)){
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
                if(gartenNewsDao.updateGartenNews(gartenNews)){
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