package com.balugaq.netex.api.data;

import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@Getter
public class ItemContainer {

    private final int id;
    private final @Nonnull ItemStack sample;
    @Getter
    private final @Nonnull ItemStackWrapper wrapper;
    @Setter
    @Getter
    private int amount;

    public ItemContainer(int id, @Nonnull ItemStack item, int amount) {
        this.id = id;
        this.sample = item.clone();
        sample.setAmount(1);
        this.wrapper = ItemStackWrapper.wrap(sample);
        this.amount = amount;
    }

    public @Nonnull ItemStack getSample() {
        return sample.clone();
    }

    public ItemStack getSampleDirectly() {
        return sample;
    }

    public boolean isSimilar(ItemStack other) {
        return StackUtils.itemsMatch(wrapper, other);
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    /**
     * Remove specific amount from container
     *
     * @param amount: amount will be removed
     * @return amount that actual removed
     */
    public int removeAmount(int amount) {
        if (this.amount > amount) {
            this.amount -= amount;
            return amount;
        } else {
            int re = this.amount;
            this.amount = 0;
            return re;
        }
    }

    public @Nonnull String toString() {
        return "ItemContainer{" +
                "id=" + id +
                ", sample=" + sample +
                ", wrapper=" + wrapper +
                ", amount=" + amount +
                '}';
    }
}
