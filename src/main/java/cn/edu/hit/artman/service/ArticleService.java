package cn.edu.hit.artman.service;

import cn.edu.hit.artman.pojo.dto.ArticleCreateDTO;
import cn.edu.hit.artman.pojo.dto.ArticleSimpleSearchDTO;
import cn.edu.hit.artman.pojo.dto.ArticleUpdateDTO;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;

public interface ArticleService extends IService<Article> {

    Long createArticleWithMetaInfo(Long userId, ArticleCreateDTO articleCreateDTO);

    PageInfo<ArticleInfoVO> getAllArticlesByUserId(Long userId, Integer pageNum, Integer pageSize);

    PageInfo<ArticleInfoVO> getSimpleSearchArticlesByUserId(
        Long userId, Integer pageNum, Integer pageSize, ArticleSimpleSearchDTO articleSimpleSearchDTO);

    ArticleInfoVO getArticleInfoById(Long articleId);

    boolean updateArticleWithMetaInfo(Long userId, Long articleId, ArticleUpdateDTO articleUpdateDTO);

    boolean deleteArticle(Long userId, Long articleId);

    PageInfo<ArticleInfoVO> getSimpleSearchSharedArticles(
        Integer pageNum, Integer pageSize, LocalDateTime startDate, LocalDateTime endDate);
}
