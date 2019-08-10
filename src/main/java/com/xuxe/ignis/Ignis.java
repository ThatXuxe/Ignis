package com.xuxe.ignis;

import com.xuxe.ignis.commands.FireworkCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Ignis extends JavaPlugin {
    private Logger logger;
    private FileConfiguration config;

    public Ignis() {
        this.logger = getLogger();
        config = getConfig();
    }

    @Override
    public void onEnable() {
        logger.info("<===***----- Ignis has ignited -----***===>");
        config.addDefault("maxRockets", 20);
        config.addDefault("ignisDelay", 200);
        config.options().copyDefaults(true);
        saveConfig();
        getCommand("fw").setExecutor(new FireworkCommand(config, logger, this));
    }

    @Override
    public void onDisable() {
        logger.info("<===----- Ignis has extinguished -----===>");
    }
}
