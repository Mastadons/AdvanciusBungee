package net.advancius.person.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.channel.ChannelManager;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.utils.Metadata;

import java.io.IOException;

@Data
public class ChannelContext extends PersonContext {

    private Metadata persistentMetadata = new Metadata();
    private Metadata transientMetadata  = new Metadata();

    @Override
    public JsonObject serializeJson() {
        return new JsonObject();
    }

    public ConfiguredChannel getChannel() {
        ChannelManager channelManager = AdvanciusBungee.getInstance().getChannelManager();

        MetadataContext metadata = person.getContextManager().getContext("metadata");
        Metadata persistentMetadata = metadata.getPersistentMetadata();

        if (persistentMetadata.hasMetadata("channel")) {
            ConfiguredChannel channel = channelManager.getChannel(persistentMetadata.getMetadata("channel").toString());
            if (channel == null) {
                persistentMetadata.setMetadata("channel", channelManager.getDefaultChannel().getName());
                return channelManager.getDefaultChannel();
            }
            return channel;
        }
        return channelManager.getDefaultChannel();
    }

    public void setChannel(ConfiguredChannel channel) {
        MetadataContext metadata = person.getContextManager().getContext("metadata");
        Metadata persistentMetadata = metadata.getPersistentMetadata();

        persistentMetadata.setMetadata("channel", channel.getName());
    }

    @Override
    public void onPersonLoad() throws IOException {
    }

    @Override
    public void onPersonSave() throws IOException {
    }

    @Override
    public String getName() {
        return "channel";
    }
}
