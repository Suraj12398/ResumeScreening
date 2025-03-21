package com.app.resumeScreening.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SkillExtractorService {

    /**
     * Calculates the total skill score based on the weighted skills found in the resume text.
     *
     * @param resumeText   The extracted text from the resume.
     * @param skillWeights A map of skills with their corresponding weights.
     * @return The total weighted skill score.
     */
    public int calculateSkillScore(String resumeText, Map<String, Integer> skillWeights) {
        int totalScore = 0;
        String lowerCaseText = resumeText.toLowerCase();

        for (Map.Entry<String, Integer> entry : skillWeights.entrySet()) {
            String skill = entry.getKey().toLowerCase();
            int weight = entry.getValue();

            if (lowerCaseText.contains(skill)) {
                totalScore += weight;
            }
        }
        return totalScore;
    }

    /**
     * Extracts and returns a set of matched skills found in the resume text.
     * @param resumeText   The extracted text from the resume.
     * @param skillWeights A map of skills with their corresponding weights.
     * @return A set of matched skills.
     */
    public Set<String> getMatchedSkills(String resumeText, Map<String, Integer> skillWeights) {
        Set<String> matchedSkills = new HashSet<>();
        String lowerCaseText = resumeText.toLowerCase();

        for (String skill : skillWeights.keySet()) {
            if (lowerCaseText.contains(skill.toLowerCase())) {
                matchedSkills.add(skill);
            }
        }
        return matchedSkills;
    }
}
