package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Pair;
import net.forthecrown.royalgrenadier.types.*;
import net.forthecrown.royalgrenadier.types.item.ItemArgumentImpl;
import net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl;
import net.forthecrown.royalgrenadier.types.selector.EntityArgumentImpl;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoyalArgumentsImpl {
    private static final Map<Class<? extends ArgumentType<?>>, Pair<Supplier<ArgumentType<?>>, Boolean>> wrapperAndNms = new HashMap<>();

    //Registers the default arguments into the registry
    public static void init(){
        register(PositionArgumentImpl.class,    Vec3Argument::vec3,                     true);
        register(ComponentArgumentImpl.class,   ComponentArgument::textComponent,       true);
        register(WorldArgumentImpl.class,       DimensionArgument::dimension,           false);
        register(EnchantArgumentImpl.class,     ItemEnchantmentArgument::enchantment,   false);
        register(ItemArgumentImpl.class,        ItemArgument::item,                     true);
        register(ParticleArgumentImpl.class,    ParticleArgument::particle,             true);
        register(EntityArgumentImpl.class,      EntityArgument::entities,               true);
        register(UUIDArgumentImpl.class,        UuidArgument::uuid,                     true);
        register(TimeArgumentImpl.class,        StringArgumentType::word,               false);
        register(GameModeArgumentImpl.class,    StringArgumentType::word,               false);
    }

    //Gets the NMS equivalent to a registered argument type
    public static @NotNull ArgumentType<?> getNMS(ArgumentType<?> type){
        if(wrapperAndNms.containsKey(type.getClass())) return wrapperAndNms.get(type.getClass()).getFirst().get();

        //ArgumentScoreholder seems to be a works-for-all kind of thing, except not really
        //Wish there was a string type that allowed symbols and isn't GREEDY_STRING
        return ScoreHolderArgument.scoreHolders();
    }

    //Checks if the given ArgumentType should use vanilla suggestions
    public static boolean shouldUseVanillaSuggestions(ArgumentType<?> wrapped){
        if(!wrapperAndNms.containsKey(wrapped.getClass())) return false;
        return wrapperAndNms.get(wrapped.getClass()).getSecond();
    }

    public static boolean isVanillaType(ArgumentType<?> type){
        return ArgumentTypes.isTypeRegistered(type);
    }

    //Checks if registered lol
    public static <T extends ArgumentType<V>, V> boolean isRegistered(Class<T> type){
        return wrapperAndNms.containsKey(type);
    }

    //Registers the type and allows you to specify if the type should default to NMS for suggestions
    public static <T extends ArgumentType<V>, V> void register(Class<T> type, Supplier<ArgumentType<?>> nmsSupplier, boolean nmsSuggests){
        if(!isVanillaType(nmsSupplier.get())) throw new IllegalArgumentException("ArgumentType supplier must supply a vanilla ArgumentType");
        wrapperAndNms.put(type, new Pair<>(nmsSupplier, nmsSuggests));
    }

    //Same as above, but it's used by RoyalArguments, so it doesn't allow you to decide if suggestions default to NMS
    public static <T extends ArgumentType<V>, V> void  register(Class<T> clazz, Supplier<ArgumentType<?>> supplier){
        register(clazz, supplier, false);
    }
}
