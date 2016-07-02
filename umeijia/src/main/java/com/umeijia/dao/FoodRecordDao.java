package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.FoodRecord;
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
@Repository("foodrecorddao")
public class FoodRecordDao {
    public FoodRecordDao(){

    }

    public FoodRecord queryFoodRecord(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from FoodRecord as food where food.id=%d and valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            FoodRecord food = (FoodRecord) list.get(0);
            return food;
        }else {
            return null;
        }
    }

    /**
     * 根据classId来查找饮食记录
     * @param classId
     * @return
     */
    public FoodRecord queryFoodRecordByClassId(long classId,int year,int month,int day) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from FoodRecord as food where food.class_id=%d and year(food.date)=%d and month(food.date)=%d and day(food.date)=%d and valid=1",classId,year,month,day);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            FoodRecord food = (FoodRecord) list.get(0);
            return food;
        }else {
            return null;
        }
    }

    public boolean addFoodRecord(FoodRecord food) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(food);
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

    /**
     * 后续改为分页处理
     * **/
    public List<FoodRecord> queryFoodRecordList(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from FoodRecord as fd where fd.cla.id=%d and valid=1 order by fd.date desc",class_id);
        Query query = session.createQuery(sql);
        List <FoodRecord> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public boolean setFoodRecord(long class_id,String foodrecord,String images){
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update FoodRecord f set f.records=\'%s\' and f.image_urls=\'%s' where c.class_id=%d",foodrecord,images,class_id);
            Query queryupdate=session.createQuery(hql);
            int ret=queryupdate.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            if(ret>=0)
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

    public boolean updateFoodRecord(FoodRecord food) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(food);
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

    public boolean invalidFoodRecord(long g_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update FoodRecord bs set bs.valid=0 where bs.id=%d",g_id);
            Query queryupdate=session.createQuery(hql);
            int ret=queryupdate.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            if(ret>=0)
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

    public boolean deleteFoodRecord(long id) {
        boolean result=false;
        FoodRecord food = queryFoodRecord(id);
        if(food!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(food);
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
