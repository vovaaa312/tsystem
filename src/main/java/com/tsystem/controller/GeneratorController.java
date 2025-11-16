package com.tsystem.controller;

import ch.qos.logback.core.joran.spi.HttpUtil;
import com.tsystem.service.GeneratorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/generator")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.POST})
public class GeneratorController {
    private final GeneratorService generatorService;

    @GetMapping("/generate")
    public ResponseEntity<?> generate(){

        return  ResponseEntity.ok().build();
    }

}
