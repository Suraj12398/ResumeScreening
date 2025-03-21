package com.app.resumeScreening.service;

import com.app.resumeScreening.exception.ResumeProcessingException;
import com.app.resumeScreening.model.Candidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class ResumeService {

    private final Tika tika = new Tika();
    private final SkillExtractorService skillExtractorService;
    private final SkillParserService skillParserService;

    public ResumeService(SkillExtractorService skillExtractorService,
                         SkillParserService skillParserService
    ) {
        this.skillExtractorService = skillExtractorService;
        this.skillParserService = skillParserService;
    }

    public List<Candidate> processResumes(MultipartFile[] files, String skillList) {
        if (files == null || files.length == 0) {
            throw new ResumeProcessingException("No files provided for processing.");
        }

        Map<String, Map<String, Integer>> categorizedSkills = skillParserService.parseSkillSections(skillList);
        List<Candidate> candidates = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    log.warn("Skipping empty file: {}", file.getOriginalFilename());
                    continue;
                }

                String extractedText = tika.parseToString(file.getInputStream());

                // Extract Skills
                Map<String, Integer> mustKnowSkills = categorizedSkills.getOrDefault("Must Know", new HashMap<>());
                Map<String, Integer> betterToKnowSkills = categorizedSkills.getOrDefault("Better to Know", new HashMap<>());

                int mustKnowScore = skillExtractorService.calculateSkillScore(extractedText, mustKnowSkills);
                int betterToKnowScore = skillExtractorService.calculateSkillScore(extractedText, betterToKnowSkills);

                Set<String> mustKnowMatched = skillExtractorService.getMatchedSkills(extractedText, mustKnowSkills);
                Set<String> betterToKnowMatched = skillExtractorService.getMatchedSkills(extractedText, betterToKnowSkills);


                candidates.add(new Candidate(
                        file.getOriginalFilename(), mustKnowScore, betterToKnowScore,
                        mustKnowMatched, betterToKnowMatched
                ));

            } catch (IOException | TikaException e) {
                log.error("Error processing file: {}", file.getOriginalFilename(), e);
                throw new ResumeProcessingException("Error processing resume: " + file.getOriginalFilename(), e);
            }
        }

        return candidates;
    }
}
