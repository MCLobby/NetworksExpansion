package com.balugaq.netex.utils;

import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@UtilityClass
public class BlockMenuUtil {
    @Nullable public static ItemStack pushItem(
            @NotNull final BlockMenu blockMenu,
            @Nullable final ItemStack item,
            @Range(from = 0, to = 53) final int @NotNull ... slots) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
            // throw new IllegalArgumentException("Cannot push null or AIR");
        }

        int leftAmount = item.getAmount();

        for (final int slot : slots) {
            if (leftAmount <= 0) {
                break;
            }

            final ItemStack existing = blockMenu.getItemInSlot(slot);

            if (existing == null || existing.getType() == Material.AIR) {
                final int received = Math.min(leftAmount, item.getMaxStackSize());
                blockMenu.replaceExistingItem(slot, StackUtils.getAsQuantity(item, received));
                leftAmount -= received;
                item.setAmount(Math.max(0, leftAmount));
            } else {
                final int existingAmount = existing.getAmount();
                if (existingAmount >= item.getMaxStackSize()) {
                    continue;
                }

                if (!StackUtils.itemsMatch(item, existing)) {
                    continue;
                }

                final int received = Math.max(0, Math.min(item.getMaxStackSize() - existingAmount, leftAmount));
                leftAmount -= received;
                existing.setAmount(existingAmount + received);
                item.setAmount(leftAmount);
            }
        }

        if (leftAmount > 0) {
            return new CustomItemStack(item, leftAmount);
        } else {
            return null;
        }
    }

    @NotNull public static Map<ItemStack, Integer> pushItem(
            @NotNull final BlockMenu blockMenu,
            @Nullable final ItemStack @NotNull [] items,
            @Range(from = 0, to = 53) final int @NotNull ... slots) {
        if (items == null || items.length == 0) {
            return new HashMap<>();
            // throw new IllegalArgumentException("Cannot push null or empty array");
        }

        final List<ItemStack> listItems = new ArrayList<>();
        for (final ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                listItems.add(item);
            }
        }

        return pushItem(blockMenu, listItems, slots);
    }

    @NotNull public static Map<ItemStack, Integer> pushItem(
            @NotNull final BlockMenu blockMenu,
            @Nullable final List<ItemStack> items,
            @Range(from = 0, to = 53) final int @NotNull ... slots) {
        if (items == null || items.isEmpty()) {
            return new HashMap<>();
            // throw new IllegalArgumentException("Cannot push null or empty list");
        }

        final Map<ItemStack, Integer> itemMap = new HashMap<>();
        for (final ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                final ItemStack leftOver = pushItem(blockMenu, item, slots);
                if (leftOver != null) {
                    itemMap.put(leftOver, itemMap.getOrDefault(leftOver, 0) + leftOver.getAmount());
                }
            }
        }

        return itemMap;
    }

    public static boolean fits(
            @NotNull final BlockMenu blockMenu,
            @Nullable final ItemStack item,
            @Range(from = 0, to = 53) final int @NotNull ... slots) {
        if (item == null || item.getType() == Material.AIR) {
            return true;
        }

        int incoming = item.getAmount();
        for (final int slot : slots) {
            final ItemStack stack = blockMenu.getItemInSlot(slot);

            if (stack == null || stack.getType() == Material.AIR) {
                incoming -= item.getMaxStackSize();
            } else if (stack.getMaxStackSize() > stack.getAmount() && StackUtils.itemsMatch(item, stack)) {
                incoming -= stack.getMaxStackSize() - stack.getAmount();
            }

            if (incoming <= 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean fits(
            @NotNull final BlockMenu blockMenu,
            @Nullable final ItemStack @Nullable [] items,
            @Range(from = 0, to = 53) final int @NotNull ... slots) {
        if (items == null || items.length == 0) {
            return false;
        }

        final List<ItemStack> listItems = new ArrayList<>();
        for (final ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                listItems.add(item.clone());
            }
        }

        return fits(blockMenu, listItems, slots);
    }

    public static boolean fits(
            @NotNull final BlockMenu blockMenu,
            @Nullable final List<ItemStack> items,
            @Range(from = 0, to = 53) final int @NotNull ... slots) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        final List<ItemStack> cloneMenu = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            cloneMenu.add(null);
        }

        for (final int slot : slots) {
            final ItemStack stack = blockMenu.getItemInSlot(slot);
            if (stack != null && stack.getType() != Material.AIR) {
                cloneMenu.set(slot, stack.clone());
            } else {
                cloneMenu.set(slot, null);
            }
        }

        for (final ItemStack rawItem : items) {
            final ItemStack item = rawItem.clone();
            int leftAmount = item.getAmount();
            for (int slot : slots) {
                if (leftAmount <= 0) {
                    break;
                }

                final ItemStack existing = cloneMenu.get(slot);

                if (existing == null || existing.getType() == Material.AIR) {
                    final int received = Math.min(leftAmount, item.getMaxStackSize());
                    cloneMenu.set(slot, StackUtils.getAsQuantity(item, leftAmount));
                    leftAmount -= received;
                    item.setAmount(Math.max(0, leftAmount));
                } else {
                    final int existingAmount = existing.getAmount();
                    if (existingAmount >= item.getMaxStackSize()) {
                        continue;
                    }

                    if (!StackUtils.itemsMatch(item, existing)) {
                        continue;
                    }

                    final int received = Math.max(0, Math.min(item.getMaxStackSize() - existingAmount, leftAmount));
                    leftAmount -= received;
                    existing.setAmount(existingAmount + received);
                    item.setAmount(leftAmount);
                }
            }

            if (leftAmount > 0) {
                return false;
            }
        }

        return true;
    }

    public static void consumeItem(@NotNull final BlockMenu blockMenu, @Range(from = 0, to = 64) final int slot) {
        consumeItem(blockMenu, slot, 1);
    }

    public static void consumeItem(
            @NotNull final BlockMenu blockMenu,
            @Range(from = 0, to = 53) final int slot,
            final boolean replaceConsumables) {
        consumeItem(blockMenu, slot, 1, replaceConsumables);
    }

    public static void consumeItem(
            @NotNull final BlockMenu blockMenu,
            @Range(from = 0, to = 53) final int slot,
            @Range(from = 0, to = 64) final int amount) {
        consumeItem(blockMenu, slot, amount, false);
    }

    @SuppressWarnings("deprecation")
    public static void consumeItem(
            @NotNull final BlockMenu blockMenu,
            @Range(from = 0, to = 53) final int slot,
            @Range(from = 0, to = 64) final int amount,
            final boolean replaceConsumables) {
        if (amount == 0) {
            return;
        }

        final ItemStack item = blockMenu.getItemInSlot(slot);
        if (item != null && item.getType() != Material.AIR) {
            if (replaceConsumables
                    && item.getAmount() == 1
                    && StackUtils.itemsMatch(item, new ItemStack(item.getType()))) {
                switch (item.getType()) {
                    case WATER_BUCKET,
                            LAVA_BUCKET,
                            MILK_BUCKET,
                            COD_BUCKET,
                            SALMON_BUCKET,
                            PUFFERFISH_BUCKET,
                            TROPICAL_FISH_BUCKET,
                            AXOLOTL_BUCKET,
                            POWDER_SNOW_BUCKET,
                            TADPOLE_BUCKET -> item.setType(Material.BUCKET);
                    case POTION, SPLASH_POTION, LINGERING_POTION, HONEY_BOTTLE, DRAGON_BREATH -> item.setType(
                            Material.GLASS_BOTTLE);
                    case MUSHROOM_STEW, BEETROOT_SOUP, RABBIT_STEW, SUSPICIOUS_STEW -> item.setType(Material.BOWL);
                }
            } else {
                if (item.getAmount() <= amount) {
                    item.setAmount(0);
                } else {
                    item.setAmount(item.getAmount() - amount);
                }
            }
        }
    }
}
