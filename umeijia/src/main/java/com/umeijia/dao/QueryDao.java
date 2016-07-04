package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Pager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dolphin0520 on 16-7-4.
 */
@Scope("prototype")
@Repository("querydao")
public class QueryDao {
    public Pager queryPager(String hql,Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
//        String hql=String.format("from BabyShowtime bs where bs.class_id=%d",class_id);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session= DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
//        List<BabyShowtime> list=(List<BabyShowtime>)query.list();
        List list = query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }
}
