package com.chenyc.netty.websocket.netty;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebsocketTest {

  //只是为了阻塞主线程
  static CountDownLatch countDownLatch=new CountDownLatch(1);
  public static void main(String[] args) throws InterruptedException {
    OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    Request request = new Request.Builder().url("wss://l10n-pro.huobiasia.club:443/-/s/pro/ws").build();
    client.dispatcher().cancelAll();//清理一次
    client.newWebSocket(
        request,
        new WebSocketListener() {
          @Override
          public void onOpen(WebSocket webSocket, Response response) {
            System.out.println("连接打开");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sub", "market.ethbtc.kline.1min");
            jsonObject.put("id", "id1");
            webSocket.send(jsonObject.toString());
          }

          @Override
          public void onMessage(WebSocket webSocket, String text) {
              JSONObject jsonObject = JSONObject.parseObject(text);
              Long ping = jsonObject.getLong("ping");
              System.out.println("接收到消息：" + text);

              JSONObject resp = new JSONObject();
              resp.put("pong", ping);
              webSocket.send(resp.toString());
          }

          @Override
          public void onMessage(WebSocket webSocket, ByteString bytes) {
              try {

                  GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes.toByteArray()));
                  InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, "UTF-8");
                  BufferedReader in2 = new BufferedReader(inputStreamReader);
                  StringBuilder builder = new StringBuilder();
                  String s;
                  while ((s = in2.readLine()) != null) {
                      builder.append(s);
                  }


                  JSONObject jsonObject = JSONObject.parseObject(builder.toString());
                  Long ping = jsonObject.getLong("ping");
                  if (ping!=null) {
                      JSONObject resp = new JSONObject();
                      resp.put("pong", ping);
                      System.out.println(resp.toString());
                      webSocket.send(resp.toString());
                  }
                  System.out.println(builder.toString());
              } catch (IOException e) {

              }
          }

          @Override
          public void onClosing(WebSocket webSocket, int code, String reason) {
            System.out.println("连接关闭中");
          }

          @Override
          public void onClosed(WebSocket webSocket, int code, String reason) {
            System.out.println("连接关闭");
          }

          @Override
          public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            System.out.println("结束时，重连可以在这儿发起");
            countDownLatch.countDown();
          }
        });
    countDownLatch.await();
  }
}

