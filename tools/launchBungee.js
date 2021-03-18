const fs = require("fs");
const path = require("path");
const chalk = require("chalk");

const launchLib = require("./launchLib");
const config = require("../config.json");
const log = launchLib.log;

var bungeePath = path.resolve("bungee/BungeeCord.jar");

if (!fs.existsSync(bungeePath)) {
    log(`Downloading BungeeCord.jar from ${config.urls.bungee}`);
    launchLib.downloadFile(config.urls.bungee, bungeePath, () => {
        setTimeout(start_server, 500);
    });
} else start_server();

function bungeeHighlighter(lines) {
    var oldLines = lines.split("\n");
    var newLines = [];

    oldLines.forEach((line) => {
        var matches = line.match(/^\[(.*?)\]:/);
        if (matches) {
            newLines.push(line.replace(matches[0], chalk.red(matches[0])));
        } else {
            newLines.push(line);
        }
    });

    return newLines.join("\n");
}

function start_server() {
    log("Starting BungeeCord server");
    var cmd = "java -Xms512M -Xmx512M -jar BungeeCord.jar";
    var options = {
        cwd: path.resolve("bungee"),
        wrapper: bungeeHighlighter
    };

    var proc = launchLib.makeProcess(cmd, options, () => {
        log("BungeeCord server closed");
    });
}
