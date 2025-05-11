package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.mapper.ArticleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

}




