package fun.epoch.mall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    private Integer status;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;

    private List<Category> categories;
}