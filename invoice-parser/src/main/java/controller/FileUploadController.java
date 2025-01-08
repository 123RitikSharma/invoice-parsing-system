package controller;

import dto.InvoiceDTO;
import dto.UploadFileResponse;
import service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private InvoiceParseService invoiceParseService;

    @Autowired
    private InvoiceConversionService invoiceConversionService; // Inject InvoiceConversionService

    @PostMapping("/upload")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileType = getFileType(file);  // Determine file type (pdf, txt, image)

            // Parse the file based on its type
            InvoiceDTO invoice = invoiceParseService.parseFile(file.getInputStream(), fileType);

            // Convert invoice to JSON using the injected service
            String jsonOutput = invoiceConversionService.convertToJson(invoice);

            // Print the JSON output (for logging or further processing)
            System.out.println(jsonOutput);

            UploadFileResponse response = new UploadFileResponse("File uploaded and processed successfully", jsonOutput);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UploadFileResponse("Error processing file", null));
        }
    }

    private String getFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() : "";
    }
}
