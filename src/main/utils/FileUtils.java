package src.main.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    
    /**
     * Read content from a file and return it as a String
     */
    public static String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
    
    /**
     * Read content from a file path and return it as a String
     */
    public static String readFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    /**
     * Write content to a file
     */
    public static void writeFileContent(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }
    
    /**
     * Serialize an object to a file
     */
    public static void serializeObject(Object object, String filePath) throws IOException {
        // Create parent directories if they don't exist
        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(object);
        }
    }
    
    /**
     * Deserialize an object from a file
     */
    public static Object deserializeObject(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return ois.readObject();
        }
    }
    
    /**
     * Check if a file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Create directories if they don't exist
     */
    public static void createDirectories(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }
}