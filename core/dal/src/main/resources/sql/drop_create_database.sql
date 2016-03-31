-- The database has to be dropped first
drop database op;

-- The login can now be dropped and recreated
drop user ambergarden;
create user ambergarden;

-- Password = password
-- command: md5 -s password
--          MD5 ("password") = 3750c667d5cd8aecc0a9213b362066e9
--ALTER ROLE ambergarden LOGIN ENCRYPTED PASSWORD '3750c667d5cd8aecc0a9213b362066e9' VALID UNTIL 'infinity';
ALTER ROLE ambergarden WITH ENCRYPTED PASSWORD 'password' VALID UNTIL 'infinity';
   
ALTER Role ambergarden CREATEDB;

create database op;