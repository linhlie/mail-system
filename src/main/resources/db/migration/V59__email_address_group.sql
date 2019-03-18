CREATE TABLE `email_address_group` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `group_name` VARCHAR(191) NOT NULL,
  `account_create_id` INT NOT NULL,
  UNIQUE KEY unique_group_name (group_name),
  FOREIGN KEY fk_account_create_group(account_create_id)
  REFERENCES accounts(id)
    ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;


CREATE TABLE `emails_address_in_group` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `group_id` INT NOT NULL,
  `people_in_charge_id` INT NOT NULL,
  FOREIGN KEY fk_email_group(group_id)
    REFERENCES email_address_group(id)
    ON DELETE CASCADE,
  FOREIGN KEY fk_email_group_to_people_in_charge(people_in_charge_id)
  REFERENCES people_in_charge_partner(id)
    ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;