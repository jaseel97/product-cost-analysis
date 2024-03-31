package com.minions.PropertyCostAnalysis.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/home")
public class HomeController {
    @GetMapping
    public String Home(){
        return "Home Page";
    }
}
