package controller;  

import dto.UploadFileResponse;  
import exception.InvalidFileTypeException;  
import service.FileUploadService;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate and process single file
            fileUploadService.validateFileType(file);
            fileUploadService.processFile(file);

            UploadFileResponse response = new UploadFileResponse("File uploaded successfully", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (InvalidFileTypeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UploadFileResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UploadFileResponse("Error processing file", null));
        }
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<List<UploadFileResponse>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<UploadFileResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // Validate and process each file
                fileUploadService.validateFileType(file);
                fileUploadService.processFile(file);
                responses.add(new UploadFileResponse("File uploaded successfully", file.getOriginalFilename()));
            } catch (InvalidFileTypeException e) {
                responses.add(new UploadFileResponse(e.getMessage(), file.getOriginalFilename()));
            } catch (Exception e) {
                responses.add(new UploadFileResponse("Error processing file", file.getOriginalFilename()));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
