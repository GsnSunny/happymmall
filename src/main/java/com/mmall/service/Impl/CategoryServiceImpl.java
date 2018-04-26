package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mysql.fabric.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ClanceRen on 2018/4/23.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    //新增分类
    public ServerResponse addCategory(String categoryName, Integer parentId){
        //首先对参数进行一个校验
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessagr("添加分类参数错误");
        }
        Category category = new Category();
        category.setStatus(true);
        category.setName(categoryName);
        category.setParentId(0);
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("添加分类成功");
        }
        return ServerResponse.createByErrorMessagr("添加分类失败");
    }

    //修改分类名称
    public ServerResponse setCategoryName(Integer categoryId, String categoryName){
        //首先对参数进行一个校验
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessagr("更新品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessagr("更新品类名称失败");
    }

    //查询子节点的category信息（parallel表示同级）
    public ServerResponse<List<Category>> getChildParallelCategory(Integer parentId){
        List<Category> category = categoryMapper.selectChildParallelByParentId(parentId);
        if(CollectionUtils.isEmpty(category)){
            //查找到子分类为空，若返回给前端，并不是异常，所以在后端打印一个日志，提示子分类为空
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(category);
    }

    //递归查找本节点的id以及孩子节点的id
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();//创建了一个Set集合
        findChildCategory(categorySet, categoryId);//调用递归方法之后categorySet中装入家用电器、冰箱、电视等。。

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法算出子节点
    // -->家用电器-->得到家用电器（id，状态等信息）-->将category放到set集合-->通过家用电器得到冰箱，电视等子节点
    // -->循环遍历各个子节点，并且递归调用findChildCategory方法，让每个节点作为父节点，继续查找子节点
    // -->循环结束后
    public Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //查找子节点(递归算法一定要有一个退出的条件)-->根据家用电器id查出冰箱id、电视id、洗衣机id、空调id、电热水器id等子节点
        List<Category> categoryList = categoryMapper.selectChildParallelByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }






}
