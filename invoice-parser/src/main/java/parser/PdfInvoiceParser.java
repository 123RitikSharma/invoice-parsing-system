package parser;

import dto.InvoiceDTO;
import dto.LineItemDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

@Component
public class PdfInvoiceParser {

    public InvoiceDTO parsePdf(File pdfFile) throws IOException {
        String text = extractTextFromPdf(pdfFile);
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setInvoiceNumber(extractInvoiceNumber(text));
        invoice.setInvoiceDate(extractInvoiceDate(text));
        invoice.setLineItems(extractLineItems(text));
        return invoice;
    }

    private String extractTextFromPdf(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractInvoiceNumber(String text) {
        Pattern pattern = Pattern.compile("Invoice Number:\\s*(\\S+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractInvoiceDate(String text) {
        Pattern pattern = Pattern.compile("Invoice Date:\\s*(\\S+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private List<LineItemDTO> extractLineItems(String text) {
        List<LineItemDTO> lineItems = new ArrayList<>();
        Pattern pattern = Pattern.compile("([A-Za-z\\s]+)\\s+(\\d+)\\s+(\\d+\\.\\d{2})\\s+(\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            LineItemDTO item = new LineItemDTO();
            item.setDescription(matcher.group(1));
            item.setQuantity(Integer.parseInt(matcher.group(2)));
            item.setUnitPrice(Double.parseDouble(matcher.group(3)));
            item.setTotalPrice(Double.parseDouble(matcher.group(4)));
            lineItems.add(item);
        }
        return lineItems;
    }
}
