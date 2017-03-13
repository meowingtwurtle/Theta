package com.srgood.reasons.commands.upcoming.impl.descriptor;

import com.srgood.reasons.commands.upcoming.CommandDescriptor;
import com.srgood.reasons.commands.upcoming.CommandExecutionData;
import com.srgood.reasons.commands.upcoming.CommandExecutor;
import com.srgood.reasons.commands.upcoming.impl.executor.EmptyCommandExecutor;

import java.util.HashSet;
import java.util.Set;

import java.util.*;
import java.util.function.Function;

public abstract class MultiTierCommandDescriptor extends BaseCommandDescriptor {

    private final Set<CommandDescriptor> subCommands;

    public MultiTierCommandDescriptor(Set<CommandDescriptor> subCommands, String help, String... names) {
        this(subCommands, executionData -> EmptyCommandExecutor.INSTANCE, help, names);
    }

    public MultiTierCommandDescriptor(Set<CommandDescriptor> subCommands, Function<CommandExecutionData, CommandExecutor> defaultExecutorFunction, String help, String... names) {
        super(generateDataToExecutorFunction(subCommands, defaultExecutorFunction), help, names);
        this.subCommands = new HashSet<>(subCommands);
    }

    private static Function<CommandExecutionData, CommandExecutor> generateDataToExecutorFunction(Collection<CommandDescriptor> subCommandDescriptors,
                                                                                                  Function<CommandExecutionData, CommandExecutor> defaultExecutorFunction) {
        return executionData -> {
            if (executionData.getParsedArguments().isEmpty()) {
                return defaultExecutorFunction.apply(executionData);
            }

            String targetName = executionData.getParsedArguments().get(0);

            for (CommandDescriptor subDescriptor : subCommandDescriptors) {
                if (Arrays.stream(subDescriptor.getNames()).anyMatch(targetName::equals)) {
                    return subDescriptor.getExecutor(patchExecutionDataForSubCommand(executionData));
                }
            }

            return defaultExecutorFunction.apply(executionData);
        };
    }

    private static CommandExecutionData patchExecutionDataForSubCommand(CommandExecutionData data) {
        List<String> oldParsedArguments = data.getParsedArguments();
        List<String> newParsedArguments = oldParsedArguments.subList(1, oldParsedArguments.size());

        return new CommandExecutionData(data.getRawData(), data.getRawArguments(), newParsedArguments, data.getChannel(),
                data.getGuild(), data.getSender());
    }

    @Override
    public boolean hasSubCommands() {
        return true;
    }

    @Override
    public Set<CommandDescriptor> getSubCommands() {
        return Collections.unmodifiableSet(subCommands);
    }
}
