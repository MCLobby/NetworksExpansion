package com.balugaq.netex.api.texture;

import io.github.bakedlibs.dough.collections.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapImageGenerator {
    public static Pair<ItemStack, MapView> getImageItem(@NotNull String imagePath) {
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapView view = apply(map, imagePath);
        return new Pair<>(map, view);
    }

    public static MapView apply(@NotNull ItemStack map, @NotNull String imagePath) {
        if (map.getItemMeta() instanceof MapMeta meta) {
            MapView view;
            if (!meta.hasMapView()) {
                view = Bukkit.createMap(Bukkit.getWorlds().get(0));
            } else {
                view = meta.getMapView();
                if (view == null) {
                    view = Bukkit.createMap(Bukkit.getWorlds().get(0));
                }
            }

            BufferedImage image = resizeImage(ImageUtil.getImage(imagePath), 128, 128);
            BufferedImage finalImage = image;
            view.addRenderer(new MapRenderer() {
                @Override
                public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
                    mapCanvas.setPixel(0, 0, (byte) 14);
                    mapCanvas.drawImage(0, 0, finalImage);
                }
            });
            view.setScale(MapView.Scale.FARTHEST);
            view.setLocked(true);
            meta.setMapView(view);
            meta.setScaling(true);
            meta.setColor(Color.GREEN);
            map.setItemMeta(meta);
            return view;
        }

        return null;
    }

    public static BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resized;
    }
}
