from flask import Flask, jsonify, render_template, url_for, request, redirect, session
from flask_socketio import SocketIO


app = Flask(__name__)
app.secret_key = 'the rastafarian in you'
socketio = SocketIO(app)


@socketio.on("pong")
def reply(msg):
    print("received pong")


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
        if username == "admin" and password == "password":
            session['username'] = username
            # terminal logs below dashboard pass data from nav link ro confirm original request logout sqlite
            return redirect("/", code=302)
        else:
            return jsonify({'htmlresponse': render_template("error_modal.html", error="Invald Username/Password")}), 401
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/logout", methods=['GET'])
def logout():
    session.pop('username', None)
    return redirect("/login", code=302)


@app.route("/dashboard", methods=['GET'])
def dashboard():
    if request.method == 'GET':
        return jsonify({'htmlresponse': render_template("dashboard.html")}), 200
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/files", methods=['GET'])
def files():
    if request.method == 'GET':
        return jsonify({'htmlresponse': render_template("files.html")}), 200
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/settings", methods=['GET'])
def settings():
    if request.method == 'GET':
        return jsonify({'htmlresponse': render_template("settings.html")}), 200
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/help", methods=['GET'])
def help():
    if request.method == 'GET':
        return jsonify({'htmlresponse': render_template("help.html")}), 200
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.route("/about", methods=['GET'])
def about():
    if request.method == 'GET':
        return jsonify({'htmlresponse': render_template("about.html")}), 200
    else:
        return render_template("error_page.html", code= ["400", "Bad Request"]), 400


@app.errorhandler(404)
def page_not_found(e):
    return render_template('error_page.html', code= ["404", "Not Found"]), 404



if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5001, debug=True)