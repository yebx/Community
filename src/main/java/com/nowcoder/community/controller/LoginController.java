package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.thymeleaf.TemplateEngine;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;


    @Value("${server.servlet.context-path}")
    private String contextPath;


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

    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        //验证码图片生成
        BufferedImage image=kaptchaProducer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha",text);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os =response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败:"+e.getMessage());
        }
    }


    @RequestMapping(path = "/login" ,method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model,HttpSession session,HttpServletResponse response){
        //检查验证码
        String kaptcha = (String)session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确哦");
            return "/site/login";
        }

        //检查账号密码
        int expiredSeconds = rememberme ? REMEMEBER_EXPIRED_SECONDS : DEFALUT_EXPIRED_SECONDS;
        Map<String,Object> map=userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }

    }

    @RequestMapping(path = "/logout" ,method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }


    @RequestMapping(path = "/forget" ,method = RequestMethod.GET)
    public String getforgetPage(){
        return "/site/forget";
    }

    @RequestMapping(path = "/forget/code" ,method = RequestMethod.GET)
    @ResponseBody
    public String getForgetCode(String email,HttpSession session){
        if(StringUtils.isBlank(email)){
            return CommunityUtil.getJSONString(1, "邮箱不能为空！");
        }
        //发送邮箱
        Context context = new Context();
        context.setVariable("email",email);
        String code=CommunityUtil.generateUUID().substring(0,4);
        context.setVariable("verifyCode",code);
        String content = templateEngine.process("/mail/forget",context);
        mailClient.sendMail(email,"找回密码",content);
        //保存验证码
        session.setAttribute("verifyCode",code);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/forget/password",method = RequestMethod.POST)
    public String resetPassword(String email,String verifycode,String password,Model model  ,HttpSession session){
        String code = (String) session.getAttribute("verifyCode");
        if(StringUtils.isBlank(verifycode) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(verifycode)){
            model.addAttribute("codeMsg","验证码不正确哦");
            return "/site/login";
        }

        Map<String,Object> map = userService.resetPassword(email,password);
        if(map.containsKey("user")){
                return "redirect:/login";
        }else {
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }





    }

}
