#heroku plugins:install heroku-cli-deploy
heroku war:deploy login.war --app mod-dev-login
heroku ps:scale web=1 --app mod-dev-login

