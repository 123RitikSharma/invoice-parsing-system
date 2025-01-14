package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.InvoiceDTO;
import org.springframework.stereotype.Service;

@Service
public class InvoiceConversionService {

    public String convertToJson(InvoiceDTO invoice) {
        // ObjectMapper instance to handle JSON conversion
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Using pretty printer to format the JSON output
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
