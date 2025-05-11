package cn.edu.hit.artman.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片信息表
 */
@TableName(value ="image")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Serializable {
    /**
     * 图片ID
     */
    @TableId(value = "image_id", type = IdType.AUTO)
    private Long imageId;

    /**
     * 图片上传者
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 图片名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 图片在OSS中的URL
     */
    @TableField(value = "url")
    private String url;

    /**
     * 图片上传时间
     */
    @TableField(value = "upload_time")
    private LocalDateTime uploadTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}