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
# The "WebMacro.defaults" file in this directory is the
# one used to configure this testing system.
#
# USAGE
#	test-templates.sh <path-to-webmacro.jar>
#

if [ x$1 = "x" ]; then
	echo "Usage:"
	echo "     test-templates.sh <path-to-webmacro.jar>"
	exit 1;
fi

WEBMACRO_JAR=$1
ROOT_DIR=`pwd`

# compile base classes if they don't exist 
if [ ! -f "$ROOT_DIR/TemplateEvaluatorMain.class" ]; then
	echo "Compiling base classes for testing"
	javac -classpath $ROOT_DIR:$WEBMACRO_JAR -d $ROOT_DIR $ROOT_DIR/*.java
fi

# walk all the directories, ignoring CVS
for dir in `find ./ -type d -maxdepth 1 -mindepth 1 | grep -v CVS`; do
	# compile the TestTemplate.java file
	# if it exists in this directory
	if [ $dir/TestTemplate.java -nt $dir/TestTemplate.class ]; then
		echo "Compiling $dir/TestTemplate.java"
		javac -classpath $dir:$ROOT_DIR:$WEBMACRO_JAR -d $dir $dir/TestTemplate.java || exit;
	fi

	if [ -f $dir/TestTemplate.class ]; then

		# run each template in this dir through our template evaluator
		for template in `find $dir -type f -name \*.wm -maxdepth 1`; do
			java -classpath $dir:$ROOT_DIR:$WEBMACRO_JAR -Dorg.webmacro.LogLevel=WARNING TemplateEvaluatorMain TestTemplate $template > $template.out || exit;

			if [ ! -f "$template.baseline" ]; then
				echo "No baseline for $template.";
				echo "You should examine $template.out and copy to $template.baseline if it is correct";
				echo 
			else
				diff -x CVS -x "\*~" $template.out $template.baseline || exit
			fi
		done
	fi
done
exit 0;
