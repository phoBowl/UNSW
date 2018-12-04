create table People (
	id          integer, -- PG: serial
	unswid      integer unique, -- staff/student id (can be null)
	password    ShortString not null,
	family      LongName,
	given       LongName not null,
	title       ShortName, -- e.g. "Prof", "A/Prof", "Dr", ...
	sortname    LongName not null,
	name        LongName not null,
	street      LongString,
	city        MediumString,
	state       MediumString,
	postcode    ShortString,
	country     integer references Countries(id),
	homephone   PhoneNumber, -- should be not null
	mobphone    PhoneNumber,
	email       EmailString not null,
	homepage    URLString,
	gender      char(1) check (gender in ('m','f')),
	birthday    date,
	origin      integer references Countries(id),  -- country where born
	primary key (id)
);


-- Student (sub-class): enrolment type

create table Students (
	id          integer references People(id),
	stype       varchar(5) check (stype in ('local','intl')),
	primary key (id)
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


-- Course: info about an offering of a subject in a given semester
-- we insist on knowing the lecturer because there's no point running
--   a course unless you've got someone organised to lecture it
-- Note: UNSW calls courses "course offerings"

create table Courses (
	id          integer, -- PG: serial
	subject     integer not null references Subjects(id),
	semester    integer not null references Semesters(id),
	homepage    URLString,
	primary key (id)
);

create table Semesters (
	id          integer, -- PG: serial
	unswid      integer not null unique,
	year        CourseYearType,
	term        char(2) not null check (term in ('S1','S2','X1','X2','A1','A2')),
	name        ShortName not null,
	longname    LongName not null,
	starting    date not null,
	ending      date not null,
	startBrk    date, -- start of mid-semester break
	endBrk      date, -- end of mid-semester break
	endWD       date, -- last date to withdraw without academic penalty
	endEnrol    date, -- last date to enrol without special permission
	census      date, -- last date to withdraw without paying for course
	primary key (id)
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


create domain AcadObjectGroupType as ShortName
	check (value in (
		'subject',      -- group of subjects
		'stream',       -- group of streams
		'program'       -- group of programs
	));

-- how to interpret combinations of objects in groups

create domain AcadObjectGroupLogicType as ShortName
	check (value in ( 'and', 'or'));

-- how groups are defined

create domain AcadObjectGroupDefType as ShortName
	check (value in (
		'enumerated',    -- has members in xxx_group_members
		'pattern',       -- members defined by e.g. COMP3###
		'query'          -- uses query to enumerate members
	));

-- there are some constraints in this table that we haven't implemented

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

alter table Acad_object_groups
	add foreign key (parent) references Acad_object_groups(id);

alter table Subjects
	add foreign key (excluded) references Acad_object_groups(id);

alter table Subjects
	add foreign key (equivalent) references Acad_object_groups(id);

-- Each kind of AcademicObjectGroup requires it own membership relation

create table Subject_group_members (
	subject     integer references Subjects(id),
	ao_group    integer references Acad_object_groups(id),
	primary key (subject,ao_group)
);

create table Stream_group_members (
	stream      integer references Streams(id),
	ao_group    integer references Acad_object_groups(id),
	primary key (stream,ao_group)
);

create table Program_group_members (
	program     integer references Programs(id),
	ao_group    integer references Acad_object_groups(id),
	primary key (program,ao_group)
);


-- Rules: requirements for programs and stream, pre-reqs for subjects

create domain RuleType as char(2)
	check (value in (
		'CC', -- core courses ... with min, max, subject group
		'PE', -- program electives ... with min, max, subject group
		'FE', -- free electives ... with min, max, group with FREE?###
		'GE', -- general education ... with min, max, group with GEN??###
		'RQ', -- subject pre-req ... typically with min, max, subject group
		'WM', -- WAM requirement ... typically with min WAM score
		'LR', -- limit rule ... with min or max, big subject group (####...)
		'MR', -- maturity rule ... with min UOC and (optionally) a subject group
		'DS', -- done stream ... with min, max, stream group
		'RC', -- recommended ... with subject group, useful for suggestions
		'IR'  -- information rule ... doesn't need checking
    ));

-- Various types of rules ...
-- Some rules require reference to a group of subjects or streams
-- min/max can have different kinds of units depending on rule type
--   (frequently they are UOC, sometimes just counters)
-- Rule names don't have a standard form and are not very useful
-- Rule descriptions are slightly more useful

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

create table Subject_prereqs (
	subject     integer references Subjects(id),
	career      CareerType, -- what kind of students it applies to
	rule        integer references Rules(id),
	primary key (subject,career,rule)
);

create table Stream_rules (
	stream      integer references Streams(id),
	rule        integer references Rules(id),
	primary key (stream,rule)
);

create table Program_rules (
	program     integer references Programs(id),
	rule        integer references Rules(id),
	primary key (program,rule)
);