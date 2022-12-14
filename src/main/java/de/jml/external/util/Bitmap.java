package de.jml.external.util;

import org.jetbrains.annotations.NotNull;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

/**
 * @name Bitmap
 * @author jml04
 * @version 1.1
 *
 * @description
 * the Bitmap class represents a 2D-bitmap,
 * each value stands for a certain level (e.g. the flight count on this position).
 * The 2D-array represents a 2D-map with lat and lon values
 */
public class Bitmap {

    // bitmap as 2D-byte array (memory-efficient),
    // it is not possible to use short- or int-arrays here
    // because they use too much memory
    private final byte[][] bitmap;

    // bitmap width and height
    public final int width, height;

    /**
     * constructor for Bitmap,
     * needs a filled 2D-bitmap-array which must be created before.
     * not easy to use, maybe look at static methods
     * Bitmap.fromInt2d(...) or Bitmap.fromPosVector(...)
     *
     * @param bitmap is the bitmap 2D-array (pre-filled)
     */
    public Bitmap(byte[][] bitmap) {
        this.bitmap = bitmap;
        this.width = bitmap.length;
        if (this.width == 0) {
            throw new ArrayIndexOutOfBoundsException("Array width and length must be higher or equals one!");
        }
        this.height = bitmap[0].length;
        if (this.height == 0) {
            throw new ArrayIndexOutOfBoundsException("Array width and length must be higher or equals one!");
        }
    }


    /**
     * creates a Bitmap from pre-filled 2D-int-array
     * (much easier than creating a Bitmap per constructor)
     *
     * @param ints2d is the input 2D-int array, which is automatically converted to byte-array
     * @return Bitmap from 2D-int array
     */
    @NotNull
    public static Bitmap fromInt2d(int[][] ints2d) {

        int width = ints2d.length;
        if (width == 0) {
            throw new ArrayIndexOutOfBoundsException("input array is empty, width out of range!");
        }
        int height = ints2d[0].length;
        if (height == 0) {
            throw new ArrayIndexOutOfBoundsException("input array is empty, height out of range!");
        }

        int max = StandardToolkit.maxValue(ints2d);
        byte[][] bytes = new byte[width][height];
        int level;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                level = ints2d[x][y];
                bytes[x][y] = StandardToolkit.toByteLevel(level, max);
            }
        }
        return new Bitmap(bytes);
    }

    /**
     * creates a {@link Bitmap} from {@link Image} with filename
     *
     * @param imgFile is the image file
     * @return {@link Bitmap} from image
     * @throws FileNotFoundException if the file was not found
     * @throws IIOException if the image data is invalid
     */
    @NotNull
    public static Bitmap fromImage(@NotNull File imgFile)
            throws FileNotFoundException, IIOException {

        BufferedImage img;
        if (!imgFile.exists()) {
            throw new FileNotFoundException("No Bitmap found for filename " + imgFile.getName() + ", file must end with '.bmp'");
        }
        try {
            img = ImageIO.read(imgFile);
        } catch (IOException e) {
            throw new IIOException("Couldn't read bitmap image! Be sure you have a valid file type!", e);
        }
        return Bitmap.fromImage(img);
    }

    /**
     * creates a {@link Bitmap} from {@link BufferedImage}
     *
     * @param img is the {@link BufferedImage} to convert into {@link Bitmap}
     * @return {@link Bitmap}, converted from {@link BufferedImage}
     */
    @NotNull
    public static Bitmap fromImage(@NotNull BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        byte[][] bmpBytes = new byte[width][height];
        byte rgb;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rgb = (byte) img.getRGB(x, y);
                bmpBytes[x][y] = rgb;
            }
        }
        return new Bitmap(bmpBytes);
    }

    /**
     * writes a {@link Bitmap} to '.bmp' file with {@link ImageIO}
     *
     * @param bitmap is the {@link Bitmap} to write
     * @param filename is the filename of the {@link Bitmap} file
     * @return the written {@link File}
     * @throws IOException if an error occurs during the write operation
     */
    public static File write(Bitmap bitmap, String filename)
            throws IOException {

        // using write method with file parameter
        return write(bitmap, new File(filename));
    }

    /**
     * writes a {@link Bitmap} to a specific {@link File}
     *
     * @param bitmap is the {@link Bitmap} to write
     * @param file is the {@link File} to write the {@link Bitmap} into
     * @return the written {@link File}
     * @throws IOException if an error occurs during the write operation
     */
    public static File write(Bitmap bitmap, File file)
            throws IOException {

        String filename = file.getName();
        if (!filename.endsWith(".bmp")) {
            file = new File(filename + ".bmp");
        }
        ImageIO.write(bitmap.toImage(), "BMP", file);
        return file;
    }

    /**
     *
     *
     * @return
     */
    @NotNull
    public static File writeToCSV(@NotNull Bitmap bitmap, @NotNull String filename) {
        File file = new File(StandardToolkit.checkFileName(filename, "csv"));
        try (Writer fw = new FileWriter(file)) {

            int c, len;
            for (byte[] arr : bitmap.getBitmap()) {
                c = 0;
                len = arr.length - 1;
                for (byte lvl : arr) {
                    fw.write(lvl + (c++ == len ? "\n" : ","));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * reads a {@link Bitmap} from a specific {@link File}
     *
     * @param filename is the name of the {@link File} to read
     * @return the read {@link Bitmap}
     * @throws FileNotFoundException if the file does not exist
     */
    public static Bitmap read(String filename)
            throws IOException {

        return Bitmap.fromImage(new File(filename));
    }

    /**
     * converts this {@link Bitmap} to a {@link BufferedImage}
     *
     * @return {@link BufferedImage} displaying the {@link Bitmap}
     */
    public BufferedImage toImage() {
        var img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_GRAY);
        short lvl;
        Color color;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                // this should be an unsigned byte,
                //  but there is no unsigned byte in Java,
                //  so we use short here
                lvl = (short) (255 - (this.bitmap[x][y] + 128));
                color = new Color(lvl, lvl, lvl);
                img.setRGB(x, y, color.getRGB());
            }
        }
        return StandardToolkit.rotate(img, 180, BufferedImage.TYPE_BYTE_GRAY, true);
    }

    /**
     * creates a 1D-byte-array from the 2D-{@link Bitmap}-array
     *
     * @return 1D-array of this {@link Bitmap}
     */
    public byte[] getByteArray() {
        byte[] bytes = new byte[this.width * this.height];
        int i = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++, i++) {
                bytes[i] = this.bitmap[x][y];
            }
        }
        return bytes;
    }

    /**
     * getter for the bitmap 2D-array
     *
     * @return the 2D-bitmap-array
     */
    public byte[][] getBitmap() {
        return this.bitmap;
    }

    /**
     * overwritten equals() method, compares two object first,
     * then comparing the bitmap values
     *
     * @param obj is another {@link Bitmap} to compare
     * @return true if the given {@link Bitmap} is equals this {@link Bitmap}, else false
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof Bitmap bmp && Arrays.deepEquals(this.bitmap, bmp.bitmap));
    }

    /**
     * overwritten getter for the {@link Bitmap} hash code
     *
     * @return hash code of the {@link Bitmap} class
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * overwritten toString() method returns this {@link Bitmap} as a {@link String}
     *
     * @return {@link String} of this {@link Bitmap} object
     */
    @Override
    public String toString() {
        return Arrays.deepToString(getBitmap());
    }

}
