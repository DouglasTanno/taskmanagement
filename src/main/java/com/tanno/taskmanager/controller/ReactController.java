package com.tanno.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ReactController {


    @RequestMapping(value = {
            "/login",
            "/dashboard",
            "/projects/**"
    })
    public String forwardReact() {

        return "forward:/index.html";
    }

}