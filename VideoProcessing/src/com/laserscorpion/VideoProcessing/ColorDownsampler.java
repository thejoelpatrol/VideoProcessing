package com.laserscorpion.VideoProcessing;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ColorDownsampler {
    private enum Channel {RED, GREEN, BLUE}
    Image originalImage;


    public ColorDownsampler(Image original) {
        originalImage = original;
        //originalRGB = original.getRGB(0, 0, original.getWidth(), original.getHeight(), null, 0, original.getWidth());
    }

    public Image downsample(int outputColors) {
        Date then = new Date();
        Pixel[] commonColors = selectColors(outputColors);
        Date now = new Date();
        Long diff = now.getTime() - then.getTime();
        boolean print = (now.getTime() % 20L == 0L);
        if (print) {
            System.err.println("selecting colors took " + diff + " ms");
        }
        then = new Date();
        Pixel[] modifiedColors = convertColors(commonColors);
        now = new Date();
        diff = now.getTime() - then.getTime();
        if (print) {
            System.err.println("converting colors took " + diff + " ms");
        }
        Image result = new Image(modifiedColors, originalImage.height, originalImage.width);
        return result;
    }

    private Pixel[] convertColors(Pixel[] selectedOutputColors) {
        Pixel[] convertedColors = new Pixel[originalImage.height * originalImage.width];
        for (int i = 0; i < originalImage.height; i++) {
            for (int j = 0; j < originalImage.width; j++) {
                int index = j + i*originalImage.width;
                Pixel pixelColor = originalImage.pixels[i][j];
                Pixel closestColor = selectedOutputColors[0];
                for (int k = 1; k < selectedOutputColors.length; k++) {
                    closestColor = pickCloserColor(pixelColor, closestColor, selectedOutputColors[k]);
                }
                convertedColors[index] = closestColor;
            }
        }
        return convertedColors;
    }

    private Pixel pickCloserColor(Pixel source, Pixel color1, Pixel color2) {
        double dist1 = calculateDistance(source, color1);
        double dist2 = calculateDistance(source, color2);
        if (dist1 < dist2)
            return color1;
        return color2;
    }

    private double calculateDistance(Pixel c1, Pixel c2) {
        // 3D pythagorean theorem
        int dR = c2.r - c1.r;
        int dG = c2.g - c1.g;
        int dB = c2.b - c1.b;
        int dR2 = dR * dR;
        int dG2 = dG * dG;
        int dB2 = dB * dB;
        return Math.sqrt(dR2 + dG2 + dB2);
    }

    private Pixel[] selectColors(int topColorCount) {
        int[][] colorTriples = convertRGBToTriples();

        int[] topXcolors = medianCut(colorTriples, topColorCount);

        /*synchronized (System.err) {
            System.err.print("top ints: ");
            for (int x : topXcolors) {
                System.err.print(x + ":");
            }
            System.err.print("\n");
        }*/

        Pixel[] topColors = new Pixel[topXcolors.length];
        for (int i = 0; i < topXcolors.length; i++) {
            topColors[i] = new Pixel();
            topColors[i].b = (short)(topXcolors[i] & 0xFF);
            topColors[i].g = (short)((topXcolors[i] >> 8) & 0xFF);
            topColors[i].r = (short)((topXcolors[i] >> 16) & 0xFF);
        }

        return topColors;
    }

    private int[][] convertRGBToTriples() {
        int[][] colorTriples = new int[originalImage.pixels.length * originalImage.pixels[0].length][];
        for (int i = 0; i < originalImage.pixels.length; i++) {
            for (int j = 0; j < originalImage.pixels[i].length; j++) {
                int index = j + i*originalImage.pixels[i].length;
                colorTriples[index] = new int[3];
                Pixel pixel = originalImage.pixels[i][j];
                colorTriples[index][0] = pixel.r;
                colorTriples[index][1] = pixel.g;
                colorTriples[index][2] = pixel.b;
            }
        }
        return colorTriples;
    }

    /**
     * Precondition: desiredColors is a power of 2
     * @param RGBTriples
     * @param desiredColors must be power of 2
     * @return desiredColors representative average colors
     */
    private int[] medianCut(int[][] RGBTriples, int desiredColors) {
        /*if (desiredColors == 16) {
            synchronized (System.err) {
                System.err.print("desiredColors: " + desiredColors + " -- ");
                for (int i = 50; i<60; i++) {
                    for (int x : RGBTriples[i]){
                        System.err.print(x + ":");
                    }
                    System.err.print(" ");
                }
                System.err.print("\n");
            }

        }*/

        if (desiredColors == 1) {
            int average = averageColor(RGBTriples);
            int[] result = new int[1];
            result[0] = average;
            return result;
        }

        Channel sortRange = selectRange(RGBTriples);
        SortingPixels sorter = new SortingPixels(sortRange);
        int[][] sortedCopy = Arrays.copyOf(RGBTriples, RGBTriples.length);
        Arrays.sort(sortedCopy, sorter);

        int midpoint = sortedCopy.length / 2;
        int[][] firstHalf = Arrays.copyOfRange(sortedCopy, 0, midpoint);
        int[][] secondHalf = Arrays.copyOfRange(sortedCopy, midpoint, sortedCopy.length);

        int[] firstHalfAverages = medianCut(firstHalf, desiredColors / 2);
        int[] secondHalfAverages = medianCut(secondHalf, desiredColors / 2);
        return merge(firstHalfAverages, secondHalfAverages);
    }

    private int[] merge(int[] array1, int[] array2) {
        int[] result = new int[array1.length + array2.length];
        int i = 0;
        while (i < array1.length) {
            result[i] = array1[i];
            i++;
        }
        int j = 0;
        while (j < array2.length) {
            result[i] = array2[j];
            i++;
            j++;
        }
        return result;
    }

    private int findMidpoint(int[][] triples) {
        int midpoint = triples.length / 2;
        if (Arrays.equals(triples[midpoint], triples[midpoint - 1])) {

        }
        if (Arrays.equals(triples[midpoint], triples[midpoint + 1])) {

        }
        return midpoint;
    }

    private int averageColor(int[][] RGBTriples) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        for (int[] rgb : RGBTriples) {
            redSum += rgb[0];
            greenSum += rgb[1];
            blueSum += rgb[2];
        }
        int[] result = new int[3];
        result[0] = redSum / RGBTriples.length;
        result[1] = greenSum / RGBTriples.length;
        result[2] = blueSum / RGBTriples.length;
        Color color = new Color(result[0], result[1], result[2]);
        return color.getRGB();
    }

    private Channel selectRange(int[][] RGB) {
        int redRange = calculateRange(RGB, Channel.RED);
        int greenRange = calculateRange(RGB, Channel.GREEN);
        int blueRange = calculateRange(RGB, Channel.BLUE);
        if (redRange > greenRange && redRange > blueRange)
            return Channel.RED;
        if (greenRange > redRange && greenRange > blueRange)
            return Channel.GREEN;
        return Channel.BLUE;
    }

    private int calculateRange(int[][] colors, Channel channel) {
        int min = 255;
        int max = 0;
        for (int[] color : colors) {
            int value = color[channel.ordinal()];
            if (value < min)
                min = value;
            if (value > max)
                max = value;
        }
        return max - min;
    }

    private class SortingPixels implements Comparator<int[]> {
        Channel compareChannel;

        public SortingPixels(Channel compareChannel) {
            this.compareChannel = compareChannel;
        }

        @Override
        public int compare(int[] o1, int[] o2) {

            int first, second;
            if (compareChannel == Channel.RED) {
                first = o1[0];
                second = o1[0];
            } else if (compareChannel == Channel.GREEN) {
                first = o1[1];
                second = o1[1];
            } else {
                first = o1[2];
                second = o1[2];
            }

            if (first == second)
                return 0;
            else if (first < second)
                return -1;
            return 1;
        }
    }

}
