import graphics.Draw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static graphics.Draw3d.*;

public class Window extends JFrame {
    private int width;
    private int height;
    private int titleBarHeight;
    private JPanel bgPanel;
    private CustomPanel canvasPanel;
    boolean firstResize;
    public boolean isMacOS;

    public Window() {
        this.isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMacOS) {
            final JRootPane rootPane = this.getRootPane();
            rootPane.putClientProperty("apple.awt.fullWindowContent", true);
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        titleBarHeight = 28;
        if (isMacOS) titleBarHeight = 0;

        width = 1001;
        height = 501;

        setSize(width, height + titleBarHeight + 40);
        setLocationRelativeTo(null);
        setLayout(null);
        firstResize = true;
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();

            }
        });

        bgPanel = new JPanel();
        bgPanel.setBounds(0, 0, width, height);
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        canvasPanel = new CustomPanel(width, height);
        canvasPanel.setBounds(0, 0, width, height);
        canvasPanel.setLayout(null);

        bgPanel.add(canvasPanel);

    }

    private void updateSize() {
        width = getWidth();
        height = getHeight() - titleBarHeight;
        setBackground(new Color(39, 43, 76));

        bgPanel.setBounds(0, 0, width, height);

        bgPanel.repaint();

        // Set new bounds for canvasPanel
        canvasPanel.setBounds(0, 0, width, height - 40);
        canvasPanel.resize(width, height - 40);
        canvasPanel.repaint();

        firstResize = false;
    }

    // ---------------------------------------- Panel ----------------------------------------
    public class CustomPanel extends JPanel {
        public BufferedImage buffer;
        public int[] director;
        public int[] origin2D;
        public int width;
        public int height;

        public double scale;
        public double angle;
        public int x;
        public boolean growing;
        public boolean toRight;

        public CustomPanel(int width, int height) {
            this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.width = width;
            this.height = height;

            // Transformations
            this.scale = 1;
            this.angle = 0;
            this.x = 0;
            this.growing = false;
            this.toRight = true;

            CustomThread scaleThread = new CustomThread(() -> {
                if (growing) {
                    scale += 0.1;
                    if (scale >= 15) growing = false;
                }
                else {
                    scale -= 0.1;
                    if (scale <= 1) growing = true;
                }
            }, 20, () -> false);
            scaleThread.start();

            CustomThread rotateThread = new CustomThread(() -> {
                angle += 0.1;
                if (angle >= 2 * Math.PI) {
                    angle = 0;
                }

            }, 100, () -> false);
            rotateThread.start();

            CustomThread moveThread = new CustomThread(() -> {
                if (toRight) {
                    x ++;
                    if (x >= 50) toRight = false;
                }
                else {
                    x --;
                    if (x <= -50) toRight = true;
                }
            }, 10, () -> false);
            moveThread.start();
        }

        public void resize(int width, int height) {
            this.width = width;
            this.height = height;

            this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        // ------------------------------ Guides ------------------------------
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

        public void paint(Graphics g) {
            super.paint(g);

            Graphics gBuffer = buffer.getGraphics();
            gBuffer.setColor(new Color(39, 43, 76));
            gBuffer.fillRect(0, 0, this.getWidth(), this.getHeight());
            grid();

            g.drawImage(buffer, 0, 0, this);
        }
    }

    // ---------------------------------------- Execution ----------------------------------------
    public static void main(String[] args) {


    }
}
