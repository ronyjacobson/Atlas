create table temp_location(id INT, label VARCHAR(100));
create table temp_category(id INT, category VARCHAR(100));
create table final(id INT NOT NULL, label VARCHAR(100) NOT NULL, category varchar(100) NOT NULL, primary key(`id`));

insert into temp_location VALUES(1, "paz");
insert into temp_location VALUES(2, "etan");
insert into temp_category VALUES(1, "doctor");
