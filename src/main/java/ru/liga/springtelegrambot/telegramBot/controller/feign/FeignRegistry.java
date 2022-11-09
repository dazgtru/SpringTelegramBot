package ru.liga.springtelegrambot.telegramBot.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.liga.springtelegrambot.telegramBot.data.Profile;

@FeignClient(name="profile-registry",
                url="${feign.server.url}",
                decode404 = true)
public interface FeignRegistry {

    @PostMapping("users")
    public Long setProfile(@RequestBody Profile profile);
}
