package com.umeijia.service;

import com.umeijia.dao.*;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.LockerLogger;
import com.umeijia.util.MD5;
import com.umeijia.vo.*;
import com.umeijia.vo.Class;
import net.sf.json.JSONArray;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Path("/parents_service")
public class ParentsService {
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    @Autowired
    @Qualifier("studentdao")
    private StudentDao studentdao;
    @Autowired
    @Qualifier("classdao")
    private  ClassDao classdao;

    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherdao;
    @Autowired
    @Qualifier("kindergartendao")
    private KinderGartenDao kindergartendao;


    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2(){

        Parents p = parentsdao.queryParents(1);
        Student stu=p.getStudent();
        String s = p.getName()+p.isAllow_app_push()+"  vip "+stu.isVip();
        return "welcom to UMJ parents service...."+s;
    }


        /***
         * 用户登录
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
                Parents p=null;
                if (!phone.isEmpty()) {
                    p = parentsdao.loginCheckByPhone(phone,pwd_md);
                } else if(!email.isEmpty()){ // 邮箱登录
                    p = parentsdao.loginCheckByEmail(email,pwd_md);
                }
                if(p!=null)
                {
                    //幼儿园和班级有效性判断
                    Kindergarten garten = kindergartendao.queryKindergarten(p.getGarten_id());
                    if(garten==null||garten.isValid()==false){
                        job_out.put("resultCode", GlobalStatus.error.toString());
                        job_out.put("resultDesc","所属幼儿园已无效");
                        return  job_out.toString();
                    }
                    Class cla = classdao.queryClass(p.getClass_id());
                    if(cla==null||cla.isValid()==false){
                        job_out.put("resultCode", GlobalStatus.error.toString());
                        job_out.put("resultDesc","所属班级已无效");
                        return  job_out.toString();
                    }

                    Student stu = p.getStudent();

                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","登陆成功");
                    job_out.put("tkn",p.getToken());
                    job_out.put("tkn_exptime",p.getExpire().toString());
                    job_out.put("p_id",p.getId());
                    job_out.put("schoolId",p.getGarten_id());
                    job_out.put("phone",p.getPhone_num());
                    job_out.put("email",p.getEmail());
                    job_out.put("name",p.getName());
                    job_out.put("baby_id",stu.getId());
                    job_out.put("class_id",p.getClass_id());
                    job_out.put("class_name",p.getClass().getName());
                    job_out.put("relation",p.getRelationship());
                    job_out.put("is_vip",stu.isVip());
                    job_out.put("vip_start",stu.getVip_start().toString());
                    job_out.put("vip_end",stu.getVip_end().toString());
                    job_out.put("avatar",p.getAvatar_path());
                    job_out.put("app_push",p.isAllow_app_push());
                    job_out.put("wechat_push",p.isAllow_wechat_push());

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
                if(!parentsdao.verifyToken(tid,tkn)){ // token验证
                    job_out.put("resultCode",GlobalStatus.error.toString());
                    job_out.put("resultDesc","token已过期");
                    return job_out.toString();
                }

                String phone=job.getString("phone");
                String oldPasswordMD=MD5.GetSaltMD5Code(job.getString("oldPassword"));
                String newPasswordMD=MD5.GetSaltMD5Code(job.getString("newPassword"));
                Parents p=parentsdao.queryParents(phone);

                if(p!=null && oldPasswordMD.equals(p.getPwd_md()))
                {
                    p.setPwd_md(newPasswordMD);
                    p.setToken(MD5.GetSaltMD5Code(newPasswordMD+new Date().toString())); //token重置
                    if(parentsdao.updateParents(p)){
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

        @Path("/correctPInfo")
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
                if(!parentsdao.verifyToken(tid,tkn)){ // token验证
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc","token已过期");
                    return job_out.toString();
                }

                String phone=job.getString("phone");
                String name=job.getString("name");
                boolean app_push=job.getBoolean("app_push");
                boolean wechat_push=job.getBoolean("wechat_push");
                String avata=job.getString("avatar");
                String email = job.getString("email");
        /*        String descrip = job.getString("");
                String wishes = job.getString("wishes")*/

                Parents p=parentsdao.queryParents(phone);

                if(p!=null)
                {
                    String old_phone = p.getPhone_num();

                    p.setName(name);
                    p.setAllow_app_push(app_push);
                    p.setAllow_wechat_push(wechat_push);
                    p.setAvatar_path(avata);
                    p.setPhone_num(phone);
                    p.setEmail(email);

                    if(!phone.equals(old_phone)){
                        //更新通信录
                        UpdateParentContractsThread thread = new UpdateParentContractsThread(p.getClass_id());
                        thread.start();
                    }

                    if(parentsdao.updateParents(p)){
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

    @Path("/getBabyInfo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getBabyInfo(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!parentsdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            Parents p = parentsdao.queryParents(tid);
            if(p==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效家长id");
                return job_out.toString();
            }
            Student stu = p.getStudent(); //获取 当前家长的宝�
            if(stu==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","当前家长还没宝贝");
                return job_out.toString();
            }
            job_out.put("baby_id",stu.getId());
            job_out.put("name",stu.getName());
            job_out.put("nick_name",stu.getNick_name());
            job_out.put("avatar",stu.getAvatar_path());
            job_out.put("gender",stu.getGender());
            job_out.put("birthday",stu.getBirthday());
            job_out.put("weight",stu.getWeight());
            job_out.put("height",stu.getHeight()); //baby相关基本信息
            job_out.put("relation",p.getRelationship()); //宝贝关系

        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }

    @Path("/getClassTeacherContacts")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getClassTeacherContacts(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!parentsdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            Parents p = parentsdao.queryParents(tid);
            if(p==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效家长id");
                return job_out.toString();
            }
            Class cla = classdao.queryClass(p.getClass_id());// 获取家长对应的班级
            if(cla==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","当前家长没有对应的班级");
                return job_out.toString();
            }
            Set<Teacher> teachers = cla.getTeachers();
            if(teachers==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","当前班级没有老师");
                return job_out.toString();
            }

            JSONArray ja = new JSONArray();
            Iterator<Teacher> it = teachers.iterator();
            while (it.hasNext()){
                Teacher t = (Teacher) it.next();
                JSONObject jo=new JSONObject();
                jo.put("name",t.getName());
                jo.put("phoneNum",t.getPhone_num());
                jo.put("avatar", t.getAvatar_path());
                jo.put("className",cla.getName());
                ja.add(jo);
            }
            job_out.put("resultCode", GlobalStatus.succeed.toString());
            job_out.put("resultDesc","成功获取班级老师通信录");
            job_out.put("data",ja.toString());

        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }


    @Path("/correctBabyInfo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctBabyInfo(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!parentsdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            long baby_id=job.getLong("baby_id"); // 后去 宝贝id
            String name = job.getString("name");
            String nick_name = job.getString("nick_name");
            long class_id = job.getLong("class_id");
            String avatar = job.getString("avatar");
            String gender=job.getString("gender");
            String birth_str=job.getString("birthday");
            int weight=job.getInt("weight");
            int height = job.getInt("height");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            Date birth = sdf.parse(birth_str);
            Student stu = studentdao.queryStudent(baby_id);
            if(stu==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","宝贝id无效");
                return  job_out.toString();
            }
            Class cla = classdao.queryClass(class_id);
            if(cla==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","新班级id无效");
                return  job_out.toString();
            }

            //设置宝贝信息为新设置的�
            stu.setName(name);
            stu.setNick_name(nick_name);
            stu.setAvatar_path(avatar);
            stu.setCla(cla);
            stu.setGender(gender);
            stu.setBirthday(birth);
            stu.setWeight(weight);
            stu.setHeight(height);
            if(studentdao.addStudent(stu)){
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功修改宝贝信息");
                return  job_out.toString();
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","修改信息失败");
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        } catch (ParseException e) {
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","生日解析失败");
        }
        return job_out.toString();
    }


    /***
     * 当一个班添加一个家长或一个老师时，更新 幼儿园的老师通信录和班级家长通信录
     *
     *更新一个班级的 家长通信录
     * **/
    class UpdateParentContractsThread extends Thread {
        long cla_id=0;
        public UpdateParentContractsThread(long class_id) {
            cla_id=class_id;
        }
        public void run() {
            Class cla = classdao.queryClass(cla_id);
            if(cla==null)   return ;
            List<Parents> parents= parentsdao.getParentsByClass(cla_id);
            String parents_contact="";
            Iterator<Parents>it = parents.iterator();
            while (it.hasNext()){
                Parents p =it.next();
                parents_contact+=p.getStudent().getName()+"-"+p.getRelationship()+
                        "-"+p.getName()+"-"+p.getPhone_num()+"-"+p.getAvatar_path()+"-"+cla.getName();
                // baby名称-baby妈妈-家长名字-电话-头像路径-
                parents_contact+=";"; //下一条记录
            }
            cla.setParents_contacts(parents_contact);
            classdao.updateClass(cla);
        }
    }


}
