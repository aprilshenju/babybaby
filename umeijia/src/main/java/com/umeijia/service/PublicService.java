package com.umeijia.service;

import com.sun.jersey.multipart.FormDataParam;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shenju on 2016/6/20.
 */

@Service
@Path("/public_service")
public class PublicService {
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

    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2() {

        return "welcom to UMJ server... public service ";
    }

    @Path("/addBabyShowTime")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addBabyShowTime(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            JSONObject jobIn = JSONObject.fromObject(showTimeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int showTimeType = jobIn.getInt("showTimeType");
            BabyShowtime bst = new BabyShowtime();
            switch (roleType) {
                case 1: //1,2为老师
                case 2:
                    bst.setTeacher_id(roleId);
                    bst.setParent_id(-1);
                    break;
                case 3:
                    bst.setParent_id(roleId);
                    bst.setTeacher_id(-1);
                    break;
            }
            bst.setShow_type(showTimeType);
            bst.setDescription(jobIn.getString("description"));
            bst.setDate(new Date(jobIn.getString("date")));
            bst.setImage_urls(jobIn.getString("urls"));
            bst.setClass_id(jobIn.getInt("classId"));
            bst.setBaby_id(jobIn.getInt("babyId"));
            bst.setValid(true);
            int isShareToFootPrint = jobIn.getInt("isShareToFootPrint");
            if (roleType == 3 && isShareToFootPrint == 1) {//家长选择同时分享到足迹
                BabyFootPrint bfp = new BabyFootPrint();
                bfp.setShow_type(bst.getShow_type());
                bfp.setDescription(bst.getDescription());
                bfp.setDate(bst.getDate());
                bfp.setImage_urls(bst.getImage_urls());
                bfp.setClass_id(bst.getClass_id());
                bfp.setBaby_id(bst.getBaby_id());
                bfp.setValid(bst.isValid());
                bfp.setParent_id(bst.getParent_id());
                babyfootprintdao.addBabyFootPrint(bfp);
            }
            babyshowtimedao.addBabyShowtime(bst);
            jobOut.put("resultCode", "success");
            jobOut.put("resultDesc", "添加成功");
        } catch (Exception e) {
            jobOut.put("resultCode", "error");
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    @Path("/queryBabyShowTime")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBabyShowTime(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            JSONObject jobIn = JSONObject.fromObject(showTimeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");  //分页
            List<BabyShowtime> result = new ArrayList<BabyShowtime>();
            switch (roleType) {
                case 1: //老师查询
                    result = babyshowtimedao.queryBabyShowtimesByTeacher(roleId);
                    break;
                case 2:
                    result = babyshowtimedao.queryBabyShowtimesByClass(roleId);
                    break;
                case 3:  //家长查询
                    result = babyshowtimedao.queryBabyShowtimesByParents(roleId);
                    break;
            }
            JSONArray ja = new JSONArray();
            for (BabyShowtime item : result) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                if (item.getTeacher_id() != -1) {  //发布者信息
                    Teacher teacher = teacherdao.queryTeacher(item.getTeacher_id());
                    jo.put("publisherId", teacher.getId());
                    jo.put("publisherName", teacher.getName());
                    jo.put("avatar", teacher.getAvatar_path());
                } else if (item.getParent_id() != -1) {
                    Parents parents = parentsdao.queryParents(item.getParent_id());
                    jo.put("publisherId", parents.getId());
                    jo.put("publisherName", parents.getName());
                    jo.put("avatar", parents.getAvatar_path());
                }
                jo.put("description", item.getDescription());
                jo.put("date", item.getDate());
                jo.put("type", item.getShow_type());
                jo.put("urls", item.getImage_urls());
                JSONArray commentsArray = new JSONArray();
                /**
                 * comments的jsonarray
                 */
                List<ShowtimeComments> comments = showtimecommentsdao.queryShowtimeComments(item.getId());
                for (ShowtimeComments commentsItem : comments) {
                    JSONObject job = new JSONObject();
                    job.put("userRoleType", commentsItem.getUser_type());
                    job.put("userId", commentsItem.getUser_id());
                    job.put("userName", getNameFromRoleTypeAndRoleId(commentsItem.getUser_type(), commentsItem.getUser_id()));
                    job.put("isLike", commentsItem.isSay_good());
                    job.put("responseUserType", commentsItem.getResponse_user_type());
                    job.put("responseUserId", commentsItem.getResponse_user_id());
                    job.put("responseUserName", getNameFromRoleTypeAndRoleId(commentsItem.getResponse_user_type(), commentsItem.getResponse_user_id()));
                    job.put("content", commentsItem.getComment_content());
                    commentsArray.add(job);
                }
                jo.put("comments", commentsArray.toString());

                ja.add(jo);
            }
            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("hasNextPage", true); //是否有下一页
            jobOut.put("totalCount", ja.size());  //总共返回多少条记录
            jobOut.put("resultCode", "success");
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", "error");
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
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
        return true;
    }

    @Path("/deleteBabyShowTime")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteBabyShowTime(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            JSONObject jobIn = JSONObject.fromObject(showTimeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            BabyShowtime bst = babyshowtimedao.queryBabyShowtime(id);
            if (bst.getParent_id() != -1) {  //这两个判断的目的是：要本人发布的才能删
                if (roleType == 3 && roleId == bst.getParent_id()) {
                    babyshowtimedao.invalidShowtime(id);
                }
            } else if (bst.getTeacher_id() != -1) {
                if ((roleType == 1 || roleType == 2) && roleId == bst.getTeacher_id()) {
                    babyshowtimedao.invalidShowtime(id);
                }
            }
            jobOut.put("resultCode", "success");
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", "error");
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    @Path("/likeOrComment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String likeOrComment(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            JSONObject jobIn = JSONObject.fromObject(showTimeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            ShowtimeComments stc = new ShowtimeComments();
            stc.setDate(new Date());
            stc.setUser_id(roleId);
            stc.setUser_type(roleType);
            stc.setShowtime(babyshowtimedao.queryBabyShowtime(id));
            stc.setComment_content(jobIn.getString("content"));
            stc.setResponse_user_id(jobIn.getInt("responseUserId"));
            stc.setResponse_user_type(jobIn.getInt("responseUserType"));
            if (jobIn.getInt("type") == 1) { //点赞
                stc.setSay_good(true);
            } else if (jobIn.getInt("type") == 2) {
                stc.setSay_good(false);
            }
            showtimecommentsdao.addShowtimeComments(stc);
            jobOut.put("resultCode", "success");
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", "error");
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询校园新闻
     * @param reqJson
     * @return
     */
    @Path("/querySchoolNews")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String querySchoolNews(@RequestBody String reqJson) {
        JSONObject returnJsonObject = new JSONObject();
        long schoolId;
        int pageNum;
        if (reqJson == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject.containsKey("schoolId")) {
            schoolId = jsonObject.getLong("schoolId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数schoolId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("pageNum")) {
            pageNum = jsonObject.getInt("pageNum");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数pageNum");
            return returnJsonObject.toString();
        }
        List<GartenNews> newsList = gartennewsdao.queryGartenNewss(schoolId);
        if (newsList != null) {
            JSONArray data = new JSONArray();
            Iterator iterator = newsList.iterator();
            while (iterator.hasNext()) {
                GartenNews news = (GartenNews) iterator.next();
                JSONObject item = new JSONObject();
                item.put("id", news.getId());
                item.put("title", news.getTitle());
                item.put("summary", news.getSummary());
                item.put("description", news.getDescription());
                item.put("imageUrls", news.getImage_urls());
                item.put("teacherName", news.getTeacher().getName());
                item.put("publishDate", news.getPublishDate());
                item.put("modifyDate", news.getModifyDate());
                data.add(item);
            }
            returnJsonObject.put("data", data);
            //测试，待加入分页功能
            returnJsonObject.put("totalCount", newsList.size());
            returnJsonObject.put("hasNextPage", false);
            returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
            returnJsonObject.put("resultDesc", "操作成功");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "操作失败");
        }
        return returnJsonObject.toString();
    }

    /**
     * 添加或更新摄像头
     * @param reqJson
     * @return
     */
    @Path("/addOrUpdateCamera")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrUpdateCamera(@RequestBody String reqJson) {
        JSONObject returnJsonObject = new JSONObject();
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        int type;
        long id;
        int roleType;
        long roleId;
        String ipUrl;
        String videoUrl;
        String description;
        String manufactory;
        long classId;
        long gartenId;
        String cameraType;
        String state;
        String thumbPath;
        String activePeriod;
        Boolean isPublic;
        String addDate;
        String modifyDate;
        if (jsonObject.containsKey("type")) {
            type = jsonObject.getInt("type");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数type");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("roleType")) {
            roleType = jsonObject.getInt("roleType");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数roleType");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("roleId")) {
            roleId = jsonObject.getLong("roleId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数roleId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("ipUrl")) {
            ipUrl = jsonObject.getString("ipUrl");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数ipUrl");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("videoUrl")) {
            videoUrl = jsonObject.getString("videoUrl");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数videoUrl");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("description")) {
            description = jsonObject.getString("description");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数description");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("manufactory")) {
            manufactory = jsonObject.getString("manufactory");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数manufactory");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("classId")){
            classId = jsonObject.getLong("classId");
        }else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数classId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("gartenId")) {
            gartenId = jsonObject.getLong("gartenId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数gartenId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("cameraType")) {
            cameraType = jsonObject.getString("cameraType");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数cameraType");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("state")) {
            state = jsonObject.getString("state");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数state");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("thumbPath")) {
            thumbPath = jsonObject.getString("thumbPath");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数thumbPath");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("activePeriod")) {
            activePeriod = jsonObject.getString("activePeriod");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数activePeriod");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("isPublic")) {
            isPublic = jsonObject.getBoolean("isPublic");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数isPublic");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("addDate")) {
            addDate = jsonObject.getString("addDate");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数addDate");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("modifyDate")) {
            modifyDate = jsonObject.getString("modifyDate");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数modifyDate");
            return returnJsonObject.toString();
        }
        switch (type) {
            case 0://添加
                Class clz = new Class();
                clz.setId(classId);
                Kindergarten kindergarten = new Kindergarten();
                kindergarten.setId(gartenId);
                Camera camera = new Camera(ipUrl, videoUrl, description, manufactory, clz, kindergarten, cameraType, state, thumbPath, activePeriod, isPublic);
                if (cameradao.addCamera(camera)) {
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "添加摄像头失败");
                }
                break;
            case 1://更新
                if (jsonObject.containsKey("id")) {
                    id = jsonObject.getLong("id");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "找不到参数id");
                    return returnJsonObject.toString();
                }
                Class clz1 = new Class();
                clz1.setId(classId);
                Kindergarten kindergarten1 = new Kindergarten();
                kindergarten1.setId(gartenId);
                Camera camera1 = new Camera(id, ipUrl, videoUrl, description, manufactory, clz1, kindergarten1, cameraType, state, thumbPath, activePeriod, isPublic);
                if(cameradao.updateCamera(camera1)){
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "更新摄像头失败");
                }
                break;
            default:
                returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                returnJsonObject.put("resultDesc", "操作类型错误，type应该为0或1");
                break;
        }
        return returnJsonObject.toString();
    }

    /**
     * 文件上传
     * 单个文件上传
     * @param ins
     * @param reqJson
     * @return
     */
    @Path("/fileUpload")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String fileUpload(@FormDataParam("fileData") InputStream ins, @FormDataParam("jsonArgs") String reqJson) {
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

    /**
     *发布或更新校园新闻
     * @param reqJson
     * @return
     */
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

    /**
     * 显示摄像头列表
     * @return
     */
    @Path("/publishOrUpdateSchoolNews")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String queryCamera(@RequestBody String reqJson){
        JSONObject returnJsonObject = new JSONObject();
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }

        long gardenId,classId;
        int pageNum;
        if(jsonObject.containsKey("gardenId")){
            gardenId = jsonObject.getLong("gardenId");
        }else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数gardenId");
            return returnJsonObject.toString();
        }
        if(jsonObject.containsKey("classId")){
            classId = jsonObject.getLong("classId");
        }else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数classId");
            return returnJsonObject.toString();
        }
        if(jsonObject.containsKey("pageNum")){
            pageNum = jsonObject.getInt("pageNum");
        }else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数pageNum");
            return returnJsonObject.toString();
        }

        return returnJsonObject.toString();
    }

}
