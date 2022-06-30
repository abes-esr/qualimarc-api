package fr.abes.qualimarc.web.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PublicController {
    @GetMapping("/hello/{name}")
    public String hello(String name) {
        return "hello " + name;
    }

}
