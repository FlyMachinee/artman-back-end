package cn.edu.hit.artman.common.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ArtManException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer code;
    private final String msg;

    public ArtManException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
