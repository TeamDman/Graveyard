const login = require("facebook-chat-api");
const {exec} = require('child_process');
const util = require('util');
const express = require('express');
const fs = require("fs");
const app = express();
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));


setTimeout(() => {const listener = app.listen(process.env.PORT, () => console.log(`Running on port ${listener.address().port}.`))}, 0);

login({appState: JSON.parse(process.env.APPSTATE)}, (err, api) => {
    if (err) return console.error(err);
    fs.unlinkSync('appstate.json');
    appState = undefined;
    // Prevent kicking of myself
    (() => {

        const whitelist = [
            '100041659805320',
            '100010346405871',
        ];
        const blacklist = [
            '100002580913926',
        ];
        const kick = api.removeUserFromGroup;
        api.removeUserFromGroup = (id, thread, callback) => {
            if (whitelist.some(x => x === id))
                throw new Error("no");
            else
                return kick(id, thread, callback);
        };
        const changeAdminStatus = api.changeAdminStatus;
        api.changeAdminStatus = (thread, ids, value, callback) => {
            if (blacklist.some(x => ids.some(y => y === x)) && !!value)
                throw new Error("no");
            else if (whitelist.some(x => ids.some(y => y === x)) && !value)
                throw new Error("no");
            else
                return changeAdminStatus(thread, ids, value, callback);
        }
    })();

    app.get('/', async (req, res) => res.send("Use POST."));
    app.post('/', async (req, res) => {
        try {
            if (typeof req.body !== 'object')
                return res.sendStatus(400);
            if ('eval' in req.body) {
                console.log(`Received eval with value ${req.body.eval}`);
                let result = util.inspect(await eval(`${req.body.eval}`));
                console.log(`Resolved eval with value ${result}`);
                return res.send(result);
            } else if ('promise' in req.body) {
                console.log(`Received promise with value ${req.body.promise}`);
                let result = util.inspect(await eval(`
					new Promise((resolve, reject) => {
						try {
							${req.body.promise}
						} catch (e) {
							reject(e)
						}
					})
				`));
                console.log(`Resolved promise with value ${result}`);
                return res.send(result);
            }
            res.sendStatus(400);
        } catch (error) {
        	console.log(`Error handling network request ${error}`);
            return res.send(error.toString());
        }
    });

    api.listen((err, message) => {
        try {
            if (message.body.indexOf('OwO') === 0) {
                let body = message.body.slice(3).trim();
                exec(body, {
                    env: {
                        'threadID': `'${message.threadID}'`,
                        'senderID': `'${message.senderID}'`,
                        'PATH': process.env.PATH,
                        'PORT': process.env.PORT,
                    }, shell: 'bash'
                }, (err, stdout, stderr) => {
                    if (err) api.sendMessage(err, message.threadID);
                    if (stdout) api.sendMessage(stdout, message.threadID);
                    if (stderr) api.sendMessage(stderr, message.threadID);
                });
            }
            if (message.body.indexOf('UwU') === 0) {
                try {
                    let result = util.inspect(eval(message.body.slice(3).trim()));
                    api.sendMessage(result, message.threadID);
                } catch (error) {
                    api.sendMessage(error, message.threadID);
                }
            }
            if (message.body.toLowerCase().indexOf('good bot') !== -1) {
            	api.sendMessage("(◕‿◕✿)", message.threadID);
			}
        } catch (e) {
        	console.error(`Error parsing raw command: ${e}`);
        }
    });
});