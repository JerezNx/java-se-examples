package xyz.jerez.net.spring.server;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
public class SpringServerExamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServerExamplesApplication.class, args);
    }

    @RestController
    static class InfoController {

        @GetMapping("/get")
        String get() {
            return "success";
        }

        @PostMapping("/postForm")
        String postForm(String name, int num) {
            return "success";
        }

        @PostMapping("/postJson")
        String postJson(@RequestBody Map<String,Object> param) {
            System.out.println(param);
            return "success";
        }
    }
}
