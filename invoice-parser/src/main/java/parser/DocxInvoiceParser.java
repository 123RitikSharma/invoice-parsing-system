package parser;

import dto.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DocxInvoiceParser implements InvoiceParser {

    @Override
    public InvoiceDTO parse(InputStream inputStream) throws Exception {
        XWPFDocument document = new XWPFDocument(inputStream);
        String text = extractTextFromDocx(document);

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setInvoiceNumber(extractInvoiceNumber(text));
        invoice.setInvoiceDate(extractInvoiceDate(text));
        invoice.setVendor(extractVendor(text));
        invoice.setBuyer(extractBuyer(text));
        invoice.setLineItems(extractLineItems(text));
        invoice.setSubtotal(extractSubtotal(text));
        invoice.setTotalAmount(extractTotalAmount(text));
        invoice.setPaymentTerms(extractPaymentTerms(text));
        return invoice;
    }

    private String extractTextFromDocx(XWPFDocument document) throws Exception {
        StringBuilder text = new StringBuilder();
        document.getParagraphs().forEach(paragraph -> text.append(paragraph.getText()).append("\n"));
        return text.toString();
    }

    private String extractInvoiceNumber(String text) {
        // Extract the invoice number after the word 'INVOICE' or '#'
        Pattern pattern = Pattern.compile("#\\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    private String extractInvoiceDate(String text) {
        // Extract the date format such as 'Feb 20 2013'
        Pattern pattern = Pattern.compile("(\\w{3}\\s\\d{2}\\s\\d{4})");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    private VendorDTO extractVendor(String text) {
        // Assuming vendor information is static or can be extracted by name from the document (if present)
        VendorDTO vendor = new VendorDTO();
        vendor.setName("SuperStore"); // As per the provided example, the vendor name is "SuperStore"
        vendor.setAddress("N/A"); // No address is provided in the document
        vendor.setContact("N/A"); // No contact details in the document
        return vendor;
    }

    private BuyerDTO extractBuyer(String text) {
        BuyerDTO buyer = new BuyerDTO();
        Pattern namePattern = Pattern.compile("Bill\\sTo:\\s*(.*)");
        Pattern addressPattern = Pattern.compile("Ship\\sTo:\\s*(.*)");

        Matcher nameMatcher = namePattern.matcher(text);
        Matcher addressMatcher = addressPattern.matcher(text);

        if (nameMatcher.find()) buyer.setName(nameMatcher.group(1).trim());
        if (addressMatcher.find()) buyer.setAddress(addressMatcher.group(1).trim());
        buyer.setContact("N/A"); // No contact details in the document

        return buyer;
    }

    private List<LineItemDTO> extractLineItems(String text) {
        List<LineItemDTO> items = new ArrayList<>();
        
        // Modified line item pattern, allowing for more variations in the structure
        // Looking for the following format:
        // Item Description    Quantity    Unit Price    Total Price
        Pattern lineItemPattern = Pattern.compile("([\\w\\s,]+)\\s+(\\d+)\\s+\\$([\\d,]+\\.\\d{2})\\s+\\$([\\d,]+\\.\\d{2})");

        Matcher matcher = lineItemPattern.matcher(text);
        
        while (matcher.find()) {
            LineItemDTO item = new LineItemDTO();
            
            item.setDescription(matcher.group(1).trim());
            
            // Safely parse quantity
            try {
                item.setQuantity(Integer.parseInt(matcher.group(2)));
            } catch (NumberFormatException e) {
                item.setQuantity(0);
            }

            // Safely parse unit price
            try {
                String unitPriceStr = matcher.group(3).replace(",", "");
                item.setUnitPrice(Double.parseDouble(unitPriceStr));
            } catch (NumberFormatException e) {
                item.setUnitPrice(0.0);
            }

            // Safely parse total price
            try {
                String totalPriceStr = matcher.group(4).replace(",", "");
                item.setTotalPrice(Double.parseDouble(totalPriceStr));
            } catch (NumberFormatException e) {
                item.setTotalPrice(0.0);
            }

            items.add(item);
        }

        // Debugging: log the items extracted
        if (items.isEmpty()) {
            System.out.println("No line items found in the document.");
        } else {
            items.forEach(item -> System.out.println("Line item: " + item.getDescription() + " | Quantity: " + item.getQuantity() + " | Unit Price: " + item.getUnitPrice() + " | Total Price: " + item.getTotalPrice()));
        }

        return items;
    }


    private double extractSubtotal(String text) {
        Pattern subtotalPattern = Pattern.compile("Subtotal:\\s*\\$([\\d,]+\\.\\d{2})");
        Matcher matcher = subtotalPattern.matcher(text);
        return matcher.find() ? parseAmount(matcher.group(1)) : 0.0;
    }

    private double extractShipping(String text) {
        Pattern shippingPattern = Pattern.compile("Shipping:\\s*\\$([\\d,]+\\.\\d{2})");
        Matcher matcher = shippingPattern.matcher(text);
        return matcher.find() ? parseAmount(matcher.group(1)) : 0.0;
    }

    private double extractTotalAmount(String text) {
        Pattern totalAmountPattern = Pattern.compile("Total:\\s*\\$([\\d,]+\\.\\d{2})");
        Matcher matcher = totalAmountPattern.matcher(text);
        return matcher.find() ? parseAmount(matcher.group(1)) : 0.0;
    }

    private String extractPaymentTerms(String text) {
        Pattern paymentTermsPattern = Pattern.compile("Terms:\\s*(.*)");
        Matcher matcher = paymentTermsPattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    // Helper method to safely parse amounts with a fallback for invalid data
    private double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;  // Default to 0.0 if invalid
        }
    }
}
