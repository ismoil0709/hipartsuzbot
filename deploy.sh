echo "Building app..."

mvn clean install

echo "Deploying files to server..."
scp -r target/*.jar root@5.182.26.95:/home/

echo "Done !"