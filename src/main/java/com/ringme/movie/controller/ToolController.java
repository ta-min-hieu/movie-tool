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

    @GetMapping("/add-movie-by-folder")
    ResponseEntity<?> addMovieByFolder(@RequestParam String folderPath,
                                       @RequestParam(required = false) Long episodeParent) {
        log.info("folderPath: {}, episodeParent: {}", folderPath, episodeParent);

        service.addMovieByFolder(folderPath, episodeParent);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }
}
