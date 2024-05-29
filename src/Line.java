import java.awt.*;
import java.awt.image.BufferedImage;

public class Line {
    private static void draw(int x, int y, Color color, BufferedImage buffer) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, color.getRGB());
        }
    }

    public static void drawLine(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
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

    public static void drawPolyline(int x1, int y1, int x2, int y2, Color color, int numFragments, int fragment, BufferedImage buffer) {
        double m = (double) (y2 - y1) / (x2 - x1);
        if ((x2 - x1) == 0) {
            m = 0;
        }

        int r = 20 + (int) ((((double) fragment - 1) * (205 / (double) numFragments)));
        int g = 255 - (int) ((((double) fragment - 1) * (205 / (double) numFragments)));
        int b = 50 + (int) ((((double) fragment - 1) * (205 / (double) numFragments)));

        // Evaluate x
        if (Math.abs(x2 - x1) >= Math.abs(y2 - y1)) {
            // Line color
            double colorRate = (double) (Math.abs(x2 - x1) * (numFragments)) / 205;
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
            double colorRate = (double) (Math.abs(y2 - y1) * (numFragments)) / 205;
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


    public static void drawLineMedium(int x1, int y1, int x2, int y2, BufferedImage buffer) {
        int r = 20;
        int g = 255;
        int b = 50;

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

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

            int signum = (int) Math.signum(y2 - y1);

            int y = y1;

            double d = dy - dx / 2;
            double dE = dy;
            double dNE = (dy - dx);

            if (x2 > x1) {
                for (int x = x1; x <= x2; x++) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (d <= 0) {
                        d += dE;
                    } else {
                        d += dNE;
                        y += signum;
                    }

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (d <= 0) {
                        d += dE;
                    } else {
                        d += dNE;
                        y += signum;
                    }

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

            int x = x1;

            int signum = (int) Math.signum(x2 - x1);

            double d = dx - dy / 2;
            double dE = dx;
            double dNE = (dx - dy);

            if (y2 >= y1) {
                for (int y = y1; y <= y2; y++) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (d <= 0) {
                        d += dE;
                    } else {
                        d += dNE;
                        x += signum;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                System.out.println("jejee");
                for (int y = y1; y >= y2; y--) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (d <= 0) {
                        d += dE;
                    } else {
                        d += dNE;
                        x += signum;
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

    public static void drawLineBresenham(int x1, int y1, int x2, int y2, BufferedImage buffer) {
        int r = 20;
        int g = 255;
        int b = 50;

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

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

            int signum = (int) Math.signum(y2 - y1);

            int y = y1;

            double pk = 2 * dy - dx;
            double dE = 2 * dy;
            double dNE = 2 * (dy - dx);

            if (x2 > x1) {
                for (int x = x1; x <= x2; x++) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (pk <= 0) {
                        pk += dE;
                    } else {
                        pk += dNE;
                        y += signum;
                    }

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (pk <= 0) {
                        pk += dE;
                    } else {
                        pk += dNE;
                        y += signum;
                    }

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

            int x = x1;

            int signum = (int) Math.signum(x2 - x1);

            double pk = 2 * dx - dy;
            double dE = 2 * dx;
            double dNE = 2 * (dx - dy);

            if (y2 >= y1) {
                for (int y = y1; y <= y2; y++) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (pk <= 0) {
                        pk += dE;
                    } else {
                        pk += dNE;
                        x += signum;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                System.out.println("jejee");
                for (int y = y1; y >= y2; y--) {
                    Color color = new Color(r, g, b);
                    draw(x, y, color, buffer);

                    if (pk <= 0) {
                        pk += dE;
                    } else {
                        pk += dNE;
                        x += signum;
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
}
