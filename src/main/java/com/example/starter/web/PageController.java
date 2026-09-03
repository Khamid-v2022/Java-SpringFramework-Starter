package com.example.starter.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/api-demo")
    public String apiDemo(Model model) {
        model.addAttribute("pageTitle", "REST API Demo");
        return "api-demo";
    }

    @GetMapping("/websocket")
    public String websocket(Model model) {
        model.addAttribute("pageTitle", "WebSocket Chat");
        return "websocket";
    }
}
