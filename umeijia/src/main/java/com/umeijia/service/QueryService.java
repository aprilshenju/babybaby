package com.umeijia.service;

import com.umeijia.dao.*;
import com.umeijia.util.GlobalStatus;
import com.umeijia.vo.*;
import com.umeijia.vo.Class;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.print.attribute.standard.JobImpressions;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
//            String hql = GenerateSqlFromInput(queryType,jobIn);
//            Pager pager = new Pager();
//            pager.setPageNumber(pageNum);
//            pager.setPageSize(pageSize);
//            pager = querydao.queryPager(hql,pager);
            JSONArray ja = new JSONArray();
            String checkInput2 = "";
            switch(queryType){
                case"Teacher":
                    if(roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId", "phoneNum", "classId");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_teacher = jobIn.getInt("schoolId");
                    String phoneNum_teacher = jobIn.getString("phoneNum");
                    int classId_teacher = jobIn.getInt("classId");
                    List<Teacher> teachers = new ArrayList<Teacher>();
                    /**
                     * 什么都不传就返回学校的所有老师
                     */
                    if(phoneNum_teacher.equals("")&&classId_teacher==-1){
                        teachers = teacherdao.queryTeachersByGarten(schoolId_teacher);
                    }
                    else if(!phoneNum_teacher.equals("")){
                        Teacher t = teacherdao.queryTeacherBySchoolAndPhone(jobIn.getString("phoneNum"),schoolId_teacher);
                        if(t!=null)
                        teachers.add(t);
                    }
                    else if(classId_teacher!=-1){
                        Set<Teacher> teachersInSet = classdao.queryClass(jobIn.getInt("classId")).getTeachers();
                        if(teachersInSet!=null){
                            for(Teacher item :teachersInSet){
                                teachers.add(item);
                            }
                        }
                    }
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
                    if(roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId", "className");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_class = jobIn.getInt("schoolId");
                    String className_class = jobIn.getString("className");
                    List<Class>  classes = new ArrayList<Class>();
                    //如果不传className,直接返回所有的班级
                    if(className_class.equals("")){
                        classes = classdao.queryClassBySchoolId(schoolId_class);
                    }else{
                        classes = classdao.queryClassBySchoolIdAndClassName(schoolId_class,className_class);
                    }
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
                    if(roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId", "classId","babyName");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_student = jobIn.getInt("schoolId");
                    int classid_student = jobIn.getInt("classId");
                    String babyName_student = jobIn.getString("babyName");
                    List<Student> students = new ArrayList<Student>();
                    if(babyName_student.equals("")&&classid_student==-1){
                        students = studentdao.queryStudentBySchool(schoolId_student);
                    }else if(classid_student!=-1){
                        if(babyName_student.equals("")){
                            students = studentdao.queryStudentByClassId(classid_student);
                        }else{
                            students = studentdao.queryStudentByClassIdAndStudentName(classid_student,babyName_student);
                        }
                    }else if(!babyName_student.equals("")){
                        students = studentdao.queryStudentByStudentName(babyName_student);
                    }
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
                    if(roleType!=5){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "agentId");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    List<Agent> agents = new ArrayList<Agent>();
                    int agentId_agent = jobIn.getInt("agentId");
                    /**
                     * 不传id就显示所有的加盟商
                     */
                    if(agentId_agent==-1){
                        agents = agentdao.queryAgents();
                    }else{
                        Agent a = agentdao.queryAgent(agentId_agent);
                        if(a!=null)
                        agents.add(a);
                    }
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
                    if(roleType!=5&&roleType!=4){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_Kindergarten = jobIn.getInt("schoolId");
                    List<Kindergarten> kindergartens = new ArrayList<Kindergarten>();
                    if(schoolId_Kindergarten==-1){
                        if(roleType==5){  //查看所有的学校
                            kindergartens = kindergartendao.queryKindergartens();
                        }else if(roleType==4){  //查看自己的学校
                            kindergartens = kindergartendao.queryKindergartens(roleId);
                        }

                    }else{
                        Kindergarten kg = kindergartendao.queryKindergarten(schoolId_Kindergarten);
                        if(kg!=null){
                            kindergartens.add(kg);
                        }
                    }
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
                    if(roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","classId","phoneNum");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_parent = jobIn.getInt("schoolId");
                    int classId_parent = jobIn.getInt("classId");
                    String phone_parent = jobIn.getString("phoneNum");
                    //什么都不传显示本校的所有家长
                    List<Parents> parents = new ArrayList<>();
                    if(classId_parent==-1&&phone_parent.equals("")){
                        parents = parentsdao.queryParentssByGarten(schoolId_parent);
                    }else if(!phone_parent.equals("")){
                        Parents p = parentsdao.queryParents(phone_parent);
                        if(p!=null)
                        parents.add(p);
                    }else if(classId_parent!=-1){
                        parents = parentsdao.queryParentsByClassId(classId_parent);
                    }

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
                    if(roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","title");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_gartenNews = jobIn.getInt("schoolId");
                    String title_gartenNews = jobIn.getString("title");
                    List<GartenNews> gartenNews = new ArrayList<>();
                    if(title_gartenNews.equals("")){
                        gartenNews = gartennewsdao.queryGartenNewss(schoolId_gartenNews);
                    }else{
                        gartenNews = gartennewsdao.queryGartenNewssByShoolIdAndTitle(schoolId_gartenNews,title_gartenNews);
                    }

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
                    if(roleType!=1&&roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","classId");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_foodRecord = jobIn.getInt("schoolId");
                    int classId_foodRecord = jobIn.getInt("classId");
                    List<FoodRecord> foodRecords = new ArrayList<>();
                    if(classId_foodRecord==-1){
                        if(roleType==1){ //如果不传则老师查看自己班级的，院长查看所有班级的
                            for(Class item:teacherdao.queryTeacher(roleId).getClasses()){
                                foodRecords.addAll(foodrecorddao.queryFoodRecordList(item.getId()));
                            }
                        }else if(roleType==2){
                            foodRecords = foodrecorddao.queryFoodRecordListBySchool(schoolId_foodRecord);
                        }
                    }else{
                        foodRecords = foodrecorddao.queryFoodRecordList(classId_foodRecord);
                    }

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
                    if(roleType!=1&&roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","classId","year","month","day","babyName");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_checkinRecords = jobIn.getInt("schoolId");
                    int classId_checkinRecords = jobIn.getInt("classId");
                    int year_checkinRecords = jobIn.getInt("year");
                    int month_checkinRecords = jobIn.getInt("month");
                    int day_checkinRecords = jobIn.getInt("day");
                    String babyName_checkinRecords = jobIn.getString("babyName");
                    List<CheckinRecords> checkinRecords = new ArrayList<CheckinRecords>();
                    //什么都没选，院长显示学校的所有签到记录，老师显示班级的所有记录
                    if(classId_checkinRecords==-1&&year_checkinRecords==-1&&babyName_checkinRecords.equals("")){
                        if(roleId==1){
                            for(Class item:teacherdao.queryTeacher(roleId).getClasses()){
                                checkinRecords.addAll(checkinrecorddao.queryCheckinRecordsByClass(item.getId()));
                            }
                        }else if(roleType==2){
                            checkinRecords = checkinrecorddao.queryCheckinRecordsBySchool(schoolId_checkinRecords);
                        }
                    }else if(classId_checkinRecords!=-1){//传了班级
                        if(year_checkinRecords!=-1){//传了班级，传了日期
                            if(babyName_checkinRecords.equals("")){//传了班级，传了日期，没传姓名
                                checkinRecords = checkinrecorddao.queryCheckinRecordsByClassAndTime(classId_checkinRecords,year_checkinRecords,month_checkinRecords,day_checkinRecords);
                            }else{//传了班级，传了日期，也传了姓名
                                for(Student item:studentdao.queryStudentByStudentName(babyName_checkinRecords)){
                                    List<CheckinRecords> crs = checkinrecorddao.queryCheckinRecordsByBabyNameAndClassAndTime(classId_checkinRecords,item.getId(),year_checkinRecords,month_checkinRecords,day_checkinRecords);
                                    if(crs!=null){
                                        checkinRecords.addAll(crs);
                                    }
                                }
                            }
                        }else{
                            if(babyName_checkinRecords.equals("")){//传了班级，没传日期，没传姓名
                                checkinRecords = checkinrecorddao.queryCheckinRecordsByClass(classId_checkinRecords);
                            }else{//传了班级，没传日期，传了姓名
                                for(Student item:studentdao.queryStudentByStudentName(babyName_checkinRecords)){
                                    List<CheckinRecords> crs = checkinrecorddao.queryCheckinRecordsByClassAndBabyId(classId_checkinRecords,item.getId());
                                    if(crs!=null){
                                        checkinRecords.addAll(crs);
                                    }
                                }
                            }
                        }

                    }else if(year_checkinRecords!=-1){  //没传班级，传了日期
                        if(babyName_checkinRecords.equals("")){//没传班级，传了日期，没传姓名
                            checkinRecords = checkinrecorddao.queryCheckinRecordsByTime(year_checkinRecords,month_checkinRecords,day_checkinRecords);
                        }else{//没传班级，传了日期，传了姓名
                            for(Student item:studentdao.queryStudentByStudentName(babyName_checkinRecords)){
                                List<CheckinRecords> crs = checkinrecorddao.queryCheckinRecordsByBabyAndTime(item.getId(),year_checkinRecords,month_checkinRecords,day_checkinRecords);
                                if(crs!=null){
                                    checkinRecords.addAll(crs);
                                }
                            }
                        }

                    }else if(!babyName_checkinRecords.equals("")){  //没传班级和日期，只传了宝贝姓名
                        for(Student item:studentdao.queryStudentByStudentName(babyName_checkinRecords)){
                            List<CheckinRecords> crs = checkinrecorddao.queryCheckinRecordsByBabyId(item.getId());
                            if(crs!=null){
                                checkinRecords.addAll(crs);
                            }
                        }
                    }

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
                    if(roleType!=1&&roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","classId","title");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_classnotification = jobIn.getInt("schoolId");
                    int classId_classnotification = jobIn.getInt("classId");
                    String title_classnotification = jobIn.getString("title");
                    List<ClassNotification> classNotifications = new ArrayList<>();
                    if(classId_classnotification==-1&&title_classnotification.equals("")){
                        if(roleType==2){
                            classNotifications = classnotificationdao.queryClassNotificationsBySchool(schoolId_classnotification);
                        }else if(roleType==1){
                            for(Class item:teacherdao.queryTeacher(roleId).getClasses()){
                                classNotifications.addAll(classnotificationdao.queryClassNotifications(item.getId()));
                            }
                        }

                    }else if(classId_classnotification!=-1){
                        if(!title_classnotification.equals("")){
                            classNotifications = classnotificationdao.queryClassNotifications(classId_classnotification);
                        }else{
                            classNotifications = classnotificationdao.queryClassNotificationsByClassAndTitle(classId_classnotification,title_classnotification);
                        }
                    }else if(!title_classnotification.equals("")){
                        classNotifications = classnotificationdao.queryClassNotificationsByTitle(title_classnotification);
                    }
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
                    if(roleType!=1&&roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","classId","title");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_homework = jobIn.getInt("schoolId");
                    int classId_homework = jobIn.getInt("classId");
                    String title_homework  = jobIn.getString("title");
                    List<HomeWork> homeWorks = new ArrayList<>();
                    if(classId_homework==-1&&title_homework.equals("")){
                        if(roleType==2){
                            homeWorks = homeworkdao.queryHomeWorksBySchool(schoolId_homework);
                        }else if(roleType==1){
                            for(Class item:teacherdao.queryTeacher(roleId).getClasses()){
                                homeWorks.addAll(homeworkdao.queryHomeWorks(item.getId()));
                            }
                        }

                    }else if(classId_homework!=-1){
                        if(!title_homework.equals("")){
                            homeWorks = homeworkdao.queryHomeWorks(classId_homework);
                        }else{
                            homeWorks = homeworkdao.queryHomeWorksByClassidAndTitle(classId_homework,title_homework);
                        }
                    }else if(!title_homework.equals("")){
                        homeWorks = homeworkdao.queryHomeWorksByTitle(title_homework);
                    }
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
                    if(roleType!=1&&roleType!=2){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","classId","title");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_classActivity = jobIn.getInt("schoolId");
                    int classId_classActivity = jobIn.getInt("classId");
                    String title_classActivity  = jobIn.getString("title");
                    List<ClassActivity> classActivities = new ArrayList<>();
                    if(classId_classActivity==-1&&title_classActivity.equals("")){
                        if(roleType==2){
                            classActivities = classactivitydao.queryOneClassActivitysListBySchoolId(schoolId_classActivity);
                        }else if(roleType==1){
                            for(Class item:teacherdao.queryTeacher(roleId).getClasses()){
                                classActivities.addAll(classactivitydao.queryOneClassActivitysList(item.getId()));
                            }
                        }

                    }else if(classId_classActivity!=-1){
                        if(!title_classActivity.equals("")){
                            classActivities = classactivitydao.queryOneClassActivitysList(classId_classActivity);
                        }else{
                            classActivities = classactivitydao.queryOneClassActivitysListByClassAndTitle(classId_classActivity,title_classActivity);
                        }
                    }else if(!title_classActivity.equals("")){
                        classActivities = classactivitydao.queryOneClassActivitysListByTitle(title_classActivity);
                    }
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
                    if(roleType!=5){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    List<FeedBack> feedBacks = feedbackdao.getFeedBackList();
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
                    if(roleType!=5&&roleType!=4){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "schoolId","name");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int schoolId_camera = jobIn.getInt("schoolId");
                    String name_camera = jobIn.getString("name");
                    List<Camera> cameras = new ArrayList<>();
                    if(schoolId_camera==-1&&name_camera.equals("")){  //什么都没传
                        if(roleType==5){
                            cameras = cameradao.getCamerasList();
                        }else if(roleType==4){
                            for(Kindergarten item:agentdao.queryAgent(roleId).getGartens()){
                                if(cameradao.getCamerasListBySchoolId(item.getId())!=null){
                                    cameras.addAll(cameradao.getCamerasListBySchoolId(item.getId()));
                                }
                            }
                        }
                    }else if(schoolId_camera!=-1){
                        if(name_camera.equals("")){
                            cameras = cameradao.getCamerasListBySchoolId(schoolId_camera);
                        }else{
                            cameras = cameradao.getCamerasListBySchoolIdAndCameraName(schoolId_camera,name_camera);
                        }
                    }else if(!name_camera.equals("")){
                        cameras = cameradao.getCamerasListByCameraName(name_camera);
                    }
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
                    if(roleType!=5){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "title");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    List<BabyKnowledge> babyKnowledges = new ArrayList<>();
                    String title_babyknowledge = jobIn.getString("title");
                    if(title_babyknowledge.equals("")){
                        babyKnowledges = babyknowledgedao.getBabyKnowledgeList();
                    }else{
                        babyKnowledges = babyknowledgedao.queryBabyKnowledgeByTitle(title_babyknowledge);
                    }

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
                    if(roleType!=5){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    List<SystemNotification> systemNotifications = systemnotificationdao.querySystemNotifications();
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
                    if(roleType!=5){
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "没权限");
                        return jobOut.toString();
                    }
                    checkInput2 = judgeValidationOfInputJson(queryInfo, "year","month","day","phoneNum");
                    if (!checkInput2.equals("")) {
                        return checkInput2;
                    }
                    int year_log = jobIn.getInt("year");
                    int month_log = jobIn.getInt("month");
                    int day_log = jobIn.getInt("day");
                    String phoneNum_log = jobIn.getString("phoneNum");
                    List<DailyLog> dailyLogs = new ArrayList<>();
                    if(year_log==-1&&phoneNum_log.equals("")){
                        dailyLogs =dailylogdao.queryDailyLogs();
                    }else if(year_log!=-1){
                        if(phoneNum_log.equals("")){
                            dailyLogs = dailylogdao.queryDailyLogByDate(year_log,month_log,day_log);
                        }else{
                            long userType = getUserTypeAndIdFromPhone(phoneNum_log)[0];
                            long userId = getUserTypeAndIdFromPhone(phoneNum_log)[1];
                            dailyLogs =dailylogdao.queryDailyLogByDateAndUserTypeAndUserId(year_log,month_log,day_log,userType,userId);
                        }
                    }else if(!phoneNum_log.equals("")){
                        long userType = getUserTypeAndIdFromPhone(phoneNum_log)[0];
                        long userId = getUserTypeAndIdFromPhone(phoneNum_log)[1];
                        dailyLogs = dailylogdao.queryDailyLogByUserTypeAndUserId(userType,userId);
                    }

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

    public long[] getUserTypeAndIdFromPhone(String phoneNum){
        long[] result = new long[2];
        Teacher t = teacherdao.queryTeacher(phoneNum);
        if(t!=null){
            if(t.getIs_leader()){
                result[0]=2;
                result[1] = t.getId();
            }else{
                result[0]=2;
                result[1] = t.getId();
            }
            return result;
        }else{
            Parents p = parentsdao.queryParents(phoneNum);
            if(p!=null){
                result[0]=3;
                result[1] = p.getId();
                return result;
            }else{
                Agent a = agentdao.queryAgent(phoneNum);
                if(a!=null){
                    result[0]=4;
                    result[1] = a.getId();
                    return result;
                }else{
                    Administrator ad = administratordao.queryAdministrator(phoneNum);
                    if(ad!=null){
                        result[0]=5;
                        result[1] = ad.getId();
                        return result;
                    }
                }
            }
        }
        return result;
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
