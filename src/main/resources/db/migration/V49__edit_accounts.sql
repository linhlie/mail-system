ALTER TABLE `accounts`
CHANGE COLUMN `name` `last_name` VARCHAR(50) CHARACTER SET 'utf8' NULL DEFAULT NULL ,
ADD COLUMN `first_name` VARCHAR(50) NULL DEFAULT NULL AFTER `last_name`;
