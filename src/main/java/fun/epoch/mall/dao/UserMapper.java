package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserMapper {
    String selectAll = "select id, username, password, email, mobile, question, answer, role, create_time, update_time from user ";

    @Select(selectAll)
    List<User> selectAll();

    @Select(selectAll + "where id = #{id}")
    User selectByPrimaryKey(int id);

    @Delete("delete from user where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into user <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(User user);

    @Update("update user <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(User user);

    @Update("update user <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(User user);

    @Select("select count(1) from user where username=#{username}")
    int selectCountByUsername(String username);

    @Select("select count(1) from user where mobile=#{mobile}")
    int selectCountByMobile(String mobile);

    @Select("select count(1) from user where email=#{email}")
    int selectCountByEmail(String email);

    @Select(selectAll + "where username=#{username} and password=#{password}")
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Select(selectAll + "where email=#{email} and password=#{password}")
    User selectByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Select(selectAll + "where mobile=#{mobile} and password=#{password}")
    User selectByMobileAndPassword(@Param("mobile") String mobile, @Param("password") String password);

    @Select("select count(1) from user where username=#{username} and id!=#{id} ")
    int selectCountByUsernameExceptCurrentUser(@Param("id") int id, @Param("username") String username);

    @Select("select count(1) from user where email=#{email} and id!=#{id} ")
    int selectCountByEmailExceptCurrentUser(@Param("id") int id, @Param("email") String email);

    @Select("select count(1) from user where mobile=#{mobile} and id!=#{id} ")
    int selectCountByMobileExceptCurrentUser(@Param("id") int id, @Param("mobile") String mobile);

    @Update("update user set password=#{newPass} where id=#{id} and password=#{oldPass}")
    int updatePasswordByOldPassword(@Param("id") int id, @Param("oldPass") String oldPass, @Param("newPass") String newPass);

    @Select("select question from user where username=#{username}")
    String selectQuestionByUsername(String username);

    @Select("select count(1) from user where username=#{username} and question=#{question} and answer=#{answer}")
    int selectCountByUsernameAndQuestionAndAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    @Update("update user set password=#{password} where username=#{username}")
    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);
}