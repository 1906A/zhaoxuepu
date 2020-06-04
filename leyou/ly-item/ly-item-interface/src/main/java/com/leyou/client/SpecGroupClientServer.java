package com.leyou.client;

import com.leyou.pojo.SpecGroup;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("spec")
public interface SpecGroupClientServer {

    @RequestMapping("groups/{cid}")
    public List<SpecGroup> findSpecGroupList(@PathVariable("cid") Long cateGoryId);
}
