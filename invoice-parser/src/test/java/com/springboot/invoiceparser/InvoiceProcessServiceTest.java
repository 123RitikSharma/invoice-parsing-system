package com.springboot.invoiceparser;

import dto.*;
import parser.*;
import service.InvoiceParseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvoiceParseServiceTest {

    @InjectMocks
    private InvoiceParseService invoiceParseService;

    @Mock
    private PdfInvoiceParser pdfInvoiceParser;

    @Mock
    private DocxInvoiceParser docInvoiceParser;

    @Mock
    private TxtInvoiceParser txtInvoiceParser;

    @Mock
    private ImageInvoiceParser imageInvoiceParser;  // Add the image parser mock

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testParsePDFInvoice() throws Exception {
        // Mock PDF parser behavior
        URL pdfUrl = getClass().getClassLoader().getResource("invoice_Maria Zettner_24429.pdf");
        if (pdfUrl == null) {
            throw new IllegalArgumentException("File not found!");
        }
        InputStream pdfInputStream = pdfUrl.openStream(); // Open InputStream from URL
        InvoiceDTO mockInvoice = createMockExtractedInvoice();
        when(pdfInvoiceParser.parse(pdfInputStream)).thenReturn(mockInvoice);

        // Parse the file and assert the extracted data
        InvoiceDTO result = invoiceParseService.parseFile(pdfInputStream, "pdf");
        printExtractedFields(result);
        assertNotNull(result);
    }

    @Test
    void testParseDocInvoice() throws Exception {
        // Mock DOC/DOCX parser behavior
        URL docUrl = getClass().getClassLoader().getResource("sample-invoice.docx");
        if (docUrl == null) {
            throw new IllegalArgumentException("File not found!");
        }
        InputStream docInputStream = docUrl.openStream(); // Open InputStream from URL
        InvoiceDTO mockInvoice = createMockExtractedInvoice();
        when(docInvoiceParser.parse(docInputStream)).thenReturn(mockInvoice);

        // Parse the file and assert the extracted data
        InvoiceDTO result = invoiceParseService.parseFile(docInputStream, "docx");
        printExtractedFields(result);
        assertNotNull(result);
    }

    @Test
    void testParseTxtInvoice() throws Exception {
        // Mock TXT parser behavior
        URL txtUrl = getClass().getClassLoader().getResource("invoice1.txt");
        if (txtUrl == null) {
            throw new IllegalArgumentException("File not found!");
        }
        InputStream txtInputStream = txtUrl.openStream(); // Open InputStream from URL
        InvoiceDTO mockInvoice = createMockExtractedInvoice();
        when(txtInvoiceParser.parse(txtInputStream)).thenReturn(mockInvoice);

        // Parse the file and assert the extracted data
        InvoiceDTO result = invoiceParseService.parseFile(txtInputStream, "txt");
        printExtractedFields(result);
        assertNotNull(result);
    }

//    // Test for JPG Image
//    @Test
//    void testParseJPGImage() throws Exception {
//        // Mock Image parser behavior (JPG)
//        URL jpgUrl = getClass().getClassLoader().getResource("Invoice.png");
//        if (jpgUrl == null) {
//            throw new IllegalArgumentException("File not found!");
//        }
//        InputStream jpgInputStream = jpgUrl.openStream(); // Open InputStream from URL
//        InvoiceDTO mockInvoice = createMockExtractedInvoice();
//        when(imageInvoiceParser.parse(jpgInputStream)).thenReturn(mockInvoice);
//
//        // Parse the file and assert the extracted data
//        InvoiceDTO result = invoiceParseService.parseFile(jpgInputStream, "jpg");
//        printExtractedFields(result);
//        assertNotNull(result);
//    }

    // Test for PNG Image
    @Test
    void testParsePNGImage() throws Exception {
        // Mock Image parser behavior (PNG)
        URL pngUrl = getClass().getClassLoader().getResource("Invoice.png");
        if (pngUrl == null) {
            throw new IllegalArgumentException("File not found!");
        }
        InputStream pngInputStream = pngUrl.openStream(); // Open InputStream from URL
        InvoiceDTO mockInvoice = createMockExtractedInvoice();
        when(imageInvoiceParser.parse(pngInputStream)).thenReturn(mockInvoice);

        // Parse the file and assert the extracted data
        InvoiceDTO result = invoiceParseService.parseFile(pngInputStream, "png");
        printExtractedFields(result);
        assertNotNull(result);
    }

    private void printExtractedFields(InvoiceDTO invoice) {
        System.out.println("=== Extracted Fields ===");
        if (invoice.getInvoiceNumber() != null)
            System.out.println("Invoice Number: " + invoice.getInvoiceNumber());
        if (invoice.getInvoiceDate() != null)
            System.out.println("Invoice Date: " + invoice.getInvoiceDate());
        if (invoice.getVendor() != null)
            System.out.println("Vendor Name: " + invoice.getVendor().getName());
        if (invoice.getBuyer() != null)
            System.out.println("Buyer Name: " + invoice.getBuyer().getName());
        if (invoice.getLineItems() != null && !invoice.getLineItems().isEmpty()) {
            invoice.getLineItems().forEach(item -> {
                System.out.println("Line Item Description: " + item.getDescription());
                System.out.println("Line Item Quantity: " + item.getQuantity());
                System.out.println("Line Item Unit Price: " + item.getUnitPrice());
                System.out.println("Line Item Total Price: " + item.getTotalPrice());
            });
        }
        if (invoice.getSubtotal() > 0)
            System.out.println("Subtotal: " + invoice.getSubtotal());
        if (invoice.getTax() > 0)
            System.out.println("Tax: " + invoice.getTax());
        if (invoice.getDiscount() > 0)
            System.out.println("Discount: " + invoice.getDiscount());
        if (invoice.getTotalAmount() > 0)
            System.out.println("Total Amount: " + invoice.getTotalAmount());
        if (invoice.getPaymentTerms() != null)
            System.out.println("Payment Terms: " + invoice.getPaymentTerms());
        System.out.println("=========================");
    }

    private InvoiceDTO createMockExtractedInvoice() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber("INV12345");
        invoiceDTO.setInvoiceDate("2025-01-01");
        invoiceDTO.setPaymentTerms("Net 30");

        VendorDTO vendor = new VendorDTO();
        vendor.setName("Vendor Inc.");
        invoiceDTO.setVendor(vendor);

        BuyerDTO buyer = new BuyerDTO();
        buyer.setName("Buyer Corp.");
        invoiceDTO.setBuyer(buyer);

        return invoiceDTO;
    }
}
