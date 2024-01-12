<h1 align="center">Stats-Plugin</h1>
<!-- TABLE OF CONTENTS -->

## Table of Contents

- [Overview](#overview)
- [Built With](#built-with)
- [How To Use](#How To Use)
- [Contact](#contact)

<!-- OVERVIEW -->

## Overview
- A Jenkins plugin to give stats of projects like number of classes, line of code and numbers of methods.


### Built With

- The project is built using Java, maven and Spring boot a popular Java framework.

## How To Use

<!-- This is an example, please update according to your application -->

To run this application, you'll need [Java](https://www.oracle.com/java/technologies/downloads/), [Jenkins](https://www.jenkins.io/download/) and [maven](https://maven.apache.org) 
```bash
# Clone this repository
$ git clone [https://github.com/Ibrahim-Payak/stats-plugin]

# Build the application
$ mvn clean install

- This will create a stats-plugin.hpi file in the target directory. We need to copy it to the Jenkins plugins directory (C:\Users\ibrah\.jenkins\plugins in my case)

# Copy it to the Jenkins plugins directory
$ copy ./target/stats-plugin.hpi C:\Users\ibrah\.jenkins\plugins

# Run the Jenkins
$ java -jar jenkins.war
```

- Once Jenkins is fully up and running, go to http://localhost:8080
- Create new job to verify
- Give any github repo link for which you wanted stats of Build the job
- When you will build the job, you will get stats.html file.

![image](https://github.com/Ibrahim-Payak/stats-plugin/assets/114486038/6c4c2fb9-8154-4ea2-9896-2a97f4252f66)
![image](https://github.com/Ibrahim-Payak/stats-plugin/assets/114486038/534d96c0-d6b6-4837-85dd-e944a6f823f1)

## Contact

- GitHub [@IbrahimPayak](https://github.com/Ibrahim-Payak)
- LinkedIn [@IbrahimPayak](https://www.linkedin.com/in/ibrahim-payak-6b8445174/)

