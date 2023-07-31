package com.rshs.use.server.provider;

import com.rshs.api.annotation.RshsProvider;
import com.rshs.use.api.Rshs;
import com.rshs.use.api.World;

@RshsProvider(port=8999)

public class WorldImpl implements World {
    @Override
    public Rshs WFun(Rshs rshs) {
        rshs.setStr("Rshs"+rshs.getStr());
        rshs.setI(rshs.getI()+123);
        return rshs;
    }
}
