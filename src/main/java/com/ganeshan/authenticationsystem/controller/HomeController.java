package com.ganeshan.authenticationsystem.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/account")
public class HomeController {


    @GetMapping("/home")
    private String indexPage(HttpServletResponse response) {
        setExtraCookie(response);
        return "home";
    }

    public void setExtraCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("extraCookie", "One_more_cookie");
        cookie.setMaxAge(259200);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
