<?php
// COMP3311 18s1 Assignment 2
// Functions for assignment Tasks A-E
// Written by <<YOUR NAME>> (<<YOUR ID>>), May 2018

// assumes that defs.php has already been included


// Task A: get members of an academic object group

// E.g. list($type,$codes) = membersOf($db, 111899)
// Inputs:
//  $db = open database handle
//  $groupID = acad_object_group.id value
// Outputs:
//  array(GroupType,array(Codes...))
//  GroupType = "subject"|"stream"|"program"
//  Codes = acad object codes in alphabetical order
//  e.g. array("subject",array("COMP2041","COMP2911"))


// select su.code from Subjects su, Subject_group_members sgm
// where su.id=sgm.subject and sgm.ao_group=2249;

function membersOf($db,$groupID)
{
    $res = array();
    $q = "select * from acad_object_groups where id = %d";
    $tuple = dbOneTuple($db, mkSQL($q, $groupID));      
    
    
    
    
    if($tuple['gtype'] == 'subject' or $tuple['gtype'] == 'stream' or $tuple['gtype'] == 'program'){
        ##################################
        ############ENUMURATED###############
        #################################
        if($tuple['gdefby'] == 'enumerated'){
            $queryCode="select su.code from Subjects su, Subject_group_members sgm
                        where su.id=sgm.subject and sgm.ao_group=%d";
            if ($tuple['gtype'] == 'stream'){
                $queryCode="select st.code from Streams st, Stream_group_members stgm
                        where st.id=stgm.stream and stgm.ao_group=%d";
            }
            elseif ($tuple['gtype'] == 'program') {
                $queryCode="select prog.code from Programs prog, Program_group_members pgm
                        where prog.id=pgm.program and pgm.ao_group=%d"; 
            }
            $x_queryCode = dbAllTuples($db,mkSQL($queryCode,$groupID));
            $res_array = t1Helper($x_queryCode);
            $counter = 0;
            foreach ($res_array as $item){
                $res[$counter] = $item;
                $counter += 1;
            }
            #CONTINUE TO CHECK IF ANY SUBGROUP - FIND CHILDREN
            $childIDArray = t1FindChild($db,$groupID);
            $_res = array();
            if (!empty($childIDArray)){
                foreach ($childIDArray as $child) {
                            
                    $res_child = array(); 
                    $queryCode="select su.code from Subjects su, Subject_group_members sgm
                                where su.id=sgm.subject and sgm.ao_group=%d";
                    $x_queryCode = dbAllTuples($db,mkSQL($queryCode,$child));
                    $res_child = t1Helper($x_queryCode);

                    $counter = 0;
                    foreach ($res_child as $item){
                        $_res[$counter] = $item;
                        $counter += 1;
                    }
                    $res = array_merge($res,$_res);
                }
            }
        }
        ##################################
        ############PATTERN###############
        #################################
        if($tuple['gdefby'] == 'pattern'){
            // echo $tuple['definition'];
            // echo "\n";
            $patterns = explode(",",$tuple['definition']);
            $regex = "/.*\[[0-9][0-9]\].*/"; # [01]
            $regex2 = "/^[A-Z]+[0-9]{4}$/";
            $regex3 = "/.*##.*/";
            $regex4 = "/[A-Z]{4}[0-9]{3}\[[0-9]\-[0-9]\]/";
            $regex5 = "/\{[A-Z]+[0-9]{4};[A-Z]+[0-9]{4}\}/";
            $regex6 = "/^[0-9]{4}$/";
            $regex7 = "//";
            $res = array();
            foreach ($patterns as $pattern) {
                $res_temp = array();
                # Pattern-based subject group like TELE311[89]
                #############################################
                if (preg_match($regex, $pattern)){
                    // echo "BAME";
                    // echo "\n";
                    
                    $square_brackets = array();
                    preg_match('/\[[0-9]{2}\]/', $pattern, $square_brackets);
                    $fix_part = str_replace($square_brackets[0], "", $pattern);
                    #echo("fucking fix_part: $fix_part\n");
                    #print_r($square_brackets);
                    $square_brackets[0] = str_replace("[", "", $square_brackets[0]);
                    $digits = str_replace("]", "", $square_brackets[0]);
                    $digits = str_split($digits);
                    $counter = 0;
                    foreach ($digits as $digit) {
                        $fix_part_temp = $fix_part . $digit;
                        $res_temp[$counter] = $fix_part_temp;
                        $counter++; 
                    }
                    $res = array_merge($res, $res_temp);
                }
                #Pattern-based subject group like ELEC4122
                ##########################################
                elseif (preg_match($regex2, $pattern) or preg_match($regex6, $pattern)){
                    $res_temp[0] = $pattern;
                    $res = array_merge($res, $res_temp);
                }
                #Pattern-based subject group like GEN####
                ##########################################
                elseif (preg_match($regex3, $pattern)){
                    $res_temp[0] = $pattern;
                    $res = array_merge($res, $res_temp);
                }
                #Pattern-based subject group like ZEIT460[2-5]
                ##########################################
                elseif(preg_match($regex4, $pattern)){
                    $square_brackets = array();
                    preg_match('/\[[0-9]-[0-9]\]/', $pattern, $square_brackets);
                    $fix_part = str_replace($square_brackets[0], "", $pattern);
                    $digits = str_split($square_brackets[0]);
                    $start = $digits[1];
                    $end = $digits[3];
                    $counter = 0;
                    for ($i = $start; $i <= $end; $i++){
                        $fix_part_temp = $fix_part . $i;
                        $res_temp[$counter] = $fix_part_temp;
                        $counter++;
                    }
                    $res = array_merge($res, $res_temp);
                }
                #Pattern-based subject group like {ZEIT4500;ZEIT4501}
                ####################################################
                elseif (preg_match($regex5, $pattern)) {
                    $pattern = str_replace("{", "", $pattern);
                    $pattern = str_replace("}", "", $pattern);
                    $codes = explode(";", $pattern);
                    $counter = 0;
                    foreach ($codes as $code) {
                        $res_temp[$counter] = $code;
                        $counter++;
                    }
                    $res = array_merge($res,$res_temp);
                }
            }
        }elseif ($tuple['gdefby'] == "query") { //
		      #$query = dbOneTuple($db, mkSQL($q, $groupID));
		      return array($tuple["gtype"], array("########")); // stub	
		   }

    }

    sort($res);
    return array($grp["gtype"], $res); // stub
}

function t1FindChild($db, $groupID){
    $queryCode = "select id from Acad_object_groups where parent=%d";
    $tuples = dbAllTuples($db, mkSQL($queryCode, $groupID));
    $id_array = t1Helper($tuples);
    return $id_array;
}

#put all tuples values in to array
function t1Helper($tuples){
    $res_array =  array();
    $counter = 0;
    
    foreach ($tuples as $tuple) {
        foreach ($tuple as $attr) {
            if (!in_array($attr, $res_array))
                $res_array[$counter] = $attr;
                $counter++;
        }
    }
    return $res_array;
}

// Task B: check if given object is in a group

// E.g. if (inGroup($db, "COMP3311", 111938)) ...
// Inputs:
//  $db = open database handle
//  $code = code for acad object (program,stream,subject)
//  $groupID = acad_object_group.id value
// Outputs:
//  true/false

#HANDLE QUERY TYPE 
function inGroup($db, $code, $groupID)
{
    $q = "select * from acad_object_groups where id = %d";
    $tuple = dbOneTuple($db, mkSQL($q, $groupID));

    ##########################SUBJECT################################################
    #if ($tuple['gtype'] == 'subject'){
    $tuples = membersOf($db, $groupID);
    $regex = "/[#]+/";
    #echo "$tuples[1][0]\n";
    if (preg_match($regex, $tuples[1][0])){
        if (preg_match("/GENG/", $tuples[1][0])){
            $tuples[1][0] = "GEN#####"; 
        }

        $regex = str_replace('#', '.', $tuples[1][0]);
        if( preg_match("/$regex/", $code)){
            return true;
        }
    }
    #print_r($tuples);
    if(in_array($code, $tuples[1])){
        return true;
    }
    else{
        return false;
    }
        
    return false; // stub
}


// Task C: can a subject be used to satisfy a rule

// E.g. if (canSatisfy($db, "COMP3311", 2449, $enr)) ...
// Inputs:
//  $db = open database handle
//  $code = code for acad object (program,stream,subject)
//  $ruleID = rules.id value
//  $enr = array(ProgramID,array(StreamIDs...))
// Outputs:

// me check: 
//  select * from rules where id = 10384;
// select * from Acad_object_groups where id = 4064;

//  select * from OrgUnits ou, Subjects su where su.code='GENE1011' and ou.id=su.offeredby;
function canSatisfy($db, $code, $ruleID, $enrolment)
{
    #echo "$enrolment\n";
    #print_r($enrolment);
    #echo "$enrolment[0]\n";
    #$q = 'select code from programs where id=%d';
    #$program = dbOneTuple($db, mkSQL($q, $enrolment[0]));
    #$program= $program['code'];
    #echo "$program\n";
    

    $q = 'select ao_group from rules where id=%d';
    $tuple = dbOneTuple($db, mkSQL($q, $ruleID));
    #print_r($tuple);
    $ao_group = $tuple['ao_group'];
    ##echo "AO_GROUP: $ao_group $code\n";
    $check = inGroup($db, $code, $ao_group);
    #echo "CHECK: $check\n";
    if($check == true){
        #print_r($enrolment);
        
        $query='select ou.id from OrgUnits ou, Subjects su where su.code=%s and ou.id=su.offeredby';
        $tuple = dbOneTuple($db, mkSQL($query, $code));
        #print_r($tuple);
        
        $org1 = $tuple['id'];
        if(empty($enrolment[1]) and preg_match('/GEN.*/', $code)){
            #echo "this is $enrolment[0]\n";

            #gen ed outside stream is ok 
            $query2='select offeredby from Programs where id=%d';
            $tuple = dbOneTuple($db, mkSQL($query2, $enrolment[0]));
            $one = $tuple[0];
            #echo "ONE: $one\n";
            #print_r($tuple);


            $query='select offeredBy from Subjects where code=%s';
            $tuple = dbOneTuple($db, mkSQL($query, $code));
            #print_r($tuple);
            $two = $tuple[0];
            #echo "TWO: $two\n";


            $query='select owner from OrgUnit_groups where member=%d';
            $tuple = dbOneTuple($db, mkSQL($query, $one));
            $one = $tuple[0];
            #echo "ONE: $one\n";
            #print_r($tuple);


            $query='select owner from OrgUnit_groups where member=%d';
            $tuple = dbOneTuple($db, mkSQL($query, $two));
            #$echo "2\n";
            #print_r($tuple);
            $two = $tuple[0];
            #echo "TWO: $two\n";
            
            if (!empty($one) and !empty($two)){
               #  echo "BBBBBB\n";
                if( $one == $two){
                    // echo "BAME\n";
                    
                    return false;
                  }
            }
                
            if ($tuple['offeredby'] != $org1){
                return true;
            }
            else{ 
                
                return false;
            }
        }
        #check case not ingroup but not in the same owner
        
        return true;
    }

    else {
            $query2='select offeredby from Programs where id=%d';
            $tuple = dbOneTuple($db, mkSQL($query2, $enrolment[0]));
            $one = $tuple[0];
            #echo "ONE: $one\n";
            #print_r($tuple);

            $query='select offeredBy from Subjects where code=%s';
            $tuple = dbOneTuple($db, mkSQL($query, $code));
            #print_r($tuple);
            $two = $tuple[0];
            #echo "TWO: $two\n";

            $query='select owner from OrgUnit_groups where member=%d';
            $tuple = dbOneTuple($db, mkSQL($query, $one));
            $one = $tuple[0];
            #echo "ONE: $one\n";
            #print_r($tuple);


            $query='select owner from OrgUnit_groups where member=%d';
            $tuple = dbOneTuple($db, mkSQL($query, $two));
            #$echo "2\n";
            #print_r($tuple);
            $two = $tuple[0];
            #echo "TWO: $two\n";
            
            if (!empty($one) and !empty($two)){
               #  echo "BBBBBB\n";
                if( $one == $two){
                    return false;
                  }
            }
            #print_r($tuple);
            #echo "offerby: $tuple['offeredby']\n";
            #####$tuple[0] is 'owner'####
            if ($tuple[0] != $org1){
                return true;
            }
            else{ 
            #   echo "BAME\n";  
                return false;
            }
            
            return true;
    }


    // echo "BAME\n";
    return false; // stub
}

// Task D: determine student progress through a degree

// E.g. $vtrans = progress($db, 3012345, "05s1");
// Inputs:
//  $db = open database handle
//  $stuID = People.unswid value (i.e. unsw student id)
//  $semester = code for semester (e.g. "09s2")
// Outputs:
//  Virtual transcript array (see spec for details)


//select pe.id, pe.student, pe.semester, pe.program, st.id, pe.wam from Program_enrolments pe, Semesters sem, Stream_enrolments se, Streams st where sem.id=pe.semester and pe.semester =168 and pe.student=1182627 and se.partOf=pe.id and st.id=se.stream;
function progress($db, $stuID, $term)
{  
    #echo "$stuID\n";
    #echo "$term\n";
    $partOne = array();
    
    $a =ruleName($db,12721);
    #echo "------> $a\n";

    $q='select st.id as streamid, pe.program, pe.semester, pe.student from Program_enrolments pe, Semesters sem, Stream_enrolments se, Streams st where sem.id=pe.semester and pe.semester =%d and pe.student=%d and se.partOf=pe.id and st.id=se.stream';

    $rules = dbOneTuple($db,mkSQL($q, $term, $stuID));

    $streamID = $rules[0];
    #echo "$streamID\n";
    $programID = $rules[1];
    #echo "$programID\n";

    $rules = array();
     // GE maturity
    // Maturity Level 2
    // Maturity Level 3
    // Maturity Level 4
    // Stream Requirement
    $q='select sr.rule, ru.ao_group, ru.type from Stream_rules sr, Rules ru where ru.id=sr.rule and stream=%d UNION select pr.rule, ru.ao_group, ru.type from Program_rules pr, Rules ru where ru.id=pr.rule and program=%d and type !=\'MR\' and type !=\'DS\' and type !=\'IR\' and type != \'RC\' order by type, rule';

    $rules = dbAllTuples($db,mkSQL($q, $streamID, $programID));

############ I JUST DONT UNDERSTANSD BUT IT WORKS##################
    $counter = 0;
    foreach ($rules as $rule) {
        $r = ruleName($db, $rule[0]);
        if ($r == "Stream Reqts"){
            unset($rules[$counter]);
        }
        $counter++;
    }
###################################################################
    
    $enrolments =array();
    $enrolments[0] = $programID;

   
    $t  = ruleName($db, 11466);

    foreach ($rules as $rule) {
        $r = ruleName($db, $rule[0]);
        #echo "OKOK $r\n";    
    }

    $length = count($rules);
    #echo "COUNTER: $length\n";

    
    $partTwo_temp = array();
    usort($rules, "sort_by_type");
    #print_r($rules);

    ##### find MIN MAX for each rule ######
    foreach ($rules as $rule) {
        $r = ruleName($db, $rule[0]);
        #echo "$r $rule[0]\n";
        $q = 'select min , max from Rules where id=%d';
        $minMax = dbOneTuple($db, mkSQL($q,$rule[0]));
        ##print_r($minMax);
        $temp = array();
        $temp  = array(0 => $r, 1 => $minMax[0], 2 => $minMax[1] );

        $partTwo_temp[] = $temp;   
    }

    $q = 'select * from transcript(%d, %d)';
    $tuples = dbAllTuples($db, mkSQL($q,$stuID, $term));
    #print_r($tuples);
   
    $total_WAM = 0;
    $total_UOC = 0;
   
    $total = array();
    $partTwo = array();

   #######################PART 1#########################################################
   $length = count($tuples);

   for ($i=0; $i < $length-1; $i++){
        $keys = array_keys($tuples[$i]);
        foreach ($keys as $key) {
            if(preg_match('/[a-zA-Z]+/', $key)){
               
                unset($tuples[$i][$key]);
            }
            $tuples[$i][6]=".";
        }
    }
   
    #print_r($tuples);
    for ($i=0; $i < $length-1; $i++){
        if (empty($tuples[$i][3]) and $i != $length-1){
            $tuples[$i][5]=null;
            $tuples[$i][6]="Incomplete. Does not yet count";
            #print_r($partTwo);
        }

        else{

           #find rule of the subject
            foreach ($rules as $rule) {
               # print_r($rule);
                if ($tuples[$i][3] < 50){
                   
                    $tuples[$i][6]="Failed. Does not count";    
                }

                $test = canSatisfy($db, $tuples[$i][0], $rule[0], $term);
                if(canSatisfy($db, $tuples[$i][0], $rule[0], $term)){ 
                    $subject =  $tuples[$i][0];
                    if(check_subject_rule($db, $tuples[$i][0], $rule[0])){
                        $test = ruleName($db, $rule[0]);
                        $test1 = $tuples[$i][0]; 
                        $tuples[$i][6]=ruleName($db, $rule[0]);
                    }
                    
                }
                ################## SUBJECT CAN'T SATISFY BUT THE STUDENT PASS IT --> FREE ELECT#########
                if (check_subject_rule($db, $tuples[$i][0], $rule[0]) == false or canSatisfy($db, $tuples[$i][0], $rule[0], $term) == false) {
                        # code...
                    if($tuples[$i][6] == '.'){
                         $test = ruleName($db, $rule[0]);
                        if($test == "Free elect"){
                            #echo "YEP\n";
                            $tuples[$i][6]=ruleName($db, $rule[0]);
                        }
                    }
                }
                ################## END SUBJECT CANT SATISFY BUT THE STUDENT PASS IT --> FREE ELECT#########
                
            }
        }
       $total[] = $tuples[$i]; 
    }


    $total[]= array("Overall WAM", $tuples[$length-1][3], $tuples[$length-1][5]);

###############################PART 2#####################################################
    $temp = array();
    #print_r($tuples);

    ###### HARD CODE VAR #####
    $lv1_uoc = 0; $left_uoc1 = 0;
    $lv2_uoc = 0; $left_uoc2 = 0;
    $lv3_uoc = 0; $left_uoc3 = 0;
    $free_elect_uoc = 0; $left_uoc_free_elect = 0;
    #####  END HARD CODE VAR #####
    
    #print_r($partTwo_temp);
    foreach ($partTwo_temp as $ptt) {

        foreach ($tuples as $tuple){
            if ($tuple[6] == "Incomplete. Does not yet count"){
                #echo "BAME WTF: $tuple[6]\n";
                $temp_t  = array("0 UOC so far; need $ptt[1] UOC more","$ptt[0]" );
                if (!in_array($temp_t, $temp)){
                    $temp[] = $temp_t;
                }
            }

            ################### Part 2 level 1 #############################################
            elseif ($tuple[6] == "Level 1 core" or $tuple[6] == "Level 1 Core Courses") {
                
                $counter = 0;
                foreach ($temp as $t) {
                    $search = in_array("Level 1 core", $t);
                    $search1 = in_array("Level 1 Core Courses", $t);
                    if ($search or $search1){
                       
                        #$left_uoc1=$ptt[1]-6;
                        #$lv1_uoc+=1;
                        foreach ($partTwo_temp as $pt) {
                            if ($pt[0] == "Level 1 core" or $pt[0] == "Level 1 Core Courses"){
                                #echo "IT IS HERE BOY\n";
                                if($left_uoc1 == 0){
                                    $left_uoc1 = $pt[1]-6;    
                                }
                                else{
                                    $left_uoc1 -= 6;
                                }
                                $lv1_uoc+=1;
                                #echo "FOUND\n";
                                if ($pt[0] == "Level 1 core"){
                                    $temp[$counter] = array("$lv1_uoc UOC so far; need $left_uoc1 UOC more", "Level 1 core");
                                }else{
                                    $temp[$counter] = array("$lv1_uoc UOC so far; need $left_uoc1 UOC more", "Level 1 Core Courses");
                                }
                                break;
                            }
                        }

                        $temp[$counter] = array("$lv1_uoc UOC so far; need $left_uoc1 UOC more", "Level 1 core");
                    }else{
                        
                        #echo "Not Found\n";
                    }
                    $counter++;
                    
                }
                
            }
            ################### END Part 2 level 1 #############################################

            ################### Part 2 level 2 #############################################
            elseif ($tuple[6] == "Level 2 core" or $tuple[6] == "Level 2 Core Courses") {
                $counter = 0;
                foreach ($temp as $t) {
                    $search = in_array("Level 2 core", $t);
                    if ($search and $ptt[0]){
              
                        foreach ($partTwo_temp as $pt) {
                            if ($pt[0] == "Level 2 core"){
                               
                                if($left_uoc2 == 0){
                                    $left_uoc2 = $pt[1]-6;    
                                }
                                else{
                                    $left_uoc2 -= 6;
                                }
                                $lv2_uoc+=1;
                               
                                $temp[$counter] = array("$lv2_uoc UOC so far; need $left_uoc2 UOC more", "Level 2 core");
                                break;
                            }
                        }
                        $temp[$counter] = array("$lv2_uoc UOC so far; need $left_uoc2 UOC more", "Level 2 core");
                    }else{
                        
                        #echo "Not Found\n";
                    }
                    $counter++;
                    
                }
                
            }
            ################### END Part 2 level 2 #############################################

            ################### Part 2 level 3 #############################################
                elseif ($tuple[6] == "Level 3 core" or $tuple[6] == "Level 3 Core Courses") {
                    $counter = 0;
                    foreach ($temp as $t) {
                        $search = in_array("Level 3 core", $t);
                        if ($search){
                            $left_uoc=$ptt[1]-6;
                            $lv3_uoc+=1;
                            $temp[$counter] = array("$lv3_uoc UOC so far; need $left_uoc UOC more", "Level 3 core");
                        }else{
                            #echo "Not Found\n";
                        }
                        $counter++;
                        
                    }
            }
        ################### end Part 2 level 3 #############################################

        ################### Part 2 FREE ELECT #############################################
        ###################################################################################
            if ($tuple[6] == "Free elect") {
                #echo "BAME WTFs: $tuple[6]\n";
                $counter = 0;
                foreach ($temp as $t) {
                    #print_r($ptt);
                    $search = in_array("Free elect", $t);
                    if ($search){
                        #echo "PTTT FREE ELECT: $ptt[1] $ptt[0]\n";
                        foreach ($partTwo_temp as $pt) {
                            if ($pt[0] == "Free elect"){
                                #echo "IT IS HERE BOY\n";
                                if($left_uoc_free_elect == 0){
                                    $left_uoc_free_elect = $pt[1]-6;    
                                }
                                else{
                                    $left_uoc_free_elect -= 6;
                                }
                                $free_elect_uoc+=6;
                                #echo "FOUND\n";
                                $temp[$counter] = array("$free_elect_uoc UOC so far; need $left_uoc_free_elect UOC more", "Free elect");
                                break;
                            }
                        }
                       
                        
                    }else{
                        #echo "Not Found\n";
                    }
                    $counter++;   
                }
            }
        ###################END  Part 2 FREE ELECT ############################################# 
        #######################################################################################
        }

    }
    #print_r($temp);
    foreach ($temp as $t){
        $total[] = $t;
    }
    return  $total;
}

// CHECK WHICH RULE THAT A SUBJECT FOLLOW FOR THAT PROGRAM/STREAM
// first find offeredby of a subject 
// find offeredBy of a rule
function check_subject_rule($db ,$subject, $ruleID){
    #$q = 'select * from Acad_object_groups where id = (select ao_group from Rules where id=%d)';
    $q = 'select ao_group from Rules where id=%d';
    $tuple = dbOneTuple($db, mkSQL($q,$ruleID));
    #print_r($tuple);
    $ao_group = $tuple[0];
    $check = inGroup($db, $subject, $ao_group);
    if($check == 1){
        #echo "true\n";
        return true;
    }else{
        #echo "false\n";
        return false;
    }
    #echo "$check\n";
}

function sort_by_type($a, $b){
    $sortOrder = array("CC" => 1 , "PE" => 2, "FE" => 3, "GE"=>4, "LR"=>5);
    $type_a = $a[2];
    $type_b = $b[2];
    if($sortOrder[$type_a] < $sortOrder[$type_b]){
        return -1;
    }elseif ($sortOrder[$type_a] == $sortOrder[$type_b]) {
        $t = $sortOrder["CC"];
        if($a[0] < $b[0]){
            return -1;
        }elseif ($a[0] == $b[0]) {
            return 0;
        }else{
            return 1;
        }
        return 0;
    }else{
        $t = $sortOrder["CC"];
        return 1;
    }
}


// Task E:

// E.g. $advice = advice($db, 3012345, 162, 164)
// Inputs:
//  $db = open database handle
//  $studentID = People.unswid value (i.e. unsw student id)
//  $currTermID = code for current semester (e.g. "09s2")
//  $nextTermID = code for next semester (e.g. "10s1")
// Outputs:
//  Advice array (see spec for details)

function advice($db, $studentID, $currTermID, $nextTermID)
{
    #FIND THE SEMESTEER THAT STUDENT START
    $q = 'select sem.id from Semesters sem, Program_enrolments pe where pe.semester=sem.id  and  pe.student=%d order by id';
    $list_of_sem = dbAllTuples($db, mkSQL($q, $studentID));
#    print_r($list_of_sem);
    $q='select st.id as streamid, pe.program, pe.semester, pe.student from Program_enrolments pe, Semesters sem, Stream_enrolments se, Streams st where sem.id=pe.semester and pe.semester =%d and pe.student=%d and se.partOf=pe.id and st.id=se.stream';
    $prog_stream_ids = dbOneTuple($db,mkSQL($q, $nextTermID, $studentID));
    $streamID = $prog_stream_ids[0];
    #echo $streamID;
    $programID = $prog_stream_ids[1];
    #echo "PROGRAMID: $programID\n";
    $q='select sr.rule, ru.ao_group, ru.type from Stream_rules sr, Rules ru where ru.id=sr.rule and stream=%d UNION select pr.rule, ru.ao_group, ru.type from Program_rules pr, Rules ru where ru.id=pr.rule and program=%d and type !=\'MR\' and type !=\'DS\' and type !=\'IR\' and type != \'RC\' order by type, rule';

    $rules = dbAllTuples($db,mkSQL($q, $streamID, $programID));
    
    

    $total = array();
    $free_elect_uoc = 0;
    #####################################
    #CASE THAT STUDENT HAVENT STARTED YET
    #####################################
    usort($rules, 'sort_by_type');
    #print_r($rules);
    
    if($currTermID < $list_of_sem[0][0]){
        $q = 'select * from transcript(%d, %d)';
        $tuples = dbAllTuples($db, mkSQL($q, $studentID, $nextTermID));
        
        foreach ($rules as $rule) {

            $q = 'select ao_group, name from rules where id=%d';

            #ao_group contains [0]-> ao_group id [1]->rulename
            $ao_group = dbOneTuple($db, mkSQL($q, $rule[0]));
            #FIND ALL subject memer of that ao_groups;
            $subject_members = membersOf($db,$ao_group[0]);
            // print_r($subject_members);
            $count_core = 0;
            if($ao_group[1] == "Level 1 Core Courses" or $ao_group[1] == "Level 1 core"){
                
                #echo "COUNT_CORE: $count_core\n";
                foreach ($subject_members[1] as $sm) {

                    if($count_core < 5){
                        
                        $q = 'select * from Subject_prereqs where subject=(select id from Subjects where code=%s)';

                        $sub_name_uoc = 'select name, uoc from Subjects where code=%s';
                        
                        $sub_name_uoc = dbOneTuple($db, mkSQL($sub_name_uoc, $sm));
                        
                        $prereq = dbOneTuple($db,mkSQL($q, $sm));
                        
                        if (empty($prereq)){
                            
                            $total[] = array($sm,$sub_name_uoc[0],$sub_name_uoc[1], $ao_group[1]);
                            $count_core++;
                        }
                    }
                         
                }
                    
            }
            
            if(ruleName($db,$rule[0]) == "Free elect"){
                $q = 'select min,max from Rules where id=%d';
                $min_max = dbOneTuple($db,mkSQL($q,$rule[0]));
                $total[] = array("Free....", "Free Electives (many choices)", $min_max[0], "Free elect");
            }
            
        }
    }

    #######################################################################################################
    #######STUDENT ALREADY STUDIED#########################################################################
    ########################################################################################################
    
    else{
        $q = 'select * from transcript(%d, %d)';
        $tuples = dbAllTuples($db, mkSQL($q, $studentID, $currTermID));
        #print_r($tuples);

        #######GET ALL SUBJECT ENROLMENTS IN THE PAST#######
        $course_enrolled = array(); 
        $tuples_length = count($tuples);
        $counter = 0 ;
        foreach ($tuples as $tuple) {
            if ($counter < $tuples_length - 1){
                $course_enrolled[] = array($tuple[0], $tuple[2], $tuple[5]);
            }
        }
        #print_r($course_enrolled);
       
        ####### DONE GET ALL SUBJECT ENROLMENTS IN THE PAST#######
        $count_core = 0;
        $free_elect_uoc = 0;
        foreach ($rules as $rule) {
            $q = 'select ao_group, name, type from rules where id=%d';

            #ao_group contains [0]-> ao_group id [1]->rulename
            $ao_group = dbOneTuple($db, mkSQL($q, $rule[0]));
            $subject_members = membersOf($db,$ao_group[0]);
           
            #LET CHECK WITH LEVEL 1 CORE COURSES FIRST
            if($ao_group[1] == "Level 1 Core Courses" or $ao_group[1]=="Level 1 core"){

                foreach ($subject_members[1] as $sm) {
                    if($count_core < 5){
                        $q = 'select * from Subject_prereqs where subject=(select id from Subjects where code=%s)';

                        $sub_name_uoc = 'select name, uoc from Subjects where code=%s';
                        
                        $sub_name_uoc = dbOneTuple($db, mkSQL($sub_name_uoc, $sm));
                        
                        $prereq = dbOneTuple($db,mkSQL($q, $sm));
                        
                        if (empty($prereq) and checkCourse($sm, $course_enrolled) == false){
                           
                            $check = checkExcluded($db, $sm,$course_enrolled);
                            if($check == false){
                                             
                                $total[] = array($sm,$sub_name_uoc[0],$sub_name_uoc[1], $ao_group[1]);
                                $count_core++;
                            }

                        }else{
                           
                            $check = checkCourse($sm, $course_enrolled);
                            if($check  == false){
                                if(checkExcluded($db, $sm,$course_enrolled) == false){
                                
                                    $total[] = array($sm,$sub_name_uoc[0],$sub_name_uoc[1], $ao_group[1]);
                                    $count_core++;
                                }
                            }
                        }
                    

                    }
                         
                }

            }
            elseif ($ao_group[1] == "Level 2 Core Courses" or $ao_group[1]=="Level 2 core") {
                foreach ($subject_members[1] as $sm) {
                    if($count_core < 5){
                        $q = 'select * from Subject_prereqs where subject=(select id from Subjects where code=%s)';

                        $sub_name_uoc = 'select name, uoc from Subjects where code=%s';
                        
                        $sub_name_uoc = dbOneTuple($db, mkSQL($sub_name_uoc, $sm));
                        
                        $prereq = dbOneTuple($db,mkSQL($q, $sm));
                        
                        if (empty($prereq) and checkCourse($sm, $course_enrolled) == false){
                            
                            $check = checkExcluded($db, $sm,$course_enrolled);
                            if($check == false){
                                
                                $total[] = array($sm,$sub_name_uoc[0],$sub_name_uoc[1], $ao_group[1]);
                                $count_core++;
                            }

                        }else{
                              
                            $check = checkCourse($sm, $course_enrolled); #check if a course is enrolled before or not
                            if($check  == false){
                                
                               
                                ######CHECK IF THE COURSE MEET PREREQ #######
                                $check_prereq = check_prereq($db, $course_enrolled, $prereq[2]);
                                if($check_prereq == true){
                                     if($count_core<5){
                                        $total[] = array($sm,$sub_name_uoc[0],$sub_name_uoc[1], $ao_group[1]);
                                        $count_core++;
                                    }
                                }
                                
                            }
                        }
                    

                    }
                         
                }
            }

            elseif ($ao_group[1] == "Level 3 Core Courses" or $ao_group[1]=="Level 3 core") {
                   
            }

            elseif ($ao_group[1] == "Level 4 Core Courses" or $ao_group[1] == "Level 4 core") {
                # code...
            }

            elseif ($ao_group[1] == "Free elect") {
               
            }


        ############### FREE ELECT UOC CALCULATE###########################
        
        $free_elect_uoc =  isElective($db, $course_enrolled, $rules) * 6;
        $gened_uoc = isGen($db, $course_enrolled, $rules) * 6;
        #echo "FUCKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK $gened_uoc\n";
        if($ao_group[2] == "FE"){
            #echo "FUCKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK\n";
            $q = 'select min,max from Rules where id=%d';
            $min_max = dbOneTuple($db,mkSQL($q,$rule[0]));
            #print_r($min_max);
            $total_uoc = count_uoc_taken($db, $course_enrolled);
            #echo "UOCCCCCCCC: $total_uoc\n";
            $total[] = array("Free....", "Free Electives (many choices)", $min_max[0]-$free_elect_uoc, ruleName($db,$rule[0]));
        }
        if($total_uoc > 48){
            if($ao_group[2] == "GE"){
                $q = 'select min,max from Rules where id=%d';
                $min_max = dbOneTuple($db,mkSQL($q,$rule[0]));

                $total[] = array("GenEd...","General Education (many choices)", $min_max[0]-$gened_uoc, ruleName($db,$rule[0]));
            }
        }
        }

    }
    return $total;
}

function count_uoc_taken($db, $course_enrolled){
    $q = 'select uoc from Subjects where code=%s';
    $total_uoc = 0;
    foreach ($course_enrolled as $course) {
        $total_uoc+=$course[2];
    }
    return $total_uoc;
}

#enrolments : all the courses that student enrolled before
#prereq_rule: subject_prereq rule for that course
function check_prereq($db, $enrolments, $prereq_rule){
    $q = 'select ao_group from Rules where id=%d';
    $ao_group = dbOneTuple($db, mkSQL($q, $prereq_rule));
    $prereq_course_list = membersOf($db, $ao_group[0]);
    foreach ($prereq_course_list[1] as $course) {
        $check = checkCourse($course, $enrolments);
        if($check == true){
            return true; ######TRUE IS YES IT MEET THE REQUIREMENT
        }
    }
    return false;
}

#check if a course is enrolled before or not
# true is yes it is enrolled before /// false means nope not yet should enrol next sem
function checkCourse($course_code, $enrolments){
    foreach ($enrolments as $e) {
        $check = strcmp($course_code, $e);
        if ($course_code == $e[0]){
            return true;
        }
    }
    return false;
}

function checkExcluded($db, $course_code, $enrolments){
    $q = 'select excluded from Subjects where code=%s';
    $excluded = dbOneTuple($db,mkSQL($q, $course_code));
    $all_equi_subs = membersOf($db,$excluded[0]);
    $counter = 0; 
    foreach ($all_equi_subs[1] as $s) {
        $check = checkCourse($s, $enrolments);
        if ($check == true){
            return true;
        }
    }
    return false;
}

function isElective($db, $course_enrolled, $rules){
    $level1 = array();
    $level2 = array();
    $level3 = array();
    $free_elect = array();

    foreach ($rules as $rule) {
        $q = 'select ao_group, name from rules where id=%d';
        $ao_group = dbOneTuple($db, mkSQL($q, $rule[0]));
        if($ao_group[1] == "Level 1 Core Courses" or $ao_group[1] == "Level 1 core"){
            foreach ($course_enrolled as $ce) {
                if(inGroup($db, $ce[0], $ao_group[0])){
                    array_push($level1, $ce[0]);
                                        
                }
                        
            }
        }

        elseif ($ao_group[1] == "Level 2 Core Courses" or $ao_group[1]=="Level 2 core") {
            foreach ($course_enrolled as $ce) {
                if(inGroup($db, $ce[0], $ao_group[0])){
                    array_push($level1, $ce[0]);
                                        
                }
                        
            }
        }

        else{
            foreach ($course_enrolled as $ce) {
                if(!in_array($ce[0], $level1) and !preg_match('/GEN...../', $ce[0])){
                    if(!in_array($ce[0], $free_elect)){
                        array_push($free_elect, $ce[0]);
                    }
                }
            } 
        }
    }
  
    return count($free_elect)-1;
}


function isGen($db, $course_enrolled, $rules){
    $level1 = array();
    $level2 = array();
    $level3 = array();
    $free_elect = array();
    $gened = array();
    foreach ($rules as $rule) {
        $q = 'select ao_group, name from rules where id=%d';
        $ao_group = dbOneTuple($db, mkSQL($q, $rule[0]));
        #print_r($ao_group);
        if($ao_group[1] == "Level 1 Core Courses" or $ao_group[1] == "Level 1 core"){
            foreach ($course_enrolled as $ce) {
                if(inGroup($db, $ce[0], $ao_group[0])){
                    array_push($level1, $ce[0]);
                                        
                }
                        
            }
        }

        elseif ($ao_group[1] == "Level 2 Core Courses" or $ao_group[1]=="Level 2 core") {
            foreach ($course_enrolled as $ce) {
                if(inGroup($db, $ce[0], $ao_group[0])){
                    array_push($level1, $ce[0]);
                                        
                }
                        
            }
        }

        elseif($ao_group[1] == "GE"){

            foreach ($course_enrolled as $ce) {
                if(!in_array($ce[0], $level1) and preg_match('/GEN...../', $ce[0])){
                    if(!in_array($ce[0], $gened)){
                        array_push($gened, $ce[0]);
                    }
                }
            }

        }

        else{
            foreach ($course_enrolled as $ce) {
                if(!in_array($ce[0], $level1) and !preg_match('/GEN...../', $ce[0])){
                    if(!in_array($ce[0], $free_elect)){
                        array_push($free_elect, $ce[0]);
                    }
                }
            }
            
        }
    }
  
    return count($gened);
}

?>
