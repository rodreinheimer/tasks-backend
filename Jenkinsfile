pipeline {
    agent any
    tools { 
      maven 'MAVEN_LOCAL' 
      jdk 'JAVA_LOCAL' 
    }
    stages {
        stage ('Build Backend') {
            steps {
                sh 'mvn clean package -DskipTests=true'
            }
        }
        stage ('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage ('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    sh '${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://localhost:9000 -Dsonar.login=a9abf5194ed29bdb9cae1e451861e68b73b2a926 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/src/test/**,**/model/**,**Application.java'
                }
            }
        }
        stage ('Quality Gate') {
            steps {
                sleep(5)
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage ('ShiftLeft Analysis') {
            steps {
                dir('shiftleft-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/rodreinheimer/shiftleft-java-demo.git/'
                    sh 'mvn -e clean package'
                    sh '/usr/local/bin/sl analyze --tag app.teamname=Loyalty --app HelloShiftLeft --java target/hello-shiftleft-0.0.1.jar'
                }
            }
        }
        stage ('Deploy Backend') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat_login', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Test') {
            steps {
                dir('api-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/rodreinheimer/tasks-api-test.git'
                    sh 'mvn test'
                }
            }
        }
        stage ('Deploy Frontend') {
            steps {
                dir('frontend') {
                    git credentialsId: 'github_login', url: 'https://github.com/rodreinheimer/tasks-frontend.git'
                    sh 'mvn clean package'
                    deploy adapters: [tomcat8(credentialsId: 'tomcat_login', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test') {
            steps {
                dir('functional-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/rodreinheimer/task-functional-test.git'
                    sh 'mvn test'
                }
            }
        }
        stage ('Deploy Prod') {
            steps {
                withEnv(['PATH+EXTRA=/usr/local/bin/:/usr/local/bin/docker']) {
                    sh 'docker-compose build'
                    sh 'docker-compose up -d'
                }
            }
        }
        stage ('Health Check') {
            steps {
                dir('functional-test') {
                    sh 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, api-test/target/surefire-reports/*.xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-repor/*.xml'
            archiveArtifacts artifacts: 'target/tasks-backend.war, tasks-frontend/target/tasks.war', followSymlinks: false
        }
        unsuccessful {
            emailext body: 'See attached logs', subject: 'Build $BUILD_NUMBER has failed', to: 'rodrigo.reinheimer@gmail.com'
        }
        fixed {
            emailext body: 'See attached logs', subject: 'Build is fine!', to: 'rodrigo.reinheimer@gmail.com'
        }
    }
}