package com.leyou.listener;

import com.leyou.service.GoodService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingType;

@Component
public class MessageListener {

    @Autowired
    GoodService goodService;

    @RabbitListener(bindings = @QueueBinding(
            value =@Queue(name = "item.edit.search.queue",durable = "true"),
            exchange =@Exchange(name = "item.exchanges",ignoreDeclarationExceptions = "true",
                              type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert","item.update"}
    ))
    public void editEsData(Long spuId) throws Exception {
        System.out.println("开始监听修改ES数据，spuId="+spuId);

        if(spuId==null){
            return;
        }

        goodService.editEsData(spuId);

        System.out.println("结束监听修改ES数据，spuId="+spuId+"，修改成功");

    }


    @RabbitListener(bindings = @QueueBinding(
            value =@Queue(name = "item.delete.search.queue",durable = "true"),
            exchange =@Exchange(name = "item.exchanges",ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.delete"}
    ))
    public void deleteEsData(Long spuId) throws Exception {
        System.out.println("开始监听删除ES数据，spuId="+spuId);

        if(spuId==null){
            return;
        }

        goodService.deleteEsData(spuId);

        System.out.println("结束监听删除ES数据，spuId="+spuId+"，删除成功");

    }
}
