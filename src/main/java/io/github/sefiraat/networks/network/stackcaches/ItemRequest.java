package io.github.sefiraat.networks.network.stackcaches;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@Setter
@Getter
public class ItemRequest extends ItemStackCache {
    @Getter
    private final int originalAmount;
    private int amount;

    public ItemRequest(@Nonnull ItemStack itemStack, int amount) {
        super(itemStack);
        this.originalAmount = amount;
        this.amount = amount;
    }


    public void receiveAmount(int amount) {
        this.amount = this.amount - amount;
    }

    public int getReceivedAmount() {
        return originalAmount - amount;
    }

    public String toString() {
        return "ItemRequest{" +
                "itemStack=" + getItemStack() +
                ", amount=" + amount +
                '}';
    }
}
