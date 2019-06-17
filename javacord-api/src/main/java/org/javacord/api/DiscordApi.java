package org.javacord.api;

import org.javacord.api.entity.ApplicationInfo;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.GroupChannel;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.UncachedMessageUtil;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerBuilder;
import org.javacord.api.entity.server.invite.Invite;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.entity.webhook.Webhook;
import org.javacord.api.listener.GloballyAttachableListenerManager;
import org.javacord.api.util.concurrent.ThreadPool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is the most important class for your bot, containing all important methods, like registering listener.
 */
public interface DiscordApi extends GloballyAttachableListenerManager {

    /**
     * Gets the used token.
     *
     * @return The used token.
     */
    String getToken();

    /**
     * Gets the used token with the {@link AccountType#getTokenPrefix()}, that
     * way it is usable directly in the authentication header for custom
     * REST calls.
     *
     * @return The prefixed, used token.
     */
    String getPrefixedToken();

    /**
     * Gets the thread pool which is internally used.
     *
     * @return The internally used thread pool.
     */
    ThreadPool getThreadPool();

    /**
     * Gets a utility class to interact with uncached messages.
     *
     * @return A utility class to interact with uncached messages.
     */
    UncachedMessageUtil getUncachedMessageUtil();

    /**
     * Gets the type of the current account.
     *
     * @return The type of the current account.
     */
    AccountType getAccountType();

    /**
     * Creates an invite link for the this bot.
     * The method only works for bot accounts!
     *
     * @return An invite link for this bot.
     * @throws IllegalStateException If the current account is not {@link AccountType#BOT}.
     */
    default String createBotInvite() {
        return new BotInviteBuilder(getClientId()).build();
    }

    /**
     * Creates an invite link for the this bot.
     * The method only works for bot accounts!
     *
     * @param permissions The permissions which should be granted to the bot.
     * @return An invite link for this bot.
     * @throws IllegalStateException If the current account is not {@link AccountType#BOT}.
     */
    default String createBotInvite(Permissions permissions) {
        return new BotInviteBuilder(getClientId()).setPermissions(permissions).build();
    }

    /**
     * Sets the cache size of all caches.
     * This settings are applied on a per-channel basis.
     * It overrides all previous settings, so it's recommended to directly set it after logging in, if you want to
     * change some channel specific cache settings, too.
     * Please notice that the cache is cleared only once every minute!
     *
     * @param capacity The capacity of the message cache.
     * @param storageTimeInSeconds The maximum age of cached messages.
     */
    void setMessageCacheSize(int capacity, int storageTimeInSeconds);

    /**
     * Gets the default message cache capacity which is applied for every newly created channel.
     *
     * @return The default message cache capacity which is applied for every newly created channel.
     */
    int getDefaultMessageCacheCapacity();

    /**
     * Gets the default maximum age of cached messages.
     *
     * @return The default maximum age of cached messages.
     */
    int getDefaultMessageCacheStorageTimeInSeconds();

    /**
     * Sets whether automatic message cache cleanup is enabled for
     * all existing message caches and all newly created ones.
     *
     * @param automaticMessageCacheCleanupEnabled Whether automatic message cache cleanup is enabled.
     */
    void setAutomaticMessageCacheCleanupEnabled(boolean automaticMessageCacheCleanupEnabled);

    /**
     * Gets whether automatic message cache cleanup is enabled.
     *
     * @return Whether automatic message cache cleanup is enabled.
     */
    boolean isDefaultAutomaticMessageCacheCleanupEnabled();

    /**
     * Gets the current shard of the bot, starting with <code>0</code>.
     *
     * @return The current shard of the bot.
     */
    int getCurrentShard();

    /**
     * Gets the total amount of shards. If the total amount is <code>0</code> sharding is disabled.
     *
     * @return The total amount of shards.
     */
    int getTotalShards();

    /**
     * Checks if Javacord is waiting for all servers to become available on startup.
     *
     * @return Whether Javacord is waiting for all servers to become available on startup or not.
     */
    boolean isWaitingForServersOnStartup();

    /**
     * Updates the status of this bot.
     * The update might not be visible immediately as it's through the websocket and only a limited amount of
     * status changes is allowed per minute.
     *
     * @param status The status of this bot.
     */
    void updateStatus(UserStatus status);

    /**
     * Gets the status which should be displayed for this bot.
     * This might not be the status which is really displayed in the client, but it's the status which Javacord
     * is trying to set for your bot, so it might change in the client a few seconds afterwards.
     *
     * @return The status which should be displayed for this bot.
     */
    UserStatus getStatus();

    /**
     * Updates the activity of this bot, represented as "Playing Half-Life 3" for example.
     *
     * @param name The name of the activity.
     */
    void updateActivity(String name);

    /**
     * Updates the activity of this bot with any type.
     *
     * @param type The type of the activity.
     * @param name The name of the activity.
     */
    void updateActivity(ActivityType type, String name);

    /**
     * Updates the activity of this bot with a streaming url, represented as "Streaming Half-Life 3" for example.
     * The update might not be visible immediately as it's through the websocket and only a limited amount of
     * activity status changes is allowed per minute.
     *
     * @param name The name of the activity.
     * @param streamingUrl The streaming url of the activity.
     */
    void updateActivity(String name, String streamingUrl);

    /**
     * Unsets the activity of this bot.
     */
    void unsetActivity();

    /**
     * Gets the activity which should be displayed.
     * This might not be the activity which is really displayed in the client, but it's the activity which Javacord
     * is trying to set for your bot, so it might change in the client a few seconds afterwards.
     *
     * @return The activity which should be displayed.
     */
    Optional<Activity> getActivity();

    /**
     * Gets a user of the connected account.
     * This may be a bot user (for normal bots), or a regular user (for client-bots).
     *
     * @return The user of the connected account.
     */
    User getYourself();

    /**
     * Gets the id of the application's owner.
     *
     * @return The id of the application's owner.
     * @throws IllegalStateException If the current account is not {@link AccountType#BOT}.
     */
    long getOwnerId();

    /**
     * Gets the owner of the application.
     *
     * @return The owner of the application.
     * @throws IllegalStateException If the current account is not {@link AccountType#BOT}.
     */
    default CompletableFuture<User> getOwner() {
        return getUserById(getOwnerId());
    }

    /**
     * Gets the client id of the application.
     *
     * @return The client id of the application.
     * @throws IllegalStateException If the current account is not {@link AccountType#BOT}.
     */
    long getClientId();

    /**
     * Disconnects the bot.
     * After disconnecting you should NOT use this instance again.
     */
    void disconnect();

    /**
     * Sets a function which is used to get the delay between reconnects.
     *
     * @param reconnectDelayProvider A function which get's the amount of reconnects (starting with <code>1</code>) as
     *                               the parameter and should return the delay in seconds to wait for the next reconnect
     *                               attempt. By default the function reconnect delay is calculated using the following
     *                               equation: <code>f(x): (x^1.5-(1/(1/(0.1*x)+1))*x^1.5)+(currentShard*6)</code>.
     *                               This would result in a delay which looks like this for a bot with 1 shard:
     *                               <table>
     *                                  <caption style="display: none">Reconnect Delays</caption>
     *                                  <tr>
     *                                      <th>Attempt</th>
     *                                      <th>Delay</th>
     *                                  </tr>
     *                                  <tr><td>1</td><td>1</td></tr>
     *                                  <tr><td>2</td><td>2</td></tr>
     *                                  <tr><td>3</td><td>4</td></tr>
     *                                  <tr><td>4</td><td>6</td></tr>
     *                                  <tr><td>5</td><td>7</td></tr>
     *                                  <tr><td>...</td><td>...</td></tr>
     *                                  <tr><td>10</td><td>16</td></tr>
     *                                  <tr><td>15</td><td>23</td></tr>
     *                                  <tr><td>20</td><td>30</td></tr>
     *                                  <tr><td>...</td><td>...</td></tr>
     *                                  <tr><td>50</td><td>59</td></tr>
     *                                  <tr><td>100</td><td>91</td></tr>
     *                                  <tr><td>150</td><td>115</td></tr>
     *                                  <tr><td>...</td><td>...</td></tr>
     *                               </table>
     *                               Too many reconnect attempts may cause a token reset (usually 1000 per day), so you
     *                               should always make sure to not provide a function which might exceed this limit.
     *                               You should also make sure to take into account the amount of shards!
     */
    void setReconnectDelay(Function<Integer, Integer> reconnectDelayProvider);

    /**
     * Gets the reconnect delay for a given amount of attempts.
     *
     * @param attempt The amount of attempts (starting with <code>1</code>)
     * @return The reconnect delay in seconds.
     */
    int getReconnectDelay(int attempt);

    /**
     * Gets the application info of the bot.
     * The method only works for bot accounts.
     *
     * @return The application info of the bot.
     */
    CompletableFuture<ApplicationInfo> getApplicationInfo();

    /**
     * Gets a webhook by its id.
     *
     * @param id The id of the webhook.
     * @return The webhook with the given id.
     */
    CompletableFuture<Webhook> getWebhookById(long id);

    /**
     * Gets a collection with the ids of all unavailable servers.
     *
     * @return A collection with the ids of all unavailable servers.
     */
    Collection<Long> getUnavailableServers();

    /**
     * Gets an invite by its code.
     *
     * @param code The code of the invite.
     * @return The invite with the given code.
     */
    CompletableFuture<Invite> getInviteByCode(String code);

    /**
     * Gets an invite by its code while requesting additional information.
     *
     * @param code The code of the invite.
     * @return The invite with the given code.
     */
    CompletableFuture<Invite> getInviteWithMemberCountsByCode(String code);

    /**
     * Creates a server builder which can be used to create servers.
     *
     * @return A server builder.
     */
    default ServerBuilder createServerBuilder() {
        return new ServerBuilder(this);
    }

    /**
     * Creates an account updater for the current account.
     *
     * @return An account updater for the current account.
     */
    default AccountUpdater createAccountUpdater() {
        return new AccountUpdater(this);
    }

    /**
     * Updates the username of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param username The new username.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateUsername(String username) {
        return createAccountUpdater().setUsername(username).update();
    }

    /**
     * Updates the avatar of the current account.
     * This method assumes the file type is "png"!
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(BufferedImage avatar) {
        return createAccountUpdater().setAvatar(avatar).update();
    }

    /**
     * Updates the avatar of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @param fileType The type of the avatar, e.g. "png" or "jpg".
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(BufferedImage avatar, String fileType) {
        return createAccountUpdater().setAvatar(avatar, fileType).update();
    }

    /**
     * Updates the avatar of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(File avatar) {
        return createAccountUpdater().setAvatar(avatar).update();
    }

    /**
     * Updates the avatar of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(Icon avatar) {
        return createAccountUpdater().setAvatar(avatar).update();
    }

    /**
     * Updates the avatar of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(URL avatar) {
        return createAccountUpdater().setAvatar(avatar).update();
    }

    /**
     * Updates the avatar of the current account.
     * This method assumes the file type is "png"!
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(byte[] avatar) {
        return createAccountUpdater().setAvatar(avatar).update();
    }

    /**
     * Updates the avatar of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @param fileType The type of the avatar, e.g. "png" or "jpg".
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(byte[] avatar, String fileType) {
        return createAccountUpdater().setAvatar(avatar, fileType).update();
    }

    /**
     * Updates the avatar of the current account.
     * This method assumes the file type is "png"!
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(InputStream avatar) {
        return createAccountUpdater().setAvatar(avatar).update();
    }

    /**
     * Updates the avatar of the current account.
     *
     * <p>If you want to update several settings at once, it's recommended to use the
     * {@link AccountUpdater} from {@link #createAccountUpdater()} ()} which provides a better performance!
     *
     * @param avatar The new avatar.
     * @param fileType The type of the avatar, e.g. "png" or "jpg".
     * @return A future to check if the update was successful.
     */
    default CompletableFuture<Void> updateAvatar(InputStream avatar, String fileType) {
        return createAccountUpdater().setAvatar(avatar, fileType).update();
    }

    /**
     * Gets a collection with all currently cached users.
     *
     * @return A collection with all currently cached users.
     */
    Collection<User> getCachedUsers();

    /**
     * Gets a cached user by its id.
     *
     * @param id The id of the user.
     * @return The user with the given id.
     */
    Optional<User> getCachedUserById(long id);

    /**
     * Gets a cached user by its id.
     *
     * @param id The id of the user.
     * @return The user with the given id.
     */
    default Optional<User> getCachedUserById(String id) {
        try {
            return getCachedUserById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a user by its id.
     *
     * @param id The id of the user.
     * @return The user with the given id.
     */
    CompletableFuture<User> getUserById(long id);

    /**
     * Gets a user by its id.
     *
     * @param id The id of the user.
     * @return The user with the given id.
     */
    default CompletableFuture<User> getUserById(String id) {
        try {
            return getUserById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return getUserById(-1);
        }
    }

    /**
     * Gets a user by its discriminated name like e. g. {@code Bastian#8222}.
     * This method is case sensitive!
     *
     * @param discriminatedName The discriminated name of the user.
     * @return The user with the given discriminated name.
     */
    default Optional<User> getCachedUserByDiscriminatedName(String discriminatedName) {
        String[] nameAndDiscriminator = discriminatedName.split("#", 2);
        return getCachedUserByNameAndDiscriminator(nameAndDiscriminator[0], nameAndDiscriminator[1]);
    }

    /**
     * Gets a user by its discriminated name like e. g. {@code Bastian#8222}.
     * This method is case insensitive!
     *
     * @param discriminatedName The discriminated name of the user.
     * @return The user with the given discriminated name.
     */
    default Optional<User> getCachedUserByDiscriminatedNameIgnoreCase(String discriminatedName) {
        String[] nameAndDiscriminator = discriminatedName.split("#", 2);
        return getCachedUserByNameAndDiscriminatorIgnoreCase(nameAndDiscriminator[0], nameAndDiscriminator[1]);
    }

    /**
     * Gets a user by its name and discriminator.
     * This method is case sensitive!
     *
     * @param name The name of the user.
     * @param discriminator The discriminator of the user.
     * @return The user with the given name and discriminator.
     */
    default Optional<User> getCachedUserByNameAndDiscriminator(String name, String discriminator) {
        return getCachedUsersByName(name).stream()
                .filter(user -> user.getDiscriminator().equals(discriminator))
                .findAny();
    }

    /**
     * Gets a user by its name and discriminator.
     * This method is case insensitive!
     *
     * @param name The name of the user.
     * @param discriminator The discriminator of the user.
     * @return The user with the given name and discriminator.
     */
    default Optional<User> getCachedUserByNameAndDiscriminatorIgnoreCase(String name, String discriminator) {
        return getCachedUsersByNameIgnoreCase(name).stream()
                .filter(user -> user.getDiscriminator().equalsIgnoreCase(discriminator))
                .findAny();
    }

    /**
     * Gets a collection with all users with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the users.
     * @return A collection with all users with the given name.
     */
    default Collection<User> getCachedUsersByName(String name) {
        return Collections.unmodifiableList(
                getCachedUsers().stream()
                        .filter(user -> user.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all users with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the users.
     * @return A collection with all users with the given name.
     */
    default Collection<User> getCachedUsersByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getCachedUsers().stream()
                        .filter(user -> user.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all users with the given nickname on the given server.
     * This method is case sensitive!
     *
     * @param nickname The nickname of the users.
     * @param server The server where to lookup the nickname.
     * @return A collection with all users with the given nickname on the given server.
     */
    default Collection<User> getCachedUsersByNickname(String nickname, Server server) {
        return Collections.unmodifiableList(
                getCachedUsers().stream()
                        .filter(user -> user.getNickname(server).map(nickname::equals).orElse(false))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all users with the given nickname on the given server.
     * This method is case insensitive!
     *
     * @param nickname The nickname of the users.
     * @param server The server where to lookup the nickname.
     * @return A collection with all users with the given nickname on the given server.
     */
    default Collection<User> getCachedUsersByNicknameIgnoreCase(String nickname, Server server) {
        return Collections.unmodifiableList(
                getCachedUsers().stream()
                        .filter(user -> user.getNickname(server).map(nickname::equalsIgnoreCase).orElse(false))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all users with the given display name on the given server.
     * This method is case sensitive!
     *
     * @param displayName The display name of the users.
     * @param server The server where to lookup the display name.
     * @return A collection with all users with the given display name on the given server.
     */
    default Collection<User> getCachedUsersByDisplayName(String displayName, Server server) {
        return Collections.unmodifiableList(
                getCachedUsers().stream()
                        .filter(user -> user.getDisplayName(server).equals(displayName))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all users with the given display name on the given server.
     * This method is case insensitive!
     *
     * @param displayName The display name of the users.
     * @param server The server where to lookup the display name.
     * @return A collection with all users with the given display name on the given server.
     */
    default Collection<User> getCachedUsersByDisplayNameIgnoreCase(String displayName, Server server) {
        return Collections.unmodifiableList(
                getCachedUsers().stream()
                        .filter(user -> user.getDisplayName(server).equalsIgnoreCase(displayName))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a message set with all currently cached messages.
     *
     * @return A message set with all currently cached messages.
     */
    MessageSet getCachedMessages();

    /**
     * Gets a cached message by its id.
     *
     * @param id The id of the message.
     * @return The cached message.
     */
    Optional<Message> getCachedMessageById(long id);

    /**
     * Gets a cached message by its id.
     *
     * @param id The id of the message.
     * @return The cached message.
     */
    default Optional<Message> getCachedMessageById(String id) {
        try {
            return getCachedMessageById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a message by its id.
     *
     * @param id The id of the message.
     * @param channel The channel of the message.
     * @return The message with the given id.
     * @see TextChannel#getMessageById(long)
     */
    default CompletableFuture<Message> getMessageById(long id, TextChannel channel) {
        return channel.getMessageById(id);
    }

    /**
     * Gets a message by its id.
     *
     * @param id The id of the message.
     * @param channel The channel of the message.
     * @return The message with the given id.
     * @see TextChannel#getMessageById(String)
     */
    default CompletableFuture<Message> getMessageById(String id, TextChannel channel) {
        return channel.getMessageById(id);
    }

    /**
     * Gets a collection with all servers the bot is in.
     *
     * @return A collection with all servers the bot is in.
     */
    Collection<Server> getServers();

    /**
     * Gets a server by its id.
     *
     * @param id The id of the server.
     * @return The server with the given id.
     */
    default Optional<Server> getServerById(long id) {
        return getServers().stream()
                .filter(server -> server.getId() == id)
                .findAny();
    }

    /**
     * Gets a server by its id.
     *
     * @param id The id of the server.
     * @return The server with the given id.
     */
    default Optional<Server> getServerById(String id) {
        try {
            return getServerById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all servers with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the servers.
     * @return A collection with all servers with the given name.
     */
    default Collection<Server> getServersByName(String name) {
        return Collections.unmodifiableList(
                getServers().stream()
                        .filter(server -> server.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all servers with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the servers.
     * @return A collection with all servers with the given name.
     */
    default Collection<Server> getServersByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getServers().stream()
                        .filter(server -> server.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all known custom emojis.
     *
     * @return A collection with all known custom emojis.
     */
    Collection<KnownCustomEmoji> getCustomEmojis();

    /**
     * Gets a custom emoji in this server by its id.
     *
     * @param id The id of the emoji.
     * @return The emoji with the given id.
     */
    default Optional<KnownCustomEmoji> getCustomEmojiById(long id) {
        return getCustomEmojis().stream().filter(emoji -> emoji.getId() == id).findAny();
    }

    /**
     * Gets a custom emoji in this server by its id.
     *
     * @param id The id of the emoji.
     * @return The emoji with the given id.
     */
    default Optional<KnownCustomEmoji> getCustomEmojiById(String id) {
        try {
            return getCustomEmojiById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all custom emojis with the given name in the server.
     * This method is case sensitive!
     *
     * @param name The name of the custom emojis.
     * @return A collection with all custom emojis with the given name in this server.
     */
    default Collection<KnownCustomEmoji> getCustomEmojisByName(String name) {
        return Collections.unmodifiableList(
                getCustomEmojis().stream()
                        .filter(emoji -> emoji.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all custom emojis with the given name in the server.
     * This method is case insensitive!
     *
     * @param name The name of the custom emojis.
     * @return A collection with all custom emojis with the given name in this server.
     */
    default Collection<KnownCustomEmoji> getCustomEmojisByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getCustomEmojis().stream()
                        .filter(emoji -> emoji.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all roles the bot knows.
     *
     * @return A collection with all roles the bot knows.
     */
    default Collection<Role> getRoles() {
        Collection<Role> roles = new HashSet<>();
        getServers().stream().map(Server::getRoles).forEach(roles::addAll);
        return Collections.unmodifiableCollection(roles);
    }

    /**
     * Gets a role by its id.
     *
     * @param id The id of the role.
     * @return The role with the given id.
     */
    default Optional<Role> getRoleById(long id) {
        return getServers().stream()
                .map(server -> server.getRoleById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    /**
     * Gets a role by its id.
     *
     * @param id The id of the role.
     * @return The role with the given id.
     */
    default Optional<Role> getRoleById(String id) {
        try {
            return getRoleById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all roles with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the roles.
     * @return A collection with all roles with the given name.
     */
    default Collection<Role> getRolesByName(String name) {
        return Collections.unmodifiableList(
                getRoles().stream()
                        .filter(role -> role.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all roles with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the roles.
     * @return A collection with all roles with the given name.
     */
    default Collection<Role> getRolesByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getRoles().stream()
                        .filter(role -> role.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all channels of the bot.
     *
     * @return A collection with all channels of the bot.
     */
    Collection<Channel> getChannels();

    /**
     * Gets a collection with all group channels of the bot.
     *
     * @return A collection with all group channels of the bot.
     */
    default Collection<GroupChannel> getGroupChannels() {
        // Not very efficient, but it's a client-only feature and normal users can only be in 100 servers anyway
        return Collections.unmodifiableList(
                getChannels().stream()
                        .filter(GroupChannel.class::isInstance)
                        .map(GroupChannel.class::cast)
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all private channels of the bot.
     *
     * @return A collection with all private channels of the bot.
     */
    default Collection<PrivateChannel> getPrivateChannels() {
        // TODO This can be improved
        return Collections.unmodifiableList(
                getChannels().stream()
                        .filter(PrivateChannel.class::isInstance)
                        .map(PrivateChannel.class::cast)
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all server channels of the bot.
     *
     * @return A collection with all server channels of the bot.
     */
    default Collection<ServerChannel> getServerChannels() {
        // TODO This can be improved
        Collection<ServerChannel> channels = new ArrayList<>();
        getServers().forEach(server -> channels.addAll(server.getChannels()));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all channel categories of the bot.
     *
     * @return A collection with all channel categories of the bot.
     */
    default Collection<ChannelCategory> getChannelCategories() {
        // TODO This can be improved
        Collection<ChannelCategory> channels = new ArrayList<>();
        getServers().forEach(server -> channels.addAll(server.getChannelCategories()));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all server text channels of the bot.
     *
     * @return A collection with all server text channels of the bot.
     */
    default Collection<ServerTextChannel> getServerTextChannels() {
        // TODO This can be improved
        Collection<ServerTextChannel> channels = new ArrayList<>();
        getServers().forEach(server -> channels.addAll(server.getTextChannels()));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all server voice channels of the bot.
     *
     * @return A collection with all server voice channels of the bot.
     */
    default Collection<ServerVoiceChannel> getServerVoiceChannels() {
        // TODO This can be improved
        Collection<ServerVoiceChannel> channels = new ArrayList<>();
        getServers().forEach(server -> channels.addAll(server.getVoiceChannels()));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all text channels of the bot.
     *
     * @return A collection with all text channels of the bot.
     */
    default Collection<TextChannel> getTextChannels() {
        // TODO This can be improved
        return Collections.unmodifiableList(
                getChannels().stream()
                        .filter(TextChannel.class::isInstance)
                        .map(TextChannel.class::cast)
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all voice channels of the bot.
     *
     * @return A collection with all voice channels of the bot.
     */
    default Collection<VoiceChannel> getVoiceChannels() {
        // TODO This can be improved
        return Collections.unmodifiableList(
                getChannels().stream()
                        .filter(VoiceChannel.class::isInstance)
                        .map(VoiceChannel.class::cast)
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a channel by its id.
     *
     * @param id The id of the channel.
     * @return The channel with the given id.
     */
    Optional<Channel> getChannelById(long id);

    /**
     * Gets a channel by its id.
     *
     * @param id The id of the channel.
     * @return The channel with the given id.
     */
    default Optional<Channel> getChannelById(String id) {
        try {
            return getChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all channels with the given name.
     */
    default Collection<Channel> getChannelsByName(String name) {
        Collection<Channel> channels = new HashSet<>();
        channels.addAll(getServerChannelsByName(name));
        channels.addAll(getGroupChannelsByName(name));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all channels with the given name.
     */
    default Collection<Channel> getChannelsByNameIgnoreCase(String name) {
        Collection<Channel> channels = new HashSet<>();
        channels.addAll(getServerChannelsByNameIgnoreCase(name));
        channels.addAll(getGroupChannelsByNameIgnoreCase(name));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a text channel by its id.
     *
     * @param id The id of the text channel.
     * @return The text channel with the given id.
     */
    default Optional<TextChannel> getTextChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asTextChannel);
    }

    /**
     * Gets a text channel by its id.
     *
     * @param id The id of the text channel.
     * @return The text channel with the given id.
     */
    default Optional<TextChannel> getTextChannelById(String id) {
        try {
            return getTextChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all text channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the text channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all text channels with the given name.
     */
    default Collection<TextChannel> getTextChannelsByName(String name) {
        Collection<TextChannel> channels = new HashSet<>();
        channels.addAll(getServerTextChannelsByName(name));
        channels.addAll(getGroupChannelsByName(name));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all text channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the text channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all text channels with the given name.
     */
    default Collection<TextChannel> getTextChannelsByNameIgnoreCase(String name) {
        Collection<TextChannel> channels = new HashSet<>();
        channels.addAll(getServerTextChannelsByNameIgnoreCase(name));
        channels.addAll(getGroupChannelsByNameIgnoreCase(name));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a voice channel by its id.
     *
     * @param id The id of the voice channel.
     * @return The voice channel with the given id.
     */
    default Optional<VoiceChannel> getVoiceChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asVoiceChannel);
    }

    /**
     * Gets a voice channel by its id.
     *
     * @param id The id of the voice channel.
     * @return The voice channel with the given id.
     */
    default Optional<VoiceChannel> getVoiceChannelById(String id) {
        try {
            return getVoiceChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all voice channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the voice channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all voice channels with the given name.
     */
    default Collection<VoiceChannel> getVoiceChannelsByName(String name) {
        Collection<VoiceChannel> channels = new HashSet<>();
        channels.addAll(getServerVoiceChannelsByName(name));
        channels.addAll(getGroupChannelsByName(name));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a collection with all voice channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the voice channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all voice channels with the given name.
     */
    default Collection<VoiceChannel> getVoiceChannelsByNameIgnoreCase(String name) {
        Collection<VoiceChannel> channels = new HashSet<>();
        channels.addAll(getServerVoiceChannelsByNameIgnoreCase(name));
        channels.addAll(getGroupChannelsByNameIgnoreCase(name));
        return Collections.unmodifiableCollection(channels);
    }

    /**
     * Gets a server channel by its id.
     *
     * @param id The id of the server channel.
     * @return The server channel with the given id.
     */
    default Optional<ServerChannel> getServerChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asServerChannel);
    }

    /**
     * Gets a server channel by its id.
     *
     * @param id The id of the server channel.
     * @return The server channel with the given id.
     */
    default Optional<ServerChannel> getServerChannelById(String id) {
        try {
            return getServerChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all server channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the server channels.
     * @return A collection with all server channels with the given name.
     */
    default Collection<ServerChannel> getServerChannelsByName(String name) {
        return Collections.unmodifiableList(
                getServerChannels().stream()
                        .filter(channel -> channel.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all server channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the server channels.
     * @return A collection with all server channels with the given name.
     */
    default Collection<ServerChannel> getServerChannelsByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getServerChannels().stream()
                        .filter(channel -> channel.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a channel category by its id.
     *
     * @param id The id of the channel category.
     * @return The channel category with the given id.
     */
    default Optional<ChannelCategory> getChannelCategoryById(long id) {
        return getChannelById(id).flatMap(Channel::asChannelCategory);
    }

    /**
     * Gets a channel category by its id.
     *
     * @param id The id of the channel category.
     * @return The channel category with the given id.
     */
    default Optional<ChannelCategory> getChannelCategoryById(String id) {
        try {
            return getChannelCategoryById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all channel categories with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the channel categories.
     * @return A collection with all channel categories with the given name.
     */
    default Collection<ChannelCategory> getChannelCategoriesByName(String name) {
        return Collections.unmodifiableList(
                getChannelCategories().stream()
                        .filter(channel -> channel.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all channel categories with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the channel categories.
     * @return A collection with all channel categories with the given name.
     */
    default Collection<ChannelCategory> getChannelCategoriesByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getChannelCategories().stream()
                        .filter(channel -> channel.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a server text channel by its id.
     *
     * @param id The id of the server text channel.
     * @return The server text channel with the given id.
     */
    default Optional<ServerTextChannel> getServerTextChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asServerTextChannel);
    }

    /**
     * Gets a server text channel by its id.
     *
     * @param id The id of the server text channel.
     * @return The server text channel with the given id.
     */
    default Optional<ServerTextChannel> getServerTextChannelById(String id) {
        try {
            return getServerTextChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all server text channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the server text channels.
     * @return A collection with all server text channels with the given name.
     */
    default Collection<ServerTextChannel> getServerTextChannelsByName(String name) {
        return Collections.unmodifiableList(
                getServerTextChannels().stream()
                        .filter(channel -> channel.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all server text channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the server text channels.
     * @return A collection with all server text channels with the given name.
     */
    default Collection<ServerTextChannel> getServerTextChannelsByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getServerTextChannels().stream()
                        .filter(channel -> channel.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a server voice channel by its id.
     *
     * @param id The id of the server voice channel.
     * @return The server voice channel with the given id.
     */
    default Optional<ServerVoiceChannel> getServerVoiceChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asServerVoiceChannel);
    }

    /**
     * Gets a server voice channel by its id.
     *
     * @param id The id of the server voice channel.
     * @return The server voice channel with the given id.
     */
    default Optional<ServerVoiceChannel> getServerVoiceChannelById(String id) {
        try {
            return getServerVoiceChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all server voice channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the server voice channels.
     * @return A collection with all server voice channels with the given name.
     */
    default Collection<ServerVoiceChannel> getServerVoiceChannelsByName(String name) {
        return Collections.unmodifiableList(
                getServerVoiceChannels().stream()
                        .filter(channel -> channel.getName().equals(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all server voice channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the server voice channels.
     * @return A collection with all server voice channels with the given name.
     */
    default Collection<ServerVoiceChannel> getServerVoiceChannelsByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getServerVoiceChannels().stream()
                        .filter(channel -> channel.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a private channel by its id.
     *
     * @param id The id of the private channel.
     * @return The private channel with the given id.
     */
    default Optional<PrivateChannel> getPrivateChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asPrivateChannel);
    }

    /**
     * Gets a private channel by its id.
     *
     * @param id The id of the private channel.
     * @return The private channel with the given id.
     */
    default Optional<PrivateChannel> getPrivateChannelById(String id) {
        try {
            return getPrivateChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a group channel by its id.
     *
     * @param id The id of the group channel.
     * @return The group channel with the given id.
     */
    default Optional<GroupChannel> getGroupChannelById(long id) {
        return getChannelById(id).flatMap(Channel::asGroupChannel);
    }

    /**
     * Gets a group channel by its id.
     *
     * @param id The id of the group channel.
     * @return The group channel with the given id.
     */
    default Optional<GroupChannel> getGroupChannelById(String id) {
        try {
            return getGroupChannelById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a collection with all group channels with the given name.
     * This method is case sensitive!
     *
     * @param name The name of the group channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all group channels with the given name.
     */
    default Collection<GroupChannel> getGroupChannelsByName(String name) {
        return Collections.unmodifiableList(
                getGroupChannels().stream()
                        .filter(channel -> Objects.deepEquals(channel.getName().orElse(null), name))
                        .collect(Collectors.toList()));
    }

    /**
     * Gets a collection with all server channels with the given name.
     * This method is case insensitive!
     *
     * @param name The name of the group channels. Can be <code>null</code> to search for group channels without name.
     * @return A collection with all group channels with the given name.
     */
    default Collection<GroupChannel> getGroupChannelsByNameIgnoreCase(String name) {
        return Collections.unmodifiableList(
                getGroupChannels().stream()
                        .filter(channel -> {
                            String channelName = channel.getName().orElse(null);
                            if (name == null || channelName == null) {
                                return Objects.deepEquals(channelName, name);
                            }
                            return name.equalsIgnoreCase(channelName);
                        })
                        .collect(Collectors.toList()));
    }

}
