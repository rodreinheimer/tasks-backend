pipeline {
    agent any
    tools { 
      maven 'MAVEN_HOME' 
      jdk 'JAVA_HOME' 
    }
    stages {
        stage ('Build Backend') {
            steps {
                sh 'mvn package clean -DskipTest=true'
            }
        }
    }
}