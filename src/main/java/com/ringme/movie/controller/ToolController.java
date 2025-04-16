package com.ringme.movie.controller;

import com.ringme.movie.dto.record.Response;
import com.ringme.movie.service.TestService;
import com.ringme.movie.service.ToolService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping("/tool")
public class ToolController {
    @Autowired
    TestService service;
    @Autowired
    ToolService toolService;

    @GetMapping("/add-movie-by-folder")
    ResponseEntity<?> addMovieByFolder(@RequestParam String folderPath,
                                       @RequestParam(required = false) Long episodeParent) {
        log.info("folderPath: {}, episodeParent: {}", folderPath, episodeParent);

        service.addMovieByFolder(folderPath, episodeParent);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/generate-subtitle-from-mkv-path")
    ResponseEntity<?> generateSubtitleFromMkvPath(@RequestParam String folderPath) {
        log.info("folderPath: {}", folderPath);

        service.generateSubtitleFromMkvPath(folderPath);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @PostMapping("/save-movie-by-excel-file")
    ResponseEntity<?> saveMovieByExcelFile(@RequestParam Long episodeParent,
                                           @RequestParam MultipartFile file) {
        log.info("episodeParent: {}", episodeParent);

        toolService.saveMovieByExcelFile(episodeParent, file);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }
}
