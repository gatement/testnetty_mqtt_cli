package lgh.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

@Component
public class MqttChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	private MqttConnAckHandler mqttConnAckHandler;
	@Autowired
	private MqttSubAckHandler mqttSubAckHandler;
	@Autowired
	private MqttPublishHandler mqttPublishHandler;
	@Autowired
	private MqttPingRespHandler mqttPingRespHandler;

	@Override
	protected void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(MqttEncoder.INSTANCE);
		pipeline.addLast(new MqttDecoder());
		pipeline.addLast(mqttConnAckHandler);
		pipeline.addLast(mqttSubAckHandler);
		pipeline.addLast(mqttPublishHandler);
		pipeline.addLast(mqttPingRespHandler);
	}
}