package com.bigO.asv;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.junit.Test;

public class CreatBotTest {

    @Test
    public void testBotCreation() throws Exception{

        String botToken = "NTg5NDA5MDYwNjIxOTEwMDI2.XQTQTA.PeeMzOOmAtgaxKHiWY9L7jJN1Uc";

        DiscordApi api = new DiscordApiBuilder().setToken(botToken)
                .login().join();
        System.out.println(api.createBotInvite());
    }
}
