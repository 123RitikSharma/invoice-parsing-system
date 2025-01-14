package service;

import dto.InvoiceDTO;
import exception.InvalidFileTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private InvoiceParseService invoiceParseService;

    // Validate the file type
    public void validateFileType(MultipartFile file) throws InvalidFileTypeException {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (!"pdf".equalsIgnoreCase(extension) && !"docx".equalsIgnoreCase(extension) && !"txt".equalsIgnoreCase(extension)) {
            throw new InvalidFileTypeException("Invalid file type. Only PDF, DOCX, and TXT files are allowed.");
        }
    }

    // Process the uploaded file and convert to JSON (Single file)
    public void processFile(MultipartFile file) {
        try {
            // Get the file type (extension)
            String fileExtension = getFileExtension(file);

            // Parse the file to get InvoiceDTO
            InvoiceDTO invoiceDTO = invoiceParseService.parseFile(file.getInputStream(), fileExtension);

            // Convert InvoiceDTO to JSON
            String jsonInvoice = convertToJson(invoiceDTO);

            // Print the structured JSON to the terminal
            logger.info("=== Structured JSON Output ===");
            logger.info(jsonInvoice);
            logger.info("=============================");

        } catch (Exception e) {
            logger.error("Error processing the file", e);
        }
    }

    // Process multiple files in bulk
    public void processBulkFiles(MultipartFile[] files) {
        for (MultipartFile file : files) {
            try {
                // Process each file one by one
                processFile(file);
            } catch (Exception e) {
                logger.error("Error processing the file " + file.getOriginalFilename(), e);
            }
        }
    }

    // Helper method to get file extension
    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
    }

    // Convert InvoiceDTO to structured JSON using Jackson's ObjectMapper
    private String convertToJson(InvoiceDTO invoiceDTO) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(invoiceDTO);
        } catch (Exception e) {
            logger.error("Error converting InvoiceDTO to JSON", e);
            return "{}";
        }
    }
}
