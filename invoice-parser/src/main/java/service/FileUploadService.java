package service;  // Package declaration should match the directory structure

import exception.InvalidFileTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class FileUploadService {

    private static final List<String> SUPPORTED_FILE_TYPES = Arrays.asList("pdf", "docx", "txt", "jpg", "jpeg", "png");

    // Validate the file type
    public void validateFileType(MultipartFile file) throws InvalidFileTypeException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!SUPPORTED_FILE_TYPES.contains(fileExtension)) {
            throw new InvalidFileTypeException("Unsupported file type. Supported types: PDF, DOCX, TXT, JPG, PNG.");
        }
    }

    // Process the file (placeholder logic)
    public void processFile(MultipartFile file) {
        // Placeholder for file processing logic
        System.out.println("Processing file: " + file.getOriginalFilename());
    }

    // Helper method to get the file extension
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}