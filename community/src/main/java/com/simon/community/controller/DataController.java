package com.simon.community.controller;

import com.simon.community.service.DataService;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 跳转到统计页面
     * */
    @RequestMapping(value = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    /**
     * 统计网站UV
     * @DateTimeFormat:自动将前端穿的字符串转换成Date
     * */
    @RequestMapping(value = "data/uv",method = RequestMethod.POST)
    public String getUv(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv= dataService.calculateUv(start,end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        return  "forward:/data";
    }

    /**
     * 统计活跃用户
     * @DateTimeFormat:自动将前端穿的字符串转换成Date
     * */
    @RequestMapping(value = "data/dau",method = RequestMethod.POST)
    public String getDau(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long dau= dataService.calculateDau(start,end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        return  "forward:/data";
    }
}
