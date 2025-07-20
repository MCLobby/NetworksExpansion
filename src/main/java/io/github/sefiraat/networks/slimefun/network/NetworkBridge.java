package io.github.sefiraat.networks.slimefun.network;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class NetworkBridge extends NetworkObject {

    private static final String KEY_UUID = "display-uuid";

    @Setter
    private boolean useSpecialModel = false;

    private @Nullable Function<Location, DisplayGroup> displayGroupGenerator;

    public NetworkBridge(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack[] recipe,
        ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput, NodeType.BRIDGE);
    }

    public NetworkBridge(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack[] recipe,
        ItemStack recipeOutput,
        String itemId) {
        super(itemGroup, item, recipeType, recipe, recipeOutput, NodeType.BRIDGE);
    }

    @Override
    public void onPlace(@NotNull BlockPlaceEvent e) {
        super.onPlace(e);
    }

    @Override
    public void postBreak(@NotNull BlockBreakEvent e) {
        super.postBreak(e);
        Location location = e.getBlock().getLocation();
        removeDisplay(location);
        e.getBlock().setType(Material.AIR);
    }

    private void setupDisplay(@NotNull Location location) {
        if (this.displayGroupGenerator != null) {
            DisplayGroup displayGroup =
                this.displayGroupGenerator.apply(location.clone().add(0.5, 0, 0.5));
            StorageCacheUtils.setData(
                location, KEY_UUID, displayGroup.getParentUUID().toString());
        }
    }

    private void removeDisplay(@NotNull Location location) {
        DisplayGroup group = getDisplayGroup(location);
        if (group != null) {
            group.remove();
        }
    }

    @Nullable
    private UUID getDisplayGroupUUID(@NotNull Location location) {
        String uuid = StorageCacheUtils.getData(location, KEY_UUID);
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }

    @Nullable
    private DisplayGroup getDisplayGroup(@NotNull Location location) {
        UUID uuid = getDisplayGroupUUID(location);
        if (uuid == null) {
            return null;
        }
        return DisplayGroup.fromUUID(uuid);
    }
}
