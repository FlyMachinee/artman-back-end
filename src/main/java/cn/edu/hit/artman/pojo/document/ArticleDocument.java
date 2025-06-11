package cn.edu.hit.artman.pojo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Data
@Document(indexName = "articles") // 对应Elasticsearch中的索引名
@Setting(shards = 1, replicas = 0) // 索引设置，可根据实际情况调整
public class ArticleDocument {

    @Id
    private Long id; // 对应数据库中的 articleId

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart") // 中文分词器
    private String username; // 用户名，便于搜索和展示

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart") // 中文分词器
    private String nickname; // 用户昵称，便于展示

    @Field(type = FieldType.Long)
    private Long categoryId; // 用于分类筛选

    @Field(type = FieldType.Keyword) // Keyword类型不分词，用于精确匹配
    private String status; // draft 或 published

    @Field(type = FieldType.Boolean)
    private Boolean isShared; // 是否共享

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart") // 中文分词器
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart") // 中文分词器
    private String summary;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart") // 中文分词器
    private String content; // 文章内容

    @Field(type = FieldType.Keyword) // 存储文章内容的URL，便于快速访问
    private String contentUrl; // 文章内容的URL，存储在对象存储中

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second) // 日期类型，支持精确到秒
    private LocalDateTime createTime; // 文章创建时间

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second) // 日期类型，支持精确到秒
    private LocalDateTime updateTime; // 文章更新时间
}
