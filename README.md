# PACR

PACR analyzes commits in repositories.

## Setup

### Database

1. Install mysql-server
2. Create user 'pacr' with password 'pacr'
3. Create database 'pacr'
4. Grant all privileges on database to 'pacr'
5. Start mysql-server
 
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

TODO

### Backend

TODO

### Frontend

1. Follow steps 1-6 from setup frontend
2. Run `ng build --prod --aot` in frontend directory
3. Deploy contents of `frontend/dist` to desired location on webserver (for example copy files to /www directory of apache)
4. Configure webserver to redirect missing pages to `index.html`
