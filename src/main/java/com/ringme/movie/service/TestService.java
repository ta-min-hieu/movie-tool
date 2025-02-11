package com.ringme.movie.service;

import com.ringme.movie.model.VcsMedia;
import com.ringme.movie.model.VcsMediaSubtile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TestService {
    void movieExport(String folderPath, String filePath, String fileName);

    void storeMedia(MultipartFile excelFile);

    void getMoviePreview(HttpServletResponse response, String folderPath);

    void getMovieShort(HttpServletResponse response, String folderPath, int movieQuality);

    void addTextIntoFile(String filePath, String text);

    void executeCommand(String command);

    void generateSubtile();

    void subtitleHandler(VcsMedia media);

    void generateSubtileForCms(VcsMediaSubtile subtile, VcsMedia media);

    void standardPlaylistFile();

    void standardMediaTime();

    void generateSubtitleFromMkv();

    void filterErrorSubtitle();
}
