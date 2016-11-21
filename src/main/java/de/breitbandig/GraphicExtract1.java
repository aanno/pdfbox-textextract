package de.breitbandig;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * https://github.com/torutk/pdfviewer/blob/master/src/pdfviewer/PdfModel.java
 * http://stackoverflow.com/questions/14219276/pdfbox-pagedrawer-draws-outside-the-pdfpagepanel/16545425#16545425
 */
public class GraphicExtract1 {

    private final PDDocument doc;

    private final PDFRenderer renderer;

    public GraphicExtract1(PDDocument doc) {
        this.doc = doc;
        this.renderer = new PDFRenderer(doc);
    }

    public BufferedImage extract(int page) throws IOException {
        BufferedImage image = renderer.renderImage(page);
        return image;
    }

}
