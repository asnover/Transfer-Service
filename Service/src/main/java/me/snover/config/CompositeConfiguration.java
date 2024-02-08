package me.snover.config;

import com.moandjiezana.toml.Toml;
import me.snover.TransferService;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CompositeConfiguration {
    private final TransferService PLUGIN;
    private final Path WORKING_DIRECTORY;
    private final File SECRET_TOML_FILE;
    private Toml SECRET_TOML;
    private static String secret;

    public CompositeConfiguration(TransferService plugin) throws IOException {
        PLUGIN = plugin;
        WORKING_DIRECTORY = Path.of(System.getProperty("user.dir"));
        SECRET_TOML_FILE = new File(WORKING_DIRECTORY + "/plugins/TransferService/secret.toml");
        plugin.getLogger().debug(SECRET_TOML_FILE.getPath());
        if(!SECRET_TOML_FILE.exists()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                File pluginDir = new File(WORKING_DIRECTORY + "/plugins/TransferService/");
                if (!pluginDir.exists()) Files.createDirectories(Paths.get(pluginDir.getPath()));

                URL url = new URL("jar:file:" + WORKING_DIRECTORY + "/plugins/TransferService.jar!/secret.toml");
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                JarFile jarFile = connection.getJarFile();
                JarEntry jarEntry = connection.getJarEntry();
                in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
                out = new BufferedOutputStream(new FileOutputStream(WORKING_DIRECTORY + "/plugins/TransferService/secret.toml"));

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

    public void load() {
        SECRET_TOML = new Toml().read(SECRET_TOML_FILE);
        secret = SECRET_TOML.getString("secret");
        PLUGIN.getLogger().debug(secret);
    }

    public static String getSecret() {
        return secret;
    }
}
