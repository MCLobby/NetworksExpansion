package com.balugaq.netex.api.enums;

import com.balugaq.netex.core.guide.GridNewStyleCustomAmountGuideOption;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;

@NullMarked
public enum AmountHandleStrategy {
    ONE((player, itemStack) -> 1),
    STACK((player, itemStack) -> itemStack.getMaxStackSize()),
    CUSTOM((player, itemStack) -> GridNewStyleCustomAmountGuideOption.get(player));

    private final BiFunction<Player, ItemStack, Integer> amountFunction;

    AmountHandleStrategy(BiFunction<Player, ItemStack, Integer> amountFunction) {
        this.amountFunction = amountFunction;
    }

    public int getAmount(Player player, ItemStack itemStack) {
        return amountFunction.apply(player, itemStack);
    }
}