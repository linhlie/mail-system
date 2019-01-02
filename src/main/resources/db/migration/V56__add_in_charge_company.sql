ALTER TABLE `email_accounts`
ADD COLUMN `in_charge_company` TEXT NULL DEFAULT NULL AFTER `signature`;
