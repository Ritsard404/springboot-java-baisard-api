package com.ritsard.baisard.test.controller;

import com.ritsard.baisard.utils.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping()
    public ApiResponse<?> test() {
        return ApiResponse.ok("Hello from Test Controller!");
    }
}
