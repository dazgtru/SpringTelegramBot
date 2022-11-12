package ru.liga.springtelegrambot.telegrambot.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.liga.springtelegrambot.telegrambot.data.Profile;

@FeignClient(name="profile-registry",
                url="${feign.server.url}",
                decode404 = true)
public interface FeignRegistry {

    @PostMapping("users")
    Long setProfile(@RequestBody Profile profile);
}

//запрос картинки
//@Operation(summary = "Печать постановления")
//@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Файл постановление")})
//@RequestMapping(value = "/print", method = RequestMethod.GET,
//        produces = MediaType.MULTIPART_FORM_DATA_VALUE)
//public void print(HttpServletResponse response, @RequestParam("caseId") Long caseId) throws Exception {
//    printService.printResolution(response, caseId);
//}
