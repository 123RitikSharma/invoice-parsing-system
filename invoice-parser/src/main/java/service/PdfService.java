package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    private final PdfTextExtractor pdfTextExtractor;

    @Autowired
    public PdfService(PdfTextExtractor pdfTextExtractor) {
        this.pdfTextExtractor = pdfTextExtractor;
    }

    /**
     * Extract text from a given PDF file.
     *
     * @param filePath Path to the PDF file.
     * @return Extracted text.
     * @throws IOException If the PDF file cannot be read.
     */
    public String extractPdfText(String filePath) throws IOException {
        File pdfFile = new File(filePath);
        return pdfTextExtractor.extractText(pdfFile);
    }
}

