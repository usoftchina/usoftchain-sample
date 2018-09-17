package com.usoftchina.chain.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public ModelAndView dashboard() {
        return new ModelAndView("dashboard");
    }
}
