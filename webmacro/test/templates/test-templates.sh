#/bin/sh
TEST_DIR=$1
CLASS_DIR=$2
export CLASSPATH=$CLASS_DIR:$CLASSPATH
for dir in `find $TEST_DIR -type d -maxdepth 1 -mindepth 1`; do 
    if [ $dir/TestTemplate.java -nt $dir/TestTemplate.class ]; then
        javac -d $dir $dir/TestTemplate.java || exit;
    fi
    for t in `find $dir -type f -name \*.wm -maxdepth 1`; do
        CLASSPATH=$dir:$CLASSPATH java -Dorg.webmacro.LogLevel=WARNING TestTemplate $t > $t.out || exit;
    if [ ! -f "$t.baseline" ]; then
        echo "No baseline file $t.baseline";
    else
        diff -x CVS -x "\*~" $t.out $t.baseline || exit;
    fi
    done
done
