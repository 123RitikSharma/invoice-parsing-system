package service;

import dto.InvoiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parser.*;

import java.io.InputStream;

@Service
public class InvoiceParseService {

    private final PdfInvoiceParser pdfInvoiceParser;
    private final DocxInvoiceParser docxInvoiceParser;
    private final TxtInvoiceParser txtInvoiceParser;
    private final ImageInvoiceParser imageInvoiceParser;

    // Constructor-based dependency injection for better testability
    @Autowired
    public InvoiceParseService(PdfInvoiceParser pdfInvoiceParser,
                               DocxInvoiceParser docxInvoiceParser,
                               TxtInvoiceParser txtInvoiceParser,
                               ImageInvoiceParser imageInvoiceParser) {
        this.pdfInvoiceParser = pdfInvoiceParser;
        this.docxInvoiceParser = docxInvoiceParser;
        this.txtInvoiceParser = txtInvoiceParser;
        this.imageInvoiceParser = imageInvoiceParser;
    }

    public InvoiceDTO parseFile(InputStream inputStream, String fileType) throws Exception {
        InvoiceParser parser;

        switch (fileType.toLowerCase()) {
            case "pdf":
                parser = pdfInvoiceParser;
                break;
            case "docx":
            case "doc":
                parser = docxInvoiceParser;
                break;
            case "txt":
                parser = txtInvoiceParser;
                break;
            case "jpg":
            case "png":
                parser = imageInvoiceParser;
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        try {
            return parser.parse(inputStream);  // Use the InputStream here
        } catch (Exception e) {
            // Log and rethrow a more descriptive exception if needed
            throw new Exception("Error parsing file of type " + fileType, e);
        }
    }
}
