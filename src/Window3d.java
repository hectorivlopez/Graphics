import graphics.Draw;
import graphics.Transformations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static graphics.Draw3d.*;

public class Window3d extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private int width;
    private int height;
    private int titleBarHeight;
    private JPanel bgPanel;
    private CustomPanel canvasPanel;
    private JPanel bottomBar;
    public JLabel directorLabel;
    public int xPressed, yPressed;
    public int xDirInit, yDirInit;
    boolean firstResize;
    public boolean isMacOS;

    public Window3d() {
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
        canvasPanel.addMouseListener(this);
        canvasPanel.addMouseMotionListener(this);
        canvasPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                canvasPanel.director[2] += notches;
                canvasPanel.repaint();
                directorLabel.setText("P(" + canvasPanel.director[0] + ", " + canvasPanel.director[1] + ", " + canvasPanel.director[2] + ")");
            }
        });
        bgPanel.add(canvasPanel);


    }

    private void updateSize() {
        width = getWidth();
        height = getHeight() - titleBarHeight;
        setBackground(new Color(39, 43, 76));

        bgPanel.setBounds(0, 0, width, height);
        if (!firstResize) {
            bottomBar.remove(directorLabel);
            bgPanel.remove(bottomBar);
        }

        bottomBar = new JPanel();
        bottomBar.setLayout(null);
        bottomBar.setBounds(0, height - 40, this.width, 40);
        bottomBar.setBackground(new Color(30, 32, 47));
        bgPanel.add(bottomBar);

        directorLabel = new JLabel();
        directorLabel.setBounds(0, 0, this.width, 40);
        directorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        directorLabel.setVerticalAlignment(SwingConstants.CENTER);
        directorLabel.setForeground(Color.white);
        directorLabel.setText("P(" + canvasPanel.director[0] + ", " + canvasPanel.director[1] + ", " + canvasPanel.director[2] + ")");
        bottomBar.add(directorLabel);
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

            this.director = new int[]{0, 0, 400};

            this.origin2D = new int[]{width / 2, height / 2};

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
                angle += 0.01;
                if (angle >= 2 * Math.PI) {
                    angle = 0;
                }

            }, 10, () -> false);
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
            origin2D = new int[]{width / 2, height / 2};
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

        public void axis(int[] director, double scale, BufferedImage buffer) {
            int[][] points = new int[][]{
                    new int[]{0, 0, 0},
                    new int[]{500, 0, 0},
                    new int[]{-500, 0, 0},
                    new int[]{0, 500, 0},
                    new int[]{0, -500, 0},
                    new int[]{0, 0, 500},
                    new int[]{0, 0, -500},
            };
            int[][] projectedPoints = projection(points, director, "oblique");

            int[] xPoints = projectedPoints[0];
            int[] yPoints = projectedPoints[1];

            for (int i = 1; i <= 3; i++) {
                Draw.drawLine(xPoints[(i * 2) - 1], yPoints[(i * 2) - 1], xPoints[i * 2], yPoints[i * 2], null, buffer);
            }
        }

        public void paint(Graphics g) {
            super.paint(g);
            
            Graphics gBuffer = buffer.getGraphics();
            gBuffer.setColor(new Color(39, 43, 76));
            gBuffer.fillRect(0, 0, this.getWidth(), this.getHeight());
            grid();
            //axis(director, 1, buffer);

           /* int[][] points5 = new int[][]{
                    new int[]{-10 + origin2D[0], 0 + origin2D[0], 10 + origin2D[0], 0 + origin2D[0]},
                    new int[]{0 + origin2D[1], 10 + origin2D[1], 0 + origin2D[1], -10 + origin2D[1]},
                    new int[]{0, 0, 0, 0},
            };


            int[][] points6 = new int[][]{
                    new int[]{-360 + origin2D[0], -350 + origin2D[0], -340 + origin2D[0], -350 + origin2D[0]},
                    new int[]{0 + origin2D[1], 10 + origin2D[1], 0 + origin2D[1], -10 + origin2D[1]},
                    new int[]{0, 0, 0, 0},
            };
            int[][] translatedPoints = Transformations.translate3d(points6[0], points6[1], points6[2], x, 0, x);

            int[][] points7 = new int[][]{
                    new int[]{340 + origin2D[0], 350 + origin2D[0], 360 + origin2D[0], 350 + origin2D[0]},
                    new int[]{0 + origin2D[1], 10 + origin2D[1], 0 + origin2D[1], -10 + origin2D[1]},
                    new int[]{0, 0, 0, 0},
            };*/

            //biPyramid(points5, 10, new int[] {director[0] - origin2D[0], director[1] - origin2D[1], director[2]}, 10, new double[]{Math.PI / 8, angle, angle}, null, "perspective", true, Color.white, Color.blue, buffer);
            //biPyramid(translatedPoints, 10, new int[] {director[0] - origin2D[0], director[1] - origin2D[1], director[2]}, 10, new double[]{Math.PI / 8, 0, 0}, null, "perspective", true, Color.white, Color.red, buffer);
            //biPyramid(points7, 10, new int[] {director[0] - origin2D[0], director[1] - origin2D[1], director[2]}, scale, new double[]{Math.PI / 8, 0, 0}, null, "perspective", true, Color.white, Color.green, buffer);

            int xc = 100;
            int yc = 250;
            int[][] points = new int[][]{
                    new int[]{xc + 0, xc + 0, xc + 420, xc + 485, xc + 560, xc + 675, xc + 710, xc + 680},
                    new int[]{yc + 17, yc + 32, yc + 137, yc + 92, yc + 107, yc + 217, yc + 217, yc + 52},
                    /*new int[]{100, 100, 100, 100, 100, 100, 100, 100},*/
                    new int[]{100, 104, 116, 99, 101, 127, 126, 79},
            };

            int[][] points1 = new int[][]{
                    new int[]{xc + 0, xc + 0, xc + 420, xc + 485, xc + 560, xc + 675, xc + 710, xc + 680},
                    new int[]{yc - 17, yc - 32, yc - 137, yc - 92, yc - 107, yc - 217, yc - 217, yc - 52},
                    /*new int[]{100, 100, 87, 85, 83, 79, 78, 79},*/
                    new int[]{100, 104, 116, 99, 101, 127, 126, 79},

            };
            int[][] points2 = new int[][]{
                    new int[]{xc + 0, xc + 420, xc + 430, xc + 420, xc + 0},
                    new int[]{yc + 17, yc + 39, yc + 0, yc - 39, yc - 17},
                    new int[]{100, 87, 87, 87, 100},

            };

            int[][] points3 = new int[][]{
                    new int[]{xc + 420, xc + 545, xc + 545, xc + 505, xc + 505, xc + 465, xc + 465, xc + 505, xc + 505, xc + 545, xc + 545, xc + 420, xc + 430},
                    new int[]{yc + 39, yc + 45, yc + 25, yc + 25, yc + 12, yc + 12, yc - 12, yc - 12, yc -  25, yc - 25, yc - 45, yc - 39, yc - 0},
                    new int[]{87, 83, 83, 84, 84, 86, 86, 84, 84, 83, 83, 87, 87},
            };


            int[][] points4 = new int[][]{
                    new int[]{xc + 0, xc + 0, xc + 420, xc + 485, xc + 560, xc + 675, xc + 710, xc + 680},
                    new int[]{yc + 17, yc + 32, yc + 137, yc + 92, yc + 107, yc + 217, yc + 217, yc + 52},
                    new int[]{130, 130, 130, 130, 130, 130, 130, 130},
            };

            int[][] points5 = new int[][]{
                    new int[]{xc + 0, xc + 0, xc + 420, xc + 485, xc + 560, xc + 675, xc + 710, xc + 680},
                    new int[]{yc - 17, yc - 32, yc - 137, yc - 92, yc - 107, yc - 217, yc - 217, yc - 52},
                    new int[]{130, 130, 130, 130, 130, 130, 130, 130},
            };

            int[] p1 = new int[]{points2[0][0], points2[1][2], points2[2][0]};
            int[] p2 = new int[]{points2[0][2], points2[1][2], points2[2][0]};

            int[] pa = new int[]{points2[0][0], points2[1][0], points2[2][0]};
            int[] pb = new int[]{points2[0][4], points2[1][4], points2[2][0]};

            int[] p1a = new int[]{points1[0][0], points1[1][0], points1[2][0]};
            int[] p1b = new int[]{points1[0][7], points1[1][7], points1[2][7]};

            int[][] rotated = Transformations.rotateAroundLine(
                    points[0],
                    points[1],
                    points[2],
                    pa,
                    pb,
                    -0.03
            );

            int[][] rotated1 = Transformations.rotateAroundLine(
                    points1[0],
                    points1[1],
                    points1[2],
                    p1a,
                    p1b,
                    -0.3
            );



            int[][] rotated2 = Transformations.rotateAroundLine(
                    points2[0],
                    points2[1],
                    points2[2],
                    pa,
                    pb,
                    -0.03
            );

            int[][] rotated3 = Transformations.rotateAroundLine(
                    points3[0],
                    points3[1],
                    points3[2],
                    pa,
                    pb,
                    -0.03
            );

            int[][] rotated4 = Transformations.rotateAroundLine(
                    points4[0],
                    points4[1],
                    points4[2],
                    pa,
                    pb,
                   0.05
            );

            int[][] rotated5 = Transformations.rotateAroundLine(
                    points5[0],
                    points5[1],
                    points5[2],
                    pa,
                    pb,
                   0.05
            );
            /*for(int i = 0; i < points1.length; i++) {
                for(int j = 0; j < points1[0].length; j++) {
                    System.out.print(rotated1[i][j] + ", ");

                }
                System.out.println("");
            }*/
            surface(points, angle, p1, p2, director, "oblique", Color.white, buffer);
            surface(points1, angle, p1, p2, director, "oblique", Color.white, buffer);
            surface(points3, angle, p1, p2, director, "oblique", Color.white, buffer);
            surface(points2, angle, p1, p2, director, "oblique", Color.red, buffer);

            /*surface(rotated4, angle, p1, p2, director, "oblique", Color.green, buffer);
            surface(rotated5, angle, p1, p2, director, "oblique", Color.green, buffer);*/

            g.drawImage(buffer, 0, 0, this);
        }
    }

    // ---------------------------------------- Listeners ----------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.xPressed = e.getX();
        this.yPressed = e.getY();

        this.xDirInit = canvasPanel.director[0];
        this.yDirInit = canvasPanel.director[1];
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        int xDirector = -(e.getX() - xPressed) / 1;
        int yDirector = -(e.getY() - yPressed) / 1;

        canvasPanel.director[0] = xDirInit + xDirector;
        canvasPanel.director[1] = yDirInit + yDirector;
        canvasPanel.repaint();

        directorLabel.setText("P(" + canvasPanel.director[0] + ", " + canvasPanel.director[1] + ", " + canvasPanel.director[2] + ")");
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // ---------------------------------------- Execution ----------------------------------------
    public static void main(String[] args) {
        Window3d window = new Window3d();

        CustomThread thread = new CustomThread(() -> {
            window.bgPanel.repaint();
        }, 20, () -> false);
        thread.start();

    }
}
