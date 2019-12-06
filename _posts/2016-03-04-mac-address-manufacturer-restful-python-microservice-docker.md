---
layout: post
title: A MAC Address Manufacturer DB and RESTful Python Microservice in a Docker Container
date: 2016-03-04 12:06:14.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Big Data
- Microservices
- Security
- SOA
tags:
- Docker
- Flask
- Kismet
- Python
- WSO2 BAM
meta:
  _wpcom_is_markdown: '1'
  _edit_last: '578869'
  geo_public: '0'
  _publicize_done_external: a:1:{s:7:"twitter";a:1:{i:13849;s:54:"https://twitter.com/Chilcano/status/705711044488855552";}}
  _publicize_job_id: '20424949607'
  publicize_google_plus_url: https://plus.google.com/+RogerCarhuatocto/posts/VUBvfFT7YvT
  _publicize_done_5110107: '1'
  _wpas_done_5053089: '1'
  publicize_linkedin_url: https://www.linkedin.com/updates?discuss=&scope=6985267&stype=M&topic=6111476669703348224&type=U&a=727X
  _publicize_done_5110110: '1'
  _wpas_done_5053092: '1'
  _publicize_done_17477: '1'
  _wpas_done_13849: '1'
  publicize_twitter_user: Chilcano
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2016/03/04/mac-address-manufacturer-restful-python-microservice-docker/"
---
A MAC address, also called physical address, is a unique identifier assigned to every network interfaces for communications on the physical network segment. In other words, you can identify the manufacturer of your device through your pyshical address.  
  
There are different tools on the Internet that allow you to identify the manufacturer from the MAC Address. How in my 3 previous post I wrote about how to capture the wireless traffic and all MAC Address, now in this post I will explain how to implement a Docker container exposing a Rest API to get the Manufacturer from the captured MAC Address.  
  
As everything should be lightweight, minimalist, easy to use and auto-contained, I'm going to use the next:  
  
\- `Python` as lightweight and powerful programming language.  
  
\- `Flask` (http://flask.pocoo.org) is a microframework for Python based on Werkzeug and Jinja 2. I will use `Flask` to implement a mini-web application.  
  
\- `SQLAlchemy` (http://www.sqlalchemy.org/) is a Python SQL toolkit and ORM.  
  
\- `SQLite3` (https://www.sqlite.org) is a software library that implements a self-contained, serverless, zero-configuration, transactional SQL database engine.  
  
\- `pyOpenSSL` library to work with X.509 certificates. Required to start the embedded Webserver on HTTPS (TLS).  
  
\- `CORS extension for Flask` (https://flask-cors.readthedocs.org) useful to solve cross-domain Ajax request issues.

  


![The MAC Address Manufacturer Lookup Docker Container]({{ site.baseurl }}/assets/chilcano_docker_microservice_mac_address_manuf_lookup_2.png)

  


  


This Docker container provides a Microservice (API Rest) to MAC Address Manufacturer resolution. This Docker container is part of the "Everything generates Data: Capturing WIFI Anonymous Traffic using Raspberry Pi and WSO2 BAM" blog serie ([Part I](http://ow.ly/YcEf1), [Part II](http://ow.ly/YcEgz) & [Part III](http://ow.ly/YcEij)), but you can use it independently as part of other set of Docker containers.

  


This Docker Container will work in this scenario, as shown above image. Then, let's do it.

  


## I. Preparing the Python development environment in Mac OSX

  


Follow this guide to setup your Python Development Environment in your Mac OSX: https://github.com/chilcano/how-tos/blob/master/Preparing-Python-Dev-Env-Mac-OSX.md

  


## II. Creating a MAC Address Manufacturer DB

  


Exist in Internet several MAC Address Lookup Tools, in fact, the OUI's prefix used to identify the MAC Address are public available.

  


But, in this case I am going to use the MAC Address List of Wireshark (https://www.wireshark.org/tools/oui-lookup.html).  
  
Wireshark is a popular network protocol analyzer a.k.a. `network sniffer`, the Wireshark tool uses internally the MAC Address list to identity the Manufacturer of a NIC.  
  
I'm going to download and create a API Rest for you. Below the steps.

  


 **1) Downloading the Wireshark MAC Addresses Manufacturer file and loading into a DB**

  


Using the below Python script I will download the Wireshark MAC Address list into a file and to get the hash. The idea is to parse the file and load it into a minimalist DB.  
  
I will use SQLite Database where I will create an unique table and all information will be loaded there. The Table structure will be:

  


```sh  
  
mac String # The original MAC Address  
  
manuf String # The original Manufacturer name  
  
manuf_desc String # The Manufacturer description, if exists.  
  
```

  


Here the Python script used to do that: [mac_manuf_wireshark_file.py](https://github.com/chilcano/docker-mac-address-manuf-lookup/blob/master/python/latest/mac_manuf_wireshark_file.py)

  


## III. Exposing the MAC Address Manufacturer DB as an API Rest

  


After creating the database, the next step is to expose the data through a simple API Rest. The idea is to make a call `GET` to the API Rest with a `MAC Address` and get the `Manufacturer` as response.

  


 **1) Defining the API**

  


The best way to define an API Rest and the `contract` is using the `Swagger` language (http://swagger.io). The idea is to create documentation about the API Rest and explain what resources are available or exposed, writte a request and response sample, etc.  
In this scenario I'm going to define in a simple way the API, also I'm going to use JSON to define the request and response.  
Then, below the API definition.

```sh  
POST /chilcano/api/manuf # Add a new Manufacturer  
PUT /chilcano/api/manuf # Update an existing Manufacturer  
GET /chilcano/api/manuf/{macAddress} # Find Manufacturer by MAC Address  
```

In this Proof-of-Concept I will implement only the `GET` resource for the API.

**2) Implementing the API Rest**

I have created 2 Python scripts to implement the API Rest.  
The first one ([`mac_manuf_table_def.py`](https://github.com/chilcano/docker-mac-address-manuf-lookup/blob/master/python/latest/mac_manuf_table_def.py)) is just a `Model` of the `MacAddressManuf` table.

```python  
#!/usr/bin/python  
# -*- coding: utf-8 -*-  
#  
# file name: mac_manuf_table_def.py  
#

from sqlalchemy import create_engine, ForeignKey  
from sqlalchemy import Column, Date, Integer, String  
from sqlalchemy.ext.declarative import declarative_base

engine = create_engine('sqlite:///mymusic.db', echo=True)  
Base = declarative_base()

#  
# Model for 'MacAddressManuf':  
# used for API Rest to get access to data from DB  
#  
class MacAddressManuf(Base):  
""""""  
__tablename__ = "MacAddressManuf"

mac = Column(String, primary_key=True)  
manuf = Column(String)  
manuf_desc = Column(String)

def __init__(self, manuf, manuf_desc):  
""""""  
self.manuf = manuf  
self.manuf_desc = manuf_desc  
```

And second Python script ([`mac_manuf_api_rest.py`](https://github.com/chilcano/docker-mac-address-manuf-lookup/blob/master/python/latest/mac_manuf_api_rest.py)) implements the API Rest. You can review the

```python  
#!/usr/bin/python  
# -*- coding: utf-8 -*-  
#  
# file name: mac_manuf_api_rest.py  
#

import os, re  
from flask import Flask, jsonify  
from flask.ext.cors import CORS  
from sqlalchemy import create_engine  
from sqlalchemy.orm import sessionmaker  
from mac_manuf_table_def import MacAddressManuf

ROOT_DIR = "manuf"  
FINAL_MANUF_DB_FILENAME = "mac_address_manuf.db"  
HTTPS_ENABLED = "true"

engine = create_engine("sqlite:///" \+ os.path.join(ROOT_DIR, FINAL_MANUF_DB_FILENAME))  
Session = sessionmaker(bind=engine)

app = Flask(__name__)  
cors = CORS(app, resources={r"/chilcano/api/*": {"origins": "*"}})

#  
# API Rest:  
# i.e. curl -i http://localhost:5000/chilcano/api/manuf/00:50:5a:e5:6e:cf  
# i.e. curl -ik https://localhost:5443/chilcano/api/manuf/00:50:5a:e5:6e:cf  
#  
@app.route("/chilcano/api/manuf/<string:macAddress>", methods=["GET"])  
def get_manuf(macAddress):  
try:  
if re.search(r'^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$', macAddress.strip(), re.I).group():  
# expected MAC formats : a1-b2-c3-p4-q5-r6, a1:b2:c3:p4:q5:r6, A1:B2:C3:P4:Q5:R6, A1-B2-C3-P4-Q5-R6  
mac1 = macAddress[:2] + ":" \+ macAddress[3:5] + ":" \+ macAddress[6:8]  
mac2 = macAddress[:2] + "-" \+ macAddress[3:5] + "-" \+ macAddress[6:8]  
mac3 = mac1.upper()  
mac4 = mac2.upper()  
session = Session()  
result = session.query(MacAddressManuf).filter(MacAddressManuf.mac.in_([mac1, mac2, mac3, mac4])).first()  
try:  
return jsonify(mac=result.mac, manuf=result.manuf, manuf_desc=result.manuf_desc)  
except:  
return jsonify(error="The MAC Address '" \+ macAddress + "' does not exist"), 404  
else:  
return jsonify(mac=macAddress, manuf="Unknown", manuf_desc="Unknown"), 404  
except:  
return jsonify(error="The MAC Address '" \+ macAddress + "' is malformed"), 400

if __name__ == "__main__":  
if HTTPS_ENABLED == "true":  
# 'adhoc' means auto-generate the certificate and keypair  
app.run(host="0.0.0.0", port=5443, ssl_context="adhoc", threaded=True, debug=True)  
else:  
app.run(host="0.0.0.0", port=5000, threaded=True, debug=True)  
```

This second Python script performs the next tasks:

  * Calls the Model (`mac_manuf_table_def.py`).
  * Connects to SQLite Database and creates a Session.
  * Runs a query by using `macAddress` as parameter.
  * And creates a JSON response with the query's result.



**3) Running and Testing the API Rest**

We could use the `Flask` buit-in HTTP server just for testing and debugging. To run the above Python Web Application (API Rest) just execute the Python script. Note that actually I have 3 versions (py-1.0, py-1.1 and py-latest)

```sh  
Chilcano@Pisc0 : ~/1github-repo/docker-mac-address-manuf-lookup/python/1.0  
$ python mac_manuf_api_rest.py  
* Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)  
* Restarting with stat  
* Debugger is active!  
* Debugger pin code: 258-876-642

Chilcano@Pisc0 : ~/1github-repo/docker-mac-address-manuf-lookup/python/1.1  
$ python mac_manuf_api_rest.py  
* Running on https://0.0.0.0:5443/ (Press CTRL+C to quit)  
* Restarting with stat  
* Debugger is active!  
* Debugger pin code: 258-876-642

Chilcano@Pisc0 : ~/1github-repo/docker-mac-address-manuf-lookup/python/latest  
$ python mac_manuf_api_rest.py  
* Running on https://0.0.0.0:5443/ (Press CTRL+C to quit)  
* Restarting with stat  
* Debugger is active!  
* Debugger pin code: 258-876-642  
```

Now, from other Terminal call the API Rest using `curl`, I'm going to use only the `python-lates` version:

```sh  
$ curl -ik https://127.0.0.1:5443/chilcano/api/manuf/00-50:Ca-Fe-Ca-Fe  
HTTP/1.0 200 OK  
Content-Type: application/json  
Content-Length: 93  
Server: Werkzeug/0.11.4 Python/2.7.11  
Date: Thu, 03 Mar 2016 17:37:45 GMT

{  
"mac": "00:50:CA",  
"manuf": "NetToNet",  
"manuf_desc": "# NET TO NET TECHNOLOGIES"  
}

$ curl -ik https://127.0.0.1:5443/chilcano/api/manuf/11-50:Ca-Fe-Ca-Fe  
HTTP/1.0 404 NOT FOUND  
Content-Type: application/json  
Content-Length: 67  
Server: Werkzeug/0.11.4 Python/2.7.11  
Date: Thu, 03 Mar 2016 17:38:49 GMT

{  
"error": "The MAC Address '11-50:Ca-Fe-Ca-Fe' does not exist"  
}

curl -ik https://127.0.0.1:5443/chilcano/api/manuf/00-50:Ca-Fe-Ca-Fe---  
HTTP/1.0 400 BAD REQUEST  
Content-Type: application/json  
Content-Length: 68  
Server: Werkzeug/0.11.4 Python/2.7.11  
Date: Thu, 03 Mar 2016 17:39:23 GMT

{  
"error": "The MAC Address '00-50:Ca-Fe-Ca-Fe---' is malformed"  
}  
```

But if you want to run in Production. In the `Flask` webpage (http://flask.pocoo.org/docs/0.10/deploying/wsgi-standalone) recommends these HTTP servers (Standalone WSGI Containers):

  * Gunicorn
  * Tornado
  * Gevent
  * Twisted Web



## IV. Putting everything in a Docker Container

**1) The Dockerfile**

The latest version of the MAC Address Manufacturer lookup Docker container is the `python-latest` (aka `Docker MAC Manuf`) and has the next Dockerfile:

```sh  
# Dockerfile to MAC Address Manufacturer Lookup container.

FROM python:2.7

MAINTAINER Roger CARHUATOCTO <chilcano at intix dot info>

RUN pip install --upgrade pip  
RUN pip install unicodecsv  
RUN pip install Flask  
RUN pip install sqlalchemy  
RUN pip install pyOpenSSL  
RUN pip install -U flask-cors

# Allocate the 5000/5443 to run a HTTP/HTTPS server  
EXPOSE 5000 5443

COPY mac_manuf_wireshark_file.py /  
COPY mac_manuf_table_def.py /  
COPY mac_manuf_api_rest.py /

RUN python mac_manuf_wireshark_file.py  
CMD python mac_manuf_api_rest.py  
```

**2) Using the Docker Container**

Clone the Github repository and build it.

```sh  
$ git clone https://github.com/chilcano/docker-mac-address-manuf-lookup.git  
$ cd docker-mac-address-manuf-lookup  
$ docker build --rm -t chilcano/mac-manuf:py-latest python/latest/.  
```

Or Pull from Docker Hub.

```sh  
$ docker login  
$ docker pull chilcano/mac-manuf-lookup:py-latest  
$ docker images  
REPOSITORY TAG IMAGE ID CREATED VIRTUAL SIZE  
chilcano/mac-manuf-lookup py-latest 19d33a4f3ec1 16 minutes ago 714.8 MB  
```

Run and check the container.

```sh  
$ docker run -dt --name=mac-manuf-py-latest -p 5443:5443/tcp chilcano/mac-manuf-lookup:py-latest

$ docker ps  
CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES  
4b0bb0b5b518 chilcano/mac-manuf-lookup:py-latest "/bin/sh -c 'python m" 2 minutes ago Up 2 minutes 5000/tcp, 0.0.0.0:5443->5443/tcp mac-manuf-py-latest  
```

Gettting SSH access to the Container to check if SQLite DB exists.

```sh  
$ docker exec -ti mac-manuf-py-latest bash  
```

Getting the Docker Machine IP Address.

```sh  
$ docker-machine ls  
NAME ACTIVE DRIVER STATE URL SWARM ERRORS  
default * virtualbox Running tcp://192.168.99.100:2376  
machine-dev - virtualbox Stopped  
machine-test - virtualbox Stopped  
```

Testing/Calling the Microservice (API Rest).

```sh  
$ curl -i http://192.168.99.100:5000/chilcano/api/manuf/00-50:Ca-ca-fe-ca  
HTTP/1.0 200 OK  
Content-Type: application/json  
Content-Length: 93  
Server: Werkzeug/0.11.4 Python/2.7.11  
Date: Sat, 20 Feb 2016 09:01:38 GMT

{  
"mac": "00:50:CA",  
"manuf": "NetToNet",  
"manuf_desc": "# NET TO NET TECHNOLOGIES"  
}  
```

If the embedded server was started on HTTPS, you could test it as shown below.

```sh  
$ curl -ik https://192.168.99.100:5443/chilcano/api/manuf/00-50:Ca-ca-fe-ca  
HTTP/1.0 200 OK  
Content-Type: application/json  
Content-Length: 93  
Server: Werkzeug/0.11.4 Python/2.7.11  
Date: Mon, 29 Feb 2016 15:58:21 GMT

{  
"mac": "00:50:CA",  
"manuf": "NetToNet",  
"manuf_desc": "# NET TO NET TECHNOLOGIES"  
}  
```

 

## V. And now what?, How to use the MAC Manuf Docker with the WSO2 BAM Docker?

_Visualizing Captured WIFI Traffic in Realtime from WSO2 BAM Dashboard_  
![Visualizing Captured WIFI Traffic in Realtime]({{ site.baseurl }}/assets/chilcano-wso2bam-wifi-thrift-cassandra-4-kismet.png)

As you can see in above image, when capturing WIFI traffic the information is shown in the WSO2 BAM Dashboard but not the MAC Address Manufaturer.  
In this scenario, our `Docker MAC Manuf` will be useful because It will provide the Manufacturer information via a RESTful Microservice. Then, the idea is configure the WSO2 BAM Dashboard (the prepared Kismet Toolbox) to point to the `Docker MAC Manuf` RESTful Microservice. In other words, the WSO2 BAM will call to the `Docker MAC Manuf` Microservice to get the Manufacturer information.

The next blog post I will explain how to connect the MAC Address Manufacturer Docker Container with the WSO2 BAM Docker Container by using [Docker Compose](https://docs.docker.com/compose) to do a minimal orchestration.

## VI. Conclusions

`Python` and a few modules (as Flask, SQLAlchemy, CORS, pyOpenssl, ...) more you can create quickly any kind of Applications (Business Applications, Web Applications, Mobile Applications, Microservices, ...). The development of this `(Micro)service` and put It into a Docker container was a smooth experience. It was possible to implement the older scripts to automatize some task while at the same time implement modern layered web applications as a microservice, and everything in a few lines of code.

See you soon.

## Inspirational reference

  * [A great blogpost explaining the "Developing a RESTful micro service in Python" - http://www.skybert.net/python/developing-a-restful-micro-service-in-python](http://www.skybert.net/python/developing-a-restful-micro-service-in-python)



## MAC Address references

  * [Wikipedia - MAC Address](https://en.wikipedia.org/wiki/MAC_address)
  * [The Wireshark OUI lookup tool](https://www.wireshark.org/tools/oui-lookup.html)
  * [IEEE MAC Address Registration Authority](https://regauth.standards.ieee.org/standards-ra-web/pub/view.html)


