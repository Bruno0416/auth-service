package com.mariluz.auth_service.demo;

import org.springframework.web.bind.annotation.*;

// endpoint para testear seguridad via token
@RestController
@RequestMapping("/api/v1")
public class DemoController {

    @GetMapping("/demo")
    public String welcome() {
        return "Welcome from secure endpoint";
    }
}
