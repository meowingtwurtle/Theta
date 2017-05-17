package com.srgood.reasons.impl.commands.impl.actual;

import com.srgood.reasons.commands.CommandExecutionData;
import com.srgood.reasons.impl.commands.impl.base.descriptor.BaseCommandDescriptor;
import com.srgood.reasons.impl.commands.impl.base.executor.DMOutputCommandExecutor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRolesDescriptor extends BaseCommandDescriptor {
    public CommandRolesDescriptor() {
        super(Executor::new, "Lists the roles and their IDs in the current Guild", "<>", Collections
                .singletonList("roles"));
    }

        private static class Executor extends DMOutputCommandExecutor {
            public Executor(CommandExecutionData executionData) {
                super(executionData);
            }

            @Override
            public void execute() {
                List<String> roleList = executionData.getGuild()
                                                     .getRoles()
                                                     .stream()
                                                     .sorted(Comparator.reverseOrder())
                                                     .map(role -> String.format("[%s] %s", role.getName(), role.getId()))
                                                     .collect(Collectors.toList());
                sendOutput("**`Roles in %s`**", executionData.getGuild().getName());
                roleList.forEach(this::sendOutput);
            }
        }
}
