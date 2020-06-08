package com.leyou.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxExpressionContext;

@FeignClient(name = "item-service")
public interface SpecParamClient extends SpecParamClientServer {

}
