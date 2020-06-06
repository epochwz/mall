package fun.epoch.mall.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageUtils {
    public static <IN, OUT> PageInfo<OUT> convert(PageInfo<IN> pageInfoPo, List<OUT> list) {
        Page<OUT> page = new Page<>(pageInfoPo.getPageNum(), pageInfoPo.getPageSize());
        page.setTotal(pageInfoPo.getTotal());
        page.addAll(list);
        return new PageInfo<>(page);
    }
}
