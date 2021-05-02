package net.forthecrown.grenadier;

import com.mojang.brigadier.arguments.*;
import net.minecraft.server.v1_16_R3.ArgumentRegistry;

import java.util.function.Supplier;

public class VanillaArgumentType {
    public static final VanillaArgumentType STRING = new VanillaArgumentType(StringArgumentType::string);
    public static final VanillaArgumentType GREEDY_STRING = new VanillaArgumentType(StringArgumentType::greedyString);
    public static final VanillaArgumentType WORD = new VanillaArgumentType(StringArgumentType::word);
    public static final VanillaArgumentType INTEGER = new VanillaArgumentType(IntegerArgumentType::integer);
    public static final VanillaArgumentType LONG = new VanillaArgumentType(LongArgumentType::longArg);
    public static final VanillaArgumentType DOUBLE = new VanillaArgumentType(DoubleArgumentType::doubleArg);
    public static final VanillaArgumentType BOOLEAN = new VanillaArgumentType(BoolArgumentType::bool);
    public static final VanillaArgumentType FLOAT = new VanillaArgumentType(FloatArgumentType::floatArg);

    private final Supplier<ArgumentType<?>> supplier;
    VanillaArgumentType(Supplier<ArgumentType<?>> supplier){
        this.supplier = supplier;
    }

    public static VanillaArgumentType custom(Supplier<ArgumentType<?>> supplier){
        if(!ArgumentRegistry.a(supplier.get())) throw new IllegalStateException("Cannot use non vanilla argument type :\\");
        return new VanillaArgumentType(supplier);
    }

    public Supplier<ArgumentType<?>> getSupplier() {
        return supplier;
    }
}
