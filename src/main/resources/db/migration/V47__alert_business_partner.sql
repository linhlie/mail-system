ALTER TABLE `business_partners`
    ADD COLUMN `alert_level` INT NULL DEFAULT 0,
    ADD COLUMN `alert_content` MEDIUMTEXT NULL ;