package com.umeijia.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;


public class DBManager {
/*	@Autowired
	@Qualifier("sessionFactory")*/
	public  static  long EXPIRE_SECONDS=720000000; // 200小时，后续改动

	private static SessionFactory factory;
	static{
		//读取配置文件hibernate.cfg.xml
		Configuration cfg = new Configuration().configure();
		//创建会话工厂SessionFactory
		factory = cfg.buildSessionFactory();
	}
	public static Session getSession(){
		return factory.openSession();
	}
	

	 
	public static void main(String[] args) {
		Configuration cfg = new Configuration().configure();
		SchemaExport export = new SchemaExport(cfg);
		export.create(true, true);
		
	
	}
	

}
