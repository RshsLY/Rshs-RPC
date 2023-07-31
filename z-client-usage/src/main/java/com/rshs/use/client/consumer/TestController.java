package com.rshs.use.client.consumer;

import com.rshs.api.annotation.RshsConsumer;
import com.rshs.use.api.Hello;
import com.rshs.use.api.Rshs;
import com.rshs.use.api.World;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @RshsConsumer()
    private Hello hello;
    @RshsConsumer()
    private World world;

    @GetMapping("/hello/{var1}")
    @ResponseBody
    public String f1(@PathVariable String var1){
        return hello.HFun(var1);
    }
    @GetMapping("/world/{var1}/{var2}")
    @ResponseBody
    public String f2(@PathVariable String var1, @PathVariable String var2){
        return world.WFun(new Rshs(var1,new Integer(var2))).toString();
    }
}
