 ALTER TABLE `people_in_charge_partner`
    ADD COLUMN `alert_level` INT NULL DEFAULT 0,
    ADD COLUMN `alert_content` MEDIUMTEXT NULL ;