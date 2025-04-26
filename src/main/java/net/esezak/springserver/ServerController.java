package net.esezak.springserver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    @GetMapping("/getdata")
    public String getData() {
        return "Hello World!";
    }

    public String index() {
        return "index.html";
    }

    @GetMapping("/askai")
    public String askAI(){
        /*

         */
        return "Hello World!";
    }
    @PostMapping("/postpromt")
    public String postPromt(String promt){
        return AIController.askAI(promt);
    }
}
