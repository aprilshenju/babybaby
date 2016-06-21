package com.umeijia.service;

import com.umeijia.dao.ParentsDao;
import com.umeijia.dao.StudentDao;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.MD5;
import com.umeijia.vo.Parents;
import com.umeijia.vo.Student;
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

@Service
@Path("/parents_service")
public class ParentsService {
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    @Autowired
    @Qualifier("studentdao")
    private StudentDao studentdao;


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

                    Student stu = p.getStudent();
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","登陆成功");
                    job_out.put("tkn",p.getToken());
                    job_out.put("tkn_exptime",p.getExpire().toString());
                    job_out.put("p_id",p.getId());
                    job_out.put("phone",p.getPhone_num());
                    job_out.put("email",p.getEmail());
                    job_out.put("name",p.getName());
                    job_out.put("baby_id",stu.getId());
                    job_out.put("class_id",p.getClass_id());
                    job_out.put("relation",p.getRelationship());
                    job_out.put("is_vip",stu.isVip());
                    job_out.put("vip_start",stu.getVip_start());
                    job_out.put("vip_end",stu.getVip_end());
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

                if(p!=null && newPasswordMD.equals(p.getPwd_md()))
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
        /*        String descrip = job.getString("");
                String wishes = job.getString("wishes")*/

                Parents p=parentsdao.queryParents(phone);

                if(p!=null)
                {
                    p.setName(name);
                    p.setAllow_app_push(app_push);
                    p.setAllow_wechat_push(wechat_push);
                    p.setAvatar_path(avata);
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




}
