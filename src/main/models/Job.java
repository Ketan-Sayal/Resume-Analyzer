package src.main.models;

public class Job {
    private String fileName;
    private String description;
    
    public Job(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getDescription() {
        return description;
    }
}