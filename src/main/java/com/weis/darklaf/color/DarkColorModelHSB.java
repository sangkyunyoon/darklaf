package com.weis.darklaf.color;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DarkColorModelHSB extends DarkColorModel {

    private static final int[] hsb = new int[3];
    private static final int[] rgb = new int[3];

    public DarkColorModelHSB() {
        super("hsv", "Hue", "Saturation", "Brightness");
    }

    @Contract("_, _, _ -> new")
    @NotNull
    public static int[] RGBtoHSB(final int r, final int g, final int b) {
        double hue, saturation, brightness;
        int cmax = Math.max(r, g);
        if (b > cmax) cmax = b;
        int cmin = Math.min(r, g);
        if (b < cmin) cmin = b;

        brightness = ((double) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((double) (cmax - cmin)) / ((double) cmax);
        } else {
            saturation = 0;
        }
        if (saturation == 0) {
            hue = 0;
        } else {
            double redc = ((double) (cmax - r)) / ((double) (cmax - cmin));
            double greenc = ((double) (cmax - g)) / ((double) (cmax - cmin));
            double bluec = ((double) (cmax - b)) / ((double) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            } else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }
        hsb[0] = (int) Math.round(hue * 360);
        hsb[1] = (int) Math.round(saturation * 100);
        hsb[1] = (int) Math.round(brightness * 100);
        return hsb;
    }

    public static int[] HSBtoRGB(final double hue, final double saturation, final double brightness) {
        double r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (brightness * 255.0f + 0.5f);
        } else {
            double h = (hue - Math.floor(hue)) * 6.0f;
            double f = h - Math.floor(h);
            double p = brightness * (1.0f - saturation);
            double q = brightness * (1.0f - saturation * f);
            double t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) Math.round(h)) {
                case 0:
                    r = (brightness * 255.0f + 0.5f);
                    g = (t * 255.0f + 0.5f);
                    b = (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (q * 255.0f + 0.5f);
                    g = (brightness * 255.0f + 0.5f);
                    b = (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (p * 255.0f + 0.5f);
                    g = (brightness * 255.0f + 0.5f);
                    b = (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (p * 255.0f + 0.5f);
                    g = (q * 255.0f + 0.5f);
                    b = (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (t * 255.0f + 0.5f);
                    g = (p * 255.0f + 0.5f);
                    b = (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (brightness * 255.0f + 0.5f);
                    g = (p * 255.0f + 0.5f);
                    b = (q * 255.0f + 0.5f);
                    break;
            }
        }
        rgb[0] = ((int) Math.round(r));
        rgb[1] = ((int) Math.round(g));
        rgb[2] = ((int) Math.round(b));
        return rgb;
    }

    @Override
    public int getMaximum(final int index) {
        return (index == 0) ? 359 : 100;
    }

    @Override
    public String toString() {
        return "HSB";
    }

    @Override
    public char[] getLabelDescriptorsBefore() {
        return new char[]{'H', 'S', 'B'};
    }

    @Override
    public char[] getLabelDescriptorsAfter() {
        return new char[]{'\u00B0', '%', '%'};
    }

    @Override
    public int[] getValuesFromColor(@NotNull final Color color) {
        return RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public Color getColorFromValues(@NotNull final int[] values) {
        var rgb = HSBtoRGB(values[0] / 360.0, values[1] / 100.0, values[2] / 100.0);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
}
