package src.main.services;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import src.main.models.Job;
import src.main.models.Resume;
import src.main.utils.FileUtils;

public class ResumeService {
    private static final String RESUMES_DIRECTORY = "data/resumes";
    private static final String JOBS_DIRECTORY = "data/jobs";
    
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
    
    /**
     * Analyze all resumes in the resumes directory
     * @return Map of resume file names to their total scores, sorted by score
     */
    public Map<String, Integer> analyzeResumes() {
        List<Resume> resumes = loadResumes();
        
        // Score each resume
        for (Resume resume : resumes) {
            scoreResume(resume);
        }
        
        // Sort resumes by total score and create result map
        return resumes.stream()
                .sorted(Comparator.comparing(Resume::getTotalScore).reversed())
                .collect(Collectors.toMap(
                    Resume::getFileName,
                    Resume::getTotalScore,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Analyze resumes based on a specific job description
     * @param jobFileName The name of the job file to analyze against
     * @return Map of resume file names to their job match scores, sorted by score
     */
    public Map<String, Integer> analyzeResumesForJob(String jobFileName) {
        List<Resume> resumes = loadResumes();
        Job job = loadJob(jobFileName);
        
        if (job == null) {
            System.err.println("Job file not found: " + jobFileName);
            return new LinkedHashMap<>();
        }
        
        // Extract keywords from job description
        Set<String> jobKeywords = extractKeywords(job.getDescription());
        
        // Score each resume against the job
        for (Resume resume : resumes) {
            scoreResumeForJob(resume, jobKeywords);
        }
        
        // Sort resumes by job match score and create result map
        return resumes.stream()
                .sorted(Comparator.comparing(Resume::getJobMatchScore).reversed())
                .collect(Collectors.toMap(
                    Resume::getFileName,
                    Resume::getJobMatchScore,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Extract important keywords from a text
     */
    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        
        // Add all skill keywords found in the text
        for (String skill : skillKeywords) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                keywords.add(skill.toLowerCase());
            }
        }
        
        // Add all experience keywords found in the text
        for (String exp : experienceKeywords) {
            if (text.toLowerCase().contains(exp.toLowerCase())) {
                keywords.add(exp.toLowerCase());
            }
        }
        
        // Add additional processing for extracting more keywords if needed
        
        return keywords;
    }
    
    /**
     * Load all resumes from the resumes directory
     */
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
    
    /**
     * Load a specific job from the jobs directory
     */
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
    
    /**
     * List all available job files
     */
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
    
    /**
     * Score a resume based on skill and experience keywords
     */
    private void scoreResume(Resume resume) {
        String content = resume.getContent();
        
        int skillScore = calculateScore(content, skillKeywords);
        int experienceScore = calculateScore(content, experienceKeywords);
        
        resume.setSkillScore(skillScore);
        resume.setExperienceScore(experienceScore);
    }
    
    /**
     * Score a resume specifically for a job match
     */
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
    
    /**
     * Calculate score based on keywords
     */
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
    
    /**
     * Add custom keywords to the existing lists
     */
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