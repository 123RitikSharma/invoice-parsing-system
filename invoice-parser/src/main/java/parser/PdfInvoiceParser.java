package parser;

import dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfInvoiceParser implements InvoiceParser {

    @Override
    public InvoiceDTO parse(InputStream inputStream) throws Exception {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

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
        Pattern pattern = Pattern.compile("INV-\\d+");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "Unknown";
    }

    private String extractInvoiceDate(String text) {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "Unknown";
    }

    private VendorDTO extractVendor(String text) {
        VendorDTO vendor = new VendorDTO();
        Pattern namePattern = Pattern.compile("Vendor Name:\\s*(.*)");
        Pattern addressPattern = Pattern.compile("Vendor Address:\\s*(.*)");
        Pattern contactPattern = Pattern.compile("Vendor Contact:\\s*(.*)");

        Matcher nameMatcher = namePattern.matcher(text);
        Matcher addressMatcher = addressPattern.matcher(text);
        Matcher contactMatcher = contactPattern.matcher(text);

        if (nameMatcher.find()) vendor.setName(nameMatcher.group(1));
        if (addressMatcher.find()) vendor.setAddress(addressMatcher.group(1));
        if (contactMatcher.find()) vendor.setContact(contactMatcher.group(1));

        return vendor;
    }

    private BuyerDTO extractBuyer(String text) {
        BuyerDTO buyer = new BuyerDTO();
        Pattern namePattern = Pattern.compile("Buyer Name:\\s*(.*)");
        Pattern addressPattern = Pattern.compile("Buyer Address:\\s*(.*)");
        Pattern contactPattern = Pattern.compile("Buyer Contact:\\s*(.*)");

        Matcher nameMatcher = namePattern.matcher(text);
        Matcher addressMatcher = addressPattern.matcher(text);
        Matcher contactMatcher = contactPattern.matcher(text);

        if (nameMatcher.find()) buyer.setName(nameMatcher.group(1));
        if (addressMatcher.find()) buyer.setAddress(addressMatcher.group(1));
        if (contactMatcher.find()) buyer.setContact(contactMatcher.group(1));

        return buyer;
    }

    private List<LineItemDTO> extractLineItems(String text) {
        List<LineItemDTO> items = new ArrayList<>();
        Pattern lineItemPattern = Pattern.compile("(\\w+\\s+\\w+).*?([0-9]+).*?([0-9.]+).*?([0-9.]+)");

        Matcher matcher = lineItemPattern.matcher(text);
        while (matcher.find()) {
            LineItemDTO item = new LineItemDTO();
            item.setDescription(matcher.group(1));
            item.setQuantity(Integer.parseInt(matcher.group(2)));
            item.setUnitPrice(Double.parseDouble(matcher.group(3)));
            item.setTotalPrice(Double.parseDouble(matcher.group(4)));
            items.add(item);
        }
        return items;
    }

    private double extractSubtotal(String text) {
        Pattern subtotalPattern = Pattern.compile("Subtotal:\\s*(\\d+\\.\\d{2})");
        Matcher matcher = subtotalPattern.matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    private double extractTax(String text) {
        Pattern taxPattern = Pattern.compile("Tax:\\s*(\\d+\\.\\d{2})");
        Matcher matcher = taxPattern.matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    private double extractDiscount(String text) {
        Pattern discountPattern = Pattern.compile("Discount:\\s*(\\d+\\.\\d{2})");
        Matcher matcher = discountPattern.matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    private double extractTotalAmount(String text) {
        Pattern totalAmountPattern = Pattern.compile("Total Amount:\\s*(\\d+\\.\\d{2})");
        Matcher matcher = totalAmountPattern.matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    private String extractPaymentTerms(String text) {
        Pattern paymentTermsPattern = Pattern.compile("Payment Terms:\\s*(.*)");
        Matcher matcher = paymentTermsPattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }
}
