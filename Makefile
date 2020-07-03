clean:
	rm Main.class

build: Main.java
	javac Main.java

run: build
	java Main
	rm Main.class
