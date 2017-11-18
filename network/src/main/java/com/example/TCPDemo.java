package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPDemo {


    public static class Client {
        public static void main(String[] args) throws IOException {
            //客户端请求与本机在20006端口建立TCP连接
            Socket client = new Socket("127.0.0.1", 20006);
            client.setSoTimeout(10000);
            //获取键盘输入
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            //获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(client.getOutputStream());
            //获取Socket的输入流，用来接收从服务端发送过来的数据
            BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean flag = true;
            while(flag){
                System.out.print("输入信息：");
                String str = input.readLine();
                //发送数据到服务端
                out.println(str);
                if("bye".equals(str)){
                    flag = false;
                }else{
                    try{
                        //从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
                        String echo = buf.readLine();
                        System.out.println(echo);
                    }catch(SocketTimeoutException e){
                        System.out.println("Time out, No response");
                    }
                }
            }
            input.close();
            if(client != null){
                //如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭
                client.close(); //只关闭socket，其关联的输入输出流也会被关闭
            }
        }
    }

    /**
     * 该类为多线程类，用于服务端
     */
    public static class ServerThread implements Runnable {

        private Socket client = null;
        public ServerThread(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            try{
                //获取Socket的输出流，用来向客户端发送数据
                PrintStream out = new PrintStream(client.getOutputStream());
                //获取Socket的输入流，用来接收从客户端发送过来的数据
                BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                boolean flag =true;
                while(flag){
                    //接收从客户端发送过来的数据
                    String str =  buf.readLine();
                    if(str == null || "".equals(str)){
                        flag = false;
                    }else{
                        if("bye".equals(str)){
                            flag = false;
                        }else{
                            //将接收到的字符串前面加上echo，发送到对应的客户端
                            out.println("echo:" + str);
                        }
                    }
                }
                out.close();
                client.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public static class Server {
        public static void main(String[] args) throws Exception{
            //服务端在20006端口监听客户端请求的TCP连接
            ServerSocket server = new ServerSocket(20006);
            Socket client = null;
            boolean f = true;
            while(f){
                //等待客户端的连接，如果没有获取连接
                client = server.accept();
                System.out.println("与客户端连接成功！");
                //为每个客户端连接开启一个线程
                new Thread(new ServerThread(client)).start();
            }
            server.close();
        }
    }
}
