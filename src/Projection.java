import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Projection extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private int width;
    private int height;
    private int titleBarHeight;
    private CustomPanel canvasPanel;
    private JPanel bgPanel;
    JPanel bottomBar;
    public JLabel directorLabel;
    public int xPressed, yPressed;
    public int xDirInit, yDirInit;
    boolean firstResize;
    public boolean isMacOS;

    public Projection() {
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

        public CustomPanel(int width, int height) {
            this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.width = width;
            this.height = height;

            this.director = new int[]{0, 0, 100};

            this.origin2D = new int[]{width / 2, height / 2};
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
            int[][] projectedPoints = projection(points, director, origin2D, scale, "oblique");

            int[] xPoints = projectedPoints[0];
            int[] yPoints = projectedPoints[1];

            for (int i = 1; i <= 3; i++) {
                drawLine(xPoints[(i * 2) - 1], yPoints[(i * 2) - 1], xPoints[i * 2], yPoints[i * 2], null, buffer);
            }
        }

        // ------------------------------ Utils ------------------------------
        public int[][] multiplyMatrices(double[][] scaleMatrix, int[][] initialMatrix) {
            int n = scaleMatrix.length;
            int[][] result = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double newValue = 0;
                    for (int k = 0; k < n; k++) {
                        newValue += scaleMatrix[i][k] * (double) initialMatrix[k][j];
                    }
                    result[i][j] = (int) newValue;
                }
            }

            return result;
        }

        public int[] calculatePerpendicularVector(int[] xPoints, int[] yPoints, int[] zPoints, int magnitude) {
            // Calculate vectors u and v
            int[] A = new int[] {xPoints[0], yPoints[0], zPoints[0]};
            int[] B = new int[] {xPoints[1], yPoints[1], zPoints[1]};
            int[] C = new int[] {xPoints[2], yPoints[2], zPoints[2]};

            double[] u = new double[]{B[0] - A[0], B[1] - A[1], B[2] - A[2]};
            double[] v = new double[]{C[0] - A[0], C[1] - A[1], C[2] - A[2]};

            // Calculate the cross product u x v
            double[] normal = new double[]{
                    u[1] * v[2] - u[2] * v[1],
                    u[2] * v[0] - u[0] * v[2],
                    u[0] * v[1] - u[1] * v[0]
            };

            // Calculate the magnitude of the normal vector
            double normalMagnitude = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);

            // Normalize the normal vector
            double[] normalized = new double[]{
                    normal[0] / normalMagnitude,
                    normal[1] / normalMagnitude,
                    normal[2] / normalMagnitude
            };

            // Scale the normal vector to the desired magnitude
            int[] scaled = new int[]{
                    (int) (normalized[0] * magnitude),
                    (int) (normalized[1] * magnitude),
                    (int) (normalized[2] * magnitude)
            };

            return scaled;
        }

        // ------------------------------ Drawing ------------------------------
        private void draw(int x, int y, Color color, BufferedImage buffer) {
            if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
                buffer.setRGB(x, y, color.getRGB());
            }
        }

        public void drawLine(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
            double m = (double) (y2 - y1) / (x2 - x1);
            if ((x2 - x1) == 0) {
                m = 0;
            }

            int r = 20;
            int g = 255;
            int b = 50;

            // Evaluate x
            if (Math.abs(x2 - x1) >= Math.abs(y2 - y1)) {
                // Line color
                double colorRate = (double) (Math.abs(x2 - x1)) / 205;
                int colorIncrement = 1;
                if (colorRate < 1) {
                    colorIncrement = (int) Math.floor(1 / colorRate);
                    colorRate = 1;
                } else {
                    colorRate = Math.ceil(colorRate);
                }

                double y = y1;
                if (x2 > x1) {
                    for (int x = x1; x <= x2; x++) {
                        Color gradColor = new Color(r, g, b);
                        draw(x, (int) y, color != null ? color : gradColor, buffer);

                        y += m;

                        if (x % (int) colorRate == 0) {
                            b += colorIncrement;
                            r += colorIncrement;
                            g -= colorIncrement;
                        }
                    }
                } else {
                    for (int x = x1; x >= x2; x--) {
                        Color gradColor = new Color(r, g, b);
                        draw(x, (int) y, color != null ? color : gradColor, buffer);

                        y -= m;

                        if (x % (int) colorRate == 0) {
                            b += colorIncrement;
                            r += colorIncrement;
                            g -= colorIncrement;
                        }
                    }
                }
            }

            // Evaluate y
            else {
                // Line color
                double colorRate = (double) (Math.abs(y2 - y1)) / 205;
                int colorIncrement = 1;
                if (colorRate < 1) {
                    colorIncrement = (int) Math.floor(1 / colorRate);
                    colorRate = 1;
                } else {
                    colorRate = Math.ceil(colorRate);
                }

                double x = x1;
                if (y2 > y1) {
                    for (int y = y1; y <= y2; y++) {
                        Color gradColor = new Color(r, g, b);
                        draw((int) x, y, color != null ? color : gradColor, buffer);

                        if (m != 0) {
                            x += 1 / m;
                        }

                        if (y % (int) colorRate == 0) {
                            b += colorIncrement;
                            r += colorIncrement;
                            g -= colorIncrement;
                        }
                    }
                } else {
                    for (int y = y1; y >= y2; y--) {
                        Color gradColor = new Color(r, g, b);
                        draw((int) x, y, color != null ? color : gradColor, buffer);

                        if (m != 0) {
                            x -= 1 / m;
                        }

                        if (y % (int) colorRate == 0) {
                            b += colorIncrement;
                            r += colorIncrement;
                            g -= colorIncrement;
                        }
                    }
                }
            }

        }

        public void drawPolygon(int[] xPoints, int[] yPoints, Color color, BufferedImage buffer) {
            for (int i = 0; i < xPoints.length - 1; i++) {
                drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], color, buffer);
            }
            drawLine(xPoints[xPoints.length - 1], yPoints[yPoints.length - 1], xPoints[0], yPoints[0], color, buffer);

        }

        public int[][] scale(int[] xPoints, int[] yPoints, int xc, int yc, boolean center, double xScale, double yScale) {
            int xCenter = 0 + xc;
            int yCenter = 0 + yc;

            // Calculate the center
            if (center) {
                int sumX = 0;
                int sumY = 0;

                for (int i = 0; i < xPoints.length; i++) {
                    sumX += xPoints[i];
                    sumY += yPoints[i];
                }

                xCenter = sumX / xPoints.length;
                yCenter = sumY / yPoints.length;
            }

            // Create initial matrix
            int[][] initialMatrix = new int[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] = 0 + xPoints[i];
                initialMatrix[1][i] = 0 + yPoints[i];
            }

            // Setting center to [0, 0]
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] -= xCenter;
                initialMatrix[1][i] -= yCenter;
            }

            for (int i = 2; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    initialMatrix[i][j] = 1;
                }
            }

            // Create resizing matrix
            double[][] scaleMatrix = new double[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    if (i == j) {
                        scaleMatrix[i][j] = 1;
                    } else {
                        scaleMatrix[i][j] = 0;
                    }
                }
            }

            scaleMatrix[0][0] = xScale;
            scaleMatrix[1][1] = yScale;

            int[][] resultMatrix = multiplyMatrices(scaleMatrix, initialMatrix);

            // Translation to origin al center
            for (int i = 0; i < xPoints.length; i++) {
                resultMatrix[0][i] += xCenter;
                resultMatrix[1][i] += yCenter;
            }

            return resultMatrix;
        }

        public int[][] scale3D(int[] xPoints, int[] yPoints, int[] zPoints, int xc, int yc, int zc, boolean center, double xScale, double yScale, double zScale) {
            int xCenter = 0 + xc;
            int yCenter = 0 + yc;
            int zCenter = 0 + zc;

            // Calculate the center
            if (center) {
                int sumX = 0;
                int sumY = 0;
                int sumZ = 0;

                for (int i = 0; i < xPoints.length; i++) {
                    sumX += xPoints[i];
                    sumY += yPoints[i];
                    sumZ += zPoints[i];
                }

                xCenter = sumX / xPoints.length;
                yCenter = sumY / yPoints.length;
            }

            // Create initial matrix
            int[][] initialMatrix = new int[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] = 0 + xPoints[i];
                initialMatrix[1][i] = 0 + yPoints[i];
            }

            // Setting center to [0, 0]
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] -= xCenter;
                initialMatrix[1][i] -= yCenter;
            }

            for (int i = 2; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    initialMatrix[i][j] = 1;
                }
            }

            // Create resizing matrix
            double[][] scaleMatrix = new double[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    if (i == j) {
                        scaleMatrix[i][j] = 1;
                    } else {
                        scaleMatrix[i][j] = 0;
                    }
                }
            }

            scaleMatrix[0][0] = xScale;
            scaleMatrix[1][1] = yScale;

            int[][] resultMatrix = multiplyMatrices(scaleMatrix, initialMatrix);

            // Translation to origin al center
            for (int i = 0; i < xPoints.length; i++) {
                resultMatrix[0][i] += xCenter;
                resultMatrix[1][i] += yCenter;
            }

            return resultMatrix;
        }

        public int[][] projection(int[][] points, int[] director, int[] p0, double scale, String projection) {
            int[][] projectedPoints = new int[2][points[0].length];

            if (projection.equals("oblique")) {
                for (int i = 0; i < points[0].length; i++) {
                    double u = -((double) points[2][i]) / ((double) director[2]);

                    int x = points[0][i] + (int) ((double) director[0] * u);
                    int y = points[1][i] + (int) ((double) director[1] * u);

                    projectedPoints[0][i] = p0[0] + x;
                    projectedPoints[1][i] = p0[1] + y;
                }
            }

            if (projection.equals("orthogonal")) {
                for (int i = 0; i < points[0].length; i++) {
                    int x = points[0][i];
                    int y = points[1][i];

                    projectedPoints[0][i] = p0[0] + x;
                    projectedPoints[1][i] = p0[1] + y;
                }
            }

            if (projection.equals("perspective")) {
                for (int i = 0; i < points[0].length; i++) {
                    if (points[2][i] == director[2]) return null;

                    double u = -((double) director[2]) / ((double) (points[2][i] - director[2]));

                    int x = director[0] + (int) ((double) (points[0][i] - director[0]) * u);
                    int y = director[1] + (int) ((double) (points[1][i] - director[1]) * u);

                    projectedPoints[0][i] = p0[0] + x;
                    projectedPoints[1][i] = p0[1] + y;
                }
            }


            int[][] scaledPoints = scale(projectedPoints[0], projectedPoints[1], p0[0], p0[1], false, scale, scale);

            return scaledPoints;
        }

        public void cube(int x0, int y0, int z0, int width, int depth, int height, int[] director, double scale, int[] p0, String projection, Color color, BufferedImage buffer) {
            int[][] points1 = new int[][]{
                    new int[]{x0, x0 + width, x0 + width, x0},
                    new int[]{y0, y0, y0 + depth, y0 + depth},
                    new int[]{z0, z0, z0, z0},

            };
            int[][] points2 = new int[][]{
                    new int[]{x0, x0 + width, x0 + width, x0},
                    new int[]{y0, y0, y0 + depth, y0 + depth},
                    new int[]{z0 + height, z0 + height, z0 + height, z0 + height},

            };

            int[][] projectedPoints1 = projection(points1, director, p0, scale, projection);
            int[][] projectedPoints2 = projection(points2, director, p0, scale, projection);

            if (projectedPoints1 != null && projectedPoints2 != null) {
                for (int i = 0; i < projectedPoints1[0].length; i++) {
                    drawLine(projectedPoints1[0][i], projectedPoints1[1][i], projectedPoints2[0][i], projectedPoints2[1][i], color, buffer);
                }

                drawPolygon(
                        projectedPoints1[0],
                        projectedPoints1[1],
                        color,
                        buffer
                );
                drawPolygon(
                        projectedPoints2[0],
                        projectedPoints2[1],
                        color,
                        buffer
                );
            }

        }

        public void prism(int[][] points, int height, int[] director, double scale, int[] p0, String projection, int direction, Color color, BufferedImage buffer) {
            // ---------- Calculations ----------
            // Height vector
            int[] heightVector = calculatePerpendicularVector(points[0], points[1], points[2], height);

            // Bottom face
            int[][] projectedPoints1 = projection(points, director, p0, scale, projection);

            // Top face
            int[][] topPoints = new int[3][points[0].length];
            for (int i = 0; i < points[0].length; i++) {
                topPoints[0][i] = points[0][i] + (direction * heightVector[0]);
                topPoints[1][i] = points[1][i] + (direction * heightVector[1]);
                topPoints[2][i] = points[2][i] + (direction * heightVector[2]);
                /*System.out.println("Cara1: (" + points[i][0] + ", " + points[i][1] + ", " + points[i][2] + ")");
                System.out.println("Cara2: (" + topPoints[i][0] + ", " + topPoints[i][1] + ", " + topPoints[i][2] + ")");*/
            }

            int[][] projectedPoints2 = projection(topPoints, director, p0, scale, projection);

            // ---------- Drawing ----------
            if (projectedPoints1 != null && projectedPoints2 != null) {
                for (int i = 0; i < points[0].length; i++) {
                    drawLine(projectedPoints1[0][i], projectedPoints1[1][i], projectedPoints2[0][i], projectedPoints2[1][i], color, buffer);
                }

                // Bottom face
                drawPolygon(
                        projectedPoints1[0],
                        projectedPoints1[1],
                        color,
                        buffer
                );

                // Top face
                drawPolygon(
                        projectedPoints2[0],
                        projectedPoints2[1],
                        color,
                        buffer
                );
            }
        }


        public int[][] star(int xc, int yc) {
            int outerRadius = 10;
            int innerRadius = 5;
            int numPoints = 5;

            int[] xPoints = new int[numPoints * 2];
            int[] yPoints = new int[numPoints * 2];

            for (int i = 0; i < numPoints * 2; i++) {
                double angle = Math.PI / 2 + (i * Math.PI / numPoints);
                int radius = (i % 2 == 0) ? outerRadius : innerRadius;
                xPoints[i] = xc + (int) (radius * Math.cos(angle));
                yPoints[i] = yc + (int) (radius * Math.sin(angle));

                xPoints[i] = 2 * xc - xPoints[i];
                yPoints[i] = 2 * yc - yPoints[i];
            }

            int[][] starPoints = new int[3][10];
            for (int i = 0; i < 10; i++) {
                starPoints[0][i] = xPoints[i];
                starPoints[1][i] = yPoints[i];
                starPoints[2][i] = 10;
            }

            return starPoints;
        }

        public void paint(Graphics g) {
            super.paint(g);
            Graphics gBuffer = buffer.getGraphics();
            gBuffer.setColor(new Color(39, 43, 76));
            gBuffer.fillRect(0, 0, this.getWidth(), this.getHeight());
            grid();
            //axis(director, 1, buffer);

            int[][] tPoints = new int[][]{
                    new int[]{-60, 0, 0, 60, 60, 0, 0, -60},
                    new int[]{-90, -90, -30, -30, 30, 30, 90, 90},
                    new int[]{120, 180, 180, 240, 240, 180, 180, 120},
            };

            int[][] points = new int[][]{
                    new int[]{0, 100, 100, 0},
                    new int[]{0, 0, 100, 100},
                    new int[]{0,0,0,0}
            };


            //cube(-10, -10, 0, 20, 20, 30, director, 5, origin2D, "perspective", Color.white, buffer);
            //cosa(points, director, 1, origin2D, "oblique", Color.green, buffer);

            int[][] star = star(0, 0);
            prism(star, 85, director, 10, origin2D, "oblique", 1, Color.green, buffer);
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
        Projection window = new Projection();
    }
}