package ep.asyncapi.tool.sap.is.converter.service.apis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class SolaceCloudV0APIClient {

    @Value("${solace.api.v0.token-permission-url}")
    private String SOLACE_V0_TOKEN_PERMISSIONS_URL;

    public HttpStatus validateUserEPToken(final String userEpToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userEpToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(SOLACE_V0_TOKEN_PERMISSIONS_URL, HttpMethod.GET, entity, String.class);
            return response.getStatusCode();
        } catch (Exception exception) {
            return HttpStatus.UNAUTHORIZED;
        }
    }


}
