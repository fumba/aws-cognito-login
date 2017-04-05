#heroku plugins:install heroku-cli-deploy
heroku war:deploy ./target/login-0.0.1-SNAPSHOT-shaded.war --app mod-dev-login
heroku ps:scale web=1 --app mod-dev-login

