package de.breitbandig;

import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * http://stackoverflow.com/questions/6596719/jframe-image-display-at-frame-resize
 */
public class GraphicExtractMain {

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
        GraphicExtract1 extract1 = new GraphicExtract1(document);
        final BufferedImage image = extract1.extract(16);

        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                ImageFrame frame = new ImageFrame(image);
                frame.createAndShowGUI();
            }
        });

        // image = extract1.extract(24);
    }

}
