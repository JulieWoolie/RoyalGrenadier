package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.entity.LookAnchor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.types.pos.CoordinateSuggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.phys.Vec2;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link CommandSource}.
 * <p>
 * This implementation uses a vanilla {@link CommandSourceStack} as
 * a backing handle.
 */
@AllArgsConstructor(staticName = "of")
public class CommandSourceImpl implements CommandSource {

    public static final LegacyComponentSerializer lSerializer = LegacyComponentSerializer.builder()
            .extractUrls()
            .hexColors()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexCharacter('#')
            .build();

    public static final DynamicCommandExceptionType INVALID_SENDER_TYPE =
            new DynamicCommandExceptionType(obj -> new LiteralMessage("Only " + obj.toString() + "s may execute this command"));

    private final CommandSourceStack source;

    @Getter @Setter
    private AbstractCommand currentCommand;

    private ResultConsumer<CommandSource> callback;

    public static CommandSourceStack getStack(CommandSender sender) {
        return VanillaCommandWrapper.getListener(sender);
    }

    private CommandSourceImpl cloneWith(CommandSourceStack stack) {
        return new CommandSourceImpl(stack, currentCommand, callback);
    }

    public static CommandSourceStack getStack(CommandSource source) {
        return ((CommandSourceImpl) source).getHandle();
    }

    public CommandSourceStack getHandle() {
        return source;
    }

    @Override
    public <T extends CommandSender> boolean is(Class<T> clazz) {
        return clazz.isAssignableFrom(asBukkit().getClass());
    }

    @Override
    public CommandSender asBukkit() {
        return source.getBukkitSender();
    }

    @Override
    public <T extends CommandSender> T as(Class<T> clazz) throws CommandSyntaxException {
        return senderOptional(clazz).orElseThrow(() -> {
            // Throw vanilla exceptions here because
            // they are translatable
            if (clazz == Player.class) {
                return CommandSourceStack.ERROR_NOT_PLAYER.create();
            }

            if (clazz.isAssignableFrom(Entity.class)) {
                return CommandSourceStack.ERROR_NOT_ENTITY.create();
            }

            return INVALID_SENDER_TYPE.create(clazz.getSimpleName());
        });
    }

    @Override
    @Nullable
    public <T extends CommandSender> T asOrNull(Class<T> clazz) {
        return senderOptional(clazz).orElse(null);
    }

    private <T extends CommandSender> Optional<T> senderOptional(Class<T> clazz) {
        return is(clazz) ? Optional.of((T) asBukkit()) : Optional.empty();
    }

    @Override
    public Location getLocation() {
        return source.getBukkitLocation();
    }

    @Override
    public Location getAnchoredLocation() {
        final var loc = getLocation();

        if (!isEntity() || loc == null) {
            return loc;
        }

        var anchored = source.getAnchor().apply(source);
        loc.set(anchored.x, anchored.y, anchored.z);

        return loc;
    }

    @Override
    public LookAnchor getAnchor() {
        return source.getAnchor() == EntityAnchorArgument.Anchor.EYES ? LookAnchor.EYES : LookAnchor.FEET;
    }

    @Override
    public Component displayName() {
        return PaperAdventure.asAdventure(source.getDisplayName());
    }

    @Override
    public String textName() {
        return source.getTextName();
    }

    @Override
    public World getWorld() {
        return source.getBukkitWorld();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public boolean hasPermission(String s) {
        return asBukkit().hasPermission(s);
    }

    @Override
    public boolean hasPermission(int level) {
        return source.hasPermission(level);
    }

    @Override
    public boolean isOp() {
        return source.getBukkitSender().isOp();
    }

    @Override
    public void setOp(boolean b) {
        source.getBukkitSender().setOp(b);
    }

    @Override
    public void sendMessage(String s) {
        if (isSilent()) {
            return;
        }

        source.getBukkitSender().sendMessage(s);
    }


    @Override
    public void sendAdmin(Component component, boolean sendToSelf) {
        if (sendToSelf) {
            sendMessage(component);
        }

        if (!shouldInformAdmins()) {
            return;
        }

        broadcastAdmin(component);
    }

    @Override
    public void sendAdmin(String s, boolean sendToSelf) {
        sendAdmin(lSerializer.deserialize(s), sendToSelf);
    }

    @Override
    public void broadcastAdmin(Component component) {
        String name = textName();
        Component message = Component.translatable("chat.type.admin",
                Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC),
                displayName(),
                component
        );

        if (getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
            for (Player p: Bukkit.getOnlinePlayers()) {
                if (currentCommand != null && !currentCommand.testPermissionSilent(p)) {
                    continue;
                }

                if (!p.hasPermission("minecraft.admin.command_feedback")) {
                    continue;
                }

                // Don't send to self
                if (p.getName().equalsIgnoreCase(name)) {
                    continue;
                }

                p.sendMessage(message);
            }
        }

        // Let console know as well
        if (!is(ConsoleCommandSender.class)) {
            Bukkit.getConsoleSender().sendMessage(message);
        }
    }

    @Override
    public boolean shouldInformAdmins() {
        return source.source.shouldInformAdmins();
    }

    private Vector getFacingBlock() {
        if (!isEntity()) {
            return null;
        }

        final var loc = getLocation();
        var anchored = source.getAnchor().apply(source);

        loc.set(anchored.x, anchored.y, anchored.z);

        RayTraceResult result = getWorld().rayTraceBlocks(loc, loc.getDirection(), 5);

        if (result == null) {
            return null;
        }

        return result.getHitPosition();
    }

    @Override
    public CoordinateSuggestion getRelevant3DCords() {
        Vector hitPos = getFacingBlock();

        if (hitPos == null) {
            return null;
        }

        return CoordinateSuggestion.of(hitPos.getX(), hitPos.getY(), hitPos.getZ());
    }

    @Override
    public @Nullable CoordinateSuggestion getRelevant2DCords() {
        Vector hitPos = getFacingBlock();

        if (hitPos == null) {
            return null;
        }

        return CoordinateSuggestion.of(hitPos.getX(), hitPos.getZ());
    }

    @Override
    public Collection<String> getEntitySuggestions() {
        return source.getSelectedEntities();
    }

    @Override
    public boolean isSilent() {
        return isSilent(source);
    }

    @Override
    public boolean acceptsSuccessMessage() {
        return source.source.acceptsSuccess();
    }

    @Override
    public boolean acceptsFailureMessage() {
        return source.source.acceptsFailure();
    }

    public static boolean isSilent(CommandSourceStack stack) {
        Field silent = null;

        // the 'silent' field is the only boolean field in CommandSourceStack
        // at least currently, so any boolean fields we find must be the
        // silent field.
        // Why not just put the field name here directly? Obfuscation mappings
        for (Field f: stack.getClass().getDeclaredFields()) {
            if (f.getType().equals(Boolean.TYPE)) {
                silent = f;
                break;
            }
        }

        if (silent == null) {
            // This should not happen
            return false;
        }

        silent.setAccessible(true);

        try {
            return silent.getBoolean(stack);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CommandSource silent() {
        if (isSilent()) {
            return this;
        }

        return cloneWith(source.withSuppressedOutput());
    }

    @Override
    public CommandSource withPosition(Vector vector) {
        var vec = CraftVector.toNMS(vector);
        var pos = source.getPosition();

        if (pos.equals(vec)) {
            return this;
        }

        return cloneWith(source.withPosition(vec));
    }

    @Override
    public CommandSource withWorld(World world) {
        if (Objects.equals(world, getWorld())) {
            return this;
        }

        return cloneWith(source.withLevel(((CraftWorld) world).getHandle()));
    }

    @Override
    public CommandSource facing(Vector vector) {
        return cloneWith(source.facing(CraftVector.toNMS(vector)));
    }

    @Override
    public CommandSource withRotation(float yaw, float pitch) {
        var rot = source.getRotation();

        if (rot.x == yaw && rot.y == pitch) {
            return this;
        }

        return cloneWith(source.withRotation(new Vec2(yaw, pitch)));
    }

    @Override
    public CommandSource withOutput(CommandSender sender) {
        if (asBukkit().equals(sender)) {
            return this;
        }

        var stack = getStack(sender);
        var source = this.source;

        if (sender instanceof CraftEntity entity) {
            source = source.withEntity(entity.getHandle());
        }

        return cloneWith(source.withSource(stack.source));
    }

    @Override
    public CommandSource addCallback(ResultConsumer<CommandSource> consumer) {
        var clone = cloneWith(source);

        if (callback == null) {
            clone.callback = consumer;
        } else {
            clone.callback = (context, success, result) -> {
                callback.onCommandComplete(context, success, result);
                consumer.onCommandComplete(context, success, result);
            };
        }

        return clone;
    }

    @Override
    public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {
        if (callback != null) {
            callback.onCommandComplete(context, success, result);
        }

        setCurrentCommand(null);
    }
}