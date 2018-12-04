#echo "$stuID\n";
	#echo "$term\n";
   // $q = 'select ppl.unswid from Students stud , People ppl where ppl.id=stud.id and stud.id=%d';
   #$tuple = dbOneTuple($db, mkSQL($q, $stuID));
   #$unswID = $tuple[0];
   
   #echo "dd: $tuple\n";
#   print_r($tuple);
   #transcipt($stuID, $term);
   
   #$a =ruleName($db,10384);
   #echo "$a\n";
   
   $q = 'select * from transcript(%d, %d)';
   $tuples = dbAllTuples($db, mkSQL($q,$stuID, $term));
   print_r($tuples);
   #$tuples = transcipt($stud, $term);
   // echo "$tuples\n";

   #$keys = array_keys($tuples);
   $length = count($tuples);

   echo "$length\n";
   for ($i=0; $i < $length; $i++){
   		$keys = array_keys($tuples[$i]);
   		foreach ($keys as $key) {
   		 	if(preg_match('/[a-zA-Z]+/', $key)){
   				unset($tuples[$i][$key]);
   			}
   			$tuples[$i][6]=".";
   		}
   		if (empty($tuples[$i][3]) and $i != $length-1){
   			$tuples[$i][5]=null;
   			$tuples[$i][6]="Incomplete. Does not yet count";
   		} 
   }

   #print_r($tuples);
   $tuples[$length-1][2]="Overall WAM";
   if (empty($tuples[$length-1][3])){
   		echo "BAME\n";
   		$tuples[$length-1][3] = 'None';
   }
   #$tuples[$length-1][6]="dwqd";

   #print_r($array);
   #return $tuples;
