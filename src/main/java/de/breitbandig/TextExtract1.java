package de.breitbandig;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;

/**
 * http://stackoverflow.com/questions/19770987/how-to-extract-bold-text-from-pdf-using-pdfbox
 */
public class TextExtract1 {

    private final PDDocument doc;

    public TextExtract1(PDDocument doc) {
        this.doc = doc;
    }

    public void extract(int from, int to) throws IOException {
        // PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        // stripper.setSortByPosition(true);
        // PDFTextStripper stripper = new PDFTextStripper();
        PDFTextStripper stripper = new EnhancedTextStripper();

        // stripper.setAddMoreFormatting(true);
        stripper.setStartPage(from);
        stripper.setEndPage(to);

        // VERY IMPORTANT SETTINGS! (tp)
        stripper.setSortByPosition(false);
        stripper.setShouldSeparateByBeads(false);

        /*
        stripper.setArticleEnd("</article>");
        stripper.setArticleStart("<article>");
        stripper.setParagraphEnd("</p>");
        stripper.setPageStart("<p>");
         */

        String st = stripper.getText(doc);
        System.out.println(st);
    }

}
