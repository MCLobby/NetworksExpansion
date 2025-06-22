package io.github.sefiraat.networks.network;

public enum NodeType {
    CONTROLLER,
    BRIDGE,
    STORAGE_MONITOR,
    IMPORT,
    EXPORT,
    GRID,
    CELL,
    GRABBER,
    PUSHER,
    CUTTER,
    PASTER,
    VACUUM,
    PURGER,
    CRAFTER,
    POWER_NODE,
    POWER_OUTLET,
    POWER_DISPLAY,
    ENCODER,
    GREEDY_BLOCK,
    WIRELESS_TRANSMITTER,
    WIRELESS_RECEIVER,

    // from NetworksExpansion
    ADVANCED_GREEDY_BLOCK,
    ADVANCED_IMPORT,
    ADVANCED_EXPORT,
    ADVANCED_PURGER,
    TRANSFER,
    TRANSFER_PUSHER,
    TRANSFER_GRABBER,
    LINE_TRANSFER_VANILLA_PUSHER,
    LINE_TRANSFER_VANILLA_GRABBER,
    INPUT_ONLY_MONITOR,
    OUTPUT_ONLY_MONITOR,
    DECODER,
    LINE_POWER_OUTLET,
    QUANTUM_MANAGER,
    DRAWER_MANAGER,
    CRAFTER_MANAGER,
    FLOW_VIEWER,
    ADVANCED_VACUUM,

    // Hanging blocks
    @Deprecated
    HANGING_PLACEHOLDER,
    // will NOT count as a node
    SWITCHING_MONITOR,
    // will NOT count as a node
    HANGING_GRID,

    // from foreign addons
    // For SlimeAEPlugin
    AE_SWITCHER
}
