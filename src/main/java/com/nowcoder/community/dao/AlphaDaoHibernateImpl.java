package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author shkstart
 * @create 2023-07-13 10:44
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
