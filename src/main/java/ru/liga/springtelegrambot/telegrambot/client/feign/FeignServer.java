package ru.liga.springtelegrambot.telegrambot.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.liga.springtelegrambot.telegrambot.data.Profile;

@FeignClient(name="profile-registry",
                url="${feign.server.url}",
                decode404 = true)
public interface FeignServer {

    @PostMapping("profiles")
    Long setProfile(@RequestBody Profile profile);

    @GetMapping(value = "profiles/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgMyProfile(@RequestParam("id") Long id);

    @GetMapping(value = "profiles/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImgSearchNext(@RequestParam("id") Long id);
}

//запрос картинки
//@Operation(summary = "Печать постановления")
//@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Файл постановление")})
//@RequestMapping(value = "/print", method = RequestMethod.GET,
//        produces = MediaType.MULTIPART_FORM_DATA_VALUE)
//public void print(HttpServletResponse response, @RequestParam("caseId") Long caseId) throws Exception {
//    printService.printResolution(response, caseId);
//}
