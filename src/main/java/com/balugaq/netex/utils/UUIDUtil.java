package com.balugaq.netex.utils;

import io.github.sefiraat.networks.Networks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author Ddggdd135
 */
public class UUIDUtil {
    public static UUID serverUUID = null;

    public static UUID getServerUUID() {
        if (serverUUID != null) {
            return serverUUID;
        }

        File uuidFile = new File(Networks.getInstance().getDataFolder(), "server-uuid");
        if (uuidFile.exists()) {
            try {
                serverUUID = UUID.nameUUIDFromBytes(Files.readAllBytes(Path.of(uuidFile.getPath())));
                return serverUUID;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            var uuid = UUID.randomUUID();
            serverUUID = uuid;
            try {
                Networks.getInstance().getDataFolder().mkdirs();
                uuidFile.createNewFile();
                Files.write(Path.of(uuidFile.getPath()), toByteArray(uuid));
                return uuid;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] toByteArray(@NotNull UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        byte[] bytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (mostSigBits >>> 8 * (7 - i));
            bytes[8 + i] = (byte) (leastSigBits >>> 8 * (7 - i));
        }
        return bytes;
    }
}
