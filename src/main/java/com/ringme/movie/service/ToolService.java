package com.ringme.movie.service;

import org.springframework.web.multipart.MultipartFile;

public sealed interface ToolService permits ToolServiceImpl {
    void saveMovieByExcelFile(Long episodeParent, MultipartFile file);

    void subtitleNotMatchHandler(int movieId, int timeNotMatch);
}
