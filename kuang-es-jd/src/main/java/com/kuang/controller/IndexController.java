package com.kuang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @auther 陈彤琳
 * @Description $
 * 2023/11/10 20:23
 */
@Controller
public class IndexController {
    @GetMapping({"/", "/index"})
    public String index(){
        return "index";
    }
}
