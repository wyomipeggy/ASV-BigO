package org.javacord.core.util.handler.message;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.core.event.message.MessageDeleteEventImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.javacord.core.util.gateway.PacketHandler;

import java.util.Optional;

/**
 * Handles the message delete packet.
 */
public class MessageDeleteHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public MessageDeleteHandler(DiscordApi api) {
        super(api, true, "MESSAGE_DELETE");
    }

    @Override
    public void handle(JsonNode packet) {
        long messageId = packet.get("id").asLong();
        long channelId = packet.get("channel_id").asLong();

        api.getTextChannelById(channelId).ifPresent(channel -> {
            MessageDeleteEvent event = new MessageDeleteEventImpl(api, messageId, channel);

            api.removeMessageFromCache(messageId);

            Optional<Server> optionalServer = channel.asServerChannel().map(ServerChannel::getServer);
            api.getEventDispatcher().dispatchMessageDeleteEvent(
                    optionalServer.map(DispatchQueueSelector.class::cast).orElse(api),
                    messageId,
                    optionalServer.orElse(null),
                    channel,
                    event);
            api.removeObjectListeners(Message.class, messageId);
        });
    }
}
