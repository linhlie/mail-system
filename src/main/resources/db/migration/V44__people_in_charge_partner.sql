CREATE TABLE `business_partners` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `last_name` VARCHAR(191) NOT NULL,
  `first_name` VARCHAR(191) NOT NULL,
  `department` VARCHAR(191) NOT NULL,
  `position` VARCHAR(191) NOT NULL,
  `email_address` VARCHAR(191) NOT NULL,
  `email_in_charge_partner` BOOLEAN DEFAULT FALSE,
  `number_phone1` VARCHAR(191) NOT NULL,
  `number_phone2` VARCHAR(191) DEFAULT NULL,
  `note` TEXT DEFAULT NULL,
  `partner_id` INT NOT NULL,
  UNIQUE KEY unique_email_address (email_address),
  FOREIGN KEY fk_partner(partner_id)
  REFERENCES business_partners(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;