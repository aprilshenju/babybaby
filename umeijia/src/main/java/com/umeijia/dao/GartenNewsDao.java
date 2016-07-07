package com.umeijia.dao;
import com.umeijia.util.DBManager;
import com.umeijia.vo.GartenNews;
import com.umeijia.vo.Pager;
import com.umeijia.vo.Parents;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/17.
 */
@Scope("prototype")
@Repository("gartennewsdao")
public class GartenNewsDao {
    public GartenNewsDao(){
        
    }


    public GartenNews queryGartenNews(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from GartenNews as gnews where gnews.id=%d and gnews.valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            GartenNews gnews = (GartenNews) list.get(0);
            return gnews;
        }else {
            return null;
        }
    }

    /**
     *    // 按 学校获取
     * **/
    public Pager queryGartenNewss(long school_id,Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql= String.format("from GartenNews as gnews where gnews.kindergarten.id=%d and gnews.valid=1 order by gnews.modifyDate desc",school_id);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<GartenNews> list=(List<GartenNews>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }


    public List<GartenNews> queryGartenNewssByShoolIdAndTitle(long school_id,String title) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from GartenNews as gnews where gnews.kindergarten.id=%d and gnews.title=\'%s\' and gnews.valid=1 order by gnews.modifyDate desc",school_id,title);
        Query query = session.createQuery(sql);
        List <GartenNews> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    /**
     * 后续改为分页处理,page传入 每页多少项，当前需要第几页的内容。返回总项目数（总页数可通过计算获得）。
     * **/
    public Pager queryGartenNewsPageBySchool(long school_id, Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from GartenNews gn where gn.kindergarten.id=%d and gn.valid=1",school_id);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<GartenNews> list=(List<GartenNews>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }

    public boolean addGartenNews(GartenNews gnews) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(gnews);
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


    public boolean updateGartenNews(GartenNews gnews) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(gnews);
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

    public boolean invalidGartenNews(long g_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update GartenNews bs set bs.valid=0 where bs.id=%d",g_id);
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


    public boolean deleteGartenNews(long id) {
        boolean result=false;
        GartenNews gnews = queryGartenNews(id);
        if(gnews!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(gnews);
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

