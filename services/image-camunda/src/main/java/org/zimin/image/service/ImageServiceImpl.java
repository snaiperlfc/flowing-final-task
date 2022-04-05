package org.zimin.image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zimin.image.model.Fact;
import org.zimin.image.rest.RestClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
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
        Font font = new Font("Roboto", Font.BOLD, 18);
        Font newFont;

        FontMetrics ruler = g.getFontMetrics(font);
        GlyphVector vector = font.createGlyphVector(ruler.getFontRenderContext(), fact);

        Shape outline = vector.getOutline(0, 0);

        //Add fact
        double expectedWidth = outline.getBounds().getWidth();
        double expectedHeight = outline.getBounds().getHeight();

        boolean textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;

        double widthBasedFontSize = (font.getSize2D() * image.getWidth()) / expectedWidth;
        double heightBasedFontSize = (font.getSize2D() * image.getHeight()) / expectedHeight;

        double newFontSize = Math.min(widthBasedFontSize, heightBasedFontSize);
        newFont = font.deriveFont(font.getStyle(), (float) newFontSize);

        AttributedString attributedText = new AttributedString(fact);
        attributedText.addAttribute(TextAttribute.FONT, textFits ? font : newFont);
        attributedText.addAttribute(TextAttribute.FOREGROUND, Color.green);

        FontMetrics metrics = g.getFontMetrics(textFits ? font : newFont);
        int positionX = 1;
        int positionY = metrics.getAscent();
//        int positionX = (image.getWidth() - metrics.stringWidth(fact.getFact())) / 2;
//        int positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

//        g.drawString(attributedText.getIterator(), positionX, positionY);
        paintTextWithOutline(g, attributedText.getIterator(), fact, positionX, positionY, image.getWidth());

        //Add email
        expectedWidth = outline.getBounds().getWidth();
        expectedHeight = outline.getBounds().getHeight();

        textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;

        widthBasedFontSize = (font.getSize2D() * image.getWidth()) / expectedWidth;
        heightBasedFontSize = (font.getSize2D() * image.getHeight()) / expectedHeight;

        newFontSize = Math.min(widthBasedFontSize, heightBasedFontSize);
        newFont = font.deriveFont(font.getStyle(), (float) newFontSize);

        attributedText = new AttributedString(email);
        attributedText.addAttribute(TextAttribute.FONT, textFits ? font : newFont);
        attributedText.addAttribute(TextAttribute.FOREGROUND, Color.RED);

        metrics = g.getFontMetrics(textFits ? font : newFont);
//        positionX = //(image.getWidth() - metrics.stringWidth(email));
        positionY = (image.getHeight() - metrics.getHeight()) + metrics.getAscent();

//        g.drawString(attributedText.getIterator(), positionX, positionY);
        paintTextWithOutline(g, attributedText.getIterator(), email, positionX, positionY, image.getWidth());
    }

    public void paintTextWithOutline(Graphics g, AttributedCharacterIterator characterIterator,
                                     String text, int positionX, int positionY, int imageWidth) {
        Color outlineColor = Color.black;
        Color fillColor = (Color) characterIterator.getAttribute(TextAttribute.FOREGROUND); //Color.black;
        Font font = (Font) characterIterator.getAttribute(TextAttribute.FONT);
//
//        BasicStroke outlineStroke = new BasicStroke(2.0f);

        FontRenderContext frc =
                new FontRenderContext(null, true, true);
        Rectangle2D r2D = font.getStringBounds(text, frc);

        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // remember original settings
            Color originalColor = g2.getColor();
            Stroke originalStroke = g2.getStroke();
            RenderingHints originalHints = g2.getRenderingHints();

            // create a glyph vector from your text
            GlyphVector glyphVector = font.createGlyphVector(g2.getFontRenderContext(), text);
            // get the shape object
            Shape textShape = glyphVector.getOutline();

            AffineTransform at = new AffineTransform();
            at.translate(positionX, positionY);
            Shape transformedShape = at.createTransformedShape(textShape);

            // activate anti aliasing for text rendering (if you want it to look nice)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2.setColor(outlineColor);
//            g2.setStroke(outlineStroke);
            g.fillRect(0, positionY - g.getFontMetrics(font).getAscent(),
                    imageWidth, (int) r2D.getHeight());
//            g2.translate(positionX, positionY);
//            g2.draw(transformedShape); // draw outline

            g2.setColor(fillColor);
            g2.fill(transformedShape); // fill the shape

            // reset to original settings after painting
            g2.setColor(originalColor);
            g2.setStroke(originalStroke);
            g2.setRenderingHints(originalHints);
        }
    }

}
