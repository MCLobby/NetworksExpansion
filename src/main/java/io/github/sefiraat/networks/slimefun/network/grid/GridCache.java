package io.github.sefiraat.networks.slimefun.network.grid;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GridCache {

    private int page;
    private int maxPages;
    @Nonnull
    private DisplayMode displayMode;
    @Nonnull
    private SortOrder sortOrder;
    @Nullable
    private String filter;
    @Nonnull
    private List<ItemStack> pullItemHistory = new ArrayList<>();

    public GridCache(int page, int maxPages, @Nonnull SortOrder sortOrder) {
        this.page = page;
        this.maxPages = maxPages;
        this.sortOrder = sortOrder;
        this.displayMode = DisplayMode.DISPLAY;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxPages() {
        return this.maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
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
            if (getPullItemHistory().contains(itemStack)) {
                getPullItemHistory().remove(itemStack);
            }

            getPullItemHistory().add(0, itemStack);
        }
    }

    public DisplayMode getDisplayMode() {
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
