const token = require("./token.json")
const login = require("facebook-chat-api");
const { exec } = require('child_process');
const util = require('util');
const express = require('express');
const fs = require("fs");
const app = express();
const port = 80;

const methods = {
	getUserID: async (identifier) => '',
	kick: async (id) => {},
};

app.get('/', async (req, res) => res.send('Working'));
app.post('/getID', async (req, res) => {
	try {
		return res.send(await methods.getUserID(req.query.identifier));
	} catch (e) {
		console.error(e);
	}
});
app.post('/kick', async (req, res) => {
	try {
		return res.send(await methods.kick(req.query.id, req.query.threadID));
	} catch (e) {
		console.error(e);
	}
});
setTimeout(()=>app.listen(port, ()=> console.log(`Running on port ${port}.`)),0);
login({appState: JSON.parse(fs.readFileSync('appstate.json', 'utf8'))}, (err, api) => {
	if (err) return console.error(err);

	(function patch() {
		const kick = api.removeUserFromGroup;
		api.removeUserFromGroup = (id, thread, callback) => {
			if (id.indexOf('100010346405871') !== -1)
				return "no";
			else
				return kick(id, thread, callback);
		};
	})();

	methods.getUserID = (identifier) => new Promise((resolve, reject) => {
		api.getUserID(identifier, (err, data) => {
			if (err) {
				console.log(err);
				resolve("Error");
			}
			resolve(data[0].userID);
		});
	});

	methods.kick = (id, threadID) => new Promise((resolve, reject) => {
		api.removeUserFromGroup(id, threadID, (err) => {
			if (err) {
				console.error(err);
				resolve("Error");
			} else
				resolve("Removed user from group.");
		});
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