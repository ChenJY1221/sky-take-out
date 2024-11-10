package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 插入菜品口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id列表删除相关的口味信息
     * @param ids
     */
     void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id删除相关的口味信息
     * @param dishId
     */
     @Delete("delete from dish_flavor where dish_id = #{dishId}")
     void deleteByDishId(Long dishId);
     /**
     * 根据菜品id查询口味
     * @param dishId
     * @return
     */
     @Select("select * from dish_flavor where dish_id = #{dishId}")
     List<DishFlavor> getByDishId(Long dishId);
}
