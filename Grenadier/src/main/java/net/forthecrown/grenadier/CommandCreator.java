package net.forthecrown.grenadier;

import net.forthecrown.grenadier.command.BrigadierCommand;

/**
 * Interface used by {@link CommandBuilder} for comand logic
 * creation.
 * Has one method, {@link CommandCreator#createCommand(BrigadierCommand)} which
 * will create a provided command's logic
 */
@FunctionalInterface
public interface CommandCreator {
    /**
     * A constant which does nothing
     */
    CommandCreator EMPTY = command -> {};

    /**
     * Creates command logic for the given command
     * @param command The command to create logic for
     */
    void createCommand(BrigadierCommand command);
}
