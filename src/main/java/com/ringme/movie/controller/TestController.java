package com.ringme.movie.controller;

import com.ringme.movie.dto.record.Response;
import com.ringme.movie.model.VcsMedia;
import com.ringme.movie.model.VcsMediaSubtile;
import com.ringme.movie.repository.VcsMediaRepository;
import com.ringme.movie.repository.VcsMediaSubtileRepository;
import com.ringme.movie.service.TestService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    TestService service;
    @Autowired
    VcsMediaRepository mediaRepository;
    @Autowired
    VcsMediaSubtileRepository subtileRepository;

    @GetMapping("/movie-export")
    ResponseEntity<?> movieExport(@RequestParam String folderPath,
                                  @RequestParam String filePath,
                                  @RequestParam(required = false) String fileName) {
        log.info("folderPath|{}|filePath|{}|fileName|{}", folderPath, filePath, fileName);

        service.movieExport(folderPath, filePath, fileName);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/store-media")
    ResponseEntity<?> storeMedia(@RequestParam MultipartFile excelFile) {
        log.info("");

        service.storeMedia(excelFile);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    //todo dùng mediaId để lấy ra folder path để không phải url encode
    @GetMapping("/get-movie-preview")
    ResponseEntity<?> getMoviePreview(@RequestParam String folderPath,
                                      HttpServletResponse response) {
        log.info("folderPath|{}", folderPath);

        service.getMoviePreview(response, folderPath);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/get-movie-short/{movieQuality}")
    ResponseEntity<?> getMovieShort(@PathVariable int movieQuality,
                                    @RequestParam String folderPath,
                                    HttpServletResponse response) {
        log.info("movieQuality|{}|folderPath|{}", movieQuality, folderPath);

        service.getMovieShort(response, folderPath, movieQuality);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/add-text-into-file")
    ResponseEntity<?> addTextIntoFile(@RequestParam String filePath,
                                      @RequestParam String text) {
        log.info("folderPath|{}|text|{}", filePath, text);

        service.addTextIntoFile(filePath, text);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/run-command")
    ResponseEntity<?> runCommand(@RequestParam String command) {
        log.info("command|{}", command);

        service.executeCommand(command);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/generate-subtile-by-media-id")
    ResponseEntity<?> generateSubtileByMediaId(@RequestParam int mediaId) {
        log.info("mediaId|{}", mediaId);
        VcsMedia media = mediaRepository.findById(mediaId).get();
        log.info("media|{}", media);
        service.subtitleHandler(media);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/generate-subtile")
    ResponseEntity<?> generateSubtile() {
        log.info("");
        service.generateSubtile();

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/generate-subtitle-from-mkv")
    ResponseEntity<?> generatePlaylistPreview() {
        log.info("");
        service.generateSubtitleFromMkv();

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/filter-error-subtitle")
    ResponseEntity<?> filterErrorSubtitle() {
        log.info("");
        service.filterErrorSubtitle();

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/standard-media-time")
    ResponseEntity<?> standardMediaTime() {
        log.info("");
        service.standardMediaTime();

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/generate-subtile-for-cms")
    public ResponseEntity<?> generateSubtileForCms(@RequestParam int mediaSubtileId) {
        log.info("mediaSubtileId|{}", mediaSubtileId);

        VcsMediaSubtile subtile = subtileRepository.findById(mediaSubtileId).get();
        log.info("subtile|{}", subtile);

        VcsMedia media = mediaRepository.findById(subtile.getMediaId()).get();
        log.info("media|{}", media);

        service.generateSubtileForCms(subtile, media);

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/standard-playlist-file")
    ResponseEntity<?> standardPlaylistFile() {
        log.info("vào đây");

        service.standardPlaylistFile();

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }

    @GetMapping("/standard-image-file")
    ResponseEntity<?> standardImageFile() {
        log.info("vào đây");

        service.standardImageFile();

        return ResponseEntity.ok().body(new Response(200, "Success"));
    }
}
