package ru.liga.springtelegrambot.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.liga.springtelegrambot.telegrambot.data.Profile;

@FeignClient(name = "profile-registry",
        url = "${feign.server.url}",
        decode404 = true)
public interface FeignServer {

    @PostMapping("profiles")
    ResponseEntity<byte[]> setProfile(@RequestBody Profile profile);

    @GetMapping(value = "profiles/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgMyProfile(@RequestParam("id") Long id);

    /**
     * Кнопка search(влево)
     */
    @GetMapping(value = "profiles/search/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgSearchNext(@RequestParam("id") Long id);

    /**
     * Кнопка search(вправо)
     */
    @GetMapping(value = "profiles/search/like/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgSearchLikeAndNext(@RequestParam("id") Long id);


    @GetMapping(value = "profiles/lovers/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgLoversNext(@RequestParam("id") Long id);

    @GetMapping(value = "profiles/lovers/prev/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgLoversPrev(@RequestParam("id") Long id);
}
