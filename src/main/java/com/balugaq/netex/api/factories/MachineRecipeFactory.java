package com.balugaq.netex.api.factories;

import com.balugaq.netex.api.data.AdvancedMachineRecipe;
import com.balugaq.netex.api.data.ItemAmountWrapper;
import com.balugaq.netex.api.data.RandomMachineRecipe;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;

/**
 * @author Final_ROOT
 * @since 2.0
 */
public class MachineRecipeFactory {
    private static volatile MachineRecipeFactory instance;
    private final Map<String, List<MachineRecipe>> recipeMap = new HashMap<>();
    private final Map<String, List<AdvancedMachineRecipe>> advancedRecipeMap = new HashMap<>();

    private MachineRecipeFactory() {}

    @Nonnull
    public static MachineRecipeFactory getInstance() {
        if (instance == null) {
            synchronized (MachineRecipeFactory.class) {
                if (instance == null) {
                    instance = new MachineRecipeFactory();
                }
            }
        }
        return instance;
    }

    @Nonnull
    public List<MachineRecipe> getRecipe(@Nonnull String id) {
        if (this.recipeMap.containsKey(id)) {
            return this.recipeMap.get(id);
        }
        List<MachineRecipe> machineRecipeList = new ArrayList<>();
        this.recipeMap.put(id, machineRecipeList);
        return machineRecipeList;
    }

    @Nonnull
    public List<AdvancedMachineRecipe> getAdvancedRecipe(@Nonnull String id) {
        if (this.advancedRecipeMap.containsKey(id)) {
            return this.advancedRecipeMap.get(id);
        } else if (this.recipeMap.containsKey(id)) {
            this.initAdvancedRecipeMap(id);
            return this.advancedRecipeMap.get(id);
        }
        return new ArrayList<>();
    }

    public void initAdvancedRecipeMap(@Nonnull String id) {
        List<MachineRecipe> machineRecipeList = this.recipeMap.get(id);
        if (machineRecipeList == null) {
            return;
        }
        List<AdvancedMachineRecipe> advancedMachineRecipeList = new ArrayList<>(machineRecipeList.size());
        for (MachineRecipe machineRecipe : machineRecipeList) {
            ItemAmountWrapper[] inputItems = ItemStackUtil.calItemArrayWithAmount(machineRecipe.getInput());

            AdvancedMachineRecipe.AdvancedRandomOutput[] advancedRandomOutputs;
            if (machineRecipe instanceof RandomMachineRecipe) {
                advancedRandomOutputs = new AdvancedMachineRecipe.AdvancedRandomOutput
                        [((RandomMachineRecipe) machineRecipe).getRandomOutputs().length];
                RandomMachineRecipe.RandomOutput[] randomOutputs =
                        ((RandomMachineRecipe) machineRecipe).getRandomOutputs();
                for (int i = 0; i < randomOutputs.length; i++) {
                    advancedRandomOutputs[i] = new AdvancedMachineRecipe.AdvancedRandomOutput(
                            ItemStackUtil.calItemArrayWithAmount(randomOutputs[i].getOutputItem()),
                            randomOutputs[i].getWeight());
                }
            } else {
                advancedRandomOutputs = new AdvancedMachineRecipe.AdvancedRandomOutput[1];
                advancedRandomOutputs[0] = new AdvancedMachineRecipe.AdvancedRandomOutput(
                        ItemStackUtil.calItemArrayWithAmount(machineRecipe.getOutput()), 1);
            }

            advancedMachineRecipeList.add(new AdvancedMachineRecipe(inputItems, advancedRandomOutputs));
        }
        this.advancedRecipeMap.put(id, advancedMachineRecipeList);
    }
}
