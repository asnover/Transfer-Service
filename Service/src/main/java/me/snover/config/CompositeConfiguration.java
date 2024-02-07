package me.snover.config;

import com.moandjiezana.toml.Toml;
import me.snover.TransferService;

import java.io.*;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CompositeConfiguration {
    private final Path DATA_DIRECTORY;
    private final File SECRET_TOML_FILE;
    private Toml SECRET_TOML;

    public CompositeConfiguration(TransferService plugin) throws IOException {
        DATA_DIRECTORY = plugin.getDataDirectory();
        SECRET_TOML_FILE = new File(DATA_DIRECTORY.toString() + "\\secret.toml");
        plugin.getLogger().debug(SECRET_TOML_FILE.getPath());
        if(!SECRET_TOML_FILE.exists()) {
            //Write a new blank file. (Either make a new one, or load the current one inside the jar file)
            InputStream in = null;
            OutputStream out = null;
            plugin.getLogger().debug(DATA_DIRECTORY.toString());
            try {


                plugin.getLogger().debug(DATA_DIRECTORY.toString());
                URL url = new URL("jar:file:plugins/TransferClient.jar!/secret.toml");
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                JarFile jarFile = connection.getJarFile();
                JarEntry jarEntry = connection.getJarEntry();
                File pluginDir = new File("plugins/TransferService/");
                if (!pluginDir.exists()) Files.createDirectories(Paths.get(pluginDir.getPath()));
                in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
                out = new BufferedOutputStream(new FileOutputStream("~/plugins/TransferService"));

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
        SECRET_TOML = new Toml().read(SECRET_TOML_FILE);
    }

    public void load() {

    }
}
