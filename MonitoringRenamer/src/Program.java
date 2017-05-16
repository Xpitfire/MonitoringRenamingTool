import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by xpitfire on 15/05/2017.
 */
public class Program {

    public static final String COUNTER = "counter.txt";
    public static final String OUTPUT_DIR = "processed";
    public static final int DEFAULT_INTERVAL = 30;
    public static final String LOGGER_FILE_NAME = "MonitoringRenamer.log";

    private static Logger logger;

    private static String path = null;
    private static String prefix = null;
    private static String outputDir = OUTPUT_DIR;
    private static int interval = DEFAULT_INTERVAL;
    private static int counter = loadOrCreateCounter();

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        initLogger();

        try {

            if (args == null
                    && args.length < 1) {
                System.err.println("Invalid parameter list!");
                System.err.println("Missing path and text prefix.");
                printErrorHelp();
                return;
            }

            int idx = 0;

            if (args.length > 2
                    && args[idx].equals("-path")) {
                path = args[++idx];
            } else {
                System.err.println("Invalid path parameter!");
                printErrorHelp();
                return;
            }

            if (args.length > 3
                    && args[++idx].equals("-file-prefix")) {
                prefix = args[++idx];
            } else {
                System.err.println("Invalid file prefix parameter!");
                printErrorHelp();
                return;
            }

            if (args.length > 5
                    && args[++idx].equals("-output")) {
                outputDir = args[++idx];
            }

            if (args.length > 7
                    && args[++idx].equals("-interval")) {
                interval = Integer.parseInt(args[++idx]);
            }

            System.out.printf("Initialized path=(%s), prefix=(%s), output=(%s), interval=(%d)\n",
                    path, prefix, outputDir, interval);
            final File folder = new File(path);

            while (true) {
                System.out.println("Scanning directories...");
                scanAndUpdateFiles(folder);
                Thread.sleep(interval * 1000);
            }

        } catch (Exception ex) {

            System.err.println("Application crashed: " + ex.getMessage());
            logger.log(Level.SEVERE, "Application crashed", ex);

        } finally {
            writeFileCounter(counter);
        }
    }

    private static void printErrorHelp() {
        System.err.println("usage> java MonitoringRenamer.jar -path <path-to-monitor> -file-prefix <prefix> -output <path-to-output> -interval <seconds>");
        System.err.println("example> java MonitoringRenamer.jar -path C:\\test -file-prefix file_ -output C:\\processed -interval 10");
        logger.severe("Invalid application parameter> " +
            String.format("path=(%s), prefix=(%s), output=(%s), interval=(%d)\n",
                    path, prefix, outputDir, interval));
    }

    private static void scanAndUpdateFiles(final File folder)
            throws IOException {
        if (outputDir.equals(folder)) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        System.out.println(folder + ":");
        for (final File fileEntry : files) {
            if (fileEntry.isDirectory()) {
                scanAndUpdateFiles(fileEntry);
            } else {
                String oldFileName = fileEntry.getName();
                String newFileName = String.format("%s%d%s", prefix, ++counter, getFileExtension(oldFileName));
                System.out.println("Renaming: " + oldFileName + " to " + newFileName);
                logger.info("Renaming: " + folder.getAbsolutePath() + " from: " + oldFileName + " -> " + outputDir + " to: " + newFileName);
                renameAndMoveFile(oldFileName, newFileName, folder);
            }
        }
        System.out.println();

        writeFileCounter(counter);
    }

    private static int loadOrCreateCounter() {
        int value = 0;
        File file = new File(COUNTER);
        try {
            if (file.exists()) {
                String str = readCounterFileAsString(file);
                value = Integer.parseInt(str);
            } else {
                writeFileCounter(++value);
            }
        } catch (IOException e) {
            System.err.println("Could not write COUNTER file! Check your file system permissions.");
            logger.log(Level.SEVERE, "Could not write COUNTER file!", e);
            System.exit(1);
        }
        return value;
    }

    private static void renameAndMoveFile(String oldFileName, String newFileName, File folder) throws IOException {
        // File (or directory) with old name
        File oldFile = new File(folder, oldFileName);

        File output = new File(outputDir);
        if (!output.exists()) {
            output.mkdir();
        }

        // File (or directory) with new name
        File newFile = new File(outputDir, newFileName);

        if (newFile.exists())
            throw new java.io.IOException("New file name already exists!");

        // Rename file (or directory)
        boolean success = oldFile.renameTo(newFile);
        if (!success) {
            // File was not successfully renamed
            throw new java.io.IOException("Failed to rename file! ");
        }
    }

    private static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = "." + fileName.substring(i + 1);
        }
        return extension;
    }

    private static void writeFileCounter(int value)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(COUNTER, "UTF-8");
        writer.println(value);
        writer.close();
    }

    private static String readCounterFileAsString(File file) throws IOException {
        // Open the file
        FileInputStream stream = new FileInputStream(COUNTER);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String strLine = null;
        try {
            //Read File Line By Line
            if ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                System.out.println("Loaded counter: " + strLine);
            }
        } finally {
            br.close();
            stream.close();
        }
        return strLine;
    }

    private static void initLogger() {
        logger = Logger.getLogger("MonitoringRenamer");
        FileHandler fh;
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler(LOGGER_FILE_NAME);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
