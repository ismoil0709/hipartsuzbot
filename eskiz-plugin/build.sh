echo start building...

mvn -Dmaven.test.skip=true install

cp  target/*.jar /media/ismoil_0709/d_disk/G30/projects/hipartsuzbot/lib/

scp -r target/*.jar root@45.138.158.110:/root/workdir/projects/hipartsuzbot/lib

echo Successfully!
