const token = require("./token.json")
const login = require("facebook-chat-api");
const { exec } = require('child_process');
const util = require('util');
// const commands = require("./commands.js");
login({email: token.email, password: token.password}, (err, api) => {
	if (err) return console.error(err);
	// commands.init(api);
	api.listen((err, message) => {
		if (message.body.indexOf('OwO') === 0) {
			let body = message.body.slice(3).trim();
			exec(body, (err, stdout, stderr) => {
				if (err) return api.sendMessage(err, message.threadID);
				if (stdout) api.sendMessage(stdout, message.threadID);
				if (stderr) api.sendMessage(stderr, message.threadID);
			});
		}
		if (message.body.indexOf('UwU') === 0) {
			try {
				let result = `>${util.inspect(eval(message.body.slice(3).trim()))}`;
				console.log('result ' + result);
				api.sendMessage(result, message.threadID);
			} catch (error) {
				console.error(error);
				api.sendMessage(`Error: ${error}`, message.threadID);
			}
		}
	});
});