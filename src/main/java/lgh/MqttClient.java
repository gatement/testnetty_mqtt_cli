package lgh;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import lgh.handler.MqttChannelInitializer;
import lgh.mqtt.MqttPingReqMessage;

@Component
public class MqttClient implements DisposableBean, CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(MqttClient.class);

	@Autowired
	private MqttChannelInitializer mqttChannelInitializer;

	private int messageId = 0;

	@Value("${app.mqtt.host}")
	private String host;
	@Value("${app.mqtt.port}")
	private int port;
	@Value("${app.mqtt.heartbeat.interval.seconds}")
	private int heartbeatIntervalSeconds;
	@Value("${app.mqtt.username}")
	private String username;
	@Value("${app.mqtt.password}")
	private String password;
	@Value("${app.device.mac}")
	private String mac;

	private EventLoopGroup group;
	private Channel channel;

	public int getMessageId() {
		messageId++;
		if (messageId > 65535) {
			messageId = 1;
		}
		return messageId;
	}

	@Override
	public void run(String... arg0) throws Exception {
		group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).handler(mqttChannelInitializer);
		logger.info("Start connecting {}:{}", host, port);
		channel = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					logger.info("Connect successfully");
					login();
					future.channel().eventLoop().scheduleAtFixedRate(ping(), heartbeatIntervalSeconds,
							heartbeatIntervalSeconds, TimeUnit.SECONDS);
				} else {
					logger.info("Connect failed: {}", future.cause().getMessage());
					future.cause().printStackTrace();
				}
			}
		}).channel();
	}

	@Override
	public void destroy() throws Exception {
		if (group != null) {
			group.shutdownGracefully();
		}
	}

	private void login() {
		MqttConnectMessage msg = MqttMessageBuilders.connect().clientId(mac).keepAlive(heartbeatIntervalSeconds)
				.protocolVersion(MqttVersion.MQTT_3_1).username(username).password(password.getBytes(CharsetUtil.UTF_8))
				.build();
		channel.writeAndFlush(msg);
	}

	private Runnable ping() {
		return new Runnable() {
			@Override
			public void run() {
				MqttPingReqMessage msg = new MqttPingReqMessage(
						new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
				channel.writeAndFlush(msg);
				logger.debug("MQTT PINGREQ sent");
			}
		};
	}
}