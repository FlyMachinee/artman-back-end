package cn.edu.hit.artman.common.enumeration;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// 将字符串转换为 ArticleStatus 枚举的转换器
@Component // 标记为Spring组件，以便自动扫描和注册
public class ArticleStatusConverter implements Converter<String, ArticleStatus> {

    @Override
    @Nonnull
    public ArticleStatus convert(@Nonnull String source) {
        return ArticleStatus.fromValue(source);
    }
}
