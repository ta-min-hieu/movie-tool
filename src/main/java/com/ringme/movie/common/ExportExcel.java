package com.ringme.movie.common;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;

@Log4j2
@Component
public class ExportExcel {

    public void export(List<?> objects, String[] headers, String filePath, String fileName) {
        try {
            long startTime = System.currentTimeMillis();
            writeExcel(objects, headers, filePath, fileName);
            long endTime = System.currentTimeMillis();
            long timeExport = endTime - startTime;
            log.info("Time Export|" + timeExport);
        } catch (Exception e) {
            log.error("ERROR|" + e.getMessage(), e);
        }
    }

    private static void writeExcel(List<?> objects, String[] headers, String filePath, String fileName) {
        try {
            // Create Workbook
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            // Create sheet
            SXSSFSheet sheet = workbook.createSheet("ReportSDK"); // Create sheet with sheet name
            int rowIndex = 0;
            // Write header
            writeHeader(sheet, rowIndex, headers);
            // Write data
            rowIndex++;
            for (Object object : objects) {
                // Create row
                SXSSFRow row = sheet.createRow(rowIndex);
                // Write data on row
                writeObject(object, row);
                rowIndex++;
            }

            for (int i=0; i<headers.length; i++) {
                sheet.trackAllColumnsForAutoSizing();
                sheet.autoSizeColumn(i);
            }

            // Create file excel
            createOutputFile(workbook, filePath, fileName);
            log.info("Done!!!");
        } catch (Exception e) {
            log.error("ERROR|" + e.getMessage(), e);
        }
    }
    // Write header with format
    private static void writeHeader(SXSSFSheet sheet, int rowIndex, String[] headers) {
        // Create row
        SXSSFRow row = sheet.createRow(rowIndex);
        // Create cells
        SXSSFCell cell;
        for (int i = 0; i < headers.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }
    // Write data
    private static void writeObject(Object object, SXSSFRow row) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(object);

                SXSSFCell cell = row.createCell(i);
                cell.setCellValue(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    // Create output file
    private static void createOutputFile(SXSSFWorkbook workbook, HttpServletResponse response) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();

            outputStream.close();
        } catch (Exception e) {
            log.error("ERROR|" + e.getMessage(), e);
        }
    }

    private static void createOutputFile(SXSSFWorkbook workbook, String filePath, String fileName) {
        if (!filePath.endsWith("/") && !filePath.endsWith("\\")) {
            filePath += File.separator;
        }

        String fullFilePath = filePath + fileName + ".xlsx";
        log.info("full file path|{}", fullFilePath);

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullFilePath)) {
            workbook.write(fileOutputStream); // Ghi nội dung workbook ra file
            workbook.close(); // Đóng workbook
        } catch (Exception e) {
            log.error("ERROR|" + e.getMessage(), e);
        }
    }
}