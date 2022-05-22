-- +goose Up

insert into current_versions values ('idea', '2022.1');
insert into current_versions values ('go', '2022.1');
insert into current_versions values ('resharper', '2022.1');
insert into current_versions values ('resharper.sdk', 'dev');


insert into default_pages values ('idea', '2022.1', 'getting-started.html');
insert into default_pages values ('go', '2022.1', 'getting-started.html');
insert into default_pages values ('resharper', '2022.1', 'Introduction__Index.html');
insert into default_pages values ('resharper.sdk', 'dev', 'welcome.html');


-- +goose Down
truncate current_versions;
truncate default_pages;