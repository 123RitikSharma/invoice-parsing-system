package parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PdfTextExtractor {

    /**
     * Extracts text content from a PDF file.
     *
     * @param pdfFile the PDF file to extract text from
     * @return the extracted text as a String
     * @throws IOException if there is an issue reading the PDF file
     */
    public String extractText(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        }
    }
}
