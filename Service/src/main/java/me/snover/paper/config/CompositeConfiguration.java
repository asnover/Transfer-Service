package me.snover.paper.config;

import com.moandjiezana.toml.Toml;
import me.snover.paper.TransferService;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class handles the configuration file for the plugin
 */
public class CompositeConfiguration {
    private final TransferService PLUGIN;
    private final Path WORKING_DIRECTORY;
    private final File SECRET_TOML_FILE;
    private Toml SECRET_TOML;
    private static String secret;

    /**
     * Constructs the class for the TOML configuration. If the file does not exist, one will be created.
     * @param plugin The Transfer plugin itself
     * @throws IOException Throws IOException when the file cannot be extracted or saved
     */
    public CompositeConfiguration(TransferService plugin) throws IOException {
        //Init
        PLUGIN = plugin;
        WORKING_DIRECTORY = Path.of(System.getProperty("user.dir"));
        SECRET_TOML_FILE = new File(WORKING_DIRECTORY + "/plugins/TransferService/secret.toml");
        //Debug
        plugin.getLogger().debug(SECRET_TOML_FILE.getPath());
        //Check if a secret TOML file exists. If it does not, write a new one.
        if(!SECRET_TOML_FILE.exists()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                //If the directories do not exist, create them.
                File pluginDir = new File(WORKING_DIRECTORY + "/plugins/TransferService/");
                if (!pluginDir.exists()) Files.createDirectories(Paths.get(pluginDir.getPath()));

                //Begin TOML extraction
                URL url = new URL("jar:file:" + WORKING_DIRECTORY + "/plugins/TransferService.jar!/secret.toml");
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                JarFile jarFile = connection.getJarFile();
                JarEntry jarEntry = connection.getJarEntry();
                in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
                out = new BufferedOutputStream(new FileOutputStream(WORKING_DIRECTORY + "/plugins/TransferService/secret.toml"));

                //Begin writing new TOML
                byte[] buffer = new byte[2048];
                for(;;) {
                    int nBytes = in.read(buffer);
                    if(nBytes <= 0) break;
                    out.write(buffer, 0, nBytes);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if(in != null) in.close();
                if(out != null) {
                    out.flush();
                    out.close();
                }
            }
        }
    }

    /**
     * Read the TOML file on the disk and save the settings. Any settings currently loaded into memory will be overwritten.
     */
    public void load() {
        SECRET_TOML = new Toml().read(SECRET_TOML_FILE);
        secret = SECRET_TOML.getString("secret");
        PLUGIN.getLogger().debug(secret);
    }

    /**
     *
     * @return Returns a {@link String} with the secret key
     */
    public static String getSecret() {
        return secret;
    }
}
