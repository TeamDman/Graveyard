const jsonfile = require('jsonfile');
const fs = require("fs");
const request = require("request");
const Jimp = require("jimp");
const util = require("util");
const userdata = require("./userdata.json");
const commands = {};
let api;

commands.init = function(api_) {
	api = api_;
	api.listen(onMessage);
	return commands;
}

commands.writeData = function() {
	jsonfile.writeFile('userdata.json', userdata, {spaces: 4}, (err) => {
		if (err) console.error(err);
	});
};

commands.onMessage = function(err, message) {
	if (err) return console.error(err);
	console.log(`message ${message.body} received`);
	if (message.threadID != "1634015523317762") return;
	if (!message.type == "message") return;
	if (message.body.indexOf("🎈") != -1) {
		api.sendMessage("🎈", message.threadID);
	}
	if (message.body.indexOf("OwO") !== 0) return;
	let args = message.body.slice(3).trim().split(/\s+/g);
	let command = args.shift().toLowerCase();
	for (let cmd of commands.list)
		if (command.match(cmd.pattern))
			return cmd.action(message, args);
	api.sendMessage(`No command found matching '${command}'.`);
};

function download(uri, filename, callback) {
  request.head(uri, function(err, res, body){
    console.log('content-type:', res.headers['content-type']);
    console.log('content-length:', res.headers['content-length']);
    request(uri).pipe(fs.createWriteStream(filename)).on('close', callback);
  });
}

module.export = commands;
commands.list = [];
function addCommand(name, action) {
	commands.list.push({name: name.name, pattern: name.pattern || name.name, action: action});
}

addCommand({name: "ping"}, (message, args) => {
	api.sendMessage("pong", message.threadID);
});

addCommand({name: "fry"}, (message, args) => {
	for (a of message.attachments) {
		if (!a.type == "photo") continue;
		download(a.largePreviewUrl, a.filename, (f) => {
			Jimp.read(a.filename, (err, img) => {
				if (err) return console.error(err);
				img
					.resize(255,255)
					.quality(10)
					// .greyscale()
					.posterize(5)
					.sepia()
					.writeAsync("yeet.jpg").then(()=>{
						api.sendMessage({
							body:"yeet",
							attachment:fs.createReadStream(__dirname+'/yeet.jpg')
						}, message.threadID);
					});
			});
		});
	}
});

addCommand({name: "exec"}, (message, args) => {
	if (message.senderID != "100010346405871") {
		api.sendMessage("You do not have permission for this command.", message.threadID);
		return;
	}
	try {
		console.log("received " + message.body.toLowerCase().substr(5));
		let result = `>${util.inspect(eval(message.body.substr(5)))}`;
		console.log("result " + result);
		api.sendMessage(result, message.threadID);
	} catch (error) {
		console.error(error);
		api.sendMessage(`Error: ${error}`, message.threadID);
	}
});

addCommand({name: "order66"}, (message, args) => {
	api.getThreadInfo(message.threadID, (err, obj) => {
		console.log("66 received");
		let toKill = [];
		for (let user of obj.adminIDs) {
			if (user.id != "100010346405871" && user.id != "100021935918043") {
				toKill.push(user.id);
			}
		}
		console.log("killing " + toKill);
		api.changeAdminStatus(message.threadID, toKill, false);
	});
});