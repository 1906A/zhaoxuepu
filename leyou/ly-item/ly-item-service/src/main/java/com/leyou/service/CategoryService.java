package com.leyou.service;

import com.leyou.dao.CategoryMapper;
import com.leyou.pojo.Category;
import com.leyou.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 根据节点id查询所有分类信息
     * @param category
     * @return
     */
    public List<Category> findCategory(Category category){
        return categoryMapper.select(category);
    }


    /**
     * 测试
     * @param id
     * @return
     */
    public Category findCate(int id){
        return categoryMapper.findCate(id);
    }

    /**
     * 添加商品分类
     *
     * @param category
     */
    public void cateGoryAdd(Category category) {
        categoryMapper.insertSelective(category);
    }

    /**
     * 修改商品分类
     *
     * @param category
     */
    public void cateGoryUpdate(Category category) {
        categoryMapper.updateByPrimaryKey(category);
    }

    /**
     * 根据节点id进行删除
     * @param id
     */
    public void deleteById(Long id) {
        Category category=new Category();
        category.setId(id);
        categoryMapper.deleteByPrimaryKey(category);
    }

    /**
     * 根据分类id查询分类名称
     *
     * @param id
     * @return
     */
    public Category findCategoryById(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }


    /**
     *
     * 根据分类id查询分类名称
     *
     * @param ids
     * @return
     */
    public List<Category> findCategoryByCids(List<Long> ids) {
        List<Category> categoryList =new ArrayList<>();
        ids.forEach(cid ->{
            categoryList.add(categoryMapper.selectByPrimaryKey(cid));
        });
        return categoryList;
    }
}
