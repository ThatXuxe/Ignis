package com.xuxe.ignis.commands;

import com.xuxe.ignis.includes.HelperCommands;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class FireworkCommand implements CommandExecutor {
    private static int delay;
    private static Plugin plugin;
    private FileConfiguration config;
    private Logger logger;

    public FireworkCommand(FileConfiguration config, Logger logger, Plugin plugin) {
        this.config = config;
        this.logger = logger;
        FireworkCommand.plugin = plugin;
        delay = config.getInt("ignisDelay");
    }

    private static void fireInOrder(List<Integer> firingOrder, CommandSender sender) {
        Random rand = new Random(System.currentTimeMillis());
        int count = 0;
        for (Integer i : firingOrder) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> fire(i, rand, sender), delay * count++);
        }
    }

    private static int colorParser(String color) {
        switch (color.toLowerCase()) {
            case "aqua":
            case "blue":
                return 1;
            case "red":
                return 2;
            case "green":
                return 3;
            case "lime":
                return 4;
            case "yellow":
            case "gold":
                return 5;
            case "silver":
            case "gray":
            case "grey":
                return 6;
            case "fuchsia":
            case "pink":
                return 7;
            case "orange":
                return 8;
            case "black":
                return 9;
            case "white":
                return 10;
            default:
                return (int) (Math.random() * 10);
        }
    }

    private static void fire(int i, Random rand, CommandSender sender) {
        Location location = ((Player) sender).getLocation();
        Entity fireworkEntity = location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        Firework firework = (Firework) fireworkEntity;
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        switch (i) {
            case 1:
                builder.withColor(Color.AQUA);
                break;
            case 2:
                builder.withColor(Color.RED);
                break;
            case 3:
                builder.withColor(Color.GREEN);
                break;
            case 4:
                builder.withColor(Color.LIME);
                break;
            case 5:
                builder.withColor(Color.YELLOW);
                break;
            case 6:
                builder.withColor(Color.SILVER);
                break;
            case 7:
                builder.withColor(Color.FUCHSIA);
                break;
            case 8:
                builder.withColor(Color.ORANGE);
                break;
            case 10:
                builder.withColor(Color.WHITE);
                break;
            default:
                builder.withColor(Color.BLACK);
        }
        switch (rand.nextInt(5)) {
            case 0:
                builder.with(FireworkEffect.Type.BALL);
                break;
            case 1:
                builder.with(FireworkEffect.Type.BALL_LARGE);
                break;
            case 2:
                builder.with(FireworkEffect.Type.BURST);
                break;
            case 3:
                builder.with(FireworkEffect.Type.STAR);
                break;
            case 4:
                builder.with(FireworkEffect.Type.CREEPER);
        }
        meta.setPower(3);
        builder.withFlicker();
        meta.addEffect(builder.build());
        firework.setFireworkMeta(meta);
    }

    private static List<Integer> parse(String input, String input2) {
        int amount = Integer.parseInt(input2);
        String[] split = input.split(",");
        String[] result = new String[split.length];
        int percent = 0;
        int unsetCount = 0;
        int x = 0;
        for (String s : split) {
            if (percent >= 100)
                break;
            int i = 0;
            while (Character.isDigit(s.charAt(i)))
                i++;
            int fractionOfColour = 0;
            if (i != 0)
                fractionOfColour = Integer.parseInt(s.substring(0, i));
            else
                unsetCount++;

            if (percent < (100 - fractionOfColour))
                percent += fractionOfColour;
            else {
                fractionOfColour = 100 - percent;
                percent = 100;
            }
            result[x++] = fractionOfColour + "-" + s.substring(i).replace("%", "");
        }
        List<Integer> results = new ArrayList<>();
        if (percent <= 100) {
            try {
                if (percent != 100)
                    percent = (100 - percent) / unsetCount;
            } catch (ArithmeticException arithmetic) {
                percent = 100 - percent;
            }
            for (String s : result) {
                if (s != null) {
                    split = s.split("-");
                    if (split[0].equals("0")) {
                        split[0] = percent + "";
                    }
                    int noOfColor = (int) Math.floor((Double.parseDouble(split[0]) / 100) * amount);
                    int colorID = colorParser(split[1]);
                    if (colorID != 0)
                        for (int j = 0; j < noOfColor; j++)
                            results.add(colorID);

                }
            }
        }
        Collections.shuffle(results);
        return results;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        try {
            //Syntax: /fw amount color(s)
            if (!(sender instanceof Player) || !(sender.hasPermission("ignis.firework")))
                return true;
            ((Player) sender).getWorld().getName();
            Location location = ((Player) sender).getLocation();

            if (args.length == 0) {
                List<Integer> fireOnce = new ArrayList<>();
                fireOnce.add((int) (Math.random() * 10));
                fireInOrder(fireOnce, sender);
                sender.sendMessage("" + ChatColor.GREEN + "Rocket has been fired!");
                logger.info(((Player) sender).getDisplayName() + " has fired a rocket at Location: " + HelperCommands.displayLocation(location));
                return true;
            } else if (args.length == 1) {

                if (Integer.parseInt(args[0]) > config.getInt("maxRockets")) {
                    sender.sendMessage("" + ChatColor.RED + "You cannot exceed " + ChatColor.GOLD + config.getInt("maxRockets") + ChatColor.RED + " rockets!");
                    return false;
                }
                List<Integer> firingOrder = parse("default", args[0]);
                fireInOrder(firingOrder, sender);
                sender.sendMessage("" + ChatColor.GREEN + "Rockets have been fired!");
                logger.info(((Player) sender).getDisplayName() + " has fired " + args[0] + " rocket(s) at Location: " + HelperCommands.displayLocation(location));
                return true;
            } else {
                if (Integer.parseInt(args[0]) > config.getInt("maxRockets")) {
                    sender.sendMessage("" + ChatColor.RED + "You cannot exceed " + ChatColor.GOLD + config.getInt("maxRockets") + ChatColor.RED + " rockets!");
                    return false;
                }
                List<Integer> firingOrder = parse(args[1], args[0]);
                fireInOrder(firingOrder, sender);
                sender.sendMessage("" + ChatColor.GREEN + "Rockets have been fired!");
                logger.info(((Player) sender).getDisplayName() + " has fired " + args[0] + " rocket(s) at Location: " + HelperCommands.displayLocation(location));
                return true;
            }
        } catch (NumberFormatException exception) {
            sender.sendMessage("" + ChatColor.RED + "Correct Usage: " + ChatColor.GREEN + "/fw amount color1,color2...");
        }
        return true;
    }
}
