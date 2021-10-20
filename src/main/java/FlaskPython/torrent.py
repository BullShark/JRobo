from __future__ import with_statement                                                        


import random
import string
import sys
import urllib.parse

import requests

from flask import Flask, request, jsonify
from bs4 import BeautifulSoup

app = Flask(__name__)
app.config["DEBUG"] = False
app.config["API_KEY"] = ''.join(random.SystemRandom().choice(string.ascii_lowercase+string.ascii_uppercase + string.digits) for _ in range(32))

@app.route('/thepiratebay/<query>/<page>', methods=['GET'])
def api_thepiratebay(query, page=0):
    results={}
    resp=requests.get(f"https://thepiratebay.org/search.php?q={query}&all=on&search=Pirate+Search&page={page}&orderby=")
    #parse, build dict and return

    return jsonify(results)

"""
Parse 1337x results and return them as JSON.
"""
@app.route('/1337x/<query>', defaults={"page":"1", "category":None, "sort_by":"seeders", "sort_order":"desc"})
@app.route('/1337x/<query>/<page>/<category>/<sort_by>/<sort_order>', methods=['GET'])
def api_1337x(query, page, category, sort_by, sort_order):
    if request.headers.get("API_KEY") != app.config["API_KEY"]:
        return jsonify({"error": "unauthorized"}), 403

    results=[]

    # Page must be a digit
    if not page.isdigit():
        return jsonify({"error": "invalid page"})
    
    # Sort order must be known
    if sort_order not in ["asc", "desc"]:
        return jsonify({"error": "invalid sort_order"})

    # Sort group must be known
    if sort_by not in ["seeders", "time", "leechers", "size"]:
        return jsonify({"error": "invalid sort_by"})

    # Category must be known
    if category is None:
        url=f"https://1337x.to/sort-search/{query}/{sort_by}/{sort_order}/{page}/"
    elif category not in ["Movies","TV","Games","Music","Apps","Documentaries","Anime","Other","XXX"]:
        return jsonify({"error": "invalid category"})
    else:
        url=f"https://1337x.to/sort-category-search/{query}/{category}/{sort_by}/{sort_order}/{page}/"

    resp=requests.get(url, headers={"User-Agent": "Mozilla/5.0"})
    
    #parse, build dict and return
    if resp.status_code != 200:
        return jsonify({"error": resp.status_code}), resp.status_code

    try:
        soup=BeautifulSoup(resp.content, "html.parser")

        table=soup.find("table", attrs={"class":"table-list table table-responsive table-striped"})
        tbody=soup.find("tbody")
        for tr in tbody.find_all("tr"):
            item={}
            link=tr.find("td", attrs={"class":"coll-1 name"}).find_all("a")[1]
            
            item["href"]	= f"https://1337x.to{link['href']}"
            item["name"]	= link.getText()
            item["seeds"]	= tr.find("td", attrs={"class":"coll-2 seeds"}).getText()
            item["leeches"]	= tr.find("td", attrs={"class":"coll-3 leeches"}).getText()
            item["date"]	= tr.find("td", attrs={"class":"coll-date"}).getText()
            item["tinyurl"]	= make_tiny(item["href"])

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
    except Exception:
        return jsonify({"error": "unknown"}), 500
 
    return jsonify(results)

def make_tiny(url):
    resp = requests.get(f"http://tinyurl.com/api-create.php?url={url}")
    return str(resp.content, "utf-8")

print("[*] API_KEY: {}".format(app.config["API_KEY"]))
app.run(host="0.0.0.0")
