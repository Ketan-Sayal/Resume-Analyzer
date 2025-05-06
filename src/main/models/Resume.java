package src.main.models;

public class Resume {
    private String fileName;
    private String content;
    private int skillScore;
    private int experienceScore;
    private int jobMatchScore;
    
    public Resume(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.skillScore = 0;
        this.experienceScore = 0;
        this.jobMatchScore = 0;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getContent() {
        return content;
    }
    
    public int getSkillScore() {
        return skillScore;
    }
    
    public void setSkillScore(int skillScore) {
        this.skillScore = skillScore;
    }
    
    public int getExperienceScore() {
        return experienceScore;
    }
    
    public void setExperienceScore(int experienceScore) {
        this.experienceScore = experienceScore;
    }
    
    public int getTotalScore() {
        return skillScore + experienceScore;
    }
    
    public int getJobMatchScore() {
        return jobMatchScore;
    }
    
    public void setJobMatchScore(int jobMatchScore) {
        this.jobMatchScore = jobMatchScore;
    }
}