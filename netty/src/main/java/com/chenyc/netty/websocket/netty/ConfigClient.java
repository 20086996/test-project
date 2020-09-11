//package com.chenyc.netty.websocket.netty;
//
//import com.huobi.hkex.cc.sdk.model.AppConfig;
//import com.huobi.hkex.cc.sdk.model.ChangeEvent;
//import com.huobi.hkex.cc.sdk.model.GroupChangeEvent;
//import com.huobi.hkex.cc.sdk.model.GroupConfig;
//import com.huobi.hkex.cc.sdk.ws.WSClient;
//import com.huobi.hkex.lang.AppException;
//import com.huobi.hkex.lang.Code;
//import com.huobi.hkex.lang.Logger;
//import com.huobi.hkex.lang.R;
//import org.springframework.util.CollectionUtils;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
///**
// * 单例模式的ConfigClient
// */
//public final class ConfigClient {
//    private final static Logger LOG = Logger.get(ConfigClient.class);
//    private volatile static ConfigClient instance;
//    private final Map<String, Config> configs;
//    private final ExecutorService executor;
//    private final CountDownLatch countDownLatch;
//    private final WSClient wsClient;
//    private R<Collection<AppConfig>> res;
//    private Consumer<GroupChangeEvent> eventConsumer;
//
//    private ConfigClient() {
//        this.countDownLatch = new CountDownLatch(1);
//        this.wsClient = new WSClient();
//        this.executor = Executors.newCachedThreadPool();
//        this.configs = new ConcurrentHashMap<>();
//    }
//
//    public static ConfigClient getInstance() {
//        if (instance == null) {
//            synchronized (ConfigClient.class) {
//                if (instance == null) {
//                    instance = new ConfigClient();
//                }
//            }
//        }
//        return instance;
//    }
//
//    /**
//     * 启动客户端，并启动websocket客户端
//     */
//    public void start(Consumer<Collection<Config>> consumer) {
//        wsClient.start(r -> {
//            if (r.ok()) {
//                // 按组存储配置项
//                r.getData().forEach(v -> {
//                    GroupConfig config = (GroupConfig) configs.computeIfAbsent(v.getGroupCode(), k -> new GroupConfig(v.getGroupCode()));
//                    handleAppConfig(config, v);
//                });
//                consumer.accept(configs.values());
//            }
//
//            res = r;
//            countDownLatch.countDown();
//        });
//
//        // 等待配置初始化
//        try {
//            countDownLatch.await();
//            if (res.failed()) {
//                throw new AppException(res.getCode(), res.getMessage());
//            }
//        } catch (InterruptedException e) {
//            throw new AppException(Code.SYSTEM_ERROR, "从配置中心获取配置初始化失败!");
//        }
//    }
//
//    public void setListener(Consumer<GroupChangeEvent> eventConsumer) {
//        this.eventConsumer = eventConsumer;
//    }
//
//    /**
//     * 处理从上游接收到的消息
//     */
//    private void handleAppConfig(GroupConfig groupConfig, AppConfig appConfig) {
//        String groupCode = appConfig.getGroupCode();
//        try {
//            Properties newProperties = new Properties();
//            newProperties.putAll(appConfig.getConfigurations());
//            if (groupConfig.getProperties() == null) {
//                groupConfig.updateProperties(newProperties);
//            } else {
//                Map<String, ChangeEvent> actualChanges = calcPropertiesChanges(groupCode,
//                    newProperties, groupConfig.getProperties());
//
//                if (!CollectionUtils.isEmpty(actualChanges)) {
//                    groupConfig.updateProperties(newProperties);
//                    fireConfigChange(new GroupChangeEvent(groupCode, actualChanges));
//                }
//            }
//        } catch (Throwable ex) {
//            LOG.warn("处理上游推送的配置失败,groupCode={}", groupCode, ex);
//        }
//    }
//
//    /**
//     * 计算配置变更项
//     */
//    private Map<String, ChangeEvent> calcPropertiesChanges(String groupCode, Properties current,
//                                                           Properties previous) {
//        if (current == null) {
//            current = new Properties();
//        }
//        if (previous == null) {
//            previous = new Properties();
//        }
//
//        Set<String> previousKeys = previous.stringPropertyNames();
//        Set<String> currentKeys = current.stringPropertyNames();
//        //相同的key
//        Set<String> commonKeys = previousKeys.stream().filter(currentKeys::contains).collect(Collectors.toSet());
//        Map<String, ChangeEvent> changes = new HashMap<>();
//        for (String key : commonKeys) {
//            String previousValue = previous.getProperty(key);
//            String currentValue = current.getProperty(key);
//            if (previousValue.equals(currentValue)) {
//                continue;
//            }
//            changes.computeIfAbsent(key, k -> new ChangeEvent(groupCode, key, previousValue, currentValue));
//        }
//        return changes;
//    }
//
//    /**
//     * 配置变更事件
//     */
//    private void fireConfigChange(final GroupChangeEvent event) {
//        if (eventConsumer != null) {
//            executor.submit(() -> {
//                try {
//                    eventConsumer.accept(event);
//                } catch (Throwable ex) {
//                    LOG.error("通知配置变更事件异常,groupCode={}", event.groupCode, ex);
//                }
//            });
//        }
//    }
//}
