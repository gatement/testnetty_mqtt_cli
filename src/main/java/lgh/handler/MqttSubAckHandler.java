package lgh.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;

@Component
public class MqttSubAckHandler extends SimpleChannelInboundHandler<MqttSubAckMessage> {
	private final Logger logger = LoggerFactory.getLogger(MqttSubAckHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttSubAckMessage msg) throws Exception {
		logger.info("MQTT SUBSCRIBE successfully");
	}
}
