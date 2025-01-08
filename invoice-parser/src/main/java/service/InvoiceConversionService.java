package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.InvoiceDTO;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class InvoiceConversionService {

    public String convertToJson(InvoiceDTO invoice) {
        // Your logic to convert the invoice to JSON (e.g., using Jackson)
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
