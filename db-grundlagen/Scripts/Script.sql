drop table if exists accounts;
CREATE TABLE accounts (
    id varchar(10) NOT NULL,
    balance integer NOT NULL DEFAULT 0,
    "version" integer NOT NULL DEFAULT 1,
    PRIMARY KEY (ID)
);

delete from accounts;
insert into accounts values ('a', 15);
insert into accounts values ('b', 20);
insert into accounts values ('e', 1);

select * from accounts for update;

update accounts 
set balance = 20 + 10, "version" = 2
where id = 'b' and "version" = 1;


select application_name, state, query, pid from pg_catalog.pg_stat_activity;