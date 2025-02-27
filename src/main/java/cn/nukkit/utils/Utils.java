package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.*;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.network.protocol.ProtocolInfo;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class contains miscellaneous stuff used in other parts of the program.
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class Utils {

    /**
     * A SplittableRandom you can use without having to create a new object every time.
     */
    public static final SplittableRandom random = new SplittableRandom();
    /**
     * A NukkitRandom you can use without having to create a new object every time.
     */
    public static final NukkitRandom nukkitRandom = new NukkitRandom();
    /**
     * An empty damage array used when mobs have no attack damage.
     */
    public static final int[] emptyDamageArray = new int[] { 0, 0, 0, 0 };
    /**
     * List of network ids of monsters. Currently used for example to check which entities will make players unable to sleep when nearby the bed.
     */
    public static final IntSet monstersList = new IntOpenHashSet(Arrays.asList(EntityBlaze.NETWORK_ID, EntityCaveSpider.NETWORK_ID, EntityCreeper.NETWORK_ID, EntityDrowned.NETWORK_ID, EntityElderGuardian.NETWORK_ID, EntityEnderman.NETWORK_ID, EntityEndermite.NETWORK_ID, EntityEvoker.NETWORK_ID, EntityGhast.NETWORK_ID, EntityGuardian.NETWORK_ID, EntityHoglin.NETWORK_ID, EntityHusk.NETWORK_ID, EntityPiglinBrute.NETWORK_ID, EntityPillager.NETWORK_ID, EntityRavager.NETWORK_ID, EntityShulker.NETWORK_ID, EntitySilverfish.NETWORK_ID, EntitySkeleton.NETWORK_ID, EntitySlime.NETWORK_ID, EntitySpider.NETWORK_ID, EntityStray.NETWORK_ID, EntityVex.NETWORK_ID, EntityVindicator.NETWORK_ID, EntityWitch.NETWORK_ID, EntityWither.NETWORK_ID, EntityWitherSkeleton.NETWORK_ID, EntityZoglin.NETWORK_ID, EntityZombie.NETWORK_ID, EntityZombiePigman.NETWORK_ID, EntityZombieVillager.NETWORK_ID, EntityZombieVillagerV2.NETWORK_ID));
    /**
     * List of biomes where water can freeze
     */
    public static final IntSet freezingBiomes = new IntOpenHashSet(Arrays.asList(10, 11, 12, 26, 30, 31, 140, 158));

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile(fileName, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(String fileName, InputStream content) throws IOException {
        writeFile(new File(fileName), content);
    }

    public static void writeFile(File file, String content) throws IOException {
        writeFile(file, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream stream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = content.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
        }
        content.close();
    }

    public static String readFile(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return readFile(new FileInputStream(file));
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return readFile(new FileInputStream(file));
    }

    public static String readFile(InputStream inputStream) throws IOException {
        return readFile(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static String readFile(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String temp;
            StringBuilder stringBuilder = new StringBuilder();
            temp = br.readLine();
            while (temp != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append('\n');
                }
                stringBuilder.append(temp);
                temp = br.readLine();
            }
            return stringBuilder.toString();
        }
    }

    public static void copyFile(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new FileNotFoundException();
        }
        if (from.isDirectory() || to.isDirectory()) {
            throw new FileNotFoundException();
        }
        FileInputStream fi = null;
        FileChannel in = null;
        FileOutputStream fo = null;
        FileChannel out = null;
        try {
            if (!to.exists()) {
                to.createNewFile();
            }
            fi = new FileInputStream(from);
            in = fi.getChannel();
            fo = new FileOutputStream(to);
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } finally {
            if (fi != null) fi.close();
            if (in != null) in.close();
            if (fo != null) fo.close();
            if (out != null) out.close();
        }
    }

    public static String getAllThreadDumps() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        StringBuilder builder = new StringBuilder();
        for (ThreadInfo info : threads) {
            builder.append('\n').append(info);
        }
        return builder.toString();
    }


    public static String getExceptionMessage(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            printWriter.flush();
        }
        return stringWriter.toString();
    }

    public static UUID dataToUUID(String... params) {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param);
        }
        return UUID.nameUUIDFromBytes(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static UUID dataToUUID(byte[]... params) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (byte[] param : params) {
            try {
                stream.write(param);
            } catch (IOException e) {
                break;
            }
        }
        return UUID.nameUUIDFromBytes(stream.toByteArray());
    }

    public static String rtrim(String s, char character) {
        int i = s.length() - 1;
        while (i >= 0 && (s.charAt(i)) == character) {
            i--;
        }
        return s.substring(0, i + 1);
    }

    public static boolean isByteArrayEmpty(final byte[] array) {
        for (byte b : array) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    public static long toRGB(byte r, byte g, byte b, byte a) {
        long result = (int) r & 0xff;
        result |= ((int) g & 0xff) << 8;
        result |= ((int) b & 0xff) << 16;
        result |= ((int) a & 0xff) << 24;
        return result & 0xFFFFFFFFL;
    }

    public static long toABGR(int argb) {
        long result = argb & 0xFF00FF00L;
        result |= (argb << 16) & 0x00FF0000L; // B to R
        result |= (argb >>> 16) & 0xFFL; // R to B
        return result & 0xFFFFFFFFL;
    }

    public static Object[][] splitArray(Object[] arrayToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            return null;
        }

        int rest = arrayToSplit.length % chunkSize;
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);

        Object[][] arrays = new Object[chunks][];
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if (rest > 0) {
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays;
    }

    public static <T> void reverseArray(T[] data) {
        reverseArray(data, false);
    }

    public static <T> T[] reverseArray(T[] array, boolean copy) {
        T[] data = array;

        if (copy) {
            data = Arrays.copyOf(array, array.length);
        }

        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            T temp = data[left];
            data[left] = data[right];
            data[right] = temp;
        }

        return data;
    }

    public static <T> T[][] clone2dArray(T[][] array) {
        T[][] newArray = Arrays.copyOf(array, array.length);

        for (int i = 0; i < array.length; i++) {
            newArray[i] = Arrays.copyOf(array[i], array[i].length);
        }

        return newArray;
    }

    public static <T,U,V> Map<U,V> getOrCreate(Map<T, Map<U, V>> map, T key) {
        Map<U, V> existing = map.get(key);
        if (existing == null) {
            ConcurrentHashMap<U, V> toPut = new ConcurrentHashMap<>();
            existing = map.putIfAbsent(key, toPut);
            if (existing == null) {
                existing = toPut;
            }
        }
        return existing;
    }

    public static <T, U, V extends U> U getOrCreate(Map<T, U> map, Class<V> clazz, T key) {
        U existing = map.get(key);
        if (existing != null) {
            return existing;
        }
        try {
            U toPut = clazz.newInstance();
            existing = map.putIfAbsent(key, toPut);
            if (existing == null) {
                return toPut;
            }
            return existing;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int toInt(Object number) {
        if (number instanceof Integer) {
            return (Integer) number;
        }

        return (int) Math.round((double) number);
    }

    public static byte[] parseHexBinary(String s) {
        final int len = s.length();

        if (len % 2 != 0)
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);

        byte[] out = new byte[(len >> 1)];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1)
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);

            out[(i >> 1)] = (byte)((h << 4) + l);
        }

        return out;
    }

    private static int hexToBin( char ch ) {
        if ('0' <= ch && ch <= '9')    return ch - '0';
        if ('A' <= ch && ch <= 'F')    return ch - 'A' + 10;
        if ('a' <= ch && ch <= 'f')    return ch - 'a' + 10;
        return -1;
    }

    /**
     * Get a random int
     *
     * @param min minimum value
     * @param max maximum value
     * @return random int between min and max
     */
    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return random.nextInt(max + 1 - min) + min;
    }

    /**
     * Get a random double
     *
     * @param min minimum value
     * @param max maximum value
     * @return random double between min and max
     */
    public static double rand(double min, double max) {
        if (min == max) {
            return max;
        }
        return min + random.nextDouble() * (max-min);
    }

    /**
     * Get a random boolean
     *
     * @return random boolean
     */
    public static boolean rand() {
        return random.nextBoolean();
    }

    /**
     * Get game version string by protocol version.
     * For internal usage!
     *
     * @param protocol protocol version
     * @return game version string
     */
    public static String getVersionByProtocol(int protocol) {
        switch (protocol) {
            case ProtocolInfo.v1_2_0:
                return "1.2.0";
            case ProtocolInfo.v1_2_5_11:
            case ProtocolInfo.v1_2_5:
                return "1.2.5";
            case ProtocolInfo.v1_2_6:
                return "1.2.6";
            case ProtocolInfo.v1_2_7:
                return "1.2.7";
            case ProtocolInfo.v1_2_10:
                return "1.2.10";
            case ProtocolInfo.v1_2_13:
            case ProtocolInfo.v1_2_13_11:
                return "1.2.13";
            case ProtocolInfo.v1_4_0:
                return "1.4.0";
            case ProtocolInfo.v1_5_0:
                return "1.5.0";
            case ProtocolInfo.v1_6_0_5:
            case ProtocolInfo.v1_6_0:
                return "1.6.0";
            case ProtocolInfo.v1_7_0:
                return "1.7.0";
            case ProtocolInfo.v1_8_0:
                return "1.8.0";
            case ProtocolInfo.v1_9_0:
                return "1.9.0";
            case ProtocolInfo.v1_10_0:
                return "1.10.0";
            case ProtocolInfo.v1_11_0:
                return "1.11.0";
            case ProtocolInfo.v1_12_0:
                return "1.12.0";
            case ProtocolInfo.v1_13_0:
                return "1.13.0";
            case ProtocolInfo.v1_14_0:
                return "1.14.0";
            case ProtocolInfo.v1_14_60:
                return "1.14.60";
            case ProtocolInfo.v1_16_0:
                return "1.16.0";
            case ProtocolInfo.v1_16_20:
                return "1.16.20";
            case ProtocolInfo.v1_16_100_0:
            case ProtocolInfo.v1_16_100_51:
            case ProtocolInfo.v1_16_100_52:
            case ProtocolInfo.v1_16_100:
                return "1.16.100";
            case ProtocolInfo.v1_16_200_51:
            case ProtocolInfo.v1_16_200:
                return "1.16.200";
            case ProtocolInfo.v1_16_210_50:
            case ProtocolInfo.v1_16_210_53:
            case ProtocolInfo.v1_16_210:
                return "1.16.210";
            case ProtocolInfo.v1_16_220:
                return "1.16.220";
            case ProtocolInfo.v1_16_230_50:
            case ProtocolInfo.v1_16_230:
            case ProtocolInfo.v1_16_230_54:
                return "1.16.230";
            case ProtocolInfo.v1_17_0:
                return "1.17.0";
            case ProtocolInfo.v1_17_10:
                return "1.17.10";
            case ProtocolInfo.v1_17_20_20:
                return "1.17.20";
            case ProtocolInfo.v1_17_30:
                return "1.17.30";
            case ProtocolInfo.v1_17_40:
                return "1.17.40";
            default:
                throw new IllegalStateException("Invalid protocol: " + protocol);
        }
    }

    /**
     * Get player's operating system/device name from login chain data.
     * NOTICE: It's possible to spoof this.
     *
     * @param player player
     * @return operating system/device name
     */
    public static String getOS(Player player) {
        switch(player.getLoginChainData().getDeviceOS()) {
            case 1:
                return "Android";
            case 2:
                return "iOS";
            case 3:
                return "macOS";
            case 4:
                return "Fire";
            case 5:
                return "Gear VR";
            case 6:
                return "HoloLens";
            case 7:
                return "Windows 10";
            case 8:
                return "Windows";
            case 9:
                return "Dedicated";
            case 10:
                return "tvOS";
            case 11:
                return "PlayStation";
            case 12:
                return "Switch";
            case 13:
                return "Xbox";
            case 14:
                return "Windows Phone";
            default:
                return "Unknown";
        }
    }
}
