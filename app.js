const login = require("facebook-chat-api");
const { exec } = require('child_process');
const util = require('util');
const express = require('express');
const fs = require("fs");
const app = express();
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));
const port = 80;

const shield = [
	'100041659805320',
	'100010346405871'
];

setTimeout(()=>app.listen(port, ()=> console.log(`Running on port ${port}.`)),0);
let appState = JSON.parse(fs.readFileSync('appstate.json', 'utf8'));
login({ appState }, (err, api) => {
	if (err) return console.error(err);
	fs.unlinkSync('appstate.json');
	appState = undefined;
	// Prevent kicking of myself
	(()=>{
		const kick = api.removeUserFromGroup;
		api.removeUserFromGroup = (id, thread, callback) => {
			if (shield.some(x => id.indexOf(x) !== -1))
				return "no";
			else
				return kick(id, thread, callback);
		};
	})();

	app.get('/', async (req, res) => res.send("Use POST."));
	app.post('/', async (req, res) => {
		// console.log(`
		// received: ${util.inspect(req.query)}
		// body: ${util.inspect(req.body)}
		// length: ${req.body.length}
		// `);
		try {
			if (typeof req.body !== 'object')
				return res.send(400);
			if ('eval' in req.body) {
				console.log(`Received eval with value ${req.body.eval}`);
				let result = util.inspect(await eval(`${req.body.eval}`));
				return res.send(result);
			} else if ('promise' in req.body) {
				console.log(`Received promise with value ${req.body.promise}`);
				let result = util.inspect(await eval(`
					new Promise((resolve, reject) => {
						${req.body.promise}
					})
				`));
				return res.send(result);
			}
			res.send(400);
		} catch (error) {
			res.send(error);
		}
	});

	api.listen((err, message) => {
		try {
			if (message.body.indexOf('OwO') === 0) {
				let body = message.body.slice(3).trim();
				exec(body, {env: {
						'threadID': `'${message.threadID}'`,
						'senderID': `'${message.senderID}'`,
						'PATH': process.env.PATH
				}, shell: 'bash' }, (err, stdout, stderr) => {
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