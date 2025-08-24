package com.simon.community.controller;

import com.simon.community.annotation.LoginRequired;
import com.simon.community.pojo.User;
import com.simon.community.service.FollowService;
import com.simon.community.service.LikeService;
import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController implements CommunityConstant {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;


    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String UploadHeader(@RequestParam("headerImage") MultipartFile file, Model model) {
        if (file.isEmpty() || file == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        //命名上传到服务端后文件的名字，防止覆盖
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }
        String uploadName = CommunityUtil.generateUUID() + suffix;
        // 获取上传之后的存放目录
        File dest = new File(uploadPath + "/" + uploadName);
        //把文件写入到指定路径
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件上传失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        //更新当前用户头像的路径（web路径）
        //http://localhost:8080/community/user/head/*
        userService.updateHeaderUrl(domain + contextPath + "/user/header/" + uploadName, hostHolder.getUser().getId());
        return "redirect:/index";
    }

    /**
     * 获取头像
     */
    @RequestMapping(value = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response)  {
        //去上传路径找对应的图片
        filename = uploadPath + "/" + filename;
        //设置返回类型，动态获取
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        //获取文件输入流和输出流，完成文件的拷贝
        try (
                FileInputStream inputStream = new FileInputStream(filename);
            ){
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] buf=new byte[1024];
            int len=0;
            while((len=inputStream.read(buf))!=-1){
                outputStream.write(buf,0,len);
            }
        } catch (IOException e) {
            log.error("文件读取失败："+e.getMessage());
        }

    }

    @LoginRequired
    @RequestMapping(value="/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String oldPassword,String newPassword,Model model){
        if(oldPassword==null || oldPassword.equals("")){
            model.addAttribute("oldPasswordMsg","旧密码不能为空");
            return "/site/setting";
        }
        if(newPassword==null || newPassword.equals("")){
            model.addAttribute("newPasswordMsg","新密码不能为空");
            return "/site/setting";
        }
        //检查原密码看是否正确
        User user = hostHolder.getUser();
        String saltedOldPassword=CommunityUtil.md5(oldPassword+user.getSalt());
        if(!saltedOldPassword.equals(user.getPassword())){
            model.addAttribute("oldPasswordMsg","原密码输入错误");
            return "/site/setting";
        }
        //新旧密码是否相同
        if(oldPassword.equals(newPassword)){
            model.addAttribute("newPasswordMsg","新密码与旧密码不能一致");
            return "/site/setting";
        }
        //此时更新密码
        userService.updatePassword(user,newPassword);
        return "redirect:/index";
    }

    /**
     * 访问个人主页
     * */
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        //查询用户获得赞的数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注，先判断当前用户是否登录
        boolean hasFollowed=false;
        if(hostHolder.getUser()!=null) {
            hasFollowed= followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }

}
