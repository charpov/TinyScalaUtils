#!/usr/bin/env sh
set -eu

readonly docs=~/GIT/TinyScalaUtils/docs
readonly from=$docs/maven-add/com/github/charpov
readonly to=$docs/maven/com/github/charpov

JAVA_HOME="$(/usr/libexec/java_home -v11)"
export JAVA_HOME

rm -rf $docs/maven-add
sbt 'clean; publish'
git restore docs/maven/

cd $from

for d in *; do
  mv -vf "$from/$d"/* "$to/$d"/
done

rm -rf $docs/maven-add
git add docs
