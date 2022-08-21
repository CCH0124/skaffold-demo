package cch.com.example.skaffold.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getName());

    @GetMapping("/hello")
    public String hello() {
        StringBuilder message = new StringBuilder("Hello Skaffold!");
        try {
            InetAddress ip = InetAddress.getLocalHost();
            message.append(" From host: " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return message.toString();
    }

    @GetMapping("/states")
    public ResponseEntity<?> states() {
        LOGGER.info("Get Itachi");
        return ResponseEntity.ok(Map.of("name", "itachi", "age", 28));
    }
}
