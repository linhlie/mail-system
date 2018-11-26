ALTER TABLE `engineers`
CHANGE COLUMN `name` `last_name` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `kana_name` `kana_last_name` VARCHAR(255) NOT NULL ,
ADD COLUMN `first_name` VARCHAR(255) NOT NULL AFTER `last_name`,
ADD COLUMN `kana_first_name` VARCHAR(255) NOT NULL AFTER `kana_last_name`;
