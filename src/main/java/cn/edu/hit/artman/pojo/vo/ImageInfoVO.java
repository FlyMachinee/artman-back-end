package cn.edu.hit.artman.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(title = "图片信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoVO implements Serializable {

    @Schema(title = "图片ID", example = "1")
    private Long imageId;

    @Schema(title = "图片上传者ID", example = "1")
    private Long userId;

    @Schema(title = "图片名", example = "example.jpg")
    private String name;

    @Schema(title = "图片在OSS中的URL", example = "https://example.com/image.jpg")
    private String url;

    @Schema(title = "图片上传时间", example = "2023-01-01T12:00:00")
    private LocalDateTime uploadTime;
}
