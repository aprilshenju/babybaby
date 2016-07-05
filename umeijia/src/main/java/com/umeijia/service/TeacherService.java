package com.umeijia.service;


import com.umeijia.dao.*;
import com.umeijia.enums.OptEnum;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.LogUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// ip/umeijia/teacher_service/hello

@Service
@Path("/teacher_service")
public class TeacherService {

    @Autowired
    @Qualifier("administratordao")
    private AdministratorDao administratordao;
    @Autowired
    @Qualifier("agentdao")
    private AgentDao agentdao;

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
    @Autowired
    @Qualifier("dailylogdao")
    private DailyLogDao dailylogdao;
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {

        Teacher te = teacherdao.queryTeacher(1);
        Kindergarten garten = te.getKindergarten();

        return "welcome to umj server   "+garten.getName()+"   "+garten.getId();
    }


    @Path("/addStudent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addStudent(@RequestBody String userinfo, @Context HttpHeaders headers) {
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
            String name = job.getString("name");
            String nick_name = job.getString("nick_name");
            long roleId =job.getLong("roleId");
            int roleType= job.getInt("roleType");
            long class_id = job.getLong("class_id");
            String avatar = job.getString("avatar");
            String gender=job.getString("gender");
            String date_str=job.getString("birthday");
            int weight=job.getInt("weight");
            int height = job.getInt("height");
            boolean isvip=job.getBoolean("vip");
            String vip_begin=job.getString("vip_begin");
            String vip_end=job.getString("vip_end");
            String entrence=job.getString("entrence");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            Date date = sdf.parse(date_str);
            Date date_vip_begin=sdf.parse(vip_begin);
            Date date_vip_end=sdf.parse(vip_end);
            Date date_entrence = sdf.parse(entrence);

            Class cla = classdao.queryClass(class_id);
            if(cla==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","班级不存在");
                return job_out.toString();
            }
            Student stu = new Student(name,nick_name,gender,date,height,weight,avatar,cla,isvip,date_vip_begin,date_vip_end,date_entrence);
            if(studentdao.addStudent(stu)){
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("baby_id",stu.getId()); //返回 baby_id
                job_out.put("resultDesc","成功添加宝贝");
                //添加日志
                DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),roleType,roleId, OptEnum.insert.toString(),"添加学生","学生id:"+String.valueOf(stu.getId()));
                dailylogdao.addDailyLog(dailyLog);
                return  job_out.toString();
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","添加宝贝失败");
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        } catch (ParseException e) {
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","生日解析失败");
        }
        return job_out.toString();
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
   /*         String pwd = job.getString("password");*/
            String name = job.getString("name");
            long roleId =job.getLong("roleId");
            int roleType= job.getInt("roleType");
            long class_id = job.getLong("class_id");
            long stu_id=job.getLong("baby_id");
            String relation = job.getString("relation");
            String avatar = job.getString("avatar");
            String gender=job.getString("gender");
            //用户账号是否已注册判断
            if(isPhoneOrEmailExist(phone,email)){
                //已经存在
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","用户手机号或邮箱已存在");
                return job_out.toString();
            }

            Class cla = classdao.queryClass(class_id);
            if(cla==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效班级id");
                return job_out.toString();
            }

            String pwd = SMSMessageService.GenerateRandomNumber();
            String org_pwd=pwd;
            String pwd_md = MD5.GetSaltMD5Code(pwd);
            Student baby = studentdao.queryStudent(stu_id);
            if(baby==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","宝贝id不能为空");
                return job_out.toString();
            }

            Parents p = new Parents(phone,email,name,baby,class_id,cla.getGarten().getId(),pwd_md,relation,avatar,gender);
            if(parentsdao.addParents(p)){
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功添加家长");
                //添加日志
                DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),roleType,roleId, OptEnum.insert.toString(),"添加家长","家长id："+String.valueOf(p.getId()));
                dailylogdao.addDailyLog(dailyLog);
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("phoneNum",phone);
                map.put("verifyCode",org_pwd);
                map.put("type",2);
                SMSMessageService .cmds.add(map);
                // 添加成功，后台异步更新通讯录
                UpdateParentContractsThread th_update=new UpdateParentContractsThread(class_id);
                th_update.start();

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
     * 添加班级
     * curl -X POST -H "Content-Type:application/json" -d {"phone":"13534456644","password":"134df","name":"ltt4aoshou","email":"12345@qq.com","class_id":"1","baby_id":"1","relation":"dad","avatar":"fdef.jpg","gender":"0"}
     * http://127.0.0.1/umeijiaServer/teacher_service/addParents
     * **/
    @Path("/addClass")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addClass(@RequestBody String userinfo, @Context HttpHeaders headers) {
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
            long roleId =job.getLong("roleId");
            int roleType= job.getInt("roleType");
                String name = job.getString("name");
                String introduciton=job.getString("introduciton");
                long garten_id=job.getLong("garten");
                String schedule=job.getString("schedule");
                String teacher_ids=job.getString("teachers"); // ; 号隔离的 id列表
                Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
                if(garten==null){
                    job_out.put("resultCode",GlobalStatus.error.toString());
                    job_out.put("resultDesc","无此幼儿园");
                    return  job_out.toString();
                }

                Class cla = new Class(name,introduciton,schedule,"","",garten);
                if(!teacher_ids.isEmpty()){
                    String [] id_arr = teacher_ids.split(";");
                    for (int i=0;i<id_arr.length;i++){
                        long t_id=Long.parseLong(id_arr[i]);
                        Teacher t=teacherdao.queryTeacher(t_id);
                        if(t!=null){
                            cla.getTeachers().add(t);
                        }
                    }
                    if(classdao.addClass(cla)){
                        job_out.put("resultCode",GlobalStatus.succeed.toString());
                        job_out.put("resultDesc","成功添加班级");
                        //添加日志
                        DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),roleType,roleId, OptEnum.insert.toString(),"添加班级","班级id:"+String.valueOf(cla.getId()));
                        dailylogdao.addDailyLog(dailyLog);
                        return job_out.toString();
                    }
                }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","添加班级失败");
            return job_out.toString();
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
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
            long roleId =job.getLong("roleId");
            int roleType= job.getInt("roleType");
            String phone = job.getString("phone");
            String email = job.getString("email");
         /*   String pwd = job.getString("password");
            pwd=MD5.GetSaltMD5Code(pwd);*/
            String name = job.getString("name");
/*            long class_id = job.getLong("class_id");*/
            long garten_id=job.getLong("garten_id");
            String avatar = job.getString("avatar");
     //       String wishes = job.getString("wishes"); //园长寄语，老师不传
            String descrip=job.getString("description"); //老师介绍
            String gender=job.getString("gender");
    //        boolean is_leader = job.getBoolean("leader"); //是否是园长
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            String pwd = SMSMessageService.GenerateRandomNumber(); //获取随即密码
            String org_pwd=pwd;
            pwd=MD5.GetSaltMD5Code(pwd); //计算盐值
            //用户账号是否已注册判断
            if(isPhoneOrEmailExist(phone,email)){
                //已经存在
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","用户手机号或邮箱已存在");
                return job_out.toString();
            }

            Teacher ordTeacher=new Teacher(name,avatar,pwd,garten,phone,descrip,email,false,"-",gender); //普通老师
            if(teacherdao.addTeacher(ordTeacher)){
            /*    Class cla =classdao.queryClass(class_id);
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
                }*/
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("phoneNum",phone);
                map.put("verifyCode",org_pwd);
                map.put("type",2);
                SMSMessageService .cmds.add(map);

                UpdateTeacherContractsThread thread = new UpdateTeacherContractsThread(garten_id);
                thread.start();
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功添加老师");
                //添加日志
                DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),roleType,roleId, OptEnum.insert.toString(),"添加老师","老师id:"+String.valueOf(ordTeacher.getId()));
                dailylogdao.addDailyLog(dailyLog);
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
                job_out.put("gender",t.getGender());
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
            String email = job.getString("email");


            Teacher t=teacherdao.queryTeacher(tid);
            if(t!=null)
            {
                t.setName(name); // 重设相关信息
                t.setAvatar_path(avata);
                t.setDescription(descrip);
                t.setWishes(wishes);
                t.setPhone_num(phone);
                t.setEmail(email);
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

    @Path("/invalidTeacher")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invalidTeacher(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long tid = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(tid,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }

            Teacher leader = teacherdao.queryTeacher(tid);
            if(leader==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","非法操作人员");
                return job_out.toString();
            }
            if(leader.getIs_leader()==false){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","只有园长才能删除老师");
                return job_out.toString();
            }

            String phone=job.getString("teacher_id");
            Teacher t=teacherdao.queryTeacher(phone);
            if(t!=null)
            {
                t.setValid(false); // 老师设为无效
                if(teacherdao.updateTeacher(t)){
                    Set<Class> class_set=t.getClasses();
                    Iterator<Class>it=class_set.iterator();
                    while (it.hasNext()){
                        Class one_class=(Class)it.next();
                        one_class.getTeachers().remove(t); //移除对应老师
                        classdao.updateClass(one_class);
                    }
                    Kindergarten garten = t.getKindergarten();
                    garten.getTeachers().remove(t);
                    kindergartendao.updateKindergarten(garten); //更新幼儿园老师列表
                    //更新幼儿园老师通信录
                    UpdateTeacherContractsThread thread = new UpdateTeacherContractsThread(garten.getId());
                    thread.start();
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功删除老师");
                    return  job_out.toString(); //成功
                }
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","删除老师失败");
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
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
                parents_contact+=p.getStudent().getName()+"-"+p.getStudent().getNick_name()+p.getRelationship()+
                        "-"+p.getName()+"-"+p.getPhone_num()+"-"+p.getAvatar_path();
                // baby名称-baby妈妈-家长名字-电话-头像路径
                parents_contact+=";"; //下一条记录
            }
            cla.setParents_contacts(parents_contact);
            classdao.updateClass(cla);
        }
    }

    /***
     *更新一个幼儿园 老师通信录
     * **/
    class UpdateTeacherContractsThread extends Thread {
        long garten_id=0;
        public UpdateTeacherContractsThread(long garten_id) {
            garten_id=garten_id;
        }
        public void run() {
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            if(garten==null)   return ;
            List<Teacher> teachers= teacherdao.getTeachersByGarten(garten_id);
            String teachers_contact="";
            Iterator<Teacher>it = teachers.iterator();
            while (it.hasNext()){
                Teacher t =it.next();
                Set<Class> t_clses=t.getClasses();
                Iterator<Class> it_set=t_clses.iterator();
                String clas_arrs="";
                while (it_set.hasNext()){
                    Class cla = (Class)it_set.next();
                    clas_arrs+=cla.getName();
                }
                teachers_contact+=t.getName()+"-"+t.getPhone_num()+"-"+t.getAvatar_path()+"-"+clas_arrs;
                // 老师名字-老师电话-头像路径-老师班级
                teachers_contact+=";"; //下一条记录
            }
           garten.setTeacher_contacts(teachers_contact);
            kindergartendao.updateKindergarten(garten);
        }
    }


    public  boolean isPhoneOrEmailExist(String phoneNum,String email){
        boolean exsitFlag = false;
        if(teacherdao.queryTeacher(phoneNum)!=null||teacherdao.queryTeacherByEmail(email)!=null){
            exsitFlag = true;
        }else if(parentsdao.queryParents(phoneNum)!=null||parentsdao.queryParentsByEmail(email)!=null){
            exsitFlag = true;
        }else if(agentdao.queryAgent(phoneNum)!=null||agentdao.queryAgentByEmail(email)!=null){
            exsitFlag = true;
        }else if(administratordao.queryAdministrator(phoneNum)!=null||administratordao.queryAdministratorByEmail(email)!=null){
            exsitFlag = true;
        }else{

        }
        return exsitFlag;
    }


}