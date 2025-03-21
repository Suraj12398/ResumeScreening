package com.app.resumeScreening.service;

import com.app.resumeScreening.exception.ResumeProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkillParserService {

    public Map<String, Map<String, Integer>> parseSkillSections(String skillList) {
        Map<String, Map<String, Integer>> categorizedSkills = new HashMap<>();

        if (skillList == null || skillList.trim().isEmpty()) {
            throw new ResumeProcessingException("Skill list cannot be empty.");
        }

        try {
            String[] sections = skillList.split("\\s*;\\s*");

            for (String section : sections) {
                String[] sectionParts = section.split("\\s*:\\s*", 2);
                if (sectionParts.length < 2) continue;

                String category = sectionParts[0].trim();
                String skillsPart = sectionParts[1];

                Map<String, Integer> skillMap = Arrays.stream(skillsPart.split("\\s*,\\s*"))
                        .map(skill -> skill.split("\\s*:\\s*"))
                        .filter(parts -> parts.length == 2)
                        .collect(Collectors.toMap(
                                parts -> parts[0].trim(),
                                parts -> Integer.parseInt(parts[1].trim()),
                                (oldValue, newValue) -> newValue
                        ));

                categorizedSkills.put(category, skillMap);
            }
        } catch (Exception e) {
            log.error("Error parsing skill list: {}", skillList, e);
            throw new ResumeProcessingException("Invalid skill list format.", e);
        }

        return categorizedSkills;
    }
}
