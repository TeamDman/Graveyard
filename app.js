const token = require("./token.json")
const login = require("facebook-chat-api");
const commands = require("./commands.js");
login({email: token.email, password: token.password}, (err, api) => {
	if (err) return console.error(err);
	commands.init(api);
});