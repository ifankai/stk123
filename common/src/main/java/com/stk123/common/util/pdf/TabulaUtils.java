package com.stk123.common.util.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;

import java.io.File;
import java.io.IOException;

/**
 * java -jar web/WEB-INF/lib/tabula-1.0.4-jar-with-dependencies.jar --help
 */
public class TabulaUtils {

    public static void main(String[] args) {

    }

    public static Page getPage(String path, int pageNumber) throws IOException {
        ObjectExtractor oe = null;
        try {
            PDDocument document = PDDocument.load(new File(path));
            oe = new ObjectExtractor(document);
            Page page = oe.extract(pageNumber);
            return page;
        } finally {
            if (oe != null)
                oe.close();
        }
    }
}
