package com.leyou.listener;

import com.leyou.service.GoodService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @Autowired
    GoodService goodService;

    @RabbitListener(bindings = @QueueBinding(
            value =@Queue(name = "item.edit.web.queue",durable = "true"),
            exchange =@Exchange(name = "item.exchanges",ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert","item.update"}
    ))
    public void editThymeleafData(Long spuId) throws Exception {
        System.out.println("开始监听修改Thymeleaf数据，spuId="+spuId);

        if(spuId==null){
            return;
        }
        goodService.creatHtml(spuId);

        System.out.println("结束监听修改Thymeleaf数据，spuId="+spuId+"，修改成功");

    }

    @RabbitListener(bindings = @QueueBinding(
            value =@Queue(name = "item.delete.web.queue",durable = "true"),
            exchange =@Exchange(name = "item.exchanges",ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.delete"}
    ))
    public void deleteThymeleafData(Long spuId) throws Exception {
        System.out.println("开始监听删除Thymeleaf数据，spuId="+spuId);

        if(spuId==null){
            return;
        }
        goodService.deleteHtml(spuId);

        System.out.println("结束监听删除Thymeleaf数据，spuId="+spuId+"，删除成功");

    }
}
