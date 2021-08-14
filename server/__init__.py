import re
from flask import Flask, jsonify, render_template, url_for, request, redirect, session
from flask_socketio import SocketIO, emit
from passlib.hash import pbkdf2_sha256
import sqlite3

# below dashboard pass data from nav link ro confirm original request
app = Flask(__name__)
app.secret_key = 'the rastafarian in you'
socketio = SocketIO(app)


@socketio.on("pong")
def reply(msg):
    print(msg)
    emit("response", msg)


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
                cur.execute("INSERT INTO user(username, passkey) VALUES (?, ?)", (username, password))
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