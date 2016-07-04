package com.umeijia.service;

import com.umeijia.dao.*;
import com.umeijia.util.GlobalStatus;
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
import java.util.*;

@Service
@Path("/query_service")
public class QueryService {
    @Autowired
    @Qualifier("querydao")
    private QueryDao querydao;
    @Autowired
    @Qualifier("administratordao")
    private AdministratorDao administratordao;
    @Autowired
    @Qualifier("agentdao")
    private AgentDao agentdao;
    @Autowired
    @Qualifier("babyfootprintdao")
    private BabyFootPrintDao babyfootprintdao;
    @Autowired
    @Qualifier("babyknowledgedao")
    private BabyKnowledgeDao babyknowledgedao;
    @Autowired
    @Qualifier("babyshowtimedao")
    private BabyShowtimeDao babyshowtimedao;
    @Autowired
    @Qualifier("basicinfodao")
    private BasicInfoDao basicinfodao;
    @Autowired
    @Qualifier("cameradao")
    private CameraDao cameradao;

    @Autowired
    @Qualifier("checkincarddao")
    private CheckinCardDao checkincarddao;

    @Autowired
    @Qualifier("checkinrecorddao")
    private CheckinRecordsDao checkinrecorddao;
    @Autowired
    @Qualifier("classactivitydao")
    private ClassActivityDao classactivitydao;
    @Autowired
    @Qualifier("classalbumdao")
    private ClassAlbumDao classalbumdao;
    @Autowired
    @Qualifier("classdao")
    private ClassDao classdao;
    @Autowired
    @Qualifier("classnotificationdao")
    private ClassNotificationDao classnotificationdao;
    @Autowired
    @Qualifier("dailylogdao")
    private DailyLogDao dailylogdao;
    @Autowired
    @Qualifier("feedbackdao")
    private FeedBackDao feedbackdao;
    @Autowired
    @Qualifier("foodrecorddao")
    private FoodRecordDao foodrecorddao;
    @Autowired
    @Qualifier("gartennewsdao")
    private GartenNewsDao gartennewsdao;
    @Autowired
    @Qualifier("homeworkdao")
    private HomeWorkDao homeworkdao;
    @Autowired
    @Qualifier("kindergartendao")
    private KinderGartenDao kindergartendao;
    @Autowired
    @Qualifier("messagedao")
    private MessageDao messagedao;
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    @Autowired
    @Qualifier("showtimecommentsdao")
    private ShowtimeCommentsDao showtimecommentsdao;
    @Autowired
    @Qualifier("studentdao")
    private StudentDao studentdao;
    @Autowired
    @Qualifier("teacherdao")
    private TeacherDao teacherdao;
    @Autowired
    @Qualifier("systemnotificationdao")
    private SystemNotificationDao systemnotificationdao;

    @Path("/queryByCondition")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryByCondition(@RequestBody String queryInfo, @Context HttpHeaders headers){
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(queryInfo, "roleType", "roleId", "queryType","pageSize","pageNum");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(queryInfo);
            String queryType = jobIn.getString("queryType");
            String queryTables = "Teacher,Class,Student,Agent,Kindergarten,Parents,GartenNews,FoodRecord,CheckinRecords,ClassNotification,HomeWork,ClassActivity,FeedBack,Camera,BabyKnowledge,SystemNotification,DailyLog";
            if(!queryTables.contains(queryType)){
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "传入的queryType不合法");
                return jobOut.toString();
            }
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int pageSize = jobIn.getInt("pageSize");
            int pageNum = jobIn.getInt("pageNum");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }

            //        String hql=String.format("from BabyShowtime bs where bs.class_id=%d",class_id);
            String hql = GenerateSqlFromInput(queryType,jobIn);
            Pager pager = new Pager();
            pager.setPageNumber(pageNum);
            pager.setPageSize(pageSize);
            pager = querydao.queryPager(hql,pager);
            JSONArray ja = new JSONArray();
            switch(queryType){
                case"Teacher":
                    List<Teacher> teachers = (List<Teacher>)pager.getList();
                    if(teachers==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Teacher item:teachers){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getName());
                        jo.put("gender",item.getGender());
                        jo.put("phoneNum",item.getPhone_num());
                        String classNames = "";
                        for(Class cla : item.getClasses()){
                            classNames+=cla.getName()+";";
                        }
                        jo.put("classNames",classNames);
                        ja.add(jo);
                    }
                    break;
                case "Class":
                    List<Class>  classes = (List<Class>)pager.getList();
                    if(classes==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Class item: classes){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getName());
                        jo.put("studentNumber",studentdao.queryStudentByClass(item.getId()).size());
                        ja.add(jo);
                    }
                    break;
                case "Student":
                    List<Student> students = (List<Student>)pager.getList();
                    if(students==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Student item : students){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getName());
                        jo.put("gender",item.getGender());
                        jo.put("age",(new Date().getTime()-item.getBirthday().getTime())/(365*24*3600*1000));
                        jo.put("className",item.getCla().getName());
                        ja.add(jo);
                    }
                    break;
                case "Agent":
                    List<Agent> agents = (List<Agent>)pager.getList();
                    if(agents==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Agent item:agents){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getName());
                        jo.put("phoneNum",item.getPhone_num());
                        jo.put("priceRate",item.getPrice_rate());
                        ja.add(jo);
                    }
                    break;
                case "Kindergarten":
                    List<Kindergarten> kindergartens = (List<Kindergarten>)pager.getList();
                    if(kindergartens==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Kindergarten item: kindergartens){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getName());
                        jo.put("addr",item.getAddr());
                        jo.put("leaderName",teacherdao.queryTeacher(item.getLeader_id()).getName());
                        jo.put("leaderPhone",teacherdao.queryTeacher(item.getLeader_id()).getPhone_num());
                        ja.add(jo);
                    }
                    break;
                case "Parents":
                    List<Parents> parents = (List<Parents>)pager.getList();
                    if(parents==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Parents item: parents){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getName());
                        jo.put("phoneNum",item.getPhone_num());
                        jo.put("isVip",item.getStudent().isVip());
                        ja.add(jo);
                    }
                    break;
                case "GartenNews":
                    List<GartenNews> gartenNews = (List<GartenNews>)pager.getList();
                    if(gartenNews==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(GartenNews item:gartenNews){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("title",item.getTitle());
                        jo.put("date",item.getPublishDate().toString().contains(".")?item.getPublishDate().toString().split("\\.")[0]:item.getPublishDate().toString());
                        ja.add(jo);
                    }
                    break;
                case "FoodRecord":
                    List<FoodRecord> foodRecords = (List<FoodRecord>)pager.getList();
                    if(foodRecords==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(FoodRecord item: foodRecords){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("content",item.getRecords());
                        jo.put("className",classdao.queryClass(item.getClass_id()).getName());
                        ja.add(jo);
                    }
                    break;
                case "CheckinRecords":
                    List<CheckinRecords> checkinRecords = (List<CheckinRecords>)pager.getList();
                    if(checkinRecords==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(CheckinRecords item: checkinRecords){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("babyName",studentdao.queryStudent(item.getStu_id()).getName());
                        jo.put("state1",item.getState_1());
                        jo.put("state2",item.getState_2());
                        jo.put("state3",item.getState_3());
                        jo.put("state4",item.getState_4());
                        ja.add(jo);
                    }
                    break;
                case "ClassNotification":
                    List<ClassNotification> classNotifications = (List<ClassNotification>)pager.getList();
                    if(classNotifications==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(ClassNotification item:classNotifications){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("title",item.getTitle());
                        jo.put("className",classdao.queryClass(item.getClass_id()).getName());
                        jo.put("date",item.getDate().toString().contains(".")?item.getDate().toString().split("\\.")[0]:item.getDate().toString());
                        ja.add(jo);
                    }
                    break;
                case "HomeWork":
                    List<HomeWork> homeWorks = (List<HomeWork>)pager.getList();
                    if(homeWorks==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(HomeWork item: homeWorks){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("className",classdao.queryClass(item.getClass_id()).getName());
                        jo.put("date",item.getDate().toString().contains(".")?item.getDate().toString().split("\\.")[0]:item.getDate().toString());
                        ja.add(jo);
                    }
                    break;
                case "ClassActivity":
                    List<ClassActivity> classActivities = (List<ClassActivity>)pager.getList();
                    if(classActivities==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(ClassActivity item:classActivities){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("title",item.getTitle());
                        jo.put("date",item.getStart_date().toString().contains(".")?item.getStart_date().toString().split("\\.")[0]:item.getStart_date().toString());
                        jo.put("partiNum",item.getBaby_ids().split(";").length);
                        jo.put("totalNum",item.getParticipate_num());
                        ja.add(jo);
                    }
                    break;
                case "FeedBack":
                    List<FeedBack> feedBacks = (List<FeedBack>)pager.getList();
                    if(feedBacks==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(FeedBack item: feedBacks){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("content",item.getContent());
                        jo.put("publisherName",getNameFromRoleTypeAndRoleId(item.getUser_type(), item.getUser_id()));
                        jo.put("publisherPhone",getPhoneFromRoleTypeAndRoleId(item.getUser_type(), item.getUser_id()));
                        jo.put("date",item.getDate().toString().contains(".")?item.getDate().toString().split("\\.")[0]:item.getDate().toString());
                        ja.add(jo);
                    }
                    break;
                case "Camera":
                    List<Camera> cameras = (List<Camera>)pager.getList();
                    if(cameras==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(Camera item:cameras){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",item.getManufactory());
                        jo.put("schoolName",item.getGarten().getName());
                        jo.put("state",item.getState());
                        ja.add(jo);
                    }
                    break;
                case "BabyKnowledge":
                    List<BabyKnowledge> babyKnowledges = (List<BabyKnowledge>)pager.getList();
                    if(babyKnowledges==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(BabyKnowledge item:babyKnowledges){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("title",item.getQuestion());
                        jo.put("date",item.getDate().toString().contains(".")?item.getDate().toString().split("\\.")[0]:item.getDate().toString());
                        ja.add(jo);
                    }
                    break;
                case "SystemNotification":
                    List<SystemNotification> systemNotifications = (List<SystemNotification>)pager.getList();
                    if(systemNotifications==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(SystemNotification item: systemNotifications){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("content",item.getContent());
                        jo.put("publisherName",administratordao.queryAdministrator(item.getPublishId()).getName());
                        jo.put("date",item.getDate().toString().contains(".")?item.getDate().toString().split("\\.")[0]:item.getDate().toString());
                        ja.add(jo);
                    }
                    break;
                case "DailyLog":
                    List<DailyLog> dailyLogs = (List<DailyLog>)pager.getList();
                    if(dailyLogs==null){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "无记录");
                        return jobOut.toString();
                    }
                    for(DailyLog item:dailyLogs){
                        JSONObject jo = new JSONObject();
                        jo.put("id",item.getId());
                        jo.put("name",getNameFromRoleTypeAndRoleId(item.getUser_type(),item.getUser_id()));
                        jo.put("content",item.getOp_content());
                        jo.put("date",item.getLog_date().toString().contains(".")?item.getLog_date().toString().split("\\.")[0]:item.getLog_date().toString());
                        ja.add(jo);
                    }
                    break;
                default:
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "操作失败");
                    return jobOut.toString();
            }

            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    public static String GenerateSqlFromInput(String tableName,JSONObject jobin){
        String result = "from "+tableName+" query where ";
        Iterator ir = jobin.keys();
        while(ir.hasNext()){
            String key = ir.next().toString();
            String value = jobin.get(key).toString();
            if(key.equals("roleType")||key.equals("roleId")||key.equals("queryType")||key.equals("pageSize")||key.equals("pageNum")){  //这几个不作为参数解析
                continue;
            }
            if(key.equals("date")){
                Calendar cal=Calendar.getInstance();//使用日历类
                int year=cal.get(Calendar.YEAR);//得到年
                int month=cal.get(Calendar.MONTH)+1;//得到月，因为从0开始的，所以要加1
                int day=cal.get(Calendar.DAY_OF_MONTH);//得到天
                result+="year(query.date)="+year+" and month(query.date)="+month+" and day(query.date)="+day+" and";
            }else{
                result+="query."+key+"="+"\'"+value+"\'"+" and";
            }
        }
        result = result.substring(0,result.length()-4);
        return result;
    }


 



    /**
     * 判断传入参数中json是否合法
     */
    public String judgeValidationOfInputJson(String inputString, String... args) {
        JSONObject jobIn = JSONObject.fromObject(inputString);
        JSONObject jobOut = new JSONObject();
        for (String item : args) {
            if (!jobIn.containsKey(item)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "未传入" + item);
                break;
            }
        }
        if (jobOut.toString().equals("{}")) {
            return "";
        } else
            return jobOut.toString();
    }


    /**
     * 根据角色类型和角色id，返回name
     *
     * @param type
     * @param id
     * @return
     */
    public String getNameFromRoleTypeAndRoleId(int type, long id) {
        String result = "";
        switch (type) {
            case 1:
            case 2:
                Teacher t = teacherdao.queryTeacher(id);
                result = t.getName();
                break;
            case 3:
                Parents p = parentsdao.queryParents(id);
                result = p.getName();
                break;
            case 4:
                Agent agent = agentdao.queryAgent(id);
                result = agent.getName();
                break;
            case 5:
                Administrator administrator = administratordao.queryAdministrator(id);
                result = administrator.getName();
                break;
        }
        return result;
    }


    /**
     * 根据角色类型和角色id，返回手机号
     *
     * @param type
     * @param id
     * @return
     */
    public String getPhoneFromRoleTypeAndRoleId(int type, long id) {
        String result = "";
        switch (type) {
            case 1:
            case 2:
                Teacher t = teacherdao.queryTeacher(id);
                result = t.getPhone_num();
                break;
            case 3:
                Parents p = parentsdao.queryParents(id);
                result = p.getPhone_num();
                break;
            case 4:
                Agent agent = agentdao.queryAgent(id);
                result = agent.getPhone_num();
                break;
            case 5:
                Administrator administrator = administratordao.queryAdministrator(id);
                result = administrator.getPhone_num();
                break;
        }
        return result;
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
