import de.jml.external.util.Bitmap;

import java.io.IOException;

public class Testing {

    public static void main(String[] args) throws IOException {
        createBitmap();
    }

    private static void createBitmap() throws IOException {
        byte[][] bytes = {
                {-128, -128, -128, -128, -50, -20, -10, 0, 14, -128},
                {-128, -128, -128, 0, 42, 63, 111, 56, 75, 0},
                {-128, -128, -128, -128, -100, -50, -23, 12, -43, -128},
                {-128, 13, 73, 127, 127, 127, 64, 63, 0, -128},
                {-128, 0, 41, 15, 0, -51, -64, -78, -100, -128},
                {-128, 0, 41, 15, 0, -51, -64, -78, -100, -128},
                {-128, 0, 41, 15, 0, -51, -64, -78, -100, -128}
        };
        Bitmap bmp = new Bitmap(bytes);
        Bitmap.write(bmp, "bmp5x10");
    }

}
