package com.umeijia.service;

import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.umeijia.util.MD5;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.umeijia.dao.ParentsDao;
import com.umeijia.vo.Parents;

@Service
@Path("/parents_service")
public class ParentsService {
    static Logger logger = Logger.getLogger("info");
    @Autowired
    @Qualifier("parentsdao")
    private ParentsDao parentsdao;
    public ParentsService() {

    }
    public ParentsDao getParentsdao() {
        return parentsdao;
    }
    public void setParentsdao(ParentsDao parentsdao) {
        this.parentsdao = parentsdao;
    }


    /**
     * 测试get
     * @return
     */
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test(){

        return "welcom to umeijia";
    }


    /***
     * 添加用户
     * curl -X POST -H "Content-Type:application/json" -d {"phoneNum":"15208207369","password":"123456","name":"jalsary","gender":1,"email":"635313831@qq.com"} http://127.0.0.1/umeijiaServer/parents_service/add
     * **/
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addRequest(@RequestBody String userinfo,@Context HttpHeaders headers){
        //
//			String tkn = headers.getRequestHeader("tkn").get(0);
//			String uid = headers.getRequestHeader("uid").get(0);
        JSONObject job =JSONObject.fromObject(userinfo);
        JSONObject job_out=new JSONObject();
        //logger.info(tkn+"-----"+uid);
//			if (checkLogin(uid, tkn)==false) { /* not login */	
//				job_out.put("msg",GlobalStatus.error.toString());
//				return job_out.toString();
//			}
        Parents parents = new Parents();
        parents.setPhone_num(job.getString("phoneNum"));
        parents.setPwd_md(job.getString("password"));
        parents.setName(job.getString("name"));
        parents.setGender(job.getInt("gender"));
        parents.setRegist_date(new Date());
        parents.setEmail(job.getString("email"));
        if(parentsdao.addParents(parents)){
            job_out.put("resultCode","0000");
            job_out.put("resultDesc","添加成功");
        }
        else{
            job_out.put("resultCode","0001");
            job_out.put("resultDesc","添加失败");
        }

        return job_out.toString();
    }

    public boolean checkLogin(String uid, String tkn){
        return true;
    }
}
