USE `artman`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `image`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `article`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `article`  (
  `article_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '文章作者ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '文章所属分类ID，NULL为无分类',
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `summary` varchar(1024) NOT NULL DEFAULT '' COMMENT '文章摘要',
  `content_url` varchar(2083) NOT NULL DEFAULT '' COMMENT '文章URL',
  `is_shared` tinyint(1) NOT NULL DEFAULT 0 COMMENT '文章是否共享',
  `status` enum('draft','published') NOT NULL DEFAULT 'draft' COMMENT '文章状态：草稿、发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文章创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '文章更新时间',
  PRIMARY KEY (`article_id`),
  INDEX `article_index`(`user_id`, `category_id`, `is_shared`, `status`, `create_time`, `update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `category`  (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `user_id` bigint NOT NULL COMMENT '分类所属用户',
  `name` varchar(255) NOT NULL COMMENT '分类名',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父分类ID，NULL为顶级分类',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分类创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '分类更新时间',
  PRIMARY KEY (`category_id`),
  INDEX `category_index`(`user_id`, `parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `comment`  (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `article_id` bigint NOT NULL COMMENT '评论所属文章ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '评论用户ID，NULL表示该用户已注销',
  `content` varchar(512) NOT NULL COMMENT '评论内容',
  `reply_id` bigint NULL DEFAULT NULL COMMENT '回复评论的ID，NULL表示该评论没有回复某个评论（即：1级评论或回复1级评论的评论）',
  `root_id` bigint NULL DEFAULT NULL COMMENT '评论所属1级评论的ID，NULL表示该评论为1级评论',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论创建时间',
  PRIMARY KEY (`comment_id`),
  INDEX `comment_index`(`article_id`, `user_id`, `reply_id`, `root_id`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `image`  (
  `image_id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `user_id` bigint NOT NULL COMMENT '图片上传者',
  `name` varchar(255) NOT NULL COMMENT '图片名',
  `url` varchar(2083) NOT NULL COMMENT '图片在OSS中的URL',
  `upload_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '图片上传时间',
  PRIMARY KEY (`image_id`),
  INDEX `image_index`(`user_id`, `name`, `upload_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) NOT NULL COMMENT '用户名，非空唯一',
  `email` varchar(255) NOT NULL COMMENT '用户邮箱，非空唯一',
  `password_hash` varchar(255) NOT NULL COMMENT '用户密码哈希值',
  `nickname` varchar(255) NOT NULL DEFAULT '' COMMENT '用户昵称',
  `avatar` varchar(2083) NOT NULL DEFAULT '' COMMENT '用户头像URL',
  `profile` varchar(512) NOT NULL DEFAULT '' COMMENT '用户个人简介',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_username_uindex`(`username`) USING BTREE,
  UNIQUE INDEX `user_email_uindex`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

ALTER TABLE `article` ADD CONSTRAINT `article_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
ALTER TABLE `article` ADD CONSTRAINT `article_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL;
ALTER TABLE `category` ADD CONSTRAINT `category_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
ALTER TABLE `category` ADD CONSTRAINT `category_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE;
ALTER TABLE `comment` ADD CONSTRAINT `comment_article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`) ON DELETE CASCADE;
ALTER TABLE `comment` ADD CONSTRAINT `comment_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL;
ALTER TABLE `comment` ADD CONSTRAINT `comment_reply_id` FOREIGN KEY (`reply_id`) REFERENCES `comment` (`comment_id`) ON DELETE SET NULL;
ALTER TABLE `comment` ADD CONSTRAINT `comment_root_id` FOREIGN KEY (`root_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE;
ALTER TABLE `image` ADD CONSTRAINT `image_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

