package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.ClassAlbum;
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
@Repository("classalbumdao")
public class ClassAlbumDao {
    public ClassAlbumDao(){

    }


    public ClassAlbum queryClassAlbum(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassAlbum as ca where ca.id=%d ",id);
        Query query = session.createQuery(sql);
        List <ClassAlbum> list = query.list();
        session.close();
        if(list.size()>0){
            ClassAlbum ca = (ClassAlbum) list.get(0);
            return ca;
        }else {
            return null;
        }
    }

    public List queryClassAlbumList(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassAlbum as ca where ca.class_id=%d order by ca.date desc",class_id);
        Query query = session.createQuery(sql);
        List <ClassAlbum> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    
    public boolean addClassAlbum(ClassAlbum album) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(album);
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

    public boolean updateClassAlbum(ClassAlbum album) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(album);
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


    public boolean deleteClassAlbum(long id) {
        boolean result=false;
        ClassAlbum album = queryClassAlbum(id);
        if(album!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(album);
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
