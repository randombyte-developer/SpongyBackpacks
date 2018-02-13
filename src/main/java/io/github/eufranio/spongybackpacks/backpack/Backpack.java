package io.github.eufranio.spongybackpacks.backpack;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import io.github.eufranio.spongybackpacks.SpongyBackpacks;
import io.github.eufranio.spongybackpacks.data.DataManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Frani on 13/02/2018.
 */
@ConfigSerializable
public class Backpack {

    public static Backpack of(Text name, UUID owner, int size) {
        Backpack backpack = new Backpack();
        backpack.name = TextSerializers.FORMATTING_CODE.serialize(name);
        backpack.owner = owner;
        backpack.size = size;
        backpack.id = UUID.randomUUID();
        return backpack;
    }

    @Setting
    public UUID id;
    public UUID getId() {
        return id;
    }

    @Setting
    public String name;
    public Text getName() {
        return TextSerializers.FORMATTING_CODE.deserialize(name);
    }

    public String getNamePlain() {
        return this.getName().toPlain();
    }

    @Setting
    public UUID owner;
    public UUID getOwner() {
        return owner;
    }

    @Setting
    public int size;
    public int getSize() {
        return size;
    }

    private Inventory inventory;
    public Inventory getInventory() {
        return inventory;
    }

    @Setting
    public List<ItemStack> items = Lists.newArrayList();

    public void open(Player player) {
        if (this.getInventory() == null) {
            this.createInventory(player);
        }
        player.openInventory(this.getInventory());
    }

    private void createInventory(Player player) {
        this.inventory = Inventory.builder().of(InventoryArchetypes.CHEST)
                .withCarrier(player)
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(this.getName()))
                .property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, this.size))
                .listener(InteractInventoryEvent.Close.class, new CloseListener(this).getConsumer())
                .build(SpongyBackpacks.getInstance());
        for (ItemStack i : this.items) {
            this.inventory.offer(i);
        }
        this.items.clear();
    }

    public void delete(CommandSource source, boolean drop) {
        Player player = Sponge.getServer().getPlayer(this.owner).orElse(null);
        if (player != null) {
            if (player.getOpenInventory().isPresent()) player.closeInventory();
        }
        if (drop) {
            Location<World> position = null;
            if (source instanceof Player) {
                position = ((Player) source).getLocation();
            } else if (player != null) {
                position = player.getLocation();
            }

            if (position != null) {
                for (ItemStack i : this.items) {
                    Entity e = position.getExtent().createEntity(EntityTypes.ITEM, position.getPosition());
                    e.offer(Keys.REPRESENTED_ITEM, i.createSnapshot());
                    position.getExtent().spawnEntity(e);
                }
            }
        }
        DataManager.getDataFor(this.owner).removeBackpack(this);
    }

    private static class CloseListener {
        public CloseListener(Backpack backpack) {
            this.backpack = backpack;
        }
        private Backpack backpack;
        public Consumer<InteractInventoryEvent.Close> getConsumer() {
            return event -> {
                for (Inventory slot : this.backpack.inventory.slots()) {
                    Optional<ItemStack> item = slot.poll();
                    item.ifPresent(backpack.items::add);
                }
                backpack.inventory = null;
                DataManager.save(backpack.getOwner());
            };
        }
    }

}
