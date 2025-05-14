package src.main.services;

import src.main.models.Resume;
import src.main.models.Job;
import src.main.utils.FileUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ResumeService {
    private static final String RESUMES_DIRECTORY = "../data/resumes";
    private static final String JOBS_DIRECTORY = "../data/jobs";
    
    private static final List<String> skillKeywords = new ArrayList<>(Arrays.asList(
         "java", "python", "sql", "oop", "dsa", "spring", "react"
    ));
     
    private static final List<String> experienceKeywords = new ArrayList<>(Arrays.asList(
         "experience", "years", "intern", "developer", "engineer", "worked", "project", "company"
    ));
    
    public ResumeService() {
        // Create resumes directory if it doesn't exist
        File resumeDirectory = new File(RESUMES_DIRECTORY);
        File jobDirectory = new File(JOBS_DIRECTORY);
        
        if (!resumeDirectory.exists()) {
            resumeDirectory.mkdirs();
        }
        
        if (!jobDirectory.exists()) {
            jobDirectory.mkdirs();
        }
    }
    
    
    public Map<String, Integer> analyzeResumes() {
        List<Resume> resumes = loadResumes();
        
        
        for (Resume resume : resumes) {
            scoreResume(resume);
        }
        
        
        return resumes.stream()
                .sorted(Comparator.comparing(Resume::getTotalScore).reversed())
                .collect(Collectors.toMap(
                    Resume::getFileName,
                    Resume::getTotalScore,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    
    public Map<String, Integer> analyzeResumesForJob(String jobFileName) {
        List<Resume> resumes = loadResumes();
        Job job = loadJob(jobFileName);
        
        if (job == null) {
            System.err.println("Job file not found: " + jobFileName);
            return new LinkedHashMap<>();
        }
        
        
        Set<String> jobKeywords = extractKeywords(job.getDescription());
        
        
        for (Resume resume : resumes) {
            scoreResumeForJob(resume, jobKeywords);
        }
        
        
        return resumes.stream()
                .sorted(Comparator.comparing(Resume::getJobMatchScore).reversed())
                .collect(Collectors.toMap(
                    Resume::getFileName,
                    Resume::getJobMatchScore,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    
    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        
        for (String skill : skillKeywords) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                keywords.add(skill.toLowerCase());
            }
        }
        
        
        for (String exp : experienceKeywords) {
            if (text.toLowerCase().contains(exp.toLowerCase())) {
                keywords.add(exp.toLowerCase());
            }
        }
        
       
        
        return keywords;
    }
    
   
    private List<Resume> loadResumes() {
        List<Resume> resumes = new ArrayList<>();
        File resumesDir = new File(RESUMES_DIRECTORY);
        
        if (resumesDir.exists() && resumesDir.isDirectory()) {
            File[] resumeFiles = resumesDir.listFiles((dir, name) -> name.endsWith(".txt"));
            
            if (resumeFiles != null) {
                for (File file : resumeFiles) {
                    try {
                        String content = FileUtils.readFileContent(file);
                        resumes.add(new Resume(file.getName(), content.toLowerCase()));
                    } catch (Exception e) {
                        System.err.println("Error reading resume " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
        
        return resumes;
    }
    
    
    private Job loadJob(String jobFileName) {
        File jobFile = new File(JOBS_DIRECTORY + File.separator + jobFileName);
        
        if (jobFile.exists() && jobFile.isFile()) {
            try {
                String content = FileUtils.readFileContent(jobFile);
                return new Job(jobFile.getName(), content.toLowerCase());
            } catch (Exception e) {
                System.err.println("Error reading job " + jobFile.getName() + ": " + e.getMessage());
            }
        }
        
        return null;
    }
    
    
    public List<String> listJobs() {
        List<String> jobFiles = new ArrayList<>();
        File jobsDir = new File(JOBS_DIRECTORY);
        
        if (jobsDir.exists() && jobsDir.isDirectory()) {
            File[] files = jobsDir.listFiles((dir, name) -> name.endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    jobFiles.add(file.getName());
                }
            }
        }
        
        return jobFiles;
    }
    
    private void scoreResume(Resume resume) {
        String content = resume.getContent();
        
        int skillScore = calculateScore(content, skillKeywords);
        int experienceScore = calculateScore(content, experienceKeywords);
        
        resume.setSkillScore(skillScore);
        resume.setExperienceScore(experienceScore);
    }
    
    
    private void scoreResumeForJob(Resume resume, Set<String> jobKeywords) {
        String content = resume.getContent();
        int jobMatchScore = 0;
        
        for (String keyword : jobKeywords) {
            if (content.contains(keyword.toLowerCase())) {
                jobMatchScore++;
            }
        }
        
        resume.setJobMatchScore(jobMatchScore);
    }
    
    
    private int calculateScore(String content, List<String> keywords) {
        int score = 0;
        String[] words = content.split("\\W+");
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));
        
        for (String keyword : keywords) {
            if (wordSet.contains(keyword.toLowerCase())) {
                score++;
            }
        }
        
        return score;
    }
    
    
    public void addSkillKeyword(String keyword) {
        if (!skillKeywords.contains(keyword.toLowerCase())) {
            skillKeywords.add(keyword.toLowerCase());
        }
    }
    
    public void addExperienceKeyword(String keyword) {
        if (!experienceKeywords.contains(keyword.toLowerCase())) {
            experienceKeywords.add(keyword.toLowerCase());
        }
    }
}