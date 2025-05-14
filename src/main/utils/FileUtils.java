package src.main.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    
   
    public static String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
    
    
    public static String readFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    
    public static void writeFileContent(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }
    
    
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
    
    
    public static Object deserializeObject(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return ois.readObject();
        }
    }
    
   
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    
    public static void createDirectories(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }
}