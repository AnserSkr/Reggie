package com.reggie.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.Result;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.entity.SetmealDishDto;
import com.reggie.service.DishService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealDtoService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 98248
 * @Date: 2022/9/27 - 09 - 27 - 17:56
 * @Description: com.reggie.web
 * @version: 1.0
 * 套餐setmeal的控制器
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDtoService setmealDtoService;

    @Autowired
    DishService dishService;

    @Autowired
    SetmealDishService setmealDishService;
    /**
     * 根据接收参数，分页查询setmeal并获取categoryName放入setmealDto返回
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> selectAllAndPagition(int page,int pageSize,String name){
        // 查询setmeal分页数据
        Page<Setmeal> setmealPage = setmealService.selectAllAndPagition(page, pageSize, name);

        //将setmeal的categoryName查询出来，并保存到setmealDto。封装到分页对象中
        Page<SetmealDto> setmealDtoPage = setmealDtoService.transforSetmealDtoPage(setmealPage);

        return Result.success(setmealDtoPage);

    }

    /**
     * 根据SetmealDto插入setmeal。同时根据得到的setmealId更新setmealDish
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        boolean b = setmealService.addSetmeal(setmealDto);
        if(b){
            return Result.success("套餐插入成功");
        }
        return Result.error("套餐插入失败");
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmeal(@PathVariable Long id){
        //根据ID获取到setmeal对象
        Setmeal setmeal = setmealService.getById(id);
        //根据setmeal的数据获取到setmealDish封装到setmealDto中
        SetmealDto setmealDto = setmealDtoService.getDtoBySetmeal(setmeal);
        if(setmealDto!=null){
            return Result.success(setmealDto);
        }
        return Result.error("数据查询错误，请检查");
    }

    /**
     * 根据传入的setmealDish数据更新setmeal
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
         boolean b = setmealService.updateSetmeal(setmealDto);
        if(b){
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 根据setmealIDs删除所有setmeal，以及setmealID对应的所有setmealDish
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteSetmeal(String ids){
        ArrayList<Long> setmealIds = parseStrsToLongList(ids);
        boolean b = setmealService.deleteBatchByIds(setmealIds);
        if(b){
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 将String数组转换为List<Long>
     * @param ids
     * @return
     */
    private ArrayList<Long> parseStrsToLongList(String ids) {
        String[] split = ids.split(",");
        ArrayList<Long> idss = new ArrayList<>();
        for (String s : split) {
            Long dishId = Long.valueOf(s);
            idss.add(dishId);
        }
        return idss;
    }

    /**
     * 根据setmealIds修改setmeal的状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,String ids){
        ArrayList<Long> setmealIds = parseStrsToLongList(ids);
        boolean b = setmealService.updateStatusByIds(setmealIds, status);
        if(b){
            return Result.success("状态修改成功");
        }
        return Result.error("状态修改失败");
    }

    /**
     * 根据categoryId获取到对应的套餐分类下的套餐信息
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    public Result<List<SetmealDto>> getAllsetmeal(String categoryId,Integer status) {
        // 根基catgoryId查询到所有的setmeal信息
        Long categoryID = Long.valueOf(categoryId);
        List<Setmeal> setmealList = setmealService.getAllByCategoryId(categoryID);
        // 根据查询到的setmeal信息获取setmeal套餐内的的所有菜品信息
        List<SetmealDto> setmealDtos = setmealList.stream().map(setmeal -> {
            SetmealDto setmealDto = setmealDtoService.getDtoBySetmeal(setmeal);
            return setmealDto;
        }).collect(Collectors.toList());
        if (setmealList != null) {
            return Result.success(setmealDtos);
        }
        return Result.error("查询失败");
    }

    /**
     * 此处的id为setmealId,用于查询setmeal对应的所有setmealDIsh
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public Result<List<SetmealDishDto>> getDishBySetmealId(@PathVariable Long id){
        //根据setmealId查询到所有的setmealDish
        List<SetmealDishDto> setmealDishDtoList = setmealDishService.getAllBySetmealId(id);
        if(setmealDishDtoList!=null){
            return Result.success(setmealDishDtoList);
        }
        return Result.error("查询失败");
    }
}
