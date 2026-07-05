package com.balugaq.netex.api.interfaces;

import org.jspecify.annotations.NullMarked;

import com.balugaq.netex.api.enums.CraftType;

/**
 * @author balugaq
 */
@Deprecated
@NullMarked
public interface CraftTyped {
    default CraftType craftType() {
        return CraftType.CRAFTING;
    }
}
