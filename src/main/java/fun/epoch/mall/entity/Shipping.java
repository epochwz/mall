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
public class Shipping {
    private Integer id;
    private Integer userId;
    private String name;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String address;
    private String zip;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}