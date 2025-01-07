package parser;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TxtInvoiceParser implements InvoiceParser {
    @Override
    public Map<String, String> parse(File file) throws Exception {
        String text = Files.readString(file.toPath());
        Map<String, String> data = new HashMap<>();

        // Extracting fields like InvoiceNumber, Date, Vendor, etc.
        data.put("InvoiceNumber", extractValue(text, "Invoice Number:"));
        data.put("Date", extractValue(text, "Date:"));
        data.put("Vendor", extractValue(text, "Vendor:"));
        data.put("Buyer", extractValue(text, "Buyer:"));
        data.put("TotalAmount", extractValue(text, "Total Amount:"));
        data.put("Taxes", extractValue(text, "Taxes:"));
        data.put("Subtotal", extractValue(text, "Subtotal:"));
        data.put("Discounts", extractValue(text, "Discounts:"));
        data.put("PaymentTerms", extractValue(text, "Payment Terms:"));
        data.put("LineItems", extractLineItems(text)); // Assuming line items are a list of items

        return data;
    }

    private String extractValue(String text, String key) {
        int start = text.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = text.indexOf("\n", start);
        return end == -1 ? text.substring(start).trim() : text.substring(start, end).trim();
    }

    private String extractLineItems(String text) {
        // Example of line items extraction (this can be more sophisticated depending on your file structure)
        int start = text.indexOf("Line Items:");
        if (start == -1) return null;
        
        // Assuming line items are listed under "Line Items:"
        start += "Line Items:".length();
        int end = text.indexOf("\n", start);
        if (end == -1) end = text.length();

        return text.substring(start, end).trim();
    }
}
