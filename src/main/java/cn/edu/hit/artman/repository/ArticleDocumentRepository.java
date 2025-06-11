package cn.edu.hit.artman.repository;

import cn.edu.hit.artman.pojo.document.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDocumentRepository extends ElasticsearchRepository<ArticleDocument, Long> {
    // ElasticsearchRepository 提供了基本的 CRUD 操作，如 save, findById, deleteById, findAll 等
    // 我们可以添加自定义查询方法，但对于复杂查询，通常会使用 ElasticsearchOperations 或 NativeQueryBuilder
}
