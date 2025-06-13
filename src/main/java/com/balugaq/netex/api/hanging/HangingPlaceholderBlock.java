package com.balugaq.netex.api.hanging;

import com.balugaq.netex.api.interfaces.HangingBlock;
import com.balugaq.netex.utils.Debug;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class HangingPlaceholderBlock extends SpecialSlimefunItem {
    public static final Material PLACEHOLDER_MATERIAL = Material.STRUCTURE_VOID;
    public static final Map<Location, Map<BlockFace, HangingBlock>> hangingBlocks = new HashMap<>();
    public static final String BS_NORTH = "north";
    public static final String BS_SOUTH = "south";
    public static final String BS_EAST = "east";
    public static final String BS_WEST = "west";
    public static final String BS_UP = "up";
    public static final String BS_DOWN = "down";

    public HangingPlaceholderBlock(@NotNull ItemGroup itemGroup, @NotNull SlimefunItemStack item, @NotNull RecipeType recipeType, @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(newBlockTicker());
    }

    public static final Set<Location> firstTick = new HashSet<>();

    @NotNull
    private BlockTicker newBlockTicker() {
        return new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(@NotNull Block block, SlimefunItem slimefunItem, SlimefunBlockData data) {
                Location placeholder = block.getLocation();
                if (!firstTick.contains(placeholder)) {
                    firstTick.add(placeholder);
                    onFirstTick(placeholder, slimefunItem, data);
                } else {
                    Map<BlockFace, HangingBlock> hangingBlocks = getHangingBlocks(placeholder);
                    for (var entry : hangingBlocks.entrySet()) {
                        try {
                            entry.getValue().onTick(placeholder, HangingBlock.getItemFrame(placeholder, entry.getKey()));
                        } catch (Exception e) {
                            Debug.trace(e);
                        }
                    }
                }
            }
        };
    }

    public static void onFirstTick(Location placeholder, SlimefunItem slimefunItem, SlimefunBlockData data) {
        loadHangingBlocks(data);
    }

    public static void loadHangingBlocks(SlimefunBlockData placeholder) {
        var map = getHangingBlocks(placeholder.getLocation(), NetworkDirectional.VALID_FACES);
        for (var entry : map.entrySet()) {
            placeHangingBlock(placeholder, entry.getValue(), entry.getKey(), HangingBlock.getHangingBlock(getHangingBlockId(placeholder, entry.getKey())));
        }
    }

    public static Map<BlockFace, ItemFrame> getHangingBlocks(Location placeholder, Set<BlockFace> attachSides) {
        Map<BlockFace, ItemFrame> hangingBlocks = new HashMap<>();
        for (BlockFace attachSide : attachSides) {
            var v = HangingBlock.getItemFrame(placeholder, attachSide);
            if (v != null) {
                hangingBlocks.put(attachSide, v);
            }
        }

        return hangingBlocks;
    }

    public static String getHangingBlockId(SlimefunBlockData placeholder, BlockFace attachSide) {
        return placeholder.getData(getHangingBlockKey(attachSide));
    }

    public static String getHangingBlockKey(BlockFace attachSide) {
        return switch (attachSide) {
            case NORTH -> BS_NORTH;
            case SOUTH -> BS_SOUTH;
            case EAST -> BS_EAST;
            case WEST -> BS_WEST;
            case UP -> BS_UP;
            case DOWN -> BS_DOWN;
            default -> null;
        };
    }

    public static void placeHangingBlock(SlimefunBlockData placeholder, ItemFrame entityBlock, BlockFace attachSide, HangingBlock hangingBlock) {
        switch (attachSide) {
            case NORTH -> placeholder.setData(BS_NORTH, hangingBlock.getId());
            case SOUTH -> placeholder.setData(BS_SOUTH, hangingBlock.getId());
            case EAST -> placeholder.setData(BS_EAST, hangingBlock.getId());
            case WEST -> placeholder.setData(BS_WEST, hangingBlock.getId());
            case UP -> placeholder.setData(BS_UP, hangingBlock.getId());
            case DOWN -> placeholder.setData(BS_DOWN, hangingBlock.getId());
        }

        var e = hangingBlocks.get(placeholder.getLocation());
        if (e == null) {
            e = new HashMap<>();
        }
        e.put(attachSide, hangingBlock);
        hangingBlocks.put(placeholder.getLocation(), e);
        HangingBlock.tagItemFrame(entityBlock, hangingBlock.getId());
    }

    public static void breakHangingBlock(Location placeholder, BlockFace attachSide) {
        var e = hangingBlocks.get(placeholder);
        e.remove(attachSide);
        if (e.isEmpty()) {
            hangingBlocks.remove(placeholder);
            Slimefun.getDatabaseManager().getBlockDataController().removeBlock(placeholder);
        }
    }

    @NotNull
    public static Map<BlockFace, HangingBlock> getHangingBlocks(Location placeholder) {
        return Optional.ofNullable(hangingBlocks.get(placeholder)).orElse(new HashMap<>());
    }
}
