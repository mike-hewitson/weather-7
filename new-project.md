# weather-7

## to create a new version of this from a template

### begin

```
$ git init
$ git add .
$ git commit
```
copy across readme.md
do the following to confirm all works, in seperate windows
```
$ lein uberjar
$ lein run
$ lein figwheel
```
uberjar should have not errors
run should have no errors
figwheel should have no errors
open browser to localhost:3000, should get connected

### update dependencies

Add ancient to dev dependencies

run it to check first, then to update
```
$ lein ancient
$ lein ancient upgrade
```
And make sure you test afterwards


### project.clj

Add midje and proto-repl to dev dependencies
Add midje to plugins (for testing)
Add clj-http to dependencies (for darksky access)
                 [clj-http "3.4.1"]
                 [clj-time "0.13.0"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
                 [com.novemberain/monger "3.1.0"]

<!-- TODO add in extra dependencies -->

### copy source and artifacts

Copy weather icons directory into public
Add into base.html
Copy contents from home.html, and any other html pages (unless using cljs)

Copy log_data.clj adjust ns and test it

Change database names to in profiles.clj weather_4 to keep old test data

Do a test run of the log_data
Checkin, push and push to heroku (repaet this a lot)

Do the following, checking at each stage. If the step works, checkin, push to heroku

-  Copy code from db.core and the tests
-  Copy routes from home.clj, services and tests

### profiles.clj

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


# to set up a datomic database from new

Use db_init.clj with repl
Setup url to point to the correct database
Create the database & schema using supplied functions
No need to create initial locations
Don't forget to execute the functions in the repl
