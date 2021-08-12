from flask import Flask, jsonify
from flask_socketio import SocketIO


app = Flask(__name__)
socketio = SocketIO(app)

@socketio.on("pong")
def reply(msg):
    print("received pong")

@app.route("/")
def index():
    socketio.emit("ping")
    return jsonify("somw"), 200

if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5001, debug=True)