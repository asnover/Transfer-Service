package me.snover.config;

import com.moandjiezana.toml.Toml;
import me.snover.TransferService;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;

public class CompositeConfiguration {
    private final Path DATA_DIRECTORY;
    private final File SECRET_TOML_FILE;
    private Toml SECRET_TOML;

    public CompositeConfiguration(TransferService plugin) {
        DATA_DIRECTORY = plugin.getDataDirectory();
        SECRET_TOML_FILE = new File(DATA_DIRECTORY.toString() + "secret.toml");
        plugin.getLogger().debug(SECRET_TOML_FILE.getPath());
        OutputStream out;
        if(!SECRET_TOML_FILE.exists()) {
            //Write a new blank file. (Either make a new one, or load the current one inside the jar file)
        }
        SECRET_TOML = new Toml().read(SECRET_TOML_FILE);
    }

    public void load() {

    }
}
