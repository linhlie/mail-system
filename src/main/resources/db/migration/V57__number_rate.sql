ALTER TABLE `number_treatments`
CHANGE COLUMN `upper_limit_name` `upper_limit_name` VARCHAR(191) CHARACTER SET 'utf8' NULL ,
CHANGE COLUMN `upper_limit_sign` `upper_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x' ,
CHANGE COLUMN `upper_limit_rate` `upper_limit_rate` DOUBLE NOT NULL DEFAULT 1.0 ,
CHANGE COLUMN `lower_limit_name` `lower_limit_name` VARCHAR(191) CHARACTER SET 'utf8' NULL ,
CHANGE COLUMN `lower_limit_sign` `lower_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x' ,
CHANGE COLUMN `lower_limit_rate` `lower_limit_rate` DOUBLE NOT NULL DEFAULT 1.0 ;
