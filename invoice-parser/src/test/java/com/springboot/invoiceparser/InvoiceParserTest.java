package com.springboot.invoiceparser;

import org.junit.jupiter.api.Test;
import parser.DocxInvoiceParser;
import parser.ImageInvoiceParser;
import parser.PdfInvoiceParser;
import parser.TxtInvoiceParser;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceParserTest {

    // Helper method to test parsing
    private void testParser(String filePath, Object parser) {
        try {
            assertTrue(parser instanceof PdfInvoiceParser ||
                           parser instanceof DocxInvoiceParser ||
                           parser instanceof TxtInvoiceParser ||
                           parser instanceof ImageInvoiceParser,
                       "Invalid parser instance!");

            Map<String, String> parsedData = null;

            if (parser instanceof PdfInvoiceParser) {
                parsedData = ((PdfInvoiceParser) parser).parse(new File(filePath));
            } else if (parser instanceof DocxInvoiceParser) {
                parsedData = ((DocxInvoiceParser) parser).parse(new File(filePath));
            } else if (parser instanceof TxtInvoiceParser) {
                parsedData = ((TxtInvoiceParser) parser).parse(new File(filePath));
            } else if (parser instanceof ImageInvoiceParser) {
                parsedData = ((ImageInvoiceParser) parser).parse(new File(filePath));
            }

            assertNotNull(parsedData, "Parsed data should not be null");
            assertFalse(parsedData.isEmpty(), "Parsed data should not be empty");

            // Log parsed data for debugging
            System.out.println("Parsed Data: " + parsedData);

        } catch (Exception e) {
            fail("Error parsing file: " + e.getMessage());
        }
    }

    @Test
    void testPdfParsing() {
        String filePath = "src/test/resources/sample-invoice.pdf";
        testParser(filePath, new PdfInvoiceParser());
    }

    @Test
    void testDocxParsing() {
        String filePath = "src/test/resources/sample-invoice.docx";
        testParser(filePath, new DocxInvoiceParser());
    }

    @Test
    void testTxtParsing() {
        String filePath = "src/test/resources/sample-invoice.txt";
        testParser(filePath, new TxtInvoiceParser());
    }

    @Test
    void testImageParsing() {
        String filePath = "src/test/resources/sample-invoice.png";
        testParser(filePath, new ImageInvoiceParser());
    }
}
