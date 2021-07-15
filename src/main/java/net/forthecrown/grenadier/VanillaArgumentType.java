package net.forthecrown.grenadier;

import com.mojang.brigadier.arguments.*;
import net.forthecrown.royalgrenadier.arguments.RoyalArgumentsImpl;

import java.util.function.Supplier;

/**
 * As completely custom argument types cannot exist in Minecraft, we must map any
 * custom ones to already existing vanilla ones.
 *
 * <p>Attempting to use custom types in vanilla will either cause all players to
 * be disconnected due to, what I think, is a mismatch between the server's
 * ArgumentRegistry and the client's, or will cause the arguments to show up as
 * invalid or "unkown" in chat</p>
 * <p>
 * The constants are functioning argument types, using the
 * {@link VanillaArgumentType#custom(Supplier)} method can be dangerous if you
 * don't know what you're doing.
 * </p>
 * @see RoyalArguments#register(Class, VanillaArgumentType)
 */
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

    /**
     * Creates a custom VanillaArgumentType
     * @param supplier The Supplier of the argument type, used for converting Grenadier nodes into Vanilla ones
     * @throws IllegalStateException if the given argument type is not registered in the vanilla argument registry
     * @return The created argument type
     */
    public static VanillaArgumentType custom(Supplier<ArgumentType<?>> supplier){
        if(!RoyalArgumentsImpl.isVanillaType(supplier.get())) throw new IllegalStateException("Cannot use non vanilla argument type");
        return new VanillaArgumentType(supplier);
    }

    /**
     * Gets the argument type supplier
     * @return The argument type supplier
     */
    public Supplier<ArgumentType<?>> getSupplier() {
        return supplier;
    }
}
