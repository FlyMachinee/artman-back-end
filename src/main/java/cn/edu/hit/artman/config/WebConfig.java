package cn.edu.hit.artman.config;

import cn.edu.hit.artman.common.enumeration.ArticleStatusConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 确保 ArticleStatusConverter 已经是一个 Spring Bean
    private final ArticleStatusConverter articleStatusConverter;

    public WebConfig(ArticleStatusConverter articleStatusConverter) {
        this.articleStatusConverter = articleStatusConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(articleStatusConverter);
    }
}
