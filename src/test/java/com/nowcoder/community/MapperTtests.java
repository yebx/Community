package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTtests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
        User user=userMapper.selectById(122);
        System.out.println(user.toString());

        user=userMapper.selectByName("lll");
        System.out.println(user.toString());

        user=userMapper.selectByEmail("nowcoder122@sina.com");
        System.out.println(user.toString());
    }

    @Test
    public void testInsertUser(){
        User user=new User();
        user.setUsername("gyx");
        user.setPassword("abc123");
        user.setSalt("gyxxxxxx");
        user.setEmail("1013159206@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/gyx.png");
        user.setCreateTime(new Date());

        int t=userMapper.insertUser(user);
        System.out.println(t);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int sts = userMapper.updateStatus(150,1);
        System.out.println(sts);
        int hes = userMapper.updateHeader(150,"");
        System.out.println(hes);
        int pws = userMapper.updatePassword(150,"abcc");
        System.out.println(pws);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list =discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost post :list){
            System.out.println(post);
        }
        int rows=discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket.toString());

        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket.toString());

    }

}
