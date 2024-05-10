create table if not exists reprocessed_issue
(
    id         bigint auto_increment,
    title      varchar(100) not null,
    image_url  text         not null,
    caption    varchar(255),
    origin_url text         not null,
    category   varchar(255) check (category in
                                   ('ENTERTAINMENTS', 'POLITICS', 'ECONOMY', 'SOCIETY', 'WORLD',
                                    'SPORTS')),
    views      integer      not null,
    topic      varchar(255) not null,
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
    views                integer      not null,
    created_at           timestamp(6) not null,
    updated_at           timestamp(6) not null,
    primary key (id)
);

create table if not exists reprocessed_issue_opinion
(
    id                   bigint auto_increment,
    paragraph_id         bigint       not null,
    reprocessed_issue_id bigint       not null,
    member_id            bigint       not null,
    content              varchar(300) not null,
    is_reliable          boolean      not null,
    created_at           timestamp(6) not null,
    updated_at           timestamp(6) not null,
    primary key (id)
);

create table if not exists follow_up_issue_opinion
(
    id                 bigint auto_increment,
    paragraph_id       bigint       not null,
    follow_up_issue_id bigint       not null,
    member_id          bigint       not null,
    content            varchar(300) not null,
    is_reliable        boolean      not null,
    created_at         timestamp(6) not null,
    updated_at         timestamp(6) not null,
    primary key (id)
);

create table if not exists member
(
    id                bigint auto_increment,
    nickname          varchar(30)  not null,
    profile_image_url text         not null,
    created_at        timestamp(6) not null,
    updated_at        timestamp(6) not null,
    primary key (id)
);

create table if not exists follow_up_issue_views
(
    id                 bigint auto_increment,
    follow_up_issue_id bigint       not null,
    member_id          bigint       not null,
    created_at         timestamp(6) not null,
    primary key (id)
);

create table if not exists reprocessed_issue_paragraph
(
    id                   bigint auto_increment,
    reprocessed_issue_id bigint  not null,
    content              text    not null,
    selectable           boolean not null,
    primary key (id)
);

create table if not exists follow_up_issue_paragraph
(
    id                 bigint auto_increment,
    follow_up_issue_id bigint  not null,
    content            text    not null,
    selectable         boolean not null,
    primary key (id)
);

create table if not exists reprocessed_issue_tag
(
    id                   bigint auto_increment,
    reprocessed_issue_id bigint       not null,
    tag                  varchar(100) not null,
    primary key (id)
);

create table if not exists reprocessed_issue_bookmark
(
    id                   bigint auto_increment,
    reprocessed_issue_id bigint  not null,
    member_id            bigint  not null,
    is_bookmarked        boolean not null,
    primary key (id)
);

create table if not exists reprocessed_issue_trust_vote
(
    id                   bigint auto_increment,
    reprocessed_issue_id bigint      not null,
    member_id            bigint      not null,
    status               varchar(50) not null,
    primary key (id)
);

create table if not exists reprocessed_issue_opinion_like
(
    id                           bigint auto_increment,
    reprocessed_issue_opinion_id bigint  not null,
    member_id                    bigint  not null,
    is_deleted                   boolean not null,
    primary key (id)
);
