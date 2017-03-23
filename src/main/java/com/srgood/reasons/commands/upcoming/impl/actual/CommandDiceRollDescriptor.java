package com.srgood.reasons.commands.upcoming.impl.actual;

import com.srgood.reasons.commands.upcoming.CommandExecutionData;
import com.srgood.reasons.commands.upcoming.impl.base.descriptor.BaseCommandDescriptor;
import com.srgood.reasons.commands.upcoming.impl.base.executor.ChannelOutputCommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CommandDiceRollDescriptor extends BaseCommandDescriptor {
    private static final int MAX_ROLL = 6;
    private static final int MAX_DICE = 50;

    public CommandDiceRollDescriptor() {
        super(Executor::new, "Rolls some dice", "<>", "diceroll");
    }

    private static class Executor extends ChannelOutputCommandExecutor {
        public Executor(CommandExecutionData executionData) {
            super(executionData);
        }

        @Override
        public void execute() {
            Random r = new Random();

            if (executionData.getParsedArguments().size() > 0) {
                int numRolls;
                if (Integer.parseInt(executionData.getParsedArguments().get(0)) > MAX_DICE) {
                    sendOutput("Whoa there, Im not going to roll %s dice, how about %s instead?", executionData.getParsedArguments()
                                                                                                               .get(0), MAX_DICE);
                    numRolls = MAX_DICE;
                } else {
                    numRolls = Integer.parseInt(executionData.getParsedArguments().get(0));
                }

                List<Integer> results = new ArrayList<>();

                for (int roll = 0; roll < numRolls; roll++) {
                    int randNum = r.nextInt(MAX_ROLL) + 1;
                    results.add(randNum);
                }

                String output = String.join(", ", results.stream()
                                                         .map(x -> Integer.toString(x))
                                                         .collect(Collectors.toList())) + ".";

                sendOutput(output);
            } else {
                sendOutput(r.nextInt(MAX_ROLL) + 1 + ".");
            }
        }
    }
}
