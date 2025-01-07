package parser;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocxInvoiceParser implements InvoiceParser {
    @Override
    public Map<String, String> parse(File file) throws Exception {
        Map<String, String> data = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(p -> text.append(p.getText()).append("\n"));

            // Extract Invoice Number
            data.put("InvoiceNumber", extractValue(text.toString(), "Invoice Number:\\s*(\\S+)"));

            // Extract Date
            data.put("Date", extractValue(text.toString(), "Date:\\s*(\\S+)"));
            
            // Extract Vendor and Buyer Information
            data.put("Vendor", extractValue(text.toString(), "Vendor:\\s*(.*?)\\n"));
            data.put("Buyer", extractValue(text.toString(), "Buyer:\\s*(.*?)\\n"));
            
            // Extract Line Items (description, quantity, unit price, total price)
            String lineItems = extractLineItems(text.toString());
            data.put("LineItems", lineItems);
            
            // Extract Subtotal, Taxes, Discounts, Total Amount
            data.put("Subtotal", extractValue(text.toString(), "Subtotal:\\s*(\\S+)"));
            data.put("Taxes", extractValue(text.toString(), "Taxes:\\s*(\\S+)"));
            data.put("Discounts", extractValue(text.toString(), "Discounts:\\s*(\\S+)"));
            data.put("TotalAmount", extractValue(text.toString(), "Total Amount:\\s*(\\S+)"));
            
            // Extract Payment Terms
            data.put("PaymentTerms", extractValue(text.toString(), "Payment Terms:\\s*(.*?)\\n"));
        }
        return data;
    }

    private String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractLineItems(String text) {
        StringBuilder lineItems = new StringBuilder();
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
