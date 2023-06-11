package com.nowcoder.community.controller;


import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {


    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){

        return "Hello gyx Spring Boot!";
    }
    @RequestMapping("/date")
    @ResponseBody
    public String getDate(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name=enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name + ":"+ value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter printWriter=response.getWriter();
        ){

            printWriter.write("<h1>郭雨昕专用测试文字</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Get请求

    @RequestMapping(path="/students",method= RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name="current",required = false,defaultValue = "1")int current,
            @RequestParam(name="limit",required = false,defaultValue = "10")int limit
    ){
        System.out.println(current);
        System.out.println(limit);
        return "some students such as gyx";
    }


    @RequestMapping(path="/students/{id}",method=RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id ){
        System.out.println(id);
        return "a student gyx";
    }


    //POST请求
    @RequestMapping(path="/student" ,method=RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应Html数据
    //向浏览器响应HTML格式的数据
    @RequestMapping(path = "/teacher",method =RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav =new ModelAndView();
        mav.addObject("name","张香荣");
        mav.addObject("age","51");
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path = "/school",method =RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","ucla");
        model.addAttribute("age",200);
        return "/demo/view";
    }

    //响应json数据(异步请求)
    //java对象  json对象  js对象

    //向浏览器响应JSON格式的数据。
    @RequestMapping(path = "/emp", method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","gyx");
        emp.put("age",100);
        emp.put("salary",11000.00);
        return emp;

    }


    @RequestMapping(path = "/emps", method=RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();

        Map<String,Object> emp=new HashMap<>();
        emp.put("name","gyx");
        emp.put("age",10);
        emp.put("salary",11000.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","zxr");
        emp.put("age",20);
        emp.put("salary",12000.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","ghy");
        emp.put("age",30);
        emp.put("salary",20000.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","gjl");
        emp.put("age",40);
        emp.put("salary",10000.00);
        list.add(emp);
        return list;

    }


    //cookie
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //设置生效范围
        cookie.setPath("/community/alpha");
        //默认生效时间是关掉浏览器就消失
        //设置生存时间
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie by gyx";


    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie by gyx";
    }

    //session
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session by ggyxx";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session by ggyxx" ;
    }
}
