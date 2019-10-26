import { DeviceEventEmitter, NativeModules, Platform } from 'react-native';

const { Socketio } = NativeModules;

/** 
 * @typedef {{
    *            forcenew:boolean,
    *            multiplex:boolean,
    *            reconnection:boolean,
    *            connect_timeout: number,
    *            reconnectionAttempts: number,
    *            reconnectionDelay: number
    *       }}
    */
var Option;
   
class IO {
    /**
     * 
     * @param {String} host 
     * @param {Option} config 
     */
    constructor(host, config){
        
        if (typeof host === 'undefined')
            throw 'Host is undefined';
        if (typeof config === 'undefined')
            config = {};
        this.sockets = Socketio;
        this.isConnected = false;
        this.handlers = {};
        this.onAnyHandler = null;

        this.deviceEventSubscription = DeviceEventEmitter.addListener(
            'socketEvent', this._handleEvent.bind(this)
        );

        this.sockets.initialize(host, config);
    }

    /**
     * 
     * @param {Object} event 
     * @private
     */
    _handleEvent (event) {
        if (this.handlers.hasOwnProperty(event.name)) {
          this.handlers[event.name](
            (event.hasOwnProperty('items')) ? event.items : null
          );
        }
    }

    connect () {
        this.sockets.connect();
        return this;
    }
    
    /**
     * 
     * @param {String} event 
     * @param {Function} handler 
     */
    on (event, handler) {
        this.handlers[event] = handler;
        if (Platform.OS === 'android') {
          this.sockets.on(event);
        }
        return this;
    }

    /**
     * 
     * @param {String} event 
     * @param {Object} data 
     */
    emit (event, data) {
        this.sockets.emit(event, data);
        return this;
    }

    disconnect () {
        this.sockets.disconnect();
        return this;
    }
}


export default IO;
