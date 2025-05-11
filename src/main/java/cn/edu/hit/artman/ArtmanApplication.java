package cn.edu.hit.artman;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.hit.artman.mapper")
public class ArtmanApplication {
	public static void main(String[] args) {
		SpringApplication.run(ArtmanApplication.class, args);
	}
}
