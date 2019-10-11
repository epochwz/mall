package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.ForeachLangDriver;
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

    @Select("select count(1) from category where id=#{id}")
    int selectCountByPrimaryKey(int id);

    @Select("select count(1) from category where parent_id=#{parentId} and name=#{categoryName}")
    int selectCountByParentIdAndCategoryName(@Param("parentId") int parentId, @Param("categoryName") String categoryName);

    @Select("select count(1) from category where parent_id=#{parentId} and name=#{categoryName} and id!=#{id}")
    int selectCountByParentIdAndCategoryNameExceptCurrentId(@Param("parentId") int parentId, @Param("categoryName") String categoryName, @Param("id") int id);

    @Update("update category set status=#{status} where id in <list>")
    @Lang(ForeachLangDriver.class)
    int updateStatusByPrimaryKey(@Param("list") List<Integer> ids, @Param("status") int status);

    @Select(selectAll + "where parent_id=#{id}")
    List<Category> selectByParentId(int id);
}