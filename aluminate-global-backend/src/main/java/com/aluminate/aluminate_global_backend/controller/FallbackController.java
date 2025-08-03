package com.aluminate.aluminate_global_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/**")
    public String catchAll(HttpServletRequest request) {
        return "Caught path: " + request.getRequestURI();
    }
}

