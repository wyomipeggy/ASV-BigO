
package org.javacord.core.event.channel.server.voice;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent;

import java.util.Optional;

/**
 * The implementation of {@link ServerVoiceChannelMemberLeaveEvent}.
 */
public class ServerVoiceChannelMemberLeaveEventImpl extends ServerVoiceChannelMemberEventImpl
        implements ServerVoiceChannelMemberLeaveEvent {

    /**
     * The new channel of the event.
     */
    private final ServerVoiceChannel newChannel;

    /**
     * Creates a new server voice channel member leave event.
     *
     * @param userId The id of the user of the event.
     * @param newChannel The new channel of the event.
     * @param oldChannel The old channel of the event.
     */
    public ServerVoiceChannelMemberLeaveEventImpl(
            Long userId, ServerVoiceChannel newChannel, ServerVoiceChannel oldChannel) {
        super(userId, oldChannel);
        this.newChannel = newChannel;
    }

    @Override
    public Optional<ServerVoiceChannel> getNewChannel() {
        return Optional.ofNullable(newChannel);
    }

    @Override
    public boolean isMove() {
        return newChannel != null;
    }
}
