package com.umeijia.service;

import com.umeijia.dao.AdministratorDao;
import com.umeijia.dao.AgentDao;
import com.umeijia.vo.Administrator;
import com.umeijia.vo.Agent;
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







    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2(){

        return "welcom to UMJ server... xiao大峰峰";
    }

    @Path("/addAdmin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addAdmin(@RequestBody String userinfo, @Context HttpHeaders headers) {
// curl -X POST -H "Content-Type:application/json" -d '{"phone":"156053432304","pwd":"fdf","name":"uhdfh","email":"72fds5244@qq.com","super":"1"}' http://182.150.6.36:8080/umeijiaServer/run_service/addAdmin
// curl -X POST -H "Content-Type:application/json" -d '{"phone":"155456644","pwd":"234df","name":"ljiaoshou","email":"723370244@qq.com","super":"1"}' http://127.0.0.1/umeijiaServer/run_service/addAdmin

    /*    String tkn = headers.getRequestHeader("tkn").get(0);
        String uid = headers.getRequestHeader("uid").get(0);*/
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();

        String phone= job.getString("phone");
        String pwd= job.getString("pwd");
        String name= job.getString("name");
        String email=job.getString("email");
        boolean is_super= job.getBoolean("super");
        Date date = new Date();
        Administrator admin =  new Administrator(phone,email,pwd,name,date,is_super);
        administratordao.addAdministrator(admin);

        job_out.put("status","success");
        job_out.put("id", admin.getId());
        return  job_out.toString();
    }

    @Path("/addAgent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addAgent(@RequestBody String userinfo, @Context HttpHeaders headers) {
// curl -X POST -H "Content-Type:application/json" -d '{"phone":"156053432304","pwd":"fdf","name":"uhdfh","email":"72fds5244@qq.com","super":"1"}' http://182.150.6.36:8080/umeijiaServer/run_service/addAdmin
// curl -X POST -H "Content-Type:application/json" -d '{"phone":"155456644","pwd":"234df","name":"ljiaoshou","email":"723370244@qq.com","super":"1"}' http://127.0.0.1/umeijiaServer/run_service/addAdmin

    /*    String tkn = headers.getRequestHeader("tkn").get(0);
        String uid = headers.getRequestHeader("uid").get(0);*/
        JSONObject job = JSONObject.fromObject(userinfo);
        JSONObject job_out = new JSONObject();

        String phone= job.getString("phone");
        String pwd= job.getString("pwd");
        String name= job.getString("name");
        String email=job.getString("email");
        String company=job.getString("company");
        float price = (float) job.getDouble("price");

        Date date = new Date();
        Agent agent=new Agent(phone,email,pwd,name,date,company,price);
        agentdao.addAgent(agent);

        job_out.put("status","success");
        job_out.put("id",agent.getId());
        return  job_out.toString();
    }

}
