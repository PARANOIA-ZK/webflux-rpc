package com.paranoia.rpc;

import com.paranoia.rsocket.server.RsocketProtocol;
import io.netty.channel.ChannelOption;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import reactor.netty.tcp.TcpServer;

@SpringBootApplication
public class RpcServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RpcServerApplication.class, args);

        RsocketProtocol rsocketProtocol = new RsocketProtocol();
        //todo : 注解实现包的扫描
        rsocketProtocol.getProviderClass("com.paranoia.rpc.service");
        rsocketProtocol.doRegister();

        TcpServer tcpServer = TcpServer.create()
                .host("localhost")
                .port(9999)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE);

        RSocketFactory.receive()
                .acceptor(new RsocketProtocol.SocketAcceptorImpl())
                .transport(TcpServerTransport.create(tcpServer))
                .start()
                .subscribe(System.out::println);
    }
}
