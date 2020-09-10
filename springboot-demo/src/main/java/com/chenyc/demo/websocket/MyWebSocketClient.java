package com.chenyc.demo.websocket;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFuture;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.util.zip.GZIPInputStream;

public class MyWebSocketClient extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketClient.class);

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }


    @Override
    public void onOpen(ServerHandshake arg0) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sub", "market.ethbtc.kline.1min");
        jsonObject.put("id", "id1");
        send(jsonObject.toString());
        LOGGER.info("------ MyWebSocket onOpen ------");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        // TODO Auto-generated method stub
        LOGGER.info("------ MyWebSocket onClose ------{}",arg1);
    }

    @Override
    public void onError(Exception arg0) {
        // TODO Auto-generated method stub
        LOGGER.info("------ MyWebSocket onError ------{}",arg0);
    }

    @Override
    public void onMessage(String arg0) {
        // TODO Auto-generated method stub
        LOGGER.info("-------- 接收到服务端数据String： " + arg0 + "--------");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            byte[] dst=new byte[bytes.limit()];
            bytes.get(dst);

            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(dst));
            InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, "UTF-8");
            BufferedReader in2 = new BufferedReader(inputStreamReader);
            StringBuilder builder = new StringBuilder();
            String s;
            while ((s = in2.readLine()) != null) {
                builder.append(s);
            }
            LOGGER.info("-------- 接收到服务端数据bytes： " + bytes + "--------");
            LOGGER.info("-------- 接收到服务端数据bytes： " + builder.toString() + "--------");

            JSONObject jsonObject = JSONObject.parseObject(builder.toString());
            Long ping = jsonObject.getLong("ping");
            if (ping!=null) {
                JSONObject resp = new JSONObject();
                resp.put("pong", ping);
                System.out.println(resp.toString());
                send(resp.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
