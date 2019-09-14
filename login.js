const fs = require("fs");
const login = require("facebook-chat-api");
const token = require('./token.json');

login({email: token.email, password: token.password}, (err, api) => {
    if(err) return console.error(err);

    fs.writeFileSync('appstate.json', JSON.stringify(api.getAppState()));
});