package com.chenyc.springcloud.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface GreetingsStreams {
    String INPUT = "greetings-in";
    @Input(INPUT)
    SubscribableChannel inboundGreetings();
}