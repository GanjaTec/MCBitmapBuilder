package de.jml.external.util;

import de.jml.external.util.math.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author jml04
 * @author Lukas
 * @author Bennet
 * @version 1.0
 *
 * @description
 * class Utilities contains different utility-methods for different usages
 */
public abstract class StandardToolkit {

    /**
     * char-values connected to hex-int-values
     */
    public static final Map<Character, Integer> charIntValues = new HashMap<>(16);

    // initialing the char-int map
    static {
        initCharInts();
    }

    /**
     * initializes the charIntValues-Map which contains char-values connected to hex-int-values
     */
    private static void initCharInts() {
        charIntValues.put('0', 0x000000);
        charIntValues.put('1', 0x000001);
        charIntValues.put('2', 0x000002);
        charIntValues.put('3', 0x000003);
        charIntValues.put('4', 0x000004);
        charIntValues.put('5', 0x000005);
        charIntValues.put('6', 0x000006);
        charIntValues.put('7', 0x000007);
        charIntValues.put('8', 0x000008);
        charIntValues.put('9', 0x000009);
        charIntValues.put('a', 0x00000A);
        charIntValues.put('b', 0x00000B);
        charIntValues.put('c', 0x00000C);
        charIntValues.put('d', 0x00000D);
        charIntValues.put('e', 0x00000E);
        charIntValues.put('f', 0x00000F);
    }

    /**
     * getter for the project root-directory,
     * should only be invoked while starting the program
     *
     * @return absolute root directory name as String
     */
    @NotNull
    public static String getAbsoluteRootPath() {
        return System.getProperty("user.dir") + "\\";
    }

    /**
     * does a connection pre-check for all given {@link URL}s
     *
     * @param timeoutMillis is the maximum request time
     * @param urls are the {@link URL}s to check
     * @throws ConnectException if a connection check failed
     */
    public static void connectionPreCheck(int timeoutMillis, @NotNull URL... urls) throws ConnectException {
        URLConnection conn;
        InetAddress address = null;
        String hostName;
        for (URL url : urls) {
            try {
                conn = url.openConnection();
                conn.setConnectTimeout(timeoutMillis);
                conn.connect();
                address = InetAddress.getByName(url.getHost());
                if (!address.isReachable(timeoutMillis)) {
                    throw new IOException();
                }
            } catch (IOException e) {
                hostName = address == null ? "N/A" : address.getHostName();
                throw new ConnectException("address " + hostName + "is not reachable!");
            }
        }
    }

    /**
     * creates an URI-{@link String} by its parts
     *
     * @param scheme is the URI scheme (e.g. http or https)
     * @param host is the URI hostname
     * @param port os the URI port
     * @param path is the URI path
     * @param query is the URI query
     * @param fragment is the URI fragment
     * @return a new URI-{@link String}, composed of these parts
     */
    @NotNull
    public static String createURI(@Nullable String scheme, @NotNull String host, @Nullable String port,
                                @Nullable String path, @Nullable String query, @Nullable String fragment) {

        if (host.isBlank()) {
            throw new IllegalArgumentException("Host may not be blank");
        }
        return  (scheme == null ? "" : scheme + "://") + host +
                (port == null ? "" : ":" + port + "/") +
                (path == null ? "" : path) +
                (query == null ? "" : "?" + query) +
                (fragment == null ? "" : "#" + fragment);


    }

    /**
     * plays a sound from the default toolkit
     *
     * @param sound is the sound to be played
     */
    public static boolean playSound(@NotNull final String sound) {
        Runnable sound2 = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty(sound);
        if (sound2 != null) {
            sound2.run();
            return true;
        }
        return false;
    }

    /**
     * finds the highest value of an 2D int array,
     * 0 is the minimum value
     *
     * @param of is the 2D int array to search in
     * @return the highest value found in the input array
     */
    public static int maxValue(final int @NotNull [][] of) {
        int max = 0;
        for (int[] line : of) {
            max = findMax(line, max);
        }
        return max;
    }

    /**
     * finds the highest value in an array in O(n)
     *
     * @param in is the input-2D-array to search in
     * @param startValue is the start max-value
     * @return max value of the input int-array
     */
    private static int findMax(final int[] in, final int startValue) {
        int max = startValue;

        for (int curr : in) {
            if (curr > max) {
                max = curr;
            }
        }
        return max;
    }

    /**
     * converts a decimal-int to hex-int,
     * does not work yet
     *
     * @param dec is the decimal
     * @return hexadecimal of the decimal-input
     */
    public static long decToHex(int dec) {
        return Long.parseLong(Integer.toHexString(dec), 16);
    }

    /**
     * converts a hex-string (e.g. "0xF62BA5" or "0xffa5b3") to an int
     *
     * @param hexStr is the hexadecimal input value as String
     * @return hex string as int
     */
    public static int hexStrToInt(@NotNull String hexStr) {
        char[] chars = hexStr.toCharArray();
        int pow = 0x000000,
            hex = 0x000000,
            num;
        for (char c : chars) {
            num = charIntValues.get(c);
            hex += num * StrictMath.pow(0x000010, pow);
            pow += 0x000001;
        }
        return hex;
    }

    /**
     * converts an int-level to a byte-level depending on
     * the max-int-level (calculated before)
     *
     * @param lvl is the level to convert
     * @param max is the max level, which must be calculated before this method
     * @return input level as byte level (-128 - 127)
     */
    public static byte toByteLevel(int lvl, int max) {
        if (lvl < 0 || max <= 0) {
            throw new IllegalArgumentException("level or max-level is out of range (0-\u221E)");
        } else if (lvl == 0) {
            return -128;
        }
        // got this calculation from internet, easier than the one before and more pretty numbers
        int rest = lvl % 256;
        return (byte) (rest - 256);
    }

    /**
     * converts a feet-value to a meters-value
     *
     * @param feet is the input, in feet (ft)
     * @return a feet value in meters
     */
    public static int feetToMeters(@Range(from = 0, to = Integer.MAX_VALUE) int feet) {
        return asInt(MathUtils.divide(feet, 3.2808));
    }

    /**
     * converts a knots-value to a km/h-value
     *
     * @param kn is the input, in knots (kn)
     * @return the knots in km per hour
     */
    public static int knToKmh(@Range(from = 0,to = Integer.MAX_VALUE) int kn) {
        return asInt(Math.round(kn * 1.852));
    }

    /**
     * converts a {@link Number} to an {@link Integer}, not directly by casting,
     * but by just calling the intValue() method from the Number class
     *
     * @param number is the number to be cast to an int
     * @param <N> is an instance of Number
     * @return number cast as int
     */
    public static <N extends Number> int asInt(@NotNull final N number) {
        boolean primitive = number.getClass().isPrimitive();
        return primitive ? (int) number : number.intValue();
    }

    /**
     * converts an array or a collection to Deque
     *
     * @param arrayOrCollection is the input value, any array or collection
     * @param <T> is the input type, stands for array or collection
     * @return Deque consisting of the input values
     */
    @NotNull
    public static <T> Deque<?> parseDeque(@NotNull T arrayOrCollection) {
        if (arrayOrCollection instanceof Collection<?> collection) {
            return new ArrayDeque<>(collection);
        } else if (arrayOrCollection instanceof Object[] arr) {
            return new ArrayDeque<>(List.of(arr));
        } else if (arrayOrCollection.getClass().isArray()) {
            int length = Array.getLength(arrayOrCollection);
            Deque<Object> dq = new ArrayDeque<>();
            for (int i = 0; i < length; i++) {
                dq.add(Array.get(arrayOrCollection, i));
            }
            return dq;
        }
        throw new IllegalArgumentException("incorrect input, array or collection expected!");
    }

    /**
     * converts a {@link Deque} of {@link Integer}s to an int-array
     *
     * @param deque is the Integer-Deque to parse
     * @return converted int-array
     */
    public static int[] parseIntArray(@NotNull Deque<Integer> deque) {
        return Arrays.stream(deque.toArray(Integer[]::new))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * adds a custom icon to the {@link SystemTray}
     *
     * @param icon is the added icon ({@link Image})
     * @param onClick is the ({@link FunctionalInterface}) {@link ActionListener} which is executed on icon click
     * @return true if the icon was added to the {@link SystemTray}, else false
     */
    public static boolean addTrayIcon(@NotNull Image icon, @NotNull ActionListener onClick) {
        if (!SystemTray.isSupported()) {
            return false;
        }
        TrayIcon trayIcon = new TrayIcon(icon);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(onClick);
        try {
            SystemTray.getSystemTray().add(trayIcon);
            return true;
        } catch (AWTException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * checks a {@link java.net.http.HttpResponse} status code,
     * does nothing if it is 200 (default),
     * if it is not 200, an exception is thrown
     *
     * @param status is the status code, given by the {@link java.net.http.HttpResponse}
     * @throws RuntimeException when the status code is invalid FIXME no RuntimeException
     */
    public static void checkStatusCode(int status) {
        String invalidMsg = "CheckStatus: Status code '" + status + "' is invalid!";
        RuntimeException stex = switch (status) {
            case 200, 201 -> null; // status code is OK
            case 403 -> new RuntimeException(invalidMsg + "\nError 403, Forbidden");
            case 451 -> new RuntimeException(invalidMsg + "\nSeems like there is a problem with the Http-header (User-Agent)!");
            default -> new RuntimeException(invalidMsg + "\nUnknown error!");
        };
        if (stex != null) {
            throw stex;
        }
    }


    // string utilities

    /**
     * packs a string in the format 'myText'
     *
     * @param str is the string to pack
     * @return packed input string with 's
     */
    @NotNull
    public static String packString(@NotNull String str) {
        return "'" + str + "'";
    }

    /**
     * strips a string to the right format
     * Example: from ' "Hello" ' to ' Hello '
     *
     * @param in is the string to strip
     * @return input-string, but without the "s
     */
    @NotNull
    public static String stripString(@NotNull String in) {
        return in.replaceAll("\"", "");
    }

    /**
     * checks a string for illegal characters or expressions
     *
     * @param check is the (sql) string to check
     * @return string, without illegal characters/expressions
     */
    @NotNull
    public static String checkForSQL(@NotNull String check)
            throws IllegalAccessException {

        if (       check.contains("*")      || check.contains(";")
                || check.contains("SELECT") || check.contains("select")
                || check.contains("JOIN")   || check.contains("join")
                || check.contains("DROP")   || check.contains("drop")
                || check.contains("INSERT") || check.contains("insert")
                || check.contains("FROM")   || check.contains("from")
                || check.contains("TABLE")  || check.contains("--")) {
            // throwing new exception because of illegal data in the input string
            throw new IllegalAccessException("Input expressions or characters not allowed!");
        }
        // replacing all '%', to prevent inputs like '%.....%', which take too much time to search for
        // '%' is a SQL-placeholder for everything with any length
        return check.replaceAll("%", "");
    }

    /**
     *
     *
     * @param filename
     * @param expectedFormat
     * @return
     */
    @NotNull
    public static String checkFileName(@NotNull String filename, @NotNull String expectedFormat) {
        if (filename.isBlank() || expectedFormat.isBlank()) {
            throw new IllegalArgumentException("No file name or format!");
        }
        expectedFormat = (expectedFormat.startsWith(".") ? "" : ".") + expectedFormat.toLowerCase();
        return filename.endsWith(expectedFormat) ? filename : filename + expectedFormat;
    }

    /**
     * checks an {@link URI} for illegal expressions
     *
     * @param uri is the {@link URI} to check
     * @throws IllegalArgumentException if an illegal expression was found
     */
    public static void checkUri(@NotNull URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new IllegalArgumentException("Wrong scheme, URI must begin with 'http' or 'https'!");
        }
        // TODO: 08.09.2022 weitere checks
    }


    // image utilities

    /**
     * brings an image in a certain scale
     *
     * @param img is the input image that should be scaled
     * @param width is the image output width
     * @param height is the image output height
     * @return a scaled instance of the input image
     */
    @NotNull
    public static ImageIcon scale(@NotNull ImageIcon img, int width, int height) {
        Image scaled = img.getImage().getScaledInstance(width, height, 4);
        return new ImageIcon(scaled);
    }

    /**
     * rotates an image by certain degrees
     * the rotating technique is a bit tricky, because we cannot
     * just do something like img.rotate(...), we have to create
     * a new, rotated Graphics first and then draw the image on it.
     *
     * @param img is the {@link Image} that should be rotated
     * @param degrees is the degree of the rotation, from 0 to 360
     * @param imageType is the image type constant from {@link BufferedImage}
     * @param flipHorizontally indicates if the image should be flipped horizontally
     * @return {@link BufferedImage} with specific rotation
     */
    @NotNull
    public static BufferedImage rotate(@NotNull Image img, @Range(from = 0, to = 360) final int degrees, int imageType, boolean flipHorizontally) {
        final int width = img.getWidth(null);
        final int height = img.getHeight(null);

        BufferedImage buf = new BufferedImage(width, height, imageType);
        Graphics2D graphics = buf.createGraphics();

        graphics.rotate(Math.toRadians(degrees), MathUtils.divide(width, 2), MathUtils.divide(height, 2));
        if (flipHorizontally) {
            graphics.drawImage(img, width, 0, -width, height, null);
        } else {
            graphics.drawImage(img, 0, 0, null);
        }

        return buf;
    }

    /**
     * converts an {@link Image} of any type (mostly ToolkitImages) to {@link BufferedImage}
     *
     * @param img is the {@link Image} to be converted
     * @param imgType is the image type constant from {@link BufferedImage}.'...'
     * @return new {@link BufferedImage}, converted from any {@link Image} type
     */
    @NotNull
    public static BufferedImage createBufferedImage(Image img, int imgType) {
        BufferedImage buf = new BufferedImage(img.getWidth(null), img.getHeight(null), imgType);
        Graphics2D g = buf.createGraphics();
        g.drawImage(img, 0, 0, null);
        return buf;
    }


    // file utilities

    /**
     * counts the lines of code with given file extensions
     *
     * @param rootPath is the root path to start counting
     * @param extensions are the file extensions
     * @return count of lines code (with the given extension)
     */
    public static int linesCode(@NotNull final String rootPath, @NotNull String... extensions) {
        if (extensions.length == 0) {
            throw new IllegalArgumentException("No given extensions!");
        }
        AtomicInteger linesCode = new AtomicInteger(0);
        Deque<Deque<File>> allFiles = new ArrayDeque<>();

        for (String ext : extensions) {
            if (!ext.startsWith(".")) {
                ext = "." + ext;
            }
            allFiles.add(allFilesWithExtension(rootPath, ext));
        }

        Deque<File> currentFiles;
        while (!allFiles.isEmpty()) {
            currentFiles = allFiles.poll();
            while (!currentFiles.isEmpty()) {
                try (   Reader fr = new FileReader(currentFiles.poll());
                        BufferedReader br = new BufferedReader(fr)) {

                    br.lines().forEach(l -> {
                        if (!l.isBlank()) {
                            linesCode.getAndIncrement();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return linesCode.get();
    }

    /**
     * returns all files, which have the given extension
     *
     * @param path is the root path to search in
     * @param extension is the file extension to search for (e.g. '.java')
     * @return Deque of all files with the given extension
     */
    @NotNull
    private static Deque<File> allFilesWithExtension(@NotNull final String path, @NotNull String extension) {
        if (!extension.startsWith(".")) {
            throw new IllegalArgumentException("extension must begin with '.'");
        }
        Deque<File> files = new ArrayDeque<>();
        Path start = Paths.get(path); // java.nio.file.Paths is not equal to our Paths-class
        // try with resource
        try (Stream<Path> paths = Files.walk(start)) {

            paths.forEach(p -> {
                File file = p.toFile();
                if (!file.isDirectory() && file.getName().endsWith(extension)) {
                    files.add(file);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }


    // reflection utilities

    /**
     * finds the {@link Class} who called this method
     *
     * @return the caller class of this method as a {@link Class} object
     */
    public static Class<?> getCallerClass() {
        StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return stackWalker.getCallerClass();
    }

    /**
     * this method prints all current field values
     * of the given object of any class
     *
     * @param o an object, containing the printed values
     * @param <E> is the object type (class)
     */
    @TestOnly
    public static <E> void printCurrentFields(@NotNull E o) {
        Class<?> classOfO = o.getClass();
        try {
            Field[] fields = classOfO.getDeclaredFields();
            Arrays.stream(fields).forEach(field -> {
                String name; Object value;
                try {
                    field.setAccessible(true);
                    name = field.getName();
                    value = field.get(o);
                    System.out.println(name + ": " + value);
                } catch (InaccessibleObjectException | IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

}
