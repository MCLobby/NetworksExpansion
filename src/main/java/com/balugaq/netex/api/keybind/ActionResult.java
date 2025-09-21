package com.balugaq.netex.api.keybind;

import lombok.Data;

public record ActionResult(MultiActionHandle handle, boolean allowClick) {
    public static ActionResult of(MultiActionHandle handle, boolean allowClick) {
        return new ActionResult(handle, allowClick);
    }
}
