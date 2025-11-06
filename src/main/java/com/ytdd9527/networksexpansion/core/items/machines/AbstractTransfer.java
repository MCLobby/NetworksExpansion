package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.TransferType;
import com.balugaq.netex.api.enums.TransportMode;
import com.balugaq.netex.api.factories.TransferConfigFactory;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.GrabTickOnly;
import com.balugaq.netex.api.interfaces.PushTickOnly;
import com.balugaq.netex.api.interfaces.SoftCellBannable;
import com.balugaq.netex.api.transfer.TransferConfiguration;
import com.balugaq.netex.utils.LineOperationUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("DuplicatedCode")
@EnableAsync
public abstract class AbstractTransfer extends AdvancedDirectional implements RecipeDisplayItem {
    private static final Map<Location, Integer> PUSH_TICKER_MAP = new HashMap<>();
    private static final Map<Location, Integer> GRAB_TICKER_MAP = new HashMap<>();
    private final TransferConfiguration config;

    protected AbstractTransfer(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        NodeType type) {
        this(itemGroup, item, recipeType, recipe, 1, type);
    }

    protected AbstractTransfer(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack[] recipe,
        int outputAmount,
        NodeType type) {
        super(itemGroup, item, recipeType, recipe, type);
        this.config = TransferConfigFactory.getTransferConfiguration(getTransferType(), checkPlus(item.getItemId()));
    }

    @Async
    public String checkPlus(String id) {
        return id.contains("PLUS") ? id : null;
    }

    public abstract TransferType getTransferType();

    @Override
    @Async
    public void postPlace(@NotNull BlockPlaceEvent e) {
        for (int slot : config.templateSlots) {
            this.getSlotsToDrop().add(slot);
        }
    }

    @Override
    @Async
    public boolean isExceedLimit(int quantity) {
        return quantity > config.maxTransportLimit;
    }

    @Override
    @Async
    public int getMaxLimit() {
        return config.maxTransportLimit;
    }

    @Override
    @Async
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);
        final Location location = block.getLocation();
        sendFeedback(location, FeedbackType.TRANSFER_TICKING);

        if (blockMenu == null) {
            sendFeedback(block.getLocation(), FeedbackType.INVALID_BLOCK);
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final BlockFace direction = this.getCurrentDirection(blockMenu);
        if (direction == BlockFace.SELF) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_DIRECTION_SET);
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        if (this instanceof SoftCellBannable scb) {
            if (scb.checkSoftCellBan(blockMenu.getLocation(), root)) {
                return;
            }
        }

        final TransportMode currentTransportMode = getCurrentTransportMode(blockMenu.getLocation());
        final int limitQuantity = getLimitQuantity(blockMenu.getLocation());

        if (!(this instanceof GrabTickOnly)) {
            if (config.defaultPushTick > 1) {
                sendFeedback(location, FeedbackType.TRANSFER_TRY_PUSH_ITEM_WITH_COUNTER);
                int currentPushTick = getPushTickCounter(location);
                if (currentPushTick == 0) {
                    tryPushItem(blockMenu, root, direction, currentTransportMode, limitQuantity);
                }
                currentPushTick = (currentPushTick + 1) % config.defaultPushTick;
                updatePushTickCounter(location, currentPushTick);
            } else {
                sendFeedback(location, FeedbackType.TRANSFER_TRY_PUSH_ITEM);
                tryPushItem(blockMenu, root, direction, currentTransportMode, limitQuantity);
            }
        }

        if (!(this instanceof PushTickOnly)) {
            if (config.defaultGrabTick > 1) {
                sendFeedback(location, FeedbackType.TRANSFER_TRY_GRAB_ITEM_WITH_COUNTER);
                int currentGrabTick = getGrabTickCounter(location);
                if (currentGrabTick == 0) {
                    tryGrabItem(blockMenu, root, direction, currentTransportMode, limitQuantity);
                }
                currentGrabTick = (currentGrabTick + 1) % config.defaultGrabTick;
                updateGrabTickCounter(location, currentGrabTick);
            } else {
                sendFeedback(location, FeedbackType.TRANSFER_TRY_GRAB_ITEM);
                tryGrabItem(blockMenu, root, direction, currentTransportMode, limitQuantity);
            }
        }
    }

    @Async
    private int getPushTickCounter(Location location) {
        final Integer ticker = PUSH_TICKER_MAP.get(location);
        if (ticker != null) {
            return ticker;
        } else {
            PUSH_TICKER_MAP.put(location, 0);
            return 0;
        }
    }

    @Async
    private int getGrabTickCounter(Location location) {
        final Integer ticker = GRAB_TICKER_MAP.get(location);
        if (ticker != null) {
            return ticker;
        } else {
            GRAB_TICKER_MAP.put(location, 0);
            return 0;
        }
    }

    private void updatePushTickCounter(Location location, int pushTick) {
        PUSH_TICKER_MAP.put(location, pushTick);
    }

    private void updateGrabTickCounter(Location location, int grabTick) {
        GRAB_TICKER_MAP.put(location, grabTick);
    }

    @Async
    private void tryPushItem(
        @NotNull BlockMenu blockMenu,
        @NotNull NetworkRoot root,
        @NotNull BlockFace direction,
        @NotNull TransportMode mode,
        int limitQuantity) {
        if (root.getRootPower() < config.defaultRequiredPower) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_POWER);
            return;
        }
        
        //Bukkit.getScheduler().runTaskAsynchronously(Networks.getInstance(), () -> {
        List<ItemStack> templates = new ArrayList<>();
        for (int slot : this.getItemSlots()) {
            final ItemStack template = blockMenu.getItemInSlot(slot);
            if (template != null && template.getType() != Material.AIR) {
                templates.add(StackUtils.getAsQuantity(template, 1));
            }
        }

        LineOperationUtil.doOperation(
            blockMenu.getLocation(),
            direction,
            config.maxDistance,
            false,
            false,
            (targetMenu) -> LineOperationUtil.pushItem(
                targetMenu.getLocation(), root, targetMenu, templates, mode, limitQuantity));

        root.removeRootPower(config.defaultRequiredPower);
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
        //});

        
    }

    @ParametersAreNonnullByDefault
    @Async
    private void tryGrabItem(
        BlockMenu blockMenu, NetworkRoot root, BlockFace direction, TransportMode mode, int limitQuantity) {
        if (root.getRootPower() < config.defaultRequiredPower) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_POWER);
            return;
        }
        
        //Bukkit.getScheduler().runTaskAsynchronously(Networks.getInstance(), () -> {
        LineOperationUtil.doOperation(
                blockMenu.getLocation(),
                direction,
                config.maxDistance,
                false,
                false,
                (targetMenu) ->
                    LineOperationUtil.grabItem(targetMenu.getLocation(), root, targetMenu, mode, limitQuantity));

        root.removeRootPower(config.defaultRequiredPower);
        //});

        
    }

    @Override
    @Async
    protected int @NotNull [] getBackgroundSlots() {
        return config.backgroundSlots;
    }

    @Override
    @Async
    protected int @Nullable [] getOtherBackgroundSlots() {
        return config.templateBackgroundSlots;
    }

    @Nullable
    @Override
    @Async
    protected ItemStack getOtherBackgroundStack() {
        return Icon.PUSHER_TEMPLATE_BACKGROUND_STACK;
    }

    @Override
    @Async
    public int getNorthSlot() {
        return config.northSlot;
    }

    @Override
    @Async
    public int getSouthSlot() {
        return config.southSlot;
    }

    @Override
    @Async
    public int getEastSlot() {
        return config.eastSlot;
    }

    @Override
    @Async
    public int getWestSlot() {
        return config.westSlot;
    }

    @Override
    @Async
    public int getUpSlot() {
        return config.upSlot;
    }

    @Override
    @Async
    public int getDownSlot() {
        return config.downSlot;
    }

    @Override
    @Async
    public int @NotNull [] getItemSlots() {
        return config.templateSlots;
    }

    @Override
    @Async
    public @NotNull List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(6);
        displayRecipes.add(config.getInformationIcon());
        return displayRecipes;
    }

    @Override
    @Async
    protected int getMinusSlot() {
        return config.minusSlot;
    }

    @Override
    @Async
    protected int getCargoNumberSlot() {
        return config.cargoNumberSlot;
    }

    @Override
    @Async
    protected int getAddSlot() {
        return config.addSlot;
    }

    @Override
    @Async
    protected int getTransportModeSlot() {
        return config.transportModeSlot;
    }
}
