package com.balugaq.netex.api.events;

import com.balugaq.netex.api.enums.StorageType;
import io.github.sefiraat.networks.network.NetworkRoot;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NetworkRootLocateStorageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final NetworkRoot root;
    private final StorageType storageType;
    private final boolean inputAble;
    private final boolean outputAble;
    private final Strategy strategy;

    public NetworkRootLocateStorageEvent(
            NetworkRoot root, StorageType storageType, boolean inputAble, boolean outputAble, boolean isSync) {
        super(!isSync);
        this.root = root;
        this.storageType = storageType;
        this.inputAble = inputAble;
        this.outputAble = outputAble;
        this.strategy = Strategy.DEFAULT;
    }

    public NetworkRootLocateStorageEvent(NetworkRoot root, StorageType storageType, Strategy strategy, boolean isSync) {
        super(!isSync);
        this.root = root;
        this.storageType = storageType;
        this.inputAble = false;
        this.outputAble = false;
        this.strategy = strategy;
    }

    public static @Nonnull HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public interface Strategy extends Cloneable {
        Strategy DEFAULT = new StrategyImpl();
        String DEFAULT_TAG = "default";

        static Strategy custom(String tag) {
            return StrategyImpl.custom(tag);
        }

        @Nonnull
        String getTag();

        @Nonnull
        Strategy setTag(String tag);

        class StrategyImpl implements Strategy {
            private String tag;

            public StrategyImpl() {
                this.tag = DEFAULT_TAG;
            }

            @SneakyThrows
            @Nonnull
            public static Strategy custom(String tag) {
                return new StrategyImpl().setTag(tag);
            }

            @Nonnull
            @Override
            public String getTag() {
                return tag;
            }

            @Nonnull
            @Override
            public Strategy setTag(String tag) {
                this.tag = tag;
                return this;
            }

            @Override
            public Strategy clone() {
                return new StrategyImpl().setTag(tag);
            }
        }
    }
}
