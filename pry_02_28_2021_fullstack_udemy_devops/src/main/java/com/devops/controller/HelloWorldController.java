package com.devops.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {

    // Este método se ejecuta por cada HTTP-request hacia nuestra app
    @RequestMapping("/")
    public String sayHello() {
        return "index";
    }
}

