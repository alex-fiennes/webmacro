#!/usr/local/bin/perl
# * ====================================================================
# * Copyright (c) 1995,1996,1997,1998 The PEN Project.  All rights reserved.
# *
# * This product was developed as part of the ParlEuNet (PEN) project, by
# * the STA unit of the nstitute for Safety Informatics and Systems of the
# * Joint Research Centre of the European Communities.
# *
# * More information about us; and this software product can
# * be found on http://sta.jrc.it
# * 
# * With this file you should have received a file called
# * 'License'. If not you can pick one up from
# * 
# *	http://www.ceo.org/software/licence.txt
# * 	ftp://ftp.ceo.org/software/licence.txt
# *
# * Redistribution and use in source and binary forms, with or without
# * modification, are permitted provided that the following conditions
# * are met:
# *
# * 1. Redistributions of source code must retain the above copyright
# *    notice, this list of conditions and the following disclaimer. 
# *
# * 2. Redistributions in binary form must reproduce the above copyright
# *    notice, this list of conditions and the following disclaimer in
# *    the documentation and/or other materials provided with the
# *    distribution.
# *
# * 3. All materials mentioning features or use of this
# *    software must display the following acknowledgment:
# *    "This product includes software developed by the PEN Project
# *	at ISIS/JRC (http://sta.jrc.it/)."
# *
# * 4. The name of the Copyright holder must not be used to
# *    endorse or promote products derived from this software without
# *    prior written permission.
# *
# * 5. Redistributions of any form whatsoever must retain the following
# *    acknowledgment:
# *    "This product includes software developed by the PEN Project
# *    at ISIS/JRC (http://sta.jrc.it/)."
# *
# * THIS SOFTWARE IS PROVIDED BY THE PEN PROJECT ``AS IS'' AND ANY
# * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
# * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE PEN PROJECT OR
# * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
# * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
# * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
# * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
# * OF THE POSSIBILITY OF SUCH DAMAGE.
# * ====================================================================
# *
#
# Very early experimental hack of XML template parsing. Purely to see if
# we can collaborate or somehow find a way of getting further with this.
#
# Contact: dirk.vangulik@jrc.it, alberto.reggiori@jrc.it
# Version 0.00 - November 1999.
#
# Calling Sequence
#	
#  databse -(sql)-> web application -(xml)-> PARSER -(html)-> netscape
#	                  |                   |  |      
#               input from templates, the CGI server env, etc.
#
# require "tmp.pl";
# $meta=str();
# use Data::Dumper;
# use XM::Parser;
# $b=rcd();
#
#$context->XML::parse( <<'EOM');
#<message>
#  <title>Your Personal Details</>
#  <subject>Re: Observations made during your abcense</>
#  <list>
#    <name>Dirk</>
#    </>
#  <list>
#    <name>Ardy</>
#    </>
#  </>
#EOM

$trace=0;
$debug=0;
$|=1;
 
# Just ct'n'paste it in; as the XML module is not
# quite public/finished.
#
$context = {
	'>' => 'message',
	title => 'Your Personal Details',
	name => "Asseswaar",
	subject => "Re: Observations made during your abcense",
	body => 'No body parts where savaged during the appraisal',
	list => [
		   { name => 'dirk', town => 'Leeuwarden' },
		   { name => 'ardy', town => 'Harmelen' },
		   { name => 'alberto', town => 'Monbello' }
		]
	};

# safes us reading a template !
#
$test2=<<'EOM';
<html>
<title> $title </title>
<body bgcolor="white">
<h1>$title</h1>
We have indentified you as
<p>
<blockquote><i>$name</i></blockquote>
<p>
and we have made note of the folowing
visits and/or people:
<table>
#foreach who in $list
{
   <tr>
	<td align=left>$who.name</td>
	<td align=right>$who.town</td>
   </tr>
}
</table>
<hr>
Last updated: $DATE 
EOM

# print &parse_file($context,'test2');
print &parse_string($context,$test2);
exit;

sub parse_file {
	my $data = shift;
	my $file = shift;
	local @list = &suck($file); # should be passed !

	return &_start_parse($data,$file);
	};

sub parse_string {
	my $data = shift;
	my $string = shift;

	local @list = ();
	map {
		$_.="\n";
		push @list, $_;
		} split(/\n/,$string);

	return &_start_parse($data,undef);
	};

# No public parts beyond this line (or hair for that matter).
#
sub _start_parse {
	my $data = shift;
	my $source = shift;

	my $context = {
	   data => $data,
	   nxt => {
		data => {
			DATE => scalar(gmtime(time)),
			GMDATE => scalar(gmtime(time)),
			LOCALDATE => scalar(localtime(time)),
			%ENV,
			dunno => 'yet'
			}
		}
	   };

	if (defined($source)) {
		$context->{nxt}->{data}->{SOURCE} = 'FILE';
		$context->{nxt}->{data}->{FILE} = $source;
		}
	else {
		$context->{nxt}->{data}->{SOURCE} = 'STRING';
		};
		
	my $at = 0;
	return &_parse($context,0,\$at,0,0);
	}

sub suck {
	my $file = shift;
	my @list=();

	open(FH,'<'.$file) 
		or die "Could not open $file; $!";

	while(<FH>) {
		push @list,$_;
		};

	close(FH);
	return @list; 
	};

sub _parse {
my $c= shift;
my $context; $context->{nxt} = $c;

my $depth = shift; # no need, just for debugging !
my $atp = shift;
my $nop = shift;
my $block = shift;

print STDERR (' ' x $depth)." parse\n" 	
	if $debug;

my $out = '';

while( $$atp <= $#list ) {
   my $line = $list[ $$atp ]; 
   ($$atp)++;
   print STDERR (' ' x $depth)." line $$atp\n" 	if $debug > 1;
   if ($line =~ s/^#(\w+)\s*//) {
	chop $line;
	my $cmd=$1;
        print STDERR (' ' x $depth)." $$atp:$cmd\n" 	if $debug;
	if ($cmd eq 'if') {
		$out .= "<!-- $depth if -->\n";
		# Extract
		if (1) {
			$out .= &_parse($context,$depth+1,$atp,$nop,1);
			};
		# now is there an else ?
		if (($list[ $$atp -1 ] =~ m/else/) && (1)) {
			$out .= "<!-- $depth else -->\n";
			$out .= &_parse($context,$depth+1,$atp,$nop,1);
			};
		$out .= "<!-- $depth endif -->\n"	if $trace > 1;
		}
	elsif ($cmd eq 'foreach') {
		my $t;
		# expect something like foreach $value in list
		# followed by a '{' '}' loop
		#
		# Define/Get the lvalue
		my($lvalue,$in,@other)=split(/\s+/,$line);

		die "No 'in' as in foreach LV in LIST at $."
			unless ($in eq 'in');

		my(@loop)=();
		foreach(@other) {
			if (s/^\$//) {
				push @loop,&getval($context, $_);
				}
			else {
				push @loop,&evaluate($context, $_);
				};
			};
		@other=();

		die "List empty"
			if $#loop < 0;

		if (1) {
			die "No starting { at $$atp" unless $list[ $$atp ]=~ m/^\s*{/;
			($$atp)++;
			};

		$out .= "<!-- $depth started for loop -->\n"	if $trace > 1;

		my $loop;
		my $loop_context;
		$loop_context->{nxt}=$context; 
		while($#loop >= 0) {
			$loop_context->{data}->{ $lvalue }->{data} 
				= shift @loop;
			# loop contains a ptr to the value of '$lvalue'.
			#
			$t = $$atp;
			$out .= &_parse($loop_context, $depth+1,\$t,$nop,1);
			$out .= "<!-- $depth next $$atp $t -->\n" if $trace > 2;
			};
		
		$out .= "<!-- $depth done with loop -->\n" if $trace > 1;
		$$atp = $t;
		}
	elsif ($cmd eq 'include') {
		while ($line =~ s/(\S+)\s*//) { 
			my $file = $1;
			$out .= join('',&suck($file));
			};
		}
	elsif ($cmd eq 'parse') {
		# XXX multiple or just one ?
		#
		print "Enter $line\n";
		while (1) {
			last unless
				$line =~ s/(\S+)\s*//;
			my $file = $1;
			print "Parsing $file - left '$line'\n";
			$out .= &parse_file($context->{data},$file);
			};
		print "Done\n";
		}
	elsif ($cmd eq 'set') {
		# ignore multi line issues...
		# asume set lvalue = something
		$line =~ s/(\w+)\s*\=\s*// 
			or die "Does not look like set lvalue=something at $.";

		my $lvalue = $1;
		$context->{data}->{$lvalue} = 
			&evaluate( $context, $line );
		}
	elsif ($cmd eq 'script') {
		$line =~ s/^\s*([\'\"]{1})([^\1]*)\1\s*//
			or die "No script type at $. '$_'";

		my $type =$2;

		$line =~ s/boundary=([\"\']{1})([^\1]*)\1\s*//i
			or die "No end boundary specified... at $. '$_'";

		my $bound = $2;
		if ($type eq 'unparsed') {
			while( $_ = shift @$list ) {
				last if m/$bound/;
				};
			}
		elsif ($type eq 'perl') {
			warn "not yet";
			}
		else {
			die "script type '$type' not supported";
			};
		}
	else {
		die "Uknown cmd '$cmd' at $.";
		};
	}
    elsif ($block==1 && $line =~ s/^\s*}\s*// ) {
	return $out;
	}
    else {
	if ($nop == 1) {
		# it is a non executed block..
		# ignore..
		$out .= "<-- $depth line ignored -->\n";
		}
	else {
		$out .= &evaluate( $context, $line );
		};
	};
    };
return $out;
};

# return what has _NOT_ been parsed as 
# a valid expression
# currently we only do () or single expressions, i.e
# untill a {.
#
sub expression {
	my $bracked = shift;
	my $string = shift;
	my $result;
	my $atom= 1;	 # 1=atom 0=operator
	$string =~ s/^\s+//g;
	while(1) {
		if ($string =~ s/^\(\s*//g) {
			my $r;
			($string,$r) = &expression(1,$string);
			push @ops, $r;
			}
		if ($string = s/\)\s*//g) {
			last if $bracked;
			die "Unexpected ) at $.";
			}
		elsif ($string =~ s/^\$(\w+)\s*//) {
			my $v = $1;
			die "Operator expected" if $atom;
			push @ops,$v;
			$atom = 0;
			}
		elsif ($string =~ s/^(\w+)\s*//) {
			my $v = $1;
			die "Value expected" 
				if $atom and $v ne 'not';
			die "Not a known operator"
				unless grep(/$v/,('and','or','not'));
			push @ops,$v;
			$atom = 1;
			}
		elsif ($string =~ /^\s*$/) {
			last;
			}
		else {
			die "Expresion '$string' not understoof at $.";
			};
		};

	if ($atom) {
		# we are still an atom short !
		die "Incomplete expression at $.";
		};

	# parse the experssion we have 
	# sofar; which is a simple sequence
	# of either 'and', 'or', etc..
	#
	$expr = join(' ',@ops);

#	my $result = eval $expr;
#	return ($string, $result);
	return ($string, $expr );
	};
	
sub evaluate {
	my $context = shift;
	my $line = shift;
	$line =~ s/\$([\.\w]+)/&getval($context,$1)/eg;
	return $line;
	};

sub getval {
	my $context = shift;
	my $who = shift;
	my @who = split( /\./,$who);
	my $p = $context;

	return undef 
		unless defined $p;

#	print "getval $who $context\n";

	# Go as deep as we have to, to get
	# a proper value.
	#
	my $q;
outer:	while(1) {
		$q = $p;
		my $i = 0;
inner:		foreach $i (0 .. $#who) {
			last inner 
				unless defined $q->{data}->{ $who[ $i ] };

			$q=$q->{ data }->{ $who[ $i ] };

			last outer 
				if $i == $#who;
			};

		return '<!-- value $'. $who .' not found-->'
			unless defined $p->{nxt};

		$p = $p->{nxt};
		};

	$p = $q;

	# What are we pointing at
	#
	if (!ref($p)) {
		return $p;
		} 
	elsif (ref($p) eq 'SCALAR') {
		return $$p;
		}
	elsif (ref($p) eq 'ARRAY') {
		return wantsarray ? @{ $p } : join(' ',&variant(@$p));
		}
	elsif (ref($p) eq 'HASH') {
		return &variant($p);
		}
	else {
		return '<!-- value $'.$who.' is of '.ref($p).' type -->';
		};
	return undef;
	};

# Either a pointer to a scalar, or a hash of scalars.
#
sub variant {
	my $p; my  @out;
	foreach $p (@_) {
		my $t = ref($p);
		if ($t eq 'SCALAR') {
			push @out,$t;
			}
		elsif ($t eq 'HASH') {
			die "Not a hash2scalar pointer"
				unless !ref($t->{en});
			push @out,$t->{en};
			}
		else {
			die "Did not expect this";
			};
		};
	return @out;
	};


