package com.ringme.movie.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@Service
@Log4j2
public class TestServiceImpl implements TestService {
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void callApiConvertFileInFolder(String folderPath) {

    }

    @Override
    public void standardFileName(String folderPath) {

    }

    public void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.info("exit code: {}, command: {}", exitCode, command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                    log.info(line);
            }
        } catch (Exception e) {
            log.error("ERROR|{}", e.getMessage(), e);
        }
    }

    private void renameFileName(String directoryPath) {
        log.info("directoryPath|{}", directoryPath);
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files == null) {
                log.warn("files is null");
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    String fileName = file.getName();
                    String newFileName = standardFileNameHandler(fileName);

                    String directFileName = directoryPath + "/" + fileName;
                    String directNewFileName = directoryPath + "/" + newFileName;
                    String cmd = "mv '{{fileName}}' '{{newFileName}}'"
                            .replace("{{fileName}}", directFileName)
                            .replace("{{newFileName}}", directNewFileName);
                    executeCommand(cmd);
                }
            }
        } else
            log.warn("Directory does not exist or is not a directory.");
    }

    private String standardFileNameHandler(String fileName) {
        return fileName.replace(" ", "-")
                .replace("[", "")
                .replace("]", "");
    }
}
