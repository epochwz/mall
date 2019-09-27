package fun.epoch.mall.vo;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class QrCodeVo {
    private Long orderNo;
    private String qrCode;
}
