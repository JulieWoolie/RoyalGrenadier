package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.ArgumentType;
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

public class RoyalArgumentRegistry {
    private static final Map<Class<? extends ArgumentType<?>>, Pair<Supplier<ArgumentType<?>>, Boolean>> wrapperAndNms = new HashMap<>();

    public static void init(){
        register(PositionArgumentImpl.class, ArgumentVec3::a, true);
        register(ComponentArgumentImpl.class, ArgumentChatComponent::a, true);
        register(WorldArgumentImpl.class, ArgumentDimension::a, false);
        register(EnchantArgumentImpl.class, ArgumentEnchantment::a, false);
        register(ItemArgumentImpl.class, ArgumentItemStack::a, true);
        register(ParticleArgumentImpl.class, ArgumentParticle::a, true);
        register(EntityArgumentImpl.class, ArgumentEntity::multipleEntities, true);
        register(UUIDArgumentImpl.class, ArgumentUUID::a, true);
    }

    public static @NotNull ArgumentType<?> getNMS(ArgumentType<?> type){
        if(wrapperAndNms.containsKey(type.getClass())) return wrapperAndNms.get(type.getClass()).getFirst().get();

        //ArgumentScoreholder seems to be a works-for-all kind of thing, except not really
        //Wish there was a string type that allowed symbols and isn't GREEDY_STRING
        return ArgumentScoreholder.b();
    }

    public static boolean shouldUseVanillaSuggestions(ArgumentType<?> wrapped){
        if(!wrapperAndNms.containsKey(wrapped.getClass())) return false;
        return wrapperAndNms.get(wrapped.getClass()).getSecond();
    }

    public static <T extends ArgumentType<V>, V> boolean isRegistered(Class<T> type){
        return wrapperAndNms.containsKey(type);
    }

    private static <T extends ArgumentType<V>, V> void register(Class<T> type, Supplier<ArgumentType<?>> nmsSupplier, boolean nmsSuggests){
        if(!ArgumentRegistry.a(nmsSupplier.get())) throw new IllegalArgumentException("ArgumentType supplier must supply a vanilla ArgumentType");
        wrapperAndNms.put(type, new Pair<>(nmsSupplier, nmsSuggests));
    }

    public static <T extends ArgumentType<V>, V> void  register(Class<T> clazz, Supplier<ArgumentType<?>> supplier){
        register(clazz, supplier, false);
    }
}