package com.ringme.movie.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Helper {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String space = "(-|-)";

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static Set<Integer> getListCategoryTypeIdHandler(String filmName) {
        String cateStr = validateFilmName(filmName);
        if(cateStr == null || cateStr.isEmpty())
            return null;

        Set<Integer> set = new HashSet<>();

        if (cateStr.contains("-"))
            set.addAll(getListCateTypeId(cateStr, "-"));
        else if (cateStr.contains(","))
            set.addAll(getListCateTypeId(cateStr, ","));
        else {
            Integer categoryType = getCategoryTypeId(cateStr);
            if(categoryType != null)
                set.add(getCategoryTypeId(cateStr));
        }
        return set;
    }

    private static Set<Integer> getListCateTypeId(String cateStr, String pattern) {
        Set<Integer> rs = new HashSet<>();
        String[] parts = cateStr.split(pattern);

        for (String part : parts) {
            Integer categoryType = getCategoryTypeId(part);
            if(categoryType != null)
                rs.add(getCategoryTypeId(part));
        }

        return rs;
    }

    private static Integer getCategoryTypeId(String category) {
        Integer rs;
        switch (category.trim().toLowerCase()) {
            case "tâm lý", "tâm ly" -> rs = GlobalKeys.PSYCHOLOGICAL_FILM;
            case "hành động" -> rs = GlobalKeys.ACTION_FILM;
            case "phiêu lưu", "phưu lưu" -> rs = GlobalKeys.ADVENTURE_FILM;
            case "viễn tưởng" -> rs = GlobalKeys.FANTASY_FILM;
            case "hình sự", "tội phạm" -> rs = GlobalKeys.CRIME_FILM;
            case "gây cấn" -> rs = GlobalKeys.THRILLING_FILM;
            case "hài hước", "hài" -> rs = GlobalKeys.COMEDY_FILM;
            case "hồi hộp" -> rs = GlobalKeys.SUSPENSE_FILM;
            case "lãng mạn", "tình cảm" -> rs = GlobalKeys.ROMANTIC_FILM;
            case "chiến tranh" -> rs = GlobalKeys.WAR_FILM;
            case "âm nhạc", "ca nhạc" -> rs = GlobalKeys.MUSIC_FILM;
            case "gia đình" -> rs = GlobalKeys.FAMILY_FILM;
            case "bí ẩn" -> rs = GlobalKeys.MYSTERY_FILM;
            case "tiểu sử" -> rs = GlobalKeys.BIOGRAPHICAL_FILM;
            case "lịch sử" -> rs = GlobalKeys.HISTORICAL_FILM;
            case "tài liệu" -> rs = GlobalKeys.DOCUMENTARY_FILM;
            case "chính kịch" -> rs = GlobalKeys.DRAMA_FILM;
            case "khoa học" -> rs = GlobalKeys.SCIENCE_FILM;
            case "hoạt hình" -> rs = GlobalKeys.CARTOON_FILM;
            case "kinh dị" -> rs = GlobalKeys.HORROR_FILM;
            case "anime" -> rs = GlobalKeys.ANIME;
            case "rùng rợn" -> rs = GlobalKeys.SCARY_FILM;
            default -> rs = null;
        }

        return rs;
    }

    private static String validateFilmName(String filmName) {
        if(filmName == null || filmName.isEmpty()) {
            log.warn("file name is empty");
            return null;
        }

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(filmName);

        if(!matcher.find()) {
            log.warn("matcher is empty");
            return null;
        }

        String cateStr = matcher.group(1);
        if(cateStr == null || cateStr.isEmpty()) {
            log.warn("cateStr is empty");
            return null;
        }

        log.info("cateStr|{}", cateStr);
        return cateStr;
    }

    public static Set<Integer> convertStringToSetInt(String str) {
        String[] parts = str.replaceAll("\\[|\\]|\\s", "").split(",");

        Set<Integer> resultSet = new HashSet<>();
        for (String part : parts) {
            if(!part.isEmpty())
                resultSet.add(Integer.parseInt(part));
        }

        return resultSet;
    }

    public static String getCellValue(Cell cell) {
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
}
