package cn.edu.hit.artman.common.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleStatus {
    DRAFT("0", "草稿"),
    PUBLISHED("1", "已发布");

    @EnumValue
    private final String value;

    @JsonValue
    private final String description;
}
