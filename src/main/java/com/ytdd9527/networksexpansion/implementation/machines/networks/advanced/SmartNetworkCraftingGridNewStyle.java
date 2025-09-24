package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.enums.AmountHandleStrategy;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.helpers.SupportedCraftingTableRecipes;
import com.balugaq.netex.api.interfaces.RecipeCompletableWithGuide;
import com.balugaq.netex.api.keybind.Action;
import com.balugaq.netex.api.keybind.ActionResult;
import com.balugaq.netex.api.keybind.Keybind;
import com.balugaq.netex.api.keybind.Keybinds;
import com.balugaq.netex.api.keybind.MultiActionHandle;
import com.balugaq.netex.utils.BlockMenuUtil;
import com.balugaq.netex.utils.InventoryUtil;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.events.NetworkCraftEvent;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache.DisplayMode;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
@SuppressWarnings("DuplicatedCode")
public class SmartNetworkCraftingGridNewStyle extends AbstractGridNewStyle implements RecipeCompletableWithGuide {
    private static final int[] BACKGROUND_SLOTS = {
        12, 29
    };

    private static final int[] DISPLAY_SLOTS = {
        4, 5, 6, 7,
        13, 14, 15, 16,
        22, 23, 24, 25,
        31, 32, 33, 34
    };

    private static final int RETURN_INGREDIENT_SLOT = 3;
    private static final int RETURN_OUTPUT_SLOT = 21;
    private static final int KEYBIND_BUTTON_SLOT = 28;

    private static final int CHANGE_SORT = 17;
    private static final int FILTER = 8;
    private static final int PAGE_PREVIOUS = 26;
    private static final int PAGE_NEXT = 35;
    private static final int TOGGLE_MODE_SLOT = 30;
    private static final int CRAFT_BUTTON_SLOT = 27;
    private static final int[] OUTPUT_SLOTS = {
        36, 37, 38, 39, 40, 41, 42, 43, 44,
        45, 46, 47, 48, 49, 50, 51, 52, 53
    };
    private static final int[] TEMPLATE_SLOTS = {
        0, 1, 2,
        9, 10, 11,
        18, 19, 20
    };
    private static final Map<Location, GridCache> CACHE_MAP = new HashMap<>();

    private final Keybinds smartOutsideKeybinds = Keybinds.create(Keys.newKey("smart-outside-keybinds"), it -> {
            it.usableKeybinds(
                Keybind.leftClick,
                Keybind.rightClick,
                Keybind.shiftLeftClick,
                Keybind.shiftRightClick,
                Keybind.shiftClick
            );

            Action storeItem = Action.of(Keys.newKey("store-item"), (p, s, i, a, menu) -> {
                NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                if (definition == null || definition.getNode() == null) {
                    return ActionResult.of(MultiActionHandle.CONTINUE, false);
                }

                storeItem(definition.getNode().getRoot(), menu, p, i, false);
                return ActionResult.of(MultiActionHandle.BREAK, false);
            });

            it.usableActions(
                storeItem,
                Keybind.gridActionGenerate(this, AmountHandleStrategy.ONE, true),
                Keybind.gridActionGenerate(this, AmountHandleStrategy.STACK, true),
                Keybind.gridActionGenerate(this, AmountHandleStrategy.CUSTOM, true)
            );
            it.defaultKeybinds(
                Keybind.shiftLeftClick, storeItem
            );
            it.defaultActionResult(ActionResult.of(MultiActionHandle.CONTINUE, true));
        })
        .generate();

    public SmartNetworkCraftingGridNewStyle(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    protected BlockMenuPreset getPreset() {
        return new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(getBackgroundSlots());
                drawBackground(getDisplaySlots());
                setSize(54);
            }

            @Override
            public boolean canOpen(Block block, Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                    || (ExpansionItems.NETWORK_CRAFTING_GRID_NEW_STYLE.canUse(player, false)
                    && Slimefun.getProtectionManager()
                    .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                getCacheMap().put(menu.getLocation(), new GridCache(0, 0, GridCache.SortOrder.ALPHABETICAL));

                menu.replaceExistingItem(getPagePrevious(), getPagePreviousStack());
                menu.addMenuClickHandler(getPagePrevious(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setPage(gridCache.getPage() <= 0 ? 0 : gridCache.getPage() - 1);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getPageNext(), getPageNextStack());
                menu.addMenuClickHandler(getPageNext(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setPage(
                        gridCache.getPage() >= gridCache.getMaxPages()
                            ? gridCache.getMaxPages()
                            : gridCache.getPage() + 1);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getChangeSort(), getChangeSortStack());
                menu.addMenuClickHandler(getChangeSort(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    if (gridCache.getSortOrder() == GridCache.SortOrder.ALPHABETICAL) {
                        gridCache.setSortOrder(GridCache.SortOrder.NUMBER);
                    } else if (gridCache.getSortOrder() == GridCache.SortOrder.NUMBER) {
                        gridCache.setSortOrder(GridCache.SortOrder.NUMBER_REVERSE);
                    } else if (gridCache.getSortOrder() == GridCache.SortOrder.NUMBER_REVERSE) {
                        gridCache.setSortOrder(GridCache.SortOrder.ADDON);
                    } else {
                        gridCache.setSortOrder(GridCache.SortOrder.ALPHABETICAL);
                    }
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getFilterSlot(), getFilterStack());
                menu.addMenuClickHandler(getFilterSlot(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    setFilter(p, menu, gridCache, action);
                    return false;
                });

                menu.replaceExistingItem(getToggleModeSlot(), getModeStack(DisplayMode.DISPLAY));
                menu.addMenuClickHandler(getToggleModeSlot(), (p, slot, item, action) -> {
                    if (!action.isRightClicked()) {
                        GridCache gridCache = getCacheMap().get(menu.getLocation());
                        gridCache.toggleDisplayMode();
                        menu.replaceExistingItem(getToggleModeSlot(), getModeStack(gridCache));
                        updateDisplay(menu);
                    }
                    return false;
                });

                menu.replaceExistingItem(CRAFT_BUTTON_SLOT, Icon.CRAFT_BUTTON_NEW_STYLE);
                menu.addMenuClickHandler(CRAFT_BUTTON_SLOT, (p, slot, item, action) -> {
                    tryCraft(menu, p, action);
                    return false;
                });

                for (int displaySlot : getDisplaySlots()) {
                    menu.replaceExistingItem(displaySlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
                }

                for (int backgroundSlot : getBackgroundSlots()) {
                    menu.replaceExistingItem(backgroundSlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(backgroundSlot, (p, slot, item, action) -> false);
                }

                menu.addItem(RETURN_INGREDIENT_SLOT, Icon.RETURN_INGREDIENT);
                menu.addMenuClickHandler(RETURN_INGREDIENT_SLOT, (p, s, i, a) -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition == null || definition.getNode() == null) return false;
                    NetworkRoot root = definition.getNode().getRoot();
                    for (int slot : getIngredientSlots()) {
                        storeItem(root, menu, p, menu.getItemInSlot(slot));
                    }
                    return false;
                });
                menu.addItem(RETURN_OUTPUT_SLOT, Icon.RETURN_OUTPUT);
                menu.addMenuClickHandler(RETURN_OUTPUT_SLOT, (p, s, i, a) -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition == null || definition.getNode() == null) return false;
                    NetworkRoot root = definition.getNode().getRoot();
                    for (int slot : OUTPUT_SLOTS) {
                        storeItem(root, menu, p, menu.getItemInSlot(slot));
                    }

                    return false;
                });

                menu.addPlayerInventoryClickHandler(smartOutsideKeybinds);
                addKeybindSettingsButton(menu, getKeybindButtonSlot());
            }
        };
    }

    public Map<Location, GridCache> getCacheMap() {
        return CACHE_MAP;
    }

    public int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    public int[] getDisplaySlots() {
        return DISPLAY_SLOTS;
    }

    public int getChangeSort() {
        return CHANGE_SORT;
    }

    public int getPagePrevious() {
        return PAGE_PREVIOUS;
    }

    public int getPageNext() {
        return PAGE_NEXT;
    }

    @Override
    public int getKeybindButtonSlot() {
        return KEYBIND_BUTTON_SLOT;
    }

    public int getToggleModeSlot() {
        return TOGGLE_MODE_SLOT;
    }

    @Override
    protected int getFilterSlot() {
        return FILTER;
    }

    @SuppressWarnings("deprecation")
    private synchronized void tryCraft(BlockMenu menu, Player player, ClickAction action) {
        int times = 1;
        if (action.isRightClicked()) {
            times = 64;
        }

        for (int k = 0; k < times; k++) {
            // Get node and, if it doesn't exist - escape
            final NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
            if (definition == null || definition.getNode() == null) {
                return;
            }
            NetworkRoot root = definition.getNode().getRoot();

            // Get the recipe input
            final ItemStack[] templates = new ItemStack[TEMPLATE_SLOTS.length];
            int i = 0;
            for (int templateSlot : TEMPLATE_SLOTS) {
                templates[i++] = StackUtils.getAsQuantity(menu.getItemInSlot(templateSlot), 1);
            }

            ItemStack crafted = null;

            // Go through each slimefun recipe, trigger and set the ItemStack if found
            for (Map.Entry<ItemStack[], ItemStack> entry :
                SupportedCraftingTableRecipes.getRecipes().entrySet()) {
                if (SupportedCraftingTableRecipes.testRecipe(templates, entry.getKey())) {
                    crafted = entry.getValue().clone();
                    break;
                }
            }

            if (crafted != null) {
                final SlimefunItem sfi2 = SlimefunItem.getByItem(crafted);
                if (sfi2 != null && sfi2.isDisabled()) {
                    player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_output"));
                    return;
                }
            }

            // If no slimefun recipe found, try a vanilla one
            if (crafted == null) {
                crafted = Bukkit.craftItem(templates, player.getWorld(), player);
            }

            // If no item crafted OR result doesn't fit, escape
            if (crafted.getType() == Material.AIR || !BlockMenuUtil.fits(menu, crafted, OUTPUT_SLOTS)) {
                return;
            }

            // check if it has enough input
            for (ItemStack template : templates) {
                if (!root.contains(template)) {
                    return;
                }
            }

            // find enough item, consume
            List<ItemStack> got = new ArrayList<>();
            for (ItemStack template : templates) {
                ItemStack item = root.getItemStack0(menu.getLocation(), new ItemRequest(template, 1));
                if (item == null || item.getType() == Material.AIR) {
                    // return items
                    for (ItemStack i2 : got) {
                        storeItem(root, menu, player, i2);
                    }
                    return;
                } else {
                    got.add(item);
                }
            }

            // fire craft event
            NetworkCraftEvent event = new NetworkCraftEvent(player, this, templates, crafted);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            crafted = event.getOutput();

            // Push item
            if (crafted != null) {
                storeItem(root, menu, player, crafted);
            }

            root.refreshRootItems();
        }
    }

    private void storeItem(NetworkRoot root, BlockMenu menu, Player player, @Nullable ItemStack current) {
        storeItem(root, menu, player, current, true);
    }

    private void storeItem(NetworkRoot root, BlockMenu menu, Player player, @Nullable ItemStack current, boolean drop) {
        if (current == null || current.getType() == Material.AIR || current.getAmount() <= 0) return;

        // 1. try store back into networks
        root.addItemStack(current);
        if (current.getAmount() == 0) return;

        // 2. try store into output slots
        BlockMenuUtil.pushItem(menu, current, OUTPUT_SLOTS);
        if (current.getAmount() == 0) return;

        // 3. try store into player inventory
        InventoryUtil.addItem(player, current);
        if (current.getAmount() == 0) return;

        if (drop) {
            // 4. try drop in the world
            player.getWorld().dropItem(player.getLocation(), current);
        }
    }

    @Override
    public int[] getIngredientSlots() {
        return TEMPLATE_SLOTS;
    }

    @Override
    public List<Keybinds> keybinds() {
        return List.of(displayKeybinds(), smartOutsideKeybinds);
    }
}
