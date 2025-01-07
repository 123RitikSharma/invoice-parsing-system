package service;

import exception.InvoiceParseException;
import dto.InvoiceDTO;
import parser.PdfInvoiceParser;
import parser.DocxInvoiceParser;
import parser.TxtInvoiceParser;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class InvoiceParseService {

    private final PdfInvoiceParser pdfInvoiceParser = new PdfInvoiceParser();
    private final DocxInvoiceParser docxInvoiceParser = new DocxInvoiceParser();
    private final TxtInvoiceParser txtInvoiceParser = new TxtInvoiceParser();

    public InvoiceDTO parseInvoice(File file) throws InvoiceParseException {
        String extension = getFileExtension(file.getName());

        try {
            switch (extension) {
                case "pdf":
                    return pdfInvoiceParser.parsePdf(file);
                case "docx":
                case "doc":
                    return docxInvoiceParser.parseDoc(file);
                case "txt":
                    return txtInvoiceParser.parseTxt(file);
                default:
                    throw new InvoiceParseException("Unsupported file format: " + extension);
            }
        } catch (Exception e) {
            throw new InvoiceParseException("Error parsing file: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
