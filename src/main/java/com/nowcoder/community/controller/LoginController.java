package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register" ,method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login" ,method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path="/register",method=RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map=userService.register(user);
        if(map == null || map.isEmpty()){//如果没有问题的话，会跳转到中转页面
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{//如果有错误 会返回错误信息到注册页面 之前填写的用户、邮箱都会保留
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int rows = userService.activation(userId,code);
        if(rows==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号可以正常使用了！！");
            model.addAttribute("target","/login");
        }else if(rows==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，账号已经激活过了");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，激活码无效！！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

}
