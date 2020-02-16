!/usr/bin/env python
"""
Python script to send traffic captured for Kismet to WSO2 BAM 2.5.0.

Requires:
- Python Thrift Client (https://github.com/chilcano/iot-server-appliances/tree/master/Arduino%20Robot/PC_Clients/PythonRobotController/DirectPublishClient/BAMPythonPublisher)
- Python Kismet Client (https://github.com/chilcano/kismetclient)
- Place the 'sendTrafficFromKismetToWSO2BAM.py' in same level of 'BAMPythonPublisher' and 'kismetclient' folders.

Run:
$ python sendTrafficFromKismetToWSO2BAM.py
"""

import sys
sys.path.append('kismetclient')
sys.path.append('BAMPythonPublisher')
sys.path.append('BAMPythonPublisher/gen-py')
sys.path.append('BAMPythonPublisher/thrift')

from kismetclient import Client as KismetClient
from kismetclient import handlers
from Publisher import *
from pprint import pprint

import logging
import time

log = logging.getLogger('kismetclient')
log.addHandler(logging.StreamHandler())
log.setLevel(logging.DEBUG)

# Kismet Server
address = ('127.0.0.1', 2501)
k = KismetClient(address)
####k.register_handler('TRACKINFO', handlers.print_fields)

# BAM/CEP/Thrift server
cep_ip = '192.168.1.43' # IP address of the server
cep_port = 7713         # Thrift listen port of the server
cep_username = 'admin'  # username
cep_password = 'admin'  # passowrd

# Initialize publisher with ip and port of server
publisher = Publisher()
publisher.init(cep_ip, cep_port)

# Connect to server with username and password
publisher.connect(cep_username, cep_password)

# Define stream definition
streamDefinition = "{ 'name':'rpi_kismet_stream_in', 'version':'1.0.0', 'nickName': 'rpi_k_in', 'description': '802.11 passive packet capture using KismetClient python lib.', 'tags': ['Raspberry Pi 2 Model B', 'Kismet', 'Thrift'], 'metaData':[ {'name'$
publisher.defineStream(streamDefinition)

def handle_client(client, bssid, mac, lasttime, type, llcpackets, datapackets, cryptpackets, signal_dbm, bestlat, bestlon, bestalt, channel, datasize, newpackets):
  publisher.publish(['rpi_chicha','Raspberry Pi 2 Model B','chilcano.io',int(lasttime)],[bssid,mac,type,llcpackets,datapackets,cryptpackets,signal_dbm,bestlat,bestlon,bestalt,channel,datasize,newpackets])

k.register_handler('CLIENT', handle_client)

try:
    while True:
        k.listen()
except KeyboardInterrupt:
    pprint(k.protocols)
    publisher.disconnect()
    log.info('Exiting...')