#! /usr/bin/perl -w -i

while(<>) {
   print;
   if (m/^<!--TAIL-->/) {
      last;
   }
}

open(TAIL, "TAIL") || die "cannot open tail";
while (<TAIL>) {
   print;
}
