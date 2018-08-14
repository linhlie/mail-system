CREATE TABLE `business_partner_groups` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `partner_id` INT NOT NULL,
  `with_partner_id` INT NOT NULL,
  FOREIGN KEY fk_partner(partner_id)
  REFERENCES business_partners(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_with_partner(with_partner_id)
  REFERENCES business_partners(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;