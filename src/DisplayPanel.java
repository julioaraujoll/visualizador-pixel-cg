import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


//  Painel de dispositivo grafico (tela limpa, sem reticulados)

/** Simula o display: um buffer de pixels reais, sem grid, fundo solido. */

public class DisplayPanel extends JPanel {
    private BufferedImage buffer;
    private int width, height;

    DisplayPanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        clear();
    }

    void resizeCanvas(int w, int h) {
        if (w != this.width || h != this.height) {
            this.width = w;
            this.height = h;
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(w, h));
            clear();
        }
    }

    /** Limpa o display (background totalmente limpo, sem reticulados). */
    void clear() {
        Graphics2D g = buffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.dispose();
    }

    /** drawPixel: acende UM unico pixel na posicao (x,y) com a cor dada. */
    void drawPixel(int x, int y, Color color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            buffer.setRGB(x, y, color.getRGB());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buffer, 0, 0, null);
    }
}