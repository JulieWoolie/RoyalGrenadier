package net.forthecrown.royalgrenadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.forthecrown.royalgrenadier.types.*;
import net.forthecrown.royalgrenadier.types.item.ItemArgumentImpl;
import net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl;
import net.forthecrown.royalgrenadier.types.scoreboard.ObjectiveArgumentImpl;
import net.forthecrown.royalgrenadier.types.scoreboard.TeamArgumentImpl;
import net.forthecrown.royalgrenadier.types.selector.EntityArgumentImpl;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

//Holy fuck I hate this class
public class RoyalArgumentsImpl {
    private static final Map<Class<? extends ArgumentType<?>>, RegisteredArgument<?, ?>> wrapperAndNms = new HashMap<>();

    //Registers the default arguments into the registry
    public static void init(){
        //Wrappers for vanilla types
        register(PositionArgumentImpl.class,        PositionArgumentImpl::getHandle,            false);
        register(ComponentArgumentImpl.class,       ComponentArgumentImpl::getHandle,           true);
        register(WorldArgumentImpl.class,           g -> DimensionArgument.dimension(),         false);
        register(EnchantArgumentImpl.class,         g -> ItemEnchantmentArgument.enchantment(), false);
        register(ItemArgumentImpl.class,            ItemArgumentImpl::getHandle,                true);
        register(ParticleArgumentImpl.class,        ParticleArgumentImpl::getHandle,            true);
        register(EntityArgumentImpl.class,          EntityArgumentImpl::getHandle,              true);
        register(UUIDArgumentImpl.class,            UUIDArgumentImpl::getHandle,                true);
        register(TeamArgumentImpl.class,            TeamArgumentImpl::getHandle,                true);
        register(ObjectiveArgumentImpl.class,       ObjectiveArgumentImpl::getHandle,           true);
        register(KeyArgumentImpl.class,             KeyArgumentImpl::getHandle,                 false);

        //Custom argument types
        register(LootTableArgumentImpl.class,       LootTableArgumentImpl::getHandle,           false);
        register(TimeArgumentImpl.class,            StringArgumentType::word);
        register(GameModeArgumentImpl.class,        StringArgumentType::word);
        register(ArrayArgumentImpl.class,           StringArgumentType::greedyString);
    }

    //Gets the NMS equivalent to a registered argument type
    public static @NotNull ArgumentType<?> getNMS(ArgumentType<?> type){
        if(isVanillaType(type)) return type;

        if(wrapperAndNms.containsKey(type.getClass())){
            RegisteredArgument pair = wrapperAndNms.get(type.getClass());;

            ArgumentType<?> arg = pair.convert(type);
            Validate.isTrue(isVanillaType(arg), type.getClass().getSimpleName() + " has invalid argument converter");

            return arg;
        }

        //ArgumentScoreholder seems to be a works-for-all kind of thing, except not really
        //Wish there was a string type that allowed symbols and isn't GREEDY_STRING
        return ScoreHolderArgument.scoreHolders();
    }

    //Checks if the given ArgumentType should use vanilla suggestions
    public static boolean shouldUseVanillaSuggestions(ArgumentType<?> wrapped){
        if(!wrapperAndNms.containsKey(wrapped.getClass())) return false;
        return wrapperAndNms.get(wrapped.getClass()).nmsSuggests();
    }

    public static boolean isVanillaType(ArgumentType<?> type){
        return ArgumentTypes.isTypeRegistered(type);
    }

    //Checks if registered lol
    public static <T extends ArgumentType<V>, V> boolean isRegistered(Class<T> type){
        return wrapperAndNms.containsKey(type);
    }

    //Registers the type and allows you to specify if the type should default to NMS for suggestions
    public static <T extends ArgumentType<V>, V> void register(Class<T> type, GrenadierConverter<T, V> nmsSupplier, boolean nmsSuggests){
        wrapperAndNms.put(type, new RegisteredArgument<>(nmsSupplier, nmsSuggests));
    }

    //Same as above, but it's used by RoyalArguments, so it doesn't allow you to decide if suggestions default to NMS
    public static <T extends ArgumentType<V>, V> void  register(Class<T> clazz, GrenadierConverter<T, V> supplier){
        register(clazz, supplier, false);
    }

    public static <T extends ArgumentType<V>, V> void register(Class<T> type, SimpleGrenadierConverter<T, V> converter){
        if(!isVanillaType(converter.get())) throw new IllegalArgumentException("ArgumentType supplier must supply a vanilla ArgumentType");
        register(type, converter, false);
    }
}
