# Royal Grenadier
Implementation of Mojang's Brigadier for PaperMC
**Requires Java 11+ and Paper**
All classes in net.forthecrown.grenadier are considered API. While classes in net.forthecrown.royalgrenadier are considered internal and shouldn't be used unless neccessary.

## Using the RoyalGrenadier in a project
Unfortunately there isn't currently a maven repository for this project.
To use the RoyalGrenadier, you'll have to download the jar and add it as a dependency

## Usage
To create a command with the RoyalGrenadier you'll need to create a command class, which extends [AbstractCommand](https://github.com/BotulToxin/RoyalGrenadier/blob/main/src/main/java/net/forthecrown/grenadier/command/AbstractCommand.java). This class' constructor should specify what the name of the command is and what plugin is creating the command.

Once you've specified any extra info like the command's permission or aliases, you'll need to call the register() method to finalize creating the command.

The result should look similar to this:
````
public class ExampleCommand extends AbstractCommand {
	public ExampleCommand(String name, Plugin plugin){
		super(name, plugin);
		
		setPermission("example.permission")
		setAliases("alias1", "alias2");
		
		register();
	}

	@Override
	protected void createCommand(BrigadierCommand command){
		command.executes(context -> {
			context.getSource().sendMessage("Hello!");
			return 0;
		});
	}
}
````
Another example can be seen [here](https://github.com/BotulToxin/RoyalGrenadier/blob/main/src/main/java/net/forthecrown/grenadier/CommandExample.java).
