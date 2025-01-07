package parser;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageInvoiceParser implements InvoiceParser {
    @Override
    public Map<String, String> parse(File file) throws Exception {
        ITesseract tesseract = new Tesseract();
        
        // Set the path to the Tesseract 'tessdata' directory
        
        System.setProperty("TESSDATA_PREFIX", "C:/Users/Ritik/Downloads/tesseract-main/tesseract-main/");
        tesseract.setDatapath("C:/Users/Ritik/Downloads/tesseract-main/tesseract-main/tessdata");

        String text;

        try {
            text = tesseract.doOCR(file);
        } catch (TesseractException e) {
            throw new Exception("Error processing image file for OCR", e);
        }

        Map<String, String> data = new HashMap<>();

        // Extract fields using regex
        data.put("InvoiceNumber", extractValue(text, "Invoice Number:\\s*(\\S+)"));
        data.put("Date", extractValue(text, "Date:\\s*(\\S+)"));
        data.put("Vendor", extractValue(text, "Vendor:\\s*(.*?)\\n"));
        data.put("Buyer", extractValue(text, "Buyer:\\s*(.*?)\\n"));
        
        // Extract Line Items (description, quantity, unit price, total price)
        String lineItems = extractLineItems(text);
        data.put("LineItems", lineItems);

        // Extract Subtotal, Taxes, Discounts, Total Amount
        data.put("Subtotal", extractValue(text, "Subtotal:\\s*(\\S+)"));
        data.put("Taxes", extractValue(text, "Taxes:\\s*(\\S+)"));
        data.put("Discounts", extractValue(text, "Discounts:\\s*(\\S+)"));
        data.put("TotalAmount", extractValue(text, "Total Amount:\\s*(\\S+)"));

        // Extract Payment Terms
        data.put("PaymentTerms", extractValue(text, "Payment Terms:\\s*(.*?)\\n"));
        
        return data;
    }

    // Helper method to extract a single value using regex
    private String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);  // DOTALL allows the dot to match newline characters
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;  // Return null if no match is found
    }

    // Extract Line Items (description, quantity, unit price, total price)
    private String extractLineItems(String text) {
        StringBuilder lineItems = new StringBuilder();
        
        // Example pattern to match line items; update as per your invoice format
        Pattern pattern = Pattern.compile("(\\d+)\\s*(\\D+)\\s*(\\d+\\.\\d{2})\\s*(\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            lineItems.append("Description: ").append(matcher.group(2))
                    .append(", Quantity: ").append(matcher.group(1))
                    .append(", Unit Price: ").append(matcher.group(3))
                    .append(", Total: ").append(matcher.group(4)).append("\n");
        }
        
        return lineItems.toString();
    }
}
