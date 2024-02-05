create table if not exists reprocessed_issue
(
    id         bigint auto_increment,
    title      varchar(100) not null,
    image_url  text         not null,
    category   varchar(255) check (category in
                                   ('ENTERTAINMENTS', 'POLITICS', 'ECONOMY', 'SOCIETY', 'WORLD',
                                    'SPORTS')),
    views      integer      not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);

create table if not exists follow_up_issue
(
    id                   bigint auto_increment,
    title                varchar(100) not null,
    image_url            text         not null,
    category             varchar(255) check (category in
                                             ('ENTERTAINMENTS', 'POLITICS', 'ECONOMY', 'SOCIETY',
                                              'WORLD', 'SPORTS')),
    tag                  varchar(255) check (tag in ('TRIAL_RESULTS', 'INTERVIEW', 'OFFICIAL_POSITION')),
    reprocessed_issue_id bigint       not null,
    created_at           timestamp(6) not null,
    updated_at           timestamp(6) not null,
    primary key (id)
);

create table if not exists issue_comment
(
    id                   bigint auto_increment,
    content              varchar(1000) not null,
    reprocessed_issue_id bigint        not null,
    member_id            bigint        not null,
    created_at           timestamp(6)  not null,
    updated_at           timestamp(6)  not null,
    primary key (id)
);
