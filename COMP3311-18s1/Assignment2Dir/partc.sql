create table Rules (
	id          integer,
	name        MediumName,
	type        RuleType,
	min         integer check (min >= 0),
	max         integer check (min >= 0),
	ao_group    integer references Acad_object_groups(id),
	description TextString,
	primary key (id)
);

create table OrgUnits (
	id          integer, -- PG: serial
	utype       integer not null references OrgUnit_types(id),
	name        MediumString not null,
	longname    LongString,
	unswid      ShortString,
	phone       PhoneNumber,
	email       EmailString,
	website     URLString,
	starting    date, -- not null
	ending      date,
	primary key (id)
);


-- OrgUnit_groups: how organisational units are related
-- notes:
--   allows for a multi-level hierarchy of groups

create table OrgUnit_groups (
	owner	    integer references OrgUnits(id),
	member      integer references OrgUnits(id),
	primary key (owner,member)
);

create table Program_enrolments (
	id          integer,
	student     integer not null references Students(id),
	semester    integer not null references Semesters(id),
	program     integer not null references Programs(id),
	wam         real,
	standing    integer references Academic_standing(id),
	advisor     integer references Staff(id),
	notes       TextString,
	primary key (id)
);


-- Stream_enrolments: student's enrolment in streams in one semester

create table Stream_enrolments (
	partOf      integer references Program_enrolments(id),
	stream      integer references Streams(id),
	primary key (partOf,stream)
);


-- Course_enrolments: student's enrolment in a course offering
-- null grade means "currently enrolled"
-- if course is graded SY/FL, then mark always remains null

create table Course_enrolments (
	student     integer references Students(id),
	course      integer references Courses(id),
	mark        integer check (mark >= 0 and mark <= 100),
	grade       GradeType,
	stuEval     integer check (stuEval >= 1 and stuEval <= 6),
	primary key (student,course)
);

create table Programs (
	id          integer, -- PG: serial
	code        char(4) not null, -- e.g. 3978, 3645, 3648
	name        LongName not null,
	uoc         integer check (uoc >= 0),
	offeredBy   integer references OrgUnits(id),
	career      CareerType,
	duration    integer,  -- #years
	description TextString, -- PG: text
	primary key (id)
);

-- Streams: academic details of a major/minor stream(s) in a degree

create table Streams (
	id          integer, -- PG: serial
	code        char(6) not null, -- e.g. COMPA1, SENGA1
	name        LongName not null,
	offeredBy   integer references OrgUnits(id),
	stype       integer references Stream_types(id),
	description TextString,
	primary key (id)
);


-- Subjects: academic details of a course (version)
-- "code" is standard UNSW course code (e.g. COMP3311)
-- "firstOffer" and "lastOffer" indicate a timespan during
--   which this subject was offered to students; if "lastOffer"
--   is null, then the subject is still running
-- Note: UNSW calls subjects "courses"

create table Subjects (
	id          integer, -- PG: serial
	code        char(8) not null,
--	              PG: check (code ~ '[A-Z]{4}[0-9]{4}'),
	name        MediumName not null,
	longname    LongName,
	uoc         integer check (uoc >= 0),
	offeredBy   integer references OrgUnits(id),
	eftsload    float,
	career      CareerType,
	syllabus    TextString, -- PG: text
	contactHPW  float, -- contact hours per week
	excluded    integer, -- references Acad_object_groups(id),
	equivalent  integer, -- references Acad_object_groups(id),
	primary key (id)
);

create table Acad_object_groups (
	id          integer,
	name        LongName,
	gtype       AcadObjectGroupType not null,
	glogic      AcadObjectGroupLogicType,
	gdefBy      AcadObjectGroupDefType not null,
	negated     boolean default false,
	parent      integer, -- references Acad_object_groups(id),
	definition  TextString, -- if pattern or query-based group
	primary key (id)
);
