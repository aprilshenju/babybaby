package com.umeijia.service;

import com.umeijia.dao.*;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.MD5;
import com.umeijia.vo.Class;
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
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

// ip/umeijia/teacher_service/hello

@Service
@Path("/teacher_service")
public class TeacherService {
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;  // 家长 是 由 老师 添加的
    @Autowired
    @Qualifier("studentdao")
    private StudentDao studentdao;
    @Autowired
    @Qualifier("gartennewsdao")
    private GartenNewsDao gartennewsdao;
    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherdao;
    @Autowired
    @Qualifier("kindergartendao")
    private KinderGartenDao kindergartendao;
    @Autowired
    @Qualifier("classdao")
    private  ClassDao classdao;

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

    /***
     * 园长添加普通老师
     * curl -X POST -H "Content-Type:application/json" -d {"phone":"13534456644","password":"134df","name":"ltt4aoshou","email":"12345@qq.com","class_id":"1","baby_id":"1","relation":"dad","avatar":"fdef.jpg","gender":"0"}
     * http://127.0.0.1/umeijiaServer/teacher_service/addParents
     * **/
    @Path("/addTeacher")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addTeacher(@RequestBody String userinfo, @Context HttpHeaders headers) {
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
            Teacher leader= teacherdao.queryTeacher(tid);
            if(leader==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","没有该老师");
                return job_out.toString();
            }
            if(!leader.getIs_leader()){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","只有园长才能添加老师");
                return job_out.toString();
            }
            String phone = job.getString("phone");
            String email = job.getString("email");
            String pwd = job.getString("password");
            pwd=MD5.GetSaltMD5Code(pwd);
            String name = job.getString("name");
            long class_id = job.getLong("class_id");
            long garten_id=job.getLong("garten_id");
            String avatar = job.getString("avatar");
     //       String wishes = job.getString("wishes"); //园长寄语，老师不传
            String descrip=job.getString("description"); //老师介绍
    //        boolean is_leader = job.getBoolean("leader"); //是否是园长
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            Teacher ordTeacher=new Teacher(name,avatar,pwd,garten,phone,descrip,email,false,"-"); //普通老师
            if(teacherdao.addTeacher(ordTeacher)){
                Class cla =classdao.queryClass(class_id);
                if(cla==null){
                    job_out.put("resultCode",GlobalStatus.error.toString());
                    job_out.put("resultDesc","班级id无效");
                    return job_out.toString();
                }
                cla.getTeachers().add(ordTeacher);//对应班级追加相应老师
                if(classdao.updateClass(cla)) //更新班级老师列表，追加新的老师
                {
                    job_out.put("resultCode",GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功添加老师");
                    return  job_out.toString();
                }
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","更新班级老师列表失败");
                return job_out.toString();
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","添加老师失败");
            return job_out.toString();
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    /***
     * 老师登录
     * curl -X POST -H "Content-Type:application/json" -d {"phone":"15608036304","password":"123456","email":""} http://127.0.0.1/umeijiaServer/parents_service/login
     * **/
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            String phone=job.getString("phone");
            String email=job.getString("email");
            String pwd=job.getString("password");
            String pwd_md=MD5.GetSaltMD5Code(pwd);
            Teacher t=null;
            if (!phone.isEmpty()) {
                t = teacherdao.loginCheckByPhone(phone,pwd_md); //登陆成功后，会自动设置token
            } else if(!email.isEmpty()){ // 邮箱登录
                t = teacherdao.loginCheckByEmail(email,pwd_md);
            }
            if(t!=null)
            {
                Set<Class> cla_set=t.getClasses();
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc","登陆成功");
                job_out.put("tkn",t.getToken());
                job_out.put("tkn_exptime",t.getExpire().toString());
                job_out.put("t_id",t.getId());
                job_out.put("phone",t.getPhone_num());
                job_out.put("email",t.getEmail());
                job_out.put("name",t.getName());
                job_out.put("avatar",t.getAvatar_path());
                Iterator<Class> it=cla_set.iterator();
                String cla_ids="";
                String cla_names="";
                while (it.hasNext()){
                    Class cla = it.next();
                    cla_ids+=cla.getId();
                    cla_ids+=";";
                    cla_names+=cla.getName();
                    cla_names+=";";
                }
                job_out.put("class_ids",cla_ids);
                job_out.put("class_names",cla_names);  // 分号 隔开 ，班级 ids names列表
                job_out.put("is_leader",t.getIs_leader());
                return  job_out.toString();
            }else{
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","登录用户名、邮箱或密码无效");
            }
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }

    @Path("/correctPwd")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctPwd(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone=job.getString("phone");
            String oldPasswordMD=MD5.GetSaltMD5Code(job.getString("oldPassword"));
            String newPasswordMD=MD5.GetSaltMD5Code(job.getString("newPassword"));
            Teacher t=teacherdao.queryTeacher(phone);

            if(t!=null && oldPasswordMD.equals(t.getPwd_md()))
            {
                t.setPwd_md(newPasswordMD);
                t.setToken(MD5.GetSaltMD5Code(newPasswordMD+new Date().toString())); //token重置
                if(teacherdao.updateTeacher(t)){
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功修改密码");
                    return  job_out.toString(); //成功
                }
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","修改密码失败");
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }

    @Path("/correctTInfo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctPInfo(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone=job.getString("phone");
            String name=job.getString("name");
            String avata=job.getString("avatar");
            String descrip = job.getString("description");
            String wishes = job.getString("wishes");
            Teacher t=teacherdao.queryTeacher(phone);
            if(t!=null)
            {
                t.setName(name); // 重设相关信息
                t.setAvatar_path(avata);
                t.setDescription(descrip);
                t.setWishes(wishes);
                if(teacherdao.updateTeacher(t)){
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功修改信息");
                    return  job_out.toString(); //成功
                }
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","修改信息失败");
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }



    /*@Path("/imgUpload")
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
    }*/

}