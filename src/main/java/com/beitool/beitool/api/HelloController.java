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
    @GetMapping("/doc/api/v1/")
    public String redirectSwagger() {
        return "redirect:/swagger-ui/index.html";
    }

    /*redoc이 적용된Swagger로 이동하기 위함*/
    @GetMapping("/doc/api/v2/")
    public String redirectSwaggerWithRedoc() {
        return "redirect:/dist/redoc/index.html";
    }
}
