package com.leyou.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface SkuClient extends SkuClientServer {

}
