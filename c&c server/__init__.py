from flask import Flask, jsonify, render_template, url_for
from flask_socketio import SocketIO


app = Flask(__name__)
socketio = SocketIO(app)

@socketio.on("pong")
def reply(msg):
    print("received pong")

@app.route("/", methods=['GET'])
def index():
    return render_template("index.html"), 200

@app.route("/dashboard", methods=['GET'])
def dashboard():
    return jsonify({'htmlresponse': render_template("dashboard.html")}), 200

@app.route("/files", methods=['GET'])
def files():
    return jsonify({'htmlresponse': render_template("files.html")}), 200

@app.route("/settings", methods=['GET'])
def settings():
    return jsonify({'htmlresponse': render_template("settings.html")}), 200

@app.route("/help", methods=['GET'])
def help():
    return jsonify({'htmlresponse': render_template("help.html")}), 200

@app.route("/about", methods=['GET'])
def about():
    return jsonify({'htmlresponse': render_template("about.html")}), 200

@app.errorhandler(404)
def page_not_found(e):
    return render_template('404.html'), 404

if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5001, debug=True)