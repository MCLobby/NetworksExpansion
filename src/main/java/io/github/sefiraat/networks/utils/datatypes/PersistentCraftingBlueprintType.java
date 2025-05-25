package io.github.sefiraat.networks.utils.datatypes;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.sefiraat.networks.network.stackcaches.BlueprintInstance;
import io.github.sefiraat.networks.network.stackcaches.CardInstance;
import io.github.sefiraat.networks.utils.Keys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

/**
 * A {@link PersistentDataType} for {@link CardInstance}
 * Creatively thieved from {@see <a href="https://github.com/baked-libs/dough/blob/main/dough-data/src/main/java/io/github/bakedlibs/dough/data/persistent/PersistentUUIDDataType.java">PersistentUUIDDataType}
 *
 * @author Sfiguz7
 * @author Walshy
 */

public class PersistentCraftingBlueprintType implements PersistentDataType<PersistentDataContainer, BlueprintInstance> {

    public static final PersistentDataType<PersistentDataContainer, BlueprintInstance> TYPE = new PersistentCraftingBlueprintType();

    @Override
    @Nonnull
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    @Nonnull
    public Class<BlueprintInstance> getComplexType() {
        return BlueprintInstance.class;
    }

    @Override
    @Nonnull
    public PersistentDataContainer toPrimitive(@Nonnull BlueprintInstance complex, @Nonnull PersistentDataAdapterContext context) {
        final PersistentDataContainer container = context.newPersistentDataContainer();

        container.set(Keys.RECIPE, DataType.ITEM_STACK_ARRAY, complex.getRecipeItems());
        container.set(Keys.OUTPUT, DataType.ITEM_STACK, complex.getItemStack());
        return container;
    }

    @Override
    @Nonnull
    public BlueprintInstance fromPrimitive(@Nonnull PersistentDataContainer primitive, @Nonnull PersistentDataAdapterContext context) {
        ItemStack[] recipe = primitive.get(Keys.RECIPE, DataType.ITEM_STACK_ARRAY);
        if (recipe == null) {
            recipe = primitive.get(Keys.RECIPE2, DataType.ITEM_STACK_ARRAY);
        }
        if (recipe == null) {
            recipe = primitive.get(Keys.RECIPE3, DataType.ITEM_STACK_ARRAY);
        }
        ItemStack output = primitive.get(Keys.OUTPUT, DataType.ITEM_STACK);
        if (output == null) {
            output = primitive.get(Keys.OUTPUT2, DataType.ITEM_STACK);
        }
        if (output == null) {
            output = primitive.get(Keys.OUTPUT3, DataType.ITEM_STACK);
        }

        return new BlueprintInstance(recipe, output);
    }

}
