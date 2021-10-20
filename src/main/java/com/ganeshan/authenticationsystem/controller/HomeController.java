package com.ganeshan.authenticationsystem.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class HomeController {


    @GetMapping("/home")
    private String indexPage() {
        return "home";
    }
}
