from __future__ import with_statement                                                        

import flask
import requests
import random
import string
from flask import request, jsonify
from bs4 import BeautifulSoup

import contextlib
  
try:
    from urllib.parse import urlencode          
  
except ImportError:
    from urllib import urlencode
  
try:
    from urllib.request import urlopen
  
except ImportError:
    from urllib2 import urlopen
  
import sys

app = flask.Flask(__name__)
app.config["DEBUG"] = True
app.config["API_KEY"] = ''.join(random.SystemRandom().choice(string.ascii_lowercase+string.ascii_uppercase + string.digits) for _ in range(32))

@app.route('/thepiratebay/<query>/<page>', methods=['GET'])
def api_thepiratebay(query, page=0):
    results={}
    resp=requests.get(f"https://thepiratebay.org/search.php?q={query}&all=on&search=Pirate+Search&page={page}&orderby=")
    #parse, build dict and return

    return jsonify(results)


"""
We can also filter by categories:
    <option value="/category-search/test/Movies/1/">
    <option value="/category-search/test/TV/1/">
    <option value="/category-search/test/Games/1/">
    <option value="/category-search/test/Music/1/">
    <option value="/category-search/test/Apps/1/">
    <option value="/category-search/test/Documentaries/1/">
    <option value="/category-search/test/Anime/1/">
    <option value="/category-search/test/Other/1/">
    <option value="/category-search/test/XXX/1/">
"""
@app.route('/1337x/<query>/<page>/<category>', methods=['GET'])
def api_1337x(query, page=1, category=None):
    if request.headers.get("API_KEY") != app.config["API_KEY"]:
        return jsonify({"error": "unauthorized"}), 403

    results=[]

    if category is None:
        url=f"https://1337x.to/search/{query}/{page}/"
    elif category not in ["Movies","TV","Games","Music","Apps","Documentaries","Anime","Other","XXX"]:
        return jsonify({"error": "unknown category"})
    else:
        url=f"https://1337x.to/search/{query}/{category}/{page}/"

    resp=requests.get(url, headers={"User-Agent": "Mozilla/5.0"})
    
    #parse, build dict and return
    if resp.status_code != 200:
        return jsonify({"error": resp.status_code}), resp.status_code

    soup=BeautifulSoup(resp.content, "html.parser")

    table=soup.find("table", attrs={"class":"table-list table table-responsive table-striped"})
    tbody=soup.find("tbody")
    for tr in tbody.find_all("tr"):
        item={}
        link=tr.find("td", attrs={"class":"coll-1 name"}).find_all("a")[1]
        
        item["href"]    = f"https://1337x.to{link['href']}"
        item["name"]    = link.getText()
        item["seeds"]   = tr.find("td", attrs={"class":"coll-2 seeds"}).getText()
        item["leeches"] = tr.find("td", attrs={"class":"coll-3 leeches"}).getText()
        item["date"]    = tr.find("td", attrs={"class":"coll-date"}).getText()
        item["tinyurl"] = make_tiny(f"https://1337x.to{link['href']}")

        size = tr.find("td", attrs={"class":"coll-4 size mob-user"})
        size = size if size is not None else tr.find("td", attrs={"class":"coll-4 size mob-vip"})
        if size is not None:
            item["size"]=size.contents[0]
        else:
            item["size"]="Unknown"


        user = tr.find("td", attrs={"class":"coll-5 user"})
        user = user if user is not None else tr.find("td", attrs={"class":"coll-5 vip"})

        if user is not None:
            item["user"]=user.contents[0].getText()
        else:
            item["size"]="Unknown"
        results.append(item)
        

    return jsonify(results)
def make_tiny(url):
    request_url = ('http://tinyurl.com/api-create.php?' + url)
    with contextlib.closing(urlopen(request_url)) as response:
        return response.read().decode('utf-8 ')

print("[*] API_KEY: {}".format(app.config["API_KEY"]))
app.run(host="0.0.0.0")
