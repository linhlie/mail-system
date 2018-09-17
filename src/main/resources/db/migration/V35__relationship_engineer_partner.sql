ALTER TABLE `engineers`
   ADD COLUMN  `skill_sheet` VARCHAR(255) DEFAULT NULL;

CREATE TABLE `relationship_engineer_partner` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `engineer_id` INT NOT NULL,
  `partner_id` INT NOT NULL,
  FOREIGN KEY fk_engineer_relationship(engineer_id)
  REFERENCES engineers(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_partner_relationship(partner_id)
  REFERENCES business_partners(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
