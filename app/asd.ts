import powerSet from "power-set-x";

// const numBowls = 27;
const numBowls = 27;
const numServants = 3;

interface Action {
  servant: number;
  eaten: number[];
  when: number;
}

interface State {
  actions: Action[];
  timestep: number;
}

let bowls: number[] = [];
for (let i = 0; i < numBowls; i++) {
  bowls.push(i);
}

let servants: number[] = [];
for (let i = 0; i < numServants; i++) {
  servants.push(i);
}

function getRand<T>(a: T[]): T {
  return a[Math.floor(Math.random() * a.length)];
}

function isValid(a: Action, soFar: Action[]) {
  const theyAte = soFar
    .filter((x) => x.servant === a.servant)
    .flatMap((x) => x.eaten)
    .reduce((s, v) => s.add(v), new Set());
  if (a.eaten.some(x => theyAte.has(x))) return false;
  return true;
}

function main() {
  let loseCount = 0;
  const choices: number[][] = powerSet(bowls);
  let soFar = [] as Action[];
  let alive = servants;
  let possibility = bowls;
  // whenever a person dies
  // the possibility collapses to just the bowls that the dead person has had
  game: for (let i = 0; i < 120; i++) {
    if (i === 0) {
      soFar = [];
      alive = servants;
      possibility = bowls;
    }
    for (const action of soFar) {
      if (action.when === i - 45 && action.eaten.length > 0) {
        // on death
        // find out what that person has taken
        const taken = soFar
          .filter((x) => x.servant === action.servant)
          .filter((x) => x.when >= i - 45)
          .flatMap((x) => x.eaten)
          .reduce((a, v) => a.add(v), new Set());
        possibility = possibility.filter((x) => taken.has(x));
        alive = alive.filter((x) => x !== action.servant);
        if (possibility.length === 1) {
          console.log("win? " + i);
          for (const a of soFar) {
            if (a.eaten.length > 0)
            console.dir(a);
          }
          return;
        }
        if (alive.length === 0) {
          console.log("lose all dead " + ++loseCount);
          i = 0;
          continue game;
        }
      }
      if (i === 119) {
        console.log("lose max iter " + ++loseCount);
        i = 0;
        continue game;
      }
    }
    for (const servant of alive) {
      let move: Action = {
        servant,
        eaten: Math.random() > 0.90 ? getRand(choices) : [],
        // eaten: getRand(choices),
        when: i
      }

      while (!isValid(move, soFar)) move = {
        servant,
        eaten: getRand(choices),
        when: i
      };

      soFar.push(move);
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