<?php 
  $big=array();
  $big[]  = array(0 => 12851, 1 => 3709, 2 => "CC");
  $big[]  = array(0 => 12852, 1 => 3712, 2 => "CC");
  $big[]  = array(0 => 12853, 1 => 3713, 2 => "CC");
  $big[]  = array(0 => 12854, 1 => 3714, 2 => "CC");
  $big[]  = array(0 => 11462, 1 => 5276, 2 => "FE");
  $big[]  = array(0 => 11461, 1 => 5275, 2 => "GE");
  $big[]  = array(0 => 12855, 1 => 7830, 2 => "PE");

  print_r($big);
  usort($big, 'sort_by_type');
  echo "AFTER SORT\n";
  print_r($big);
  function sort_by_type($a, $b){
    $sortOrder = array("CC" => 1 , "PE" => 2, "FE" => 3, "GE"=>4, "LR"=>5);
   
    echo "$a[2] is compare to $b[2]\n";
    $type_a = $a[2];
    $type_b = $b[2];
    if($sortOrder[$type_a] < $sortOrder[$type_b]){
        echo "$a[2] $sortOrder[$type_a] is compare to $b[2] $sortOrder[$type_b] and less than\n";
        return -1;
    }elseif ($sortOrder[$type_a] == $sortOrder[$type_b]) {
        $t = $sortOrder["CC"];
        echo "$a[2]  $sortOrder[$type_a] is compare to $b[2] $sortOrder[$type_b] and equal\n";
        if($a[0] < $b[0]){
            echo "FLAG $a[0] is compare to $b[0] less\n";
            return -1;
        }elseif ($a[0] == $b[0]) {
            echo "FLAG $a[0] is compare to $b[0] equa;\n";
            return 0;
        }else{
            echo "FLAG $a[0] is compare to $b[0] greater\n";
            return 1;
        }
        return 0;
    }else{
        $t = $sortOrder["CC"];
        echo "$a[2] $sortOrder[$type_a] is compare to $b[2] $sortOrder[$type_b] and greater than\n";
        return 1;
    }
}

 ?>