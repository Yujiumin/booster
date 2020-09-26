package com.github.booster.core.producer.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.booster.annotation.Message;
import com.github.booster.annotation.Producer;
import com.github.booster.common.constant.BoosterConstant;
import com.github.booster.core.producer.BoosterProducerContext;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * 生产者代理
 *
 * @author Yujiumin
 * @version 2020/09/20
 */
@Aspect
public class ProducerAspect {

    private Logger logger = LoggerFactory.getLogger(ProducerAspect.class);

    /**
     * 类上要带有 {@link com.github.booster.annotation.Producer} 注解
     */
    @Pointcut("@within(com.github.booster.annotation.Producer)")
    public void producer() {

    }

    /**
     * 必须是 {@link com.github.booster.core.producer.AbstractMessageProducer} 的子类
     */
    @Pointcut("within(com.github.booster.core.producer.AbstractMessageProducer+)")
    public void callback() {

    }

    /**
     * 方法上要有 {@link com.github.booster.annotation.Message} 注解
     */
    @Pointcut("@annotation(com.github.booster.annotation.Message)")
    public void message() {

    }

    /**
     * 方法调用前调用
     */
    @Before("producer() && callback() && message()")
    public void beforeInvoke(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            Class<?> returnType = method.getReturnType();
            if (Objects.equals(returnType, Void.TYPE)) {
                throw new UnsupportedOperationException("返回类型不可以为 void");
            }
        }
    }

    /**
     * 方法执行完后调用
     *
     * @param joinPoint 连接点
     * @param result    方法的返回值
     */
    @AfterReturning(pointcut = "producer() && callback() && message()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        try {
            Object target = joinPoint.getTarget();
            Producer producerAnnotation = target.getClass().getAnnotation(Producer.class);
            // 选用生产者
            MQProducer producer = BoosterProducerContext.getProducer(producerAnnotation.group());
            if (!Objects.isNull(producer)) {
                MessageExt messageExt = new MessageExt();

                //设置META信息
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                Message message = method.getAnnotation(Message.class);
                String topic = message.topic();
                String[] tags = message.tags();
                String[] keys = message.keys();

                messageExt.setTopic(topic);
                messageExt.setTags(String.join(BoosterConstant.TAG_SEPARATOR, tags));
                messageExt.setKeys(Arrays.asList(keys));

                // 设置消息内容
                byte[] messageBytes = JSON.toJSONBytes(result, SerializerFeature.SkipTransientField);
                messageExt.setBody(messageBytes);

                logger.info("Message Meta Data -> [TOPIC:{}, TAGS:{}, KEYS:{}", topic, tags, keys);
                logger.info("Message Body -> {}", new String(messageBytes, StandardCharsets.UTF_8));

                // 发送消息
                producer.send(messageExt, (SendCallback) target);
            } else {
                logger.info("未找到 [GROUP: {}] 的生产者", producerAnnotation.group());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
