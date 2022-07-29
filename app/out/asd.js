"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var power_set_x_1 = require("power-set-x");
// const numBowls = 27;
var numBowls = 4;
var numServants = 3;
var bowls = [];
for (var i = 0; i < numBowls; i++) {
    bowls.push(i);
}
var servants = [];
for (var i = 0; i < numServants; i++) {
    servants.push(i);
}
function getRand(a) {
    return a[Math.floor(Math.random() * a.length)];
}
function isValid(a, soFar) {
    var theyAte = soFar
        .filter(function (x) { return x.servant === a.servant; })
        .flatMap(function (x) { return x.eaten; })
        .reduce(function (s, v) { return s.add(v); }, new Set());
    if (a.eaten.some(function (x) { return theyAte.has(x); }))
        return false;
    return true;
}
function main() {
    var loseCount = 0;
    var choices = (0, power_set_x_1.default)(bowls);
    var soFar = [];
    var alive = servants;
    var possibility = bowls;
    var _loop_1 = function (i) {
        if (i === 0) {
            soFar = [];
            alive = servants;
            possibility = bowls;
        }
        var _loop_2 = function (action) {
            if (action.when === i - 45) {
                // on death
                // find out what that person has taken
                var taken_1 = soFar
                    .filter(function (x) { return x.servant === action.servant; })
                    .filter(function (x) { return x.when >= i - 45; })
                    .flatMap(function (x) { return x.eaten; })
                    .reduce(function (a, v) { return a.add(v); }, new Set());
                possibility = possibility.filter(function (x) { return taken_1.has(x); });
                alive = alive.filter(function (x) { return x !== action.servant; });
                if (possibility.length === 1) {
                    console.log("win?");
                    for (var _b = 0, soFar_2 = soFar; _b < soFar_2.length; _b++) {
                        var a = soFar_2[_b];
                        console.dir(a);
                    }
                    return { value: void 0 };
                }
                if (alive.length === 0) {
                    console.log("lose all dead " + ++loseCount);
                    i = 0;
                    return "continue-game";
                }
            }
            if (i === 119) {
                console.log("lose max iter " + ++loseCount);
                i = 0;
                return "continue-game";
            }
        };
        for (var _i = 0, soFar_1 = soFar; _i < soFar_1.length; _i++) {
            var action = soFar_1[_i];
            var state_2 = _loop_2(action);
            if (typeof state_2 === "object")
                return state_2;
            switch (state_2) {
                case "continue-game": return state_2;
            }
        }
        for (var _a = 0, alive_1 = alive; _a < alive_1.length; _a++) {
            var servant = alive_1[_a];
            var move = {
                servant: servant,
                eaten: getRand(choices),
                when: i
            };
            while (!isValid(move, soFar))
                move = {
                    servant: servant,
                    eaten: getRand(choices),
                    when: i
                };
            soFar.push(move);
        }
        out_i_1 = i;
    };
    var out_i_1;
    // whenever a person dies
    // the possibility collapses to just the bowls that the dead person has had
    game: for (var i = 0; i < 120; i++) {
        var state_1 = _loop_1(i);
        i = out_i_1;
        if (typeof state_1 === "object")
            return state_1.value;
        switch (state_1) {
            case "continue-game": continue game;
        }
    }
}
console.log("starting?");
main();
// function getDead(state: State) {
//   const dead = state.actions
//     .filter((x) => x.when + 45 <= state.timestep)
//     .map((x) => x.servant)
//     .reduce((s, v) => s.add(v), new Set());
//   return dead;
// }
// function getAlive(state: State) {
//   const dead = getDead(state);
//   return servants.filter((x) => !dead.has(x));
// }
// function* step(state: State) {
//   const dead = getDead(state);
//   const alive = servants.filter((x) => !dead.has(x));
//   const choices = powerSet(bowls) as number[][];
//   for (const servant of alive) {
//     for (const choice of choices) {
//       yield {
//         actions: state.actions.concat({
//           servant,
//           when: state.timestep,
//           eaten: choice,
//         }),
//       } as State;
//     }
//   }
//   // return active.flatMap(movers => powerSet(bowls).map(b => ({
//   //   actions:
//   // }) as State));
// }
// function getViableLocations(state: State) {
//   const dead = getDead(state);
//   let possibleLocations = bowls;
//   // const knownPoisoned = state.actions
//   //   .filter((x) => x.when + 45 <= state.timestep)
//   //   .flatMap((x) => x.eaten)
//   //   .reduce((s, v) => s.add(v), new Set());
//   for (let i=0; i<120; i++) {
//   }
// }
// function main() {
//   const toProcess = [
//     {
//       actions: [],
//       timestep: 0,
//     } as State,
//   ];
//   while (toProcess.length > 0) {
//     const current = toProcess.shift()!;
//     if (getViableLocations(current).length <= 1) { // win
//     } else if (unwinnable) { // unwinnable
//     } else {
//       toProcess.push(...step(current));
//     }
//   }
// }
// main();
// function dead(state: State): number[] {
//   const rtn = state.actions.filter(x => x.when + 45 <= state.timestep).map(x => x.servant);
//   return Array.from(new Set(rtn));
// }
// function getUncertainBowls(state: State): number[] {
//   let couldHave: number[] = [];
//   // for (let i=0; i<numBowls; i++) {
//   // }
// }
//# sourceMappingURL=asd.js.map