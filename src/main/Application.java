package src.main;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import src.main.models.User;
import src.main.services.ResumeService;
import src.main.services.UserService;

public class Application {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static final UserService userService = new UserService();
    private static final ResumeService resumeService = new ResumeService();
    
    public static void main(String[] args) {
        boolean running = true;
        
        while (running) {
            displayMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    if (isLoggedIn()) {
                        analyzeResumes();
                    }
                    break;
                case 4:
                    if (isLoggedIn()) {
                        analyzeResumesForJob();
                    }
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
    
    private static void displayMenu() {
        System.out.println("\n===== Resume Analyzer System =====");
        if (isLoggedIn()) {
            System.out.println("Logged in as: " + currentUser.getUsername());
        } else {
            System.out.println("Not logged in");
        }
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Analyze All Resumes");
        System.out.println("4. Analyze Resumes for Specific Job");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        if (userService.registerUser(username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists. Please choose another.");
        }
    }
    
    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        User user = userService.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }
    
    private static void analyzeResumes() {
        System.out.println("\nAnalyzing all resumes based on skills and experience...");
        Map<String, Integer> results = resumeService.analyzeResumes();
        
        if (results.isEmpty()) {
            System.out.println("No resumes found. Please add some resumes to the resumes directory.");
            return;
        }
        
        System.out.println("\n==== Resume Rankings (Skills + Experience) ====");
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            System.out.println("Resume: " + entry.getKey() + " | Total Score: " + entry.getValue());
        }
    }

    private static StringBuffer returnName(String filename) {
        StringBuffer name = new StringBuffer("");
        for (int i = 0; i < filename.length() - 1; i++) {
            if (filename.charAt(i) == '.' && filename.charAt(i + 1) == 't') {
                break;
            }
            name.append(filename.charAt(i));
        }
        return name;
    }
    
    
    private static void analyzeResumesForJob() {
        // List available job descriptions
        List<String> jobs = resumeService.listJobs();
        
        if (jobs.isEmpty()) {
            System.out.println("No job descriptions found. Please add job descriptions to the jobs directory.");
            return;
        }
        
        // Display available jobs
        System.out.println("\n==== Available Job Descriptions ====");
        for (int i = 0; i < jobs.size(); i++) {
            System.out.println((i + 1) + ". " + returnName(jobs.get(i)));
        }
        
        // Get user selection
        System.out.print("\nSelect a job number to analyze resumes against (1-" + jobs.size() + "): ");
        int jobIndex;
        try {
            jobIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (jobIndex < 0 || jobIndex >= jobs.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        // Analyze resumes against selected job
        String selectedJob = jobs.get(jobIndex);
        System.out.println("\nAnalyzing resumes for job: " + selectedJob);
        Map<String, Integer> results = resumeService.analyzeResumesForJob(selectedJob);
        
        if (results.isEmpty()) {
            System.out.println("No resumes found or no matches for this job description.");
            return;
        }
        
        // Display results
        System.out.println("\n==== Resume Rankings for " + selectedJob + " ====");
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            System.out.println("Resume: " + entry.getKey() + " | Job Match Score: " + entry.getValue());
        }
    }
    
    private static boolean isLoggedIn() {
        if (currentUser == null) {
            System.out.println("Please login first to access this feature.");
            return false;
        }
        return true;
    }
}