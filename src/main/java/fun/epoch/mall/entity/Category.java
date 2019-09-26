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
public class Category {
    private Integer id;
    private Integer parentId;
    private String name;
    private Byte status;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}