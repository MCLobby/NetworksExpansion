package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AdvancedWirelessTransmitter extends NetworkObject {
    private static final LocalizationService lang = Lang.get();
    private static final String BS_TARGET_LOCATION = "target";
    protected AdvancedWirelessTransmitter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.ADVANCED_WIRELESS_TRANSMITTER);
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                onTick(b, data);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    public void setTargetLocation(Location machine, Player player, Location target) {
        if (machine.getWorld() != target.getWorld()) {
            player.sendMessage(lang.getString("messages.commands.worldedit.must-select-same-world"));
            return;
        }

        if (!Slimefun.getProtectionManager().hasPermission(player, target, Interaction.INTERACT_BLOCK)) {
            player.sendMessage(lang.getString("messages.feedback.no_permission"));
            return;
        }

        StorageCacheUtils.setData(machine, BS_TARGET_LOCATION, string(target));
    }

    public static String string(Location target) {
        return target.getBlockX() + ";" + target.getBlockY() + ";" + target.getBlockZ();
    }

    @Nullable
    public Location getTargetLocation(Location machine) {
        String s = StorageCacheUtils.getData(machine, BS_TARGET_LOCATION);
        if (s == null) return null;
        String[] split = s.split(";");
        return new Location(machine.getWorld(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    private void onTick(Block b, SlimefunBlockData data) {

    }
}
