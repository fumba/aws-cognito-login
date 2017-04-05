#heroku plugins:install heroku-cli-deploy
heroku jar:deploy app.jar --app mod-dev-login
heroku ps:scale web=1 --app mod-dev-login

