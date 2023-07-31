package com.rshs.use.server.provider;

import com.rshs.api.annotation.RshsProvider;
import com.rshs.use.api.Hello;

@RshsProvider()

public class HelloImpl implements Hello {
    @Override
    public String HFun(String a) {
        return "fun"+a+"fun";
    }
}
