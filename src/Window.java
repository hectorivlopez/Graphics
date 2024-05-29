import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Window extends JFrame  {
    private int titleBarHeight;
    private int width;
    private int height;
    public AnimatedScreen bgPanel;
    private BufferedImage buffer;
    public int x;
    public int y;

    public Window() {


        initComponents();



        //buffer = new BufferedImage(bgPanel.getWidth(), bgPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Add ComponentAdapter to handle frame resizing
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();

            }
        });

        setVisible(true);


    }

    private void updateSize() {
        width = getWidth();
        height = getHeight() - titleBarHeight ;

        // Set new bounds for bgPanel
        bgPanel.setBounds(0, 0, width, height);
        bgPanel.resize(width, height);
        bgPanel.repaint();
    }

    public void grid() {
        Color gray2 = new Color(68, 75, 114);
        Color gray = new Color(49, 55, 91);

        // Vertical Lines
        for (int i = 0; i < width; i++) {
            if (i % 50 == 0) {
                for (int j = 0; j < height; j++) {
                    if (i % 100 == 0) {
                        this.buffer.setRGB(i, j, gray2.getRGB());
                    } else {
                        this.buffer.setRGB(i, j, gray.getRGB());
                    }
                }
            }
        }

        // Horizontal Lines
        for (int j = 0; j < height; j++) {
            if (j % 50 == 0) {
                for (int i = 0; i < width; i++) {
                    if (j % 100 == 0) {
                        this.buffer.setRGB(i, j, gray2.getRGB());
                    } else {
                        this.buffer.setRGB(i, j, gray.getRGB());
                    }
                }
            }
        }
    }


    private void initComponents() {
        titleBarHeight = 28;
        width = 1000;
        height = 501;

        setSize(width, height + titleBarHeight);
        setLocationRelativeTo(null);
        setBackground(new Color(39, 43, 76));


        bgPanel = new AnimatedScreen(width, height);
        bgPanel.setBounds(0, 0, width, height);
        bgPanel.setLayout(null);
        bgPanel.setBackground(new Color(39, 43, 76));
        add(bgPanel);


    }

    public void paint(Graphics g) {
        super.paint(g);
       /* grid();*/


        /*Line.drawLine(0, 0, 200, 100, buffer);
        Line.drawLine(1000, 0, 800, 100, buffer);
        Line.drawLine(0, 500, 200, 400, buffer);
        Line.drawLineBresenham(1000, 500, 800, 400, buffer);

        Shape.drawRectangle(200, 100, 800, 400, Color.white, buffer);
        Shape.drawCircle(500, 250, 100, Color.white, buffer);
        Shape.drawCirclePolar(500, 250, 200, null, buffer);
        Shape.drawCirclePolar(500, 250, 400, Color.white, buffer);*/

      /*  Line.drawLineBresenham(800, 100, 100, 400, buffer);
        Line.drawLineBresenham(100, 100, 800, 400, buffer);
        Line.drawLineBresenham(100, 500, 800, 200, buffer);
        Line.drawLineBresenham(800, 500, 100, 200, buffer);
        Line.drawLineBresenham(100, 100, 200, 400, buffer);
        Line.drawLineBresenham(400, 100, 300, 400, buffer);
        Line.drawLineBresenham(500, 400, 400, 100, buffer);
        Line.drawLineBresenham(500, 400, 600, 100, buffer);

        Line.drawLineBresenham(500, 400, 500, 100, buffer);
        Line.drawLineBresenham(400, 100, 400, 400, buffer);
        Line.drawLineBresenham(100, 100, 400, 100, buffer);
        Line.drawLineBresenham(700, 100, 500, 100, buffer);

        Line.drawLineBresenham(700, 100, 500, 100, buffer);*/


       /* Shape.drawCircle(500, 250, 390, Color.green, buffer);
        Shape.drawCircle(500, 250, 190, Color.green, buffer);*/

        /*Shape.fillCircle(500,250,400, Color.red, buffer);

        Shape.fillRhombus(500, 250, 200, 100, Color.green, buffer);
        */

    }

}
