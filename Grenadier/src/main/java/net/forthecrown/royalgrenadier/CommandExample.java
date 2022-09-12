package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.command.BrigadierCommand;
import net.forthecrown.grenadier.types.selectors.EntityArgument;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * A class to provide a quick guide on how to create commands using Brigadier
 */
public class CommandExample extends AbstractCommand {

    public CommandExample(Plugin plugin) {
        super("example", plugin);

        //These parameters can either be declared here
        setPermission("random.permission.yesnt");
        setPermissionMessage("You don't have permission for this :(");
        setAliases("examplllllle", "goodalia", "cAsE_dOeSnT_mAtTeR");

        //Use this to register the command lol
        register();
    }

    @Override
    protected void createCommand(BrigadierCommand command) {
        command
                .withAliases("aliases", "can", "also", "be", "entered", "here")
                .withPermission("so.can.permissions")
                .withDescription("And descriptions")

                //Entity Selector argument, example: @e[distance=..200,type=minecraft:player]
                .then(argument("entities", EntityArgument.multipleEntities())

                        //Message argument, they can enter string, if they don't, the executes clause below this one
                        //gets called
                        .then(argument("message", StringArgumentType.greedyString())
                                .suggests(suggestMatching("Have a good day!", "Hello", ":D"))

                                .executes(context -> {
                                    //Gets all the entities in the entity Selector
                                    //Check out the EntitySelector class for more info
                                    Collection<Entity> entities = EntityArgument.getEntities(context, "entities");
                                    String message = context.getArgument("message", String.class);

                                    //Send all selected entities a message
                                    for (Entity e: entities) {
                                        e.sendMessage(message);
                                    }

                                    //Sends and admin message telling other people with permission for this command something
                                    context.getSource().sendAdmin("Send entities a message :D");
                                    return 0;
                                })

                        )

                        //If no message is entered, this gets called
                        .executes(context -> {
                            Collection<Entity> entities = EntityArgument.getEntities(context, "entities");

                            for (Entity e: entities) {
                                e.sendMessage("I hope you're having a good day! :D");
                            }

                            context.getSource().sendAdmin("Wished entities a good day!");
                            return 0;
                        })
                );
    }
}