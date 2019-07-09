package org.adlerzz.apollo.app.lifecycle;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.adlerzz.apollo.app.param.Param.PING_URL;

@Controller
@CrossOrigin
@EnableScheduling
public class LifecycleController {

    private RestTemplate restTemplate;

    public LifecycleController() {
         this.restTemplate = new RestTemplate();
         this.restTemplate.setErrorHandler( new NoOps() );
    }

    @GetMapping("/ping")
    public @ResponseBody ResponseEntity<String> ping(){
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }

    @GetMapping("/shutdown")
    public @ResponseBody ResponseEntity<Void> shutdown(){
        System.exit(-2);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Scheduled(cron="0 */5 * * * *")
    public void task(){
        final String url = PING_URL.getValue();
        String response = restTemplate.getForObject( url, String.class);
        System.out.println(response);
    }

    private class NoOps extends DefaultResponseErrorHandler{
        public void handleError(ClientHttpResponse response) throws IOException {
        }
    }


}
