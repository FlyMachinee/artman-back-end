package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.mapper.CommentMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Comment;
import cn.edu.hit.artman.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




