package cn.edu.hit.artman;

import cn.edu.hit.artman.service.ElasticSearchArticleService;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@MapperScan("cn.edu.hit.artman.mapper")
public class ArtmanApplication implements CommandLineRunner {

    private final ElasticSearchArticleService elasticSearchArticleService;

    public static void main(String[] args) {
        SpringApplication.run(ArtmanApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 在应用启动后执行全量同步
        elasticSearchArticleService.syncAllArticlesFromDatabase();
    }
}
