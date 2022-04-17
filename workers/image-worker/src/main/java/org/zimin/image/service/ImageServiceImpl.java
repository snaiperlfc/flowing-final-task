package org.zimin.image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zimin.image.rest.RestClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedString;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Value("${image.cat.service.url}")
    private String imageCatUrl;

    final
    RestClient restClient;

    public ImageServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<String> getRandomImage() {
        return restClient.get(imageCatUrl);
    }

    public void mergeImageFact(String imageUrl, String fact, String email, ByteArrayOutputStream outputStream) throws IOException {
        final BufferedImage image = ImageIO.read(new URL(imageUrl));
        Graphics g = image.getGraphics();

        addText2Image(g, fact, email, image);

        g.dispose();
        ImageIO.write(image, "png", outputStream);
    }

    private void addText2Image(Graphics g, String fact, String email, BufferedImage image) {
        Font font = new Font("Roboto", Font.BOLD, 16);

        //Add fact
        FontMetrics metrics = g.getFontMetrics(font);
        int positionX = 1;
        int positionY = metrics.getAscent();

        JTextArea textArea = new JTextArea(fact);
        textArea.setFont(font.deriveFont(15f));
        textArea.setSelectedTextColor(Color.black);
        textArea.setForeground(Color.green);
        textArea.getCaret().setSelectionVisible(true);
        textArea.selectAll();
        textArea.setOpaque(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setBounds(positionX, positionY, image.getWidth(), image.getHeight());
        textArea.paint(g);

        //Add email
        AttributedString attributedText = new AttributedString(email);
        attributedText.addAttribute(TextAttribute.FONT, font);
        attributedText.addAttribute(TextAttribute.FOREGROUND, Color.RED);

        metrics = g.getFontMetrics(font);
        positionX = (image.getWidth() - metrics.stringWidth(email));
        positionY = (image.getHeight() - metrics.getHeight()) + metrics.getAscent();

        g.drawString(attributedText.getIterator(), positionX, positionY);
    }

}
