package net.advancius.channel;

import net.advancius.channel.message.ChannelMessage;
import net.md_5.bungee.api.chat.TextComponent;

public interface ChannelFormatter {

    TextComponent format(ChannelMessage message);
}
