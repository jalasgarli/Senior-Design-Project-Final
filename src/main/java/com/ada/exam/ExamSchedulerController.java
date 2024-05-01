package com.ada.exam;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class ExamSchedulerController {

    @PostMapping("/schedule-exam")
    public String scheduleExam(@RequestParam("roomFile") MultipartFile roomFile,
                               @RequestParam("studentFile") MultipartFile studentFile,
                                @RequestParam("percent") double percent) {
        // Logic to handle file uploads and process the algorithm

        List<String> resultList = AllocateRoomInGivenTimeAndDay.result(roomFile, studentFile, percent);
        // Write resultList to output.csv file
        AllocateRoomInGivenTimeAndDay.finalResult(resultList);
        return "Exam scheduled successfully!";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        // Path to the directory where your file is stored
        String filePath = "output.csv";

        Path path = Paths.get(filePath);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        // Build the ResponseEntity to return the file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(path))
                .body(resource);
    }
}

