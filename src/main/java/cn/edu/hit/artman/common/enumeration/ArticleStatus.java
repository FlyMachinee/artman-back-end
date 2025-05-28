package cn.edu.hit.artman.common.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ArticleStatus {
    DRAFT("draft"),
    PUBLISHED("published");

    @EnumValue
    @JsonValue
    private final String value;

    private static final Map<String, ArticleStatus> VALUE_MAP = new HashMap<>();

    static {
        for (ArticleStatus status : ArticleStatus.values()) {
            VALUE_MAP.put(status.value, status);
        }
    }

    @JsonCreator
    public static ArticleStatus fromValue(String value) {
        return VALUE_MAP.get(value);
    }
}
