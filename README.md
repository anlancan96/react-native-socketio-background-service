# react-native-socketio

## Getting started

`$ npm install react-native-socketio --save`

### Mostly automatic installation

`$ react-native link react-native-socketio`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-socketio` and add `Socketio.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libSocketio.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.SocketioPackage;` to the imports at the top of the file
  - Add `new SocketioPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-socketio'
  	project(':react-native-socketio').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-socketio/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-socketio')
  	```


## Usage
```javascript
import Socketio from 'react-native-socketio';

// TODO: What to do with the module?
Socketio;
```
