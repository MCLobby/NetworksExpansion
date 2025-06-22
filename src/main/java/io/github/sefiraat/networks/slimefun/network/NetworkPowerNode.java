package io.github.sefiraat.networks.slimefun.network;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworkPowerNode extends NetworkObject implements EnergyNetComponent {

    private static final String KEY_UUID = "display-uuid";
    private final int capacity;
    private final Map<Block, Block> placedBlocks = new HashMap<>();

    public NetworkPowerNode(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe,
            int capacity) {
        super(itemGroup, item, recipeType, recipe, NodeType.POWER_NODE);
        this.capacity = capacity;
    }

    @NotNull @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public void preRegister() {

        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(
                    @NotNull BlockBreakEvent e, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                Block brokenBlock = e.getBlock();
                Block pairedBlock = placedBlocks.get(brokenBlock);

                if (pairedBlock != null) {
                    removeDisplay(brokenBlock.getLocation());
                    removeDisplay(pairedBlock.getLocation());

                    placedBlocks.remove(brokenBlock);
                    placedBlocks.remove(pairedBlock);

                    brokenBlock.setType(Material.AIR);
                    pairedBlock.setType(Material.AIR);
                }
            }
        });
    }

    private void removeDisplay(@NotNull Location location) {
        DisplayGroup group = getDisplayGroup(location);
        if (group != null) {
            group.remove();
        }
    }

    @Nullable private UUID getDisplayGroupUUID(@NotNull Location location) {
        String uuid = StorageCacheUtils.getData(location, KEY_UUID);
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }

    @Nullable private DisplayGroup getDisplayGroup(@NotNull Location location) {
        UUID uuid = getDisplayGroupUUID(location);
        if (uuid == null) {
            return null;
        }
        return DisplayGroup.fromUUID(uuid);
    }
}
