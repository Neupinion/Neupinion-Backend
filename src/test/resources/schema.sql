create table if not exists issue
(
    id         bigint auto_increment,
    title      varchar(100) not null,
    views      integer      not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);

create table if not exists reprocessed_issue
(
    id         bigint auto_increment,
    title      varchar(100) not null,
    image_url  text         not null,
    category   varchar(255) check (category in
                                   ('ENTERTAINMENTS', 'POLITICS', 'ECONOMY', 'SOCIETY', 'WORLD',
                                    'SPORTS')),
    issue_id   bigint       not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);
