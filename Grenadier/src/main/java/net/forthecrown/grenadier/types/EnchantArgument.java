package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.EnchantArgumentImpl;
import org.bukkit.enchantments.Enchantment;

/**
 * Represents an argument type which will be parsed into an {@link Enchantment}
 */
public interface EnchantArgument extends ArgumentType<Enchantment> {
    static EnchantArgument enchantment() {
        return EnchantArgumentImpl.INSTANCE;
    }

    static Enchantment getEnchantment(CommandContext<CommandSource> c, String argument) {
        return c.getArgument(argument, Enchantment.class);
    }
}