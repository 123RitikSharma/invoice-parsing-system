package parser;

import dto.InvoiceDTO;
import java.io.File;
import java.io.InputStream;

public interface InvoiceParser {
    InvoiceDTO parse(InputStream inputStream) throws Exception;
}
