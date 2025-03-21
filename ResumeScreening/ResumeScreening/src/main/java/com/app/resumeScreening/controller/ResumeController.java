package com.app.resumeScreening.controller;

import com.app.resumeScreening.model.Candidate;
import com.app.resumeScreening.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/resume")
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<List<Candidate>> uploadMultipleResumes(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("skills") String skillList
    ) {
        List<Candidate> candidates = resumeService.processResumes(files, skillList);
        return ResponseEntity.ok(candidates);
    }
}
