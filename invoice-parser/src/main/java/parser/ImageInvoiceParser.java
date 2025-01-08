//package parser;
//
//import dto.*;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.InputStream;
//import javax.imageio.ImageIO;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class ImageInvoiceParser implements InvoiceParser {
//
//    private Tesseract tesseract;
//
//    public ImageInvoiceParser() {
//        this.tesseract = new Tesseract();
//        this.tesseract.setLanguage("eng"); // Set language to English for OCR
//    }
//
//    @Override
//    public InvoiceDTO parse(Object imageSource) throws Exception {
//        // Ensure the source is an InputStream
//        if (imageSource instanceof InputStream) {
//            InputStream inputStream = (InputStream) imageSource;
//            BufferedImage image = ImageIO.read(inputStream);  // Read the image from InputStream
//            String extractedText = extractTextFromImage(image);
//            return parseExtractedTextToInvoiceDTO(extractedText);
//        }
//        throw new IllegalArgumentException("Unsupported image source type. Expected InputStream.");
//    }
//
//    private String extractTextFromImage(BufferedImage image) throws TesseractException {
//        return tesseract.doOCR(image); // Use doOCR for BufferedImage
//    }
//
//    private InvoiceDTO parseExtractedTextToInvoiceDTO(String extractedText) {
//        InvoiceDTO invoiceDTO = new InvoiceDTO();
//
//        // Extract fields from OCR text using regular expressions
//        invoiceDTO.setInvoiceNumber(extractInvoiceNumber(extractedText));
//        invoiceDTO.setInvoiceDate(extractInvoiceDate(extractedText));
//        invoiceDTO.setVendor(extractVendor(extractedText));
//        invoiceDTO.setBuyer(extractedBuyer(extractedText));
//        invoiceDTO.setLineItems(extractLineItems(extractedText));
//        invoiceDTO.setSubtotal(extractSubtotal(extractedText));
//        invoiceDTO.setTax(extractTax(extractedText));
//        invoiceDTO.setDiscount(extractedDiscount(extractedText));
//        invoiceDTO.setTotalAmount(extractedTotalAmount(extractedText));
//        invoiceDTO.setPaymentTerms(extractedPaymentTerms(extractedText));
//
//        return invoiceDTO;
//    }
//
//    // Regular expression to extract specific fields from text
//    private String extractInvoiceNumber(String text) {
//        return extractTextUsingPattern(text, "Invoice Number: (\\S+)");
//    }
//
//    private String extractInvoiceDate(String text) {
//        return extractTextUsingPattern(text, "Invoice Date: (\\S+)");
//    }
//
//    private VendorDTO extractVendor(String text) {
//        VendorDTO vendor = new VendorDTO();
//        vendor.setName(extractTextUsingPattern(text, "Vendor Name: (\\S+)"));
//        return vendor;
//    }
//
//    private BuyerDTO extractedBuyer(String text) {
//        BuyerDTO buyer = new BuyerDTO();
//        buyer.setName(extractTextUsingPattern(text, "Buyer Name: (\\S+)"));
//        return buyer;
//    }
//
//    private List<LineItemDTO> extractLineItems(String text) {
//        List<LineItemDTO> lineItems = new ArrayList<>();
//        
//        // Regex pattern to match line items: description, quantity, unit price, total price
//        Pattern lineItemPattern = Pattern.compile("(?m)([\\w\\s]+)\\s+(\\d+)\\s+(\\d+\\.\\d{2})\\s+(\\d+\\.\\d{2})");
//        Matcher matcher = lineItemPattern.matcher(text);
//
//        while (matcher.find()) {
//            LineItemDTO lineItem = new LineItemDTO();
//            lineItem.setDescription(matcher.group(1).trim());
//            lineItem.setQuantity(Integer.parseInt(matcher.group(2)));
//            lineItem.setUnitPrice(Double.parseDouble(matcher.group(3)));
//            lineItem.setTotalPrice(Double.parseDouble(matcher.group(4)));
//            lineItems.add(lineItem);
//        }
//
//        return lineItems;
//    }
//
//    private double extractSubtotal(String text) {
//        return parseAmount(extractTextUsingPattern(text, "Subtotal: (\\d+\\.\\d{2})"));
//    }
//
//    private double extractTax(String text) {
//        return parseAmount(extractTextUsingPattern(text, "Tax: (\\d+\\.\\d{2})"));
//    }
//
//    private double extractedDiscount(String text) {
//        return parseAmount(extractTextUsingPattern(text, "Discount: (\\d+\\.\\d{2})"));
//    }
//
//    private double extractedTotalAmount(String text) {
//        return parseAmount(extractTextUsingPattern(text, "Total Amount: (\\d+\\.\\d{2})"));
//    }
//
//    private String extractedPaymentTerms(String text) {
//        return extractTextUsingPattern(text, "Payment Terms: (\\S+)");
//    }
//
//    // Helper method to extract text using regex patterns
//    private String extractTextUsingPattern(String text, String patternStr) {
//        Pattern pattern = Pattern.compile(patternStr);
//        Matcher matcher = pattern.matcher(text);
//        return matcher.find() ? matcher.group(1) : "Unknown";
//    }
//
//    // Helper method to parse amounts
//    private double parseAmount(String amountStr) {
//        try {
//            return Double.parseDouble(amountStr);
//        } catch (NumberFormatException e) {
//            return 0.0;
//        }
//    }
//}
