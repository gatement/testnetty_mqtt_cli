package lgh.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

@Component
public class MqttPublishHandler extends SimpleChannelInboundHandler<MqttPublishMessage> {
	private final Logger logger = LoggerFactory.getLogger(MqttPublishHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttPublishMessage msg) throws Exception {
		logger.info("received pub: {}", msg.toString());
	}
}
