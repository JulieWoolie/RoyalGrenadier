package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.*;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.command.BrigadierCommand;
import net.forthecrown.grenadier.types.*;
import net.forthecrown.grenadier.types.block.BlockArgument;
import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.forthecrown.grenadier.types.item.ItemArgument;
import net.forthecrown.grenadier.types.item.ParsedItemStack;
import net.forthecrown.grenadier.types.pos.Position;
import net.forthecrown.grenadier.types.pos.PositionArgument;
import net.forthecrown.grenadier.types.scoreboard.ObjectiveArgument;
import net.forthecrown.grenadier.types.scoreboard.TeamArgument;
import net.forthecrown.grenadier.types.selectors.EntityArgument;
import net.forthecrown.grenadier.types.selectors.EntitySelector;
import net.forthecrown.royalgrenadier.types.selector.EntityArgumentImpl;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Test command because I don't know how to write unit tests
public class TestCommand extends AbstractCommand {
    public TestCommand(Plugin plugin) {
        super("grenadiertest", plugin);

        mapArgTest = new HashMap<>();
        mapArgTest.put("key1", 1);
        mapArgTest.put("key2", 11);
        mapArgTest.put("key3", 111);
        mapArgTest.put("key4", 1111);
        mapArgTest.put("key5", 11111);

        setShowUsageOnFail(true);

        setAliases("grenadier_testalias_1", "grenadier_testalias_2");
        register();
    }

    @Override
    public boolean test(CommandSource source) {
        return source.isOp();
    }

    private final Map<String, Integer> mapArgTest;
    ArrayArgument<ParsedBlock> blocks = ArrayArgument.of(BlockArgument.block());

    @Override
    protected void createCommand(BrigadierCommand command) {
        command
                .then(literal("built_int")
                        .then(literal("str_word")
                                .then(argument("str", StringArgumentType.word())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("str", String.class));
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("str_string")
                                .then(argument("str", StringArgumentType.string())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("str", String.class));
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("str_greedy")
                                .then(argument("str", StringArgumentType.greedyString())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("str", String.class));
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("bool")
                                .then(argument("val", BoolArgumentType.bool())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("val", Boolean.class) + "");
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("int")
                                .then(argument("val", IntegerArgumentType.integer())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("val", Integer.class) + "");
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("long")
                                .then(argument("val", LongArgumentType.longArg())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("val", Long.class) + "");
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("float")
                                .then(argument("val", FloatArgumentType.floatArg())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("val", Float.class) + "");
                                            return 0;
                                        })
                                )
                        )

                        .then(literal("double")
                                .then(argument("val", DoubleArgumentType.doubleArg())
                                        .executes(c -> {
                                            c.getSource().sendMessage(c.getArgument("val", Double.class) + "");
                                            return 0;
                                        })
                                )
                        )
                )

                .then(literal("ref_earlier_arg")
                        .then(argument("pos", PositionArgument.blockPos())
                                .then(argument("str", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> {
                                            Block block = context.getArgument("pos", Position.class).getBlock(context.getSource());

                                            BlockState state = block.getState();
                                            if(!(state instanceof Sign)) return CompletionProvider.suggestMatching(builder, "No Sign");
                                            Sign sign = (Sign) state;

                                            return CompletionProvider.suggestMatching(builder, LegacyComponentSerializer.legacyAmpersand().serialize(sign.line(0)));
                                        })

                                        .executes(c -> {
                                            Block block = c.getArgument("pos", Position.class).getBlock(c.getSource());
                                            String str = c.getArgument("str", String.class);

                                            c.getSource().sendMessage(
                                                    Component.text("Parsed values: ")
                                                            .append(Component.newline())
                                                            .append(Component.text("pos: " + block))
                                                            .append(Component.newline())
                                                            .append(Component.text("str: " + str))
                                            );

                                            return 0;
                                        })
                                )
                        )
                )

                .then(literal("exception_test")
                        .executes(c -> {
                            throw EntityArgumentImpl.TOO_MANY_PLAYERS.create();
                        })
                )

                .then(literal("runtime_exception_test")
                        .executes(c -> {
                            throw new IllegalArgumentException("Test exception");
                        })
                )

                .then(literal("custom_multiArgInOne")
                        .then(argument("gameMode", GameModeArgument.gameMode())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("gameMode", GameMode.class).name());
                                    return 0;
                                })
                        )

                        .then(argument("obj", ObjectiveArgument.objective())
                                .executes(c -> {
                                    c.getSource().sendMessage(ObjectiveArgument.getObjective(c, "obj").getName());
                                    return 0;
                                })
                        )

                        .then(argument("team", TeamArgument.team())
                                .executes(c -> {
                                    c.getSource().sendMessage(TeamArgument.getTeam(c, "team").getName());
                                    return 0;
                                })
                        )
                )

                .then(literal("multiArgInOne")
                        .then(argument("pos", PositionArgument.position())
                                .executes(c -> {
                                    Location location = c.getArgument("pos", Position.class).getLocation(c.getSource());

                                    c.getSource().sendMessage(location.toString());
                                    return 0;
                                })
                        )

                        .then(argument("selector", EntityArgument.entity())
                                .executes(c -> {
                                    Entity entity = c.getArgument("selector", EntitySelector.class).getEntity(c.getSource());

                                    c.getSource().sendMessage(entity.toString());
                                    return 0;
                                })
                        )

                        .then(argument("gameMode", GameModeArgument.gameMode())
                                .executes(c -> {
                                    GameMode gameMode = c.getArgument("gameMode", GameMode.class);

                                    c.getSource().sendMessage(gameMode.name().toLowerCase());
                                    return 0;
                                })
                        )
                )

                .then(literal("key")
                        .then(argument("key_test", KeyArgument.minecraft())
                                .executes(c -> {
                                    Key key = c.getArgument("key_test", Key.class);

                                    c.getSource().sendMessage(key.asString());
                                    return 0;
                                })
                        )
                )

                .then(literal("lootTable")
                        .then(argument("lootTable_test", LootTableArgument.lootTable())
                                .executes(c -> {
                                    LootTable lootTable = c.getArgument("lootTable_test", LootTable.class);

                                    c.getSource().sendMessage(lootTable.getKey().asString());
                                    c.getSource().sendMessage(lootTable.toString());

                                    return 0;
                                })
                        )
                )

                .then(literal("pos_block")
                        .then(argument("loc", PositionArgument.blockPos())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    Location l = c.getArgument("loc", Position.class).getBlockLocation(source);

                                    source.sendMessage(l.toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("pos_vec")
                        .then(argument("loc", PositionArgument.position())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    Location l = c.getArgument("loc", Position.class).getLocation(source);

                                    source.sendMessage(l.toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("pos_2d_vec")
                        .then(argument("loc", PositionArgument.position2D())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    Location l = c.getArgument("loc", Position.class).getLocation(source);

                                    source.sendMessage(l.toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("pos_2d_block")
                        .then(argument("loc", PositionArgument.blockPos2D())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    Location l = c.getArgument("loc", Position.class).getLocation(source);

                                    source.sendMessage(l.toString());
                                    return 0;
                                })
                        )
                )

                .then(literal("world")
                        .then(argument("level", WorldArgument.world())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    World world = c.getArgument("level", World.class);

                                    source.sendMessage(world.getName());
                                    return 0;
                                })
                        )
                )
                .then(literal("team")
                        .then(argument("t", TeamArgument.team())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("t", Team.class).getName());
                                    return 0;
                                })
                        )
                )
                .then(literal("objective")
                        .then(argument("o", ObjectiveArgument.objective())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("o", Objective.class).getName());
                                    return 0;
                                })
                        )
                )
                .then(literal("selector_multi")
                        .then(argument("targets", EntityArgument.multipleEntities())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    EntitySelector selector = c.getArgument("targets", EntitySelector.class);

                                    source.sendMessage(selector.getEntities(source).toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("selector_single")
                        .then(argument("targets", EntityArgument.entity())
                                .executes(c -> {
                                    CommandSource source = c.getSource();
                                    EntitySelector selector = c.getArgument("targets", EntitySelector.class);

                                    source.sendMessage(selector.getEntities(source).toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("enchantment")
                        .then(argument("e", EnchantArgument.enchantment())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("e", Enchantment.class).displayName(0));
                                    return 0;
                                })
                        )
                )
                .then(literal("map")
                        .then(argument("m", MapArgument.of(mapArgTest))
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("m", Integer.class) + "");
                                    return 0;
                                })
                        )
                )

                .then(literal("block_comparator")
                        .then(argument("block", BlockArgument.block())
                                .executes(c -> {
                                    Player player = c.getSource().asPlayer();

                                    ParsedBlock block = c.getArgument("block", ParsedBlock.class);
                                    c.getSource().sendMessage("parsed: " + block.getData().toString());

                                    Block lookingAt = player.getTargetBlock(5);
                                    if(lookingAt == null) {
                                        player.sendMessage("You must be looking at a block");
                                        return 0;
                                    }

                                    BlockData data = lookingAt.getBlockData();
                                    c.getSource().sendMessage("comparison: " + data);

                                    c.getSource().sendMessage("parsed == comparison data: " + block.test(data));
                                    c.getSource().sendMessage("parsed == comparison complete: " + block.test(lookingAt));

                                    return 0;
                                })
                        )
                )

                .then(literal("item")
                        .then(argument("i", ItemArgument.itemStack())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("i", ParsedItemStack.class).singular(true).toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("chatColor")
                        .then(argument("color", EnumArgument.of(Particle.class))
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("color", Particle.class).toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("particle")
                        .then(argument("part", ParticleArgument.particle())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("part", Particle.class).name());
                                    return 0;
                                })
                        )
                )
                .then(literal("block")
                        .then(argument("b", BlockArgument.block())
                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("b", ParsedBlock.class).getData().toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("customSuggest")
                        .then(argument("str", StringArgumentType.string())
                                .suggests(suggestMatching("1", "2", "asd", "DaftFuck", "3"))

                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("str", String.class));
                                    return 0;
                                })
                        )
                )
                .then(literal("array")
                        .then(argument("arr", blocks)
                                .executes(c -> {
                                    Collection<ParsedBlock> parsedBlocks = c.getArgument("arr", Collection.class);
                                    c.getSource().sendAdmin(parsedBlocks.toString());

                                    return 0;
                                })
                        )
                )
                .then(literal("time")
                        .then(argument("time_actual", TimeArgument.time())
                                .executes(c -> {
                                    long millis = TimeArgument.getMillis(c, "time_actual");

                                    c.getSource().sendMessage("Millis: " + millis);
                                    c.getSource().sendMessage("Ticks: " + millis / 50);
                                    c.getSource().sendMessage("Date: " + new Date(millis).toString());
                                    return 0;
                                })
                        )
                )
                .then(literal("component")
                        .then(argument("msg", ComponentArgument.component())

                                .executes(c -> {
                                    c.getSource().sendMessage(c.getArgument("msg", Component.class));
                                    return 0;
                                })
                        )
                );
    }
}