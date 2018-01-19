package io.owslab.mailreceiver.controller;

/**
 * Created by khanhlvb on 1/10/18.
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class IndexController {

    @RequestMapping("/")
    String index(){
        return "index";
    }
}
