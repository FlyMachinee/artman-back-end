package cn.edu.hit.artman.service;

import cn.edu.hit.artman.pojo.po.Comment;
import cn.edu.hit.artman.pojo.vo.CommentInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentService extends IService<Comment> {

    Long createComment(Comment comment);

    List<CommentInfoVO> getArticleComments(Long articleId);

    boolean deleteComment(Long ArticleId, Long commentId, Long userId);

}
