#!/usr/bin/perl -w

# written by andrewt@cse.unsw.edu.au October 2017
# as a starting point for COMP[29]041 assignment 2
# https://cgi.cse.unsw.edu.au/~cs2041/assignments/UNSWtalk/

use CGI qw/:all/;
use CGI::Carp qw/fatalsToBrowser warningsToBrowser/;


sub main() {
    # print start of HTML ASAP to assist debugging if there is an error in the script
    print page_header();
    
    # Now tell CGI::Carp to embed any warning in HTML
    warningsToBrowser(1);
    
    # define some global variables
    $students_dir = "dataset-medium";
    
    print student_page();
    print page_trailer();
}


#
# Show unformatted details for student "n".
# Increment parameter n and store it as a hidden variable
#
sub student_page {
    my $n = param('n') || 0;
    my @students = sort(glob("$students_dir/*"));
    my $student_to_show  = $students[$n % @students];
    my $details_filename = "$student_to_show/student.txt";
    open my $p, "$details_filename" or die "can not open $details_filename: $!";
    $details = join '', <$p>;
    close $p;
    my $next_student = $n + 1;
    return <<eof
<div class="unswtalk_student_details">
$details
</div>
<p>
<form method="POST" action="">
    <input type="hidden" name="n" value="$next_student">
    <input type="submit" value="Next student" class="unswtalk_button">
</form>
eof
}


#
# HTML placed at the top of every page
#
sub page_header {
    return <<eof
Content-Type: text/html;charset=utf-8

<!DOCTYPE html>
<html lang="en">
<head>
<title>unswtalk</title>
<link href="UNSWtalk.css" rel="stylesheet">
</head>
<body>
<div class="unswtalk_heading">
UNSWtalk Blah Blah
</div>
eof
}


#
# HTML placed at the bottom of every page
# It includes all supplied parameter values as a HTML comment
#
sub page_trailer {
    my $html = "<!--\n";
	foreach $param (param()) {
	    my $value = param($param);
	    $value =~ s/\>/\&gt;/g;
	    $html .= "    $param='$value'\n"
	}
	$html .= "-->\n";
    $html .= "</body>\n";
    $html .= "</html>\n";
    return $html;
}

main();
