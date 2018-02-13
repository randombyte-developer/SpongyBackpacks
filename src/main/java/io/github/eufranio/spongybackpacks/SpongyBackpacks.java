package io.github.eufranio.spongybackpacks;

import com.google.inject.Inject;
import io.github.eufranio.spongybackpacks.commands.CommandManager;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;

@Plugin(
        id = "spongybackpacks",
        name = "SpongyBackpacks",
        description = "A simple backpacks plugin with Ender Chests or Shulker Boxes",
        authors = {
                "Eufranio"
        }
)
public class SpongyBackpacks {

    @Inject
    private Logger logger;
    private static SpongyBackpacks instance;
    public static SpongyBackpacks getInstance() {
        return instance;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;
    private File dataDir;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        this.dataDir = new File(this.configDir, "data");
        if (!this.dataDir.exists()) this.dataDir.mkdirs();
        this.logger.info("SpongyBackpacks is loading!");
        CommandManager.registerCommands();
    }

    @Listener
    public void onInteract(InteractItemEvent.Secondary.MainHand e, @Root Player player, @Getter("getItemStack") ItemStackSnapshot stack) {
        if (stack.isEmpty()) return;
        if (!player.get(Keys.IS_SNEAKING).orElse(false)) return;
        if (stack.getType() == ItemTypes.ENDER_CHEST && player.hasPermission("spongybackpacks.enderchest")) {
            player.openInventory(player.getEnderChestInventory());
            return;
        }
        if (stack.getType().getId().contains("shulker_box")) {
            //
        }

    }

    public static File getConfigDir() {
        return instance.configDir;
    }

    public static File getDataDir() {
        return instance.dataDir;
    }

}
