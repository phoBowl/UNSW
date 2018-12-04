-- COMP3311 18s1 Assignment 1
-- Written by Nam Tran (z5090191), April 2018

-- Q1: ...

create or replace view Q1(unswid, name)
as
select p.unswid, p.name
from people p, course_enrolments c
where p.id = c.student
group by p.unswid, p.name
having count(c.student) > 65;

-- Q2: ...

create or replace view Q2(nstudents, nstaff, nboth)
as
select 
(select count(stud.id) from students stud left join staff staf on stud.id = staf.id), 
(select count(staf1.id) from staff staf1 left join students stud1 on stud1.id = staf1.id), 
(select count(staf3.id) from staff staf3 join students stud3 on staf3.id = stud3.id)
;

-- Q3: ...

create or replace view Q3(name, ncourses)
as
select name,ncourses from people p 
    join (select staff, count(staff) as ncourses 
			from course_staff where role = 1870 
			group by staff order by ncourses DESC limit 1) 
			highest on p.id = highest.staff;

-- Q4: ...

create or replace view Q4a(id)
as
select p.unswid from People p
join Students s on (s.id = p.id)
join program_enrolments enrol on (enrol.student = s.id)
join programs prog on (enrol.program =  prog.id)
join semesters sem on (enrol.semester = sem.id)
where  prog.code='3978' and  sem.term='S2' and sem.year='2005';
;

create or replace view Q4b(id)
as
select p.unswid from People p
join Students s on (s.id = p.id)
join program_enrolments enrol on (enrol.student = s.id)
join semesters sem on (enrol.semester = sem.id)
join Stream_enrolments stream_enrol on (enrol.id = stream_enrol.partOf)
join Streams stream on (stream_enrol.stream = stream.id)
where sem.term='S2' and sem.year='2005' and stream.code='SENGA1'
;

create or replace view Q4c(id)
as
select p.unswid from People p
join Students stud on (p.id = stud.id) 
join program_enrolments pe on (stud.id = pe.student)
join programs prog on (pe.program = prog.id)
join semesters sem on (pe.semester = sem.id)
where prog.offeredby ='89' and sem.year='2005' and sem.term='S2';

-- Q5: ...
create or replace view findID(id)
as
select facultyOf(org.id) from OrgUnits org
where org.utype='9';
;

create or replace view countID(id, numID)
as
select fi.id, count(fi.id) as numID from findID fi where fi.id is not null
group by fi.id;


create or replace view Q5(name)
as
select org.name from orgUnits org
where org.id = (select countID.id from countID where countID.numID = (select max(numID) from countID));

-- Q6: ...

create or replace function Q6(input integer) returns text
as
$$
	select name from People where input=unswid or input=id;
$$ language sql
;

-- Q7: ...

create or replace function Q7(text)
	returns table (course text, year integer, term text, convenor text)
as $$
	select sub.code::text, sem.year, sem.term::text, ppl.name::text
	from  Subjects sub, Semesters sem, Staff_roles sr, People ppl, Course_staff cs, Courses cours, Staff stf
	where sub.id = cours.subject
	and cours.semester = sem.id
	and sr.id = cs.role
	and cs.course = cours.id
	and cs.staff =  stf.id
	and stf.id = ppl.id
	and sr.name= 'Course Convenor'
	and sub.code = $1;

$$ language sql
;

-- Q8: ...

create or replace function Q8(_sid integer)
	returns setof NewTranscriptRecord
LANGUAGE plpgsql
AS $function$
declare
        rec NewTranscriptRecord;
        UOCtotal integer := 0;
        UOCpassed integer := 0;
        wsum integer := 0;
        wam integer := 0;
        x integer;
begin
        select s.id into x
        from   Students s join People p on (s.id = p.id)
        where  p.unswid = _sid;
        if (not found) then
                raise EXCEPTION 'Invalid student %',_sid;
        end if;
        for rec in
                select  su.code,
                        substr(t.year::text,3,2)||lower(t.term) as term,
                        prog.code,
                        substr(su.name,1,20) as name,
                        e.mark, e.grade, su.uoc
                from    People p
                        join Students s on (p.id = s.id)
                        join Course_enrolments e on (e.student = s.id)
                        join Program_enrolments progEnrol on (progEnrol.student = s.id)
                        join Courses c on (c.id = e.course)
                        join Subjects su on (c.subject = su.id)
                        join Semesters t on (c.semester = t.id and progEnrol.semester = t.id)
                        join Programs prog on (prog.id = progEnrol.program)
                where  p.unswid =  _sid
                order  by t.starting, su.code
        loop
                if (rec.grade = 'SY') then
                        UOCpassed := UOCpassed + rec.uoc;
                elsif (rec.mark is not null) then
                        if (rec.grade in ('PT','PC','PS','CR','DN','HD','A','B','C')) then
                                -- only counts towards creditted UOC
                                -- if they passed the course
                                UOCpassed := UOCpassed + rec.uoc;
                        end if;
                        -- we count fails towards the WAM calculation
                        UOCtotal := UOCtotal + rec.uoc;
                        -- weighted sum based on mark and uoc for course
                        wsum := wsum + (rec.mark * rec.uoc);
                        -- don't give UOC if they failed
                        if (rec.grade not in ('PT','PC','PS','CR','DN','HD','A','B','C')) then
                                rec.uoc := 0;
                        end if;

                end if;
                return next rec;
        end loop;
        if (UOCtotal = 0) then
                rec := (null,null,null,'No WAM available',null,null,null);
        else
                wam := wsum / UOCtotal;
                rec := (null,null,null,'Overall WAM',wam,null,UOCpassed);
        end if;
        -- append the last record containing the WAM
        return next rec;
end;
$function$;

-- Q9: ...
create or replace function Q9(integer)
	returns setof AcObjRecord
as $$
declare
    my_gtype text;
    my_gdefby text;
    my_definition text;
    aor AcObjRecord;
    regexSub char(8);
begin
    select Acad_object_groups.gtype, Acad_object_groups.gdefby, Acad_object_groups.definition
    into my_gtype, my_gdefby, my_definition  
    from Acad_object_groups
    where Acad_object_groups.id=$1;
    if my_definition not like '%{%'
    then
        my_definition:=replace(my_definition,'#','.');
        my_definition := replace(my_definition, 'x','.');
        my_definition := replace(my_definition,',','|');
    else
        my_definition:= null;
    end if;
    if my_gtype = 'subject'
    then
        for regexSub in select sub.code
            from Subjects sub where sub.code ~ my_definition
            and sub.code not like 'FREE%'
            and sub.code not like 'GENG%'
            and sub.code not like '%{%;'
        loop
            aor := (my_gtype,regexSub);
            return next aor;
        end loop;
    end if;
end;
$$ language plpgsql
;

