package com.rshs.center;

import com.rshs.api.message.RegRequestMessage;
import com.rshs.api.message.ServerAddrReqMessage;
import com.rshs.api.message.ServerAddrRespMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collections;


@Slf4j
public class ServerAddrHandler  extends SimpleChannelInboundHandler<ServerAddrReqMessage> {
    private StringRedisTemplate stringRedisTemplate;

    public ServerAddrHandler(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }


    private static final String LUA_SCRIPT = "local hashKey = KEYS[1]\n" +
            "local currentTime = tonumber(ARGV[1])\n" +
            "\n" +
            "while true do\n" +
            "    local randomKey = redis.call(\"HRANDFIELD\", hashKey)\n" +
            "    if randomKey == false then\n" +
            "        return nil\n" +
            "    end\n" +
            "    local value = tonumber(redis.call(\"HGET\", hashKey, randomKey))\n " +
            "    if value > currentTime then\n" +
            "        return randomKey\n" +
            "    else\n" +
            "        redis.call('HDEL', hashKey, randomKey)\n" +
            "    end\n" +
            "end";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ServerAddrReqMessage serverAddrReqMessage) throws Exception {
        try{
            DefaultRedisScript<String> script = new DefaultRedisScript<>(LUA_SCRIPT, String.class);
            script.setResultType(String.class);
            String currentTime = String.valueOf(System.currentTimeMillis());
            String randomKey = stringRedisTemplate.execute(script, new StringRedisSerializer(), new StringRedisSerializer(),
                    Collections.singletonList(serverAddrReqMessage.getInterfaceName()), currentTime);

            log.info("random key is:{}",randomKey);
            int lastIndex = randomKey.lastIndexOf(".");

            String part1 = randomKey.substring(0, lastIndex);
            String part2 = randomKey.substring(lastIndex + 1);
            channelHandlerContext.writeAndFlush(new ServerAddrRespMessage(serverAddrReqMessage.getSequenceId(),part1,new Integer(part2)));
        }catch (Exception e){
            log.info("RPC失败，可能远程主机已经下线或接口名错误");
        }


    }
}
