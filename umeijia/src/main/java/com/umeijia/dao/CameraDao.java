package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Camera;
import com.umeijia.vo.ClassActivity;
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
 * 寰呬紭鍖栭棶棰橈細鐩墠鏇存柊灞炴€у€硷紝鏄厛鏌ヨ瀵硅薄锛岀劧鍚庢洿鏂版暣涓璞＄殑銆傚苟涓嶉珮鏁堛€ * 搴斿綋鏄洿鎺ヤ慨鏀瑰瓧娈碉紝鐢╱pdate鎿嶄綔
 *
 *
 */
@Scope("prototype")
@Repository("cameradao")
public class CameraDao {
    public CameraDao(){

    }


    public Camera queryCamera(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.id=%d and valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Camera camera = (Camera) list.get(0);
            return camera;
        }else {
            return null;
        }
    }

    /**
     * 鐝骇绉佹湁鎽勫儚澶村垪琛     * **/
    public List queryPrivateCamerasList(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.cla.id=%d and ca.is_public=0 and valid=1",class_id);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    public Pager getCamerasList(Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql= String.format("from Camera as ca where ca.valid=1");
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<Camera> list=(List<Camera>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }

    public List getCamerasListBySchoolId(long schoolId) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.garten.id=%d and ca.valid=1",schoolId);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List getCamerasListByClassId(long classId) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.cla.id=%d and ca.valid=1",classId);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List getCamerasListByClassId(long classId) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.cla.id=%d and ca.valid=1",classId);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List getCamerasListBySchoolIdAndCameraName(long schoolId,String name) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.garten.id=%d and ca.manufactory=\'%s\' and ca.valid=1",schoolId,name);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List getCamerasListByCameraName(String name) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where  ca.manufactory=\'%s\' and ca.valid=1",name);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    /**
     * 涓€涓辜鍎垮洯鍏叡鍖哄煙鎽勫儚澶村垪琛     * **/
    public List queryPublicCamerasList(long garten_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Camera as ca where ca.garten.id=%d and ca.is_public=1 and valid=1",garten_id);
        Query query = session.createQuery(sql);
        List <Camera> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    public boolean addCamera(Camera camera) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(camera);
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

    public boolean updateCamera(Camera camera) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(camera);
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

    public boolean invalidCamera(long g_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Camera bs set bs.valid=0 where bs.id=%d",g_id);
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

    // 鐝骇鍒犻櫎鎽勫儚澶达紝鍙槸瑙ｉ櫎鍏崇郴
    public boolean invalidCameraByClass(long class_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Camera ca set ca.cla.id=0 where ca.cla.id=%d",class_id);
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

    // 鎸夊辜鍎垮洯锛岀洿鎺ヤ娇鎽勫儚澶存棤鏁    public boolean invalidCameraByGarten(long garten_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Camera ca set ca.valid=0 where ca.garten.id=%d",garten_id);
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

    public boolean deleteCamera(long id) {
        boolean result=false;
        Camera camera = queryCamera(id);
        if(camera!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(camera);
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
