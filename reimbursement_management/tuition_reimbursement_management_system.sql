DROP TABLE reimbursement;
DROP TABLE reimbursement_status;

DROP TABLE request;
DROP TABLE request_status;

DROP TABLE benefits_coordinator_approval;
DROP TABLE department_head_approval;
DROP TABLE direct_supervisor_approval;
DROP TABLE approval_status;

DROP TABLE grade;
DROP TABLE grading_format;
DROP TABLE presentation_audience;

DROP TABLE event_type;
DROP TABLE locations;
DROP TABLE street_name_number;
DROP TABLE city_state_postal;

DROP TABLE employee;
DROP TABLE roles;

------------------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE roles(
	roleId serial PRIMARY KEY,
	roles varchar NOT NULL UNIQUE 
);
INSERT INTO roles values(DEFAULT, 'Benefits Coordinator');
INSERT INTO roles values(DEFAULT, 'Department Head');
INSERT INTO roles values(DEFAULT, 'Direct Supervisor');
INSERT INTO roles values(DEFAULT, 'Employee');

CREATE TABLE employee(
	userId serial PRIMARY KEY,
	username varchar NOT NULL UNIQUE, 
	userPassword varchar NOT NULL,
	firstName varchar NOT NULL,
	lastName varchar NOT NULL,
	email varchar NOT NULL UNIQUE,
	roleId integer REFERENCES roles(roleId)
);
INSERT INTO employee values(DEFAULT, 'admin1', 'adminPassword', 'Christian', 'Abad', 'christian.abad@revature.net', 1);
INSERT INTO employee values(DEFAULT, 'admin2', 'adminPassword', 'John', 'Smith', 'js@email.com', 1);
INSERT INTO employee values(DEFAULT, 'employee1', 'employeePassword', 'A', 'A', 'a@email.com', 2);
INSERT INTO employee values(DEFAULT, 'employee2', 'employeePassword', 'B', 'B', 'b@email.com', 2);
INSERT INTO employee values(DEFAULT, 'employee3', 'employeePassword', 'M', 'M', 'm@email.com', 3);
INSERT INTO employee values(DEFAULT, 'employee4', 'employeePassword', 'N', 'N', 'n@email.com', 3);
INSERT INTO employee values(DEFAULT, 'employee5', 'employeePassword', 'X', 'X', 'x@email.com', 4);
INSERT INTO employee values(DEFAULT, 'employee6', 'employeePassword', 'Y', 'Y', 'y@email.com', 4);

CREATE TABLE city_state_postal(
	postalCode integer PRIMARY KEY,
	city varchar NOT NULL,
	state varchar NOT NULL
);
INSERT INTO city_state_postal values(10000, 'Any', 'Where');
INSERT INTO city_state_postal values(20000, 'Some', 'Where');
INSERT INTO city_state_postal values(30000, 'No', 'Where');

CREATE TABLE locations(
	street_num integer,
	street_name varchar,
	postalCode integer,
	PRIMARY KEY (street_num, street_name, postalCode)
);
INSERT INTO street_name_number values(123, 'Main St', 10000);
INSERT INTO street_name_number values(235, 'Main Ave', 20000);
INSERT INTO street_name_number values(100, 'Freeway Rd', 20000);

CREATE TABLE event_type(
	eventTypeId serial PRIMARY KEY,
	eventType varchar NOT NULL UNIQUE
);
INSERT INTO event_type values(DEFAULT, 'University Course');
INSERT INTO event_type values(DEFAULT, 'Seminar');
INSERT INTO event_type values(DEFAULT, 'Certification Preparation Class');
INSERT INTO event_type values(DEFAULT, 'Certification');
INSERT INTO event_type values(DEFAULT, 'Technical Training');
INSERT INTO event_type values(DEFAULT, 'Other');