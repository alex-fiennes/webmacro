#! /usr/local/bin/perl

$inAList = 0;
$lastPara = 0;

$header = 1;
$output = 0;

while (<>) {

   chop;

   $pre = $post = "";

   s/	/        /g;

   if (m/^\s*$/) {
      if (! $lastPara) {
        $pre = "<p>$pre";
        $lastPara = 1;
      }
   } else {
      $lastPara = 0;
   }

   if (m/^[A-Z0-9\.\,\;\:\-\'\"\$\(\) ]+$/) {
      if (m/\S/) {
         s/^\s*//;
         $pre = "$pre<H$header>";
         $post = "</h$header>$post";
         $lastPara = 1;
      }
   }

   if (m/^                    *\S/) {
      s/^ *//;
      s/ *$//;
      $pre = "<center><h$header>$pre";
      $post = "$post</h$header></center>";
      if ($output == 0) {
         print "<HTML><head><title>$_</title></head><body bgcolor=white>\n";
      }
      if ($header < 3) {
         $header++;
      }
   } elsif (m/^\s+([a-zA-Z])[\)]/) {
     m/^(\s+)\S/;
     $indent = $1;
     $listChar = $1;
     $pre = "<li>$pre";
     s/^\s+\(?[a-zA-Z]\)//;
     if (! $inAlist) {
        $inAlist = 1;
        $pre = "<ol type=a>\n$pre";
     } 
  }  elsif ($inAlist) {
     if (m/^$indent/) {
        # still in the list
     } elsif (m/\S/) {
        $pre = "</ol>\n$pre";
        $inAlist = 0;
     } 
  }

  if ($output || m/\S/) {
   $output++;
   print "$pre$_$post\n";
  }
}

print "</body></html>\n";

