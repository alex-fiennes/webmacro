#! /bin/sh
#
# compare all templates in subdirectories
# to their baseline.
#
# automagically compiles and runs subdirectory/TestTemplate.java
# and runs each template in subdirectory/ through the class file
# saving it's output
#
# on the first run of each new template, you should manually
# examine the output, and if the output is correct, rename
# the file to <template-filename>.baseline
#
# USAGE
#	test-templates.sh <root-dir> [clean]
#	if "clean" is specified, source files will be recompiled
#


export ERROR=0

if [ x$1 = "x" ]; then
	echo "Usage:" 1>&2;
	echo "     test-templates.sh <root-dir> [clean]" 1>&2;
	exit 1;
fi

ROOT_DIR=$1

# if arg 2 == "clean", then nuke all existing .class files
if [ x$2 = "xclean" ]; then
	find $ROOT_DIR -name "*.class" -exec rm -f {} \;
	find $ROOT_DIR -name "*.wm.out" -exec rm -f {} \;
fi

# compile base classes if they don't exist 
if [ ! -f "$ROOT_DIR/TemplateEvaluatorMain.class" -o "$ROOT_DIR/TemplateEvaluatorMain.java" -nt "$ROOT_DIR/TemplateEvaluatorMain.class" ]; then
	echo "Compiling base test classes" 1>&2;
	javac -classpath $ROOT_DIR:$CLASSPATH -d $ROOT_DIR $ROOT_DIR/TemplateEvaluatorMain.java
fi

# walk all the directories, ignoring CVS
for dir in `find $ROOT_DIR -type d -maxdepth 1 -mindepth 1 | grep -v CVS`; do
	# compile the TestTemplate.java file if it exists in this directory
	if [ -f $dir/TestTemplate.java ]; then
		if [ ! -f "$dir/TestTemplate.class" -o "$dir/TestTemplate.java" -nt "$dir/TestTemplate.class" ]; 
                then
                    echo "Compiling $dir/TestTemplate.java" 1>&2;
                    javac -classpath $dir:$ROOT_DIR:$CLASSPATH -d $dir $dir/TestTemplate.java || exit;
		fi
	fi

	if [ -f $dir/TestTemplate.class ]; then
                echo "Entering test directory $dir" 1>&2;
		# run each template in this dir through our template evaluator
		for template in `find $dir -type f -name \*.wm -maxdepth 1`; do
			java -classpath $dir:$ROOT_DIR:$CLASSPATH -Dorg.webmacro.LogLevel=NONE TemplateEvaluatorMain TestTemplate `basename $template` > $template.out || exit;

			if [ ! -f "$template.baseline" ]; then
				echo "WARNING: No baseline for $template." 1>&2;
			else
                                echo "Comparing  `basename $template`" 1>&2;
				diff -x CVS -x "\*~" $template.out $template.baseline || ERROR=1
			fi
		done
	fi
done
exit $ERROR;
