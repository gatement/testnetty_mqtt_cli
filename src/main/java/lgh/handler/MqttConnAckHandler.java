package lgh.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import lgh.MqttClient;

@Component
public class MqttConnAckHandler extends SimpleChannelInboundHandler<MqttConnAckMessage> {
	private final Logger logger = LoggerFactory.getLogger(MqttConnAckHandler.class);

	@Autowired
	private MqttClient mqttClient;

	@Value("${app.device.mac}")
	private String mac;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttConnAckMessage msg) throws Exception {
		if (msg.variableHeader().connectReturnCode() == MqttConnectReturnCode.CONNECTION_ACCEPTED) {
			logger.info("MQTT CONNECT successfully");

			MqttSubscribeMessage subMsg = MqttMessageBuilders.subscribe().messageId(mqttClient.getMessageId())
					.addSubscription(MqttQoS.AT_MOST_ONCE, String.format("%s/Command", mac)).build();
			ctx.writeAndFlush(subMsg);
		} else {
			logger.info("MQTT CONNECT failed with reason {}", msg.variableHeader().connectReturnCode());
		}
	}
}
