package com.leyou.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "item-service")
public interface CategoryClient extends CategoryClientServer{
}
