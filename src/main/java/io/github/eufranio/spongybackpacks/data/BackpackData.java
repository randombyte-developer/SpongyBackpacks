package io.github.eufranio.spongybackpacks.data;

import com.google.common.collect.Lists;
import io.github.eufranio.spongybackpacks.backpack.Backpack;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 13/02/2018.
 */
@ConfigSerializable
public class BackpackData {

    @Setting
    public List<Backpack> backpacks = Lists.newArrayList();
    public List<Backpack> getBackpacks() {
        return backpacks;
    }

    public void addBackpack(Backpack b) {
        this.backpacks.add(b);
        DataManager.save(b.owner);
    }

    public void removeBackpack(Backpack b) {
        this.backpacks.remove(b);
        DataManager.save(b.owner);
    }

}
