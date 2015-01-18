from flask import Flask, request, Response

from flask_app import app 
from util_functions.utils import calculateCollision

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
    other_id = 'player_one' 
    
    if id == 'player_one':
        other_id = 'player_two'
    
    check = collection.find_one({'_id': other_id})
    
    if check is not None: 
        result['other_player'] = 'True'
    
    collection.update({'_id' : id}, result, upsert=False)

      
    return Response(json.dumps(result), mimetype='application/json')

@app.route('/coordinates', methods=['POST'])
def coordinates():
    usr_object = request.get_json(force=True)
    user = collection.find_one({'_id' : usr_object['_id']}) 

    if usr_object['_id'] == 'player_one' and user is not None:
        usr_object['_id'] = 'player_two'
         
    other_player = 'False'
    other_player_name = 'player_one'
    
          
    if usr_object['_id'] == 'player_one':
        other_player_name = 'player_two'
        
    if collection.find_one({'_id': other_player_name}) is not None:
        other_player = 'True'   
    
    if user is None:
        print 'user is none'
        insert_query = collection.insert({ '_id' : usr_object['_id'],
                            'coordinates': [(usr_object['latitude'], usr_object['longitude'])],
                            'other_player': other_player
        })

    else:
        if (usr_object['latitude'], usr_object['longitude']) in user['coordinates']:
            pass
        else:
            user['coordinates'].append((usr_object['latitude'], usr_object['longitude']))
        user['other_player'] = other_player 
        insert_query = collection.update(
            {'_id' : usr_object['_id']},
            user,
            upsert=False)
        
    #seperate query to determine collisions
    name = None
    if usr_object['_id'] == 'player_one':
        name = 'player_two' 
    else: 
        name = 'player_one'
    
    result = {
        'success': 'False',
        'collision': 'False'    
    }

    if other_player == 'True':
        user_two = collection.find_one({'_id': name})
        user_two_coordinates = user_two['coordinates']
        current_user = collection.find_one({'_id': usr_object['_id']})
        current_coordinates = current_user['coordinates']       
        #determine collisions
        
        num_list_cur = []
        num_list_other = []
        for item in current_user['coordinates']:
            num_list_cur.append((float(item[0]), float(item[1])))

        for item in user_two_coordinates:
            num_list_other.append((float(item[0]), float(item[1])))

        if current_user['_id'] == 'player_one':             
            result['collision'] = str(calculateCollision(num_list_cur, num_list_other, 1))
        else:
            result['collision'] = str(calculateCollision(num_list_cur, num_list_other, 2))
             
    
    
    if insert_query is not None:
        result['success'] = 'True'
        return Response(json.dumps(result), mimetype='application/json')

    return Response(json.dumps(result), mimetype='application/json')


@app.route('/clear_data', methods=['POST'])
def clear_players():
    collection.remove({'_id': 'player_one'})
    collection.remove({'_id': 'player_two'})
    
    result = {
            'success': 'True'
    }
    
    return Response(json.dumps(result), mimetype='application/json') 
            
