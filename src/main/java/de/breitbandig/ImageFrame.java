package de.breitbandig;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageFrame {

    private BufferedImage image;

    public ImageFrame(BufferedImage image) {
        this.image = image;
    }

    public void createAndShowGUI() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                Graphics g2 = g.create();
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize(){
                return new Dimension(image.getWidth(), image.getHeight());
            }
        };

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
