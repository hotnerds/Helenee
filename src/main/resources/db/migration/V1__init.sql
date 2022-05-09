create table comment (
    id bigint not null auto_increment,
    created_at datetime(6),
    last_modified_at datetime(6),
    content varchar(255),
    post_id bigint,
    user_id bigint,
    primary key (id)
);

create table diet (
   id bigint not null auto_increment,
    created_at datetime(6),
    last_modified_at datetime(6),
    meal_date date not null,
    meal_time varchar(255) not null,
    user_id bigint,
    primary key (id)
);

create table diet_food (
   id bigint not null auto_increment,
    amount bigint,
    diet_id bigint,
    food_id bigint,
    primary key (id)
);

create table follow (
   id bigint not null auto_increment,
    followed_user_id bigint,
    follower_user_id bigint,
    primary key (id)
);

create table food (
   id bigint not null auto_increment,
    food_name varchar(255),
    calories double precision,
    carbs double precision,
    fat double precision,
    protein double precision,
    primary key (id)
);

create table goal (
   id bigint not null auto_increment,
    created_at datetime(6),
    last_modified_at datetime(6),
    calories double precision,
    carbs double precision,
    date date,
    fat double precision,
    protein double precision,
    user_id bigint,
    primary key (id)
);

create table likes (
   id bigint not null auto_increment,
    post_id bigint,
    user_id bigint,
    primary key (id)
);

create table post (
   id bigint not null auto_increment,
    created_at datetime(6),
    last_modified_at datetime(6),
    content varchar(255) not null,
    title varchar(50) not null,
    user_id bigint not null,
    primary key (id)
);

create table post_tag (
   id bigint not null auto_increment,
    post_id bigint not null,
    tag_id bigint not null,
    primary key (id)
);

create table tag (
   id bigint not null auto_increment,
    name varchar(15) not null,
    primary key (id)
);

create table user (
   id bigint not null auto_increment,
    created_at datetime(6),
    last_modified_at datetime(6),
    email varchar(20) not null,
    provider varchar(255),
    role varchar(255),
    username varchar(20) not null,
    primary key (id)
);

alter table diet
   add constraint diet_unique_fields unique (meal_date, meal_time, user_id);

alter table diet_food
   add constraint diet_food_unique_fields unique (diet_id, food_id);

alter table food
   add constraint food_unique_field unique (food_name);

alter table user
   add constraint user_email_unique_field unique (email);

alter table user
   add constraint user_username_unique_field unique (username);

alter table comment
   add constraint pk_comment_post
   foreign key (post_id)
   references post (id);

alter table comment
   add constraint pk_comment_user
   foreign key (user_id)
   references user (id);

alter table diet
   add constraint pk_diet_user
   foreign key (user_id)
   references user (id);

alter table diet_food
   add constraint pk_diet_food_diet
   foreign key (diet_id)
   references diet (id);

alter table diet_food
   add constraint pk_diet_food_food
   foreign key (food_id)
   references food (id);

alter table follow
   add constraint pk_followed_user
   foreign key (followed_user_id)
   references user (id);

alter table follow
   add constraint pk_follower_user
   foreign key (follower_user_id)
   references user (id);

alter table goal
   add constraint pk_goal_user
   foreign key (user_id)
   references user (id);

alter table likes
   add constraint pk_likes_post
   foreign key (post_id)
   references post (id);

alter table likes
   add constraint pk_like_user
   foreign key (user_id)
   references user (id);

alter table post
   add constraint pk_post_user
   foreign key (user_id)
   references user (id);

alter table post_tag
   add constraint pk_post_tag_post
   foreign key (post_id)
   references post (id);

alter table post_tag
   add constraint pk_post_tag_tag
   foreign key (tag_id)
   references tag (id);
