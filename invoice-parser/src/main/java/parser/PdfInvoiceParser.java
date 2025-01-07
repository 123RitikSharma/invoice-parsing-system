package parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfInvoiceParser implements InvoiceParser {

    private static final List<String> DATE_PATTERNS = Arrays.asList(
        "MM/dd/yyyy", "dd/MM/yyyy", "d MMM yyyy", "yyyy-MM-dd", "MMM dd, yyyy"
    );

    @Override
    public Map<String, String> parse(File file) throws Exception {
        Map<String, String> data = new HashMap<>();

        // Check if the PDF is encrypted
        try (PDDocument document = PDDocument.load(file)) {
            if (document.isEncrypted()) {
                System.out.println("PDF is encrypted, cannot extract text.");
                return null; // Return null if the PDF is encrypted
            }

            // Extract text from PDF
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.isEmpty()) {
                System.out.println("No text extracted from the PDF file.");
                return null; // Return null if no text is extracted
            }

            // Extract key values
            // Extract Invoice Number
            String invoiceNumber = extractValue(text, "(Invoice Number:|Invoice No\\.|Invoice ID:?)\\s*(\\S+)");
            data.put("InvoiceNumber", invoiceNumber != null ? invoiceNumber : "N/A");

            // Extract Date
            String dateStr = extractValue(text, "(Date:|Issue Date:|Date of Issue:)\\s*(\\S+)");
            String formattedDate = parseDate(dateStr); // Standardized to yyyy-MM-dd format
            data.put("Date", formattedDate != null ? formattedDate : "N/A");

            // Extract Vendor and Buyer Information
            data.put("Vendor", extractValue(text, "Vendor:\\s*(.*?)\\n"));
            data.put("Buyer", extractValue(text, "Buyer:\\s*(.*?)\\n"));

            // Extract Line Items (description, quantity, unit price, total price)
            String lineItems = extractLineItems(text);
            data.put("LineItems", lineItems.isEmpty() ? "No line items found" : lineItems);

            // Extract Subtotal, Taxes, Discounts, Total Amount
            data.put("Subtotal", extractValue(text, "Subtotal:\\s*(\\S+)"));
            data.put("Taxes", extractValue(text, "Taxes:\\s*(\\S+)"));
            data.put("Discounts", extractValue(text, "Discounts:\\s*(\\S+)"));
            data.put("TotalAmount", extractValue(text, "Total Amount:\\s*(\\S+)"));

            // Extract Payment Terms
            data.put("PaymentTerms", extractValue(text, "Payment Terms:\\s*(.*?)\\n"));
        }
        return data;
    }

    // Extract value using regex pattern
    private String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }

    // Extract line items (description, quantity, unit price, total price)
    private String extractLineItems(String text) {
        StringBuilder lineItems = new StringBuilder();
        // Regex for line items
        Pattern pattern = Pattern.compile("Item:\\s*(.*?)\\s*-\\s*Quantity:\\s*(\\d+)\\s*-\\s*Unit Price:\\s*\\$([\\d\\.]+)\\s*-\\s*Total Price:\\s*\\$([\\d\\.]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            lineItems.append("Description: ").append(matcher.group(1).trim())
                    .append(", Quantity: ").append(matcher.group(2).trim())
                    .append(", Unit Price: $").append(matcher.group(3).trim())
                    .append(", Total: $").append(matcher.group(4).trim()).append("\n");
        }
        return lineItems.toString();
    }

    // Parse date string into standardized format (yyyy-MM-dd)
    private String parseDate(String dateStr) {
        // Try each date format and parse accordingly
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
                Date date = dateFormat.parse(dateStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                return outputFormat.format(date); // Standardized format
            } catch (ParseException e) {
                // Continue to the next format if parsing fails
            }
        }
        return null; // Return null if no date formats match
    }

    // Convert parsed data to JSON format using Jackson library
    public String toJson(Map<String, String> data) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(data);
    }
}
