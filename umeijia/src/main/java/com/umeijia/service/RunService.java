package com.umeijia.service;

import com.umeijia.dao.*;
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
import java.util.Date;

@Service
@Path("/run_service")
public class RunService {
    public AdministratorDao getAdministratordao() {
        return administratordao;
    }

    public void setAdministratordao(AdministratorDao administratordao) {
        this.administratordao = administratordao;
    }

    @Autowired
    @Qualifier("administratordao")
    private AdministratorDao administratordao;
    @Autowired
    @Qualifier("agentdao")
    private AgentDao agentdao;
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherdao;
    @Autowired
    @Qualifier("kindergartendao")
    private KinderGartenDao kindergartendao;
    @Autowired
    @Qualifier("cameradao")
    private KinderGartenDao cameradao;


    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2(){

        return "welcom to UMJ server... run service ";
    }


    /***
     * 代理商添加园长
     * curl -X POST -H "Content-Type:application/json" -d {"phone":"13534456644","password":"134df","name":"ltt4aoshou","email":"12345@qq.com","class_id":"1","baby_id":"1","relation":"dad","avatar":"fdef.jpg","gender":"0"}
     * http://127.0.0.1/umeijiaServer/teacher_service/addParents
     * **/
    @Path("/addLeader")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addLeader(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try {
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Agent agent = agentdao.queryAgent(tid);
            if(agent==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","没有该代理商");
                return job_out.toString();
            }
            String phone = job.getString("phone");
            String email = job.getString("email");
            String pwd = job.getString("password");
            String name = job.getString("name");
            long garten_id=job.getLong("garten_id");
            String avatar = job.getString("avatar");
            String wishes = job.getString("wishes"); //园长寄语，老师不传
            String descrip=job.getString("description"); //老师介绍
            //        boolean is_leader = job.getBoolean("leader"); //是否是园长
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            Teacher leader=new Teacher(name,avatar,pwd,garten,phone,descrip,email,true,wishes); //园长
            if(teacherdao.addTeacher(leader)){
                garten.setLeader_wishes(wishes); //更新幼儿园 寄语
                garten.setLeader_id(leader.getId()); //更新幼儿园 园长
                if( kindergartendao.updateKindergarten(garten))
                {
                    job_out.put("resultCode",GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功添加园长");
                    return job_out.toString();
                }
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","添加园长失败");
            return job_out.toString();
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    /***
     *
     *运营人员天添加 幼儿园
     * **/
    @Path("/addGarten")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addGarten(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Agent agent = new Agent(tid);

            String name= job.getString("name");
            String addr= job.getString("addr"); //幼儿园地址
            String contact=job.getString("contact"); //幼儿园联系方式
            String descrip=job.getString("description"); // 幼儿园介绍
            String teacher_presence_imgs=job.getString("teacher_presence_imgs");// 教师风采图片列表
            String garten_instrument_imgs=job.getString("garten_instrument_imgs");// 教学设施列表
            String garten_presence_imgs=job.getString("garten_presence_imgs");//幼儿园图片展示列表*/

            Date date = new Date();
            Kindergarten garten = new Kindergarten(name,addr,contact,descrip,teacher_presence_imgs,garten_instrument_imgs,garten_presence_imgs,agent);

            if(kindergartendao.addKindergarten(garten)){
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功添加幼儿园");
                return  job_out.toString();
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","添加幼儿园失败");
            return job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    /***
     * 管理员运营人员添加代理商
     *
     * **/
    @Path("/addAgent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addAgent(@RequestBody String userinfo, @Context HttpHeaders headers) {

        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
                String tkn = headers.getRequestHeader("tkn").get(0);
                long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
                if(!administratordao.verifyToken(tid,tkn)){ // token验证
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc","token已过期");
                    return job_out.toString();
                }
                String phone= job.getString("phone");
                String pwd= job.getString("pwd");
                String name= job.getString("name");
                String email=job.getString("email");
                String company=job.getString("company");
                float price = (float) job.getDouble("price");
                Date date = new Date();
                Agent agent=new Agent(phone,email,pwd,name,date,company,price);
                if(agentdao.addAgent(agent)){
                    job_out.put("resultCode",GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功添加代理商");
                    return  job_out.toString();
                }
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","添加代理商失败");
                return job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    /***
     * 超级管理员添加管理员(运营人员)
     * ***/
    @Path("/addAdmin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addAdmin(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try {
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!administratordao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Administrator root = administratordao.queryAdministrator(tid);
            if(root==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","只有超级管理员才有该权限");
                return job_out.toString();
            }

            String phone= job.getString("phone");
            String pwd= job.getString("pwd");
            String name= job.getString("name");
            String email=job.getString("email");
            Date date = new Date();
            Administrator admin =  new Administrator(phone,email,pwd,name,date,false);
            if(administratordao.addAdministrator(admin))
            {
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功添加管理员");
                return  job_out.toString();
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","添加管理员失败");
            return job_out.toString();
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }


    @Path("/correctAgentInfo")
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
            if(!agentdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            String name= job.getString("name");
            String company=job.getString("company");
            float price = (float) job.getDouble("price");
            String avata = job.getString("avatar");

            Agent ag=agentdao.queryAgent(tid);
            if(ag!=null)
            {
                ag.setName(name); // 重设相关信息
                ag.setAvarta(avata);
                ag.setCompany_name(company);
                ag.setPrice_rate(price);

                if(agentdao.updateAgent(ag)){
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

    @Path("/correctAgentPwd")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctAgentPwd(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone=job.getString("phone");
            String oldPasswordMD= MD5.GetSaltMD5Code(job.getString("oldPassword"));
            String newPasswordMD=MD5.GetSaltMD5Code(job.getString("newPassword"));
            Agent a=agentdao.queryAgent(phone);

            if(a!=null && newPasswordMD.equals(a.getPwd_md()))
            {
                a.setPwd_md(newPasswordMD);
                a.setToken(MD5.GetSaltMD5Code(newPasswordMD+new Date().toString())); //token重置
                if(agentdao.updateAgent(a)){
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
    @Path("/correctAdminAgentPwd")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctAdminAgentPwd(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!administratordao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone=job.getString("phone");
            String oldPasswordMD= MD5.GetSaltMD5Code(job.getString("oldPassword"));
            String newPasswordMD=MD5.GetSaltMD5Code(job.getString("newPassword"));
            Administrator a=administratordao.queryAdministrator(phone);

            if(a!=null && newPasswordMD.equals(a.getPwd_md()))
            {
                a.setPwd_md(newPasswordMD);
                a.setToken(MD5.GetSaltMD5Code(newPasswordMD+new Date().toString())); //token重置
                if(administratordao.updateAdministrator(a)){
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

}
