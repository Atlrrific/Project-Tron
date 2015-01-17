from pymongo import MongoClient

client = MongoClient()

client = MongoClient('localhost', 27017) 

# initialize a test database
db = client['test_db']

collection = db.test_collection

test = {
        '_id': 'dvd',
        'author': 'daniel dao', 
        'text': 'heh'    
}

collection.insert(test)
