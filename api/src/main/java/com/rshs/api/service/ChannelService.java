package com.rshs.api.service;

import io.netty.channel.Channel;

public interface ChannelService {
    Channel getChannel(String host, int port);

}
