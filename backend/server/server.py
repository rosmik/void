#!flask/bin/python

from flask import Flask, jsonify, make_response, request
import random
from pymongo import MongoClient
import base64
import os
import json

app = Flask(__name__)

client = MongoClient('localhost', 27017)
db = client.yoin

def generateId():
  return base64.urlsafe_b64encode(os.urandom(15)).decode('ascii') # 120 bits of randomness

def makeErrorResponse(error, message):
  return make_response(jsonify({'error': error, 'Message': message}), error)

def makeDocument(_id, data):
  data['_id'] = _id
  return data.copy()

def insertDocument(document):
  db.cards.insert(document)

def getDocument(_id):
  return db.cards.find_one({'_id': _id})

def createVcard(idCard):
  vcard = """BEGIN:VCARD
VERSION:2.1
N:$first;$last;;;
FN:$fullname
TEL;CELL:$phone
END:VCARD"""
  try:
    name = idCard['Name']
    phone = idCard['Phone']
  except KeyError:
    print('Invalid idCard to createVcard')
    return None

  t = name.split()[0]
  first = t[0]
  if(len(t) > 1):
    last = t[1]
  else:
    last = ''
  vcard = vcard.replace('$first', first)
  vcard = vcard.replace('$last', last)
  vcard = vcard.replace('$fullname', name)
  vcard = vcard.replace('$phone', phone)

  return vcard


@app.route('/')
def index():
  return 'Hello World!'

@app.route('/add', methods=['POST'])
def add():
  print('Data: ' + request.data)
  print('Values: ' + str(request.values))
  print('Blueprint: ' + str(request.blueprint))
  print('Headers: ' + str(request.headers))
  if not request.json:
    return makeErrorResponse(400, 'Bad Post')

  print(type(request.get_json()))

  idNumber = generateId()

  insertDocument(makeDocument(idNumber, request.get_json()))
  
  print('Added card with id: ' + str(idNumber))

  return str(idNumber)

@app.route('/add/<string:name>/<string:phone>', methods=['GET'])
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
    return jsonify(document)
  else :
    return makeErrorResponse(404, 'Not Found')

@app.route('/getVcard/<string:idNumber>', methods=['GET'])
def getVcard(idNumber):
  print(idNumber)
  document = getDocument(idNumber)
  if not document:
    return makeErrorResponse(404, 'Not Found')
  
  try:
    name = document['Name']
  except KeyError:
    name = idNumber
  print(document)
  vcard = createVcard(document)
  response = make_response(vcard)
  response.headers['Content-Type'] = 'text/vcard'
  response.headers['Content-Disposition'] = 'attachment; filename="' + name + '.vcf"'

  return response

if __name__ == '__main__':
  app.run(debug=True, host='0.0.0.0', port=8080)
