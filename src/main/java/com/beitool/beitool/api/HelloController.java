package com.beitool.beitool.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello Beitool!";
    }

    /*Swagger로 이동하기 위함*/
    @GetMapping("/usage/api/")
    public String redirectSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
