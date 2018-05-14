ALTER TABLE fuzzy_words
ADD CONSTRAINT uc_fuzzy_word UNIQUE KEY (word_id,with_word_id);

ALTER TABLE email_word_jobs
ADD CONSTRAINT uc_email_word_job UNIQUE KEY (message_id,word_id);

ALTER TABLE emails_words
ADD CONSTRAINT uc_email_word UNIQUE KEY (message_id,word_id);