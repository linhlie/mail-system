SET NAMES utf8 COLLATE utf8_unicode_ci;
SET FOREIGN_KEY_CHECKS=0;

INSERT INTO `email_accounts` (`id`, `account`, `disabled`)
VALUES
	(7, 'section-sales@world-link-system.com', 0);

INSERT INTO `email_account_settings` (`id`, `account_id`, `user_name`, `password`, `mail_server_address`, `mail_server_port`, `mail_protocol`, `encryption_protocol`, `authentication_protocol`, `proxy_server`, `created_at`, `updated_at`, `type`)
VALUES
	(6, 7, 'section-sales@world-link-system.com', 'WlSecSa95953', 'af125.secure.ne.jp', 993, 0, 1, 0, '', NULL, '2018-04-18 08:43:36', 0),
	(7, 7, 'section-sales@world-link-system.com', 'WlSecSa95953', 'af125.secure.ne.jp', 465, 2, 1, 0, '', NULL, '2018-04-18 08:43:36', 1);

INSERT INTO replace_numbers(`character`, replace_value)
VALUES ('K', 1000);
INSERT INTO replace_numbers(`character`, replace_value)
VALUES ('千', 1000);
INSERT INTO replace_numbers(`character`, replace_value)
VALUES ('万', 10000);

INSERT INTO replace_units(unit, replace_unit)
VALUES ('円', '円');
INSERT INTO replace_units(unit, replace_unit)
VALUES ('YEN', '円');

INSERT INTO `number_treatments` (`name`, `upper_limit_name`, `upper_limit_sign`, `upper_limit_rate`, `lower_limit_name`, `lower_limit_sign`, `lower_limit_rate`, `left_boundary_value`, `left_boundary_operator`, `combine_operator`, `right_boundary_value`, `right_boundary_operator`, `enable_replace_letter`)
VALUES
	('金額', '上代数値', 0, 1.2, '下代数値', 0, 0.8, 300000, 0, 0, 900000, 1, 1);

INSERT INTO `replace_letters` (`letter`, `position`, `replace`, `hidden`)
VALUES
  ('~', 0, 1, 0),
  ('~', 1, 0, 0),
	('〜', 0, 1, 0),
	('〜', 1, 0, 0);

