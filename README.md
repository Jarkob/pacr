# PACR

PACR analyzes commits in repositories.

![Alt text](https://github.com/Jarkob/pacr/blob/master/screenshots/dashboard_overview.png "Dashboard")
![Alt text](https://github.com/Jarkob/pacr/blob/master/screenshots/admin_overview.png "Admin-Inter")

## Setup

### Database

1. Install mysql-server
2. Create user 'pacr' with password 'pacr1'. 
3. Create user 'pacr2' with password 'pacr2'
4. Create database 'pacr'
5. Create database 'pacrtest'
6. Grant all privileges on database to 'pacr'
7. Grant only privileges to schema 'pacrtest' to 'pacr2'
8. Start mysql-server
 
### Backend

1. Install Java 13
2. Clone project
3. Import as existing maven project to IDE
4. Start WebappBackendApplication
 
### Frontend

1. Install [node](https://nodejs.org/en/)
2. Install the [angular-cli](https://cli.angular.io)
2. Clone project
3. Open terminal
4. Navigate to `frontend` directory
5. Run `npm install`
6. Run `ng serve`
7. Open `localhost:4200` in browser
 
Optional: (currently required for diagrams)
1. Clone prototypes
2. Navigate to directory `rest`
3. Run `node main.js` in terminal 

### Benchmarker

1. Install Java 13
2. Start BenchmarkerApplication

## Deploy

### Database

1. Follow steps 1-8 from setup database

### Backend

1. Follow steps 1-3 from setup backend
2. Run `mvn package`
3. Deploy jar to desired location with `ssh.key` (SSH private key) and `ssh.pub` (SSH public key) in the same folder.
4. Execute the jar file

### Frontend

1. Follow steps 1-6 from setup frontend
2. Run `ng build --prod --aot` in frontend directory
3. Deploy contents of `frontend/dist` to desired location on webserver (for example copy files to /www directory of apache)
4. Configure webserver to redirect missing pages to `index.html`

### Benchmarker

1. Follow step 1 from the benchmarker setup.
2. Run `mvn package`.
3. Deploy the jar to desired location.
4. Create a directory `runner` and place the `bench` file in it. 
5. To set the IP-address of the backend, create a file `application.properties` in the same directory as the jar file and add `ipWebApp=[insertIP]`.

## application.properties

If you want to change the default values of the app, simply create a file called `application.properties` next to the jar file and add the line with the path variable you want to change.

### WebApp

The default application.properties looks like this:

```
publicKeyPath = /ssh.pub
privateKeyPath = /ssh.key
allowedOrigins = *
gitRepositoriesPath = /repositories
gitRepositoryPullIntervalDefault = 30
repository.colors = #ff8091,#8c6973,#330d2b,#532080,#1d00d9,#204680,#00ccff,#39e6da,#394d3e,#b4e6ac,#999673,#ffaa00,#66381a,#400900,#f20000,#d9003a,#661a42,#cc00ff,#1a0040,#c8bfff,#001b33,#103640,#238c77,#008011,#414d13,#7f6600,#33260d,#7f2200,#b26559,#403030,#ffbfd0,#cc3399,#e680ff,#73698c,#005ce6,#0099e6,#4d8a99,#40ff73,#003307,#dae639,#e5cf73,#cc7033,#f2c6b6,#ff0000
adminPasswordHashPath = /adminPasswordHash.txt
secretPath = /secret.txt
ignoreTag = #pacr-ignore
labelTag = #pacr-label

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/pacr
spring.datasource.username=pacr
spring.datasource.password=pacr
```

### Benchmarker

The default application.properties looks like this:

```
privateKeyPath=/ssh.key
repositoryWorkingDir=/repositories
relPathToWorkingDir=..
ipWebApp=127.0.0.1:8080
runnerFile=bench
runnerDir=runner
server.port=0
```
