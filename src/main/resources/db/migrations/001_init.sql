-- +goose Up

create table current_versions
(
    product_name    varchar primary key,
    product_version varchar
);

create table default_pages
(
    product_name    varchar,
    product_version varchar,
    default_page    varchar,
    PRIMARY KEY (product_name, product_version)
);

-- +goose Down
DROP TABLE current_versions;
DROP TABLE default_pages;