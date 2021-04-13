package com.chenyc.springcloud.service;

import com.chenyc.springcloud.config.GreetingsStreams;
import com.chenyc.springcloud.po.Greetings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
@Slf4j
public class GreetingsService {
    @Autowired
    GreetingsStreams greetingsStreams;

    public void sendGreeting(final Greetings greetings) {
        log.info("Sending greetings {}", greetings);
        MessageChannel messageChannel = greetingsStreams.outboundGreetings();
        boolean send = messageChannel.send(MessageBuilder
                .withPayload(greetings)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
        System.out.println(send);
    }
}