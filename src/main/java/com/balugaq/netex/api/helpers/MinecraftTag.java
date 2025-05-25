package com.balugaq.netex.api.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum MinecraftTag {
    HELMET {
        public boolean isTagged(Material type) {
            return type == Material.LEATHER_HELMET || type == Material.CHAINMAIL_HELMET || type == Material.IRON_HELMET || type == Material.GOLDEN_HELMET || type == Material.DIAMOND_HELMET || type == Material.NETHERITE_HELMET || type == Material.TURTLE_HELMET;
        }
    },
    CHESTPLATE {
        public boolean isTagged(Material type) {
            return type == Material.LEATHER_CHESTPLATE || type == Material.CHAINMAIL_CHESTPLATE || type == Material.IRON_CHESTPLATE || type == Material.GOLDEN_CHESTPLATE || type == Material.DIAMOND_CHESTPLATE || type == Material.NETHERITE_CHESTPLATE || type == Material.ELYTRA;
        }
    },
    LEGGINGS {
        public boolean isTagged(Material type) {
            return type == Material.LEATHER_LEGGINGS || type == Material.CHAINMAIL_LEGGINGS || type == Material.IRON_LEGGINGS || type == Material.GOLDEN_LEGGINGS || type == Material.DIAMOND_LEGGINGS || type == Material.NETHERITE_LEGGINGS;
        }
    },
    BOOTS {
        public boolean isTagged(Material type) {
            return type == Material.LEATHER_BOOTS || type == Material.CHAINMAIL_BOOTS || type == Material.IRON_BOOTS || type == Material.GOLDEN_BOOTS || type == Material.DIAMOND_BOOTS || type == Material.NETHERITE_BOOTS;
        }
    },
    ARMOR {
        public boolean isTagged(Material type) {
            return HELMET.isTagged(type) || CHESTPLATE.isTagged(type) || LEGGINGS.isTagged(type) || BOOTS.isTagged(type);
        }
    },
    SWORD {
        public boolean isTagged(Material type) {
            return type == Material.WOODEN_SWORD || type == Material.STONE_SWORD || type == Material.IRON_SWORD || type == Material.GOLDEN_SWORD || type == Material.DIAMOND_SWORD || type == Material.NETHERITE_SWORD;
        }
    },
    AXE {
        public boolean isTagged(Material type) {
            return type == Material.WOODEN_AXE || type == Material.STONE_AXE || type == Material.IRON_AXE || type == Material.GOLDEN_AXE || type == Material.DIAMOND_AXE || type == Material.NETHERITE_AXE;
        }
    },
    PICKAXE {
        public boolean isTagged(Material type) {
            return type == Material.WOODEN_PICKAXE || type == Material.STONE_PICKAXE || type == Material.IRON_PICKAXE || type == Material.GOLDEN_PICKAXE || type == Material.DIAMOND_PICKAXE || type == Material.NETHERITE_PICKAXE;
        }
    },
    SHOVEL {
        public boolean isTagged(Material type) {
            return type == Material.WOODEN_SHOVEL || type == Material.STONE_SHOVEL || type == Material.IRON_SHOVEL || type == Material.GOLDEN_SHOVEL || type == Material.DIAMOND_SHOVEL || type == Material.NETHERITE_SHOVEL;
        }
    },
    HOE {
        public boolean isTagged(Material type) {
            return type == Material.WOODEN_HOE || type == Material.STONE_HOE || type == Material.IRON_HOE || type == Material.GOLDEN_HOE || type == Material.DIAMOND_HOE || type == Material.NETHERITE_HOE;
        }
    },
    SKULL {
        public boolean isTagged(Material type) {
            return type == Material.SKELETON_SKULL || type == Material.WITHER_SKELETON_SKULL || type == Material.ZOMBIE_HEAD || type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD || type == Material.CREEPER_HEAD || type == Material.DRAGON_HEAD || type == Material.CARVED_PUMPKIN;
        }
    },
    HORSE_ARMOR {
        public boolean isTagged(Material type) {
            return type == Material.LEATHER_HORSE_ARMOR || type == Material.IRON_HORSE_ARMOR || type == Material.GOLDEN_HORSE_ARMOR || type == Material.DIAMOND_HORSE_ARMOR;
        }
    },
    BOW {
        public boolean isTagged(Material type) {
            return type == Material.BOW || type == Material.CROSSBOW;
        }
    },
    FISHING_ROD {
        public boolean isTagged(Material type) {
            return type == Material.FISHING_ROD || type == Material.CARROT_ON_A_STICK || type == Material.WARPED_FUNGUS_ON_A_STICK;
        }
    },
    POTION {
        public boolean isTagged(Material type) {
            return type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION;
        }
    },
    POTION_WITH_TIPPED_ARROW {
        public boolean isTagged(Material type) {
            return POTION.isTagged(type) || type == Material.TIPPED_ARROW;
        }
    };

    MinecraftTag() {
    }

    public abstract boolean isTagged(Material var1);

    public boolean isTagged(@NotNull ItemStack itemStack) {
        return this.isTagged(itemStack.getType());
    }
}
