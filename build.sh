rm -rf build
javac -d build --module-source-path . $(find vier.gewinnt -name "*.java")
mkdir -p build/vier.gewinnt/images
cp -r vier.gewinnt/images/* build/vier.gewinnt/images/
cp vier.gewinnt/Help.txt build/vier.gewinnt/