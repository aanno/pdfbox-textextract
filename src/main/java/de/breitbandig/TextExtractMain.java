package de.breitbandig;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

public class TextExtractMain {

    public static void main(String[] args) throws Exception {
        // File in = new File("/home/tpasch/Downloads/Secrets_(6249342).pdf");
        File in = new File("/home/tpasch/Downloads/DeprecatingObservers2012.pdf");
        PDDocument document = PDDocument.load(in);
        /*
        document.getClass();
        if (document.isEncrypted()) {
            try {
                document.decrypt("");
            } catch (InvalidPasswordException e) {
                System.err.println("Error: Document is encrypted with a password.");
                System.exit(1);
            }
        }
         */
        TextExtract1 extract1 = new TextExtract1(document);
        extract1.extract(15, 16);
    }
}
