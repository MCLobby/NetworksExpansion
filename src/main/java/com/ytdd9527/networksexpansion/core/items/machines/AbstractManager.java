package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.events.NetworkRootLocateStorageEvent;
import com.balugaq.netex.api.helpers.Icon;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.ParticleUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("deprecation")
public abstract class AbstractManager extends NetworkObject {
    public static final String NO_ITEM = ItemStackHelper.getDisplayName(Icon.QUANTUM_STORAGE_NO_ITEM);
    public static final String MANAGER_TAG = "manager";
    public static final NetworkRootLocateStorageEvent.Strategy MANAGER_STRATEGY = NetworkRootLocateStorageEvent.Strategy.custom(MANAGER_TAG);
    private static final Comparator<? super BarrelIdentity> ALPHABETICAL_SORT = Comparator.comparing(
            barrel -> {
                ItemStack itemStack = barrel.getItemStack();
                if (itemStack == null) {
                    return NO_ITEM;
                }
                SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                if (slimefunItem != null) {
                    return ChatColor.stripColor(slimefunItem.getItemName());
                } else {
                    return ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack));
                }
            },
            Collator.getInstance(Locale.CHINA)::compare
    );
    private static final Comparator<BarrelIdentity> NUMERICAL_SORT = Comparator.comparingLong(BarrelIdentity::getAmount);
    private static final Comparator<BarrelIdentity> NUMERICAL_SORT_REVERSE = (a, b) -> -Long.compare(b.getAmount(), a.getAmount());
    private static final Map<GridCache.SortOrder, Comparator<? super BarrelIdentity>> SORT_MAP = new HashMap<>();
    private static final String BS_TOP = "netex-top";
    private static final String BS_NAME = "netex-name";
    private static final String BS_ICON = "netex-icon";
    private static final String BS_TOP_1B = "1";
    private static final String BS_TOP_0B = "0";
    private static final String NAMESPACE_SF = "sf";
    private static final String NAMESPACE_MC = "mc";

    static {
        SORT_MAP.put(GridCache.SortOrder.ALPHABETICAL, ALPHABETICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.NUMBER, NUMERICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.NUMBER_REVERSE, NUMERICAL_SORT_REVERSE);
    }

    private final IntRangeSetting tickRate;

    protected AbstractManager(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.MANAGER);

        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        addItemSetting(this.tickRate);

        addItemHandler(
                new BlockTicker() {

                    private int tick = 1;

                    @Override
                    public boolean isSynchronized() {
                        return false;
                    }

                    @Override
                    public void tick(Block block, SlimefunItem item, SlimefunBlockData data) {
                        if (tick <= 1) {
                            final BlockMenu blockMenu = data.getBlockMenu();
                            if (blockMenu == null) {
                                return;
                            }
                            addToRegistry(block);
                            updateDisplay(blockMenu);
                        }
                    }

                    @Override
                    public void uniqueTick() {
                        tick = tick <= 1 ? tickRate.getValue() : tick - 1;
                    }
                }
        );
    }

    public static String getStorageName(@Nonnull Location barrelLocation) {
        return StorageCacheUtils.getData(barrelLocation, BS_NAME);
    }

    public static void setStorageIcon(@Nonnull Player player, @Nonnull Location barrelLocation, @Nonnull ItemStack cursor) {
        StorageCacheUtils.setData(barrelLocation, BS_ICON, serializeIcon(cursor));
        player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.manager.set_icon"));
    }

    public static ItemStack getStorageIcon(@Nonnull Location barrelLocation) {
        String icon = StorageCacheUtils.getData(barrelLocation, BS_ICON);
        if (icon == null) {
            return null;
        }
        return deserializeIcon(icon);
    }

    public static String serializeIcon(@Nonnull ItemStack itemStack) {
        var sf = SlimefunItem.getByItem(itemStack);
        if (sf != null) {
            return NAMESPACE_SF + ":" + sf.getId();
        } else {
            return NAMESPACE_MC + ":" + itemStack.getType().name();
        }
    }

    @Nullable
    public static ItemStack deserializeIcon(@Nonnull String icon) {
        if (icon.startsWith(NAMESPACE_SF)) {
            var id = icon.split(":")[1];
            var sf = SlimefunItem.getById(id);
            if (sf != null) {
                return sf.getItem();
            }
        } else if (icon.startsWith(NAMESPACE_MC)) {
            var type = Material.valueOf(icon.split(":")[1]);
            return new ItemStack(type);
        }

        return null;
    }

    public static void topOrUntopStorage(@Nonnull Player player, @Nonnull Location barrelLocation) {
        if (Objects.equals(StorageCacheUtils.getData(barrelLocation, BS_TOP), BS_TOP_1B)) {
            StorageCacheUtils.setData(barrelLocation, BS_TOP, BS_TOP_0B);
            player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.manager.top_storage_off"));
        } else {
            StorageCacheUtils.setData(barrelLocation, BS_TOP, BS_TOP_1B);
            player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.manager.top_storage_on"));
        }
    }

    public static boolean isTopStorage(@Nonnull Location barrelLocation) {
        String top = StorageCacheUtils.getData(barrelLocation, BS_TOP);
        return top != null && top.equals(BS_TOP_1B);
    }

    public static void highlightBlock(@Nonnull Player player, @Nonnull Location barrelLocation) {
        ParticleUtil.drawLineFrom(player.getEyeLocation().clone().add(0D, -0.5D, 0D), barrelLocation);
        ParticleUtil.highlightBlock(barrelLocation);
    }

    public static void setItem(@Nonnull BarrelIdentity barrel, @Nonnull Location barrelLocation, @Nonnull Player player) {
        if (!(barrel instanceof io.github.sefiraat.networks.network.barrel.NetworkStorage)) {
            player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.manager.support_quantum_only"));
        }

        QuantumCache cache = NetworkQuantumStorage.getCaches().get(barrelLocation);
        if (cache == null) {
            player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.manager.support_quantum_only"));
            return;
        }

        ItemStack exist = cache.getItemStack();
        if (exist != null) {
            player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.manager.quantum_storage_not_empty"));
            return;
        }

        BlockMenu menu = StorageCacheUtils.getMenu(barrel.getLocation());
        if (menu == null) {
            return;
        }
        NetworkQuantumStorage.setItem(menu, player);
    }

    public static void openMenu(@Nonnull BarrelIdentity barrel, @Nonnull Player player) {
        BlockMenu menu = StorageCacheUtils.getMenu(barrel.getLocation());
        if (menu == null) {
            return;
        }

        menu.open(player);
    }

    public static List<BarrelIdentity> getBarrels(NetworkRoot root, GridCache cache) {
        return root.getBarrels(barrel -> barrel instanceof io.github.sefiraat.networks.network.barrel.NetworkStorage, MANAGER_STRATEGY, true)
                .stream()
                .filter(entry -> {
                    if (cache.getFilter() == null) {
                        return true;
                    }

                    final ItemStack itemStack = entry.getItemStack();
                    if (itemStack == null) {
                        return true;
                    }

                    String name = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack).toLowerCase(Locale.ROOT));
                    if (cache.getFilter().matches("^[a-zA-Z]+$")) {
                        final String pinyinName = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "");
                        final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                        return name.contains(cache.getFilter()) || pinyinName.contains(cache.getFilter()) || pinyinFirstLetter.contains(cache.getFilter());
                    } else {
                        return name.contains(cache.getFilter());
                    }
                })
                .sorted(SORT_MAP.get(cache.getSortOrder()))
                .toList();
    }

    @Nonnull
    private static String getAmountLore(Long long1) {
        final MessageFormat format = new MessageFormat(Networks.getLocalizationService().getString("messages.normal-operation.grid.item_amount"), Locale.ROOT);
        return format.format(new Object[]{Theme.CLICK_INFO.getColor(), Theme.PASSIVE.getColor(), long1}, new StringBuffer(), null).toString();
    }

    @SuppressWarnings("deprecation")
    public void handleClick(@Nonnull NetworkRoot root, @Nonnull BlockMenu blockMenu, @Nonnull Location barrelLocation, @Nonnull Player player, @Range(from = 0, to = 53) int slot, @Nonnull ItemStack item, @Nonnull ClickAction action) {
        BarrelIdentity barrel = NetworkRoot.getBarrel(barrelLocation, true);
        if (barrel == null) {
            return;
        }

        ItemStack cursor = player.getItemOnCursor();
        if (!action.isRightClicked()) {
            if (action.isShiftClicked()) {
                topOrUntopStorage(player, barrelLocation);
            } else {
                if (cursor.getType() == Material.AIR) {
                    openMenu(barrel, player);
                } else {
                    setItem(barrel, barrelLocation, player);
                }
            }
        } else {
            if (action.isShiftClicked()) {
                if (cursor.getType() == Material.AIR) {
                    setStorageName(blockMenu, player, barrelLocation);
                } else {
                    setStorageIcon(player, barrelLocation, cursor);
                }
            } else {
                highlightBlock(player, barrelLocation);
            }
        }
    }

    public void setStorageName(@Nonnull BlockMenu blockMenu, @Nonnull Player player, @Nonnull Location barrelLocation) {
        player.sendMessage(Networks.getLocalizationService().getString("messages.normal-operation.manager.set_name"));
        player.closeInventory();
        ChatUtils.awaitInput(player, s -> {
            StorageCacheUtils.setData(barrelLocation, BS_NAME, s);
            player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.manager.set_name"));

            SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
            if (data == null) {
                return;
            }

            if (blockMenu.getPreset().getID().equals(data.getSfId())) {
                BlockMenu actualMenu = data.getBlockMenu();
                if (actualMenu != null) {
                    updateDisplay(actualMenu);
                    actualMenu.open(player);
                }
            }
        });
    }

    public void updateDisplay(BlockMenu blockMenu) {
        if (blockMenu == null) {
            return;
        }

        if (!blockMenu.hasViewer()) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.AFK);
            return;
        }

        Location location = blockMenu.getLocation();
        NodeDefinition definition = NetworkStorage.getNode(location);
        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final GridCache gridCache = getCacheMap().get(location);
        var root = definition.getNode().getRoot();
        var barrels = getBarrels(root, gridCache);

        final int pages = (int) Math.ceil(barrels.size() / (double) getDisplaySlots().length) - 1;

        gridCache.setMaxPages(pages);

        // Set everything to blank and return if there are no pages (no items)
        if (pages < 0) {
            clearDisplay(blockMenu);
            return;
        }

        // Reset selected page if it no longer exists due to items being removed
        if (gridCache.getPage() > pages) {
            gridCache.setPage(0);
        }

        int start = gridCache.getPage() * getDisplaySlots().length;
        if (start < 0) {
            start = 0;
        }
        final int end = Math.min(start + getDisplaySlots().length, barrels.size());

        barrels = barrels.stream().sorted((a, b) ->
                isTopStorage(a.getLocation()) ? -1 : isTopStorage(b.getLocation()) ? 1 : 0
        ).toList();

        final List<BarrelIdentity> validBarrels = barrels.subList(start, end);

        getCacheMap().put(blockMenu.getLocation(), gridCache);

        for (int i = 0; i < getDisplaySlots().length; i++) {
            if (validBarrels.size() > i) {
                final BarrelIdentity barrel = validBarrels.get(i);
                final ItemStack barrelItemStack = barrel.getItemStack();
                boolean isEmpty = false;
                ItemStack displayStack = null;
                if (barrelItemStack == null || barrelItemStack.getType() == Material.AIR) {
                    displayStack = new ItemStack(Material.BARRIER);
                    isEmpty = true;
                }

                var barrelLocation = barrel.getLocation();

                final ItemStack custom = getStorageIcon(barrelLocation);
                if (custom != null) {
                    displayStack = custom;
                } else if (displayStack == null) {
                    displayStack = barrelItemStack.clone();
                }

                var name = getStorageName(barrelLocation);
                if (name != null) {
                    displayStack = new CustomItemStack(displayStack, ChatColor.translateAlternateColorCodes('&', name));
                } else if (!isEmpty) {
                    displayStack = new CustomItemStack(displayStack, ChatColor.GRAY + ItemStackHelper.getDisplayName(barrelItemStack));
                } else {
                    displayStack = new CustomItemStack(displayStack, NO_ITEM);
                }

                final ItemMeta itemMeta = displayStack.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }

                List<String> lore = getLoreAddition(barrel);

                itemMeta.setLore(lore);
                displayStack.setItemMeta(itemMeta);
                blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                    handleClick(root, blockMenu, barrelLocation, player, slot, item, action);
                    return false;
                });
            } else {
                blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> false);
            }
        }

        blockMenu.replaceExistingItem(getPagePrevious(), Icon.getPageStack(getPagePreviousStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));
        blockMenu.replaceExistingItem(getPageNext(), Icon.getPageStack(getPageNextStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));

        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    public List<String> getLoreAddition(BarrelIdentity barrel) {
        var loc = barrel.getLocation();
        List<String> list = new ArrayList<>();
        list.add("");
        list.add(String.format(Networks.getLocalizationService().getString("messages.normal-operation.manager.location"), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        list.add(getAmountLore(barrel.getAmount()));
        list.add("");
        list.addAll(Networks.getLocalizationService().getStringList("messages.normal-operation.manager.quantum-manager-click-behavior"));

        return list;
    }

    @Override
    public void postRegister() {
        getPreset();
    }

    @Nonnull
    protected abstract BlockMenuPreset getPreset();

    @Nonnull
    protected abstract Map<Location, GridCache> getCacheMap();

    protected abstract int[] getBackgroundSlots();

    protected abstract int[] getDisplaySlots();

    protected abstract int getChangeSort();

    protected abstract int getPagePrevious();

    protected abstract int getPageNext();

    protected abstract int getFilterSlot();

    protected void setFilter(@Nonnull Player player, @Nonnull BlockMenu blockMenu, @Nonnull GridCache gridCache, @Nonnull ClickAction action) {
        if (action.isRightClicked()) {
            gridCache.setFilter(null);
        } else {
            player.closeInventory();
            player.sendMessage(Networks.getLocalizationService().getString("messages.normal-operation.grid.waiting_for_filter"));
            ChatUtils.awaitInput(player, s -> {
                if (s.isBlank()) {
                    return;
                }
                s = s.toLowerCase(Locale.ROOT);
                gridCache.setFilter(s);
                getCacheMap().put(blockMenu.getLocation(), gridCache);
                player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.grid.filter_set"));

                SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
                if (data == null) {
                    return;
                }

                if (blockMenu.getPreset().getID().equals(data.getSfId())) {
                    BlockMenu actualMenu = data.getBlockMenu();
                    if (actualMenu != null) {
                        updateDisplay(actualMenu);
                        actualMenu.open(player);
                    }
                }
            });
        }
    }

    protected ItemStack getBlankSlotStack() {
        return Icon.BLANK_SLOT_STACK;
    }

    protected ItemStack getPagePreviousStack() {
        return Icon.PAGE_PREVIOUS_STACK;
    }

    protected ItemStack getPageNextStack() {
        return Icon.PAGE_NEXT_STACK;
    }

    protected ItemStack getChangeSortStack() {
        return Icon.CHANGE_SORT_STACK;
    }

    protected ItemStack getFilterStack() {
        return Icon.FILTER_STACK;
    }

    protected void clearDisplay(BlockMenu blockMenu) {
        for (int displaySlot : getDisplaySlots()) {
            blockMenu.replaceExistingItem(displaySlot, getBlankSlotStack());
            blockMenu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
        }
    }
}
