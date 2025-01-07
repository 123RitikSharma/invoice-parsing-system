package parser;

import java.io.File;
import java.util.Map;

public interface InvoiceParser {
    Map<String, String> parse(File file) throws Exception;
}
