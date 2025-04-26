package net.esezak.springserver;


import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServerController {

    @GetMapping("/getdata")
    public String getData() {
        return "Hello World!";
    }

    @GetMapping("/askai")
    public String askAI(){

        return "Hello World!";
    }
}
