package cat.util;

import java.io.*;
import java.util.Arrays;

public final class FileUtil {
    //so many useless variables but i'll leave them here just in case
    public static final String clientFolder = System.getenv("APPDATA") + File.separator + ".minecraft" + File.separator + "BlueZenith";
    public static final String configFolder = clientFolder + File.separator + "config";
    public static final File folder = new File(configFolder);

    public static BufferedWriter getWriter(boolean mcFolder, String path) {
        try {
            if(mcFolder || path.contains(".minecraft")) createConfigFolder();
            File file = new File(mcFolder ? FileUtil.clientFolder + File.separator + path : path);
            if(!file.exists()) file.createNewFile();
            return new BufferedWriter(new FileWriter(file));
        } catch(IOException exception) {
            ClientUtils.getLogger().error("something went wrong while creating a BufferedWriter for path " + path + "\nclient folder? " + mcFolder);
            exception.printStackTrace();
        }
        return null;
    }

    public static BufferedReader getReader(boolean mcFolder, String path) {
        try {
            if(mcFolder || path.contains(".minecraft")) createConfigFolder();
            File file = new File(mcFolder ? FileUtil.clientFolder + File.separator + path : path);
            if(!file.exists()) file.createNewFile();
            return new BufferedReader(new FileReader(file));
        } catch(IOException ex) {
            ClientUtils.getLogger().error("something went wrong while creating a BufferedReader for path " + path + "\nclient folder? " + mcFolder);
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean exists(boolean mcFolder, String path, String name) {
        File file = new File(mcFolder ? FileUtil.clientFolder : path);
        return file.list() != null && Arrays.stream(file.list()).filter(a -> a.equalsIgnoreCase(name)).findFirst().orElse(null) != null;
    }

    public static void createConfigFolder() {
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }
}
