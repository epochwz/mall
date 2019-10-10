package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CategoryMapper {
    String selectAll = "select id, parent_id, name, status, create_time, update_time from category ";

    @Select(selectAll)
    List<Category> selectAll();

    @Select(selectAll + "where id = #{id}")
    Category selectByPrimaryKey(int id);

    @Delete("delete from category where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into category <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Category category);

    @Update("update category <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(Category category);

    @Update("update category <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(Category category);

    int selectCountByPrimaryKey(int id);

    int selectCountByParentIdAndCategoryName(int parentId, String categoryName);

    int selectCountByParentIdAndCategoryNameExceptCurrentId(int parentId, String categoryName, int categoryId);

    int updateStatusByPrimaryKey(List<Integer> ids, int status);
}