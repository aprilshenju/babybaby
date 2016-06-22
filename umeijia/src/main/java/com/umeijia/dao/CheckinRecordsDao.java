package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.CheckinRecords;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 * 待优化问题：目前更新属性值，是先查询对象，然后更新整个对象的。并不高效。
 * 应当是直接修改字段，用update操作
 *
 *
 */
@Scope("prototype")
@Repository("checkinrecorddao")
public class CheckinRecordsDao {
    public CheckinRecordsDao(){

    }

    public CheckinRecords queryCheckinRecords(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from CheckinRecords as cr where cr.id=%d",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            CheckinRecords cr = (CheckinRecords) list.get(0);
            return cr;
        }else {
            return null;
        }
    }

    /**
     * 家长查询
     * @param babyId
     * @param year
     * @param month
     * @param day
     * @return
     */
    public List<CheckinRecords> queryCheckinRecordsByBabyAndTime(long babyId,int year,int month,int day){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from CheckinRecords as cr where cr.stu_id=%d and year(cr.date)=%d and month(cr.date)=%d and day(cr.date)=%d group by date desc",babyId,year,month,day);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    /**
     * 老师或园长查询
     * @param classId
     * @param year
     * @param month
     * @param day
     * @return
     */
    public List<CheckinRecords> queryCheckinRecordsByClassAndTime(long classId,int year,int month,int day){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from CheckinRecords as cr where cr.class_id=%d and year(cr.date)=%d and month(cr.date)=%d and day(cr.date)=%d group by stu_id desc",classId,year,month,day);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    
    public boolean addCheckinRecords(CheckinRecords checkincard) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(checkincard);
            session.flush();
            session.getTransaction().commit();
            result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }

    public boolean updateCheckinRecords(CheckinRecords checkincard) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(checkincard);
            session.flush();
            session.getTransaction().commit();
            result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }


    public boolean deleteCheckinRecords(long id) {
        boolean result=false;
        CheckinRecords checkincard = queryCheckinRecords(id);
        if(checkincard!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(checkincard);
                session.flush();
                session.getTransaction().commit();
                result=true;
            } catch (HibernateException e) {
                e.printStackTrace();
                session.getTransaction().rollback();
                result=false;
            } finally{
                session.close();
                return result;
            }
        }
        return false;
    }
}
