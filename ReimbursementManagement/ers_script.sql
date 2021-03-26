SHOW search_path;
SET search_path TO "$user",public;
SET search_path TO "$user", 'ExpenseReimbursementManagmentSystem';

-----------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE reimbursement_confirmation;
DROP TABLE reimbursement;
DROP TABLE reimbursement_status;

DROP TABLE approval_confirmation;
DROP TABLE supervisor_approval;
DROP TABLE supervisor_approval_status;

DROP TABLE request_confirmation;
DROP TABLE request_image;
DROP TABLE request;
DROP TABLE request_status;

DROP TABLE event_location;
DROP TABLE city_state_postal;

DROP TABLE employee_supervisor_jt;
DROP TABLE users;
DROP TABLE roles;

-----------------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE roles(
	roleId serial PRIMARY KEY,
	roles varchar NOT NULL UNIQUE 
);
INSERT INTO roles values(DEFAULT, 'Supervisor');
INSERT INTO roles values(DEFAULT, 'Employee');

CREATE TABLE users(
	userId serial PRIMARY KEY,
	loginUsername varchar NOT NULL UNIQUE,
	loginPassword varchar NOT NULL,
	firstName varchar NOT NULL,
	lastName varchar NOT NULL,
	email varchar NOT NULL UNIQUE,
	roles integer REFERENCES roles(roleId)
);
INSERT INTO users values(DEFAULT, 'admin', 'adminPassword', 'A', 'A', 'a@email.com', 1);
INSERT INTO users values(DEFAULT, 'employee1', 'employeePassword', 'B', 'B', 'b@email.com', 1);
INSERT INTO users values(DEFAULT, 'employee2', 'employeePassword', 'C', 'C', 'c@email.com', 2);
INSERT INTO users values(DEFAULT, 'employee3', 'employeePassword', 'D', 'D', 'd@email.com', 2);
INSERT INTO users values(DEFAULT, 'employee4', 'employeePassword', 'E', 'E', 'e@email.com', 2);

CREATE TABLE employee_supervisor_jt(
	hierarchyId serial NOT NULL UNIQUE,
	userIdSupervisor integer REFERENCES users(userId),
	userIdEmployee integer REFERENCES users(userId),
	PRIMARY KEY(userIdSupervisor, userIdEmployee)
);
INSERT INTO employee_supervisor_jt values(DEFAULT, 1, 2);
INSERT INTO employee_supervisor_jt values(DEFAULT, 1, 3);
INSERT INTO employee_supervisor_jt values(DEFAULT, 2, 3);
INSERT INTO employee_supervisor_jt values(DEFAULT, 1, 4);
INSERT INTO employee_supervisor_jt values(DEFAULT, 2, 4);

CREATE TABLE city_state_postal(
	postalCode integer PRIMARY KEY,
	city varchar NOT NULL,
	state varchar NOT NULL
);
INSERT INTO city_state_postal values(1, 'EVERY', 'WHERE');
INSERT INTO city_state_postal values(2, 'EVERY', 'WHERE');
INSERT INTO city_state_postal values(3, 'ANY', 'WHERE');
INSERT INTO city_state_postal values(4, 'NO', 'WHERE');

CREATE TABLE event_location(
	locationId serial NOT NULL UNIQUE,
	street_number integer NOT NULL,
	street_name varchar NOT NULL,
	postalCode integer REFERENCES city_state_postal(postalCode),
	PRIMARY KEY(street_number, street_name, postalCode)
);
INSERT INTO event_location values(DEFAULT, 100, 'MAIN AVE', 1);
INSERT INTO event_location values(DEFAULT, 200, 'FREEWAY RD', 1);

CREATE TABLE request_status(
	statusId serial PRIMARY KEY,
	status varchar NOT NULL UNIQUE 
);
INSERT INTO request_status values(DEFAULT, 'Resolved');
INSERT INTO request_status values(DEFAULT, 'Pending');
INSERT INTO request_status values(DEFAULT, 'More information required');

CREATE TABLE request(
	requestId serial PRIMARY KEY,
	amount float8 CHECK (amount >= 0),
	eventDate date NOT NULL CHECK(eventDate <= CURRENT_DATE),
	eventLocation integer REFERENCES event_location(locationId),
	requestedEvent varchar NOT NULL,
	requesterUserId integer REFERENCES users(userId),
	statusId integer REFERENCES request_status(statusId)
);
INSERT INTO request values(DEFAULT, 100.01, '2021-01-14', 1, 'Anime Convention', 3, 2);
INSERT INTO request values(DEFAULT, 100.01, '2021-01-14', 1, 'Anime Convention', 4, 2);
INSERT INTO request values(DEFAULT, 10000.99, '2021-01-17', 2, 'Magic Tricks Boot Camp', 2, 2);
INSERT INTO request values(DEFAULT, 1.99, '2021-02-01', 2, 'Topology Crash Course', 3, 1);

CREATE TABLE request_image(
	imageId serial PRIMARY KEY,
	fileName varchar NOT NULL,
	requestId integer REFERENCES request(requestId)
);

CREATE TABLE request_confirmation(
	confirmationId serial PRIMARY KEY,
	confirmationDate date NOT NULL CHECK(confirmationDate <= CURRENT_DATE),
	requestId integer REFERENCES request(requestId)
);
INSERT INTO request_confirmation values(DEFAULT, '2021-01-14', 1);
INSERT INTO request_confirmation values(DEFAULT, '2021-01-14', 2);
INSERT INTO request_confirmation values(DEFAULT, '2021-01-17', 3);
INSERT INTO request_confirmation values(DEFAULT, '2021-02-01', 4);

CREATE TABLE supervisor_approval_status(
	statusId serial PRIMARY KEY,
	status varchar NOT NULL UNIQUE
);
INSERT INTO supervisor_approval_status values(DEFAULT, 'Resolved');
INSERT INTO supervisor_approval_status values(DEFAULT, 'Pending');
INSERT INTO supervisor_approval_status values(DEFAULT, 'More information required');

CREATE TABLE supervisor_approval(
	approvalId serial PRIMARY KEY,
	dateOfPreviousUpdate date NOT NULL CHECK(dateofpreviousupdate <= CURRENT_DATE),
	requestId integer REFERENCES request(requestId),
	hierarchyId integer REFERENCES employee_supervisor_jt(hierarchyId),
	statusId integer REFERENCES supervisor_approval_status(statusId),
	approval boolean 
);
INSERT INTO supervisor_approval values(DEFAULT, '2000-01-01', 1, 3, 2, 'no');
INSERT INTO supervisor_approval values(DEFAULT, '2000-01-01', 1, 2, 2, 'no');
INSERT INTO supervisor_approval values(DEFAULT, '2000-01-01', 2, 4, 2, 'no');
INSERT INTO supervisor_approval values(DEFAULT, '2000-01-01', 2, 5, 2, 'no');
INSERT INTO supervisor_approval values(DEFAULT, '2000-01-01', 3, 1, 2, 'no');
INSERT INTO supervisor_approval values(DEFAULT, '2021-02-06', 4, 3, 1, 'yes');
INSERT INTO supervisor_approval values(DEFAULT, '2021-02-06', 4, 2, 1, 'yes');

CREATE TABLE approval_confirmation(
	confirmationId serial PRIMARY KEY,
	confirmationDate date NOT NULL CHECK(confirmationDate <= CURRENT_DATE),
	approvalId integer REFERENCES supervisor_approval(approvalId)
);
INSERT INTO approval_confirmation values(DEFAULT, '2021-02-06', 5);

CREATE TABLE reimbursement_status(
	statusId serial PRIMARY KEY,
	status varchar NOT NULL UNIQUE 
);
INSERT INTO reimbursement_status values(DEFAULT, 'Resolved');
INSERT INTO reimbursement_status values(DEFAULT, 'Pending');
INSERT INTO reimbursement_status values(DEFAULT, 'More information required');
INSERT INTO reimbursement_status values(DEFAULT, 'Request amount changed');

CREATE TABLE reimbursement(
	reimbursementId serial PRIMARY KEY,
	amount float8 CHECK (amount >= 0),
	dateAwarded date CHECK(dateAwarded <= CURRENT_DATE),
	finalApprovalId integer REFERENCES supervisor_approval(approvalId),
	statusId integer REFERENCES reimbursement_status(statusId)
);
INSERT INTO reimbursement values(DEFAULT, 100.01, '2000-01-01', 2, 2);
INSERT INTO reimbursement values(DEFAULT, 100.01, '2000-01-01', 3, 2);
INSERT INTO reimbursement values(DEFAULT, 10000.99, '2000-01-01', 4, 2);
INSERT INTO reimbursement values(DEFAULT, 1.99, '2021-02-10', 5, 1);

CREATE TABLE reimbursement_confirmation(
	confirmationId serial PRIMARY KEY,
	confirmationDate date NOT NULL CHECK(confirmationDate <= CURRENT_DATE),
	reimbursementId integer REFERENCES reimbursement(reimbursementId)
);
INSERT INTO reimbursement_confirmation values(DEFAULT, '2021-02-10', 4);