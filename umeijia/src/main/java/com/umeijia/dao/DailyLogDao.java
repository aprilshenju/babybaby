package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.DailyLog;
import com.umeijia.vo.Pager;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 * 待优化问题：目前更新属性值，是先查询对象，然后更新整个对象的。并不高效�
 * 应当是直接修改字段，用update操作
 *
 *
 */
@Scope("prototype")
@Repository("dailylogdao")
public class DailyLogDao {
    public DailyLogDao(){

    }

    public DailyLog queryDailyLog(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from DailyLog as log where log.id=%d",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            DailyLog log = (DailyLog) list.get(0);
            return log;
        }else {
            return null;
        }
    }

    /**
     * 日志查询，暂时未做筛选，后期应追加筛选条�比如时间筛�
     *
     * 后续改为分页处理,page传入 每页多少项，当前需要第几页的内容。返回总项目数（总页数可通过计算获得）�
     * **/
    public Pager queryDailyLogPage( int year,int month,Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from DailyLog as log where year(log.log_date)=%d and month(log.log_date)=%d order by log.log_date desc",year,month);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<DailyLog> list=(List<DailyLog>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }

    /**
     * 按月查日�
     * @param
     * @return
     */
    public List<DailyLog> queryDailyLogByMonth(int year,int month) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from DailyLog as log where year(log.log_date)=%d and month(log.log_date)=%d order by log.log_date desc",year,month);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){

            return list;
        }else {
            return null;
        }
    }
    
    public boolean addDailyLog(DailyLog log) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(log);
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

    public boolean updateDailyLog(DailyLog log) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(log);
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


    public boolean deleteDailyLog(long id) {
        boolean result=false;
        DailyLog log = queryDailyLog(id);
        if(log!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(log);
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
