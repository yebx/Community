package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphadaohibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "hibernate";
    }
}
