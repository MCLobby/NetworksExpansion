package io.github.sefiraat.networks.utils.datatypes;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import io.github.sefiraat.networks.network.stackcaches.BlueprintInstance;
import io.github.sefiraat.networks.network.stackcaches.CardInstance;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * A {@link PersistentDataType} for {@link CardInstance}
 * Creatively thieved from {@see <a href="https://github.com/baked-libs/dough/blob/main/dough-data/src/main/java/io/github/bakedlibs/dough/data/persistent/PersistentUUIDDataType.java">PersistentUUIDDataType}
 *
 * @author Sfiguz7
 * @author Walshy
 */
public class PersistentCraftingBlueprintType implements PersistentDataType<PersistentDataContainer, BlueprintInstance> {

    public static final PersistentDataType<PersistentDataContainer, BlueprintInstance> TYPE =
        new PersistentCraftingBlueprintType();

    @Override
    @NotNull
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    @NotNull
    public Class<BlueprintInstance> getComplexType() {
        return BlueprintInstance.class;
    }

    @Override
    @NotNull
    public PersistentDataContainer toPrimitive(
        @NotNull BlueprintInstance complex, @NotNull PersistentDataAdapterContext context) {
        final PersistentDataContainer container = context.newPersistentDataContainer();

        for (int i = 0; i < complex.getRecipeItems().length; i++) {
            ItemStack itemStack = complex.getRecipeItems()[i];
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            if (itemStack.isSimilar(new ItemStack(itemStack.getType()))) {
                // pure vanilla item
                container.set(Keys.newKey("recipe_" + i), DataType.STRING, "mc;" + itemStack.getType().name() + ";" + itemStack.getAmount());
                continue;
            } else {
                SlimefunItem sf = SlimefunItem.getByItem(itemStack);
                if (sf != null) {
                    if (itemStack.isSimilar(sf.getItem())) {
                        // pure slimefun item
                        container.set(Keys.newKey("recipe_" + i), DataType.STRING, "sf;" + sf.getId() + ";" + itemStack.getAmount());
                        continue;
                    }
                }

                // complex item
                container.set(Keys.newKey("recipe_" + i), DataType.ITEM_STACK, itemStack);
            }
        }

        // container.set(Keys.RECIPE, DataType.ITEM_STACK_ARRAY, complex.getRecipeItems());
        if (complex.getItemStack() != null) {
            container.set(Keys.OUTPUT, DataType.ITEM_STACK, complex.getItemStack());
        }
        return container;
    }

    @Override
    @NotNull
    public BlueprintInstance fromPrimitive(
        @NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        ItemStack[] recipe = primitive.get(Keys.RECIPE, DataType.ITEM_STACK_ARRAY);
        if (recipe == null) {
            recipe = primitive.get(Keys.RECIPE2, DataType.ITEM_STACK_ARRAY);
        }
        if (recipe == null) {
            recipe = primitive.get(Keys.RECIPE3, DataType.ITEM_STACK_ARRAY);
        }
        if (recipe == null) {
            // new format
            List<NamespacedKey> recipeKeys = primitive.getKeys().stream().filter(k -> k.getKey().startsWith("recipe_")).toList();
            recipe = new ItemStack[Math.max(9, recipeKeys.size())];
            for (NamespacedKey key : recipeKeys) {
                int slot = Integer.parseInt(key.getKey().split("_")[1]);
                try {
                    var s = primitive.get(key, DataType.STRING);
                    var s2 = s.split(";");
                    if (s2[0].equals("mc")) {
                        recipe[slot] = new ItemStack(Material.valueOf(s2[1]), Integer.parseInt(s2[2]));
                    } else if (s2[0].equals("sf")) {
                        SlimefunItem sf = SlimefunItem.getById(s2[1]);
                        if (sf == null) sf = ExpansionItems.PLACEHOLDER_ITEM;

                        recipe[slot] = sf.getItem().clone();
                        recipe[slot].setAmount(Integer.parseInt(s2[2]));
                    } else {
                        // impossible
                    }
                } catch (Exception ignored) {
                    var s = primitive.get(key, DataType.ITEM_STACK);
                    recipe[slot] = s;
                }
            }
        }
        ItemStack output = primitive.get(Keys.OUTPUT, DataType.ITEM_STACK);
        if (output == null) {
            output = primitive.get(Keys.OUTPUT2, DataType.ITEM_STACK);
        }
        if (output == null) {
            output = primitive.get(Keys.OUTPUT3, DataType.ITEM_STACK);
        }

        if (recipe == null || output == null) {
            return new BlueprintInstance(new ItemStack[0], new ItemStack(Material.AIR));
        }
        return new BlueprintInstance(recipe, output);
    }
}
