package com.umeijia.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

// ip/umeijia/teacher_service/hello

@Service
@Path("/teacher_service")
public class TeacherService {

	@Path("/hello")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String test(){
	
		return "welcom to UMJ server... xiao峰峰";
	}
}
