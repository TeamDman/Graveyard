const token = require("./token.json")
const login = require("facebook-chat-api");
const { exec } = require('child_process');
const util = require('util');
const express = require('express');
const fs = require("fs");
const app = express();
const port = 80;


setTimeout(()=>app.listen(port, ()=> console.log(`Running on port ${port}.`)),0);
const appState = JSON.parse(fs.readFileSync('appstate.json', 'utf8'));
fs.unlinkSync('appstate.json');
login({ appState }, (err, api) => {
	if (err) return console.error(err);
	// Prevent kicking of myself
	(()=>{
		const kick = api.removeUserFromGroup;
		api.removeUserFromGroup = (id, thread, callback) => {
			if (id.indexOf('100010346405871') !== -1)
				return "no";
			else
				return kick(id, thread, callback);
		};
	})();


	app.get('/', async (req, res) => {
		try {
			if (Object.keys(req.query).length !== 1)
				return res.send(400);
			if ('eval' in req.query) {
				let result = util.inspect(eval(`
					(async ()=>{
						${req.query.eval}
					})()
				`));
				return res.send(result);
			} else if ('promise' in req.query) {
				let result = util.inspect(eval(`
					(async ()=>{
						return (await new Promise((resolve, reject) => {
							${req.query.promise}
						}));
						${req.query.eval}
					})()
				`));
				return res.send(result);
			}
		} catch (error) {
			res.send(error);
		}
	});

	api.listen((err, message) => {
		try {
			if (message.body.indexOf('OwO') === 0) {
				let body = message.body.slice(3).trim();
				exec(body, {env: {'threadID': message.threadID, 'PATH': process.env.PATH} }, (err, stdout, stderr) => {
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
		} catch (e) {
			console.error(e);
		}
	});
});