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

    int selectCountByUsername(String username);

    int selectCountByEmail(String email);

    int selectCountByMobile(String mobile);

    User selectByUsernameAndPassword(String username, String password);

    User selectByEmailAndPassword(String email, String password);

    User selectByMobileAndPassword(String mobile, String password);

    int selectCountByUsernameExceptCurrentUser(int userId, String username);

    int selectCountByEmailExceptCurrentUser(int userId, String email);

    int selectCountByMobileExceptCurrentUser(int userId, String mobile);

    int updatePasswordByOldPassword(int userId, String oldPassword, String newPassword);

    String selectQuestionByUsername(String username);

    int selectCountByUsernameAndQuestionAndAnswer(String username, String question, String answer);

    int updatePasswordByUsername(String username, String password);
}