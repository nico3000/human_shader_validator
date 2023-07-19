package dev.nicotopia.hsv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.OptionalInt;

/**
 * Hello world!
 *
 */
public class Validator {
    public static void main(String[] args) throws IOException {
        OptionalInt pxX = OptionalInt.empty();
        OptionalInt pxY = OptionalInt.empty();
        if (args.length == 1) {
            System.out.printf("Writing PPM image to %s.\n", args[0]);
            Validator.writeImage(args[0]);
            System.out.println("Done");
        } else if (args.length == 2) {
            pxX = OptionalInt.of(Integer.valueOf(args[0]));
            pxY = OptionalInt.of(Integer.valueOf(args[1]));
        } else {
            byte buffer[] = new byte[32];
            System.out.print("Pixel x coordinate: ");
            System.in.read(buffer);
            pxX = OptionalInt.of(Integer.valueOf(new String(buffer).trim()));
            System.out.print("Pixel y coordinate: ");
            System.in.read(buffer);
            pxY = OptionalInt.of(Integer.valueOf(new String(buffer).trim()));
        }
        if (pxX.isPresent() && pxY.isPresent()) {
            new Validator(pxX.getAsInt(), pxY.getAsInt()).printResult();
        }
    }

    public static void writeImage(String path) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            int width = 71;
            int height = 40;
            bw.write("P3\n");
            bw.write(String.format("%d %d\n", width, height));
            bw.write("255\n");
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    Validator v = new Validator(x, y);
                    bw.write(String.format("%d %d %d ", v.R, v.G, v.B));
                }
                bw.newLine();
            }
        }
    }

    private int R;
    private int G;
    private int B;

    public Validator(int x, int y) {
        int u = x - 36;
        int v = 18 - y;
        int h = u * u + v * v;
        if (h < 200) {
            this.sectionB(u, v, h);
        } else if (v < 0) {
            this.sectionC(u, v, h);
        } else {
            this.sectionD(x, y);
        }
    }

    private void sectionB(int u, int v, int h) {
        this.R = 420;
        this.B = 520;
        int t = 5000 + 8 * h;
        int p = this.mod(t * u, 2);
        int q = this.mod(t * v, 2);
        int s = 2 * q;
        int w = this.mod(1000 + p - s, 2) + 8;
        if (0 < w) {
            this.R = this.R + w * w;
        }
        int o = s + 2200;
        this.R = this.mod(this.R * o, 4);
        this.B = this.mod(this.B * o, 4);
        if (-q < p) {
            w = this.mod(p + q, 1);
            this.R = this.R + w;
            this.B = this.B + w;
        }
        this.sectionE();
    }

    private void sectionC(int u, int v, int h) {
        this.R = 150 + 2 * v;
        this.B = 50;
        int p = h + 8 * v * v;
        int c = 240 * (-v) - p;
        if (1200 < c) {
            int o = this.mod(6 * c, 1);
            o = c * (1500 - o);
            o = this.mod(o, 2) - 8360;
            this.R = this.mod(this.R * o, 3);
            this.B = this.mod(this.B * o, 3);
        }
        int r = c + u * v;
        int d = 3200 - h - 2 * r;
        if (0 < d) {
            this.R = this.R + d;
        }
        this.sectionE();
    }

    private void sectionD(int x, int y) {
        int c = x + 4 * y;
        this.R = 132 + c;
        this.B = 192 + c;
        this.sectionE();
    }

    private void sectionE() {
        if (255 < this.R) {
            this.R = 255;
        }
        if (255 < this.B) {
            this.B = 255;
        }
        this.G = this.mod(7 * this.R + 3 * this.B, 1);
    }

    public void printResult() {
        System.out.printf("R=%3d, G=%3d, B=%3d\n", this.R, this.G, this.B);
    }

    private int mod(int left, int right) {
        int l = left < 0 ? -left : left;
        for (int i = 0; i < right - 1; ++i) {
            l /= 10;
        }
        int r = l % 10 < 5 ? (l / 10) : (l / 10 + 1);
        return left < 0 ? -r : r;
    }
}
