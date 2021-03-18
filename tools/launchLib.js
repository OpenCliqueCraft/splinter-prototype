const http = require("http");
const https = require("https");
const fs = require("fs");
const readline = require("readline");
const chalk = require("chalk");
const cp = require("child_process");

function getHttpLib(url) {
    if (url.startsWith("https")) return https;
    return http;
}

module.exports.log = (msg, ...extras) => {
    console.log(chalk.green("[LaunchLibs]"), msg, ...extras);
};

module.exports.makeProcess = (cmd, opts, callback) => {
    var wrapper = opts.wrapper || ((d) => d);

    var rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
        prompt: ""
    });

    var proc = cp.exec(cmd, opts);

    rl.on("line", (line) => {
        proc.stdin.write(line.trim() + "\n");
        rl.prompt();
    });

    proc.stdout.on("data", (data) => {
        var wrapped = wrapper(data.replace("\r", ""));
        readline.cursorTo(process.stdout, 0, process.stdout.rows + 1);
        process.stdout.write(wrapped);
    });

    proc.on("error", () => {
        proc.close();
    });

    proc.on("close", () => {
        rl.close();
        callback();
    });

    return proc;
};

module.exports.downloadFile = (url, output, callback) => {
    var out = fs.createWriteStream(output);
    getHttpLib(url).get(url, (resp) => {
        resp.pipe(out).on("finish", () => {
            callback();
        });
    });
};
