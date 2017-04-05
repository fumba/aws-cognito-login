#heroku plugins:install heroku-cli-deploy
heroku jar:deploy ./target/login-0.0.1-SNAPSHOT.jar --app mod-dev-login
heroku ps:scale web=1 --app mod-dev-login

