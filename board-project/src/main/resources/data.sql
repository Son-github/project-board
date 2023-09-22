insert into user_account (user_id, user_password, nickname, email, memo, created_by, created_at, modified_by, modified_at) values
                                                                                                                               ('son', 'aaaa', 'Son', 'son@gmail.com', 'i am son', 'son', '2022-03-01', 'son', '2022-04-01');

insert into article (user_account_id, title, content, hashtag, created_by, created_at, modified_at, modified_by) values
                                                                                                                     (1, 'title', 'content', 'hashtag', 'son', now(), now(), 'son');


