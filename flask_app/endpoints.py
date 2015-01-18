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

@app.route('/coordinates', methods=['POST'])
def coordinates():
    usr_object = request.get_json(force=True)
    insert_query = collection.update({'_id':usr_object['_id']}, {'posX': usr_object['posX'], 'posY': usr_object['posY'], 'counter':usr_object['counter']},upsert=True)
    result = {
        'success': 'False'    
    }
    
    if insert_query is not None:
        result['success'] = 'True'
        return Response(json.dumps(result), mimetype='application/json')

    return Response(json.dumps(result), mimetype='application/json')
     
