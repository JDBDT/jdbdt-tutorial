mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent test sonar:sonar \
    -Dsonar.host.url=https://sonarqube.com \
    -Dsonar.login=$SONARQUBE_TOKEN \
    -Dsonar.organization=jdbdt
