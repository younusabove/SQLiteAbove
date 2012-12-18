drop table  vendor;
drop table  purchase_order;
drop table  vendor_purchase;

CREATE TABLE vendor
(
    vendor_id integer   PRIMARY KEY autoincrement,
    vendorname   VARCHAR2(4000) NOT NULL 
);
CREATE TABLE vendor_purchase
(
    vendor_id integer NOT NULL ,
    purchase_id   integer NOT NULL ,
    PRIMARY KEY (vendor_id, purchase_id)
);

CREATE TABLE purchase_order
(
    purchase_id integer   PRIMARY KEY autoincrement,
    purchasedetail   VARCHAR2(4000) NOT NULL ,
    purchaseType VARCHAR2(400) NOT NULL,
     purchaseAmount integer NOT NULL,
     purchaseDate date
);

insert into vendor(vendorname) values('HCL');
insert into purchase_order(,purchasedetail,purchaseType,purchaseAmount,purchaseDate) values('DELL','Staff augmentation',30,'2013-10-01');
insert into vendor_purchase(vendor_id, purchase_id) values(1,1);

commit;


