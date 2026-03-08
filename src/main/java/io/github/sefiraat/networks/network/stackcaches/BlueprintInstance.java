package io.github.sefiraat.networks.network.stackcaches;

import lombok.Getter;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.balugaq.jeg.core.integrations.networksexpansion.NetworksExpansionIntegrationMain;

public class BlueprintInstance extends ItemStackCache {

    @Getter
    private final ItemStack[] recipeItems;

    @Nullable
    private Recipe recipe = null;

    public BlueprintInstance(@NotNull ItemStack[] recipeItems, @NotNull ItemStack expectedOutput) {
        super(expectedOutput);
        this.recipeItems = recipeItems;
    }

    @Nullable
    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(@Nullable Recipe recipe) {
        this.recipe = recipe;
    }

    public CompletableFuture<Recipe> getRecipeSync(ItemStack[] items, World world) {
        CompletableFuture<Recipe> future = new CompletableFuture<>();
        
        // 调度到主线程执行
        Bukkit.getScheduler().runTask(NetworksExpansionIntegrationMain.getPlugin(), () -> {
            try {
                Recipe recipe = Bukkit.getCraftingRecipe(items, world);
                future.complete(recipe);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    public void generateVanillaRecipe(@NotNull World world) {
        if (this.recipe == null) {
        	getRecipeSync(this.recipeItems, world).thenAccept(recipe -> {
                this.recipe = recipe;
            });
        }
        
        
    }
}
