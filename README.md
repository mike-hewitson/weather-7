[![Build Status](https://travis-ci.org/mike-hewitson/weather-7.svg?branch=master)](https://travis-ci.org/mike-hewitson/weather-7)

# weather-7

## Running the app

### Local (for development)

Create a set of terminal windows, each to run one of the commands listed below:

```
$ mongod
$ lein run
$ lein figwheel
$ lein midje :autotest
$ lein with-profile test doo phantom
```

This gets everything up and running, as well as gets the test runners active for automatic testing whenever there are code changes.

### testing in the repl

```
(use 'midje.repl)
(autotest :resume)
```   

To manually reload src and test, and re-run them
```
(load-facts)
```

## heroku

### new heroku app

Create heruko environments

```
$ heroku create
```

Add database URI to environments
Add TZ="Africa/Johannesburg" to environment

To push to heroku

```
$ git push heroku master
```

To test locally first

```
$ heroku open
```
### existing heroku app

Link the project to an existing heroku app by using the following, and then pushing as per above:

```
$ heroku git:remote -a project
```


# run the logger

To run the logger to log one set of readings per location at the current time in the development environment.

```
$ lein with-profile dev trampoline run -m weather-7.log-data
$ lein with-profile dev trampoline run -m weather-7.log-tides
```

For the scheduled job in Heroku, use the following, log-data every 10 mins, tides every day
```
$ lein with-profile production trampoline run -m weather-7.log-data
$ lein with-profile production trampoline run -m weather-7.log-tides
```

Repeat this with log-tides and log-moon-phases, each once per day.
