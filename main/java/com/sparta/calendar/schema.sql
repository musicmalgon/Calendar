create table user
(
    user_id  bigint          not null auto_increment
        primary key,
    password varchar(255) not null,
    username varchar(100) null
);

create table calendar
(
    calendar_id bigint not null auto_increment
        primary key,
    title       varchar(255) null,
    created_at  varchar(255) null,
    updated_at  varchar(255) null,
    todo        varchar(255) null,
    user_id     bigint          not null,
    foreign key (user_id) references user (user_id)
);