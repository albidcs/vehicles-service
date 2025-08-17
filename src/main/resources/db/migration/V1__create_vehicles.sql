create table if not exists vehicles (
     id    bigserial primary key,
     make  varchar(100) not null,
     model varchar(100) not null,
     year  int not null
    );
create index if not exists idx_vehicles_make  on vehicles(make);
create index if not exists idx_vehicles_model on vehicles(model);
create index if not exists idx_vehicles_year  on vehicles(year);