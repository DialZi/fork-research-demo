/*
jenkins:localhost:8080
  add user jenkins to docker usergroup

defect dojo: localhost:8081 >>>> update port in docker compose file. default = 8080
  git clone https://github.com/DefectDojo/django-DefectDojo
  set sonarqube project key xxx:xxxxx

sonarqube: localhost:9000
  get api key.
  set sonarqube api key in jenkins and defect dojo
  defect dojo set sonarqube api key: 
    url: Home/Tool Configuration List/Edit Tool Configuration
    set url=http://{sonarqube}/api, toolType=SonarQube, authentication type=API Key, username+API Key = sonarqube API Key

ports:
80    cakefactory
8080  jenkins
8081  defect dojo
9000  sonarqube




*/

pipeline {
  agent any
  tools {
    maven 'Maven'
  }

  environment {
    DEFECT_DOJO_ENGAGEMENT_ID = '12'
    DEFECT_DOJO_IP = 'http://172.16.1.4:8081'
    DEFECT_DOJO_KEY = credentials('defectDojoApiKey')
    JAVA_PROJECT_NAME = 'demoProject-0.0.1'
    DOCKER_IMAGE_NAME = 'cakefactory'
    DOCKER_CONTAINER_NAME = 'cakefactory'
    DOCKER_CONTAINER_NAME_DAST = 'cakefactorydast'
    DOCKER_DAST_NET_NAME = 'dastnet'
    JENKINS_PROJECT_NAME = 'demo'
    DEPENDENCY_CHECK_URL = 'https://raw.githubusercontent.com/domi810/demo/master/owasp-dependency-check.sh'
    CLAIR_SCANNER_URL = 'https://raw.githubusercontent.com/domi810/demo/master/clair-scanner_linux_386'
    DOCKERFILE_URL = 'https://raw.githubusercontent.com/domi810/demo/master/Dockerfile'
  }
  stages {

    stage('INIT') {
      steps {
        //workarounds for testing
        sh 'docker-compose -f /home/domi/django-DefectDojo/docker-compose.yml up -d || true'
        sh 'docker run -d -p 9000:9000 --name sonarqube_jenkins sonarqube || true'
        sh 'docker container start sonarqube_jenkins || true'
      }

    }

    stage('BUILD') {
      steps {
        sh 'mvn clean package'
        //change name of the war file do run the app under the root path
        sh 'cd target && ls -l'
        sh 'mv target/${JAVA_PROJECT_NAME}.war target/ROOT.war'
        sh 'cd target && ls -l'
      }
    }

    stage('BUILD DOCKER IMAGE') {
      steps {
        //create tmp dir
        sh 'mkdir tmp'
        //copy target folder
        sh 'cp -r target tmp/'
        //get newest dockerfile
        sh 'wget ${DOCKERFILE_URL} -P ./tmp' 
        //build image
        sh 'docker build ./tmp -t ${DOCKER_IMAGE_NAME}:latest'
        //inspect image
        sh 'docker image inspect ${DOCKER_IMAGE_NAME}'
        //delete tmp folder
        sh 'rm -r tmp' 
      }
    }

    stage('DEPLOY TO DEV') {
      steps {
        //stop runnig container
        sh 'docker stop ${DOCKER_CONTAINER_NAME} || true'
        //run 
        sh 'docker run -d -p 80:8080 --rm --name ${DOCKER_CONTAINER_NAME} ${DOCKER_IMAGE_NAME}:latest'
      }
    }

    stage('DOCKER IMAGE SECURITY') {
      steps {
        //remove binary if exists
        sh 'rm clair-scanner_* || true'
        //get newest binary
        sh 'wget ${CLAIR_SCANNER_URL}'
        //make executable
        sh 'chmod +x clair-scanner_linux_386'
        //run clair containers
        sh 'docker run --rm -p 5432:5432 -d --name clair_db arminc/clair-db:latest || true'
        sh 'docker run --rm -p 6060:6060 --link db:postgres -d --name clair arminc/clair-local-scan:latest || true'
        //scan image
        sh './clair-scanner_linux_386  --ip 172.17.0.1 -r clair_report.json postgres || true'
        sh 'cat clair_report.json'
        //stop clair containers
        sh 'docker container stop clair_db || true'
        sh 'docker container stop clair || true'
        //upload to defect dojo
        sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=Clair Scan" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/clair_report.json" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
      }
    }

    stage('SCA') {

      steps {
        //remove file if exists
        sh 'rm owasp* || true'
        //get newest script
        sh 'wget ${DEPENDENCY_CHECK_URL}'
        //make executable
        sh 'chmod +x owasp-dependency-check.sh'
        //run script
        sh 'bash owasp-dependency-check.sh'
        //stop container
        sh 'docker container stop dependency_check || true'
        ///upload to defect dojo
        sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=Dependency Check Scan" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/dependency-check-report.xml" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
      }
    }

    stage('SAST') {
      parallel {
        stage('SONARQUBE') {
          steps {
            //run sonarqube
            withSonarQubeEnv('sonarserver') {
              sh 'mvn sonar:sonar'
              sh 'cat target/sonar/report-task.txt'
            }
            //trigger defect dojo sonarqube api import
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=SonarQube API Import" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
          }
        }

        stage('SAST SCAN') {
          steps {
            //run shif left scan
            sh 'docker run --rm -e "WORKSPACE=${PWD}" -v $PWD:/app --name shift_left_scan shiftleft/sast-scan scan  --build --out_dir /app/reports || true'
            //upload to defect dojo
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=SARIF" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/reports/bash-report.sarif" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=SARIF" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/reports/class-report.sarif" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=SARIF" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/reports/source-java-report.sarif" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=SARIF" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/reports/source-sql-report.sarif" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'

          }
        }
      }
    }

    stage('INIT DAST') {
      steps {
        //stop test container if already started
        sh 'docker container stop ${DOCKER_CONTAINER_NAME_DAST} || true'
        //create network
        sh 'docker network create ${DOCKER_DAST_NET_NAME} || true'
        //start cakefatory
        sh 'docker run --net ${DOCKER_DAST_NET_NAME} -d -p 8085:8080 --rm --name ${DOCKER_CONTAINER_NAME_DAST} ${DOCKER_IMAGE_NAME}:latest'
        //make sure app is runnig
        sleep(time: 10, unit: "SECONDS")
        sh 'docker run --net ${DOCKER_DAST_NET_NAME} --rm curlimages/curl http://${DOCKER_CONTAINER_NAME_DAST}:8080/'
      }
    }

    stage('DAST') {
      parallel {
        stage('OWASP ZAP') {
          steps {
            //run zap docker. make sure folder is owned by same uid as user zap in docker
            sh 'docker run --net ${DOCKER_DAST_NET_NAME} --rm -v $(pwd)/zap:/zap/wrk:rw  -t owasp/zap2docker-stable zap-full-scan.py -t http://${DOCKER_CONTAINER_NAME_DAST}:8080/ -j -g gen.conf -x zap_report.xml || true'
            //upload to defect dojo
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=ZAP Scan" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/zap/zap_report.xml" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
          }
        }

        stage('ARACHNI') {
          steps {
            //start arachni container
            sh 'docker run --net ${DOCKER_DAST_NET_NAME} --rm -v $(pwd)/reports:/arachni/reports ahannigan/docker-arachni bin/arachni http://${DOCKER_CONTAINER_NAME_DAST}:8080/ --report-save-path=reports/arachni_report.afr'
            //create Report
            sh 'docker run --net ${DOCKER_DAST_NET_NAME} --rm --name=arachni_report  -v $(pwd)/reports:/arachni/reports ahannigan/docker-arachni bin/arachni_reporter reports/arachni_report.afr --reporter=json:outfile=reports/arachni_report.json'
            //upload to defect dojo
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=Arachni Scan" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/reports/arachni_report.json" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
          }
        }

        stage('NIKTO') {
          steps {
            sh 'rm -r nikto || true'
            //get repo
            sh 'git clone https://github.com/sullo/nikto.git'
            //build image
            sh 'docker build ./nikto -t nikto:latest'
            //inspect image
            sh 'docker image inspect nikto:latest'
            //run nikto
            sh 'docker run --rm --net ${DOCKER_DAST_NET_NAME} --name nikto -v $(pwd)/zap:/tmp nikto -h http://${DOCKER_CONTAINER_NAME_DAST}:8080 -o /tmp/nikto_report.xml || true'
            //stop container
            sh 'docker container stop nikto || true'
            //upload to defect dojo
            sh 'curl -X POST "${DEFECT_DOJO_IP}/api/v2/import-scan/"  -H "Authorization: Token ${DEFECT_DOJO_KEY}"  -H  "accept: application/json" -H   "Content-Type: multipart/form-data" -F "minimum_severity=Info" -F "active=true" -F "verified=true" -F "scan_type=Nikto Scan" -F "file=@/home/jenkins/workspace/${JENKINS_PROJECT_NAME}/zap/zap_report.xml" -F "engagement=${DEFECT_DOJO_ENGAGEMENT_ID}" -F "close_old_findings=false" -F "push_to_jira=false"'
            sh 'rm -r nikto'
          }
        }
      }
    }

    stage('CLEANUP') {
      steps {
        //stop test container
        sh 'docker container stop ${DOCKER_CONTAINER_NAME_DAST}'
      }
    }

  }
}