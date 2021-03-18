const fs = require("fs");
const path = require("path");
const chalk = require("chalk");
const util = require("util");

const launchLib = require("./launchLib");
const config = require("../config.json");
const copyFile = util.promisify(fs.copyFile);
const log = launchLib.log;

var serverName = process.argv[2];
if (!serverName) {
    log("Please provide a server name");
    log("Usage: npm run spigot <server-name>");
    process.exit(1);
}

var serverPath = path.resolve("instances", serverName);
var serverJarPath = path.resolve(serverPath, "Spigot.jar");

if (!fs.existsSync(serverPath)) {
    log(`Server ${serverName} does not exist, creating`);
    fs.mkdirSync(serverPath);
}

if (!fs.existsSync(serverJarPath)) {
    log(`Missing Spigot.jar, downloading...`);
    launchLib.downloadFile(config.urls.spigot, serverJarPath, start_server);
} else start_server();

function spigotHighlighter(lines) {
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
    log("Starting Spigot server");
    var cmd = "java -jar Spigot.jar -nogui";
    var options = {
        cwd: serverPath,
        wrapper: spigotHighlighter
    };

    var proc = launchLib.makeProcess(cmd, options, () => {
        log("Spigot server closed");
    });
}
