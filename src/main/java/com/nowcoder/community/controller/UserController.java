package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Value("${community.path.upload}")
    private String upload;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //上传头像
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        //获取文件后缀
        String fileName=headerImage.getOriginalFilename();

        String suffix;
        if(fileName.lastIndexOf(".")!=-1){
            suffix= fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else{
            model.addAttribute("error","文件的格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        fileName= CommunityUtil.generateUUID()+suffix;

        //确定文件存放路径
        File dest=new File(upload+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!",e);
        }

        //更新当前用户的头像的路径（web访问路径）http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //更新密码
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(Model model,String oldPassword,String newPassword,String secPassword){
        //验证原密码是否正确
        User user = hostHolder.getUser();
        oldPassword=CommunityUtil.md5(oldPassword+user.getSalt());
        if(!user.getPassword().equals(oldPassword)){
            model.addAttribute("oldPasswordMsg","密码不正确");
            return "/site/setting";
        }else if(!newPassword.equals(secPassword)){
            model.addAttribute("secPasswordMsg","两次密码不一致");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(),secPassword);
        return "redirect:/login";
    }

    //更新头像
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName=upload+"/"+fileName;
        //文件后缀
        String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
        //响应图片
        response.setContentType("image/"+suffix);
        try(
            FileInputStream fis=new FileInputStream(fileName);
            OutputStream os = response.getOutputStream();
        ){
                byte[] buffer=new byte[1024];
                int b=0;
                while ((b=fis.read(buffer))!=-1){
                    os.write(buffer,0,b);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
