#!flask/bin/python

from flask import Flask, jsonify, make_response, request
import random
from pymongo import MongoClient
import base64
import os

app = Flask(__name__)

client = MongoClient('localhost', 27017)
db = client.yoin

def generateId():
  return base64.urlsafe_b64encode(os.urandom(15)).decode('ascii') # 120 bits of randomness

ids = dict()

def makeErrorResponse(error, message):
  return make_response(jsonify({'error': error, 'Message': message}), error)

def makeDocument(_id, name, phone):
  return {'_id': _id, 'Name': name, 'Phone': phone}

def insertDocument(document):
  db.cards.insert(document)

def getDocument(_id):
  return db.cards.find_one({'_id': _id})

@app.route('/')
def index():
  return 'Hello World!'

@app.route('/add', methods=['POST'])
def add():
  if not request.json:
    return makeErrorResponse(400, 'Bad Post')

  newId = dict()
  newId['name'] = request.json['name']
  newId['phone'] = request.json['phone']
  idNumber = generateId()
  ids[idNumber] = newId
  insertDocument(makeDocument(idNumber, request.json['name'], request.json['phone']))

  return str(idNumber)

@app.route('/restAdd/<string:name>/<string:phone>', methods=['GET'])
def restAdd(name, phone):
  newId = dict()
  newId['name'] = name
  newId['phone'] = phone
  
  idNumber = generateId()
  while getDocument(idNumber) != None: # This really shouldn't happen, the risk is ridiculously small.
    idNumber = generateId()
    print('WARNING!!! Duplicated id, something is probably wrong!!!!!!!!!!!!')
  print('Created id: ' + str(idNumber))
  ids[idNumber] = newId
  insertDocument(makeDocument(idNumber, name, phone))

  return str(idNumber)

@app.route('/get/<string:idNumber>', methods=['GET'])
def getCard(idNumber):
  print(idNumber)
  document = getDocument(idNumber)
  if document:
    del document['_id']
    return jsonify(document)
  else :
    return makeErrorResponse(404, 'Not Found')

if __name__ == '__main__':
  app.run(debug=True, host='0.0.0.0', port=8080)
