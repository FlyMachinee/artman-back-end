package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.mapper.ArticleMapper;
import cn.edu.hit.artman.mapper.CommentMapper;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.pojo.vo.CommentInfoVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Comment;
import cn.edu.hit.artman.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.net.HttpURLConnection.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;

    @Override
    public Long createComment(Comment comment) {

        // 检查文章是否存在
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "文章不存在");
        }

        // 检查文章是否允许评论
        if (!article.getIsShared()) {
            throw new ArtManException(HTTP_FORBIDDEN, "文章非共享，不允许评论");
        }

        // 检查文章是否为草稿
        if (article.getStatus() == ArticleStatus.DRAFT) {
            throw new ArtManException(HTTP_FORBIDDEN, "文章为草稿，不允许评论");
        }

        // 若rootId不为null，则检查根评论是否存在，且应该为一级评论
        if (comment.getRootId() != null) {
            Comment rootComment = commentMapper.selectById(comment.getRootId());
            if (rootComment == null) {
                throw new ArtManException(HTTP_NOT_FOUND, "根评论不存在");
            }
            if (!rootComment.getArticleId().equals(comment.getArticleId())) {
                throw new ArtManException(HTTP_BAD_REQUEST, "根评论不属于该文章");
            }
            if (rootComment.getRootId() != null) {
                throw new ArtManException(HTTP_BAD_REQUEST, "根评论应是一级评论");
            }

            // 当rootId不为null时，才可能有replyId
            // 若replyId不为null，则检查回复的评论是否存在，且应该为二级评论
            if (comment.getReplyId() != null) {
                Comment replyComment = commentMapper.selectById(comment.getReplyId());
                if (replyComment == null) {
                    throw new ArtManException(HTTP_NOT_FOUND, "回复的二级评论不存在");
                }
                if (!replyComment.getArticleId().equals(comment.getArticleId())) {
                    throw new ArtManException(HTTP_BAD_REQUEST, "回复的评论不属于该文章");
                }
                if (replyComment.getRootId() == null) {
                    throw new ArtManException(HTTP_BAD_REQUEST, "回复的评论应是二级评论");
                }
            }
        } else {
            // 如果没有指定根评论，表示这是一级评论，其replyId和rootId都应为null
            if (comment.getReplyId() != null) {
                throw new ArtManException(HTTP_BAD_REQUEST, "一级评论不应有回复ID");
            }
        }


        // 插入评论
        if (commentMapper.insert(comment) <= 0) {
            throw new ArtManException(HTTP_INTERNAL_ERROR, "评论创建失败，内部数据库错误");
        }

        return comment.getCommentId();
    }

    @Override
    public List<CommentInfoVO> getArticleComments(Long articleId) {

        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "文章不存在");
        }

        // 检查文章是否共享
        if (!article.getIsShared()) {
            throw new ArtManException(HTTP_FORBIDDEN, "文章非共享，不允许获取评论");
        }

        // 检查文章是否为草稿
        if (article.getStatus() == ArticleStatus.DRAFT) {
            throw new ArtManException(HTTP_FORBIDDEN, "文章为草稿，不允许获取评论");
        }

        // 获取文章评论列表
        return commentMapper.getArticleComments(articleId);
    }

    @Override
    public boolean deleteComment(Long ArticleId, Long commentId, Long userId) {

        // 检查文章是否存在
        Article article = articleMapper.selectById(ArticleId);
        if (article == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "文章不存在");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "评论不存在");
        }

        // 检查评论是否属于该文章
        if (!comment.getArticleId().equals(ArticleId)) {
            throw new ArtManException(HTTP_BAD_REQUEST, "评论不属于该文章");
        }

        // 检查用户是否有权限删除评论
        if (!comment.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权删除他人评论");
        }

        // 删除评论
        return commentMapper.deleteById(commentId) > 0;
    }
}




