package io.github.eufranio.spongybackpacks.data;

import com.google.common.reflect.TypeToken;
import io.github.eufranio.spongybackpacks.SpongyBackpacks;
import io.github.eufranio.spongybackpacks.backpack.Backpack;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Stream;

/**
 * Created by Frani on 13/02/2018.
 */
public class DataManager {

    public static Map<UUID, BackpackCache> cache = new WeakHashMap<>();

    @Nonnull
    public static BackpackData getDataFor(UUID player) {
        if (cache.containsKey(player)) {
            return cache.get(player).data;
        } else {
            try {
                HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setFile(getDataFile(player)).build();
                ConfigurationNode node = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
                BackpackData data = node.getValue(TypeToken.of(BackpackData.class), new BackpackData());
                loader.save(node);
                cache.put(player, new BackpackCache(loader, node, data));
                return data;
            } catch (IOException | ObjectMappingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Backpack> getBackpacks(UUID player) {
        return getDataFor(player).getBackpacks();
    }

    public static Backpack getBackpack(String name, UUID player) {
        return getBackpacks(player).stream().filter(b -> b.getNamePlain().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static BackpackData getDataFor(User user) {
        return getDataFor(user.getUniqueId());
    }

    public static File getDataFile(UUID player) {
        File file = new File(SpongyBackpacks.getDataDir(), player.toString() + ".conf");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
        return file;
    }

    public static void save(UUID player) {
        try {
            BackpackCache c = cache.get(player);
            c.node.setValue(TypeToken.of(BackpackData.class), c.data);
            c.loader.save(c.node);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static class BackpackCache {
        public final ConfigurationLoader loader;
        public final ConfigurationNode node;
        public final BackpackData data;
        public BackpackCache(ConfigurationLoader loader, ConfigurationNode node, BackpackData data) {
            this.loader = loader;
            this.node = node;
            this.data = data;
        }
    }

}
