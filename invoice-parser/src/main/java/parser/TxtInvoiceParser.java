package parser;

import dto.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component 
public class TxtInvoiceParser implements InvoiceParser {

    @Override
    public InvoiceDTO parse(InputStream inputStream) throws Exception {
        // Use InputStreamReader and BufferedReader to read the InputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder text = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            text.append(line).append("\n"); // Append line to text
        }

        String invoiceText = text.toString();
        
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setInvoiceNumber(extractInvoiceNumber(invoiceText));
        invoice.setInvoiceDate(extractInvoiceDate(invoiceText));
        invoice.setVendor(extractVendor(invoiceText));
        invoice.setBuyer(extractBuyer(invoiceText));
        invoice.setLineItems(extractLineItems(invoiceText));
        invoice.setSubtotal(extractSubtotal(invoiceText));
        invoice.setTax(extractTax(invoiceText));
        invoice.setDiscount(extractDiscount(invoiceText));
        invoice.setTotalAmount(extractTotalAmount(invoiceText));
        invoice.setPaymentTerms(extractPaymentTerms(invoiceText));
        
        return invoice;
    }

    private String extractInvoiceNumber(String text) {
        return extractString(text, "\\bInvoice Number[:\\-\\s]*(INV[- ]?\\d+)", "Unknown");
    }

    private String extractInvoiceDate(String text) {
        return extractString(text, "\\bDate[:\\-\\s]*(\\w+ \\d{1,2}, \\d{4})", "Unknown");
    }

    private VendorDTO extractVendor(String text) {
        VendorDTO vendor = new VendorDTO();
        // Improved regex to extract vendor details
        String vendorName = extractString(text, "\\bVendor[:\\-\\s]*(.*?)(?=\\s*Buyer)", "Unknown");
        vendor.setName(vendorName);
        return vendor;
    }

    private BuyerDTO extractBuyer(String text) {
        BuyerDTO buyer = new BuyerDTO();
        // Improved regex to extract buyer details
        String buyerName = extractString(text, "\\bBuyer[:\\-\\s]*(.*?)(?=\\s*Item|\\s*Subtotal)", "Unknown");
        buyer.setName(buyerName);
        return buyer;
    }

    private List<LineItemDTO> extractLineItems(String text) {
        List<LineItemDTO> items = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\bItem[:\\s]*(.*?)\\s*-\\s*Quantity[:\\s]*(\\d+)\\s*-\\s*Unit Price[:\\s]*\\$(\\d+\\.\\d{2})\\s*-\\s*Total Price[:\\s]*\\$(\\d+\\.\\d{2})");
        
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            LineItemDTO item = new LineItemDTO();
            item.setDescription(matcher.group(1).trim());
            item.setQuantity(Integer.parseInt(matcher.group(2)));
            item.setUnitPrice(Double.parseDouble(matcher.group(3)));
            item.setTotalPrice(Double.parseDouble(matcher.group(4)));
            items.add(item);
        }
        return items;
    }

    private double extractSubtotal(String text) {
        return extractDouble(text, "\\bSubtotal[:\\s]*\\$(\\d+\\.\\d{2})", 0.0);
    }

    private double extractTax(String text) {
        return extractDouble(text, "\\bTaxes?:?[:\\s]*\\$(\\d+\\.\\d{2})", 0.0);
    }

    private double extractDiscount(String text) {
        return extractDouble(text, "\\bDiscount[:\\s]*\\$(\\d+\\.\\d{2})", 0.0);
    }

    private double extractTotalAmount(String text) {
        return extractDouble(text, "\\bTotal Amount[:\\s]*\\$(\\d+\\.\\d{2})", 0.0);
    }

    private String extractPaymentTerms(String text) {
        return extractString(text, "\\bPayment Terms[:\\s]*(.*?)(?=\\s*Invoice Currency|\\s*$)", "Unknown");
    }

    private String extractInvoiceCurrency(String text) {
        return extractString(text, "\\bInvoice Currency[:\\s]*(\\w+)", "Unknown");
    }

    private double extractShippingCharges(String text) {
        return extractDouble(text, "\\bShipping Charges[:\\s]*\\$(\\d+\\.\\d{2})", 0.0);
    }

    private String extractInvoiceNotes(String text) {
        return extractString(text, "\\bInvoice Notes[:\\s]*(.*)", "Unknown");
    }

    private String extractTermsAndConditions(String text) {
        return extractString(text, "\\bTerms and Conditions[:\\s]*(.*)", "Unknown");
    }

    // Utility Methods for Parsing

    private String extractString(String text, String regex, String defaultValue) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : defaultValue;
    }

    private double extractDouble(String text, String regex, double defaultValue) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1).trim()) : defaultValue;
    }
}
