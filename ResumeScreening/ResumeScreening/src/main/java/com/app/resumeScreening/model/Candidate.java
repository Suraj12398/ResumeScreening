package com.app.resumeScreening.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class Candidate {
    private String fileName;
    private int mustKnowSkillMatchCount;
    private int betterToKnowSkillMatchCount;
    private Set<String> mustKnowMatchedSkills;
    private Set<String> betterToKnowMatchedSkills;

}
