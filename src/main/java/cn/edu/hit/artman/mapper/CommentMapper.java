package cn.edu.hit.artman.mapper;

import cn.edu.hit.artman.pojo.po.Comment;
import cn.edu.hit.artman.pojo.vo.CommentInfoVO;
import cn.edu.hit.artman.pojo.vo.SubCommentInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentInfoVO> getArticleComments(Long articleId);

    List<SubCommentInfoVO> getSubComments(Long rootId);

}




