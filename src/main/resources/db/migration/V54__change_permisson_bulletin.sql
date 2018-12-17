ALTER TABLE `bulletin_permission`
ADD COLUMN `can_change_permission` TINYINT(1) NULL DEFAULT '0' AFTER `can_delete`;
