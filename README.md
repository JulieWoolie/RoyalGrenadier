# RoyalGrenadier
Implementation of Mojang's Brigadier for PaperMC

All classes in net.forthecrown.grenadier are considered API. While classes in net.forthecrown.royalgrenadier are considered internal and shouldn't be used unless neccessary.

To use the RoyalGrenadier you need to create a class for executing the command. The class must extend [AbstractCommand ](https://github.com/BotulToxin/RoyalGrenadier/blob/main/src/main/java/net/forthecrown/grenadier/command/AbstractCommand.java) and implement the createCommand method. To register the command, simply call the register() method in the constructor. An example of creating a command can be seen [here](https://github.com/BotulToxin/RoyalGrenadier/blob/main/src/main/java/net/forthecrown/grenadier/CommandExample.java)
