# PACR

PACR analyzes commits in repositories.

## Setup

### Database

1. Install mysql-server
2. Create user 'pacr' with password 'pacr1'. 
3. Create user 'pacr2' with password 'pacr2'
4. Create database 'pacr'
5. Create database 'pacrtest'
6. Grant all privileges on database to 'pacr'
7. Grant only privileges to scheam 'pacrtest' to 'pacr2'
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

1. Install python3.8
2. Install Java 13
3. Start BenchmarkerApplication

## Deploy

### Database

1. Follow steps 1-8 from setup database

### Backend

1. Follow steps 1-3 from setup backend
2. Run `mvn package`
3. Deploy jar to desired location with application.properties (optional) and ssh.key (must contain public ssh key)
4. Execute the jar file

### Frontend

1. Follow steps 1-6 from setup frontend
2. Run `ng build --prod --aot` in frontend directory
3. Deploy contents of `frontend/dist` to desired location on webserver (for example copy files to /www directory of apache)
4. Configure webserver to redirect missing pages to `index.html`

### Benchmarker

1. Follow steps 1-2 from setup benchmarker
2. Run `mvn package`
3. Deploy jar to desired location with runner directory and ssh.key (must contain public ssh key)
4. Execute the jar file (after starting backend)