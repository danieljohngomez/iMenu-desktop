// Modules to control application life and create native browser window
const {app, BrowserWindow} = require('electron')
const url = require('url');
platform = process.platform;

//const cp = require('child_process');
//const s = cp.spawn('bash');
//s.stdin.end('java -jar imenu-desktop-1.0-SNAPSHOT.jar');
//s.once('exit', code => {
//   // exit
//});

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow

function createWindow () {
  // Create the browser window.
  mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    title: 'iMenu',
    webPreferences: {
      nodeIntegration: false
    }
  })
  mainWindow.maximize();
  // and load the index.html of the app.
  mainWindow.loadURL('http://13.92.128.57:8080/menu');

  // Open the DevTools.
  // mainWindow.webContents.openDevTools()

  // Emitted when the window is closed.
  mainWindow.on('closed', function () {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWindow = null
  })

}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function () {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', function () {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (mainWindow === null) {
    createWindow()
  }
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.
