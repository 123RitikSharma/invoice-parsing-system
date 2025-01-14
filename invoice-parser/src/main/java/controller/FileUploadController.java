package controller;

import dto.UploadFileResponse;
import service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService; // Inject FileUploadService

    // Single file upload
    @PostMapping("/upload")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate the file type
            fileUploadService.validateFileType(file);

            // Process the uploaded file and print the structured JSON to the terminal
            fileUploadService.processFile(file);

            // Return success response with the file name
            UploadFileResponse response = new UploadFileResponse("File uploaded and processed: " + file.getOriginalFilename(), null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Return error response in case of failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UploadFileResponse("Error processing file", null));
        }
    }

    // Bulk file upload
    @PostMapping("/upload/bulk")
    public ResponseEntity<UploadFileResponse> uploadBulkFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            // Iterate over all files and process them
            for (MultipartFile file : files) {
                // Validate the file type
                fileUploadService.validateFileType(file);

                // Process the uploaded file and print the structured JSON to the terminal
                fileUploadService.processFile(file);
            }

            // Return success response with the number of files processed
            UploadFileResponse response = new UploadFileResponse("Files uploaded and processed: " + files.length, null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Return error response in case of failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UploadFileResponse("Error processing files", null));
        }
    }
}
