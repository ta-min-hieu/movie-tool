package com.ringme.movie.service;

import com.ringme.movie.common.Helper;
import com.ringme.movie.config.AppConfig;
import com.ringme.movie.enums.MovieStatus;
import com.ringme.movie.model.VcsMedia;
import com.ringme.movie.model.VcsMediaSubtile;
import com.ringme.movie.repository.VcsMediaRepository;
import com.ringme.movie.repository.VcsMediaSubtileRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public final class ToolServiceImpl implements ToolService {
    private static final Map<String, String> LANGUAGE_MAP = Map.of(
            "en", "English",
            "my", "Myanmar"
    );

    @Autowired
    VcsMediaRepository mediaRepository;
    @Autowired
    VcsMediaSubtileRepository subtileRepository;

    @Autowired
    AppConfig appConfig;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void saveMovieByExcelFile(Long episodeParent, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            boolean skipHeader = true;
            for (Row row : sheet) {
                if(skipHeader) {
                    skipHeader = false;
                    continue;
                }
                log.info("---------------------------------------------");

                Map<String, String> subtitles = new HashMap<>();

                int count = 0;
                VcsMedia obj = new VcsMedia();
                obj.setEpisodeParent(episodeParent);
                obj.setActived(MovieStatus.WAITING_TO_CONVERT.getCode());
                obj.setCateId(1);
                obj.setProductionTime("2001-06-05");
                obj.setIsEpisode(2);

                for (Cell cell : row) {
                    String cellValue = Helper.getCellValue(cell);
                    if(count == 0 && (cellValue == null || cellValue.isEmpty()))
                        break;

                    handleSaveRow(obj, cellValue, count, subtitles);
                    count++;
                }

                if(obj.getEpisodeNumber() != null && obj.getMediaTitle() != null) {
                    log.info("media valid: {}", obj);
                    mediaRepository.save(obj);

                    for (Map.Entry<String, String> entry : subtitles.entrySet())
                        saveSubtitleHandler(obj, entry.getKey(), entry.getValue());

                    convert(obj, appConfig.getApiConvertCdn() + obj.getId());
                }
            }
        } catch (Exception e) {
            log.error("ERROR episodeParent: {}, {}", episodeParent, e.getMessage(), e);
        }
    }

    private void convert(VcsMedia media, String apiEndpoint) {
        try {
            log.info("id: {}", media.getId());
            updateStatus(media.getId(), MovieStatus.WAITING_TO_CONVERT.getCode());
            callApiToConvertHandler(media, apiEndpoint);
        } catch (Exception e) {
            log.error("media: {}, ERROR: {}", media, e.getMessage(), e);
        }
    }

    private void callApiToConvertHandler(VcsMedia dto, String apiEndpoint) {
        int movieId = dto.getId();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, null, String.class);

            // Kiểm tra kết quả gọi API
            if (response.getStatusCode() == HttpStatus.OK)
                log.info("Call api convert movie successful, id: {}, dto: {}", movieId, dto);
            else {
                log.error("Call api convert movie failed, id: {}, dto: {}", movieId, dto);
                updateStatus(movieId, MovieStatus.CONVERT_ERROR.getCode());
            }
        } catch (Exception e) {
            log.error("id: {}, dto: {}, ERROR: {}", movieId, dto, e.getMessage(), e);
            updateStatus(movieId, MovieStatus.CONVERT_ERROR.getCode());
        }
    }

    private void updateStatus(int id, int status) {
        try {
            Date approvedAt = null;
            if(status == MovieStatus.APPROVED.getCode())
                approvedAt = new Date();

            mediaRepository.updateStatus(id, status, approvedAt);
        } catch (Exception e) {
            log.error("ERROR|" + e.getMessage(), e);
        }
    }

    private void saveSubtitleHandler(VcsMedia media, String lang, String filePath) {
        try {
            VcsMediaSubtile s = new VcsMediaSubtile();
            s.setMediaId(media.getId());
            s.setName(LANGUAGE_MAP.get(lang));
            s.setLanguage(lang);
            s.setSubtilePath(filePath);

            subtileRepository.save(s);
            log.info("success {} -> {}", lang, filePath);
        } catch (Exception e) {
            log.error("failed:  {} -> {}, {}", lang, filePath, e.getMessage(), e);
        }
    }

    private void handleSaveRow(VcsMedia obj, String cellValue, int count, Map<String, String> subtitles) {
        try {
            log.info(cellValue);

            if(cellValue != null && !cellValue.isEmpty())
                switch (count) {
                    case 0 -> obj.setEpisodeNumber(Integer.valueOf(cellValue.replace(".0", "")));
                    case 1 -> obj.setMediaTitle(cellValue);
                    case 2 -> obj.setMediaDesc(cellValue);
                    case 3 -> obj.setMediaImage(cellValue);
                    case 4 -> obj.setMediaPath(cellValue);
                    case 5 -> subtitles.put("en", cellValue);
                    case 6 -> subtitles.put("my", cellValue);
                }
        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }
    }
}
