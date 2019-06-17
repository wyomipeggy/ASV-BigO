package com.bigO.asv;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.Region;
import org.javacord.api.entity.server.DefaultMessageNotificationLevel;
import org.javacord.api.entity.server.ServerBuilder;
import org.javacord.api.entity.server.VerificationLevel;
import org.junit.Test;

public class CreatBotTest {

    @Test
    public void testBotCreation() throws Exception{

        String botToken = "NTg5NDA5MDYwNjIxOTEwMDI2.XQTQTA.PeeMzOOmAtgaxKHiWY9L7jJN1Uc";

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).login().join();
        System.out.println(api.createBotInvite());

        new ServerBuilder(api)
                .setName("Test server asv")
                .setIcon(api.getYourself().getAvatar())
                .setVerificationLevel(VerificationLevel.HIGH)
                .setDefaultMessageNotificationLevel(DefaultMessageNotificationLevel.ONLY_MENTIONS)
                .setRegion(Region.EU_CENTRAL)
                .create();
    }
}
