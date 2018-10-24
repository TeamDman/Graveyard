const jsonfile = require('jsonfile');
const fs = require('fs');
const request = require('request');
const Jimp = require('jimp');
const Markov = require('node-markovify');
const util = require('util');
const path = require('path');
const userdata = require('./userdata.json');
const msgdata = require('D:/Backups and Exports/facebook-Dominic9201/messages/RITHIKISCROWNED3RDHUNGERGAMESWINNER_8edeb894bf/message.json');
const raddest = '1634015523317762';
const admins = {
  '100010346405871': 'Dom'
};
const isAdmin = (id) => !!admins[id];
const commands = {};
commands.list = [];
commands.undoQueue = [];
commands.markov = {};
commands.duels = {};
let api;
let roulette = 0;

module.exports = commands;

commands.init = (api_) => {
  api = api_;
  api.listen((err, message) => commands.onMessage(err, message));
  return commands;
};

commands.writeData = () => {
  jsonfile.writeFile('userdata.json', userdata, {spaces: 4}, (err) => {
    if (err) console.error(err);
  });
};

commands.kick = (id, group) => {
  if (id in admins) {
    api.sendMessage('A divine barrier is enacted.', group);
  } else {
    api.removeUserFromGroup(id, group);
    commands.undoQueue.push(() => api.addUserToGroup(id, group));
  }
};

commands.checkDuels = (message) => {
  for (duel of Object.values(commands.duels)) {
    if (duel.isActive && duel.participants.includes(message.senderID)) {
      if (message.timestamp != 0) {
        api.sendMessage('Bang!', message.threadID);
        for (id of duel.participants.filter(n => n != message.senderID)) {
          commands.kick(id, message.threadID);
        }
      } else {
        api.sendMessage('False start!', message.threadID);
        commands.kick(message.senderID, message.threadID);
      }
      delete commands.duels[duel.host];
    }
  }
};

commands.onMessage = (err, message) => {
  if (err) return console.error(err);
  console.log(`message ${message.body} received`);
  if (message.threadID != raddest) return;
  if (!message.type == 'message') return;
  commands.checkDuels(message);
  if (message.body.indexOf('🎈') != -1) {
    api.sendMessage('🎈', message.threadID);
  }
  if (message.body.indexOf('OwO') !== 0) return;
  let repl = [];
  let body = message.body.slice(3).trim();
  for (mention of Object.keys(message.mentions)) {
    body = body.replace(message.mentions[mention], '\\$');
    repl.push(mention);
  }
  let args = body.match(/\\?.|^$/g).reduce((p, c) => {
    if (c === '"') {
      p.quote ^= 1;
    } else if (!p.quote && c === ' ') {
      p.a.push('');
    } else {
      p.a[p.a.length - 1] += c.replace(/\\(.)/, '\\$');
    }
    return p;
  }, {a: ['']}).a;
  for (i in args) {
    if (args[i] == '\\$') {
      args[i] = repl.pop();
    }
  }
  let command = args.shift().toLowerCase();
  for (let cmd of commands.list) {
    if (command.match(cmd.pattern)) {
      if (cmd.hasPerms(message.senderID)) {
        try {
          return cmd.action(message, args);
        } catch (e) {
          console.error(e.stack);
          return api.sendMessage(`Error executing command '${command}'\n>${e}`, message.threadID);
        }
      } else {
        return api.sendMessage('You do not have permission for this command.', message.threadID);
      }
    }
  }
  api.sendMessage(`No command found matching '${command}'.`, message.threadID);
};

function download (uri, filename, callback) {
  request.head(uri, function (err, res, body) {
    console.log('content-type:', res.headers['content-type']);
    console.log('content-length:', res.headers['content-length']);
    request(uri).pipe(fs.createWriteStream(filename)).on('close', callback);
  });
}

function addCommand (name, action) {
  commands.list.push({
    name: name.name,
    pattern: name.pattern || name.name,
    hasPerms: name.perms || (() => true),
    action: action
  });
}

addCommand({name: 'ping'}, (message, args) => {
  api.sendMessage('pong', message.threadID);
});

addCommand({name: 'fry'}, (message, args) => {
  for (a of message.attachments) {
    if (!a.type == 'photo') continue;
    download(a.largePreviewUrl, a.filename, (f) => {
      Jimp.read(a.filename, (err, img) => {
        if (err) return console.error(err);
        img
          .resize(255, 255)
          .quality(10)
        // .greyscale()
          .posterize(5)
          .sepia()
          .writeAsync('yeet.jpg').then(() => {
            api.sendMessage({
              body: 'yeet',
              attachment: fs.createReadStream(__dirname + '/yeet.jpg')
            }, message.threadID);
          });
      });
    });
  }
});

addCommand({name: 'exec', perms: isAdmin}, (message, args) => {
  try {
    let result = `>${util.inspect(eval(args.join(' ')))}`;
    console.log('result ' + result);
    api.sendMessage(result, message.threadID);
  } catch (error) {
    console.error(error);
    api.sendMessage(`Error: ${error}`, message.threadID);
  }
});

addCommand({name: 'order66'}, (message, args) => {
  api.getThreadInfo(message.threadID, (err, obj) => {
    console.log('66 received');
    let toKill = [];
    for (let user of obj.adminIDs) {
      if (user.id != '100010346405871' && user.id != '100021935918043') {
        toKill.push(user.id);
      }
    }
    console.log('killing ' + toKill);
    api.changeAdminStatus(message.threadID, toKill, false);
  });
});

addCommand({name: 'roulette'}, (message, args) => {
  if (roulette == 0 || args[0] && args[0] == 'spin') {
    roulette = Math.floor(Math.random() * 7 + 1);
    api.sendMessage('Reloaded the revolver.', message.threadID);
  }
  if (roulette == 1) {
    commands.kick(message.senderID, message.threadID);
    api.sendMessage('Bang!', message.threadID);
    setTimeout(() => {
      api.addUsertoGroup(message.senderID, message.threadID);
    }, 600000);
  } else {
    api.sendMessage('Click!', message.threadID);
  }
  roulette--;
});

addCommand({name: 'reload', perms: isAdmin}, (message, args) => {
  try {
    delete require.cache[path.resolve('./commands.js')];
    require('./commands.js').init(api);
    commands.onMessage = (a, b) => {};
    api.sendMessage('Reloaded successfully.', message.threadID);
  } catch (e) {
    api.sendMessage(`Could not reload commands.\n>${e}`);
  }
});

addCommand({name: 'undo'}, (message, args) => {
  commands.undoQueue.pop()();
});

addCommand({name: 'sudo', perms: isAdmin}, (message, args) => {
  message.body = args[1];
  message.senderID = args[0];
  commands.onMessage(null, message);
});

addCommand({name: 'die', perms: isAdmin}, (message, args) => {
  api.sendMessage('Goodbye.', message.threadID);
  setTimeout(() => process.exit(), 1000);
});

addCommand({name: 'debug'}, (message, args) => {
  api.sendMessage(`Arguments: ${util.inspect(args)}`, message.threadID);
});

addCommand({name: 'kick', perms: isAdmin}, (message, args) => {
  commands.kick(args[0], message.threadID);
});

addCommand({name: 'add'}, (message, args) => {
  api.addUserToGroup(args.pop(), message.threadID);
});

addCommand({name: 'undo', perms: isAdmin}, (message, args) => {
  commands.undoQueue.pop()();
});

addCommand({name: 'markov'}, (message, args) => {
  if (!(args[0] in commands.markov)) {
    let corp = [];
    for (msg of msgdata.messages) {
      if (msg.sender_name == args[0] && msg.content && msg.content.length > 4) {
        corp.push(msg.content);
      }
    }
    if (corp.length == 0) {
      return api.sendMessage('User not found while building corpus.', message.threadID);
    }
    commands.markov[args[0]] = new Markov.markovText({
      corpus: corp,
      stateSize: 2
    });
  }
  try {
    let rtn = commands.markov[args[0]].predict({
      init_state: null,
      max_chars: args[3] ? parseInt(args[3]) : 150,
      numberOfSentences: args[2] ? parseInt(args[1]) : 4,
      popularfirstWord: args[1] ? 'true' : false
    });
    console.log('output');
    console.log(rtn);
    api.sendMessage(`Excerpts from ${args[0]} in an alternate universe:\n\n${rtn.join('\n\n')}`, message.threadID);
  } catch (e) {
    console.error(e.stack);
    api.sendMessage('Error generating markov chain.', message.threadID);
  }
});

addCommand({name: 'duel'}, (message, args) => {
  if (args[0] == 'help') {
    api.sendMessage('OwO duel start [@user]\nOwO duel accept [@user]', message.threadID);
  } else if (args[0] == 'start') {
    let duel = {
      host: message.senderID,
      timestamp: Date.now(),
      canAccept: args[1] ? x => x == args[1] : () => true,
      isActive: false,
      participants: [message.senderID]
    };
    commands.duels[message.senderID] = duel;
    if (args[1]) {
      api.sendMessage({
        body: `A duel has been issued to ${message.mentions[args[1]]}!\nUse \`OwO duel accept @mention\` if you're brave enough!`,
        mentions: [{tag: message.mentions[args[1]], id: args[1]}]
      }, message.threadID);
    } else {
      api.sendMessage(`A duel has been issued to EVERYONE!\nUse \`OwO duel accept @mention\` if you're brave enough!`, message.threadID);
    }
    setTimeout(() => {
      if (message.senderID in commands.duels && commands.duels[message.senderID].timestamp == duel.timestamp) {
        delete commands.duels[message.senderID];
        api.sendMessage('The duel has expired.', message.threadID);
      }
    }, 30000);
  } else if (args[0] == 'accept') {
    if (args[1] in commands.duels) {
      let duel = commands.duels[args[1]];
      if (duel.isActive) {
        api.sendMessage('That duel has already started.', message.threadID);
      } else if (duel.canAccept(message.senderID)) {
        duel.participants.push(message.senderID);
        api.sendMessage({
          body: message.mentions[args[1]] + 's challenge has been accepted!\nThe winner of the duel will be the first one to send a message after the countdown hits 0.\nFalse starts result in death.',
          mentions: [{tag: message.mentions[args[1]], id: args[1]}]
        });
        duel.timestamp = 0;
        duel.isActive = true;
        setTimeout(() => {
          api.sendMessage('3', message.threadID);
          setTimeout(() => {
            api.sendMessage('2', message.threadID);
            setTimeout(() => {
              api.sendMessage('1', message.threadID);
              setTimeout(() => {
                api.sendMessage('DRAW!', message.threadID, (err, msg) => {
                  duel.timestamp = msg.timestamp;
                });
              }, 1000);
            }, 1000);
          }, 1000);
        }, 1000);

        setTimeout(function () {
          if (args[1] in commands.duels && commands.duels[args[1]].timestamp == duel.timestamp) {
            delete commands.duels[message.senderID];
            api.sendMessage('The duel has timed out.', message.threadID);
          }
        }, 15000);
      } else {
        api.sendMessage('You cannot accept that duel.', message.threadID);
      }
    } else {
      api.sendMessage('That user has no outstanding duels.', message.threadID);
    }
  }
});
