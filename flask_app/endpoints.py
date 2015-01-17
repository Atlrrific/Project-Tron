from flask import Flask, request, Response

from flask_app import app 
from util_functions import *

from pymongo import MongoClient
import json

client = MongoClient()
db = client['test_db']
collection = db.test_collection

@app.route('/', methods=['GET'])
def index():
    return 'Hello world!'


@app.route('/user/<id>', methods=['GET'])
def user(id):
    result = collection.find_one({'_id': id}) 
    return Response(json.dumps(result), mimetype='application/json')


