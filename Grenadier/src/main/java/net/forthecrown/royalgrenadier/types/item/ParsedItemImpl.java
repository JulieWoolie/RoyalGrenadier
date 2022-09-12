package net.forthecrown.royalgrenadier.types.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import net.forthecrown.grenadier.types.item.ParsedItemStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ParsedItemImpl implements ParsedItemStack {

    @Getter
    private final ItemInput nms;

    @Getter
    private final CompoundTag tag;

    public ParsedItemImpl(ItemInput nms, CompoundTag tag) {
        this.nms = nms;
        this.tag = tag;
    }

    @Override
    public ItemStack create(int amount, boolean nbt) throws CommandSyntaxException {
        return CraftItemStack.asBukkitCopy(nms.createItemStack(amount, nbt));
    }

    @Override
    public boolean test(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        return nms.test(i);
    }

    @Override
    public String getTags() {
        return Objects.toString(tag, null);
    }

    @Override
    public Material getType() {
        return CraftMagicNumbers.getMaterial(nms.getItem());
    }
}