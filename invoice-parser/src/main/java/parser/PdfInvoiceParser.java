package parser;

import dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PdfInvoiceParser implements InvoiceParser {

    @Override
    public InvoiceDTO parse(InputStream inputStream) throws Exception {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Extract invoice fields
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setInvoiceNumber(extractInvoiceNumber(text));
        invoice.setInvoiceDate(extractInvoiceDate(text));
        invoice.setVendor(extractVendor(text));
        invoice.setBuyer(extractBuyer(text));
        invoice.setLineItems(extractLineItems(text));
        invoice.setSubtotal(extractSubtotal(text));
        invoice.setTax(extractTax(text));
        invoice.setDiscount(extractDiscount(text));
        invoice.setTotalAmount(extractTotalAmount(text));
        invoice.setPaymentTerms(extractPaymentTerms(text));

        return invoice;
    }

    private String extractInvoiceNumber(String text) {
        Pattern pattern = Pattern.compile("\\b(?:Invoice\\s*(?:No\\.|Number|#)?\\s*[:\\s-]*)\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    private String extractInvoiceDate(String text) {
        Pattern pattern = Pattern.compile(
            "\\b(\\d{4}-\\d{2}-\\d{2})\\b|" +
            "\\b(\\d{2}-\\d{2}-\\d{4})\\b|" +
            "\\b(\\d{2}/\\d{2}/\\d{4})\\b|" +
            "\\b(\\d{1,2}\\s+[A-Za-z]{3}\\s+\\d{4})\\b|" +
            "\\b([A-Za-z]{3}\\s+\\d{1,2},?\\s+\\d{4})\\b",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    return matcher.group(i);
                }
            }
        }
        return "Unknown";
    }

    private VendorDTO extractVendor(String text) {
        VendorDTO vendor = new VendorDTO();
        Pattern vendorPattern = Pattern.compile("(?i)Bill To:\\s*(.*?)(\\n|$)");
        String vendorName = extractGroup(text, vendorPattern, 1).trim();
        vendor.setName(vendorName.isEmpty() ? "Unknown" : vendorName);
        vendor.setAddress("N/A");
        vendor.setContact("N/A");
        return vendor;
    }

    private BuyerDTO extractBuyer(String text) {
        BuyerDTO buyer = new BuyerDTO();
        Pattern buyerPattern = Pattern.compile("(?i)Ship To:\\s*(.*?)(\\n|$)");
        String buyerName = extractGroup(text, buyerPattern, 1).trim();
        buyer.setName(buyerName.isEmpty() ? "Unknown" : buyerName);
        buyer.setAddress("N/A");
        buyer.setContact("N/A");
        return buyer;
    }

    private List<LineItemDTO> extractLineItems(String text) {
        List<LineItemDTO> items = new ArrayList<>();
        Pattern lineItemPattern = Pattern.compile("^(.*?)\\s+(\\d+)\\s+\\$([0-9,.]+)\\s+\\$([0-9,.]+)", Pattern.MULTILINE);

        Matcher matcher = lineItemPattern.matcher(text);
        while (matcher.find()) {
            LineItemDTO item = new LineItemDTO();
            item.setDescription(matcher.group(1).trim());
            item.setQuantity(Integer.parseInt(matcher.group(2)));
            item.setUnitPrice(Double.parseDouble(matcher.group(3).replace(",", "")));
            item.setTotalPrice(Double.parseDouble(matcher.group(4).replace(",", "")));
            items.add(item);
        }
        return items;
    }

    private double extractSubtotal(String text) {
        return extractAmount(text, "(?:Subtotal|SUB TOTAL|SUBTOTAL):");
    }

    private double extractTax(String text) {
        return extractAmount(text, "(?:Tax|TAX):");
    }

    private double extractDiscount(String text) {
        return extractAmount(text, "(?:Discount|DISCOUNT):");
    }

    private double extractShipping(String text) {
        return extractAmount(text, "(?:Shipping|SHIPPING):");
    }

    private double extractTotalAmount(String text) {
        return extractAmount(text, "(?:Total|TOTAL AMOUNT|TOTAL):");
    }

    private String extractPaymentTerms(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:Terms|Payment Terms):\\s*(.*)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "Unknown";
    }

    private double extractAmount(String text, String labelRegex) {
        Pattern pattern = Pattern.compile(labelRegex + "\\s*\\$([0-9,.]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1).replace(",", "")) : 0.0;
    }

    private String extractGroup(String text, Pattern pattern, int group) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(group) : "";
    }
}
