package com.umeijia.service;

import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher;
import com.umeijia.dao.*;
import com.umeijia.enums.OptEnum;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.LockerLogger;
import com.umeijia.util.LogUtil;
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
import java.util.*;

@Service
@Path("/run_service")
public class RunService {
    @Autowired
    @Qualifier("administratordao")
    private AdministratorDao administratordao;
    @Autowired
    @Qualifier("agentdao")
    private AgentDao agentdao;

    @Autowired
    @Qualifier("basicinfodao")
    private  BasicInfoDao basicinfodao;

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
    private CameraDao cameradao;
    @Autowired
    @Qualifier("classdao")
    private ClassDao classdao;
    @Autowired
    @Qualifier("studentdao")
    private StudentDao studentdao;
    @Autowired
    @Qualifier("gartennewsdao")
    private GartenNewsDao gartennewsdao;
    @Autowired
    @Qualifier("babyshowtimedao")
    private BabyShowtimeDao babyshowtimedao;

    @Autowired
    @Qualifier("showtimecommentsdao")
    private  ShowtimeCommentsDao showtimecommentsdao;

    @Autowired
    @Qualifier("homeworkdao")
    private  HomeWorkDao homeworkdao;

    @Autowired
    @Qualifier("classnotificationdao")
    private  ClassNotificationDao classnotificationdao;

    @Autowired
    @Qualifier("classactivitydao")
    private ClassActivityDao classactivitydao;
    @Autowired
    @Qualifier("dailylogdao")
    private DailyLogDao dailylogdao;
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2(){
        //初始化数据
        Administrator admin = new Administrator("13000000000","1233@dd.com",MD5.GetSaltMD5Code("admin123"),"超级管理员",new Date(),true);
        administratordao.addAdministrator(admin);

        Agent ag = new Agent("agent@163.com",new Date(),"小张",MD5.GetSaltMD5Code("agent123"),"13111111111","111.jpg",null,"新东方",0.75f);
        agentdao.addAgent(ag);
        Kindergarten garten = new Kindergarten("光明幼儿园","科华南路","028-85400752","一起呵护祖国的花朵","1.jpg","2.jpg","3.jpg",ag);
        kindergartendao.addKindergarten(garten);
        Teacher leader = new Teacher("段园长","1.jpg",MD5.GetSaltMD5Code("leader123"),garten,"13222222222","让我带大家一起学习吧","dff9933@163.com",true,"让孩子们茁壮成长","男");
        teacherdao.addTeacher(leader);
        Teacher te = new Teacher("谢老师","1.jpg",MD5.GetSaltMD5Code("123456"),garten,"18090037299","一起摇摆","ppj9933@163.com",false," ","男");
        Teacher te2 = new Teacher("曾老师","2.jpg",MD5.GetSaltMD5Code("123456"),garten,"15680079196","好男人就是我","db83555@163.com",false," ","男");
        teacherdao.addTeacher(te);
        teacherdao.addTeacher(te2);
        com.umeijia.vo.Class cla = new Class("大二班","就快要升一年级了，宝宝们","上午:舞蹈;下午:算术;","张宁三:133;","李老师:14553",garten);
        cla.getTeachers().add(te); //班级追加老师
        classdao.addClass(cla);
        Date d = new Date();
        Student stu1 = new Student("仝刚","仝小宝","男",new Date(),150,30,"1.jpg",cla,garten.getId(),false,d,d,d);
        Student stu2 = new Student("刘屯屯","小屯屯","男",new Date(),138,36,"2.jpg",cla,garten.getId(),false,d,d,d);
        studentdao.addStudent(stu1);
        studentdao.addStudent(stu2);
        Parents parent1 = new Parents("18090037299","3523535@qq.com","仝大大",stu1,cla.getId(),garten.getId(),MD5.GetSaltMD5Code("123456"),"爷爷","1.jpg","男");
        Parents parent2 = new Parents("15680079196","35235111@qq.com","刘大大",stu2,cla.getId(),garten.getId(),MD5.GetSaltMD5Code("123456"),"爸爸","1.jpg","男");
        parentsdao.addParents(parent1);
        parentsdao.addParents(parent2);

        Camera ca = new Camera("222.10.13.3","222.10.13.3:355/video","操场转角",
                "海康威视",garten,"sunfllower_came1.jpg",true,"ccccc","8-10;12-14;15-17;",cla);
        cameradao.addCamera(ca);

        GartenNews news = new GartenNews("欢庆六一儿童节","为了欢庆六一儿童节，学校举行歌舞表演","歌舞表演啦啦啦。\n活动一开场，一群可爱娃娃带来的舞蹈《中华娃娃响当当》便惊艳了全场。孩子们灵动的舞姿，生动的表情，可爱的小眼神，把台下观众迷得不要不要的。随后，《春天的味道》、《亲爱的朋友》、《左手右手》等精彩舞蹈接连上演，引得观众掌声阵阵，久久不停息。\n" +
                "在舞蹈《聪明的宝贝》中，孩子们将“聪明”的状态演绎得活灵活现，让人觉得他们就是一群最聪明的宝贝；而舞蹈《枪战CS》，则将大家带入了“枪林弹雨”的情景；还有《维族姑娘》，孩子们惊艳的表演，令大家误以为表演者真是一群来自维族的小朋友。\n" +
                "舞蹈表演亮点接踵而来，令观众目不暇接。还沉浸在精彩的舞蹈中无法自拔，惊艳的T台模特秀又来了。别看小朋友们年纪小小的，走上T台个个像模像样，举手投足间尽现超模姿态，台下有观众甚至发出“未来他们中一定会诞生国际名模”的言论。",
                "xd35.jpg",te,garten);
        GartenNews news2 = new GartenNews("广西龙胜幼儿园学生拍毕业照秀童真","幼儿园学生拍创意毕业照","6月26日，在广西桂林市龙胜各族自治县民族中心体育场，小朋友们在拍摄毕业照。当天，龙胜第二幼儿园的小朋友们在老师和家长的陪伴下，拍摄各种趣味横生的毕业照。毕业季临近，该县各幼儿园通过拍摄创意毕业照、文艺表演、亲子游园活动等方式，记录小朋友们快乐的成长足迹。",
                "xd35.jpg",te,garten);
        gartennewsdao.addGartenNews(news);
        gartennewsdao.addGartenNews(news2);

        BabyShowtime showtiem = new BabyShowtime("为了庆祝儿童节的到来,老师们组织了歌舞表演","2.jpg;3.jpg",cla.getId(),stu1.getId(),te2.getId(),parent1.getId(),1);
        babyshowtimedao.addBabyShowtime(showtiem);
        ShowtimeComments comment = new ShowtimeComments(true,showtiem,1,1,0,0,d,"跳得真好看");
        showtimecommentsdao.addShowtimeComments(comment);


        BasicInfo basicInfo = new BasicInfo("软件园","v1.0","v1.0","铅笔科技","02888888","3333333","33332@qq.com","我们致力于移动app开发");
        basicinfodao.addBasicInfo(basicInfo);

        HomeWork hw = new HomeWork(1,"画一个鸡蛋","今天给孩子们布置了画一个鸡蛋的绘画作业,宝宝们都要好好表现哦。","2.jpg",d,te.getId());
        homeworkdao.addHomeWork(hw);

        ClassNotification notification = new ClassNotification("期中家长会","为了更好地了解宝宝的相关信息，这个月20号幼儿园组织家长会","3.jpg;2.jpg;",d,te2.getId(),cla.getId(),te2.getName());
        classnotificationdao.addClassNotification(notification);

        Date d2 = new Date(d.getTime()+3600000000l);
        ClassActivity activity = new ClassActivity("开学季春游","为了迎接春天的到来,我们班组织了一次春游","2.jpg;1.jpg;",d,d2,te.getId(),1,"1","1",d.toString(),cla.getId(),te.getName(),te.getPhone_num());
        classactivitydao.addClassActivity(activity);

        LockerLogger.log.info("xxxx");
        LockerLogger.log.error("");

        return "welcom to UMJ server... run service ";
    }



    @Path("/getPersonalInfo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonalInfo(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );

      //      "roleType", "roleId",
            int roleType=job.getInt("roleType"); //登陆账号角色
            int getRoleType=job.getInt("getRoleType");
            long getRoleId=job.getLong("getRoleId");
            long roleId = Long.parseLong(headers.getRequestHeader("id").get(0));
            if (!checkIdAndToken(roleType, headers)) {
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc", "无效token");
                return job_out.toString();
            }
            switch (getRoleType) { //分类查询，返回结果
                case 1: //老师
                case 2: //园长
                    if(roleType==1||roleType==2||roleType==5){
                        Teacher t = teacherdao.queryTeacher(getRoleId);
                        if(t==null) {
                            job_out.put("resultCode", GlobalStatus.error.toString());
                            job_out.put("resultDesc", "当前查询角色不存在");
                            return job_out.toString();
                        }
                        Set<Class> cla_set=t.getClasses();
                        job_out.put("resultCode", GlobalStatus.succeed.toString());
                        job_out.put("resultDesc","登陆成功");
                        job_out.put("getRoleType",getRoleType);
                        job_out.put("id",t.getId());
                        job_out.put("phone",t.getPhone_num());
                        job_out.put("email",t.getEmail());
                        job_out.put("name",t.getName());
                        job_out.put("avatar",t.getAvatar_path());
                        job_out.put("gender",t.getGender());
                        String cla_ids="";
                        String cla_names="";
                        if(t.getIs_leader()){
                            //园长班级列表为全学校的班�
                            List<Class> classes =classdao.queryClassesByGarten(t.getKindergarten().getId());
                            if(classes!=null){
                                Iterator<Class> iterator = classes.iterator();
                                while (iterator.hasNext()){
                                    Class c = iterator.next();
                                    cla_ids+=c.getId();
                                    cla_ids+=";";
                                    cla_names+=c.getName();
                                    cla_names+=";";
                                }
                            }
                        }else{
                            //普通老师i
                            if(cla_set!=null){
                                Iterator<Class> it=cla_set.iterator();
                                while (it.hasNext()){
                                    Class cla = it.next();
                                    cla_ids+=cla.getId();
                                    cla_ids+=";";
                                    cla_names+=cla.getName();
                                    cla_names+=";";
                                }
                            }
                        }
                        job_out.put("class_ids",cla_ids);
                        job_out.put("class_names",cla_names);  // 分号 隔开 ，班�ids names列表
                        job_out.put("is_leader",t.getIs_leader());
                        return  job_out.toString();
                    }
                case 3: //家长
                    Parents p = parentsdao.queryParents(getRoleId);
                    if(p==null){
                        job_out.put("resultCode", GlobalStatus.error.toString());
                        job_out.put("resultDesc", "当前查询角色不存在");
                        return job_out.toString();
                    }
                    Student stu = p.getStudent();
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","登陆成功");
                    job_out.put("getRoleType",getRoleType);
                    job_out.put("id",p.getId());
                    job_out.put("schoolId",p.getGarten_id());
                    job_out.put("phone",p.getPhone_num());
                    job_out.put("email",p.getEmail());
                    job_out.put("name",p.getName());
                    if(stu!=null){
                        job_out.put("baby_id",stu.getId());
                        job_out.put("is_vip",stu.isVip());
                        job_out.put("vip_start",stu.getVip_start().toString());
                        job_out.put("vip_end",stu.getVip_end().toString());
                    }else{
                        job_out.put("baby_id","");
                        job_out.put("is_vip","false");
                        job_out.put("vip_start","");
                        job_out.put("vip_end","");
                    }
                    job_out.put("class_id",p.getClass_id());
                    job_out.put("class_name",p.getClass().getName());
                    job_out.put("relation",p.getRelationship());
                    job_out.put("avatar",p.getAvatar_path());
                    return  job_out.toString();
                case 4: //加盟商
                    Agent ag = agentdao.queryAgent(roleId);
                    if(ag==null){
                        job_out.put("resultCode", GlobalStatus.error.toString());
                        job_out.put("resultDesc", "当前查询角色不存在");
                        return job_out.toString();
                    }
                    Set<Kindergarten> gartens = ag.getGartens();
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","登陆成功");
                    job_out.put("getRoleType",getRoleType);
                    job_out.put("id",ag.getId());
                    job_out.put("phone",ag.getPhone_num());
                    job_out.put("email",ag.getEmail());
                    job_out.put("name",ag.getName());
                    job_out.put("price",ag.getPrice_rate());
                    job_out.put("company",ag.getCompany_name());
                    job_out.put("regis_date",ag.getRegist_date().toString());
                    job_out.put("avatar",ag.getAvarta());
                    String garten_ids="";
                    String garten_names="";
                    if(gartens!=null){
                        Iterator<Kindergarten> it=gartens.iterator();
                        while (it.hasNext()){
                            Kindergarten kg = (Kindergarten)it.next();
                            garten_ids+=kg.getId();
                            garten_ids+=";";
                            garten_names+=kg.getName();
                            garten_names+=";";
                        }
                        job_out.put("garten_ids",garten_ids);
                        job_out.put("garten_names",garten_names);
                    }else{
                        job_out.put("garten_ids","");
                        job_out.put("garten_names","");
                    }
                    return  job_out.toString();
                case 5: //管理员
                    Administrator admin = administratordao.queryAdministrator(getRoleId);
                    if(admin==null){
                        job_out.put("resultCode", GlobalStatus.error.toString());
                        job_out.put("resultDesc", "当前查询角色不存在");
                        return job_out.toString();
                    }
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","登陆成功");
                    job_out.put("getRoleType",getRoleType);
                    job_out.put("id",admin.getId());
                    job_out.put("phone",admin.getPhone_num());
                    job_out.put("email",admin.getEmail());
                    job_out.put("name",admin.getName());
                    job_out.put("is_super",admin.isIs_super());
                   return  job_out.toString();
            }

            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc", "当前角色无权限查看");
            return  job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    @Path("/getBabyListByClass")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getBabyListByClass(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }

            long class_id = job.getLong("class_id");
            Class cla = classdao.queryClass(class_id);
            if(cla==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","无效班级id");
                return job_out.toString();
            }
            Set<Student> stu_set = cla.getStudents();
            if(stu_set==null||stu_set.size()<1){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","该班级还没有宝贝");
                return job_out.toString();
            }
            JSONArray array = new JSONArray();
            Iterator<Student> stu_it=stu_set.iterator();
            while (stu_it.hasNext()){
                Student baby = stu_it.next();
                JSONObject json = new JSONObject();
                json.put("baby_name",baby.getName());
                json.put("id",baby.getId());
                array.add(json);
            }
            job_out.put("resultCode",GlobalStatus.succeed.toString());
            job_out.put("resultDesc","成功获取班级宝贝列表");
            job_out.put("data",array.toString());
            return job_out.toString();
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    @Path("/getClassListByGarten")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getClassListByGarten(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            long roleId = Long.parseLong(headers.getRequestHeader("id").get(0));
            if (!checkIdAndToken(2, headers)) {
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc", "无效token");
                return job_out.toString();
            }

            long garten_id = job.getLong("garten_id");
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            if(garten==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","无效幼儿园id");
                return job_out.toString();
            }
            Set<Class>classes= garten.getClasses();
            if(classes==null||classes.size()<1){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","该幼儿园还没有班级");
                return job_out.toString();
            }
            JSONArray array = new JSONArray();
            Iterator<Class> cla_it=classes.iterator();
            while (cla_it.hasNext()){
                Class cla = cla_it.next();
                JSONObject json = new JSONObject();
                json.put("class_name",cla.getName());
                json.put("id",cla.getId());
                array.add(json);
            }
            job_out.put("resultCode",GlobalStatus.succeed.toString());
            job_out.put("resultDesc","成功获取幼儿园班级列表");
            job_out.put("data",array.toString());
            return job_out.toString();
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }
    /***
     *
     *运营人员天添加 幼儿园 和 园长
     * **/
    @Path("/addGartenAndLeader")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addGartenAndLeader(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Agent agent = new Agent(id);
            String garten_name= job.getString("garten_name");
            String addr= job.getString("addr"); //幼儿园地址
            String contact=job.getString("garten_contact"); //幼儿园联系方式
            String descrip=job.getString("garten_description"); // 幼儿园介绍
            String teacher_presence_imgs=job.getString("teacher_presence_imgs");// 教师风采图片列表
            String garten_instrument_imgs=job.getString("garten_instrument_imgs");// 教学设施列表
            String garten_presence_imgs=job.getString("garten_presence_imgs");//幼儿园图片展示列表*/
            String phone = job.getString("leader_phone");
            String email = job.getString("leader_email");
         //   String pwd = job.getString("leader_password");
       //     pwd=MD5.GetSaltMD5Code(pwd);
            String name = job.getString("leader_name");
            String avatar = job.getString("leader_avatar");
            String wishes = job.getString("leader_wishes"); //园长寄语，老师不传
            String leader_descrip=job.getString("leader_description"); //老师介绍
            String leader_gender=job.getString("leader_gender");

     //       LockerLogger.log.info("幼儿园信息解析成功");

            Date date = new Date();
            if(isPhoneOrEmailExist(phone,email)){
                //已经存在
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","用户手机号或邮箱已存在");
                return job_out.toString();
            }

            Kindergarten garten = new Kindergarten(garten_name,addr,contact,descrip,teacher_presence_imgs,garten_instrument_imgs,garten_presence_imgs,agent);
            garten.setLeader_wishes(wishes);
            if(kindergartendao.addKindergarten(garten)){
                // 成功添加幼儿园
                String pwd = SMSMessageService.GenerateRandomNumber();
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("phoneNum",phone);
                map.put("verifyCode",pwd);
                map.put("type",2);
                SMSMessageService .cmds.add(map);
                pwd=MD5.GetSaltMD5Code(pwd);
                Teacher leader=new Teacher(name,avatar,pwd,garten,phone,leader_descrip,email,true,wishes,leader_gender); //园长
                if(teacherdao.addTeacher(leader)){
                    garten.setLeader_wishes(wishes); //更新幼儿园 寄语
                    garten.setLeader_id(leader.getId()); //更新幼儿园 园长
                    String leader_contact=name+"-"+phone+"-"+avatar+"-"+"园长";
                    garten.setTeacher_contacts(leader_contact);
                    if( kindergartendao.updateKindergarten(garten))
                    {
                        job_out.put("resultCode",GlobalStatus.succeed.toString());
                        job_out.put("garten_id",garten.getId());
                        job_out.put("leader_id",leader.getId());
                        job_out.put("resultDesc","成功添加幼儿园和园长");
                        //添加日志
                        DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),4,id, OptEnum.insert.toString(),"添加幼儿园和园长","幼儿园id:"+String.valueOf(garten.getId())+",园长id:"+String.valueOf(leader.getId()));
                        dailylogdao.addDailyLog(dailyLog);
                        return job_out.toString();
                    }
                }

            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","新建幼儿园失败");
            return job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    @Path("/addClass")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addClass(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }

            Teacher teacher= teacherdao.queryTeacher(id);
            if(teacher==null||teacher.getIs_leader()==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","园长才有权限添加班级");
            }
            String class_name= job.getString("class_name");
            Class cla = new Class(class_name,teacher.getKindergarten());
            if(classdao.addClass(cla)){
                job_out.put("class_id",cla.getId());
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功添加班级");
                return job_out.toString();

            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","创建班级失败");
            return job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    @Path("/correctClass")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctClass(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }
            Teacher teacher= teacherdao.queryTeacher(id);
            if(teacher==null||teacher.getIs_leader()==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","园长才有权限修改班级");
                return  job_out.toString();
            }
            String class_name= job.getString("new_class_name");
            long cla_id = job.getLong("class_id");
            Class cla = classdao.queryClass(cla_id);
            if(cla==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","无效班级id");
                return  job_out.toString();
            }
            cla.setName(class_name); //设置新班级名
            if(classdao.updateClass(cla)){
                job_out.put("class_id",cla.getId());
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功修改班级");
                return job_out.toString();
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","创建班级失败");
            return job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    /***
     * 园长修改老师信息
     * ***/
    @Path("/correctTInfo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctTInfo(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }
            Teacher leader= teacherdao.queryTeacher(id);
            if(leader==null||leader.getIs_leader()==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","园长才有权限修改班级");
                return  job_out.toString();
            }

            String  name =job.getString("name");
            String gender=job.getString("gender");
            String phone = job.getString("phone");
            long teacher_id=job.getLong("teacher_id");
            String cla_ids=job.getString("cla_ids");

            Teacher te = teacherdao.queryTeacher(teacher_id);
            if(te==null||te.isValid()==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","老师id无效");
                return  job_out.toString();
            }
            te.setName(name);
            te.setGender(gender);
            String old_phone=te.getPhone_num();
            Map<String,Object> map = new HashMap<String,Object>();
            if(!old_phone.equals(phone)){
                //更换了手机号码
                String new_pwd=SMSMessageService.GenerateRandomNumber();
                te.setPwd_md(MD5.GetSaltMD5Code(new_pwd));
                //短信通知
                map.put("phoneNum",phone);
                map.put("verifyCode",new_pwd);
                map.put("type",2);
            }
            if(teacherdao.updateTeacher(te)==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","修改老师信息失败");
                return  job_out.toString();
            }
            SMSMessageService .cmds.add(map); //发送新密码
//// 更改老师与班级的关系，解除旧关系，添加新关系
            //在新的班级添加老师
            String [] cla_id_array=cla_ids.split(";");
            for(int i=0;i<cla_id_array.length;i++){
                long new_cla_id=Long.parseLong(cla_id_array[i]);
                Class new_cla=classdao.queryClass(new_cla_id);
                if(new_cla==null||new_cla.isValid()==false){
                    job_out.put("resultCode",GlobalStatus.error.toString());
                    job_out.put("resultDesc","新班级id无效");
                    return  job_out.toString();
                }
                new_cla.getTeachers().add(te);
                //添加新关系
                if(classdao.updateClass(new_cla)==false){
                    job_out.put("resultCode",GlobalStatus.error.toString());
                    job_out.put("resultDesc","更换班级失败");
                    return  job_out.toString();
                }
            }
            //从原有班级移除老师
            Set<Class> old_classes=te.getClasses();
            Iterator<Class> it=old_classes.iterator();
            while (it.hasNext()){
                Class old_cla=it.next();
                old_cla.getTeachers().remove(te);
                if(classdao.updateClass(old_cla)==false) //原有班级里移除这个老师
                {
                    job_out.put("resultCode",GlobalStatus.error.toString());
                    job_out.put("resultDesc","移除原有班级失败");
                    return  job_out.toString();
                }
            }
            job_out.put("resultCode",GlobalStatus.succeed.toString());
            job_out.put("resultDesc","修改班级成功");
        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
        return  job_out.toString();
        }
    /***
     *
     *运营人员天添加 幼儿园
     * **//*
    @Path("/addGarten")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addGarten(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Agent agent = new Agent(id);

            String name= job.getString("name");
            String addr= job.getString("addr"); //幼儿园地址
            String contact=job.getString("contact"); //幼儿园联系方式
            String descrip=job.getString("description"); // 幼儿园介绍
            String teacher_presence_imgs=job.getString("teacher_presence_imgs");// 教师风采图片列表
            String garten_instrument_imgs=job.getString("garten_instrument_imgs");// 教学设施列表
            String garten_presence_imgs=job.getString("garten_presence_imgs");//幼儿园图片展示列表*//*

            String phone = job.getString("leader_phone");
            String email = job.getString("leader_email");
            //   String pwd = job.getString("leader_password");
            //     pwd=MD5.GetSaltMD5Code(pwd);



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
    }*/

    /***
     *
     *运营人员天添加 幼儿园
     * **/
    @Path("/correctGartenAndLeader")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctGartenAndLeader(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try{
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }
            Agent agent = new Agent(id);
            //获取上传的幼儿园新属性
            long garten_id = job.getLong("garten_id");
            String garten_name= job.getString("garten_name");
            String addr= job.getString("addr"); //幼儿园地址
            String contact=job.getString("garten_contact"); //幼儿园联系方式
            String descrip=job.getString("garten_description"); // 幼儿园介绍
            String teacher_presence_imgs=job.getString("teacher_presence_imgs");// 教师风采图片列表
            String garten_instrument_imgs=job.getString("garten_instrument_imgs");// 教学设施列表
            String garten_presence_imgs=job.getString("garten_presence_imgs");//幼儿园图片展示列表*/

            String phone =job.getString("leader_phone");
            String email =job.getString("leader_email"); //邮箱
            String name = job.getString("leader_name");  //名字
            String avatar = job.getString("leader_avatar"); //头像
            String wishes = job.getString("leader_wishes"); //园长寄语，老师不传
            String leader_descrip=job.getString("leader_description"); //老师介绍
            String leader_gender=job.getString("leader_gender"); //性别


            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            if(garten==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","无效幼儿园id");
                return job_out.toString();
            }
            if(agent.getGartens().contains(garten)==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","当前加盟商无权限");
                return job_out.toString();
            }
            //设置幼儿园新的属性
            garten.setName(garten_name);
            garten.setAddr(addr);
            garten.setContact_num(contact);
            garten.setDescription(descrip);
            garten.setTeacher_presence_imgs(teacher_presence_imgs);
            garten.setGarten_instrument_imgs(garten_instrument_imgs);
            garten.setTeacher_presence_imgs(garten_presence_imgs);
            garten.setLeader_wishes(wishes);
        // 修改园长信息
            Teacher old_leader = teacherdao.queryTeacher(garten.getLeader_id());
            if(old_leader!=null){
                    if(old_leader.getPhone_num().equals(phone)){
                        //园长不变,只是修改信息
                        old_leader.setName(name);
                        old_leader.setWishes(wishes);
                        old_leader.setPhone_num(phone);
                        old_leader.setAvatar_path(avatar);
                        old_leader.setEmail(email);
                        old_leader.setDescription(leader_descrip);
                        old_leader.setGender(leader_gender);
                       if(teacherdao.updateTeacher(old_leader)==false){
                           job_out.put("resultCode",GlobalStatus.error.toString());
                           job_out.put("resultDesc","修改园长信息失败");
                           return job_out.toString();
                       }
                    }else{
                        //替换园长
                        String new_passwd=SMSMessageService.GenerateRandomNumber();
                        String new_passwd_md=MD5.GetSaltMD5Code(new_passwd);
                        Teacher new_leader=teacherdao.queryTeacher(phone);
                        if(new_leader!=null){
                            //一个老师提升为园长
                            new_leader.setName(name);
                            new_leader.setWishes(wishes);
                            new_leader.setPhone_num(phone);
                            new_leader.setAvatar_path(avatar);
                            new_leader.setEmail(email);
                            new_leader.setDescription(leader_descrip);
                            new_leader.setGender(leader_gender);
                            new_leader.setIs_leader(true);
                            if(teacherdao.updateTeacher(new_leader)==false){
                                job_out.put("resultCode",GlobalStatus.error.toString());
                                job_out.put("resultDesc","修改园长信息失败");
                                return job_out.toString();
                            }
                            //废除老园长
                            old_leader.setIs_leader(false);
                            old_leader.setValid(false);
                            if(teacherdao.updateTeacher(old_leader)==false){
                                job_out.put("resultCode",GlobalStatus.error.toString());
                                job_out.put("resultDesc","修改园长信息失败");
                                return job_out.toString();
                            }
                            garten.setLeader_id(new_leader.getId()); //设置新的园长id
                        }else{
                            //置换为一个新园长,发送新密码
                            old_leader.setName(name);
                            old_leader.setWishes(wishes);
                            old_leader.setPhone_num(phone);
                            old_leader.setAvatar_path(avatar);
                            old_leader.setEmail(email);
                            old_leader.setDescription(leader_descrip);
                            old_leader.setGender(leader_gender);
                            old_leader.setIs_leader(true);
                            old_leader.setPwd_md(new_passwd_md); //设置新密码
                            if(teacherdao.updateTeacher(old_leader)==false) //替换原来园长记录
                            {
                                job_out.put("resultCode",GlobalStatus.error.toString());
                                job_out.put("resultDesc","修改园长信息失败");
                                return job_out.toString();
                            }
                        }
                    }

            }else{ //直接新加园长
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","该幼儿园原来没有园长");
                return job_out.toString();
            }
            if(kindergartendao.updateKindergarten(garten)){
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功修改幼儿园");
                return  job_out.toString();
            }
            job_out.put("resultCode",GlobalStatus.error.toString());
            job_out.put("resultDesc","修改幼儿园失败");
            return job_out.toString();

        } catch (JSONException e) {
            return "error";  //json  构造异常，直接返回error
        }
    }

    /***
     * 代理商添加园长
     * curl -X POST -H "Content-Type:application/json" -d {"phone":"13534456644","password":"134df","name":"ltt4aoshou","email":"12345@qq.com","class_id":"1","baby_id":"1","relation":"dad","avatar":"fdef.jpg","gender":"0"}
     * http://127.0.0.1/umeijiaServer/teacher_service/addParents
     * **/
   /* @Path("/addLeader")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addLeader(@RequestBody String userinfo, @Context HttpHeaders headers) {
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();
        try {
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Agent agent = agentdao.queryAgent(id);
            if(agent==null){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","没有该代理商");
                return job_out.toString();
            }
            String phone = job.getString("phone");
            String email = job.getString("email");
         *//*   String pwd = job.getString("password");
            pwd=MD5.GetSaltMD5Code(pwd);*//*
            String name = job.getString("name");
            long garten_id=job.getLong("garten_id");
            String avatar = job.getString("avatar");
            String wishes = job.getString("wishes"); //园长寄语，老师不传
            String descrip=job.getString("description"); //老师介绍
            String gender=job.getString("gender");
            String pwd=SMSMessageService.GenerateRandomNumber(); //生成随即密码
            String org_pwd = pwd;

            pwd=MD5.GetSaltMD5Code(pwd); //计算密码盐值摘要

            if(isPhoneOrEmailExist(phone,email)){
                //已经存在
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","用户手机号或邮箱已存在");
                return job_out.toString();
            }

            //        boolean is_leader = job.getBoolean("leader"); //是否是园长
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            if(garten.getLeader_id()>0){
                //已有园长
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","添加失败:该幼儿园已有园长");
                return job_out.toString();
            }
            Teacher leader=new Teacher(name,avatar,pwd,garten,phone,descrip,email,true,wishes,gender); //园长
            if(teacherdao.addTeacher(leader)){
                garten.setLeader_wishes(wishes); //更新幼儿园 寄语
                garten.setLeader_id(leader.getId()); //更新幼儿园 园长
                String leader_contact=name+"-"+phone+"-"+avatar+"-"+"园长";
                garten.setTeacher_contacts(leader_contact);
                if( kindergartendao.updateKindergarten(garten))
                {
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("phoneNum",phone);
                    map.put("verifyCode",org_pwd);
                    map.put("type",2);
                    SMSMessageService .cmds.add(map);
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
    }*/

    @Path("/agentLogin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String loginAgent(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            String phone=job.getString("phone");
            String email=job.getString("email");
            String pwd=job.getString("password");
            String pwd_md=MD5.GetSaltMD5Code(pwd);
            Agent ag=null;
            if (!phone.isEmpty()) {
                ag = agentdao.loginCheckByPhone(phone,pwd_md);
            } else if(!email.isEmpty()){ // 邮箱登录
                ag= agentdao.loginCheckByEmail(email,pwd_md);
            }
            LockerLogger.log.info("加盟商开始登陆..");
            if(ag!=null)
            {
                // 无效代理商
                if(ag.isValid()==false){
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc","无效代理商");
                    return  job_out.toString();
                }
                Set<Kindergarten> gartens = ag.getGartens();
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc","登陆成功");
                job_out.put("tkn",ag.getToken());
                job_out.put("tkn_exptime",ag.getExpire().toString());
                job_out.put("id",ag.getId());
                job_out.put("phone",ag.getPhone_num());
                job_out.put("email",ag.getEmail());
                job_out.put("name",ag.getName());
                job_out.put("price",ag.getPrice_rate());
                job_out.put("company",ag.getCompany_name());
                job_out.put("regis_date",ag.getRegist_date().toString());
                job_out.put("avatar",ag.getAvarta());
                String garten_ids="";
                String garten_names="";
                Iterator<Kindergarten> it=gartens.iterator();
                while (it.hasNext()){
                    Kindergarten kg = (Kindergarten)it.next();
                    garten_ids+=kg.getId();
                    garten_ids+=";";
                    garten_names+=kg.getName();
                    garten_names+=";";
                }
                job_out.put("garten_ids",garten_ids);
                job_out.put("garten_names",garten_names);
            }else{
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","登录用户名、邮箱或密码无效");
            }
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }


    @Path("/adminLogin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String loginAdmin(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            String phone=job.getString("phone");
            String email=job.getString("email");
            String pwd=job.getString("password");
            String pwd_md=MD5.GetSaltMD5Code(pwd);
            Administrator admin=null;
            if (!phone.isEmpty()) {
                admin = administratordao.loginCheckByPhone(phone,pwd_md);
            } else if(!email.isEmpty()){ // 邮箱登录
                admin = administratordao.loginCheckByEmail(email,pwd_md);
            }
            if(admin!=null)
            {
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc","登陆成功");
                job_out.put("tkn",admin.getToken());
                job_out.put("tkn_exptime",admin.getExpire().toString());
                job_out.put("id",admin.getId());
                job_out.put("phone",admin.getPhone_num());
                job_out.put("email",admin.getEmail());
                job_out.put("name",admin.getName());
                job_out.put("is_super",admin.isIs_super());

            }else{
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","登录用户名、邮箱或密码无效");
            }
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
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
                long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
                if(!administratordao.verifyToken(id,tkn)){ // token验证
                    job_out.put("resultCode", GlobalStatus.error.toString());
                    job_out.put("resultDesc","token已过期");
                    return job_out.toString();
                }
                String phone= job.getString("phone");
//                String pwd= job.getString("pwd");
//                pwd=MD5.GetSaltMD5Code(pwd);
                String name= job.getString("name");
                String email=job.getString("email");
                String company=job.getString("company");
                float price = (float) job.getDouble("price");
                String avatar=job.getString("avatar");

            if(isPhoneOrEmailExist(phone,email)){
                //已经存在
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","用户手机号或邮箱已存在");
                return job_out.toString();
            }

            // 随机生成密码
            String pwd=SMSMessageService.GenerateRandomNumber();
            String org_pwd=pwd;
            pwd=MD5.GetSaltMD5Code(pwd);

            Date date = new Date();
            Agent agent=new Agent(email,new Date(),name,pwd,phone,avatar,null,company,price);
            if(agentdao.addAgent(agent)){
                // 加入短信队列发送密码
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("phoneNum",phone);
                map.put("verifyCode",org_pwd);
                map.put("type",2);
                SMSMessageService .cmds.add(map);

                    job_out.put("resultCode",GlobalStatus.succeed.toString());
                    job_out.put("agent_id",agent.getId());
                    job_out.put("resultDesc","成功添加代理商");
                //添加日志
                DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),5,id, OptEnum.insert.toString(),"添加代理商","代理商id:"+String.valueOf(agent.getId()));
                dailylogdao.addDailyLog(dailyLog);
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
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!administratordao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            Administrator root = administratordao.queryAdministrator(id);
            if(root==null||root.isIs_super()==false){
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","只有超级管理员才有该权限");
                return job_out.toString();
            }

            String phone= job.getString("phone");
    /*        String pwd= job.getString("pwd");
            pwd=MD5.GetSaltMD5Code(pwd);*/
            String name= job.getString("name");
            String email=job.getString("email");
            Date date = new Date();
            String pwd = SMSMessageService.GenerateRandomNumber();// 生成随机密码
            String org_pwd=pwd;
            pwd=MD5.GetSaltMD5Code(pwd); //获取 盐值md5摘要

            if(isPhoneOrEmailExist(phone,email)){
                //已经存在
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","用户手机号或邮箱已存在");
                return job_out.toString();
            }

            Administrator admin =  new Administrator(phone,email,pwd,name,date,false);
            if(administratordao.addAdministrator(admin))
            {

                Map<String,Object> map = new HashMap<String,Object>();
                map.put("phoneNum",phone);
                map.put("verifyCode",org_pwd);
                map.put("type",2);
                SMSMessageService .cmds.add(map);
                job_out.put("resultCode",GlobalStatus.succeed.toString());
                job_out.put("admin_id",admin.getId());
                job_out.put("resultDesc","成功添加管理员");
                //添加日志
                DailyLog dailyLog = LogUtil.generateDailyLog(new Date(),5,id, OptEnum.insert.toString(),"添加管理员","管理员id:"+String.valueOf(admin.getId()));
                dailylogdao.addDailyLog(dailyLog);
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
    public String correctAgentInfo(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }
            String name= job.getString("name");
            String company=job.getString("company");
            float price = (float) job.getDouble("price");
            String avata = job.getString("avatar");
            String email =job.getString("email");
            String phone=job.getString("phone");

            Agent ag=agentdao.queryAgent(id);
            if(ag!=null)
            {
                String org_phone=ag.getPhone_num();
                if(!org_phone.equals(phone)){
                    //更换了手机号码,发送新动态密码
                    String new_pwd=SMSMessageService.GenerateRandomNumber();
                    ag.setPwd_md(MD5.GetSaltMD5Code(new_pwd)); //设置新密码摘要值
                    //短信通知
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("phoneNum",phone);
                    map.put("verifyCode",new_pwd);
                    map.put("type",2);
                    SMSMessageService .cmds.add(map); //发送新密码
                }
                ag.setName(name); // 重设相关信息
                ag.setAvarta(avata);
                ag.setCompany_name(company);
                ag.setPrice_rate(price);
                ag.setPhone_num(phone);
                ag.setEmail(email);
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
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone=job.getString("phone");
            String oldPasswordMD= MD5.GetSaltMD5Code(job.getString("oldPassword"));
            String newPasswordMD=MD5.GetSaltMD5Code(job.getString("newPassword"));
            Agent a=agentdao.queryAgent(phone);

            if(a!=null && oldPasswordMD.equals(a.getPwd_md()))
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
    @Path("/correctAdminPwd")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String correctAdminPwd(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!administratordao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","token已过期");
                return job_out.toString();
            }

            String phone=job.getString("phone");
            String oldPasswordMD= MD5.GetSaltMD5Code(job.getString("oldPassword"));
            String newPasswordMD=MD5.GetSaltMD5Code(job.getString("newPassword"));
            Administrator a=administratordao.queryAdministrator(phone);

            if(a!=null && oldPasswordMD.equals(a.getPwd_md()))
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

    @Path("/invalidClass")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invalidClass(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!teacherdao.verifyToken(id,tkn)){ // token验证
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }

            long class_id=job.getLong("class_id");
            Class cla =classdao.queryClass(class_id);
            if(cla==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效班级id");
                return  job_out.toString();
            }
            // 与班级有一对多关系的集合里，剔除该班级
           /* cla.setTeachers(null); //清空所有老师
            cla.setGarten(null); //解除与幼儿园的关系
            cla.setValid(false);*/
            // 家长和宝贝无效，不可登陆
            //解除摄像头和班级关系
            cameradao.invalidCameraByClass(class_id);
            if(classdao.invalidClass(class_id)){
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功删除班级");
                return  job_out.toString();
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","删除班级失败");
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }

    @Path("/invalidGarten")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //只能由加盟商管理
    public String invalidGarten(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong( headers.getRequestHeader("id").get(0) );
            if(!agentdao.verifyToken(id,tkn)){ // token验证,只能由加盟商管理
                job_out.put("resultCode",GlobalStatus.error.toString());
                job_out.put("resultDesc","无效token");
                return job_out.toString();
            }

            long garten_id=job.getLong("garten_id");
            Kindergarten garten = kindergartendao.queryKindergarten(garten_id);
            if(garten==null){
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc","无效幼儿园id");
                return  job_out.toString();
            }
          //  garten.setValid(false);
            // 代理商列表中，解除该幼儿园的关系
         /*   Agent invalid_agent=new Agent();
            invalid_agent.setId(0);
            garten.setAgent(null);*/
            // 无效摄像头
            cameradao.invalidCameraByGarten(garten_id);

            if(kindergartendao.invalidGarten(garten_id)){
                job_out.put("resultCode", GlobalStatus.succeed.toString());
                job_out.put("resultDesc","成功删除幼儿园");
                return  job_out.toString();
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","删除幼儿园失败");
        }catch (JSONException e){
             return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
    }

    // 删除代理商
    @Path("/invalidAgent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invalidAgent(@RequestBody String userinfo, @Context HttpHeaders headers){
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        try {
            // 用户 登陆token 验证
            String tkn = headers.getRequestHeader("tkn").get(0);
            long id = Long.parseLong(headers.getRequestHeader("id").get(0));
            if (!administratordao.verifyToken(id, tkn)) { // token验证
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc", "无效token");
                return job_out.toString();
            }

            Administrator admin = administratordao.queryAdministrator(id);
            if (admin == null) {
                job_out.put("resultCode", GlobalStatus.error.toString());
                job_out.put("resultDesc", "非法操作人员");
                return job_out.toString();
            }

            long agent_id = job.getLong("agent_id");
            Agent agent =agentdao.queryAgent(agent_id);
            if(agent!=null){
                agent.setValid(false);
                if(agentdao.updateAgent(agent)){ // 只是删除加盟商，无其他关联操作
                    job_out.put("resultCode", GlobalStatus.succeed.toString());
                    job_out.put("resultDesc","成功删除加盟商");
                    return  job_out.toString();
                }
            }
            job_out.put("resultCode", GlobalStatus.error.toString());
            job_out.put("resultDesc","删除加盟商失败");
        }catch (JSONException e){
            return "error";  //json  构造异常，直接返回error
        }
        return job_out.toString();
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

    /**
     * 根据角色类型和传入的id和tkn，验证用户是否有效
     *
     * @param roleType
     * @param headers
     * @return
     */
    public boolean checkIdAndToken(int roleType, HttpHeaders headers) {
        boolean result = false;
        String tkn = headers.getRequestHeader("tkn").get(0);
        long id = Long.parseLong(headers.getRequestHeader("id").get(0));
        switch (roleType) {
            case 1:
            case 2:
                result = teacherdao.verifyToken(id, tkn);
                break;
            case 3:
                result = parentsdao.verifyToken(id, tkn);
                break;
            case 4:
                result = agentdao.verifyToken(id, tkn);
                break;
            case 5:
                result = administratordao.verifyToken(id, tkn);
                break;
        }
        return result;
    }


}
