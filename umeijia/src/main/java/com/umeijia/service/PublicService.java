package com.umeijia.service;

import cn.jpush.api.push.model.notification.Notification;
import com.sun.jersey.multipart.FormDataParam;
import com.umeijia.dao.*;
import com.umeijia.util.FileUtils;
import com.umeijia.util.GlobalStatus;
import com.umeijia.util.JpushUtil;
import com.umeijia.util.ThumbGenerateThread;
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
                    "description", "classId", "babyId", "isShareToFootPrint");
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
            bst.setImage_urls("");
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
            jobOut.put("id", bst.getId());
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "添加成功");
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
            String checkInput = judgeValidationOfInputJson(showTimeInfo, "roleType", "roleId", "classId", "pageNum",
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
            Pager pager = new Pager();
            pager.setPageSize(Pager.normalPageSize);
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
                jo.put("date", item.getDate().toString());
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
        return true;
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
            if (bst.getParent_id() != -1) {  //这两个判断的目的是：要本人发布的才能删
                if (roleType == 3 && roleId == bst.getParent_id()) {
                    babyshowtimedao.invalidShowtime(id);
                }
            } else if (bst.getTeacher_id() != -1) {
                if ((roleType == 1 || roleType == 2) && roleId == bst.getTeacher_id()) {
                    babyshowtimedao.invalidShowtime(id);
                }
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "动态不是该用户发布，不能删除");
                return jobOut.toString();
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
            showtimecommentsdao.addShowtimeComments(stc);
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
                    "footPrintType", "description", "babyId", "type", "isShareToBabyShowTime", "classId");
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
            bfp.setImage_urls("");

            if (type == 1) { //新增
                /**
                 * @shanji
                 * 需要在footprintdao里面添加一个每天只能发布一条记录的接口
                 */
                babyfootprintdao.addBabyFootPrint(bfp);
            } else if (type == 2) { //编辑
                bfp.setId(id);
                babyfootprintdao.updateBabyFootPrint(bfp);
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
                babyshowtimedao.addBabyShowtime(bst);
            }
            jobOut.put("id", bfp.getId());
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
            String checkInput = judgeValidationOfInputJson(footPrintInfo, "roleType", "roleId", "babyId", "pageNum");
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
            Pager pager = new Pager();
            pager.setPageSize(Pager.normalPageSize);
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
                jo.put("date", item.getDate().toString());
                jo.put("type", item.getShow_type());
                jo.put("urls", item.getImage_urls());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("pageCount", pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
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
                jo.put("date", item.getDate().toString());
                jo.put("type", item.getShow_type());
                jo.put("urls", item.getImage_urls());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("hasNextPage", true); //是否有下一页
            jobOut.put("totalCount", ja.size());  //总共返回多少条记录
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
            if (bfp.getParent_id() == roleId) {  //判断的目的是：要本人发布的才能删
                babyfootprintdao.invalidFootPrint(id);
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "足迹不是该用户发布，不能删除");
                return jobOut.toString();
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
            feedbackdao.addFeedBack(fb);
            jobOut.put("resultCode", GlobalStatus.succeed.toString());
            jobOut.put("resultDesc", "反馈成功");
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
            String checkInput = judgeValidationOfInputJson(feedbackInfo, "roleType", "roleId", "pageNum");
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
            int pageNum = jobIn.getInt("pageNum");
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
                jo.put("date", item.getDate().toString());
                jo.put("content", item.getContent());
                jo.put("response", item.getResponse());
                jo.put("readOrNot", item.getRead_or_not());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString()); //返回的数据
            jobOut.put("hasNextPage", true); //是否有下一页
            jobOut.put("totalCount", ja.size());  //总共返回多少条记录
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
            basicinfodao.updateBasicInfo(bi);
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
                    "description", "classId", "title");
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
            cn.setImage_urls("");
            cn.setSubscribers("");
            cn.setTeacher_id(roleId);
            cn.setTitle(jobIn.getString("title"));
            if (type == 1) { //发布
                classnotificationdao.addClassNotification(cn);
            } else if (type == 2) { //更新
                cn.setId(id);
                classnotificationdao.updateClassNotification(cn);
            }
            jobOut.put("id", cn.getId());
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

            classnotificationdao.updateClassNotification(cn);

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
                    "pageNum");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classNotificationInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            Pager pager = new Pager();
            pager.setPageSize(Pager.normalPageSize);
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
                jo.put("date", item.getDate().toString());
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
                    "period", "state", "temperature");
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
            cir.setImage_path("");
            cir.setPeriod(jobIn.getInt("period"));
            cir.setState(jobIn.getString("state"));
            cir.setStu_id(checkincarddao.queryCheckinCard(cardId).getStu_id());
            cir.setTemperature((float) jobIn.getDouble("temperature"));
            checkinrecorddao.addCheckinRecords(cir);
            jobOut.put("id", cir.getId());
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
                    jo.put("date", item.getDate().toString());
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
                    jo.put("date", item.getDate().toString());
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
    @Path("/updateBabyFood")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateBabyFood(@RequestBody String foodInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(foodInfo, "roleType", "roleId", "classId", "schoolId",
                    "week", "imageUrls", "name");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(foodInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int schoolId = jobIn.getInt("schoolId");
            int week = jobIn.getInt("week");
            String imageUrls = jobIn.getString("imageUrls");
            String content = jobIn.getString("name");
            if (!checkIdAndToken(roleType, headers)) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "token已过期");
                return jobOut.toString();
            }
            if (roleType != 1 && roleType != 2) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "没有权限更改饮食");
                return jobOut.toString();
            }
            if (week >= 6 || week <= 0) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "传入的week不合法");
                return jobOut.toString();
            }
            FoodRecord fr = foodrecorddao.queryFoodRecordByClassId(classId);
            if (fr.getSchool_id() == schoolId) {
                if (!fr.getRecords().isEmpty()) {  //食物内容的处理
                    String days[] = fr.getRecords().split(";");
                    if (days.length != 5) {
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "数据库记录天数出错（不为5天），请管理人员维护");
                        return jobOut.toString();
                    } else {
                        for (int i = 0; i < days.length; i++) {
                            if (i > week - 1) {  //把后面星期x的内容置为“ ”
                                days[i] = " ";
                            } else if (i == week - 1) {
                                days[i] = content;
                            }
                        }
                        fr.setRecords(days[0] + ";" + days[1] + ";" + days[2] + ";" + days[3] + ";" + days[4]);
                    }
                }
                if (!fr.getImage_urls().isEmpty()) {  //图片的处理
                    String days[] = fr.getImage_urls().split(";");
                    if (days.length != 5) {
                        jobOut.put("resultCode", GlobalStatus.error.toString());
                        jobOut.put("resultDesc", "数据库记录天数出错（不为5天），请管理人员维护");
                        return jobOut.toString();
                    } else {
                        for (int i = 0; i < days.length; i++) {
                            if (i > week - 1) {  //把后面星期x的内容置为“ ”
                                days[i] = " ";
                            } else if (i == week - 1) {
                                days[i] = imageUrls;
                            }
                        }
                        fr.setImage_urls(days[0] + ";" + days[1] + ";" + days[2] + ";" + days[3] + ";" + days[4]);
                    }
                }
                foodrecorddao.updateFoodRecord(fr);
            }
            jobOut.put("id", fr.getId());
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
     * 查看宝贝饮食
     */

    @Path("/queryBabyFood")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBabyFood(@RequestBody String foodInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(foodInfo, "roleType", "roleId", "classId");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(foodInfo);
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
            FoodRecord fr = foodrecorddao.queryFoodRecordByClassId(classId);
            JSONArray ja = new JSONArray();
            if (fr.getRecords() != null && !fr.getRecords().isEmpty()) {
                String foods[] = fr.getRecords().split(";");
                String images[] = fr.getImage_urls().split(";");
                if (foods.length == 5 && images.length == 5) {
                    for (int i = 0; i < foods.length; i++) {
                        JSONObject jo = new JSONObject();
                        jo.put("name", foods[i]);
                        jo.put("imageUrls", images[i]);
                        jo.put("week", (i + 1));
                        ja.add(jo);
                    }
                } else {
                    jobOut.put("resultCode", GlobalStatus.error.toString());
                    jobOut.put("resultDesc", "数据库记录天数出错（不为5天），请管理人员维护");
                    return jobOut.toString();
                }
                jobOut.put("data", ja.toString());
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
            classdao.updateClass(cls);
            jobOut.put("id", cls.getId());
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
                    "description");
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
            hw.setImage_urls("");
            hw.setDescription(description);
            hw.setClass_id(classId);
            hw.setDate(new Date());
            hw.setTeacher_id(roleId);
            hw.setTitle(title);
            homeworkdao.addHomeWork(hw);
            jobOut.put("id", hw.getId());
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
     * 查看作业
     */

    @Path("/queryHomeWork")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryHomeWork(@RequestBody String homeworkInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(homeworkInfo, "roleType", "roleId", "classId", "pageNum");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(homeworkInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            Pager pager = new Pager();
            pager.setPageSize(Pager.normalPageSize);
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
                jo.put("date", item.getDate().toString());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("pageCount", pager.getPageCount()); //总共有多少页
            jobOut.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
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
                    "title", "content", "startDate", "endDate", "participate_num", "contactName", "contactPhoneNum");
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
            ca.setImage_urls("");
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
            classactivitydao.addClassActivity(ca);
            jobOut.put("id", ca.getId());
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
                    "pageNum");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(classActivityInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int classId = jobIn.getInt("classId");
            int pageNum = jobIn.getInt("pageNum");
            Pager pager = new Pager();
            pager.setPageSize(Pager.normalPageSize);
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
                jo.put("startDate", item.getStart_date().toString());
                jo.put("endDate", item.getEnd_date().toString());
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
                    classactivitydao.updateClassActivity(ca);
                    jobOut.put("resultCode", GlobalStatus.succeed.toString());
                    jobOut.put("resultDesc", "操作成功");
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
            if (type == 1) {
                babyknowledgedao.addBabyKnowledge(bk);
            } else if (type == 2) {
                bk.setId(id);
                babyknowledgedao.updateBabyKnowledge(bk);
            } else {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "传入type有误");
                return jobOut.toString();
            }
            jobOut.put("id", bk.getId());
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
     * 查看育儿知识
     */

    @Path("/queryBabyKnowledge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBabyKnowledge(@RequestBody String babyKnowledgeInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(babyKnowledgeInfo, "roleType", "roleId", "pageNum");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(babyKnowledgeInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int pageNum = jobIn.getInt("pageNum");
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
            jobOut.put("hasNextPage", true);
            jobOut.put("totleCount", ja.size());
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


//                List<Teacher> teachersInSchool = teacherdao.queryTeachersByGarten(schoolId);
//                if(teachersInSchool==null){
//                    jobOut.put("resultCode", GlobalStatus.error.toString());
//                    jobOut.put("resultDesc","无记录");
//                     return jobOut.toString();
//                }
//                for(Teacher item : teachersInSchool){
//                    JSONObject jo = new JSONObject();
//                    jo.put("name",item.getName());
//                    jo.put("phoneNum",item.getPhone_num());
//                    jo.put("avatar",item.getAvatar_path());
//                    Set<Class> classes = item.getClasses();
//                    String className = "";
//                    for(Class cla:classes){
//                        className = cla.getName()+";";
//                    }
//                    jo.put("className",className.substring(0,className.length()-1));
//                    ja.add(jo);
//                }
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
            dailylogdao.addDailyLog(dl);
            jobOut.put("id", dl.getId());
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
     * 查看日志
     */

    @Path("/queryLog")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryLog(@RequestBody String logInfo, @Context HttpHeaders headers) {
        JSONObject jobOut = new JSONObject();
        try {
            String checkInput = judgeValidationOfInputJson(logInfo, "roleType", "roleId", "pageNum", "year", "month");
            if (!checkInput.equals("")) {
                return checkInput;
            }
            JSONObject jobIn = JSONObject.fromObject(logInfo);
            int roleType = jobIn.getInt("roleType");
            int roleId = jobIn.getInt("roleId");
            int pageNum = jobIn.getInt("pageNum");
            int year = jobIn.getInt("year");
            int month = jobIn.getInt("month");
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

            List<DailyLog> dailyLogs = dailylogdao.queryDailyLogByMonth(year, month);

            if (dailyLogs == null) {
                jobOut.put("resultCode", GlobalStatus.error.toString());
                jobOut.put("resultDesc", "无记录");
                return jobOut.toString();
            }
            JSONArray ja = new JSONArray();
            for (DailyLog item : dailyLogs) {
                JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("date", item.getLog_date().toString());
                jo.put("userType", item.getUser_type());
                jo.put("userName", getNameFromRoleTypeAndRoleId(item.getUser_type(), item.getUser_id()));
                jo.put("opType", item.getOp_type());
                jo.put("opContent", item.getOp_content());
                jo.put("opObject", item.getOp_object());
                ja.add(jo);
            }
            jobOut.put("data", ja.toString());
            jobOut.put("hasNextPage", true);
            jobOut.put("totleCount", ja.size());
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
     * 查询校园新闻
     *
     * @param reqJson
     * @return
     */
    @Path("/querySchoolNews")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String querySchoolNews(@RequestBody String reqJson) {
        System.out.println("收到查询校园新闻的请求...");
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
        Pager pager = new Pager();
        pager.setPageSize(Pager.normalPageSize);
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
                item.put("publishDate", news.getPublishDate().toString());
                item.put("modifyDate", news.getModifyDate().toString());
                data.add(item);
            }
            returnJsonObject.put("data", data);
            returnJsonObject.put("pageCount", pager.getPageCount()); //总共有多少页
            returnJsonObject.put("hasNextPage", pager.getPageCount() > pageNum ? true : false); //是否有下一页
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
     * 添加或更新摄像头
     *
     * @param reqJson
     * @return
     */
    @Path("/addOrUpdateCamera")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addOrUpdateCamera(@RequestBody String reqJson) {
        System.out.println("收到添加或更新摄像头的请求");
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
        Date addDate;
        Date modifyDate;
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
        if (jsonObject.containsKey("classId")) {
            classId = jsonObject.getLong("classId");
        } else {
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
                    returnJsonObject.put("resultDesc", "找不到参数id");
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
    public String fileUpload(@FormDataParam("fileData") InputStream ins, @FormDataParam("jsonArgs") String reqJson) {
        System.out.println("收到文件上传的请求...");
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();
        if (job == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "获取json失败");
            return returnJsonObject.toString();
        }
        int fileType = -1;
        long recordId = -1;
//        long roleId = -1;
        long gardenId = -1;
        long classId = -1;
        long babyId = -1;
//        int roleType= -1;
        String interfaceType;
        String imgName;

        if (job.containsKey("fileType")) {
            fileType = job.getInt("fileType");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数fileType");
            return returnJsonObject.toString();
        }
        if (job.containsKey("recordId")) {
            recordId = job.getLong("recordId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数recordId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("gardenId")) {
            gardenId = job.getLong("gardenId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数gardenId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("classId")) {
            classId = job.getLong("classId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数classId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("babyId")) {
            babyId = job.getLong("babyId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数babyId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("interfaceType")) {
            interfaceType = job.getString("interfaceType");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数interfaceType");
            return returnJsonObject.toString();
        }
        if (job.containsKey("imgName")) {
            imgName = job.getString("imgName");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数imgName");
            return returnJsonObject.toString();
        }
        //根据接口类型处理文件上传
        switch (interfaceType) {
            case "publishOrUpdateSchoolNews"://发布编辑校园新闻接口
                GartenNews gartenNews = gartennewsdao.queryGartenNews(recordId);
                if (gartenNews != null) {
                    filePath = "/garden/" + gardenId + "/news/img";
                    handlerFileUpload(filePath, imgName, ins, true);
                    imgUrls = gartenNews.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }

                    gartenNews.setImage_urls(imgUrls);
                    //更新表中的imgUrls字段
                    if (gartennewsdao.updateGartenNews(gartenNews)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的新闻记录");
                }
                break;
            case "publishOrUpdateClassNotification"://发布或更新班级通知
                ClassNotification classNotification = classnotificationdao.queryClassNotification(recordId);
                if (classNotification != null) {
                    filePath = "/garden/" + gardenId + "/class/" + classId + "/notification/img";
                    handlerFileUpload(filePath, imgName, ins, true);
                    imgUrls = classNotification.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    classNotification.setImage_urls(imgUrls);
                    //更新数据库表中的imgUrls字段
                    if (classnotificationdao.updateClassNotification(classNotification)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的班级通知");
                }
                break;
            case "addHomeWork"://新增班级作业接口
                HomeWork homeWork = homeworkdao.queryHomeWork(recordId);
                if (homeWork != null) {
                    filePath = "/garden/" + gardenId + "/class/" + classId + "/homework/img";
                    handlerFileUpload(filePath, imgName, ins, true);
                    imgUrls = homeWork.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    homeWork.setImage_urls(imgUrls);
                    if (homeworkdao.updateHomeWork(homeWork)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的班级作业");
                }
                break;
            case "publishClassActivity"://发布班级活动接口
                ClassActivity classActivity = classactivitydao.queryClassActivity(recordId);
                if (classActivity != null) {
                    filePath = "/garden/" + gardenId + "/class/" + classId + "/activity/img";
                    handlerFileUpload(filePath, imgName, ins, true);
                    imgUrls = classActivity.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    classActivity.setImage_urls(imgUrls);
                    if (classactivitydao.updateClassActivity(classActivity)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的班级活动");
                }
                break;
            case "addBabyShowTime"://新增宝贝动态接口
                BabyShowtime babyShowtime = babyshowtimedao.queryBabyShowtime(recordId);
                if (babyShowtime != null) {
                    switch (fileType) {
                        case 1://图片
                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                    "/showTime/img";
                            handlerFileUpload(filePath, imgName, ins, true);
                            break;
                        case 2://视频
                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                    "/showTime/video";
                            handlerFileUpload(filePath, imgName, ins, false);
                            break;
                        default://未知的文件类型
                            returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                            returnJsonObject.put("resultDesc", "未知的文件类型");
                            return returnJsonObject.toString();
                    }

                    imgUrls = babyShowtime.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    babyShowtime.setImage_urls(imgUrls);
                    if (babyshowtimedao.updateBabyShowtime(babyShowtime)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的宝贝动态");
                }
                break;
            case "addOrEditFootPrint"://新增或编辑宝贝足迹接口
                BabyFootPrint babyFootPrint = babyfootprintdao.queryBabyFootPrint(recordId);
                if (babyFootPrint != null) {
                    switch (fileType) {
                        case 1://图片
                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                    "/footprint/img";
                            handlerFileUpload(filePath, imgName, ins, true);
                            break;
                        case 2://视频
                            filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId +
                                    "/footprint/video";
                            handlerFileUpload(filePath, imgName, ins, true);
                            break;
                        default:
                            returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                            returnJsonObject.put("resultDesc", "未知的文件类型");
                            return returnJsonObject.toString();
                    }
                    imgUrls = babyFootPrint.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    babyFootPrint.setImage_urls(imgUrls);
                    if (babyfootprintdao.updateBabyFootPrint(babyFootPrint)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的宝贝足迹");
                }
                break;
            case "addOrEditBabyFood"://新增或编辑宝贝饮食接口
                FoodRecord foodRecord = foodrecorddao.queryFoodRecord(recordId);
                if (foodRecord != null) {
                    filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/food/img";
                    handlerFileUpload(filePath, imgName, ins, true);
                    imgUrls = foodRecord.getImage_urls();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    foodRecord.setImage_urls(imgUrls);
                    if (foodrecorddao.updateFoodRecord(foodRecord)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的宝贝饮食");
                }
                break;
            case "addCheckinRecord"://新增宝贝考勤接口
                CheckinRecords checkinRecords = checkinrecorddao.queryCheckinRecords(recordId);
                if (checkinRecords != null) {
                    filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/checkin/img";
                    handlerFileUpload(filePath, imgName, ins, true);
                    imgUrls = checkinRecords.getImage_path();
                    if (imgUrls != null) {
                        if (imgUrls.length() == 0) {
                            imgUrls = imgName;
                        } else {
                            imgUrls += ";" + imgName;
                        }
                    } else {
                        imgUrls = imgName;
                    }
                    checkinRecords.setImage_path(imgUrls);
                    if (checkinrecorddao.updateCheckinRecords(checkinRecords)) {
                        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                        returnJsonObject.put("resultDesc", "操作成功");
                    } else {
                        returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                        returnJsonObject.put("resultDesc", "更新数据库失败");
                    }
                } else {
                    returnJsonObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsonObject.put("resultDesc", "没有找到对应的考勤记录");
                }
                break;
            case "uploadAvatar"://上传头像接口
                filePath = "/avatar";
                File dir = new File(baseDir + filePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                    System.out.println("创建图片目录:" + dir.getPath());
                }
                //存储原图
                imgPath = dir.getPath() + "/" + imgName;
                storeImg(imgPath, ins);
                returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
                returnJsonObject.put("resultDesc", "操作成功");
                break;
            default:
                returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                returnJsonObject.put("resultDesc", "未知的接口类型");
                break;
        }
        return returnJsonObject.toString();
    }

    @Path("/fileDownload/{interfaceType}/{fileType}/{gardenId}/{classId}/{babyId}/{isThumb}/{fileName}")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] fileDownload(@PathParam("interfaceType") String interfaceType, @PathParam("fileType") String
            fileType, @PathParam("gardenId") String gardenId, @PathParam("classId") String classId, @PathParam
                                      ("babyId") String babyId, @PathParam("isThumb") String isThumb, @PathParam
                                      ("fileName") String fileName) {
        int fType  = Integer.valueOf(fileType);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOctStream;
    }

    /*@Path("/fileDownload")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String downloadFile(@RequestBody String reqJson) {
        JSONObject job = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();
        if (job == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "获取json失败");
            return returnJsonObject.toString();
        }
        int fileType = -1;
        long gardenId = -1;
        long classId = -1;
        long babyId = -1;
        boolean isThumb = false;
        String interfaceType = null;
        String fileName = null;
        String fileBase64String = null;
        if (job.containsKey("fileType")) {
            fileType = job.getInt("fileType");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数fileType");
            return returnJsonObject.toString();
        }
        if (job.containsKey("gardenId")) {
            gardenId = job.getLong("gardenId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数gardenId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("classId")) {
            classId = job.getLong("classId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数classId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("babyId")) {
            babyId = job.getLong("babyId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数babyId");
            return returnJsonObject.toString();
        }
        if (job.containsKey("interfaceType")) {
            interfaceType = job.getString("interfaceType");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数interfaceType");
            return returnJsonObject.toString();
        }
        if (job.containsKey("fileName")) {
            fileName = job.getString("fileName");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数fileName");
            return returnJsonObject.toString();
        }
        if (job.containsKey("isThumb")) {
            isThumb = job.getBoolean("isThumb");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数isThumb");
            return returnJsonObject.toString();
        }

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
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/showTime/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/showTime/video";
                        break;
                    default://未知的文件类型
                        returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                        returnJsonObject.put("resultDesc", "未知的文件类型");
                        return returnJsonObject.toString();
                }
                break;
            case "addOrEditFootPrint"://新增或编辑宝贝足迹接口
                switch (fileType) {
                    case 1://图片
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/footprint/img";
                        break;
                    case 2://视频
                        filePath = "/garden/" + gardenId + "/class/" + classId + "/baby/" + babyId + "/footprint/video";
                        break;
                    default:
                        returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                        returnJsonObject.put("resultDesc", "未知的文件类型");
                        return returnJsonObject.toString();
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
            default:
                returnJsonObject.put("resultCode", GlobalStatus.unknown.toString());
                returnJsonObject.put("resultDesc", "未知的接口类型");
                break;
        }
        try {
            switch (fileType) {
                case 1://图片
                    fileBase64String = handlerFileDownload(filePath, isThumb, false, fileName);
                    break;
                case 2://视频
                    fileBase64String = handlerFileDownload(filePath, isThumb, true, fileName);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "下载失败");
        }
        returnJsonObject.put("fileBase64String", fileBase64String);
        returnJsonObject.put("resultCode", GlobalStatus.succeed.toString());
        returnJsonObject.put("resultDesc", "操作成功");
        return returnJsonObject.toString();
    }*/

    /**
     * 将流转换为图片，并存储到指定路径
     *
     * @param imgPath 图片存储路径
     * @param ins
     */
    private void storeImg(String imgPath, InputStream ins) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        gartenNews.setTeacher(teacherdao.queryTeacher(teacherId));
        gartenNews.setKindergarten(kindergarten);
        gartenNews.setTitle(title);
        gartenNews.setSummary(summary);
        gartenNews.setDescription(description);


        switch (optType) {
            case 1: //发布
                publisDate = new Date();
                gartenNews.setPublishDate(publisDate);
                gartenNews.setModifyDate(publisDate);
                if (gartennewsdao.addGartenNews(gartenNews)) {
                    long newsId = gartenNews.getId();
                    returnJsoObject.put("id", newsId);
                    returnJsoObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsoObject.put("resultDesc", "操作成功");
                } else {
                    returnJsoObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsoObject.put("resultDesc", "操作失败");
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
                long newsId = job.getLong("id");
                gartenNews.setId(newsId);
                if (gartennewsdao.updateGartenNews(gartenNews)) {
                    returnJsoObject.put("id", newsId);
                    returnJsoObject.put("resultCode", GlobalStatus.succeed.toString());
                    returnJsoObject.put("resultDesc", "操作成功");
                } else {
                    returnJsoObject.put("resultCode", GlobalStatus.error.toString());
                    returnJsoObject.put("resultDesc", "操作失败");
                }
                break;
            default:
                returnJsoObject.put("resultCode", GlobalStatus.error.toString());
                returnJsoObject.put("resultDesc", "type类型错误，应为1或2");
                break;
        }

        return returnJsoObject.toString();
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
    public String queryCamera(@RequestBody String reqJson) {
        System.out.println("收到显示摄像头列表的请求。。。");
        JSONObject returnJsonObject = new JSONObject();
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }

        long gardenId, classId;
        int pageNum;
        if (jsonObject.containsKey("gardenId")) {
            gardenId = jsonObject.getLong("gardenId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数gardenId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("classId")) {
            classId = jsonObject.getLong("classId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数classId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("pageNum")) {
            pageNum = jsonObject.getInt("pageNum");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数pageNum");
            return returnJsonObject.toString();
        }

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
            returnJsonObject.put("totalCount", totalCount);
            //分页功能待修改
            returnJsonObject.put("hasNextPage", false);
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
    public String queryVideo(@RequestBody String reqJson) {
        System.out.println("收到查询摄像头的请求");
        JSONObject jsonObject = JSONObject.fromObject(reqJson);
        JSONObject returnJsonObject = new JSONObject();
        if (jsonObject == null) {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "请求参数异常");
            return returnJsonObject.toString();
        }
        int roleType;
        long roleId;
        long cameraId;
        long classId;
        boolean isPublic;

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
        if (jsonObject.containsKey("cameraId")) {
            cameraId = jsonObject.getLong("cameraId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数cameraId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("classId")) {
            classId = jsonObject.getLong("classId");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数classId");
            return returnJsonObject.toString();
        }
        if (jsonObject.containsKey("isPublic")) {
            isPublic = jsonObject.getBoolean("isPublic");
        } else {
            returnJsonObject.put("resultCode", GlobalStatus.error.toString());
            returnJsonObject.put("resultDesc", "找不到参数isPublic");
            return returnJsonObject.toString();
        }

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
    private void handlerFileUpload(String filePath, String fileName, InputStream ins, Boolean hasThumb) {
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
                fileUrl = baseDir + fileDirPath + "/thumb/" + fileName.substring(0,fileName.lastIndexOf(".jpg"))+"_thumb.jpg";
            } else {
                fileUrl = baseDir + fileDirPath + "/origin/" + fileName;
            }
        }
        byte[] fileBytes = FileUtils.fileToByteArrayByTraditionalWay(fileUrl);
//        String fileBase64String = new String(Base64.encodeBase64(fileBytes));
        return fileBytes;
    }

}
