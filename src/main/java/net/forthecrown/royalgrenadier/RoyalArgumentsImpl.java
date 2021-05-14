package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Pair;
import net.forthecrown.royalgrenadier.types.*;
import net.forthecrown.royalgrenadier.types.item.ItemArgumentImpl;
import net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl;
import net.forthecrown.royalgrenadier.types.selector.EntityArgumentImpl;
import net.minecraft.server.v1_16_R3.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoyalArgumentsImpl {
    private static final Map<Class<? extends ArgumentType<?>>, Pair<Supplier<ArgumentType<?>>, Boolean>> wrapperAndNms = new HashMap<>();

    //Registers the default arguments into the registry
    public static void init(){
        register(PositionArgumentImpl.class, ArgumentVec3::a, true);
        register(ComponentArgumentImpl.class, ArgumentChatComponent::a, true);
        register(WorldArgumentImpl.class, ArgumentDimension::a, false);
        register(EnchantArgumentImpl.class, ArgumentEnchantment::a, false);
        register(ItemArgumentImpl.class, ArgumentItemStack::a, true);
        register(ParticleArgumentImpl.class, ArgumentParticle::a, true);
        register(EntityArgumentImpl.class, ArgumentEntity::multipleEntities, true);
        register(UUIDArgumentImpl.class, ArgumentUUID::a, true);
        register(TimeArgumentImpl.class, StringArgumentType::word, false);
    }

    //Gets the NMS equivalent to a registered argument type
    public static @NotNull ArgumentType<?> getNMS(ArgumentType<?> type){
        if(wrapperAndNms.containsKey(type.getClass())) return wrapperAndNms.get(type.getClass()).getFirst().get();

        //ArgumentScoreholder seems to be a works-for-all kind of thing, except not really
        //Wish there was a string type that allowed symbols and isn't GREEDY_STRING
        return ArgumentScoreholder.b();
    }

    //Checks if the given ArgumentType should use vanilla suggestions
    public static boolean shouldUseVanillaSuggestions(ArgumentType<?> wrapped){
        if(!wrapperAndNms.containsKey(wrapped.getClass())) return false;
        return wrapperAndNms.get(wrapped.getClass()).getSecond();
    }

    public static boolean isVanillaType(ArgumentType<?> type){
        return ArgumentRegistry.a(type);
    }

    //Checks if registered lol
    public static <T extends ArgumentType<V>, V> boolean isRegistered(Class<T> type){
        return wrapperAndNms.containsKey(type);
    }

    //Registers the type and allows you to specify if the type should default to NMS for suggestions
    private static <T extends ArgumentType<V>, V> void register(Class<T> type, Supplier<ArgumentType<?>> nmsSupplier, boolean nmsSuggests){
        if(!isVanillaType(nmsSupplier.get())) throw new IllegalArgumentException("ArgumentType supplier must supply a vanilla ArgumentType");
        wrapperAndNms.put(type, new Pair<>(nmsSupplier, nmsSuggests));
    }

    //Same as above, but it's used by RoyalArguments, so it doesn't allow you to decide if suggestions default to NMS
    public static <T extends ArgumentType<V>, V> void  register(Class<T> clazz, Supplier<ArgumentType<?>> supplier){
        register(clazz, supplier, false);
    }
}
