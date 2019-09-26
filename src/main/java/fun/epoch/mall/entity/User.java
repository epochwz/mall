package fun.epoch.mall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String question;
    private String answer;
    private Integer role;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}