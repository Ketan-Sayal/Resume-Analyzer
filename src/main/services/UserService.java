package src.main.services;

import src.main.models.User;
import src.main.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static final String USERS_DIRECTORY = "data/users";
    private Map<String, User> userCache;
    
    public UserService() {
        // Create users directory if it doesn't exist
        File directory = new File(USERS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Load users into cache
        loadUsers();
    }
    
    /**
     * Loads all users from the users directory
     */
    private void loadUsers() {
        userCache = new HashMap<>();
        File usersDir = new File(USERS_DIRECTORY);
        
        if (usersDir.exists() && usersDir.isDirectory()) {
            File[] userFiles = usersDir.listFiles((dir, name) -> name.endsWith(".user"));
            
            if (userFiles != null) {
                for (File userFile : userFiles) {
                    try {
                        User user = (User) FileUtils.deserializeObject(userFile.getPath());
                        if (user != null) {
                            userCache.put(user.getUsername(), user);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading user from " + userFile.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Save a user to file
     */
    private void saveUser(User user) {
        String filePath = USERS_DIRECTORY + "/" + user.getUsername() + ".user";
        try {
            FileUtils.serializeObject(user, filePath);
            // Update cache
            userCache.put(user.getUsername(), user);
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }
    
    /**
     * Register a new user
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String password) {
        // Check if username already exists
        if (userCache.containsKey(username)) {
            return false;
        }
        
        // Hash the password
        String hashedPassword = hashPassword(password);
        
        // Create and save new user
        User newUser = new User(username, hashedPassword);
        saveUser(newUser);
        
        return true;
    }
    
    /**
     * Authenticate a user
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        User user = userCache.get(username);
        
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            return user;
        }
        
        return null;
    }
    
    /**
     * Hash a password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plain password if hashing fails
            System.err.println("Password hashing failed: " + e.getMessage());
            return password;
        }
    }
}