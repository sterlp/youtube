select * from accounts;

update accounts 
set balance = 20 - 10, "version" = 2
where id = 'b' and "version" = 1;