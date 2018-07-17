echo off
FileManger
echo Test

del classes\*.* /q
del classes\controller\*.* /q
del classes\enums\*.* /q
del classes\gui\*.* /q
del classes\logic\*.* /q
del classes\model\*.* /q
del classes\panel\*.* /q
del classes\screen\*.* /q
del classes\swingPanel\*.* /q
del classes\controller\*.* /q

javac src\*.java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\controller\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\enums\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\gui\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\logic\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\model\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\panel\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\screen\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes
javac src\swingPanel\*.* /java	-classpath lib/commons-cli-1.4.jar;commons-io-2.4.jar;commons-lang3-3.6.jar;jsch-0.1.54.jar;slf4j-api-1.7.25.jar;slf4j-nop-1.7.25.jar;xuggle-xuggler-5.4.jar -d classes

cd classes
java App