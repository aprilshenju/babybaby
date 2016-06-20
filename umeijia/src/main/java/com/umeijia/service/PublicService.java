package com.umeijia.service;

import com.umeijia.dao.AdministratorDao;
import com.umeijia.dao.AgentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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


    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test2(){

        return "welcom to UMJ server... public service ";
    }
}
