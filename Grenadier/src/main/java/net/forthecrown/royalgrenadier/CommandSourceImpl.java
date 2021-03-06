package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.adventure.PaperAdventure;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.types.pos.Vec2Suggestion;
import net.forthecrown.grenadier.types.pos.Vec3Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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
    private AbstractCommand current;

    CommandSourceImpl(CommandSourceStack source, AbstractCommand currentCommand){
        this.source = source;
        this.current = currentCommand;
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
        return senderOptional(clazz).orElseThrow(() -> INVALID_SENDER_TYPE.create(clazz.getSimpleName()));
    }

    @Override
    @Nullable
    public <T extends CommandSender> T asOrNull(Class<T> clazz){
        return senderOptional(clazz).orElse(null);
    }

    private <T extends CommandSender> Optional<T> senderOptional(Class<T> clazz){
        return Optional.ofNullable(is(clazz) ? (T) asBukkit() : null);
    }

    @Override
    public Location getLocation() {
        World world = getWorld();
        Vec3 pos = source.getPosition();
        Vec2 rot = source.getRotation();

        return new Location(world, pos.x, pos.y, pos.z, rot.y, rot.x);
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
        return source.getServer().server;
    }

    @Override
    public boolean hasPermission(String s) {
        return asBukkit().hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return asBukkit().hasPermission(permission);
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
        if(isSilent()) return;
        source.getBukkitSender().sendMessage(s);
    }

    @Override
    public void sendMessage(Component message, @Nullable UUID id) {
        if(isSilent()) return;
        // Well uhh, idk what to do with the UUID anymore lol
        //UUID formattedId = id == null ? Util.NIL_UUID : id;

        source.source.sendSystemMessage(PaperAdventure.asVanilla(GlobalTranslator.render(message, getLocale())));
    }

    public Locale getLocale() {
        if (is(Player.class)) {
            return asOrNull(Player.class).locale();
        }

        return Locale.ROOT;
    }

    @Override
    public void sendAdmin(Component component, boolean sendToSelf) {
        if(sendToSelf) sendMessage(component);
        if(!shouldInformAdmins()) return;

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

        if(getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
            for (Player p: Bukkit.getOnlinePlayers()){
                if(current != null && !current.testPermissionSilent(p)) continue;
                if(!p.hasPermission("minecraft.admin.command_feedback")) continue;

                //Don't send to self
                if(p.getName().equalsIgnoreCase(name)) continue;

                p.sendMessage(message);
            }
        }

        //Let console know as well
        if(!is(ConsoleCommandSender.class)) Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public AbstractCommand getCurrentCommand() {
        return current;
    }

    @Override
    public void setCurrentCommand(AbstractCommand command) {
        this.current = command;
    }

    @Override
    public boolean shouldInformAdmins() {
        return source.source.shouldInformAdmins();
    }

    @Override
    public Vec3Suggestion getRelevant3DCords() {
        Location loc = getLocation();
        RayTraceResult result = getWorld().rayTraceBlocks(loc, loc.getDirection(), 5);
        if(result == null) return null;

        Vector hitPos = result.getHitPosition();
        return new Vec3Suggestion(hitPos.getX(), hitPos.getY(), hitPos.getZ());
    }

    @Override
    public @Nullable Vec2Suggestion getRelevant2DCords() {
        Vec3Suggestion suggestion = getRelevant3DCords();
        if(suggestion == null) return null;

        return new Vec2Suggestion(suggestion.getX(), suggestion.getZ(), suggestion.tooltip());
    }

    @Override
    public boolean isSilent() {
        return GrenadierUtils.isSilent(source);
    }

    @Override
    public boolean acceptsSuccessMessage() {
        return source.source.acceptsSuccess();
    }

    @Override
    public boolean acceptsFailureMessage() {
        return source.source.acceptsFailure();
    }

    @Override
    public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {
        this.current = null;
    }
}