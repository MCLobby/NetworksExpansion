package com.ytdd9527.networksexpansion.utils;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

@Deprecated
public class WorldUtils {
    protected static Class<?> craftBlockStateClass;
    protected static Field interfaceBlockDataField;
    protected static Field blockPositionField;
    protected static Field worldField;
    protected static Field weakWorldField;
    protected static boolean success = false;

    static {
        try {
            World sampleWorld = Bukkit.getWorlds().get(0);
            BlockState blockstate = sampleWorld.getBlockAt(0, 0, 0).getState();
            boolean fail = false;
            var result = ReflectionUtil.getDeclaredFieldsRecursively(blockstate.getClass(), "data");
            if (result != null) {
                interfaceBlockDataField = result.getFirstValue();
                if (interfaceBlockDataField != null) {
                    interfaceBlockDataField.setAccessible(true);
                } else {
                    fail = true;
                }
                craftBlockStateClass = result.getSecondValue();
            } else {
                fail = true;
            }
            var r2 = ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "position");
            if (r2 != null) {
                blockPositionField = r2.getFirstValue();
                if (blockPositionField != null) {
                    blockPositionField.setAccessible(true);
                } else {
                    fail = true;
                }
            } else {
                fail = true;
            }
            var r3 = ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "world");
            if (r3 != null) {
                worldField = r3.getFirstValue();
                if (worldField != null) {
                    worldField.setAccessible(true);
                } else {
                    fail = true;
                }
            } else {
                fail = true;
            }
            var r4 = ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "weakWorld");
            if (r4 != null) {
                weakWorldField = r4.getFirstValue();
                if (weakWorldField != null) {
                    weakWorldField.setAccessible(true);
                } else {
                    fail = true;
                }
            } else {
                fail = true;
            }
            success = !fail;
        } catch (Throwable ignored) {

        }
    }

    public static boolean copyBlockState(BlockState fromBlockState, Block toBlock) {
        if (!success) {
            return false;
        }

        BlockState toState = toBlock.getState();
        if (!craftBlockStateClass.isInstance(toState) || !craftBlockStateClass.isInstance(fromBlockState)) {
            return false;
        }

        try {
            blockPositionField.set(fromBlockState, blockPositionField.get(toState));
            worldField.set(fromBlockState, worldField.get(toState));
            weakWorldField.set(fromBlockState, weakWorldField.get(toState));
            fromBlockState.update(true, false);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
