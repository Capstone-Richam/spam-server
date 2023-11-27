package com.Nunbody.global.common;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckApiController {
    @RequestMapping("/")
    public String MeetUpServer() {
        return "스팸보다 리챔";
    }
}
