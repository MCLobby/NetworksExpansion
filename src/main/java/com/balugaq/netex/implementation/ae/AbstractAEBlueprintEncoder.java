package com.balugaq.netex.implementation.ae;

import com.balugaq.netex.api.enums.AECraftType;
import com.balugaq.netex.api.enums.CraftType;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.utils.BlockMenuUtil;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractEncoder;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Set;

@NullMarked
public abstract class AbstractAEBlueprintEncoder extends AbstractEncoder {
    public static final String BS_CRAFT_TYPE = "ae-craft-type";

    public AbstractAEBlueprintEncoder(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public CraftType craftType() {
        return CraftType.AE;
    }

    @Override
    public boolean tryEncode(Player player, BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            player.sendMessage(Lang.getString("messages.feedback.no_network_found"));
            return false;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        final long networkCharge = root.getRootPower();

        if (networkCharge < getChargeCost()) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.not_enough_power"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_POWER);
            return false;
        }

        ItemStack blueprint = blockMenu.getItemInSlot(getBlankBlueprintSlot());

        SlimefunItem sfi = SlimefunItem.getByItem(blueprint);
        if (sfi != null && sfi.isDisabled()) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_blueprint"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.DISABLED_BLUEPRINT);
            return false;
        }

        if (sfi instanceof AEBlueprint) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.invalid_blueprint"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.INVALID_BLUEPRINT);
            return false;
        }

        // Get the recipe input
        final ItemStack[] inputs = new ItemStack[getRecipeSlots().length];
        int i = 0;
        for (int recipeSlot : getRecipeSlots()) {
            ItemStack stackInSlot = blockMenu.getItemInSlot(recipeSlot);
            if (stackInSlot != null) {
                inputs[i] = ItemStackUtil.getCleanItem(stackInSlot.clone());
            }
            i++;
        }

        ItemStack crafted = null;
        ItemStack[] inp = inputs.clone();
        for (int k = 0; k < inp.length; k++) {
            if (inp[k] != null) {
                inp[k] = ItemStackUtil.getCleanItem(inp[k]);
            }
        }

        for (Map.Entry<ItemStack[], Set<ItemStack>> entry : getEntries()) {
            label:
            {
                var target = entry.getKey();
                for (int k = 0; k < getRecipeSlots().length; k++) {
                    //if ()
                    // todo
                }
            }
        }
        // check vanilla
        //Bukkit.craftItem();
        // todo

        if (crafted != null) {
            final SlimefunItem sfi2 = SlimefunItem.getByItem(crafted);
            if (sfi2 != null && sfi2.isDisabled()) {
                player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_output"));
                sendFeedback(blockMenu.getLocation(), FeedbackType.DISABLED_OUTPUT);
                return false;
            }
        }

        if (crafted == null || crafted.getType() == Material.AIR) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.invalid_recipe"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.INVALID_RECIPE);
            return false;
        }

        final ItemStack blueprintClone = StackUtils.getAsQuantity(blueprint, 1);

        blueprintSetter(blueprintClone, inp, crafted);
        if (BlockMenuUtil.fits(blockMenu, blueprintClone, getOutputSlots())) {
            blueprint.setAmount(blueprint.getAmount() - 1);
            BlockMenuUtil.pushItem(blockMenu, blueprintClone, getOutputSlots());
            sendFeedback(blockMenu.getLocation(), FeedbackType.SUCCESS);
        } else {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.output_full"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.OUTPUT_FULL);
            return false;
        }

        root.removeRootPower(getChargeCost());
        return true;
    }

    public abstract int getBlankBlueprintSlot();
    public abstract int[] getOutputSlots();
    public abstract int[] getRecipeSlots();
    public abstract Set<Map.Entry<ItemStack[], Set<ItemStack>>> getEntries();
    public abstract int getChargeCost();

    public static AECraftType getCraftType(Location location) {
        String str = StorageCacheUtils.getData(location, BS_CRAFT_TYPE);
        if (str == null) return AECraftType.CRAFT;
        if (str.equals("CRAFT")) return AECraftType.CRAFT;
        if (str.equals("CUSTOM")) return AECraftType.CUSTOM;
        return AECraftType.CRAFT;
    }
}
