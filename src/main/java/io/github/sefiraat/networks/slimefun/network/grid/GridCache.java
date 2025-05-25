package io.github.sefiraat.networks.slimefun.network.grid;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GridCache {

    @Nonnull
    private final List<ItemStack> pullItemHistory = new ArrayList<>();
    @Setter
    @Getter
    private int page;
    @Setter
    @Getter
    private int maxPages;
    @Nonnull
    private DisplayMode displayMode;
    @Nonnull
    private SortOrder sortOrder;
    @Nullable
    private String filter;

    public GridCache(int page, int maxPages, @Nonnull SortOrder sortOrder) {
        this.page = page;
        this.maxPages = maxPages;
        this.sortOrder = sortOrder;
        this.displayMode = DisplayMode.DISPLAY;
    }

    @Nonnull
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(@Nonnull SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Nullable
    public String getFilter() {
        return this.filter;
    }

    public void setFilter(@Nullable String filter) {
        this.filter = filter;
    }

    @Nonnull
    public List<ItemStack> getPullItemHistory() {
        return this.pullItemHistory;
    }

    public void addPullItemHistory(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            getPullItemHistory().remove(itemStack);

            getPullItemHistory().add(0, itemStack);
        }
    }

    public @NotNull DisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public void toggleDisplayMode() {
        if (this.displayMode == DisplayMode.DISPLAY) {
            this.displayMode = DisplayMode.HISTORY;
        } else {
            this.displayMode = DisplayMode.DISPLAY;
        }
    }

    public enum SortOrder {
        ALPHABETICAL,
        NUMBER,
        ADDON
    }

    public enum DisplayMode {
        DISPLAY,
        HISTORY
    }
}
