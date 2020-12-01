package net.advancius.command;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommandFlags {

    private final List<CommandFlag> commandFlagList = new ArrayList<>();

    public CommandFlag getFlag(String name) {
        return commandFlagList.stream().filter(commandFlag -> commandFlag.getKey().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public <T extends Throwable> CommandFlag getFlag(String name, T exception) throws T {
        return commandFlagList.stream().filter(commandFlag -> commandFlag.getKey().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> exception);
    }

    public boolean hasFlag(String name) {
        return getFlag(name) != null;
    }

    @Data
    public static class CommandFlag {

        private final String key;
        private final String value;

        public double asDouble() {
            return Double.valueOf(value);
        }

        public boolean asBoolean() {
            return Boolean.valueOf(value);
        }

        public int asInteger() {
            return Integer.valueOf(value);
        }

        public long asLong() {
            return Long.valueOf(value);
        }

        public float asFloat() {
            return Float.valueOf(value);
        }
    }
}

