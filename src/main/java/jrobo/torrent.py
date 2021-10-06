import flask
import requests
from flask import request, jsonify

app = flask.Flask(__name__)
app.config["DEBUG"] = True

#todo: add basic auth to api so doesnt get abused
@app.route('/thepiratebay/<query>/<page>', methods=['GET'])
def api_thepiratebay(query, page=0):
    results={}
    resp=requests.get(f"https://thepiratebay.org/search.php?q={query}&all=on&search=Pirate+Search&page={page}&orderby=")
    #parse, build dict and return

    return jsonify(results)

@app.route('/1337x/<query>/<page>', methods=['GET'])
def api_1337x(query, page=1):
    results={}
    resp=requests.get(f"https://1337x.to/search/{query}/{page}/")
    #parse, build dict and return

    return jsonify(results)

app.run()
