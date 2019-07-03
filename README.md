# new project with 'Spring Initializr'

use 'gradle project' to build project


# spring select

* Developer Tools 
    * Lombok
    * Spring Configuration Processor
* SQL
    * Spring Data JPA
    * Mysql Driver
    * JDBC API
* NoSql
    * Spring Data Redis


# remove the App Start class created by Spring boot

# config maven local build - build.gradle - maven publish plugin 


```
# use maven plugin
apply plugin: 'maven-publish'


# build jar
jar {
    enabled = true
}

# disable boot jar
bootJar {
    enabled = false
}

# append source files
task sourcesJar(type: Jar) {
    from sourceSets.main.allJava
    archiveClassifier = 'sources'
}

# config publish
publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
            artifact sourcesJar
            pom {}
        }
    }
}

```


    
# Jenkinsfile - jenkins build

create Jenkinsfile
**change workspace path**

```
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
```

# add install.sh for local build

```
#!/usr/bin/env bash

./gradlew clean build publishToMavenLocal -i
```


Done.
Run ./install.sh for local maven publish

