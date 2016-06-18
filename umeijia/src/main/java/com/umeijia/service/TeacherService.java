package com.umeijia.service;

import com.sun.jersey.multipart.FormDataParam;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

// ip/umeijia/teacher_service/hello

@Service
@Path("/teacher_service")
public class TeacherService {
    
	
	@Path("/hello")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String test(){
	
		return "welcom to UMJ server... xiao小峰峰";
	}

	@Path("/imgUpload")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String imgUpload(@FormDataParam("imgData")InputStream ins, @FormDataParam("jsonArgs") String reqJson){
        System.out.println("接收到请求...");
		String path = "D:/work/开发/幼美加/imgs";
		File dir = new File("D:/work/开发/幼美加/imgs");
		if(!dir.exists()){
			dir.mkdirs();
            System.out.println("创建图片目录...");
		}
		JSONObject job = JSONObject.fromObject(reqJson);
		JSONObject returnJsonObject = new JSONObject();
		String imgName = job.getString("imgName");
		File img = new File(path+"/"+imgName);
		try {
			OutputStream os = new FileOutputStream(img);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = ins.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		}catch (Exception e){
			returnJsonObject.put("status","error");
			return returnJsonObject.toString();
		}
		returnJsonObject.put("status","success");
		return returnJsonObject.toString();
	}
}
