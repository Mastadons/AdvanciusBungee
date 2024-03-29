package net.advancius.command;

public class CommandFlagParser {

    public static CommandFlags getCommandFlags(String command) {
        CommandFlags commandFlags = new CommandFlags();
        char[] characters = command.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            if (characters[i] == '=') {
                CommandFlags.CommandFlag commandFlag = new CommandFlags.CommandFlag(getFlagName(command, i), getFlagData(command, i));
                commandFlags.getCommandFlagList().add(commandFlag);
            }
        }
        return commandFlags;
    }

    private static String getFlagName(String command, int equalSign) {
        boolean string = false;
        char[] characters = command.toCharArray();
        int start = 0;
        for (int i = equalSign; i >= 0; i--) {
            if (characters[i] == '"') {
                string = !string;
                continue;
            }
            if (string) continue;

            if (characters[i] == ' ') {
                start = i;
                break;
            }
        }
        String content = command.substring(start, equalSign).trim();
        if (content.startsWith("\"") && content.endsWith("\"")) {
            if (content.length() > 2) content = content.substring(1, content.length()-1);
            else content = "";
        }
        return content;
    }

    private static String getFlagData(String command, int equalSign) {
        boolean string = false;
        char[] characters = command.toCharArray();
        int end = command.length();
        for (int i = equalSign; i < characters.length; i++) {
            if (characters[i] == '"') {
                string = !string;
                continue;
            }
            if (string) continue;

            if (characters[i] == ' ') {
                end = i;
                break;
            }
        }

        String content = command.substring(equalSign + 1, end);
        if (content.startsWith("\"") && content.endsWith("\"")) {
            if (content.length() > 2) content = content.substring(1, content.length()-1);
            else content = "";
        }
        return content;
    }
}

