package net.advancius.player.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.channel.ChannelManager;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.person.context.ChannelContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.utils.Metadata;

@Data
public class PlayerChannelContext extends ChannelContext {

    @Override
    public JsonObject serializeJson() {
        return new JsonObject();
    }

    @Override
    public ConfiguredChannel getChannel() {
        ChannelManager channelManager = AdvanciusBungee.getInstance().getChannelManager();

        MetadataContext metadata = person.getContextManager().getContext(MetadataContext.class);
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

    @Override
    public void setChannel(ConfiguredChannel channel) {
        MetadataContext metadata = person.getContextManager().getContext(MetadataContext.class);
        Metadata persistentMetadata = metadata.getPersistentMetadata();

        persistentMetadata.setMetadata("channel", channel.getName());
    }

    @Override
    public void onPersonLoad() {
    }

    @Override
    public void onPersonSave() {
    }
}
