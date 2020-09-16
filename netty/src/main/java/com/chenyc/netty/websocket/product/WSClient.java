//package com.chenyc.netty.websocket.netty;
//
//import com.huobi.hkex.cc.common.EventEnum;
//import com.huobi.hkex.cc.common.ResultCodeEnum;
//import com.huobi.hkex.cc.sdk.Foundation;
//import com.huobi.hkex.cc.sdk.model.AppConfig;
//import com.huobi.hkex.cc.sdk.model.Message;
//import com.huobi.hkex.cc.sdk.model.Subscribe;
//import com.huobi.hkex.lang.AppException;
//import com.huobi.hkex.lang.Code;
//import com.huobi.hkex.lang.Json;
//import com.huobi.hkex.lang.Logger;
//import com.huobi.hkex.lang.R;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.http.DefaultHttpHeaders;
//import io.netty.handler.codec.http.FullHttpResponse;
//import io.netty.handler.codec.http.HttpClientCodec;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
//import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
//import io.netty.handler.codec.http.websocketx.WebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketVersion;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//import io.netty.handler.timeout.IdleStateHandler;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.PreDestroy;
//import java.net.URI;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.Consumer;
//
//public final class WSClient {
//    private final static Logger LOG = Logger.get(WSClient.class);
//    private final String WS_PATH = "/ws";
//    private final EventLoopGroup loopGroup;
//    private volatile boolean isDestroy;
//    private volatile long version;
//    private final Map<String, Set<String>> configItems;
//    private Consumer<R<Collection<AppConfig>>> consumer;
//    private final Foundation foundation;
//    private final WSHandler wsHandler;
//    private int maxInitRetryAttempts;
//    private volatile boolean isInit = true;
//    private final AtomicInteger currentReconnectAttempt = new AtomicInteger(0);
//
//    public WSClient() {
//        this.foundation = Foundation.getInstance();
//        foundation.init();
//        this.configItems = foundation.getConfigItems();
//        this.maxInitRetryAttempts = foundation.getMaxRetryAttemptsLimit();
//        this.wsHandler = new WSHandler(foundation);
//        loopGroup = new NioEventLoopGroup(2);
//    }
//
//    public void start(Consumer<R<Collection<AppConfig>>> consumer) {
//        this.consumer = consumer;
//        connect();
//    }
//
//    private void reconnect() {
//        if (!isDestroy) {
//            if (isInit && maxInitRetryAttempts <= 0) {
//                consumer.accept(R.fail(Code.SYSTEM_ERROR, String.format("连接websocket失败，已尝试%s次，请检查配置或确认服务端已启动！",
//                    foundation.getMaxRetryAttemptsLimit())));
//            } else {
//                loopGroup.schedule(this::connect, nextReconnectInterval(), TimeUnit.SECONDS);
//            }
//        }
//    }
//
//    private void connect() {
//        try {
//            URI uri = URI.create(foundation.getClientAddress());
//            WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13,
//                null, true, new DefaultHttpHeaders());
//            Channel channel = new Bootstrap().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, foundation.getConnectTimeout())
//                                             .option(ChannelOption.SO_KEEPALIVE, true)
//                                             .option(ChannelOption.TCP_NODELAY, true)
//                                             .group(loopGroup)
//                                             .handler(new LoggingHandler(getLogLevel()))
//                                             .channel(NioSocketChannel.class)
//                                             .handler(new ChannelInitializer<SocketChannel>() {
//                                                 @Override
//                                                 protected void initChannel(SocketChannel socketChannel) {
//                                                     ChannelPipeline pipeline = socketChannel.pipeline();
//                                                     pipeline.addLast(new IdleStateHandler(foundation.getReadTimeout(),
//                                                         foundation.getWriteTimeout(), foundation.getAllTimeout(), TimeUnit.SECONDS));
//                                                     pipeline.addLast("http-codec", new HttpClientCodec());
//                                                     pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024));
//                                                     pipeline.addLast("hookedHandler", new ClientHandler(handShaker, consumer));
//                                                 }
//                                             }).connect(uri.getHost(), uri.getPort())
//                                             .sync()
//                                             .channel();
//            handShaker.handshake(channel).sync();
//        } catch (Throwable e) {
//            LOG.error("连接websocket异常", e);
//            reconnect();
//        }
//    }
//
//    private LogLevel getLogLevel() {
//        String levelStr = foundation.getLoggingLevel();
//        switch (levelStr) {
//            case "DEBUG":
//                return LogLevel.DEBUG;
//            case "TRACE":
//                return LogLevel.TRACE;
//            case "WARN":
//                return LogLevel.WARN;
//            case "ERROR":
//                return LogLevel.ERROR;
//            default:
//                return LogLevel.INFO;
//        }
//    }
//
//    @PreDestroy
//    public void destroy() {
//        isDestroy = true;
//        loopGroup.shutdownGracefully();
//    }
//
//    class ClientHandler extends SimpleChannelInboundHandler<Object> {
//        //握手的状态信息
//        private final WebSocketClientHandshaker handShaker;
//        private final Consumer<R<Collection<AppConfig>>> consumer;
//
//        public ClientHandler(WebSocketClientHandshaker handShaker, Consumer<R<Collection<AppConfig>>> consumer) {
//            this.handShaker = handShaker;
//            this.consumer = consumer;
//        }
//
//        /**
//         * 非活跃状态，没有连接远程主机的时候
//         */
//        @Override
//        public void channelInactive(ChannelHandlerContext ctx) {
//            LOG.error("websocket连接断开,重连!");
//            reconnect();
//        }
//
//        /**
//         * 异常处理
//         */
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
//            LOG.error("websocket发生异常！", t);
//            if (t instanceof AppException) {
//                AppException e = (AppException) t;
//                consumer.accept(R.fail(e.getCode(), t.getMessage()));
//            }
//            ctx.close();
//        }
//
//        @Override
//        protected void channelRead0(ChannelHandlerContext ctx, Object obj) {
//            Channel ch = ctx.channel();
//            if (!this.handShaker.isHandshakeComplete()) {
//                FullHttpResponse response = (FullHttpResponse) obj;
//                //握手协议返回，设置结束握手
//                this.handShaker.finishHandshake(ch, response);
//                //发送认证消息
//                wsHandler.sendAuth(ch, WS_PATH, configItems.keySet());
//                currentReconnectAttempt.getAndSet(0);
//                isInit = false;
//                LOG.info("连接websocket成功, remoteAddress={}", ch.remoteAddress());
//            } else if (obj instanceof WebSocketFrame) {
//                WebSocketFrame frame = (WebSocketFrame) obj;
//                //文本信息
//                if (frame instanceof TextWebSocketFrame) {
//                    TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
//                    Message body = Json.decode(textFrame.text(), Message.class);
//                    switch (body.getEvent()) {
//                        case SUB:
//                            handleSub(ch, body);
//                            return;
//                        case AUTH:
//                            handleAuth(ch, body);
//                            return;
//                        case PING:
//                            wsHandler.send(ch, EventEnum.PONG);
//                            return;
//                        default:
//                            LOG.warn("接收到websocket消息,appCode:{} event:{}", body.getAppCode(), body.getEvent());
//                    }
//                } else if (frame instanceof CloseWebSocketFrame) {
//                    CloseWebSocketFrame closeFrame = (CloseWebSocketFrame) frame;
//                    LOG.warn("服务端主动关闭连接,code:{},reason:{},重连!", closeFrame.statusCode(), closeFrame.reasonText());
//                }
//            }
//        }
//    }
//
//    /**
//     * 处理ws接收到的订阅消息
//     */
//    private void handleSub(Channel ch, Message body) {
//        Message.Data data = body.data;
//        // 未认证
//        if (data.getCode() == ResultCodeEnum.PARAM_ERROR_NOT_AUTH.code) {
//            LOG.error("订阅失败,未认证！code:{} message:{}", data.getCode(), data.getMessage());
//            wsHandler.sendAuth(ch, WS_PATH, configItems.keySet());
//        } else if (data.getCode() == ResultCodeEnum.SYSTEM_ERROR.code) {
//            LOG.error("订阅失败,重新订阅！code:{} message:{}", data.getCode(), data.getMessage());
//            subscribe(ch);
//        } else if (data.getCode() == ResultCodeEnum.PUSH_SUCCESS.code) {
//            this.version = data.getTs();
//            if (!CollectionUtils.isEmpty(data.getItems())) {
//                Map<String, AppConfig> appConfigs = new HashMap<>();
//                data.getItems().forEach(r -> {
//                    AppConfig config = appConfigs.computeIfAbsent(r.getGroupCode(), v -> new AppConfig());
//                    config.setAppCode(body.getAppCode());
//                    config.setVersion(data.getTs());
//                    config.setGroupCode(r.getGroupCode());
//                    config.getConfigurations().computeIfAbsent(r.getDataCode(), v -> r.getDataValue());
//                });
//                consumer.accept(R.success(appConfigs.values()));
//            }
//        } else {
//            LOG.info("订阅收到消息,code={},message={}", data.getCode(), data.getMessage());
//        }
//    }
//
//    /**
//     * 处理ws接收到的认证消息
//     */
//    private void handleAuth(Channel ch, Message body) {
//        Message.Data data = body.data;
//        ResultCodeEnum codeEnum = ResultCodeEnum.valueOf(data.getCode());
//        if (codeEnum != null) {
//            switch (codeEnum) {
//                case SUCCESS:
//                    // 认证通过，开始订阅
//                    subscribe(ch);
//                    return;
//                case PARAM_ERROR_AUTH_GROUP:
//                case SIGNATURE_ERROR:
//                case PARAM_ERROR_AUTH_NO_ACCESS:
//                case ILLEGAL_PARAM:
//                case PARAM_MISSING:
//                case SYSTEM_ERROR:
//                    throw new AppException(data.getCode(), data.getMessage());
//                case PARAM_ERROR_AUTH_TIMEOUT:
//                    LOG.error("授权认证超时!重试,code:{} message:{}", data.getCode(), data.getMessage());
//                    wsHandler.sendAuth(ch, WS_PATH, configItems.keySet());
//                    return;
//                default:
//                    LOG.error("授权认证失败,code:{} message:{}", data.getCode(), data.getMessage());
//                    throw new AppException(data.getCode(), data.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 订阅
//     */
//    private void subscribe(Channel ch) {
//        if (!isDestroy && ch != null) {
//            Subscribe sub = new Subscribe(version);
//            configItems.forEach(sub::addItem);
//            wsHandler.sendSub(ch, sub);
//        }
//    }
//
//    private long nextReconnectInterval() {
//        if (isInit) {
//            maxInitRetryAttempts--;
//            return 0L;
//        }
//        int exponentOfTwo = currentReconnectAttempt.getAndIncrement();
//        int maxIntervalExponent = 5;
//        if (exponentOfTwo > maxIntervalExponent) {
//            exponentOfTwo = maxIntervalExponent;
//        }
//        long ret = foundation.getReconnectInterval() * (1 << exponentOfTwo);
//        LOG.info("当前重新连接间隔" + ret + "秒 (T" + exponentOfTwo + ")");
//        return ret;
//    }
//}
