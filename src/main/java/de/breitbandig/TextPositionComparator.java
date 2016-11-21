package de.breitbandig;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.TextPosition;

import java.text.Collator;
import java.util.Comparator;
import java.util.Objects;

public class TextPositionComparator implements Comparator<TextPosition> {

    private static final TextPositionComparator INSTANCE = new TextPositionComparator();

    private Collator collator = Collator.getInstance();

    public static TextPositionComparator getInstance() {
        return INSTANCE;
    }

    private TextPositionComparator() {
    }

    public int compare(TextPosition o1, TextPosition o2) {
        // int result = Float.compare(o1.getYScale(), o2.getYScale());
        int result = compareFloat(o1.getYScale(), o2.getYScale(), 5);
        if (result == 0) {
            // result = Float.compare(o1.getXScale(), o2.getXScale());
            result = compareFloat(o1.getXScale(), o2.getXScale(), 5);
            if (result == 0) {
                PDFont f1 = o1.getFont();
                PDFont f2 = o2.getFont();
                if (f1 != null && f2 != null) {
                    PDFontDescriptor fd1 = f1.getFontDescriptor();
                    PDFontDescriptor fd2 = f2.getFontDescriptor();
                    if (fd1 != null && fd2 != null) {
                        result = Objects.compare(fd1.getFontFamily(), fd2.getFontFamily(), collator);
                        if (result == 0) {
                            result = Objects.compare(fd1.getFontName(), fd2.getFontName(), collator);
                            if (result == 0) {
                                // result = Float.compare(fd1.getFontWeight(), fd2.getFontWeight());
                                result = compareFloat(fd1.getFontWeight(), fd2.getFontWeight(), 10);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private int compareFloat(float f1, float f2, int deltaPercent) {
        float result = f1 - f2;
        if (Math.abs(result) <= (Math.abs(f1) + Math.abs(f2))/200 * deltaPercent) {
            result = 0;
        }
        return (int) result;
    }

}
