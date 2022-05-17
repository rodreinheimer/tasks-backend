pipeline {
    agent any
    tools { 
      maven 'MAVEN_LOCAL' 
      jdk 'JAVA_LOCAL' 
    }
    stages {
        stage ('Build Backend') {
            steps {
                sh 'mvn package clean --DskipTests=true'
            }
        }
    }
}