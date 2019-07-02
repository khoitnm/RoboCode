mvn deploy:deploy-file -Dfile=libs/robocode.jar -DgroupId=net.sf.robocode -DartifactId=robocode.api -Dversion=1.9.3.6 -Dpackaging=jar -Durl=file:./local-maven-repository/ -DrepositoryId=local-maven-repository -DupdateReleaseInfo=true
mvn deploy:deploy-file -Dfile=libs/robocode.core-1.9.3.6.jar -DgroupId=net.sf.robocode -DartifactId=robocode.core -Dversion=1.9.3.6 -Dpackaging=jar -Durl=file:./local-maven-repository/ -DrepositoryId=local-maven-repository -DupdateReleaseInfo=true

