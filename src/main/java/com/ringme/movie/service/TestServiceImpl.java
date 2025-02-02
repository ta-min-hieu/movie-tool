package com.ringme.movie.service;

import com.ringme.movie.common.ExportExcel;
import com.ringme.movie.common.Helper;
import com.ringme.movie.config.AppConfig;
import com.ringme.movie.dto.Movie;
import com.ringme.movie.model.VcsMedia;
import com.ringme.movie.model.VcsMediaCategoryType;
import com.ringme.movie.model.VcsMediaSubtile;
import com.ringme.movie.repository.VcsMediaCategoryTypeRepository;
import com.ringme.movie.repository.VcsMediaRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
@Transactional
public class TestServiceImpl implements TestService {
    @Autowired
    ExportExcel export;
    @Autowired
    VcsMediaCategoryTypeRepository mediaCategoryTypeRepository;
    @Autowired
    VcsMediaRepository mediaRepository;
    @Autowired
    AppConfig appConfig;

    @Override
    public void movieExport(String folderPath, String filePath, String fileName) {
        if(fileName == null || fileName.isEmpty())
            fileName = Helper.generateRandomString(32);

        String[] headers = {"Tên film", "Categories", "jpg", "mkv", "srt", "other"};
        List<Movie> list = listFilesAndFolders(folderPath);
        log.info("list|{}", list);

        export.export(list, headers, filePath, fileName);
    }

    @Override
//    @Transactional(readOnly = true)
    public void storeMedia(MultipartFile excelFile) {
        try (InputStream inputStream = excelFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            boolean skipHeader = true;
            for (Row row : sheet) {
                if(skipHeader) {
                    skipHeader = false;
                    continue;
                }
                log.info("---------------------------------------------");

                VcsMedia media = new VcsMedia();
                List<VcsMediaCategoryType> mediaCategoryTypes = new ArrayList<>();

                media.setActived(3);
                media.setIsEpisode(0);
                media.setBelong("movie tool");
                media.setCateId(1);

                int count = 0;
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    log.info(cellValue);

                    switch (count) {
                        case 0 -> {
                            if(cellValue.isEmpty())
                                continue;

                            media.setMediaTitle(cellValue);
                            media.setMediaDesc(cellValue);
                        }
                        case 1 -> {
                            if(!cellValue.isEmpty()) {
                                Set<Integer> set = Helper.convertStringToSetInt(cellValue);
                                log.info("Set|{}", set);
                                for(Integer s : set) {
                                    if(s == null)
                                        continue;

                                    VcsMediaCategoryType categoryType = new VcsMediaCategoryType();
                                    categoryType.setCateTypeId(s);
                                    mediaCategoryTypes.add(categoryType);
                                }
                            }
                        }
                        case 2 -> media.setMediaImage(cellValue);
                        case 3 -> media.setMediaPath(cellValue);
                        case 4 -> media.setSubtilePath(cellValue);
                    }

                    count++;
                }

                if(media.getMediaTitle() == null)
                    continue;

                mediaRepository.save(media);

                for(VcsMediaCategoryType mediaCategoryType : mediaCategoryTypes) {
                    if(media.getId() == null)
                        continue;

                    mediaCategoryType.setMediaId(media.getId());
                    mediaCategoryTypeRepository.save(mediaCategoryType);
                }

                log.info("Media|{}", media);
            }

        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }
    }

    @Override
    public void getMoviePreview(HttpServletResponse response, String folderPath) {
        String baseUrl = appConfig.getBaseUrl();

        String m3u8Content = """
                    #EXTM3U
                    #EXT-X-VERSION:4
                    #EXT-X-MEDIA:TYPE=SUBTITLES,GROUP-ID="subs",NAME="English",DEFAULT=YES,AUTOSELECT=YES,FORCED=NO,LANGUAGE="en",URI="https://cdn-my.lumitel.bi{{folderPath}}"
                    #EXT-X-STREAM-INF:BANDWIDTH=700000,AVERAGE-BANDWIDTH=600000,RESOLUTION=854x480,CLOSED-CAPTIONS=NONE,CODECS="avc1.4d001e,mp4a.40.2",SUBTITLES="subs",VIDEO-RANGE=SDR
                    {{baseUrl}}/test/get-movie-short/480?folderPath={{folderPath}}
                    #EXT-X-STREAM-INF:BANDWIDTH=1200000,AVERAGE-BANDWIDTH=1000000,RESOLUTION=1280x720,CLOSED-CAPTIONS=NONE,CODECS="avc1.4d001e,mp4a.40.2",SUBTITLES="subs",VIDEO-RANGE=SDR
                    {{baseUrl}}/test/get-movie-short/720?folderPath={{folderPath}}
                    #EXT-X-STREAM-INF:BANDWIDTH=200000,AVERAGE-BANDWIDTH=150000,RESOLUTION=426x240,CLOSED-CAPTIONS=NONE,CODECS="avc1.4d001e,mp4a.40.2",SUBTITLES="subs",VIDEO-RANGE=SDR
                    {{baseUrl}}/test/get-movie-short/240?folderPath={{folderPath}}
                    #EXT-X-STREAM-INF:BANDWIDTH=550000,AVERAGE-BANDWIDTH=450000,RESOLUTION=640x360,CLOSED-CAPTIONS=NONE,CODECS="avc1.4d001e,mp4a.40.2",SUBTITLES="subs",VIDEO-RANGE=SDR
                    {{baseUrl}}/test/get-movie-short/360?folderPath={{folderPath}}
                    """
                .replace("{{baseUrl}}", baseUrl).replace("{{folderPath}}", folderPath);

        createM3U8File(response, folderPath, m3u8Content);
    }

    @Override
    public void getMovieShort(HttpServletResponse response, String folderPath, int movieQuality) {
        String movieQualityPath = folderPath + "/" + movieQuality + "/playlist_" + movieQuality + ".m3u8";
        log.info("movieQualityPath|{}", movieQualityPath);

        int lineCount = 0;
        StringBuilder first62Lines = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(movieQualityPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;

                // Lấy 62 dòng đầu tiên
                if (lineCount <= 61) {
                    first62Lines.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }

        if(lineCount < 61)
            createM3U8File(response, folderPath, first62Lines.toString());
        else
            createM3U8File(response, folderPath, first62Lines.append("#EXT-X-ENDLIST").toString());
    }

    @Override
    public void addTextIntoFile(String filePath, String text) {
        try {
            String connectSub = ",SUBTITLES=\"subs\",VIDEO-RANGE=SDR";

            List<String> lines = Files.readAllLines(Paths.get(filePath));
            boolean lineExists = lines.stream().anyMatch(line -> line.contains(text));
            if (!lineExists) {
                for (int i = 0; i < lines.size(); i++) {
                    if(lines.get(i).equalsIgnoreCase(appConfig.getEditSubProfile240())) {
                        log.info("vao day 240");
                        lines.set(i, appConfig.getEditSubProfile240() + connectSub);
                    } else if(lines.get(i).equalsIgnoreCase(appConfig.getEditSubProfile360())) {
                        log.info("vao day 360");
                        lines.set(i, appConfig.getEditSubProfile360() + connectSub);
                    } else if(lines.get(i).equalsIgnoreCase(appConfig.getEditSubProfile480())) {
                        log.info("vao day 480");
                        lines.set(i, appConfig.getEditSubProfile480() + connectSub);
                    } else if(lines.get(i).equalsIgnoreCase(appConfig.getEditSubProfile720())) {
                        log.info("vao day 720");
                        lines.set(i, appConfig.getEditSubProfile720() + connectSub);
                    }
                }

                lines.add(2, text);

                Files.write(Paths.get(filePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

                log.info("Dòng đã được thêm thành công!");
            } else {
                log.info("Dòng đã tồn tại, không thêm nữa.");
            }
        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }
    }

    @Override
    public void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "bash", "-c", command
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                    log.info(line);
            }
        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }
    }

    @Override
    public void generateSubtile() {
        List<VcsMedia> list = mediaRepository.getVcsMediaConvertDone();

        for (VcsMedia media : list)
            subtitleHandler(media);
    }

    @Override
    public void subtitleHandler(VcsMedia media) {
        if(media == null) {
            log.warn("media is null");
            return;
        }

        String mediaPath = media.getMediaPath();
        String subtilePath = media.getSubtilePath();

        if(mediaPath == null || mediaPath.isEmpty()) {
            log.warn("mediaPath is null or empty");
            return;
        }

        if(subtilePath == null || subtilePath.isEmpty()) {
            log.warn("subtilePath is null or empty");
            return;
        }

        String folderPath = getFolderPath(mediaPath);
        String subtitlePath = "/media" + subtilePath;
        String fileM3u8Path = "/media01" + mediaPath;

        generateSubtitleHandler(folderPath, subtitlePath, fileM3u8Path, "en");

        mediaRepository.updateStatusSub(media.getId());
    }

    @Override
    public void generateSubtileForCms(VcsMediaSubtile subtile, VcsMedia media) {
        if(subtile == null) {
            log.warn("subtile is null");
            return;
        }

        if(media == null) {
            log.warn("media is null");
            return;
        }

        String mediaPath = media.getMediaPath();
        String subtilePath = subtile.getSubtilePath();

        if(mediaPath == null || mediaPath.isEmpty()) {
            log.warn("mediaPath is null or empty");
            return;
        }

        if(subtilePath == null || subtilePath.isEmpty()) {
            log.warn("subtilePath is null or empty");
            return;
        }

        String folderPath = getFolderPath(mediaPath);
        String fileM3u8Path = "/media01" + mediaPath;

        generateSubtitleHandler(folderPath, subtilePath, fileM3u8Path, subtile.getLanguage());
    }

    private void generateSubtitleHandler(String folderPath, String subtitlePath, String fileM3u8Path, String language) {
        try {
            log.info("folderPath|{}|subtitlePath|{}|fileM3u8Path|{}", folderPath, subtitlePath, fileM3u8Path);

            String subs = getFileNameSubs(language);
            String cmdStr = getCmdStr(subtitlePath);

            if(subs == null || subs.isEmpty()) {
                log.error("subs is null or empty");
                return;
            }

            if(cmdStr == null || cmdStr.isEmpty()) {
                log.error("cmdStr is null or empty");
                return;
            }

            String cmd = cmdStr.replace("{{folderPath}}", folderPath + "/" + subs)
                    .replace("{{srtPath}}", subtitlePath);
            log.info("cmd|{}", cmd);

            executeCommand(cmd);

            String text = addSubCmdInM3u8Handler(language);
            if(text == null || text.isEmpty()) {
                log.error("text is null or empty");
                return;
            }

            addTextIntoFile(fileM3u8Path, text);

            log.info("success");
        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }
    }

    private String addSubCmdInM3u8Handler(String language) {
        if(language == null || language.isEmpty())
            return null;

        String defaultStr;
        String name;
        String subs;

        switch (language) {
            case "en" -> {
                defaultStr = "YES";
                name = "English";
                subs = "sub/en";
            }
            case "fr" -> {
                defaultStr = "NO";
                name = "French";
                subs = "sub/fr";
            }
            case "rn" -> {
                defaultStr = "NO";
                name = "Burundi";
                subs = "sub/rn";
            }
            default -> {
                defaultStr = null;
                name = null;
                subs = null;
            }
        }

        if(defaultStr == null || name == null || subs == null) {
            log.error("language is not supported|{}", language);
            return null;
        }

        return appConfig.getAddSubCmdInM3u8().replace("{{language}}", language)
                .replace("{{name}}", name)
                .replace("{{default}}", defaultStr)
                .replace("{{subs}}", subs);
    }

    private String getCmdStr(String subtitlePath) {
        String extension = getFileExtensionBySubtilePath(subtitlePath);
        if(extension == null || extension.isEmpty()) {
            log.warn("extension is null or empty");
            return null;
        }

        if(extension.equalsIgnoreCase("srt"))
            return appConfig.getGenerateSubCmdSrt();
        else if(extension.equalsIgnoreCase("vtt"))
            return appConfig.getGenerateSubCmdVtt();
        else {
            log.error("file extension is not supported|{}", extension);
            return null;
        }
    }

    private String getFileNameSubs(String language) {
        if(language == null || language.isEmpty())
            return null;
        else if(language.equalsIgnoreCase("en"))
            return "sub/en";
        else if(language.equalsIgnoreCase("fr"))
            return "sub/fr";
        else if(language.equalsIgnoreCase("rn"))
            return "sub/rn";
        else {
            log.error("language is not supported|{}", language);
            return null;
        }
    }

    private String getFolderPath(String mediaPath) {
        if(mediaPath == null || mediaPath.isEmpty()) {
            log.warn("mediaPath is null or empty");
            return null;
        }

        int lastSlashIndex = mediaPath.lastIndexOf('/');

        if (lastSlashIndex == -1) {
            log.warn("No '/' found in the string.");
            return null;
        }

        return "/media01" + mediaPath.substring(0, lastSlashIndex);
    }

    private String getFileExtensionBySubtilePath(String subtilePath) {
        int lastDotIndex = subtilePath.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == subtilePath.length() - 1) {
            log.warn("No file extension found in the string.");
            return null;
        }

        return subtilePath.substring(lastDotIndex + 1);
    }

    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private List<Movie> listFilesAndFolders(String directoryPath) {
        List<Movie> movies = new ArrayList<>();

        log.info("directoryPath|{}", directoryPath);
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files == null) {
                log.warn("files is null");
                return null;
            }

            for (File file : files) {
                Movie movie = new Movie();
                if (file.isDirectory()) {
                    String filmName = file.getName();
                    movie.setFilmName(filmName);
                    movie.setCategories(Helper.getListCategoryTypeIdHandler(filmName));
                    directoryHandler(movie, directoryPath + "/" + file.getName());
                } else if (file.isFile())
                    log.warn("is file");

                movies.add(movie);
            }
        } else
            log.warn("Directory does not exist or is not a directory.");

        return movies;
    }

    private void directoryHandler(Movie movie, String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files == null) {
                log.warn("files2 is null");
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    movie.setFilmName(file.getName());
                    directoryHandler(movie, directoryPath + "/" + file.getName());
                } else if (file.isFile())
                    movieHandler(movie, file, directoryPath);
            }
        } else
            log.warn("Directory does not exist or is not a directory.");
    }

    private void movieHandler(Movie movie, File file, String directoryPath) {
        String baseUrl = directoryPath.substring(6) + "/";
        String name = file.getName();
        String extension = getFileExtension(file);

        switch (extension) {
            case "jpg", "JPG", "png" -> movie.setJpg(baseUrl + name);
            case "mkv" -> movie.setMkv(baseUrl + name);
            case "srt" -> movie.setSrt(baseUrl + name);
            default -> movie.setOther(name);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < name.length() - 1) {
            return name.substring(lastIndex + 1);
        }
        return "";
    }

    private void createM3U8File(HttpServletResponse response, String folderPath, String m3u8Content) {
        try {
            response.setContentType("application/vnd.apple.mpegurl");
            response.setHeader("Content-Disposition", "attachment; filename=\"playlist.m3u8\"");

            ServletOutputStream outputStream = response.getOutputStream();

            outputStream.write(m3u8Content.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("ERROR|" + e.getMessage(), e);
        }
    }
}
