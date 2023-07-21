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

/**
 * @author shkstart
 * @create 2023-07-12 17:33
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    /**
     * 不加注解的，最笨拙的方式。
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @RequestMapping("/http")
    public void http(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        //获取请求数据
        System.out.println(httpServletRequest.getMethod());
        System.out.println(httpServletRequest.getServletPath());
        Enumeration<String> names = httpServletRequest.getHeaderNames();
        //获取消息头
        while (names.hasMoreElements()){
            String name=names.nextElement();//请求体的key
            String value=httpServletRequest.getHeader(name);
            System.out.println(name+":"+value);
        }
        //获取请求体的数据
        System.out.println(httpServletRequest.getParameter("code"));

        //返回响应数据
        httpServletResponse.setContentType("text/html;charset=utf-8");
        try{
            PrintWriter writer=httpServletResponse.getWriter();
            writer.write("<h1>牛客网</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Get请求
    // /students?current=1&limit=20，这里指定用get请求
    @RequestMapping(path="/students",method= RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name="current",required=false,defaultValue = "1") int current,
            @RequestParam(name="limit",required=false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123 后面的123是id值，是动态的，我们必须要使用{id}接收请求，加上@PathVariable用来给参数id赋值
    @RequestMapping(path="/student/{id}",method =RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id ){
        System.out.println(id);
        return "a student";
    }

    // Post请求
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应html数据
    //通过model和view数据生成动态的html
    @RequestMapping(path="/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav= new ModelAndView();
        //模板的内容
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");//模板放在templates下，参数里面就是传它的下级路径。
        return mav;
    }
    //把model装到参数里，把view返回给dispatcherServelet，model和view两个都持有，可以获得。而上面的方法则是封装到一个对象里面。
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/view";
    }

    //响应json数据（异步请求）
    //java对象->json字符串->js对象
    @RequestMapping(path="/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        return emp;
    }

    //返回多组员工数据
    @RequestMapping(path="/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","王五");
        emp.put("age",28);
        emp.put("salary",10000.00);
        list.add(emp);

        return list;
    }

    //cookie示例
    //设置cookie
    @RequestMapping(path="/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效的范围
        cookie.setPath("/community/alpha");
        //设置cookie的生存时间
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }
    //获取cookie
    @RequestMapping(path="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    //session实例
    //设置session
    @RequestMapping(path = "/session/set",method=RequestMethod.GET)
    @ResponseBody
    public String SetSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session";
    }

    //获取session
    @RequestMapping(path = "/session/get",method=RequestMethod.GET)
    @ResponseBody
    public String GetSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax实例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
