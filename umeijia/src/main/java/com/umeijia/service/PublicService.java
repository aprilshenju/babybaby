package com.umeijia.service;

import cn.jpush.api.push.model.notification.Notification;
import com.sun.jersey.multipart.FormDataParam;
import com.umeijia.dao.*;
import com.umeijia.util.*;
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
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shenju on 2016/6/20.
 */

@Service
@Path("/public_service")
public class PublicService {
    String baseDir = "/UMJ_IMG_SERVER";
    //类别路径
    String filePath = null;
    String imgUrls = null;
    //原始图片存放路径
    String imgPath = null;
    //视频存放路径
    String videoPath = null;
    //小图存放路径
    String thumbDir = null;
    Thread thumbImgThread = null;
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


    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2() {
//        BabyFootPrint bfp = babyfootprintdao.queryBabyFootPrint(1);
//        return bfp.getDate()+bfp.getDescription();
        String alias = "138";
        String content = "hahaha jpush";
        Notification notification = Notification.newBuilder()
                .setAlert(content)
                .build();
        JpushUtil.notificationToTargetClient(alias, notification);
        return "welcom to UMJ server... public service ";
    }

    /**
     * 添加宝贝动态
     *
     * @param showTimeInfo
     * @param headers
     * @return
     */
    @Path("/addBabyShowTime")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addBabyShowTime(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(showTimeInfo, "roleType", "roleId", "showTimeType",
                    "description", "classId", "babyId", "isShareToFootPrint","imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
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
            bst.setDate(new Date());
            String imageUrls = jobIn.getString("imageUrls");
            bst.setImage_urls(imageUrls);
            bst.setClass_id(jobIn.getInt("classId"));
            bst.setBaby_id(jobIn.getInt("babyId"));
            bst.setValid(true);
            int isShareToFootPrint = jobIn.getInt("isShareToFootPrint");
            boolean addFlag1=true,addFlag2 = false;
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
                addFlag1 = babyfootprintdao.addBabyFootPrint(bfp);
            }
            addFlag2 = babyshowtimedao.addBabyShowtime(bst);
            if(addFlag1&&addFlag2){
                jobOut.put("id", bst.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "添加成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "添加失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询宝贝动态
     *
     * @param showTimeInfo
     * @param headers
     * @return
     */
    @Path("/queryBabyShowTime")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBabyShowTime(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(showTimeInfo, "roleType", "roleId", "classId", "pageNum","pageSize",
                    "querySelf");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(showTimeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int classId = jobIn.getInt("classId");
            /**
             * @shanji
             * 分页
             */
            int pageNum = jobIn.getInt("pageNum");
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);
            int querySelf = jobIn.getInt("querySelf");
            List<BabyShowtime> result = new ArrayList<BabyShowtime>();
            if (querySelf == 1) {  //是否是查自己的
                switch (roleType) {
                    case 1: //老师和园长查询
                    case 2:
                        pager = babyshowtimedao.queryBabyShowtimesByTeacher(roleId, pager);
                        result = pager.getList();
                        break;
                    case 3:  //家长查询
                        pager = babyshowtimedao.queryBabyShowtimesByParents(roleId, pager);
                        result = pager.getList();
                        break;
                }
            } else {  //不是查自己的，就直接按班级查
                pager = babyshowtimedao.queryBabyShowtimesPageByClass(classId, pager);
                result = pager.getList();
            }

            if (result == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
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
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                jo.put("type", item.getShow_type());
                jo.put("urls", item.getImage_urls());
                JSONArray commentsArray = new JSONArray();
                /**
                 * comments的jsonarray
                 */
                List<ShowtimeComments> comments = showtimecommentsdao.queryShowtimeComments(item.getId());
                if (comments != null && comments.size() != 0) {
                    for (ShowtimeComments commentsItem : comments) {
                        JSONObject job = new JSONObject();
                        job.put("userRoleType", commentsItem.getUser_type());
                        job.put("userId", commentsItem.getUser_id());
                        job.put("userName", getNameFromRoleTypeAndRoleId(commentsItem.getUser_type(), commentsItem
                                .getUser_id()));
                        job.put("isLike", commentsItem.isSay_good());
                        job.put("responseUserType", commentsItem.getResponse_user_type());
                        job.put("responseUserId", commentsItem.getResponse_user_id());
                        job.put("responseUserName", getNameFromRoleTypeAndRoleId(commentsItem.getResponse_user_type()
                                , commentsItem.getResponse_user_id()));
                        job.put("content", commentsItem.getComment_content());
                        commentsArray.add(job);
                    }

                }
                jo.put("comments", commentsArray.toString());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("pageCount", pager.getPageCount());
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共返回多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
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

    /**
     * 删除宝贝动态
     *
     * @param showTimeInfo
     * @param headers
     * @return
     */

    @Path("/deleteBabyShowTime")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteBabyShowTime(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(showTimeInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
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
            boolean deleteFlag = false;
            if (bst.getParent_id() != -1) {  //这两个判断的目的是：要本人发布的才能删
                if (roleType == 3 && roleId == bst.getParent_id()) {
                    deleteFlag = babyshowtimedao.invalidShowtime(id);
                }
            } else if (bst.getTeacher_id() != -1) {
                if ((roleType == 1 || roleType == 2) && roleId == bst.getTeacher_id()) {
                    deleteFlag = babyshowtimedao.invalidShowtime(id);
                }
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "动态不是该用户发布，不能删除");
                return jobOut.toString();
            }
            if(deleteFlag){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }

        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 对宝贝动态进行点赞或评论
     *
     * @param showTimeInfo
     * @param headers
     * @return
     */
    @Path("/likeOrComment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String likeOrComment(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(showTimeInfo, "roleType", "roleId", "id", "content",
                    "responseUserId", "responseUserType", "type");
            if (!checkInput.equals("")) {
                return checkInput;
            }
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
            if(showtimecommentsdao.addShowtimeComments(stc)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 对宝贝动态取消点赞
     *
     * @param showTimeInfo
     * @param headers
     * @return
     */
    @Path("/abortLike")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String abortLike(@RequestBody String showTimeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(showTimeInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(showTimeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int id = jobIn.getInt("id");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            ShowtimeComments stc = showtimecommentsdao.queryOneShowtimeComments(id);
            boolean flag = false;
            if(stc.getUser_type()==roleType&&stc.getUser_id()==roleId){
                stc.setSay_good(false);
                if(stc.getComment_content().equals("")){  //如果取消点赞，也没有评论内容，直接删除
                    flag = showtimecommentsdao.deleteShowtimeComments(stc.getId());
                }else{
                    flag =  showtimecommentsdao.updateShowtimeComments(stc);
                }
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "不能取消点赞");
                return jobOut.toString();
            }
            if(flag){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 添加或更新宝贝成长足迹
     *
     * @param footPrintInfo
     * @param headers
     * @return
     */
    @Path("/addOrEditFootPrint")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrEditFootPrint(@RequestBody String footPrintInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(footPrintInfo, "roleType", "roleId", "id",
                    "footPrintType", "description", "babyId", "type", "isShareToBabyShowTime", "classId","imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(footPrintInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int type = jobIn.getInt("type");
            int id = jobIn.getInt("id");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "只有家长能发布足迹");
                return jobOut.toString();
            }
            int footPrintType = jobIn.getInt("footPrintType");
            BabyFootPrint bfp = new BabyFootPrint();
            bfp.setParent_id(roleId);
            bfp.setDescription(jobIn.getString("description"));
            bfp.setShow_type(footPrintType);
            bfp.setValid(true);
            bfp.setBaby_id(jobIn.getInt("babyId"));
            bfp.setClass_id(jobIn.getInt("classId"));
            bfp.setDate(new Date());
            String imageUrls = jobIn.getString("imageUrls");
            bfp.setImage_urls(imageUrls);
            boolean flag1 = false,flag2=true;
            if (type == 1) {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH) + 1;
                int day = now.get(Calendar.DAY_OF_MONTH);
                if(babyfootprintdao.queryBabyFootprintsByParentAndDay(roleId,year,month,day)!=null){
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "家长一天只能发布一条足迹");
                    return jobOut.toString();
                }
                flag1 = babyfootprintdao.addBabyFootPrint(bfp);
            } else if (type == 2) { //编辑
                bfp.setId(id);
                flag1 = babyfootprintdao.updateBabyFootPrint(bfp);
            }
            if (jobIn.getInt("isShareToBabyShowTime") == 1) {
                BabyShowtime bst = new BabyShowtime();
                bst.setShow_type(footPrintType);
                bst.setDescription(bfp.getDescription());
                bst.setDate(bfp.getDate());
                bst.setImage_urls(bfp.getImage_urls());
                bst.setClass_id(bfp.getClass_id());
                bst.setBaby_id(bfp.getBaby_id());
                bst.setValid(bfp.isValid());
                bst.setTeacher_id(-1);
                bst.setParent_id(bfp.getParent_id());
                flag2 = babyshowtimedao.addBabyShowtime(bst);
            }
            if(flag1&&flag2){
                jobOut.put("id", bfp.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询宝贝成长足迹
     *
     * @param footPrintInfo
     * @param headers
     * @return
     */
    @Path("/queryFootPrint")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryFootPrint(@RequestBody String footPrintInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(footPrintInfo, "roleType", "roleId", "babyId", "pageNum","pageSize");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(footPrintInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int babyId = jobIn.getInt("babyId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int pageNum = jobIn.getInt("pageNum");  //分页
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);
            pager = babyfootprintdao.queryBabyFootPrintByPage(babyId, pager);
            List<BabyFootPrint> result = pager.getList();
            if (result == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (BabyFootPrint item : result) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("description", item.getDescription());
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                jo.put("type", item.getShow_type());
                jo.put("urls", item.getImage_urls());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("pageCount", pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 按月查询宝贝成长足迹
     *
     * @param footPrintInfo
     * @param headers
     * @return
     */
    @Path("/queryFootPrintByMonth")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryFootPrintByMonth(@RequestBody String footPrintInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(footPrintInfo, "roleType", "roleId", "babyId", "year",
                    "month");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(footPrintInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int babyId = jobIn.getInt("babyId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int year = jobIn.getInt("year");
            int month = jobIn.getInt("month");
            List<BabyFootPrint> result =
                    babyfootprintdao.queryBabyFootprintsByMonth(babyId, year, month);
            if (result == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (BabyFootPrint item : result) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("description", item.getDescription());
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                jo.put("type", item.getShow_type());
                jo.put("urls", item.getImage_urls());
                ja.add(jo);
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

    /**
     * 删除宝贝成长足迹
     *
     * @param footPrintInfo
     * @param headers
     * @return
     */
    @Path("/deleteFootPrint")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteFootPrint(@RequestBody String footPrintInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(footPrintInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(footPrintInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "家长才能删除足迹");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            BabyFootPrint bfp = babyfootprintdao.queryBabyFootPrint(id);
            boolean flag = false;
            if (bfp.getParent_id() == roleId) {  //判断的目的是：要本人发布的才能删
                flag = babyfootprintdao.invalidFootPrint(id);
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "足迹不是该用户发布，不能删除");
                return jobOut.toString();
            }
            if(flag){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 添加反馈
     *
     * @param feedbackInfo
     * @param headers
     * @return
     */
    @Path("/addFeedback")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addFeedback(@RequestBody String feedbackInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(feedbackInfo, "roleType", "roleId", "content");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(feedbackInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }

            FeedBack fb = new FeedBack();
            fb.setUser_type(roleType);
            fb.setUser_id(roleId);
            fb.setDate(new Date());
            fb.setContent(jobIn.getString("content"));
            fb.setRead_or_not(0);
            fb.setResponse("");
            if(feedbackdao.addFeedBack(fb)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "反馈成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "反馈失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 查询反馈
     *
     * @param feedbackInfo
     * @param headers
     * @return
     */
    @Path("/queryFeedBack")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryFeedBack(@RequestBody String feedbackInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(feedbackInfo, "roleType", "roleId");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(feedbackInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 5) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看反馈");
                return jobOut.toString();
            }

            List<FeedBack> result = feedbackdao.getFeedBackList();
            if (result == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (FeedBack item : result) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("roleType", item.getUser_type());
                jo.put("roleId", item.getUser_id());
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                jo.put("content", item.getContent());
                jo.put("response", item.getResponse());
                jo.put("readOrNot", item.getRead_or_not());
                ja.add(jo);
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

    /**
     * 更新关于我们
     *
     * @param aboutUsInfo
     * @param headers
     * @return
     */
    @Path("/updateAboutUs")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrUpdateAboutUs(@RequestBody String aboutUsInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(aboutUsInfo, "roleType", "roleId", "id", "companyAddress",
                    "companyName", "phoneNum", "email", "description", "qq", "currentVersion_teacher",
                    "currentVersion_parent");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(aboutUsInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
//            int type = jobIn.getInt("type");
            int id = jobIn.getInt("id");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 5) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限更新关于我们");
                return jobOut.toString();
            }

            BasicInfo bi = new BasicInfo();
            bi.setAddr(jobIn.getString("companyAddress"));
            bi.setCompany_name(jobIn.getString("companyName"));
            bi.setContact(jobIn.getString("phoneNum"));
            bi.setEmail(jobIn.getString("email"));
            bi.setIntroduction(jobIn.getString("description"));
            bi.setQq(jobIn.getString("qq"));
            bi.setTeacher_version_no(jobIn.getString("currentVersion_teacher"));
            bi.setParent_version_no(jobIn.getString("currentVersion_parent"));
//            if(type==1){ //添加(数据库默认有一条记录，不添加)
//
//            }else if(type==2){ //更新
//                bi.setId(id);
//                basicinfodao.updateBasicInfo(bi);
//            }
            bi.setId(id);
            if(basicinfodao.updateBasicInfo(bi)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 查询关于我们
     *
     * @param aboutUsInfo
     * @param headers
     * @return
     */
    @Path("/queryAboutUs")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryAboutUs(@RequestBody String aboutUsInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(aboutUsInfo, "roleType", "roleId");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(aboutUsInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            BasicInfo bi = new BasicInfo();
            bi = basicinfodao.queryBasicInfo();
            jobOut.put("id", bi.getId());
            jobOut.put("currentVersion_teacher", bi.getTeacher_version_no());
            jobOut.put("currentVersion_parent", bi.getParent_version_no());
            jobOut.put("companyName", bi.getCompany_name());
            jobOut.put("companyAddress", bi.getAddr());
            jobOut.put("phoneNum", bi.getContact());
            jobOut.put("email", bi.getEmail());
            jobOut.put("qq", bi.getQq());
            jobOut.put("description", bi.getIntroduction());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 发布或编辑班级通知
     */

    @Path("/publishOrUpdateClassNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String publishOrUpdateClassNotification(@RequestBody String classNotificationInfo, @Context HttpHeaders
            headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classNotificationInfo, "roleType", "roleId", "id", "type",
                    "description", "classId", "title","imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int type = jobIn.getInt("type");
            int id = jobIn.getInt("id");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限发布/更新班级通知");
                return jobOut.toString();
            }
            ClassNotification cn = new ClassNotification();
            cn.setDescription(jobIn.getString("description"));
            cn.setDate(new Date());
            cn.setClass_id(jobIn.getInt("classId"));
            String imageUrls = jobIn.getString("imageUrls");
            cn.setImage_urls(imageUrls);
            cn.setSubscribers("");
            cn.setTeacher_id(roleId);
            cn.setTitle(jobIn.getString("title"));
            boolean flag = false;
            if (type == 1) { //发布
                flag = classnotificationdao.addClassNotification(cn);
            } else if (type == 2) { //更新
                cn.setId(id);
                flag =  classnotificationdao.updateClassNotification(cn);
            }
            if(flag){
                jobOut.put("id", cn.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 更新班级通知_已读家长列表
     */

    @Path("/updateReadStudentInClassNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateReadStudentInClassNotification(@RequestBody String classNotificationInfo, @Context
            HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classNotificationInfo, "studentId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classNotificationInfo);
            int id = jobIn.getInt("id");
            int studentId = jobIn.getInt("studentId");
            ClassNotification cn = classnotificationdao.queryClassNotification(id);
            if (cn.getSubscribers().equals("")) {
                cn.setSubscribers("" + studentId);
            } else {
                cn.setSubscribers(cn.getSubscribers() + ";" + studentId);
            }

            if(classnotificationdao.updateClassNotification(cn)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }


        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询班级通知接口
     */

    @Path("/queryClassNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryClassNotification(@RequestBody String classNotificationInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classNotificationInfo, "roleType", "roleId", "classId",
                    "pageNum","pageSize");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            pager = classnotificationdao.queryClassNotificationPageByClass(classId, pager);
            List<ClassNotification> result = pager.getList();
            if (result == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (ClassNotification item : result) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("title", item.getTitle());
                jo.put("description", item.getDescription());
                jo.put("imageUrls", item.getImage_urls());
                jo.put("teacherId", item.getTeacher_id());
                jo.put("teacherName", teacherdao.queryTeacher(item.getTeacher_id()).getName());
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                List<Long> allStudents = studentdao.queryStudentByClass(classId); //得到全班所有同学的id，然后去除已读的id
                if (!item.getSubscribers().equals("")) {
                    String[] readIds = item.getSubscribers().split(";");
                    for (int i = 0; i < readIds.length; i++) {
                        allStudents.remove(Long.parseLong(readIds[i]));
                    }
                }
                String unReadIds = "";
                if (allStudents != null && allStudents.size() > 0) {
                    for (Long idItem : allStudents) {
                        unReadIds += idItem + ";";
                    }
                    unReadIds = unReadIds.substring(0, unReadIds.length() - 1);
                }
                jo.put("unReadIds", unReadIds);
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("pageCount", pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询未读学生的信息
     *
     * @param unReadInfo
     * @param headers
     * @return
     */
    @Path("/queryUnreadStudent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryUnreadStudent(@RequestBody String unReadInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(unReadInfo, "roleType", "roleId", "id", "unReadIds");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(unReadInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int id = jobIn.getInt("id");
            String unReadIds = jobIn.getString("unReadIds");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无权查看");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            if (!unReadIds.equals("")) {
                String[] ids = unReadIds.split(";");
                for (int i = 0; i < ids.length; i++) {
                    JSONObject jo = new JSONObject();
                    jo.put("className", classdao.queryClass(classnotificationdao.queryClassNotification(id)
                            .getClass_id()).getName());
                    jo.put("babyName", studentdao.queryStudent(Integer.parseInt(ids[i])).getName());
                    /**
                     * 还需要家长的各种信息
                     */
                    Parents parent = parentsdao.queryParentsByStudentId(Integer.parseInt(ids[i]));
                    if (parent != null) {
                        jo.put("parentName", parent.getName());
                        jo.put("phoneNum", parent.getPhone_num());
                        jo.put("avatar", parent.getAvatar_path());
                    } else {
                        jo.put("parentName", "");
                        jo.put("phoneNum", "");
                        jo.put("avatar", "");
                    }

                    ja.add(jo);
                }
            }
            jobOut.put("data", ja.toString());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 上传考勤记录
     */
    @Path("/addCheckinRecord")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addCheckinRecord(@RequestBody String checkinInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(checkinInfo, "roleType", "roleId", "cardId", "classId",
                    "period", "state", "temperature","imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(checkinInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int cardId = jobIn.getInt("cardId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限添加考勤记录");
                return jobOut.toString();
            }
            CheckinRecords cir = new CheckinRecords();
            cir.setClass_id(jobIn.getInt("classId"));
            cir.setDate(new Date());
            String imageUrls = jobIn.getString("imageUrls");
            cir.setImage_path(imageUrls);
            cir.setPeriod(jobIn.getInt("period"));
            cir.setState(jobIn.getString("state"));
            cir.setStu_id(checkincarddao.queryCheckinCard(cardId).getStu_id());
            cir.setTemperature((float) jobIn.getDouble("temperature"));
            if(checkinrecorddao.addCheckinRecords(cir)){
                jobOut.put("id", cir.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看考勤记录(按天查)
     *
     * @param checkinInfo
     * @param headers
     * @return
     */
    @Path("/queryCheckinRecord")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryCheckinRecord(@RequestBody String checkinInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(checkinInfo, "roleType", "roleId", "classId", "babyId",
                    "year", "month", "day");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(checkinInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int babyId = jobIn.getInt("babyId");
            int year = jobIn.getInt("year");
            int month = jobIn.getInt("month");
            int day = jobIn.getInt("day");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            List<CheckinRecords> cirs = new ArrayList<CheckinRecords>();
            if (roleType == 1 || roleType == 2) {  //老师或园长按照班级查询
                cirs = checkinrecorddao.queryCheckinRecordsByClassAndTime(classId, year, month, day);
            } else if (roleType == 3) {  //家长按照baby查询
                cirs = checkinrecorddao.queryCheckinRecordsByBabyAndTime(babyId, year, month, day);
            }
            if (cirs == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray result = new JSONArray();
            if (roleType == 3) {  //家长只有一个学生最多一天4条记录
                JSONArray ja = new JSONArray();
                for (CheckinRecords item : cirs) {
                    JSONObject jo = new JSONObject();
                    jo.put("studentId", item.getStu_id());
                    jo.put("studentName", studentdao.queryStudent(item.getStu_id()).getName());
                    jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")
                            [0] : item.getDate().toString());
                    jo.put("temperature", item.getTemperature());
                    jo.put("images", item.getImage_path());
                    jo.put("period", item.getPeriod());
                    jo.put("state", item.getState());
                    ja.add(jo);
                }
                result.add(ja.toString());
            } else if (roleType == 1 || roleType == 2) {  //老师或者园长可以查看一个班所有学生的一天的记录
                long tmpStudentId = -1;
                JSONArray ja = new JSONArray();
                for (CheckinRecords item : cirs) {
                    JSONObject jo = new JSONObject();
                    jo.put("studentId", item.getStu_id());
                    jo.put("studentName", studentdao.queryStudent(item.getStu_id()).getName());
                    jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")
                            [0] : item.getDate().toString());
                    jo.put("temperature", item.getTemperature());
                    jo.put("images", item.getImage_path());
                    jo.put("period", item.getPeriod());
                    jo.put("state", item.getState());
                    if (tmpStudentId == -1) {
                        tmpStudentId = item.getStu_id();
                    }
                    if (item.getStu_id() == tmpStudentId) {
                        tmpStudentId = item.getStu_id();
                        ja.add(jo);
                    } else {
                        result.add(ja);
                        ja.clear();
                        ja.add(jo);
                        tmpStudentId = item.getStu_id();
                    }
                }
            }
            jobOut.put("data", result.toString());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 更新宝贝饮食
     *
     * @param foodInfo
     * @param headers
     * @return
     */
    @Path("/addBabyFood")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addBabyFood(@RequestBody String foodInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {

            String checkInput = judgeValidationOfInputJson(foodInfo,"roleType","roleId","classId","content","imageUrls");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(foodInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            String content = jobIn.getString("content");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限添加饮食");
                return jobOut.toString();
            }
            FoodRecord fr = new FoodRecord();
            fr.setDate(new Date());
            fr.setRecords(content);
            fr.setClass_id(classId);
            fr.setSchool_id(0);
            String imageUrls = jobIn.getString("imageUrls");
            fr.setImage_urls(imageUrls);
            if(foodrecorddao.addFoodRecord(fr)){
                jobOut.put("id", fr.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看宝贝饮食
     */

    @Path("/queryBabyFood")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBabyFood(@RequestBody String foodInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(foodInfo, "roleType", "roleId", "classId", "year",
                    "month", "day");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(foodInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int year = jobIn.getInt("year");
            int month = jobIn.getInt("month");
            int day = jobIn.getInt("day");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2 && roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看饮食");
                return jobOut.toString();
            }

            FoodRecord fr = foodrecorddao.queryFoodRecordByClassId(classId,year,month,day);
            if (fr.getRecords() != null && !fr.getRecords().isEmpty()) {
                jobOut.put("imageUrls",fr.getImage_urls());
                jobOut.put("content",fr.getRecords());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 编辑课程表
     *
     * @param courseScheduleInfo
     * @param headers
     * @return
     */
    @Path("/editCourseSchedule")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String editCourseSchedule(@RequestBody String courseScheduleInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(courseScheduleInfo, "roleType", "roleId", "classId",
                    "courseScheduleContent");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(courseScheduleInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            String content = jobIn.getString("courseScheduleContent");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限更改课程表");
                return jobOut.toString();
            }
            Class cls = classdao.queryClass(classId);
            cls.setCourse_schedule(content);
            if(classdao.updateClass(cls)){
                jobOut.put("id", cls.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看课程表
     */

    @Path("/queryCourseSchedule")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryCourseSchedule(@RequestBody String courseScheduleInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(courseScheduleInfo, "roleType", "roleId", "classId");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(courseScheduleInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2 && roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看饮食");
                return jobOut.toString();
            }
            Class cls = classdao.queryClass(classId);
            jobOut.put("data", cls.getCourse_schedule());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 发布作业
     *
     * @param homeworkInfo
     * @param headers
     * @return
     */
    @Path("/addHomeWork")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addHomeWork(@RequestBody String homeworkInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(homeworkInfo, "roleType", "roleId", "classId", "title",
                    "description","imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(homeworkInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            String title = jobIn.getString("title");
            String description = jobIn.getString("description");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限发布作业");
                return jobOut.toString();
            }
            HomeWork hw = new HomeWork();
            String imageUrls = jobIn.getString("imageUrls");
            hw.setImage_urls(imageUrls);
            hw.setDescription(description);
            hw.setClass_id(classId);
            hw.setDate(new Date());
            hw.setTeacher_id(roleId);
            hw.setTitle(title);
            if(homeworkdao.addHomeWork(hw)){
                jobOut.put("id", hw.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看作业
     */

    @Path("/queryHomeWork")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryHomeWork(@RequestBody String homeworkInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(homeworkInfo, "roleType", "roleId", "classId", "pageNum","pageSize");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(homeworkInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2 && roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看作业");
                return jobOut.toString();
            }
            pager = homeworkdao.queryHomeWorkPageByClass(classId, pager);
            List<HomeWork> homeWorks = pager.getList();
            if (homeWorks == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (HomeWork item : homeWorks) {
                JSONObject jo = new JSONObject();
                jo.put("publishName", teacherdao.queryTeacher(item.getTeacher_id()).getName());
                jo.put("title", item.getTitle());
                jo.put("description", item.getDescription());
                jo.put("imageUrls", item.getImage_urls());
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("pageCount", pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 发布班级活动
     *
     * @param classActivityInfo
     * @param headers
     * @return
     */
    @Path("/publishClassActivity")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String publishClassActivity(@RequestBody String classActivityInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classActivityInfo, "roleType", "roleId", "classId",
                    "title", "content", "startDate", "endDate", "participate_num", "contactName", "contactPhoneNum","imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classActivityInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            String title = jobIn.getString("title");
            String content = jobIn.getString("content");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //按这种格式来转化数据
            Date startDate = sdf.parse(jobIn.getString("startDate"));
            Date endDate = sdf.parse(jobIn.getString("endDate"));
            int participate_num = jobIn.getInt("participate_num");  //名额总数
            String contactName = jobIn.getString("contactName");
            String contactPhoneNum = jobIn.getString("contactPhoneNum");

            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限发布班级活动");
                return jobOut.toString();
            }
            ClassActivity ca = new ClassActivity();
            String imageUrls = jobIn.getString("imageUrls");
            ca.setImage_urls(imageUrls);
            ca.setTitle(title);
            ca.setTeacher_id(roleId);
            ca.setClass_id(classId);
            ca.setBaby_ids("");  //参与宝贝的ids
            ca.setContact_name(contactName);
            ca.setContent(content);
            ca.setContact_phone(contactPhoneNum);
            ca.setStart_date(startDate);
            ca.setEnd_date(endDate);
            ca.setParticipate_num(participate_num);
            ca.setParent_ids("");  //参与家长的ids
            ca.setParticipate_time(""); //参与时间s
            if(classactivitydao.addClassActivity(ca)){
                jobOut.put("id", ca.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看班级活动
     */

    @Path("/queryClassActivity")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryClassActivity(@RequestBody String classActivityInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classActivityInfo, "roleType", "roleId", "classId",
                    "pageNum","pageSize");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classActivityInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2 && roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看班级活动");
                return jobOut.toString();
            }
            pager = classactivitydao.queryClassActivityPageByClass(classId, pager);
            List<ClassActivity> classActivities = pager.getList();
            if (classActivities == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (ClassActivity item : classActivities) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("title", item.getTitle());
                jo.put("content", item.getContent());
                jo.put("imageUrls", item.getImage_urls());
                jo.put("startDate", item.getStart_date().toString().contains(".") ? item.getStart_date().toString()
                        .split("\\.")[0] : item.getStart_date().toString());
                jo.put("endDate", item.getEnd_date().toString().contains(".") ? item.getEnd_date().toString().split
                        ("\\.")[0] : item.getEnd_date().toString());
                jo.put("participate_num", item.getParticipate_num());
                jo.put("contactName", item.getContact_name());
                jo.put("contactPhoneNum", item.getContact_phone());
                jo.put("participateBabyNames", item.getBaby_ids());
                jo.put("participateBabyParentsNames", item.getParent_ids());
                jo.put("participateTimes", item.getParticipate_time());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("pageCount", pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 参加班级活动
     */

    @Path("/participateClassActivity")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String participateClassActivity(@RequestBody String classActivityInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classActivityInfo, "roleType", "roleId", "babyId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classActivityInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int babyId = jobIn.getInt("babyId");
            int id = jobIn.getInt("id");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限参加班级活动");
                return jobOut.toString();
            }
            ClassActivity ca = classactivitydao.queryClassActivity(id);
            if (new Date().getTime() < ca.getEnd_date().getTime() && new Date().getTime() > ca.getStart_date()
                    .getTime()) { //活动期间内可以报名
                if (ca.getBaby_ids().split(";").length > ca.getParticipate_num()) {
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "人数已满，不能报名");
                    return jobOut.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //按这种格式来转化数据
                    java.util.Date date = new java.util.Date();
                    String dateString = sdf.format(date);
                    String babyNames = ca.getBaby_ids().equals("") ? studentdao.queryStudent(babyId).getName() : (ca
                            .getBaby_ids() + ";" + studentdao.queryStudent(babyId).getName());
                    String parentName = ca.getParent_ids().equals("") ? parentsdao.queryParents(roleId).getName() :
                            (ca.getParent_ids() + ";" + parentsdao.queryParents(roleId).getName());
                    String participateDates = ca.getParticipate_time().equals("") ? dateString : (ca
                            .getParticipate_time() + ";" + dateString);
                    ca.setParent_ids(parentName);
                    ca.setBaby_ids(babyNames);
                    ca.setParticipate_time(participateDates);
                    if(classactivitydao.updateClassActivity(ca)){
                        jobOut.put("resultCode", GlobalStatus.succeed.toString());
                        jobOut.put("resultDesc", "操作成功");
                    }else{
                        jobOut.put("resultCode", GlobalStatus.succeed.toString());
                        jobOut.put("resultDesc", "操作失败");
                    }
                }

            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "活动已截止");
                return jobOut.toString();
            }
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 新增/更新育儿知识
     *
     * @param babyKnowledgeInfo
     * @param headers
     * @return
     */
    @Path("/addOrUpdateBabyKnowledge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrUpdateBabyKnowledge(@RequestBody String babyKnowledgeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(babyKnowledgeInfo, "roleType", "roleId", "type", "id",
                    "question", "answer", "linkUrl");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(babyKnowledgeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int type = jobIn.getInt("type");
            int id = jobIn.getInt("id");
            String question = jobIn.getString("question");
            String answer = jobIn.getString("answer");
            String linkUrl = jobIn.getString("linkUrl");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 5) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限发布/更新育儿知识");
                return jobOut.toString();
            }
            BabyKnowledge bk = new BabyKnowledge();
            bk.setQuestion(question);
            bk.setAnswer(answer);
            bk.setUrl(linkUrl);
            boolean flag = false;
            if (type == 1) {
                flag = babyknowledgedao.addBabyKnowledge(bk);
            } else if (type == 2) {
                bk.setId(id);
                flag = babyknowledgedao.updateBabyKnowledge(bk);
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "传入type有误");
                return jobOut.toString();
            }
            if(flag){
                jobOut.put("id", bk.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看育儿知识
     */

    @Path("/queryBabyKnowledge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBabyKnowledge(@RequestBody String babyKnowledgeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(babyKnowledgeInfo, "roleType", "roleId");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(babyKnowledgeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");

            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2 && roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看育儿知识");
                return jobOut.toString();
            }
            List<BabyKnowledge> babyKnowledges = babyknowledgedao.getBabyKnowledgeList();
            if (babyKnowledges == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (BabyKnowledge item : babyKnowledges) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("question", item.getQuestion());
                jo.put("answer", item.getAnswer());
                jo.put("linkUrl", item.getUrl());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 查看通讯录
     */

    @Path("/queryContacts")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryContacts(@RequestBody String contactInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(contactInfo, "roleType", "roleId", "classId", "schoolId",
                    "contactType");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(contactInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int schoolId = jobIn.getInt("schoolId");
            int contactType = jobIn.getInt("contactType");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看通讯录");
                return jobOut.toString();
            }
            Class cls = classdao.queryClass(classId);
            Kindergarten kg = kindergartendao.queryKindergarten(schoolId);
            JSONArray ja = new JSONArray();
            if (contactType == 1) { //查看老师的,直接查询school的通讯录字段
                String teacherContacts = kg.getTeacher_contacts();
                if (teacherContacts.equals("")) {
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "无记录");
                    return jobOut.toString();
                }
                String[] teachersInSchool = teacherContacts.split(";");
                for (int i = 0; i < teachersInSchool.length; i++) {
                    JSONObject jo = new JSONObject();
                    String items[] = teachersInSchool[i].split("-");
                    if (items.length != 4) {
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "教师通讯录存储格式有误，请联系管理员");
                        return jobOut.toString();
                    }
                    jo.put("name", items[0]);
                    jo.put("phoneNum", items[1]);
                    jo.put("avatar", items[2]);
                    jo.put("className", items[3]);
                    ja.add(jo);
                }
            } else if (contactType == 2) {//查看家长的，根据class的parent_contact查
                String parentContacts = cls.getParents_contacts();
                if (parentContacts.equals("")) {
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "无记录");
                    return jobOut.toString();
                }
                String[] parentsInSchool = parentContacts.split(";");
                for (int i = 0; i < parentsInSchool.length; i++) {
                    JSONObject jo = new JSONObject();
                    String items[] = parentsInSchool[i].split("-");
                    if (items.length != 6) {
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "家长通讯录存储格式有误，请联系管理员");
                        return jobOut.toString();
                    }
                    jo.put("babyName", items[0]);
                    jo.put("relation", items[1]);
                    jo.put("name", items[2]);
                    jo.put("phoneNum", items[3]);
                    jo.put("avatar", items[4]);
                    jo.put("className", items[5]);
                    ja.add(jo);
                }
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "传入type有误");
                return jobOut.toString();
            }
            jobOut.put("data", ja.toString());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 新增日志
     *
     * @param logInfo
     * @param headers
     * @return
     */
    @Path("/addLog")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addLog(@RequestBody String logInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(logInfo, "roleType", "roleId", "userType", "userId",
                    "opType", "opContent", "opObject");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(logInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int userType = jobIn.getInt("userType");
            int userId = jobIn.getInt("userId");
            String opType = jobIn.getString("opType");
            String opContent = jobIn.getString("opContent");
            String opObject = jobIn.getString("opObject");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }

            DailyLog dl = new DailyLog();
            dl.setLog_date(new Date());
            dl.setOp_content(opContent);
            dl.setOp_object(opObject);
            dl.setOp_type(opType);
            dl.setUser_id(userId);
            dl.setUser_type(userType);
            if(dailylogdao.addDailyLog(dl)){
                jobOut.put("id", dl.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看日志
     */

    @Path("/queryLog")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryLog(@RequestBody String logInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(logInfo, "roleType", "roleId", "pageNum","pageSize", "year", "month");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(logInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int pageNum = jobIn.getInt("pageNum");
            int year = jobIn.getInt("year");
            int month = jobIn.getInt("month");
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);

            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 5) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看日志");
                return jobOut.toString();
            }
            pager = dailylogdao.queryDailyLogPage(year, month, pager);
            List<DailyLog> dailyLogs = pager.getList();

            if (dailyLogs == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (DailyLog item : dailyLogs) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("date", item.getLog_date().toString().contains(".") ? item.getLog_date().toString().split("\\" +
                        ".")[0] : item.getLog_date().toString());
                jo.put("userType", item.getUser_type());
                jo.put("userName", getNameFromRoleTypeAndRoleId(item.getUser_type(), item.getUser_id()));
                jo.put("opType", item.getOp_type());
                jo.put("opContent", item.getOp_content());
                jo.put("opObject", item.getOp_object());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("pageCount",pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount()>pageNum?true:false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 新增留言
     *
     * @param messageInfo
     * @param headers
     * @return
     */
    @Path("/addMessage")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addMessage(@RequestBody String messageInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
//            private String content;
//            private String image_url;
//            private int content_type; // 图片还是文字  1-文字，2-图片
//            private long teacher_id;
//            private long parents_id;
//            private int send_direction; // 1-家长发给老师的，2-老师发给家长的
            String checkInput = judgeValidationOfInputJson(messageInfo,"roleType","roleId","content","content_type","otherPhoneNum","imageUrls");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(messageInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int content_type = jobIn.getInt("content_type");
            String otherPhoneNum = jobIn.getString("otherPhoneNum");
            String content = jobIn.getString("content");

            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            Message msg = new Message();
            msg.setContent(content);
            msg.setContent_type(content_type);
            msg.setDate(new Date());
            String imageUrls = jobIn.getString("imageUrls");
            msg.setImage_url(imageUrls);
            msg.setRead_or_not(false);
            if (roleType == 1 || roleType == 2) {
                msg.setTeacher_id(roleId);
                msg.setParents_id(parentsdao.queryParents(otherPhoneNum).getId());
                msg.setSend_direction(2);
            } else if (roleType == 3) {
                msg.setTeacher_id(teacherdao.queryTeacher(otherPhoneNum).getId());
                msg.setParents_id(roleId);
                msg.setSend_direction(1);
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "roleType不合法");
                return jobOut.toString();
            }
            if(messagedao.addMessage(msg)){
                jobOut.put("id", msg.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查看留言
     */

    @Path("/queryMessage")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryMessage(@RequestBody String messageInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(messageInfo, "roleType", "roleId");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(messageInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");

            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            String otherPhoneNum = "";
            List<Message> msgs = new ArrayList<Message>();
            if (roleType == 1 || roleType == 2) {
                msgs = messagedao.queryUnreadMessagesByTeacherId(roleId);
            } else if (roleType == 3) {
                msgs = messagedao.queryUnreadMessagesByParentsId(roleId);
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "roleType传入有误");
                return jobOut.toString();
            }

            if (msgs == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (Message item : msgs) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("content", item.getContent());
                jo.put("content_type", item.getContent_type());
                jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")[0] :
                        item.getDate().toString());
                jo.put("imageUrls", item.getImage_url());
                if (item.getSend_direction() == 1) {
                    otherPhoneNum = parentsdao.queryParents(item.getParents_id()).getPhone_num();
                } else if (item.getSend_direction() == 2) {
                    otherPhoneNum = teacherdao.queryTeacher(item.getTeacher_id()).getPhone_num();
                }
                jo.put("otherPhoneNum", otherPhoneNum);
                jo.put("readOrNot", item.isRead_or_not());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 更新消息读取状态
     */
    @Path("/updateStateOfMessage")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateStateOfMessage(@RequestBody String messageInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(messageInfo, "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(messageInfo);
            int id = jobIn.getInt("id");
            Message msg = messagedao.queryMessage(id);
            msg.setRead_or_not(true);
            if(messagedao.updateMessage(msg)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }

        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 根据id或phoneNum获取头像
     *
     * @param avatarInfo
     * @param headers
     * @return
     */
    @Path("/queryAvatarByIdOrPhone")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryAvatarByIdOrPhone(@RequestBody String avatarInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(avatarInfo, "roleType", "roleId", "phoneNum", "type");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(avatarInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            String phoneNum = jobIn.getString("phoneNum");
            int type = jobIn.getInt("type");

            switch (roleType) {
                case 1:
                case 2:
                    if (type == 1) {  //按id查
                        jobOut.put("avatar", teacherdao.queryTeacher(roleId).getAvatar_path());
                    } else if (type == 2) { //按phoneNum查
                        jobOut.put("avatar", teacherdao.queryTeacher(phoneNum).getAvatar_path());
                    }
                    break;
                case 3:
                    if (type == 1) {  //按id查
                        jobOut.put("avatar", parentsdao.queryParents(roleId).getAvatar_path());
                    } else if (type == 2) { //按phoneNum查
                        jobOut.put("avatar", parentsdao.queryParents(phoneNum).getAvatar_path());
                    }
                    break;
                case 4:
                    if (type == 1) {  //按id查
                        jobOut.put("avatar", agentdao.queryAgent(roleId).getAvarta());
                    } else if (type == 2) { //按phoneNum查
                        jobOut.put("avatar", agentdao.queryAgent(phoneNum).getAvarta());
                    }
                    break;
            }
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 上传班级相册
     *
     * @param classAlumInfo
     * @param headers
     * @return
     */
    @Path("/addClassAlum")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addClassAlum(@RequestBody String classAlumInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classAlumInfo,"roleType","roleId","classId","imageUrls");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classAlumInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int classId = jobIn.getInt("classId");
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限添加相册");
                return jobOut.toString();
            }
            Calendar cal=Calendar.getInstance();//使用日历类
            int year=cal.get(Calendar.YEAR);//得到年
            int month=cal.get(Calendar.MONTH)+1;//得到月，因为从0开始的，所以要加1
            int day=cal.get(Calendar.DAY_OF_MONTH);//得到天
            ClassAlbum ca = classalbumdao.queryClassAlbumByDate(year,month,day);
            String imageUrls = jobIn.getString("imageUrls");
            boolean flag = false;
            if(ca==null){ //没有当天的相册记录则直接添加
                ca = new ClassAlbum();
                ca.setDate(new Date());
                ca.setClass_id(classId);
                ca.setImage_names(imageUrls);
                flag = classalbumdao.addClassAlbum(ca);
            }else{  //有的话将图片名添加到后面
                if(!ca.getImage_names().equals("")){
                    ca.setImage_names(ca.getImage_names()+";"+imageUrls);
                }else{
                    ca.setImage_names(imageUrls);
                }
                flag = classalbumdao.updateClassAlbum(ca);
            }
            if(flag){
                jobOut.put("id",ca.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 查询班级相册
     *
     * @param classAlumInfo
     * @param headers
     * @return
     */
    @Path("/queryClassAlum")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryClassAlum(@RequestBody String classAlumInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classAlumInfo,"roleType","roleId","classId","pageNum","pageSize");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classAlumInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            if (roleType != 1 && roleType != 2 && roleType != 3) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限查看相册");
                return jobOut.toString();
            }
            int pageSize = jobIn.getInt("pageSize");
            Pager pager = new Pager();
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNum);
            pager = classalbumdao.queryClassAlbumListByPage(classId, pager);
            List<ClassAlbum> classAlbums = pager.getList();
            JSONArray ja = new JSONArray();
            if (classAlbums != null && classAlbums.size() != 0) {
                for (ClassAlbum item : classAlbums) {
                    JSONObject jo = new JSONObject();
                    jo.put("id", item.getId());
                    jo.put("date", item.getDate().toString().contains(".") ? item.getDate().toString().split("\\.")
                            [0] : item.getDate().toString());
                    jo.put("className", classdao.queryClass(item.getClass_id()).getName());
                    jo.put("imageUrls", item.getImage_names());
                    ja.add(jo);
                }
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            jobOut.put("data",ja.toString());
            jobOut.put("pageCount",pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount()>pageNum?true:false); //是否有下一页
            jobOut.put("currentPage",pageNum);
            jobOut.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询手机号码是否被注册
     *
     * @param phoneInfo
     * @param headers
     * @return
     */
    @Path("/queryPhoneNumIsExist")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryPhoneNumIsExist(@RequestBody String phoneInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(phoneInfo,"phoneNum");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(phoneInfo);
            String phoneNum = jobIn.getString("phoneNum");
            int exsitRoleType=-1;
            boolean exsitFlag = false;
            if(teacherdao.queryTeacher(phoneNum)!=null){
                if(teacherdao.queryTeacher(phoneNum).getIs_leader()){
                    exsitRoleType = 2;
                }
                else exsitRoleType = 1;
                exsitFlag = true;
            }else if(parentsdao.queryParents(phoneNum)!=null){
                exsitRoleType = 3;
                exsitFlag = true;
            }else if(agentdao.queryAgent(phoneNum)!=null){
                exsitRoleType = 4;
                exsitFlag = true;
            }else if(administratordao.queryAdministrator(phoneNum)!=null){
                exsitRoleType = 5;
                exsitFlag = true;
            }else{
            }
            jobOut.put("exsitFlag",exsitFlag);
            jobOut.put("exsitRoleType",exsitRoleType);
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 更新校园介绍
     *
     * @param schoolIntroductionInfo
     * @param headers
     * @return
     */
    @Path("/updateSchoolIntroduction")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateSchoolIntroduction(@RequestBody String schoolIntroductionInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(schoolIntroductionInfo,"roleType","roleId","schoolId","description","schoolImages","instrumentImages","teacherImages");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(schoolIntroductionInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int schoolId = jobIn.getInt("schoolId");
            String description = jobIn.getString("description");
            String schoolImages = jobIn.getString("schoolImages");
            String instrumentImages = jobIn.getString("instrumentImages");
            String teacherImages = jobIn.getString("teacherImages");
            if(roleType!=2&&roleType!=4){
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限");
                return jobOut.toString();
            }
            Kindergarten kg = kindergartendao.queryKindergarten(schoolId);
            if(kg==null){
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有找到该学校");
                return jobOut.toString();
            }else{
                kg.setGarten_presence_imgs(schoolImages);
                kg.setGarten_instrument_imgs(instrumentImages);
                kg.setDescription(description);
                kg.setTeacher_presence_imgs(teacherImages);
                if(kindergartendao.updateKindergarten(kg)){
                    jobOut.put("resultCode", GlobalStatus.succeed.toString());
                    jobOut.put("resultDesc", "操作成功");
                }else{
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "操作失败");
                }
            }
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }
    /**
     * 查询校园介绍
     *
     * @param schoolIntroductionInfo
     * @param headers
     * @return
     */
    @Path("/querySchoolIntroduction")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String querySchoolIntroduction(@RequestBody String schoolIntroductionInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(schoolIntroductionInfo,"roleType","roleId","schoolId");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(schoolIntroductionInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int schoolId = jobIn.getInt("schoolId");
            Kindergarten kg = kindergartendao.queryKindergarten(schoolId);
            if(kg==null){
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONObject jo = new JSONObject();
            jo.put("name",kg.getName());
            jo.put("description",kg.getDescription());
            jo.put("leaderWishes",kg.getLeader_wishes());
            jo.put("schoolImages",kg.getGarten_presence_imgs());
            jo.put("teacherImages",kg.getTeacher_presence_imgs());
            jo.put("instrumentImages",kg.getGarten_instrument_imgs());
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 更新校园介绍
     *
     * @param systemNotificationInfo
     * @param headers
     * @return
     */
    @Path("/addOrUpdateSystemNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrUpdateSystemNotification(@RequestBody String systemNotificationInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(systemNotificationInfo,"roleType","roleId","type","id","title","content");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(systemNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int type = jobIn.getInt("type");
            int id = jobIn.getInt("id");
            String title = jobIn.getString("title");
            String content = jobIn.getString("content");
            if(roleType!=5){
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限");
                return jobOut.toString();
            }
            SystemNotification sn = new SystemNotification();
            sn.setDate(new Date());
            sn.setContent(content);
            sn.setTitle(title);
            if(systemnotificationdao.addSystemNotification(sn)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "添加成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "添加失败");
            }
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }
    /**
     * 查询系统公告
     *
     * @param systemNotificationInfo
     * @param headers
     * @return
     */
    @Path("/querySystemNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String querySystemNotification(@RequestBody String systemNotificationInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(systemNotificationInfo,"roleType","roleId");
            if(!checkInput.equals("")){
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(systemNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            List<SystemNotification> systemNotifications = systemnotificationdao.querySystemNotifications();
            if(systemNotifications==null){
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
            }
            JSONArray ja = new JSONArray();
            for(SystemNotification item : systemNotifications){
                JSONObject jo = new JSONObject();
                jo.put("id",item.getId());
                jo.put("title",item.getTitle());
                jo.put("content",item.getContent());
                jo.put("date",item.getDate().toString().contains(".")?item.getDate().toString().split("\\.")[0]:item.getDate().toString());
                ja.add(jo);
            }
            jobOut.put("data",ja.toString());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "操作成功");
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 删除宝贝动态
     *
     * @param schoolnewsInfo
     * @param headers
     * @return
     */

    @Path("/deleteShoolNews")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteShoolNews(@RequestBody String schoolnewsInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(schoolnewsInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(schoolnewsInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(gartennewsdao.invalidGartenNews(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 删除饮食记录
     *
     * @param foodRecordInfo
     * @param headers
     * @return
     */

    @Path("/deleteFoodRecord")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteFoodRecord(@RequestBody String foodRecordInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(foodRecordInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(foodRecordInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(foodrecorddao.invalidFoodRecord(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 删除班级通知
     *
     * @param classNotificationInfo
     * @param headers
     * @return
     */

    @Path("/deleteClassNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteClassNotification(@RequestBody String classNotificationInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classNotificationInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(classnotificationdao.invalidClassNotification(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 删除班级作业
     *
     * @param homeWorkInfo
     * @param headers
     * @return
     */

    @Path("/deleteHomeWork")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteHomeWork(@RequestBody String homeWorkInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(homeWorkInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(homeWorkInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(homeworkdao.invalidHomeWork(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 删除班级活动
     *
     * @param classActivityInfo
     * @param headers
     * @return
     */

    @Path("/deleteClassActivity")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteClassActivity(@RequestBody String classActivityInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classActivityInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classActivityInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(classnotificationdao.invalidClassNotification(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 删除摄像头
     *
     * @param cameraInfo
     * @param headers
     * @return
     */

    @Path("/deleteCamera")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteCamera(@RequestBody String cameraInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(cameraInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(cameraInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(cameradao.invalidCamera(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 删除摄像头
     *
     * @param babyKnowledgeInfo
     * @param headers
     * @return
     */

    @Path("/deleteBabyKnowledge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteBabyKnowledge(@RequestBody String babyKnowledgeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(babyKnowledgeInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(babyKnowledgeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(babyknowledgedao.invaliBabyKnowledge(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 删除系统公告
     *
     * @param systemNotificationInfo
     * @param headers
     * @return
     */

    @Path("/deleteSystemNotification")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteSystemNotification(@RequestBody String systemNotificationInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(systemNotificationInfo, "roleType", "roleId", "id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(systemNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("id");
            if(systemnotificationdao.invalidSystemNotification(id)){
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "删除成功");
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "删除失败");
            }
        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }


    /**
     * 删除课程表
     *
     * @param courseSchedualInfo
     * @param headers
     * @return
     */

    @Path("/deleteCourseSchedual")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteCourseSchedual(@RequestBody String courseSchedualInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(courseSchedualInfo, "roleType", "roleId", "class_id");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(courseSchedualInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int id = jobIn.getInt("class_id");
            Class cla = classdao.queryClass(id);
            if(cla!=null){
                cla.setCourse_schedule(" ; ; ; ; ");
                if(classdao.updateClass(cla)){
                    jobOut.put("resultCode", GlobalStatus.succeed.toString());
                    jobOut.put("resultDesc", "删除成功");
                }else{
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "删除失败");
                }
            }else{
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "未找到class");
            }

        } catch (Exception e) {

            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 查询校园新闻
     *
     * @param reqJson
     * @return
     */
    @Path("/querySchoolNews")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String querySchoolNews(@RequestBody String reqJson, @Context HttpHeaders headers) {
        System.out.println("收到查询校园新闻的请求...");
        JSONObject returnJsonObject = new JSONObject();
        long schoolId;
        int roleType;
        long roleId;
        int pageNum;
        int pageSize;
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        String checkReqJson = judgeValidationOfInputJson(reqJson, "roleType","roleId","schoolId", "pageNum", "pageSize");
        if (!checkReqJson.equals("")) {
            return checkReqJson;
        }
        roleType = jsonObject.getInt("roleType");
        roleId = jsonObject.getInt("roleId");
        if (!checkIdAndToken(roleType, headers)) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "token过期");
            return returnJsonObject.toString();
        }
        schoolId = jsonObject.getLong("schoolId");
        pageNum = jsonObject.getInt("pageNum");
        pageSize = jsonObject.getInt("pageSize");
        Pager pager = new Pager();
        pager.setPageSize(pageSize);
        pager.setPageNumber(pageNum);
        pager = gartennewsdao.queryGartenNewsPageBySchool(schoolId, pager);
        List<GartenNews> newsList = pager.getList();
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
                item.put("publishDate", news.getPublishDate().toString().contains(".") ? news.getPublishDate()
                        .toString().split("\\.")[0] : news.getPublishDate().toString());
                item.put("modifyDate", news.getModifyDate().toString().contains(".") ? news.getModifyDate().toString
                        ().split("\\.")[0] : news.getModifyDate().toString());
                data.add(item);
            }
            returnJsonObject.put("data", data);
            returnJsonObject.put("pageCount", pager.getPageCount()); //总共有多少页
            returnJsonObject.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
            returnJsonObject.put("currentPage", pageNum);
            returnJsonObject.put("totalCount", pager.getTotalCount());  //总共有多少条记录
            returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
            returnJsonObject.put("resultDesc", "操作成功");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "操作失败");
        }
        return returnJsonObject.toString();
    }


    /**
     * 上传班级相册
     *
     * @param classAlumInfo
     * @param headers
     * @return
     */
    @Path("/addClassAlum")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addClassAlum(@RequestBody String classAlumInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(classAlumInfo, "roleType", "roleId", "classId", "imageUrls");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classAlumInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            int classId = jobIn.getInt("classId");
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限添加相册");
                return jobOut.toString();
            }
            Calendar cal = Calendar.getInstance();//使用日历类
            int year = cal.get(Calendar.YEAR);//得到年
            int month = cal.get(Calendar.MONTH) + 1;//得到月，因为从0开始的，所以要加1
            int day = cal.get(Calendar.DAY_OF_MONTH);//得到天
            ClassAlbum ca = classalbumdao.queryClassAlbumByDate(year, month, day);
            String imageUrls = jobIn.getString("imageUrls");
            boolean flag = false;
            if (ca == null) { //没有当天的相册记录则直接添加
                ca = new ClassAlbum();
                ca.setDate(new Date());
                ca.setClass_id(classId);
                ca.setImage_names(imageUrls);
                flag = classalbumdao.addClassAlbum(ca);
            } else {  //有的话将图片名添加到后面
                if (!ca.getImage_names().equals("")) {
                    ca.setImage_names(ca.getImage_names() + ";" + imageUrls);
                } else {
                    ca.setImage_names(imageUrls);
                }
                flag = classalbumdao.updateClassAlbum(ca);
            }
            if (flag) {
                jobOut.put("id", ca.getId());
                jobOut.put("resultCode", GlobalStatus.succeed.toString());
                jobOut.put("resultDesc", "操作成功");
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "操作失败");
            }
        } catch (Exception e) {
            jobOut.put("resultCode", GlobalStatus.error.toString());
            e.printStackTrace();
            jobOut.put("resultDesc", "操作失败");
        }
        return jobOut.toString();
    }

    /**
     * 添加或更新摄像头
     *
     * @param reqJson
     * @return
     */
    @Path("/addOrUpdateCamera")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrUpdateCamera(@RequestBody String reqJson,@Context HttpHeaders headers) {
        System.out.println("收到添加或更新摄像头的请求");
        JSONObject returnJsonObject = new JSONObject();
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        String checkReqJson = judgeValidationOfInputJson(reqJson, "type", "roleType", "roleId",
                "ipUrl", "videoUrl", "description", "manufactory", "classId", "gartenId", "cameraType", "state",
                "thumbPath", "activePeriod", "isPublic");
        if (!checkReqJson.equals("")) {
            return checkReqJson;
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
        Date addDate;
        Date modifyDate;
        type = jsonObject.getInt("type");
        roleType = jsonObject.getInt("roleType");
        roleId = jsonObject.getLong("roleId");
        if (!checkIdAndToken(roleType, headers)) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "token过期");
            return returnJsonObject.toString();
        }
        ipUrl = jsonObject.getString("ipUrl");
        videoUrl = jsonObject.getString("videoUrl");
        description = jsonObject.getString("description");
        manufactory = jsonObject.getString("manufactory");
        classId = jsonObject.getLong("classId");
        gartenId = jsonObject.getLong("gartenId");
        cameraType = jsonObject.getString("cameraType");
        state = jsonObject.getString("state");
        thumbPath = jsonObject.getString("thumbPath");
        activePeriod = jsonObject.getString("activePeriod");
        isPublic = jsonObject.getBoolean("isPublic");
        switch (type) {
            case 1://添加
                Class clz = new Class();
                clz.setId(classId);
                Kindergarten kindergarten = new Kindergarten();
                kindergarten.setId(gartenId);
                addDate = new Date();
                modifyDate = addDate;
                Camera camera = new Camera(ipUrl, videoUrl, description, manufactory, clz, kindergarten, cameraType,
                        state, thumbPath, activePeriod, isPublic, addDate, modifyDate);
                if (cameradao.addCamera(camera)) {
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "添加摄像头失败");
                }
                break;
            case 2://更新
                if (jsonObject.containsKey("id")) {
                    id = jsonObject.getLong("id");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "未传入id");
                    return returnJsonObject.toString();
                }
                Class clz1 = new Class();
                clz1.setId(classId);
                Kindergarten kindergarten1 = new Kindergarten();
                kindergarten1.setId(gartenId);
                modifyDate = new Date();
                Camera camera1 = new Camera(id, ipUrl, videoUrl, description, manufactory, clz1, kindergarten1,
                        cameraType, state, thumbPath, activePeriod, isPublic, modifyDate);
                if (cameradao.updateCamera(camera1)) {
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "更新摄像头失败");
                }
                break;
            default:
                returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                returnJsonObject.put("resultDesc", "操作类型错误，type应该为1或2");
                break;
        }
        return returnJsonObject.toString();
    }

    /**
     * 文件上传
     * 单个文件上传
     *
     * @param ins
     * @param reqJson
     * @return
     */
    @Path("/fileUpload")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String fileUpload(@FormDataParam("fileData") InputStream ins, @FormDataParam("jsonArgs") String reqJson,
                             @Context HttpHeaders headers) {
        System.out.println("收到文件上传的请求...");
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();
        if (job == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "获取json失败");
            return returnJsonObject.toString();
        }
        String checkReqJson = judgeValidationOfInputJson(reqJson, "fileType", "gardenId", "classId",
                "babyId", "interfaceType", "fileName", "roleType", "roleId");
        if (!checkReqJson.equals("")) {
            return checkReqJson;
        }

        int fileType = -1;
        long roleId = -1;
        int roleType = -1;
        long gardenId = -1;
        long classId = -1;
        long babyId = -1;
        String interfaceType;
        String fileName;
        roleType = job.getInt("roleType");
        roleId = job.getLong("roleId");
        if (!checkIdAndToken(roleType, headers)) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "token已过期");
            return returnJsonObject.toString();
        }
        fileType = job.getInt("fileType");
        gardenId = job.getLong("gardenId");
        classId = job.getLong("classId");
        babyId = job.getLong("babyId");
        interfaceType = job.getString("interfaceType");
        fileName = job.getString("fileName");
        //根据接口类型处理文件上传
        switch (interfaceType) {
            case "publishOrUpdateSchoolNews"://发布编辑校园新闻接口
                filePath = "/garden/" + gardenId + "/news/img";
                break;
            case "publishOrUpdateClassNotification"://发布或更新班级通知
                filePath = "/garden/" + gardenId + "/class/" + classId + "/notification/img";
                break;
            case "addHomeWork"://新增班级作业接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/homework/img";
                break;
            case "publishClassActivity"://发布班级活动接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/activity/img";
                break;
            case "addBabyShowTime"://新增宝贝动态接口
                switch (fileType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                "/showTime/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                "/showTime/video";
                        break;
                }

                break;
            case "addOrEditFootPrint"://新增或编辑宝贝足迹接口
                switch (fileType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                "/footprint/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                "/footprint/video";
                        break;
                }
                break;
            case "addOrEditBabyFood"://新增或编辑宝贝饮食接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/food/img";
                break;
            case "addCheckinRecord"://新增宝贝考勤接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/checkin/img";
                break;
            case "uploadAvatar"://上传头像接口
                filePath = "/avatar";
                break;
            case "addTeacherPresence"://上传教师风采图片
                filePath = "/garden/" + gardenId + "/teacher_presence/img";
                break;
            case "addFacilities"://上传幼儿园设施图片
                filePath = "/garden/" + gardenId + "/facilities/img";
                break;
            case "addGardenPresence"://上传幼儿园设施图片
                filePath = "/garden/" + gardenId + "/garden_presence/img";
                break;
            case "addClassAlum"://上传班级相册
                filePath = "/garden/" + gardenId + "/class/" + classId + "/classAlum/img";
                break;
            default:
                returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                returnJsonObject.put("resultDesc", "未知的接口类型");
                return returnJsonObject.toString();
        }
        try {
            switch (fileType) {
                case 1://图片
                    handlerFileUpload(filePath, fileName, ins, true);
                    break;
                case 2://视频
                    handlerFileUpload(filePath, fileName, ins, false);
                    break;
                default:
                    returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                    returnJsonObject.put("resultDesc", "未知的文件类型");
                    return returnJsonObject.toString();
            }
            returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
            returnJsonObject.put("resultDesc", "操作成功");
        } catch (IOException e) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "图片上传失败");
        }
        return returnJsonObject.toString();
    }

    @Path("/fileDownload/get/{roleId}/{roleType}/{interfaceType}/{fileType}/{gardenId}/{classId}/{babyId}/{isThumb}/{fileName}")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] fileDownloadByGet(@PathParam("interfaceType") String interfaceType, @PathParam("fileType") String
            fileType, @PathParam("gardenId") String gardenId, @PathParam("classId") String classId, @PathParam
                                            ("babyId") String babyId, @PathParam("isThumb") String isThumb, @PathParam
                                            ("fileName") String fileName, @PathParam("roleId") String roleId,
                                    @PathParam("roleType") String roleType, @Context HttpHeaders headers) {
        int role_type = Integer.valueOf(roleType);
        if (!checkIdAndToken(role_type, headers)) {
            LockerLogger.log.info("token过期，不能下载图片");
            return null;
        }
        int fType = Integer.valueOf(fileType);
        boolean isThumbPic = Boolean.valueOf(isThumb);
        byte[] fileOctStream = null;
        switch (interfaceType) {
            case "publishOrUpdateSchoolNews"://发布编辑校园新闻接口
                filePath = "/garden/" + gardenId + "/news/img";
                break;
            case "publishOrUpdateClassNotification"://发布或更新班级通知
                filePath = "/garden/" + gardenId + "/class/" + classId + "/notification/img";
                break;
            case "addHomeWork"://新增班级作业接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/homework/img";
                break;
            case "publishClassActivity"://发布班级活动接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/activity/img";
                break;
            case "addBabyShowTime"://新增宝贝动态接口
                switch (fType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/showTime/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/showTime/video";
                        break;
                    default://未知的文件类型
                        break;
                }
                break;
            case "addOrEditFootPrint"://新增或编辑宝贝足迹接口
                switch (fType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/footprint/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/footprint/video";
                        break;
                    default:
                        break;
                }
                break;
            case "addOrEditBabyFood"://新增或编辑宝贝饮食接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/food/img";
                break;
            case "addCheckinRecord"://新增宝贝考勤接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/checkin/img";
                break;
            case "uploadAvatar"://上传头像接口
                filePath = "/avatar";
                break;
            case "addClassAlum"://上传班级相册接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/classAlum/img";
                break;
            default:
                break;
        }
        try {
            switch (fType) {
                case 1://图片
                    fileOctStream = handlerFileDownload(filePath, isThumbPic, false, fileName);
                    break;
                case 2://视频
                    fileOctStream = handlerFileDownload(filePath, isThumbPic, true, fileName);
                    break;
                default:
                    break;
            }
        } catch (FileNotFoundException e) {
            System.err.println("找不到图片");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOctStream;
    }

    @Path("/fileDownload/post/{roleId}/{roleType}/{interfaceType}/{fileType}/{gardenId}/{classId}/{babyId}/{isThumb}/{fileName}")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] fileDownloadByPost(@PathParam("interfaceType") String interfaceType, @PathParam("fileType") String
            fileType, @PathParam("gardenId") String gardenId, @PathParam("classId") String classId, @PathParam
                                            ("babyId") String babyId, @PathParam("isThumb") String isThumb, @PathParam
                                            ("fileName") String fileName, @PathParam("roleId") String roleId,
                                    @PathParam("roleType") String roleType, @Context HttpHeaders headers) {
        int role_type = Integer.valueOf(roleType);
        if (!checkIdAndToken(role_type, headers)) {
            LockerLogger.log.info("token过期，不能下载图片");
            return null;
        }
        int fType = Integer.valueOf(fileType);
        boolean isThumbPic = Boolean.valueOf(isThumb);
        byte[] fileOctStream = null;
        switch (interfaceType) {
            case "publishOrUpdateSchoolNews"://发布编辑校园新闻接口
                filePath = "/garden/" + gardenId + "/news/img";
                break;
            case "publishOrUpdateClassNotification"://发布或更新班级通知
                filePath = "/garden/" + gardenId + "/class/" + classId + "/notification/img";
                break;
            case "addHomeWork"://新增班级作业接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/homework/img";
                break;
            case "publishClassActivity"://发布班级活动接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/activity/img";
                break;
            case "addBabyShowTime"://新增宝贝动态接口
                switch (fType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/showTime/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/showTime/video";
                        break;
                    default://未知的文件类型
                        break;
                }
                break;
            case "addOrEditFootPrint"://新增或编辑宝贝足迹接口
                switch (fType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/footprint/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/footprint/video";
                        break;
                    default:
                        break;
                }
                break;
            case "addOrEditBabyFood"://新增或编辑宝贝饮食接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/food/img";
                break;
            case "addCheckinRecord"://新增宝贝考勤接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/checkin/img";
                break;
            case "uploadAvatar"://上传头像接口
                filePath = "/avatar";
                break;
            case "addClassAlum"://上传班级相册接口
                filePath = "/garden/" + gardenId + "/class/" + classId + "/classAlum/img";
                break;
            default:
                break;
        }
        try {
            switch (fType) {
                case 1://图片
                    fileOctStream = handlerFileDownload(filePath, isThumbPic, false, fileName);
                    break;
                case 2://视频
                    fileOctStream = handlerFileDownload(filePath, isThumbPic, true, fileName);
                    break;
                default:
                    break;
            }
        } catch (FileNotFoundException e) {
            LockerLogger.log.info("找不到图片");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOctStream;
    }
    /**
     * 将流转换为图片，并存储到指定路径
     *
     * @param imgPath 图片存储路径
     * @param ins
     */
    private void storeImg(String imgPath, InputStream ins) throws IOException {
        File file = new File(imgPath);
        File imgDir = new File(file.getParent());
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            try {
                os.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发布或更新校园新闻
     *
     * @param reqJson
     * @return
     */
    @Path("/publishOrUpdateSchoolNews")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String publishOrUpdateSchoolNews(@RequestBody String reqJson,@Context HttpHeaders headers) {
        System.out.println("接收到发布或更新校园新闻的请求");
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();

        if (job == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        String checkReqJson = judgeValidationOfInputJson(reqJson, "type", "roleType","roleId", "title",
                "summary", "description");
        if (!checkReqJson.equals("")) {
            return checkReqJson;
        }
        int roleType = job.getInt("roleType");
        if (!checkIdAndToken(roleType, headers)) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "token过期");
            return returnJsonObject.toString();
        }
        int optType = job.getInt("type");
        long roleId = job.getLong("roleId");
        Teacher teacher = teacherdao.queryTeacher(roleId);
        Kindergarten kindergarten = teacher.getKindergarten();
        String title = job.getString("title");
        String summary = job.getString("summary");
        String description = job.getString("description");
        String imageUrls = job.getString("imageUrls");
//        String teacherName = teacher.getName();
//        String publishDateStr = job.getString("publishDate");
//        String modifyDateStr = job.getString("modifyDate");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date publisDate = null;
        Date modifyDate = null;
//        try {
//            publisDate = simpleDateFormat.parse(publishDateStr);
//            modifyDate = simpleDateFormat.parse(modifyDateStr);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            returnJsoObject.put("resultCode", GlobalStatus.error.toString());
//            returnJsoObject.put("resultDesc", "日期格式有误");
//            return returnJsoObject.toString();
//        }

        GartenNews gartenNews = new GartenNews();
        gartenNews.setTeacher(teacherdao.queryTeacher(roleId));
        gartenNews.setKindergarten(kindergarten);
        gartenNews.setTitle(title);
        gartenNews.setSummary(summary);
        gartenNews.setDescription(description);


        switch (optType) {
            case 1: //发布
                publisDate = new Date();
                gartenNews.setPublishDate(publisDate);
                gartenNews.setModifyDate(publisDate);
                gartenNews.setImage_urls(imageUrls);
                if (gartennewsdao.addGartenNews(gartenNews)) {
                    long newsId = gartenNews.getId();
                    returnJsonObject.put("id", newsId);
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "操作失败");
                }
                break;
            case 2: //更新
                String publishDateStr = job.getString("publishDate");
                try {
                    publisDate = simpleDateFormat.parse(publishDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                modifyDate = new Date();
                gartenNews.setPublishDate(publisDate);
                gartenNews.setModifyDate(modifyDate);
                gartenNews.setImage_urls(imageUrls);
                long newsId = job.getLong("id");
                gartenNews.setId(newsId);
                if (gartennewsdao.updateGartenNews(gartenNews)) {
                    returnJsonObject.put("id", newsId);
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "操作失败");
                }
                break;
            default:
                returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                returnJsonObject.put("resultDesc", "type类型错误，应为1或2");
                break;
        }

        return returnJsonObject.toString();
    }

    /**
     * 显示摄像头列表
     *
     * @return
     */
    @Path("/queryCamera")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String queryCamera(@RequestBody String reqJson,@Context HttpHeaders headers) {
        System.out.println("收到显示摄像头列表的请求。。。");
        JSONObject returnJsonObject = new JSONObject();
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        String checkReqJson = judgeValidationOfInputJson(reqJson, "gardenId", "classId");
        if (!checkReqJson.equals("")) {
            return checkReqJson;
        }
        long gardenId, classId;
        int roleType;
        long roleId;
        roleType = jsonObject.getInt("roleType");
        roleId = jsonObject.getLong("roleId");
        if (!checkIdAndToken(roleType, headers)) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "token过期");
            return returnJsonObject.toString();
        }
        gardenId = jsonObject.getLong("gardenId");
        classId = jsonObject.getLong("classId");

        List<Camera> privateCameraList = cameradao.queryPrivateCamerasList(classId);
        List<Camera> publicCameraList = cameradao.queryPublicCamerasList(gardenId);
        if (privateCameraList == null && publicCameraList == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "显示摄像头列表失败");
        } else {
            int privateCameraCount = 0, publicCameraCount = 0;
            JSONArray data = new JSONArray();
            if (privateCameraList != null) {
                privateCameraCount = privateCameraList.size();
                for (int i = 0; i < privateCameraCount; i++) {
                    Camera camera = privateCameraList.get(i);
                    JSONObject item = new JSONObject();
                    item.put("cameraId", camera.getId());
                    item.put("classId", camera.getCla().getId());
                    item.put("className", camera.getCla().getName());
                    item.put("openTimeArea", camera.getActive_period());
                    item.put("thumbPath", camera.getThumb_path());
                    item.put("state", camera.getState());
                    item.put("isPublic", camera.is_public());
                    data.add(item);
                }

            }
            if (publicCameraList != null) {
                publicCameraCount = publicCameraList.size();
                for (int j = 0; j < publicCameraCount; j++) {
                    Camera camera = publicCameraList.get(j);
                    JSONObject item = new JSONObject();
                    item.put("cameraId", camera.getId());
                    item.put("classId", camera.getCla().getId());
                    item.put("className", camera.getCla().getName());
                    item.put("openTimeArea", camera.getActive_period());
                    item.put("thumbPath", camera.getThumb_path());
                    item.put("state", camera.getState());
                    item.put("isPublic", camera.is_public());
                    data.add(item);
                }
            }
            int totalCount = privateCameraList == null && publicCameraList == null ? 0 : privateCameraCount +
                    publicCameraCount;
            returnJsonObject.put("data", data);
            returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
            returnJsonObject.put("resultDesc", "操作成功");
        }
        return returnJsonObject.toString();
    }

    /**
     * 查询摄像头
     *
     * @param reqJson
     * @return
     */
    @Path("/queryVideo")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String queryVideo(@RequestBody String reqJson,@Context HttpHeaders headers) {
        System.out.println("收到查询摄像头的请求");
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        String checkReqJson = judgeValidationOfInputJson(reqJson, "roleType", "roleId", "cameraId", "classId",
                "isPublic");
        if (!checkReqJson.equals("")) {
            return checkReqJson;
        }
        int roleType;
        long roleId;
        long cameraId;
        long classId;
        boolean isPublic;

        roleType = jsonObject.getInt("roleType");
        roleId = jsonObject.getLong("roleId");
        if (!checkIdAndToken(roleType, headers)) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "token过期");
            return returnJsonObject.toString();
        }
        cameraId = jsonObject.getLong("cameraId");
        classId = jsonObject.getLong("classId");
        isPublic = jsonObject.getBoolean("isPublic");

        Camera camera = cameradao.queryCamera(cameraId);
        if (camera == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "获取摄像头失败");
        } else {
            switch (roleType) {
                case 1://老师角色,只能看自己班级的和公共的
                    if (isPublic) {
                        returnJsonObject.put("videoUrl", camera.getVideo_url());
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        Teacher teacher = teacherdao.queryTeacher(roleId);
                        if (teacher != null) {
                            Set<Class> classes = teacher.getClasses();
                            boolean belongTeacher = false;
                            Iterator iterator = classes.iterator();
                            while (iterator.hasNext()) {
                                Class clazz = (Class) iterator.next();
                                if (classId == clazz.getId()) {
                                    belongTeacher = true;
                                    break;
                                }
                            }
                            if (belongTeacher) {//班级属于老师
                                returnJsonObject.put("videoUrl", camera.getVideo_url());
                                returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                                returnJsonObject.put("resultDesc", "操作成功");
                            } else {
                                returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                                returnJsonObject.put("resultDesc", "只能查看自己的班级");
                            }
                        } else {
                            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                            returnJsonObject.put("resultDesc", "获取老师信息失败");
                        }
                    }
                    break;
                case 3: //家长角色，只有vip才能看（且只能看孩子班级和公共的）
                    Parents parents = parentsdao.queryParents(roleId);
                    if (parents != null) {
                        Student student = parents.getStudent();
                        if (student != null) {
                            boolean isVip = student.isVip();
                            long clazzId = student.getCla().getId();
                            if (isVip) {
                                if (isPublic || classId == clazzId) {
                                    returnJsonObject.put("videoUrl", camera.getVideo_url());
                                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                                    returnJsonObject.put("resultDesc", "操作成功");
                                } else {
                                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                                    returnJsonObject.put("resultDesc", "不能查看其他班和公共以外的摄像头");
                                }
                            } else {
                                returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                                returnJsonObject.put("resultDesc", "该用户不是vip");
                            }
                        } else {
                            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                            returnJsonObject.put("resultDesc", "获取baby信息失败");
                        }
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "获取家长信息失败");
                    }
                    break;
                case 2://其他角色，园长、管理员、运营商和赞助商所有都可以看
                case 4:
                case 5:
                    returnJsonObject.put("videoUrl", camera.getVideo_url());
                    returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsonObject.put("resultDesc", "操作成功");
                    break;
                default://未知角色，直接返回异常
                    returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                    returnJsonObject.put("resultDesc", "未知的角色");
                    break;
            }
        }
        return returnJsonObject.toString();
    }

    /**
     * 处理文件上传
     *
     * @param filePath
     * @param fileName
     * @param ins
     * @param hasThumb
     */
    private void handlerFileUpload(String filePath, String fileName, InputStream ins, Boolean hasThumb) throws
            IOException {
        File dir = new File(baseDir + filePath);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("创建图片目录:" + dir.getPath());
        }
        //存储原图
        if (hasThumb) {
            imgPath = dir.getPath() + "/origin/" + fileName;
        } else {
            imgPath = dir.getPath() + "/" + fileName;
        }
        storeImg(imgPath, ins);
        if (hasThumb) {
            //存储缩略图
            thumbDir = dir.getPath() + "/thumb";
            thumbImgThread = new ThumbGenerateThread(imgPath, thumbDir);
            //线程处理图片缩放和存储
            thumbImgThread.start();
        }
    }

    private byte[] handlerFileDownload(String fileDirPath, boolean isThumb, boolean isVideo, String fileName) throws
            IOException {
        String fileUrl = null;
        if (isVideo) {
            fileUrl = baseDir + fileDirPath + "/" + fileName;
        } else {
            if (isThumb) {
                fileUrl = baseDir + fileDirPath + "/thumb/" + fileName.substring(0, fileName.lastIndexOf(".jpg")) +
                        "_thumb.jpg";
            } else {
                fileUrl = baseDir + fileDirPath + "/origin/" + fileName;
            }
        }
        byte[] fileBytes = FileUtils.fileToByteArrayByTraditionalWay(fileUrl);
//        String fileBase64String = new String(Base64.encodeBase64(fileBytes));
        return fileBytes;
    }

}
