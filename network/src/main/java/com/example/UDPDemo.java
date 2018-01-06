package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by zhb on 2017/11/18.
 */

public class UDPDemo {

    static class UDPServer {
        public static void main(String[] args) throws IOException {
            DatagramSocket server = new DatagramSocket(5050);
            byte[] recvBuf = new byte[100];
            DatagramPacket recvPacket
                    = new DatagramPacket(recvBuf, recvBuf.length);
            boolean flag = true;
            while (flag) {
                server.receive(recvPacket);
                String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                if ("886".equals(recvStr)) {
                    flag = false;
                }
                System.out.println("新消息：" + recvStr);
                int port = recvPacket.getPort();
                InetAddress addr = recvPacket.getAddress();
//                //获取键盘输入
//                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
//                System.out.print("回复信息：");
                byte[] sendBuf;
                sendBuf = "断开连接测试".getBytes();
                DatagramPacket sendPacket
                        = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
                server.send(sendPacket);
            }
            server.close();
        }
    }

    static class UDPClient {
        public static void main(String[] args) throws IOException {
            DatagramSocket client = new DatagramSocket();

            InetAddress addr = InetAddress.getByName("127.0.0.1");
            int port = 5050;
            boolean flag = true;
            while (flag) {
                //获取键盘输入
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("输入信息：");
                String str = input.readLine();
                if ("886".equals(str)) {
                    flag = false;
                }
                byte[] sendBuf;
                sendBuf = str.getBytes();
                DatagramPacket sendPacket
                        = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
                client.send(sendPacket);
                byte[] recvBuf = new byte[100];
                DatagramPacket recvPacket
                        = new DatagramPacket(recvBuf, recvBuf.length);
                client.receive(recvPacket);
                String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                System.out.println("收到:" + recvStr);
            }
            if (client != null) {
                client.close();
            }
        }
    }
}
