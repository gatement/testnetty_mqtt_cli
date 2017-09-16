package lgh.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

@Component
public class MqttPingRespHandler extends SimpleChannelInboundHandler<MqttMessage> {
	private final Logger logger = LoggerFactory.getLogger(MqttPingRespHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
		if (msg.fixedHeader().messageType() == MqttMessageType.PINGRESP) {
			logger.debug("MQTT PINGRESP received");
		} else {
			logger.warn("Received unhandled mqtt message: ", msg.fixedHeader().messageType());
		}
	}
}