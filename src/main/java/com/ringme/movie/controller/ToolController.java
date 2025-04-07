package com.ringme.movie.controller;

import com.ringme.movie.dto.record.Response;
import com.ringme.movie.service.TestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/tool")
public class ToolController {
    @Autowired
    TestService service;

    @GetMapping("/standard-file-name")
    ResponseEntity<?> standardFileName(@RequestParam String folderPath) {
        log.info("folderPath: {}", folderPath);

        service.standardFileName(folderPath);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/call-api-convert-in-folder")
    ResponseEntity<?> callApiConvertInFolder(@RequestParam String folderPath) {
        log.info("folderPath: {}", folderPath);

        service.callApiConvertFileInFolder(folderPath);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }
}
