package com.springboot.invoiceparser;

import parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfTextExtractorTest {

    @Autowired
    private PdfTextExtractor pdfTextExtractor;

    @Test
    void testExtractText() {
        File testPdf = new File("\"C:\\Users\\Ritik\\Downloads\\Sample-Pdf-invoices-main\\Sample-Pdf-invoices-main\\1000+ PDF_Invoice_Folder\\1000+ PDF_Invoice_Folder\\invoice_Liz Thompson_14130.pdf\"");

        try {
            String extractedText = pdfTextExtractor.extractText(testPdf);

            assertNotNull(extractedText, "Extracted text should not be null");
            assertFalse(extractedText.isEmpty(), "Extracted text should not be empty");
            
            // Add specific assertions based on expected content
            assertTrue(extractedText.contains("Invoice Number"), "Extracted text should contain 'Invoice Number'");
            assertTrue(extractedText.contains("2025-01-01"), "Extracted text should contain the expected date");

            // Log the output for debugging purposes
            System.out.println("Extracted Text: \n" + extractedText);

        } catch (IOException e) {
            fail("Exception during PDF text extraction: " + e.getMessage());
        }
    }
}










//import parser.TxtInvoiceParser;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class InvoiceParserTest {
//
//    // Helper method to test parsing
//    private void testParser(String filePath, Object parser) throws IOException {
//        assertTrue(parser instanceof PdfInvoiceParser ||
//		               parser instanceof DocxInvoiceParser ||
//		               parser instanceof TxtInvoiceParser,
//		           "Invalid parser instance!");
//
//		InvoiceDTO parsedData = null;
//
//		if (parser instanceof PdfInvoiceParser) {
//		    parsedData = ((PdfInvoiceParser) parser).parsePdf(new File(filePath));
//		} else if (parser instanceof DocxInvoiceParser) {
//		    parsedData = ((DocxInvoiceParser) parser).parseDoc(new File(filePath));
//		} else if (parser instanceof TxtInvoiceParser) {
//		    parsedData = ((TxtInvoiceParser) parser).parseTxt(new File(filePath));
//		}
//
//		assertNotNull(parsedData, "Parsed data should not be null");
//		assertNotNull(parsedData.getInvoiceNumber(), "Invoice number should not be null");
//		assertNotNull(parsedData.getInvoiceDate(), "Invoice date should not be null");
//		assertFalse(parsedData.getLineItems().isEmpty(), "Line items should not be empty");
//
//		// Log parsed data for debugging
//		System.out.println("Parsed Data: " + parsedData);
//    }
//
//    @Test
//    void testPdfParsing() throws IOException {
//        String filePath = "C:\\Users\\Ritik\\Downloads\\sample-invoice.pdf";
//        testParser(filePath, new PdfInvoiceParser());
//    }
//
//    @Test
//    void testDocxParsing() throws IOException {
//        String filePath = "src/test/resources/sample-invoice.docx";
//        testParser(filePath, new DocxInvoiceParser());
//    }
//
//    @Test
//    void testTxtParsing() throws IOException {
//        String filePath = "src/test/resources/sample-invoice.txt";
//        testParser(filePath, new TxtInvoiceParser());
//    }
//}
