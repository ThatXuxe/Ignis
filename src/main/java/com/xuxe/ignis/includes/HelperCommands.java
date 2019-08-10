package com.xuxe.ignis.includes;

import org.bukkit.Location;

import java.text.DecimalFormat;

public class HelperCommands {
    public static String displayLocation(Location location) {
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(location.getX()) + " " + df.format(location.getY()) + " " + df.format(location.getZ()) + " in World" + location.getWorld().getName();
    }
}
