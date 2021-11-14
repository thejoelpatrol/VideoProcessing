package com.laserscorpion.VideoProcessing.drawing;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.Pixel;

public class Graphics {
    private Image canvas;

    public Graphics(int height, int width) {
        Pixel transparent[] = new Pixel[width * height];
        Pixel p = new Pixel(true);

        for (int i = 0; i < transparent.length; i++) {
            transparent[i] = p;
        }
        canvas = new Image(transparent, height, width);
    }

    public Image getImage() {
        return canvas;
    }

    public void background(Pixel color) {
        Pixel bg = color.copyOf(); // intentionally using one pixel ptr for entire bg
        for (int i = 0; i < canvas.height; i++) {
            for (int j = 0; j < canvas.width; j++) {
                canvas.pixels[i][j] = bg;
            }
        }
    }

    public void rectangle(int x, int y, int width, int height, Pixel color) {
        Pixel c = color.copyOf(); // intentionally using one pixel ptr for entire shape
        for (int i = y; i < y + height; i++) {
            if (i >= canvas.height)
                break;
            for (int j = x; j < x + width; j++) {
                if (j >= canvas.width)
                    break;
                canvas.pixels[i][j] = c;
            }
        }
    }

    private void plotLineHigh(int x0, int y0, int x1, int y1, Pixel color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -1*dx;
        }
        int D = (2*dx) - dy;
        int x = x0;
        for (int y = y0; y < y1; y++) {
            canvas.pixels[y][x] = color;
            if (D > 0) {
                x = x + xi;
                D = D + (2 * (dx - dy));
            } else {
                D = D + 2*dx;
            }
        }
    }

    private void plotLineLow(int x0, int y0, int x1, int y1, Pixel color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if (dy < 0) {
            yi = -1;
            dy = -1*dy;
        }
        int D = 2*dy - dx;
        int y = y0;
        for (int x = x0; x < x1; x++) {
            canvas.pixels[y][x] = color;
            if (D > 0) {
                y = y + yi;
                D = D + (2 * (dy - dx));
            } else {
                D = D + 2*dy;
            }
        }
    }

    public void line(int startX, int startY, int endX, int endY, Pixel color) {
        /**
         * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#All_cases
         */
        Pixel c = color.copyOf();
        if (Math.abs(endY - startY) < Math.abs(endX - startX)) {
            if (startX > endX)
                plotLineLow(endX, endY, startX, startY, c);
            else
                plotLineLow(startX, startY, endX, endY, c);
        } else {
            if (startY > endY)
                plotLineHigh(endX, endY, startX, startY, c);
            else
                plotLineHigh(startX, startY, endX, endY, c);
        }

    }

    private double sign(int x, int y, int x1, int y1, int x2, int y2)
    {
        return (x - x2) * (y1 - y2) - (x1 - x2) * (y - y2);
    }

    private boolean pointInTriangle(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3)
    {
        double d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(x, y, x1, y1, x2, y2);
        d2 = sign(x, y, x2, y2, x3, y3);
        d3 = sign(x, y, x3, y3, x1, y1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    public void triangle(int x0, int y0, int x1, int y1, int x2, int y2, Pixel color) {
        /**
         * https://stackoverflow.com/a/2049593/2753454
         */
        int xMin = Math.min(Math.min(x0, x1), x2);
        int xMax = Math.max(Math.max(x0, x1), x2);
        int yMin = Math.min(Math.min(y0, y1), y2);
        int yMax = Math.max(Math.max(y0, y1), y2);
        for (int y = yMin; y < yMax; y++) {
            for (int x = xMin; x < xMax; x++) {
                if (pointInTriangle(x, y, x0, y0, x1, y1, x2, y2)) {
                    canvas.pixels[y][x] = color;
                }
            }
        }
    }

}
