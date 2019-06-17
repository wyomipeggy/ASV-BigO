package org.javacord.core.entity.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.Webhook;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.IconImpl;
import org.javacord.core.util.logging.LoggerUtil;
import org.javacord.core.util.rest.RestEndpoint;
import org.javacord.core.util.rest.RestMethod;
import org.javacord.core.util.rest.RestRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The implementation of {@link Webhook}.
 */
public class WebhookImpl implements Webhook {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(WebhookImpl.class);

    private final DiscordApiImpl api;

    private final long id;
    private final Long serverId;
    private final long channelId;
    private final User user;
    private final String name;
    private final String avatarId;
    private final String token;

    /**
     * Creates a new webhook.
     *
     * @param api The discord api instance.
     * @param data The json data of the webhook.
     */
    public WebhookImpl(DiscordApi api, JsonNode data) {
        this.api = (DiscordApiImpl) api;

        this.id = Long.parseLong(data.get("id").asText());
        this.serverId = data.has("guild_id") ? Long.parseLong(data.get("guild_id").asText()) : null;
        this.channelId = Long.parseLong(data.get("channel_id").asText());
        this.user = data.has("user") ? this.api.getOrCreateUser(data.get("user")) : null;
        this.name = data.has("name") && !data.get("name").isNull() ? data.get("name").asText() : null;
        this.avatarId = data.has("avatar") && !data.get("avatar").isNull() ? data.get("avatar").asText() : null;
        this.token = data.has("token") ? data.get("token").asText() : null;
    }

    @Override
    public DiscordApi getApi() {
        return api;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Optional<Long> getServerId() {
        return Optional.ofNullable(serverId);
    }

    @Override
    public Optional<Server> getServer() {
        return getServerId().map(api::getServerById).filter(Optional::isPresent).map(Optional::get);
    }

    @Override
    public Optional<TextChannel> getChannel() {
        return api.getTextChannelById(getChannelId());
    }

    @Override
    public long getChannelId() {
        return channelId;
    }

    @Override
    public Optional<User> getCreator() {
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<Icon> getAvatar() {
        if (avatarId != null) {
            String url = "https://cdn.discordapp.com/avatars/" + getIdAsString() + "/" + avatarId
                    + (avatarId.startsWith("a_") ? ".gif" : ".png");
            try {
                return Optional.of(new IconImpl(getApi(), new URL(url)));
            } catch (MalformedURLException e) {
                logger.warn("Seems like the url of the avatar is malformed! Please contact the developer!", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getToken() {
        return Optional.ofNullable(token);
    }

    @Override
    public CompletableFuture<Void> delete(String reason) {
        return new RestRequest<Void>(getApi(), RestMethod.DELETE, RestEndpoint.WEBHOOK)
                .setUrlParameters(getIdAsString())
                .setAuditLogReason(reason)
                .execute(result -> null);
    }

    @Override
    public boolean equals(Object o) {
        return (this == o)
               || !((o == null)
                    || (getClass() != o.getClass())
                    || (getId() != ((DiscordEntity) o).getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("Webhook (id: %s, name: %s)", getIdAsString(), getName());
    }

}
