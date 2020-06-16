package com.leyou.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service")
public interface UserClient extends UserClientServer{

}
