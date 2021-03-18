const fs = require("fs");
const path = require("path");
const chalk = require("chalk");
const util = require("util");

const launchLib = require("./launchLib");
const config = require("../config.json");
const copyFile = util.promisify(fs.copyFile);
const log = launchLib.log;

var serverName = process.argv[2];
var restartString = process.argv[3];
if (!serverName) {
    log("Please provide a server name");
    log("Usage: npm run spigot <server-name>");
    process.exit(1);
}

var restart = false;
if (restartString == "true") restart = true;

var serverPath = path.resolve("instances", serverName);
var serverJarPath = path.resolve(serverPath, "Spigot.jar");
var serverEulaPath = path.resolve(serverPath, "eula.txt");

if (!fs.existsSync(serverPath)) {
    log(`Server ${serverName} does not exist, creating`);
    fs.mkdirSync(serverPath);
}

if (!fs.existsSync(serverEulaPath)) {
    log("eula.txt does not exist, auto-agreeing");
    fs.writeFileSync(serverEulaPath, "# Generated by LaunchLibs\neula=true");
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
    if (restart) log("Auto restart enabled");
    var cmd = "java -jar Spigot.jar -nogui";
    var options = {
        cwd: serverPath,
        wrapper: spigotHighlighter
    };

    var proc = launchLib.makeProcess(cmd, options, () => {
        log("Spigot server closed");

        if (restart) {
            log("Restarting...");
            start_server();
        }
    });
}
