package net.minestom.server.command.builder;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;

/**
 * Callback executed when an error is found within the {@link Argument}
 */
public interface ArgumentCallback {

    /**
     * Executed when an error is found
     *
     * @param source the sender which executed the command
     * @param value  the raw string argument which is responsive for the error
     * @param error  the error id (you can check its meaning in the specific argument class or ask the developer about it)
     */
    void apply(CommandSender source, String value, int error);
}
