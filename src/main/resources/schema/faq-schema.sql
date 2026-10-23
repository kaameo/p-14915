create table if not exists faq
(
    id       SERIAL primary key,
    question varchar(500)  not null,
    answer   varchar(2000) not null
);

create extension if not exists pgroonga;