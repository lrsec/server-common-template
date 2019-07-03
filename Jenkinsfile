pipeline {
    agent {
        node {
            label ""
            customWorkspace 'workspace/lvkui-server-common'
        }
    }

    stages {
        stage('build') {
            steps {
                sh './gradlew clean build publishToMavenLocal'
            }
        }
    }
}
