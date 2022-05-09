package dev.johny.bungeecmds;

import dev.johny.bungeecmds.backend.Backend;
import dev.johny.bungeecmds.commands.Commands;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Main extends Plugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Backend.makeBackend();
        Commands.makeCommands();

        BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§6[sBungeeCmds] Plugin iniciado com sucesso."));

    }

    public static Main getInstance() {
        return instance;
    }

    private Configuration config ;

    public void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Configuration getConfig() {
        return config;
    }


}
